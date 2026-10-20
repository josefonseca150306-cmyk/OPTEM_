package com.optem.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.optem.logic.ActividadManager;
import com.optem.ui.DashboardView;

public class DashboardController {

    // Independizamos la memoria para que nunca se rompa si la vista falla
    private final Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
    private String correoActual;

    public void inicializarUsuario(String correoUsuario) {
        this.correoActual = correoUsuario;
        ActividadManager.getInstance().setUsuarioActual(correoUsuario);
        prefs.put("LAST_ACTIVE_ACCOUNT", correoUsuario);

        String listaCuentas = prefs.get("ACCOUNTS_LIST", "");
        if (!listaCuentas.contains(correoUsuario)) {
            if (listaCuentas.isEmpty()) {
                listaCuentas = correoUsuario;
            } else {
                listaCuentas = listaCuentas + "," + correoUsuario;
            }
            prefs.put("ACCOUNTS_LIST", listaCuentas);
        }
    }

    // --- PREFERENCIAS DE USUARIO Y ROL ---
    public String getRolUsuario() { return prefs.get("ROLE_" + correoActual, "ESTUDIANTE"); }
    public String getNombreEstudiante() { return prefs.get("NAME_" + correoActual, "Alumno UAEMéx"); }
    public String getFotoPerfil() { return prefs.get("USER_PHOTO_PATH_" + correoActual, ""); }
    public String getColorFuerte() { return prefs.get("PREF_ACCENT_COLOR_" + correoActual, "#3D5A4D"); }
    public String getColorClaro() { return prefs.get("PREF_ACCENT_LIGHT_" + correoActual, "#E9EDC9"); }
    public boolean isAnimacionesActivas() { return prefs.getBoolean("PREF_ANIMATIONS_" + correoActual, true); }
    public String getTamanoFuente() { return prefs.get("PREF_FONT_SIZE_" + correoActual, "Normal"); }
    public boolean isTecladoActivo() { return prefs.getBoolean("PREF_ACC_KEYBOARD_" + correoActual, false); }
    public boolean isVozActivo() { return prefs.getBoolean("PREF_ACC_VOICE_" + correoActual, false); }

    // --- GESTIÓN DE CUENTAS ---
    public List<String> getCuentasExtras() {
        List<String> extras = new ArrayList<>();
        for (String email : prefs.get("ACCOUNTS_LIST", "").split(",")) {
            if (!email.isEmpty() && !email.equalsIgnoreCase(correoActual)) {
                extras.add(email);
            }
        }
        return extras;
    }

    public String getNombreDeCuenta(String email) {
        return prefs.get("NAME_" + email, email);
    }

    public void registrarNuevaCuenta(String email, String nombre) {
        prefs.put("NAME_" + email, nombre);
        String listAnt = prefs.get("ACCOUNTS_LIST", "");
        if (!listAnt.contains(email)) {
            prefs.put("ACCOUNTS_LIST", listAnt.isEmpty() ? email : listAnt + "," + email);
        }
    }

    public void eliminarCuenta(String email) {
        prefs.remove("NAME_" + email);
        prefs.remove("PREF_ACCENT_COLOR_" + email);
        prefs.remove("PREF_ACCENT_LIGHT_" + email);
        prefs.remove("PREF_ANIMATIONS_" + email);
        prefs.remove("PREF_DARK_MODE_" + email);
        prefs.remove("PREF_ACC_TTS_" + email);
        prefs.remove("PREF_ACC_VOICE_" + email);
        prefs.remove("PREF_ACC_KEYBOARD_" + email);
        
        String nuevaLista = prefs.get("ACCOUNTS_LIST", "").replace(email, "").replace(",,", ",");
        if (nuevaLista.startsWith(",")) nuevaLista = nuevaLista.substring(1);
        if (nuevaLista.endsWith(",")) nuevaLista = nuevaLista.substring(0, nuevaLista.length() - 1);
        prefs.put("ACCOUNTS_LIST", nuevaLista);
    }

    public void cerrarSesion() {
        prefs.put("LAST_ACTIVE_ACCOUNT", "");
    }

    // --- MOTOR DE VOZ Y TTS ---
    public void hablar(String text) {
        if (prefs.getBoolean("PREF_ACC_TTS_" + correoActual, false)) {
            System.out.println("🗣 [Lector de Pantalla]: " + text);
            Thread ttsThread = new Thread(() -> {
                try {
                    String cleanText = text.replace("'", "").replace("\"", "");
                    String cmd = "PowerShell -Command \"Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + cleanText + "');\"";
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {}
            });
            ttsThread.setDaemon(true); 
            ttsThread.start();
        }
    }

    public String procesarComandoVoz(String comando) {
        if (comando.contains("inicio")) return "INICIO";
        if (comando.contains("ajuste")) return "AJUSTES";
        if (comando.contains("modo oscuro")) { prefs.putBoolean("PREF_DARK_MODE_" + correoActual, true); return "OSCURO"; }
        if (comando.contains("modo claro")) { prefs.putBoolean("PREF_DARK_MODE_" + correoActual, false); return "CLARO"; }
        if (comando.contains("abre menú") || comando.contains("abrir menú")) return "ABRIR_MENU";
        if (comando.contains("cierra menú") || comando.contains("cerrar menú")) return "CERRAR_MENU";
        if (comando.contains("perfil") || comando.contains("foto")) return "PERFIL";
        if (comando.contains("salir") || comando.contains("cerrar sesión")) return "SALIR";
        if (comando.contains("apagar") || comando.contains("cerrar asistente") || comando.contains("adiós")) return "APAGAR";
        return "DESCONOCIDO";
    }

    // --- MONITOR DEL SISTEMA Y LECTURA ---
    public String obtenerPistaMusicalActual() {
        try {
            File musicFile = new File("reproduccion_actual.txt");
            if (musicFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(musicFile)); 
                String track = reader.readLine(); 
                reader.close();
                if (track != null && !track.isEmpty()) return track + " 🎵";
            }
        } catch (Exception ex) {}
        return "lo-fi hip hop 🎵";
    }

