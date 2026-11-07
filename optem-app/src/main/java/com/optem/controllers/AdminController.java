package com.optem.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.optem.database.MockDB;

public class AdminController {

    private static boolean reinscripcionActiva = false; 

    public String obtenerEstadoServidor() {
        return "🟢 Estado: Óptimo | Latencia: 24ms | Base de Datos: Conectada y Sincronizada";
    }

    public boolean ejecutarRespaldoBD() {
        System.out.println("Respaldando Base de Datos SQL Server en la nube...");
        return true;
    }

    public boolean isReinscripcionActiva() { return reinscripcionActiva; }
    
    public void toggleReinscripcion(boolean estado) {
        reinscripcionActiva = estado;
        System.out.println("Periodo de reinscripción cambiado a: " + estado);
    }
    
    public boolean crearGrupoOficial(String materia, String maestro, String grupo, String horario) {
        if(materia == null || materia.isEmpty() || maestro == null || maestro.isEmpty()) return false;
        
        String clave = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        MockDB.guardarNuevoGrupo(clave, materia, maestro, grupo, horario);
        return true;
    }

    public String[] obtenerInfoContacto(String numeroCuenta) {
        if(numeroCuenta.isEmpty()) return null;
        
        // Consultamos al MockDB
        Map<String, String> estudiante = MockDB.obtenerEstudiantePorCuenta(numeroCuenta);
        
        if (estudiante != null) {
            return new String[]{
                estudiante.get("nombre_completo"), 
                estudiante.get("telefono"),                     
                estudiante.get("correo"),         
                estudiante.get("calle"),            
                estudiante.get("colonia"),                         
                estudiante.get("municipio"),                
                estudiante.get("cp"),                          
                estudiante.get("pais"),                         
                estudiante.get("estado"),               
                estudiante.get("ciudad")                          
            };
        }
        return null; // Si no existe el estudiante
    }

    public List<String> obtenerDocumentosIngreso(String numeroCuenta) {
        // Consultamos al MockDB
        return MockDB.obtenerDocumentos(numeroCuenta);
    }

    public String[] obtenerIndicadoresRiesgo(String numeroCuenta) {
        // Consultamos al MockDB
        Map<String, String> estudiante = MockDB.obtenerEstudiantePorCuenta(numeroCuenta);
        
        if (estudiante != null) {
            return new String[]{ 
                estudiante.get("avance"), 
                estudiante.get("promedio_general"), 
                estudiante.get("promedio_ultimo"), 
                estudiante.get("situacion"), 
                estudiante.get("fecha_actualizacion"), 
                estudiante.get("riesgo") 
            };
        }
        return new String[]{"0%", "0.0", "0.0", "DESCONOCIDO", "N/A", "Desconocido"}; // Valores por defecto
    }

    public String generarHorarioInteligente(String numeroCuenta) {
        // Consultamos al MockDB
        Map<String, String> estudiante = MockDB.obtenerEstudiantePorCuenta(numeroCuenta);
        if (estudiante != null && estudiante.containsKey("analisis_ia")) {
            return estudiante.get("analisis_ia");
        }
        return "No hay análisis de IA disponible para esta cuenta.";
    }

    public String evaluarRecompensas() {
        // Consultamos al MockDB
        return MockDB.obtenerRecompensasGenerales();
    }
}