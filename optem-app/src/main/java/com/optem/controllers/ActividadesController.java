package com.optem.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import com.optem.logic.ActividadManager;
import com.optem.models.Actividad;

public class ActividadesController {

    /**
     * Filtra las actividades basándose en la categoría, texto de búsqueda y materia.
     */
    public List<Actividad> getActividadesFiltradas(String categoria, String busqueda, String materia) {
        // ACTUALIZADO: Usamos getAllActividades() del nuevo Manager Kanban
        return ActividadManager.getInstance().getAllActividades().stream()
            .filter(act -> categoria.equals("Todas") || act.getTipo().equals(categoria))
            .filter(act -> busqueda == null || busqueda.isEmpty() || act.getNombre().toLowerCase().contains(busqueda.toLowerCase()))
            .filter(act -> materia == null || materia.equals("Todas") || act.getMateria().equals("General") || act.getMateria().equals(materia))
            .collect(Collectors.toList());
    }

    /**
     * Valida y guarda una nueva actividad. Retorna true si tuvo éxito, false si el título está vacío.
     */
    public boolean guardarNuevaActividad(String titulo, String categoriaCompleta, String horaStr) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        
        String categoriaLimpia = categoriaCompleta.substring(categoriaCompleta.indexOf(" ") + 1); 
        LocalTime horaLocal = LocalTime.parse(horaStr);
        LocalDate fechaHoy = LocalDate.now(); 
        String id = String.valueOf(System.currentTimeMillis()); // Generamos ID único
        
        // ACTUALIZADO: Usamos el nuevo constructor compatible con Kanban
        Actividad nuevaAct = new Actividad(id, titulo, categoriaLimpia, "Actividad agregada por el alumno.", fechaHoy, horaLocal, "General");
        ActividadManager.getInstance().agregarActividad(nuevaAct);
        return true;
    }

    /**
     * Marca una actividad como hecha.
     */
    public void marcarComoEntregado(Actividad act) {
        // ACTUALIZADO: Movemos la tarea a "COMPLETADO" en el Kanban
        ActividadManager.getInstance().actualizarEstadoActividad(act.getId(), "COMPLETADO");
    }

    /**
     * Intenta eliminar una actividad. Retorna true si se borró, false si estaba bloqueada por el Admin.
     */
    public boolean eliminarActividad(Actividad act) {
        // ACTUALIZADO: Eliminamos directo de la lista centralizada
        return ActividadManager.getInstance().getAllActividades().remove(act);
    }

    /**
     * Genera el formato de texto oculto que se envía al chatbot cuando arrastras una tarjeta.
     */
    public String generarDatosArrastre(Actividad act) {
        return "ACT|" + act.getNombre() + "|" + act.getTipo() + "|" + act.getDescripcion() + "|" + act.getHoraVencimiento() + "|" + act.getFecha().toString();
    }
}