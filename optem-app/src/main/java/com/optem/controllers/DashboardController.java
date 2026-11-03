package com.optem.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import com.optem.database.MockDB;
import com.optem.logic.ActividadManager;
import com.optem.ui.DashboardView;

public class DashboardController {

    private final Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
    private String correoActual;

    public void inicializarUsuario(String correoUsuario) {
        this.correoActual = correoUsuario;
        ActividadManager.getInstance().setUsuarioActual(correoUsuario);
        prefs.put("LAST_ACTIVE_ACCOUNT", correoUsuario);
        String listaCuentas = prefs.get("ACCOUNTS_LIST", "");
        if (!listaCuentas.contains(correoUsuario)) {
            prefs.put("ACCOUNTS_LIST", listaCuentas.isEmpty() ? correoUsuario : listaCuentas + "," + correoUsuario);
        }
    }

    public Map<String, String> getPerfilUsuario() { return MockDB.obtenerPerfilUsuario(); }
    public List< Map < String , String >> getClasesRegistradas() { return MockDB.obtenerMateriasRegistradas(); }

    public String getNombreEstudiante() { 
        if (getRolUsuario().equals("ADMINISTRADOR")) return "Administrador Principal";
        return MockDB.obtenerPerfilUsuario().get("nombre_completo"); 
    }

    // --- PREFERENCIAS ---
    public String getRolUsuario() { return prefs.get("ROLE_" + correoActual, "ESTUDIANTE"); }
    public String getFotoPerfil() { return prefs.get("USER_PHOTO_PATH_" + correoActual, ""); }
    public String getColorFuerte() { return prefs.get("PREF_ACCENT_COLOR_" + correoActual, "#3D5A4D"); }
    public String getColorClaro() { return prefs.get("PREF_ACCENT_LIGHT_" + correoActual, "#E9EDC9"); }
    public boolean isAnimacionesActivas() { return prefs.getBoolean("PREF_ANIMATIONS_" + correoActual, true); }
    public String getTamanoFuente() { return prefs.get("PREF_FONT_SIZE_" + correoActual, "Normal"); }
    public boolean isTecladoActivo() { return prefs.getBoolean("PREF_ACC_KEYBOARD_" + correoActual, false); }
    public boolean isVozActivo() { return prefs.getBoolean("PREF_ACC_VOICE_" + correoActual, false); }
    public String getNivelNotificaciones() { return prefs.get("PREF_NOTIFICATIONS_" + correoActual, "COMPLETO"); }

    // --- GESTOR DE NOTIFICACIONES (Lógica) ---
    public boolean debeMostrarNotificacion(String tipoNotificacion) {
        String nivel = getNivelNotificaciones();
        if (nivel.equals("APAGADO")) return false;
        if (nivel.equals("PRIORITARIO")) {
            return tipoNotificacion.equals("REUNION") || tipoNotificacion.equals("URGENTE");
        }
        return true; // COMPLETO: Muestra todo
    }

    // --- GESTIÓN DE CUENTAS ---
    public List<String> getCuentasExtras() {
        List<String> extras = new ArrayList<>();
        for (String email : prefs.get("ACCOUNTS_LIST", "").split(",")) {
            if (!email.isEmpty() && !email.equalsIgnoreCase(correoActual)) extras.add(email);
        }
        return extras;
    }
    public String getNombreDeCuenta(String email) { return prefs.get("NAME_" + email, email); }
    public void registrarNuevaCuenta(String email, String nombre) {
        prefs.put("NAME_" + email, nombre);
        String listAnt = prefs.get("ACCOUNTS_LIST", "");
        if (!listAnt.contains(email)) prefs.put("ACCOUNTS_LIST", listAnt.isEmpty() ? email : listAnt + "," + email);
    }
    public void eliminarCuenta(String email) {
        prefs.remove("NAME_" + email); prefs.remove("PREF_ACCENT_COLOR_" + email);
        String nuevaLista = prefs.get("ACCOUNTS_LIST", "").replace(email, "").replace(",,", ",");
        if (nuevaLista.startsWith(",")) nuevaLista = nuevaLista.substring(1);
        if (nuevaLista.endsWith(",")) nuevaLista = nuevaLista.substring(0, nuevaLista.length() - 1);
        prefs.put("ACCOUNTS_LIST", nuevaLista);
    }
    public void cerrarSesion() { prefs.put("LAST_ACTIVE_ACCOUNT", ""); }

    // --- MOTOR DE VOZ ---
    public void hablar(String text) {
        if (prefs.getBoolean("PREF_ACC_TTS_" + correoActual, false)) {
            Thread ttsThread = new Thread(() -> {
                try {
                    String cleanText = text.replace("'", "").replace("\"", "");
                    Runtime.getRuntime().exec("PowerShell -Command \"Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + cleanText + "');\"");
                } catch (Exception e) {}
            });
            ttsThread.setDaemon(true); ttsThread.start();
        }
    }

    public String procesarComandoVoz(String comando) {
        if (comando.contains("inicio")) return "INICIO";
        if (comando.contains("ajuste")) return "AJUSTES";
        if (comando.contains("modo oscuro")) { prefs.putBoolean("PREF_DARK_MODE_" + correoActual, true); return "OSCURO"; }
        if (comando.contains("modo claro")) { prefs.putBoolean("PREF_DARK_MODE_" + correoActual, false); return "CLARO"; }
        if (comando.contains("salir")) return "SALIR";
        return "DESCONOCIDO";
    }

    // --- MULTIMEDIA Y LECTURA ---
    public String obtenerPistaMusicalActual() {
        try {
            File musicFile = new File("reproduccion_actual.txt");
            if (musicFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(musicFile)); 
                String track = reader.readLine(); reader.close();
                if (track != null && !track.isEmpty()) return track + " 🎵";
            }
        } catch (Exception ex) {}
        return "lo-fi hip hop 🎵";
    }

    public String getUltimoLibroRuta() { return prefs.get("LAST_BOOK_PATH", ""); }
    public int getUltimoLibroPagina() { return prefs.getInt("LAST_BOOK_PAGE", 1); }
    public void guardarProgresoLibro(String path, int pagina) { prefs.put("LAST_BOOK_PATH", path); prefs.putInt("LAST_BOOK_PAGE", pagina); }
    public void abrirDocumentoEnOS(File file) {
        Thread openThread = new Thread(() -> { 
            try { if (java.awt.Desktop.isDesktopSupported()) java.awt.Desktop.getDesktop().open(file); } catch (Exception e) {} 
        }); openThread.setDaemon(true); openThread.start();
    }
}