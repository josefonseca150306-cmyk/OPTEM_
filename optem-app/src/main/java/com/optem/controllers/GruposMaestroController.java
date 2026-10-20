package com.optem.controllers;

import java.util.UUID;

public class GruposMaestroController {

    /**
     * Genera un código de acceso único de 6 caracteres para que los alumnos se unan a la clase.
     */
    public String generarCodigoClase() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    /**
     * Valida la creación de un nuevo grupo. (Aquí el otro ingeniero conectará la Base de Datos).
     */
    public boolean crearGrupo(String nombreMateria, String horario) {
        if (nombreMateria == null || nombreMateria.trim().isEmpty() || horario == null || horario.trim().isEmpty()) {
            return false;
        }
        // Simulación de guardado exitoso en BD...
        return true;
    }

    /**
     * Simula la búsqueda y adición manual de un alumno mediante su número de cuenta o nombre.
     */
    public boolean agregarAlumnoManual(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return false;
        }
        // Simulación de validación exitosa en BD...
        return true;
    }
}