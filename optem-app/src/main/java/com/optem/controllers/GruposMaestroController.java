package com.optem.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.optem.database.MockDB;
import com.optem.logic.NotificacionManager;

public class GruposMaestroController {

    public String generarCodigoClase() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    // Lee todos los grupos existentes desde la RAM
    public List<Map<String,String>> obtenerTodosLosGrupos() {
        return MockDB.obtenerTodosLosGrupos();
    }

    public boolean crearGrupo(String nombreMateria, String horario) {
        if (nombreMateria == null || nombreMateria.trim().isEmpty() || horario == null || horario.trim().isEmpty()) {
            return false;
        }
        
        String clave = generarCodigoClase();
        // Guardamos directamente en la Base de Datos simulada (MockDB)
        MockDB.guardarNuevoGrupo(clave, nombreMateria, "MAESTRO ACTIVO", "N/A", horario);
        return true;
    }

    public boolean agregarAlumnoManual(String claveMateria, String correoAlumno) {
        if (correoAlumno == null || correoAlumno.trim().isEmpty()) {
            NotificacionManager.mostrarNotificacion("Error", "Debes ingresar un correo o cuenta.", "ERROR");
            return false;
        }
        // Inscribimos al alumno usando el MockDB
        MockDB.inscribirAlumno(correoAlumno, claveMateria);
        return true;
    }
}