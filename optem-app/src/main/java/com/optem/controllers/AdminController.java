package com.optem.controllers;

import java.util.ArrayList;
import java.util.List;

public class AdminController {

    private static boolean reinscripcionActiva = false; // Control global del sistema

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

    public String[] obtenerInfoContacto(String numeroCuenta) {
        if(numeroCuenta.isEmpty()) return null;
        return new String[]{
            "JOSE ANTONIO FONSECA LABASTIDA", 
            "7226032706",                     
            "josefonceca3@gmail.com",         
            "Av. Universidad 123",            
            "Centro",                        
            "Toluca de Lerdo",                
            "50000",                          
            "México",                         
            "Estado de México",               
            "Toluca"                          
        };
    }

    public List<String> obtenerDocumentosIngreso(String numeroCuenta) {
        List<String> docs = new ArrayList<>();
        docs.add("✅ Protesta universitaria");
        docs.add("✅ Carta compromiso de entrega de documentos");
        docs.add("✅ Autorización de consulta de trayectoria");
        docs.add("✅ Autorización de validación de documentos");
        docs.add("✅ Documentos de ingreso");
        return docs;
    }

    public String[] obtenerIndicadoresRiesgo(String numeroCuenta) {
        return new String[]{
            "44.91%",      
            "9.2",         
            "9.5",         
            "REGULAR",     
            "22/10/2026",  
            "Sin riesgo"   
        };
    }

    public String generarHorarioInteligente(String numeroCuenta) {
        return "🤖 Análisis IA de Trayectoria (Alumno " + numeroCuenta + "):\n\n" +
               "🔹 Materias Sugeridas (No chocan horarios):\n" +
               "   - Arquitectura de Software (Lun-Mié 09:00)\n" +
               "   - Bases de Datos Avanzadas (Mar-Jue 11:00)\n\n" +
               "⚠️ Bloqueo de Adelanto de Materia:\n" +
               "   - No se puede adelantar 'Compiladores' porque requiere aprobar previamente 'Autómatas y Lenguajes Formales' (Materia Seriada).";
    }

    public String evaluarRecompensas() {
        return "🤖 Análisis de Rendimiento Docente y Estudiantil:\n\n" +
               "🏆 MAESTROS DESTACADOS (Asistencia + Calidad de clase):\n" +
               "   - Ing. Roberto Martínez (98% XP - Bono Docente Aprobado)\n\n" +
               "🎓 ALUMNOS PARA BECA (100% XP y Promedio > 9.0):\n" +
               "   - 2113911 - JOSE ANTONIO FONSECA (Beca Transporte Aprobada)\n" +
               "   - 2113912 - DANIELA PEREZ (Beca Académica Aprobada)";
    }
}