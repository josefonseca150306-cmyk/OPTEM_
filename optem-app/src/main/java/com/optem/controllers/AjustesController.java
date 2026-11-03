package com.optem.controllers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Map;
import java.util.prefs.Preferences;

import com.optem.database.MockDB;
import com.optem.ui.DashboardView;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class AjustesController {

    private final Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
    private final String correoUsuario;

    public AjustesController(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public Map<String, String> getPerfilDesdeMock() { return MockDB.obtenerPerfilUsuario(); }

    public String getRutaFotoPerfil() { return prefs.get("USER_PHOTO_PATH_" + correoUsuario, ""); }
    public void guardarRutaFotoPerfil(String path) { prefs.put("USER_PHOTO_PATH_" + correoUsuario, path); }

    public Image recortarImagenCuadrada(Image original) {
        if (original == null || original.isError()) return original;
        int w = (int) original.getWidth(); int h = (int) original.getHeight(); int size = Math.min(w, h);
        return new WritableImage(original.getPixelReader(), (w - size) / 2, (h - size) / 2, size, size);
    }

    public String getColorFuerte() { return prefs.get("PREF_ACCENT_COLOR_" + correoUsuario, "#3D5A4D"); }
    public void guardarColoresAcento(Color c) {
        String hexFuerte = String.format("#%02X%02X%02X", (int)(c.getRed() * 255), (int)(c.getGreen() * 255), (int)(c.getBlue() * 255));
        prefs.put("PREF_ACCENT_COLOR_" + correoUsuario, hexFuerte);
    }

    public String getPrefString(String key, String def) { return prefs.get(key + "_" + correoUsuario, def); }
    public void setPrefString(String key, String val) { prefs.put(key + "_" + correoUsuario, val); }
    public boolean getPrefBoolean(String prefKey, boolean defaultValue) { return prefs.getBoolean(prefKey + "_" + correoUsuario, defaultValue); }
    
    public boolean alternarPrefBoolean(String prefKey, boolean defaultValue) {
        boolean newState = !getPrefBoolean(prefKey, defaultValue);
        prefs.putBoolean(prefKey + "_" + correoUsuario, newState);
        return newState;
    }

    // --- LECTOR DE PANTALLA ELIMINADO ---

    // --- MOTOR DE RATÓN FÍSICO POR TECLADO ---
    public void moverCursorMouse(int dx, int dy) {
        try {
            Robot robot = new Robot();
            Point p = MouseInfo.getPointerInfo().getLocation();
            robot.mouseMove(p.x + dx, p.y + dy);
        } catch (Exception e) {}
    }

    public void scrollMouse(int cantidad) {
        try {
            new Robot().mouseWheel(cantidad);
        } catch (Exception e) {}
    }

    public void clickIzquierdoMouse() {
        try {
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (Exception e) {}
    }
}