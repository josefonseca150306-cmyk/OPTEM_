package com.optem.controllers;

import java.util.prefs.Preferences;
 
import com.optem.database.MockDB;
import com.optem.ui.DashboardView;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginController {

    public boolean checkAutoLogin(Stage stage) {
        Preferences prefsDirect = Preferences.userNodeForPackage(DashboardView.class);
        
        if (prefsDirect.get("ACCOUNTS_LIST", "").isEmpty()) {
            try {
                prefsDirect.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String lastActive = prefsDirect.get("LAST_ACTIVE_ACCOUNT", "");
        if (!lastActive.isEmpty()) {
            new DashboardView().show(stage, lastActive, "");
            return true; 
        }
        return false;
    }

    public void handleLogin(String nombre, String apellido, String correo, String password, String rol, Label lblError, Stage stage) {
        
        // 1. Validamos que el correo siempre esté lleno
        if (correo.isEmpty()) { 
            lblError.setText("Por favor ingresa tu correo institucional."); 
            lblError.setVisible(true); 
            return; 
        }

        // 2. EXCEPCIÓN PARA ADMIN: Si NO es administrador, entonces SÍ es obligatorio el nombre
        if (!rol.equals("ADMINISTRADOR") && nombre.isEmpty()) { 
            lblError.setText("Por favor ingresa tu nombre."); 
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

        // ========================================================
        // CAPA DE SEGURIDAD: CONSULTAMOS LA BASE DE DATOS (MOCK)
        // ========================================================
        boolean accesoConcedido = MockDB.autenticar(correo, password, rol);

        if (accesoConcedido) {
            lblError.setVisible(false);

            Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
            
            // Si es Admin y no puso nombre, guardamos "Administrador", si no, guardamos su nombre real
            String nombreFinal = nombre.isEmpty() ? "Administrador Principal" : nombre;
            String nombreCompleto = nombreFinal + (apellido.isEmpty() ? "" : " " + apellido.trim());
            
            prefs.put("NAME_" + correo, nombreCompleto);
            prefs.put("ROLE_" + correo, rol); 
            prefs.put("LAST_ACTIVE_ACCOUNT", correo);

            DashboardView dashboard = new DashboardView();
            dashboard.show(stage, correo, "");
            
        } else {
            lblError.setText("❌ Correo o número de cuenta incorrectos."); 
            lblError.setVisible(true); 
        }
    }
}