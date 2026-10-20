package com.optem.controllers;

import java.util.prefs.Preferences;

import com.optem.ui.DashboardView;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginController {

    public boolean checkAutoLogin(Stage stage) {
        Preferences prefsDirect = Preferences.userNodeForPackage(DashboardView.class);
        
        // Limpieza de memoria (Restauración de fábrica)
        if (prefsDirect.get("ACCOUNTS_LIST", "").isEmpty()) {
            try {
                prefsDirect.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Auto-Login
        String lastActive = prefsDirect.get("LAST_ACTIVE_ACCOUNT", "");
        if (!lastActive.isEmpty()) {
            new DashboardView().show(stage, lastActive, "");
            return true; 
        }
        return false;
    }

    public void handleLogin(String nombre, String apellido, String correo, String password, String rol, Label lblError, Stage stage) {
        // Validaciones de negocio
        if (nombre.isEmpty() || correo.isEmpty()) { 
            lblError.setText("Por favor ingresa tu nombre y tu correo."); 
            lblError.setVisible(true); 
            return; 
        }
        
        if (!password.matches("\\d{7}")) { 
            lblError.setText("Cuenta incorrecta. Debe contener exactamente 7 números."); 
            lblError.setVisible(true); 
            return; 
        }
        
        if (!correo.contains("@")) { 
            if (correo.matches("\\d+")) {
                correo += "@alumno.uaemex.mx"; 
            } else {
                correo += "@uaemex.mx"; 
            }
        }

        // Guardado de sesión y ROL
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        String nombreCompleto = nombre + (apellido.isEmpty() ? "" : " " + apellido.trim());
        prefs.put("NAME_" + correo, nombreCompleto);
        prefs.put("ROLE_" + correo, rol); // GUARDAMOS EL ROL (ESTUDIANTE O MAESTRO)
        prefs.put("LAST_ACTIVE_ACCOUNT", correo);

        // Redirección al Dashboard
        DashboardView dashboard = new DashboardView();
        dashboard.show(stage, correo, "");
    }
}