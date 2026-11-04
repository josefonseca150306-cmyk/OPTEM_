package com.optem.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.optem.database.MockDB;

public class ReunionesController {

    // Simula una base de datos de usuarios de la universidad
    private final List<UsuarioDirectorio> directorio = new ArrayList<>();

    public ReunionesController() {
        // Datos simulados para buscar compañeros
        directorio.add(new UsuarioDirectorio("2021001", "Marcela Camacho Avila", "M. EN CCO.", "marcela@uaemex.mx", "Conectado"));
        directorio.add(new UsuarioDirectorio("2021002", "Jose Esteban Ruiz Melo", "M. EN ADM.", "esteban@uaemex.mx", "Ocupado"));
        directorio.add(new UsuarioDirectorio("2021003", "Griselda Areli Matias", "DR. EN C.", "griselda@uaemex.mx", "Ausente"));
        directorio.add(new UsuarioDirectorio("2021004", "Daniela Sanchez", "Estudiante", "daniela@alumno.uaemex.mx", "Conectado"));
        directorio.add(new UsuarioDirectorio("2021005", "Alexis Hernandez", "Estudiante", "alexis@alumno.uaemex.mx", "Conectado"));
        directorio.add(new UsuarioDirectorio("2021006", "Mauricio Gomez", "Estudiante", "mauricio@alumno.uaemex.mx", "Desconectado"));
    }

    public List< Map < String , String >> getGruposParaLlamada() {
        // Aprovechamos el MockDB existente para obtener las clases registradas
        return MockDB.obtenerMateriasRegistradas();
    }

    public List<UsuarioDirectorio> buscarUsuarios(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String q = query.toLowerCase();
        return directorio.stream()
                .filter(u -> u.nombre.toLowerCase().contains(q) || u.matricula.contains(q))
                .collect(Collectors.toList());
    }

    // Clase interna de soporte para el directorio
    public static class UsuarioDirectorio {
        public String matricula;
        public String nombre;
        public String rol;
        public String correo;
        public String estado;

        public UsuarioDirectorio(String matricula, String nombre, String rol, String correo, String estado) {
            this.matricula = matricula;
            this.nombre = nombre;
            this.rol = rol;
            this.correo = correo;
            this.estado = estado;
        }
    }
}