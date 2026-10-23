package com.optem.controllers;

import java.util.ArrayList;
import java.util.List;

public class AdminController {

    private static boolean reinscripcionActiva = false; // Control global del sistema

    // ==========================================
    // 1. ESTADÍSTICAS Y SALUD DEL SISTEMA
    // ==========================================
    public String obtenerEstadoServidor() {
        return "🟢 Estado: Óptimo | Latencia: 24ms | Base de Datos: Conectada y Sincronizada";
    }

    public boolean ejecutarRespaldoBD() {
        System.out.println("Respaldando Base de Datos SQL Server en la nube...");
        return true;
    }

    // ==========================================
    // 2. CONFIGURACIÓN DEL SISTEMA
    // ==========================================
    public boolean isReinscripcionActiva() { return reinscripcionActiva; }
    
    public void toggleReinscripcion(boolean estado) {
        reinscripcionActiva = estado;
        System.out.println("Periodo de reinscripción cambiado a: " + estado);
    }

    // ==========================================
    // 3. CONSULTA DE PERFILES (IMAGEN 1)
    // ==========================================
    public String[] obtenerInfoContacto(String numeroCuenta) {
        // Simulación de extracción de BD basada en tu diseño
        if(numeroCuenta.isEmpty()) return null;
        return new String[]{
            "JOSE ANTONIO FONSECA LABASTIDA", // Nombre
            "7226032706",                     // Teléfono
            "josefonceca3@gmail.com",         // Correo
            "Av. Universidad 123",            // Calle
            "Centro",                         // Colonia
            "Toluca de Lerdo",                // Población
            "50000",                          // Código Postal
            "México",                         // País
            "Estado de México",               // Estado
            "Toluca"                          // Municipio
        };
    }

    // ==========================================
    // 4. DOCUMENTOS Y TRAYECTORIA (IMAGENES 2 Y 3)
    // ==========================================
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
        // Simulando los datos de la imagen 3
        return new String[]{
            "44.91%",      // Grado de avance
            "9.2",         // Promedio general
            "9.5",         // Promedio último periodo
            "REGULAR",     // Situación actual
            "22/10/2026",  // Actualización
            "Sin riesgo"   // Nivel de bandera
        };
    }

    // ==========================================
    // 5. INTELIGENCIA ARTIFICIAL (HORARIOS Y RECOMPENSAS)
    // ==========================================
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