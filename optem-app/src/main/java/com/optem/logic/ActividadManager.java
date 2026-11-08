package com.optem.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import com.optem.models.Actividad;
import com.optem.ui.DashboardView;

public class ActividadManager {

    private static ActividadManager instance;
    private Map<LocalDate, List<Actividad>> actividadesPorFecha;
    private String usuarioActual;
    
    // Archivo físico donde se guardará todo
    private final String ARCHIVO_ACT = "actividades_db.dat";

    private ActividadManager() {
        actividadesPorFecha = new HashMap<>();
        cargarDatosFisicos(); // Carga al iniciar
    }

    public static ActividadManager getInstance() {
        if (instance == null) instance = new ActividadManager();
        return instance;
    }

    public void setUsuarioActual(String correo) {
        this.usuarioActual = correo;
    }

    public List<Actividad> getAllActividades() {
        List<Actividad> todas = new ArrayList<>();
        actividadesPorFecha.values().forEach(todas::addAll);
        return todas;
    }

    public List<Actividad> getActividadesDelDia(LocalDate fecha) {
        return actividadesPorFecha.getOrDefault(fecha, new ArrayList<>());
    }

    public Actividad buscarActividadPorId(String id) {
        for (List<Actividad> lista : actividadesPorFecha.values()) {
            for (Actividad a : lista) if (a.getId().equals(id)) return a;
        }
        return null;
    }

    public void agregarActividad(Actividad act) {
        actividadesPorFecha.computeIfAbsent(act.getFecha(), k -> new ArrayList<>()).add(act);
        guardarDatosFisicos(); // Guarda en disco
    }

    public void eliminarActividad(String id) {
        for (List<Actividad> lista : actividadesPorFecha.values()) {
            lista.removeIf(a -> a.getId().equals(id));
        }
        guardarDatosFisicos(); // Guarda en disco
    }

    public List<Actividad> obtenerActividadesPorMateria(String materia) {
        return getAllActividades().stream().filter(a -> materia.equals(a.getMateria())).collect(Collectors.toList());
    }

    public void actualizarEstadoActividad(String idActividad, String nuevoEstado) {
        for (List<Actividad> lista : actividadesPorFecha.values()) {
            for (Actividad act : lista) {
                if (act.getId().equals(idActividad)) {
                    act.setEstado(nuevoEstado);
                    
                    // LÓGICA DE XP REFINADA: Solo suma si es una TAREA
                    if (nuevoEstado.equals("COMPLETADO") && "Tarea".equalsIgnoreCase(act.getTipo())) {
                        sumarXP(50); // El Alumno gana 50 XP por tarea
                    }
                    
                    guardarDatosFisicos();
                    return;
                }
            }
        }
    }

    private void sumarXP(int cantidad) {
        if (usuarioActual != null) {
            Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
            int xpActual = prefs.getInt("XP_USER_" + usuarioActual, 0);
            prefs.putInt("XP_USER_" + usuarioActual, xpActual + cantidad);
            NotificacionManager.mostrarNotificacion("¡Logro Desbloqueado!", "Has obtenido +" + cantidad + " XP por completar tu tarea oficial.", "INFO");
        }
    }

    // --- MÉTODOS DE PERSISTENCIA ---
    private void guardarDatosFisicos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_ACT))) {
            oos.writeObject(actividadesPorFecha);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDatosFisicos() {
        File file = new File(ARCHIVO_ACT);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            actividadesPorFecha = (Map<LocalDate, List<Actividad>>) ois.readObject();
        } catch (Exception e) {
            actividadesPorFecha = new HashMap<>();
        }
    }
}