package com.optem.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.optem.database.MockDB;

public class TareasMaestroController {

    // Memoria en RAM para las tareas asignadas por el maestro (con espacios de seguridad)
    private static final List< Map<String, String> > tablaAsignaciones = new ArrayList<>();

    public List<String> getMisGruposOficiales() {
        List<String> nombres = new ArrayList<>();
        // Lee los grupos reales del MockDB (con espacios de seguridad)
        List< Map<String, String> > grupos = MockDB.obtenerTodosLosGrupos();
        if (grupos != null) {
            for (Map<String, String> grupo : grupos) {
                nombres.add(grupo.get("nombre") + " (" + grupo.get("grupo") + ")");
            }
        }
        return nombres;
    }

    public void publicarNuevaTarea(String titulo, String materia, String descripcion, LocalDate fecha, String hora) {
        Map<String, String> tarea = new HashMap<>();
        tarea.put("id", UUID.randomUUID().toString());
        tarea.put("titulo", titulo);
        tarea.put("materia", materia);
        tarea.put("descripcion", descripcion);
        tarea.put("fecha", fecha.toString());
        tarea.put("hora", hora);
        tarea.put("estado", "ABIERTA");
        
        // Simulamos que el grupo tiene 30 alumnos y al inicio 0 han entregado
        tarea.put("entregas_actuales", "0");
        tarea.put("entregas_total", "30");
        
        tablaAsignaciones.add(0, tarea); // Lo agrega al inicio de la lista
    }

    public List< Map<String, String> > getTodasLasAsignaciones() {
        return tablaAsignaciones;
    }

    public void cerrarRecepcionTarea(String idTarea) {
        for (Map<String, String> tarea : tablaAsignaciones) {
            if (tarea.get("id").equals(idTarea)) {
                tarea.put("estado", "CERRADA");
                break;
            }
        }
    }
}