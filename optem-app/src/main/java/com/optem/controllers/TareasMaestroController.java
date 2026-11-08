package com.optem.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.optem.database.MockDB;

public class TareasMaestroController {

    private static List< Map<String, String> > tablaAsignaciones = new ArrayList<>();
    private static Map< String, List< Map<String, String> > > tablaEntregas = new HashMap<>();
    
    private static final String ARCHIVO_ASIG = "asignaciones_maestro.dat";
    private static final String ARCHIVO_ENTREGAS = "entregas_alumnos.dat";

    static {
        cargarAsignacionesFisicas();
        cargarEntregasFisicas();
    }

    public List<String> getMisGruposOficiales() {
        List<String> nombres = new ArrayList<>();
        List< Map<String, String> > grupos = MockDB.obtenerTodosLosGrupos();
        if (grupos != null) {
            for (Map<String, String> grupo : grupos) {
                nombres.add(grupo.get("nombre") + " (" + grupo.get("grupo") + ")");
            }
        }
        return nombres;
    }

    public void publicarNuevaTarea(String titulo, String materia, String descripcion, LocalDate fecha, String hora, String rutaArchivo) {
        Map<String, String> tarea = new HashMap<>();
        tarea.put("id", UUID.randomUUID().toString());
        tarea.put("titulo", titulo);
        tarea.put("materia", materia);
        tarea.put("descripcion", descripcion);
        tarea.put("fecha", fecha.toString());
        tarea.put("hora", hora);
        tarea.put("estado", "ABIERTA");
        tarea.put("entregas_actuales", "0");
        tarea.put("entregas_total", "30");
        tarea.put("ruta_archivo", rutaArchivo != null ? rutaArchivo : "");
        
        tablaAsignaciones.add(0, tarea);
        guardarAsignacionesFisicas(); 
    }

    public List< Map<String, String> > getTodasLasAsignaciones() {
        return tablaAsignaciones;
    }

    public void cerrarRecepcionTarea(String idTarea) {
        for (Map<String, String> tarea : tablaAsignaciones) {
            if (tarea.get("id").equals(idTarea)) {
                tarea.put("estado", "CERRADA");
                guardarAsignacionesFisicas(); 
                break;
            }
        }
    }

    public void registrarEntregaAlumno(String idTarea, String nombreAlumno, String rutaArchivoAlumno) {
        for (Map<String, String> tarea : tablaAsignaciones) {
            if (tarea.get("id").equals(idTarea)) {
                int actuales = Integer.parseInt(tarea.get("entregas_actuales"));
                tarea.put("entregas_actuales", String.valueOf(actuales + 1));
                break;
            }
        }
        guardarAsignacionesFisicas();

        tablaEntregas.computeIfAbsent(idTarea, k -> new ArrayList<>());
        Map<String, String> entrega = new HashMap<>();
        entrega.put("alumno", nombreAlumno);
        entrega.put("ruta", rutaArchivoAlumno);
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        entrega.put("fecha_entrega", LocalDateTime.now().format(fmt));
        
        tablaEntregas.get(idTarea).add(entrega);
        guardarEntregasFisicas();
    }

    public List< Map<String, String> > obtenerEntregasDeTarea(String idTarea) {
        return tablaEntregas.getOrDefault(idTarea, new ArrayList<>());
    }

    private static void guardarAsignacionesFisicas() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_ASIG))) {
            oos.writeObject(tablaAsignaciones);
        } catch (Exception e) {}
    }

    private static void guardarEntregasFisicas() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_ENTREGAS))) {
            oos.writeObject(tablaEntregas);
        } catch (Exception e) {}
    }

    @SuppressWarnings("unchecked")
    private static void cargarAsignacionesFisicas() {
        File file = new File(ARCHIVO_ASIG);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            tablaAsignaciones = (List< Map<String, String> >) ois.readObject();
        } catch (Exception e) { tablaAsignaciones = new ArrayList<>(); }
    }

    @SuppressWarnings("unchecked")
    private static void cargarEntregasFisicas() {
        File file = new File(ARCHIVO_ENTREGAS);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            tablaEntregas = (Map< String, List< Map<String, String> > >) ois.readObject();
        } catch (Exception e) { tablaEntregas = new HashMap<>(); }
    }
}