    public String getUltimoLibroRuta() { return prefs.get("LAST_BOOK_PATH", ""); }
    public int getUltimoLibroPagina() { return prefs.getInt("LAST_BOOK_PAGE", 1); }
    public void guardarProgresoLibro(String path, int pagina) {
        prefs.put("LAST_BOOK_PATH", path);
        prefs.putInt("LAST_BOOK_PAGE", pagina);
    }

    public void abrirDocumentoEnOS(File file) {
        Thread openThread = new Thread(() -> { 
            try { 
                if (java.awt.Desktop.isDesktopSupported()) { java.awt.Desktop.getDesktop().open(file); } 
            } catch (Exception e) {} 
        }); 
        openThread.setDaemon(true); 
        openThread.start();
    }

    // --- INTELIGENCIA ARTIFICIAL (CHATBOT) ---
    public String analizarArrastreChatbot(String datos) {
        if (datos.startsWith("DAY|")) {
            String fecha = datos.replace("DAY|", "");
            LocalDate date = LocalDate.parse(fecha);
            int total = ActividadManager.getInstance().getActividadesDelDia(date).size();
            return "📅 **Resumen del Día (" + fecha + ")**\n" +
                   "Tienes " + total + " actividades programadas para esta fecha. Te sugiero revisar la vista de 'Día' en tu Agenda para organizar tus bloques Pomodoro.";
        } 
        else if (datos.startsWith("ACT|")) {
            String[] partes = datos.split("\\|");
            String nombre = partes.length > 1 ? partes[1] : "Desconocido";
            String tipo = partes.length > 2 ? partes[2] : "General";
            String desc = partes.length > 3 ? partes[3] : "Sin descripción";
            String hora = partes.length > 4 ? partes[4] : "Sin hora";

            StringBuilder sb = new StringBuilder();
            sb.append("📋 **Análisis de Actividad**\n");
            sb.append("Asignatura/Título: ").append(nombre).append("\n");
            sb.append("Hora: ").append(hora).append("\n\n");
            sb.append("📌 *Recomendación Optem:* ");

            if (tipo.equalsIgnoreCase("Estudio")) {
                if (desc.toUpperCase().contains("VIRTUAL")) sb.append("Es una clase virtual. Verifica tu conexión a internet, prueba tu audio y entra 5 minutos antes al enlace.");
                else if (nombre.toLowerCase().contains("compiladores") || nombre.toLowerCase().contains("software")) sb.append("Te sugiero optimizar el rendimiento cerrando apps en segundo plano para compilar más rápido. Revisa tus apuntes previos.");
                else sb.append("Revisa tus notas de la sesión anterior. Te sugiero un bloque de enfoque de 25 mins antes de entrar.");
            } else if (tipo.equalsIgnoreCase("Tarea")) {
                sb.append("No dejes esto para el último minuto. Divide la tarea en subtareas más pequeñas. Puedes consultar la biblioteca digital para referencias.");
            } else if (tipo.equalsIgnoreCase("Deporte")) {
                sb.append("¡Excelente! Mantente hidratado, lleva tu botella de agua y no olvides estirar antes para cuidar tu ritmo cardíaco.");
            } else if (tipo.equalsIgnoreCase("Cultural")) {
                sb.append("Aprovecha para despejar la mente. Toma algunas fotos y disfruta el evento, es bueno para reducir el estrés académico.");
            } else if (tipo.equalsIgnoreCase("Reunión")) {
                sb.append("Lleva una libreta de apuntes. Define los puntos clave a discutir antes de iniciar la reunión para optimizar el tiempo.");
            } else {
                sb.append("Actividad agendada. Asegúrate de cumplirla a tiempo para subir tu XP del día.");
            }
            return sb.toString();
        }
        return "No pude procesar el formato de esta actividad.";
    }

    public String responderMensajeChatbot(String consulta) {
        String query = consulta.toLowerCase();
        if (query.contains("ayuda") || query.contains("recursos")) { return "Por supuesto. Analizando tu carga de trabajo, te recomiendo revisar el libro de C. Fischer en tu sección de Material de Apoyo."; }
        if (query.contains("hola")) { return "Hola " + getNombreEstudiante() + ". Estoy listo. Puedes escribirme tus dudas sobre cualquier clase o arrastrar una tarea directamente aquí."; }
        return "Entendido. He procesado tu consulta dentro del contexto de Optem.";
    }
}