package com.optem.controllers;

import com.optem.logic.ActividadManager;
import com.optem.models.Actividad;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class ActividadesController {

    /**
     * Filtra las actividades basándose en la categoría, texto de búsqueda y materia.
     */
    public List<Actividad> getActividadesFiltradas(String categoria, String busqueda, String materia) {
        return ActividadManager.getInstance().getTodasLasActividades().stream()
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
        
        Actividad nuevaAct = new Actividad(titulo, "General", categoriaLimpia, "Actividad agregada por el alumno.", horaLocal, fechaHoy, false, false);
        ActividadManager.getInstance().agregarActividad(nuevaAct);
        return true;
    }

    /**
     * Marca una actividad como hecha.
     */
    public void marcarComoEntregado(Actividad act) {
        act.setHecho(true);
    }

    /**
     * Intenta eliminar una actividad. Retorna true si se borró, false si estaba bloqueada por el Admin.
     */
    public boolean eliminarActividad(Actividad act) {
        return ActividadManager.getInstance().eliminarActividad(act);
    }

    /**
     * Genera el formato de texto oculto que se envía al chatbot cuando arrastras una tarjeta.
     */
    public String generarDatosArrastre(Actividad act) {
        return "ACT|" + act.getNombre() + "|" + act.getTipo() + "|" + act.getDescripcion() + "|" + act.getHoraVencimiento() + "|" + act.getFecha().toString();
    }
}