package com.optem.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.optem.database.MockDB;
import com.optem.logic.ActividadManager;
import com.optem.models.Actividad;

public class TareasController {

    private final ActividadManager manager = ActividadManager.getInstance();

    public List<Actividad> getTareasPorEstado(String estado) {
        List<Actividad> filtradas = new ArrayList<>();
        for (Actividad act : manager.getAllActividades()) {
            
            // --- MOTOR DE CADUCIDAD AUTOMÁTICA ---
            if (isTareaCaducada(act) && !act.getEstado().equals("COMPLETADO")) {
                manager.actualizarEstadoActividad(act.getId(), "COMPLETADO");
                act.setEstado("COMPLETADO"); 
            }

            if (act.getEstado().equals(estado)) {
                filtradas.add(act);
            }
        }
        return filtradas;
    }

    public void moverTarea(Actividad tarea, String nuevoEstado) {
        manager.actualizarEstadoActividad(tarea.getId(), nuevoEstado);
    }

    public List<String> getNombresMaterias() {
        List<String> materias = new ArrayList<>();
        for (Map<String, String> m : MockDB.obtenerMateriasRegistradas()) {
            materias.add(m.get("nombre"));
        }
        return materias;
    }

    public void crearNuevaTarea(String nombre, String materia, String desc) {
        String id = String.valueOf(System.currentTimeMillis());
        // Por defecto, le damos 2 días para entregar a las 23:59
        Actividad nueva = new Actividad(id, nombre, "Tarea", desc, LocalDate.now().plusDays(2), LocalTime.of(23, 59), materia);
        manager.agregarActividad(nueva);
    }

    // Método que verifica si la fecha y hora actual ya superaron la fecha límite
    public boolean isTareaCaducada(Actividad tarea) {
        // CORRECCIÓN: Usamos getFecha() en lugar de getFechaVencimiento()
        if (tarea.getFecha() == null || tarea.getHoraVencimiento() == null) return false;
        LocalDateTime fechaLimite = LocalDateTime.of(tarea.getFecha(), tarea.getHoraVencimiento());
        return LocalDateTime.now().isAfter(fechaLimite);
    }
}