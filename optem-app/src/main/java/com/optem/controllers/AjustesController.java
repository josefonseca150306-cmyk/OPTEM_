package com.optem.controllers;

import com.optem.ui.DashboardView;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.prefs.Preferences;

public class AjustesController {

    private final Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
    private final String correoUsuario;

    public AjustesController(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    // --- MANEJO DE PERFIL ---

    public String getNombreInicial() {
        String defaultNombre = "Alumno UAEMéx";
        if (correoUsuario.equalsIgnoreCase("jfonsecal001@alumno.uaemex.mx")) {
            defaultNombre = "Jose Antonio Fonseca";
        } else if (correoUsuario.equalsIgnoreCase("ahernandezh058@alumno.uaemex.mx")) {
            defaultNombre = "Alexis Gethsemani Hernandez";
        } else if (correoUsuario.equalsIgnoreCase("fmartinezc017@alumno.uaemex.mx")) {
            defaultNombre = "Fatima Daniela Martinez";
        }
        
        String nombre = prefs.get("NAME_" + correoUsuario, defaultNombre);
        prefs.put("NAME_" + correoUsuario, nombre); // Aseguramos guardarlo
        return nombre;
    }

    public String getCarreraInicial() {
        if (prefs.get("PREF_CARRERA_" + correoUsuario, "").isEmpty()) {
            String[] carrerasAleatorias = {"Arquitectura y Diseño", "Derecho", "Medicina", "Contaduría", "Psicología", "Ingeniería Civil"};
            prefs.put("PREF_CARRERA_" + correoUsuario, carrerasAleatorias[(int)(Math.random() * carrerasAleatorias.length)]);
        }
        return prefs.get("PREF_CARRERA_" + correoUsuario, "Ingeniería de Software");
    }

    public String getSemestreInicial() {
        if (prefs.get("PREF_SEMESTRE_" + correoUsuario, "").isEmpty()) {
            String[] semestresAleatorios = {"1er Semestre", "2do Semestre", "3er Semestre", "5to Semestre", "7mo Semestre", "9no Semestre"};
            prefs.put("PREF_SEMESTRE_" + correoUsuario, semestresAleatorios[(int)(Math.random() * semestresAleatorios.length)]);
        }
        return prefs.get("PREF_SEMESTRE_" + correoUsuario, "4to Semestre");
    }

    public void guardarPerfil(String nombre, String carrera, String semestre) {
        prefs.put("NAME_" + correoUsuario, nombre);
        prefs.put("PREF_CARRERA_" + correoUsuario, carrera);
        prefs.put("PREF_SEMESTRE_" + correoUsuario, semestre);
    }

    // --- MANEJO DE FOTO DE PERFIL ---

    public String getRutaFotoPerfil() {
        return prefs.get("USER_PHOTO_PATH_" + correoUsuario, "");
    }

    public void guardarRutaFotoPerfil(String path) {
        prefs.put("USER_PHOTO_PATH_" + correoUsuario, path);
    }

    public Image recortarImagenCuadrada(Image original) {
        if (original == null || original.isError()) return original;
        int w = (int) original.getWidth();
        int h = (int) original.getHeight();
        int size = Math.min(w, h);
        return new WritableImage(original.getPixelReader(), (w - size) / 2, (h - size) / 2, size, size);
    }

    // --- APARIENCIA Y PREFERENCIAS GENÉRICAS ---

    public String getColorFuerte() {
        return prefs.get("PREF_ACCENT_COLOR_" + correoUsuario, "#3D5A4D");
    }

    public void guardarColoresAcento(Color c) {
        String hexFuerte = String.format("#%02X%02X%02X", (int)(c.getRed() * 255), (int)(c.getGreen() * 255), (int)(c.getBlue() * 255));
        String hexClaro = String.format("#%02X%02X%02X", 
            (int)(c.getRed() * 255 * 0.15 + 255 * 0.85), 
            (int)(c.getGreen() * 255 * 0.15 + 255 * 0.85), 
            (int)(c.getBlue() * 255 * 0.15 + 255 * 0.85));
        
        prefs.put("PREF_ACCENT_COLOR_" + correoUsuario, hexFuerte);
        prefs.put("PREF_ACCENT_LIGHT_" + correoUsuario, hexClaro);
    }

    public String getPrefString(String key, String def) {
        return prefs.get(key + "_" + correoUsuario, def);
    }

    public void setPrefString(String key, String val) {
        prefs.put(key + "_" + correoUsuario, val);
    }

    public boolean getPrefBoolean(String prefKey, boolean defaultValue) {
        return prefs.getBoolean(prefKey + "_" + correoUsuario, defaultValue);
    }

    public boolean alternarPrefBoolean(String prefKey, boolean defaultValue) {
        boolean newState = !getPrefBoolean(prefKey, defaultValue);
        prefs.putBoolean(prefKey + "_" + correoUsuario, newState);
        return newState;
    }

    // --- ACCESIBILIDAD Y MOTOR DE VOZ ---

    public void ejecutarLectorDeVozSiEstaActivo(String prefKey, boolean newState) {
        if (prefs.getBoolean("PREF_ACC_TTS_" + correoUsuario, false)) {
            hablar(newState ? "Activado" : "Desactivado");
        }
    }

    private void hablar(String text) {
        Thread ttsThread = new Thread(() -> {
            try {
                String cleanText = text.replace("'", "").replace("\"", "");
                Runtime.getRuntime().exec("PowerShell -Command \"Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + cleanText + "');\"");
            } catch (Exception e) {}
        });
        ttsThread.setDaemon(true);
        ttsThread.start();
    }
}