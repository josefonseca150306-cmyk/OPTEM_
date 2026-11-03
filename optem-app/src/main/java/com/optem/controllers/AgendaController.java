package com.optem.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.optem.logic.ActividadManager;
import com.optem.models.Actividad;

public class AgendaController {

    private final ActividadManager manager = ActividadManager.getInstance();

    public List<Actividad> getActividadesDelDia(LocalDate date) {
        List<Actividad> actividades = manager.getActividadesDelDia(date);
        
        // PROTECCIÓN 1: Si la base de datos devuelve null, mandamos una lista vacía para no romper la vista
        if (actividades == null) {
            return new ArrayList<>();
        }
        
        // Auto-caducidad: Si ya pasó la fecha/hora y no se ha completado, se bloquea.
        for(Actividad act : actividades) {
            if(isActividadCaducada(act) && !act.getEstado().equals("COMPLETADO")) {
                manager.actualizarEstadoActividad(act.getId(), "COMPLETADO");
                act.setEstado("COMPLETADO");
            }
        }
        return actividades;
    }

    public LocalDate navegarFecha(LocalDate currentDate, String mode, int val) {
        if (mode.equals("DAY")) return currentDate.plusDays(val);
        if (mode.equals("WEEK")) return currentDate.plusWeeks(val);
        return currentDate.plusMonths(val);
    }

    public boolean isActividadCaducada(Actividad tarea) {
        // PROTECCIÓN 2: Si la tarea no tiene fecha u hora límite, no puede caducar
        if (tarea.getFecha() == null || tarea.getHoraVencimiento() == null) return false;
        
        try {
            LocalDateTime fechaLimite = LocalDateTime.of(tarea.getFecha(), tarea.getHoraVencimiento());
            return LocalDateTime.now().isAfter(fechaLimite);
        } catch (Exception e) {
            return false;
        }
    }
}