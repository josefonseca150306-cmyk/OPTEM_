package com.optem.controllers;

import java.util.UUID;

public class GruposMaestroController {

    public String generarCodigoClase() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public boolean crearGrupo(String nombreMateria, String horario) {
        if (nombreMateria == null || nombreMateria.trim().isEmpty() || horario == null || horario.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean agregarAlumnoManual(String busqueda) {
        if (busqueda == null || busqueda.trim().isEmpty()) {
            return false;
        }
        return true;
    }
}