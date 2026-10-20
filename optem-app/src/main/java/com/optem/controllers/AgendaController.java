package com.optem.controllers;

import com.optem.logic.ActividadManager;
import com.optem.models.Actividad;
import com.optem.ui.DashboardView;

import java.time.LocalDate;
import java.util.List;
import java.util.prefs.Preferences;

public class AgendaController {

    /**
     * Obtiene el color de acento guardado en las preferencias del usuario actual.
     */
    public String getThemeColor(String key, String defaultColor) {
        String correo = ActividadManager.getInstance().getCorreoActual();
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        return prefs.get(key + "_" + correo, defaultColor);
    }

    /**
     * Obtiene la lista de actividades para una fecha específica.
     */
    public List<Actividad> getActividadesDelDia(LocalDate date) {
        return ActividadManager.getInstance().getActividadesDelDia(date);
    }

    /**
     * Calcula la nueva fecha base al navegar hacia adelante o hacia atrás.
     */
    public LocalDate navegarFecha(LocalDate currentDate, String mode, int val) {
        if (mode.equals("DAY")) return currentDate.plusDays(val);
        if (mode.equals("WEEK")) return currentDate.plusWeeks(val);
        return currentDate.plusMonths(val);
    }

    /**
     * Mapea el tipo de actividad con su color de fondo.
     */
    public String getBgColor(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "estudio" -> "#E8F5E9"; case "cultural" -> "#E3F2FD"; case "reunión" -> "#F3E5F5";
            case "tarea" -> "#E0F7FA"; case "deporte" -> "#FFF3E0"; default -> "#F5F5F5";
        };
    }

    /**
     * Mapea el tipo de actividad con el color de su borde lateral.
     */
    public String getBorderColor(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "estudio" -> "#2E7D32"; case "cultural" -> "#1565C0"; case "reunión" -> "#7B1FA2";
            case "tarea" -> "#006064"; case "deporte" -> "#E65100"; default -> "#616161";
        };
    }

    /**
     * Genera la cadena de datos oculta para arrastrar una actividad al chatbot.
     */
    public String generarDatosArrastreActividad(Actividad act) {
        return "ACT|" + act.getNombre() + "|" + act.getTipo() + "|" + act.getDescripcion() + "|" + act.getHoraVencimiento() + "|" + act.getFecha().toString();
    }

    /**
     * Genera la cadena de datos oculta para arrastrar un día completo al chatbot.
     */
    public String generarDatosArrastreDia(LocalDate date) {
        return "DAY|" + date.toString();
    }
}