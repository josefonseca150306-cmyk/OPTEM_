package com.optem.controllers;

import com.optem.logic.ActividadManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotasController {

    // Aislamiento real: Map(Correo -> Map(Materia -> Apunte))
    // NOTA: Esto eventualmente irá a la clase LocalDB o Services.
    private static final Map<String, Map<String, String>> apuntesPorUsuario = new HashMap<>();

    /**
     * Obtiene una lista de materias únicas basándose en las actividades del usuario.
     */
    public List<String> getMateriasDisponibles() {
        List<String> materias = new ArrayList<>();
        ActividadManager.getInstance().getTodasLasActividades().forEach(a -> {
            if (!a.getMateria().equals("General") && !materias.contains(a.getMateria())) {
                materias.add(a.getMateria());
            }
        });
        return materias;
    }

    /**
     * Guarda un nuevo apunte para el usuario actual.
     * Si ya existe un apunte para esa materia, lo concatena.
     * Retorna true si se guardó correctamente, false si faltan datos.
     */
    public boolean guardarApunte(String materia, String texto) {
        String usuario = ActividadManager.getInstance().getCorreoActual();

        if (materia != null && texto != null && !texto.trim().isEmpty()) {
            apuntesPorUsuario.putIfAbsent(usuario, new HashMap<>());
            String apunteAnterior = apuntesPorUsuario.get(usuario).getOrDefault(materia, "");
            String nuevoApunte = apunteAnterior.isEmpty() ? texto : apunteAnterior + "\n\n---\n\n" + texto;
            
            apuntesPorUsuario.get(usuario).put(materia, nuevoApunte);
            return true;
        }
        return false;
    }

    /**
     * Obtiene todos los apuntes del usuario actual.
     */
    public Map<String, String> getApuntesDelUsuario() {
        String usuario = ActividadManager.getInstance().getCorreoActual();
        return apuntesPorUsuario.getOrDefault(usuario, new HashMap<>());
    }
}