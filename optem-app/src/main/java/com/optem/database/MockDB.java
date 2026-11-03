package com.optem.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BASE DE DATOS SIMULADA (MOCK)
 * Esta clase provee datos falsos/estáticos para probar la interfaz visual.
 * Cuando el servidor real esté listo, esta clase se puede eliminar o ignorar.
 */
public class MockDB {

    // ==========================================
    // 1. CREDENCIALES MAESTRAS (Tu acceso universal)
    // ==========================================
    private static final String CORREO_UNIVERSAL = "jfonsecal001@alumno.uaemex.mx";
    private static final String CUENTA_ESTUDIANTE_MAESTRO = "2113911";
    private static final String CUENTA_ADMINISTRADOR = "1503061";

    public static boolean autenticar(String correo, String password, String rol) {
        if (correo.equals(CORREO_UNIVERSAL)) {
            if ((rol.equals("ESTUDIANTE") || rol.equals("MAESTRO")) && password.equals(CUENTA_ESTUDIANTE_MAESTRO)) {
                return true;
            }
            if (rol.equals("ADMINISTRADOR") && password.equals(CUENTA_ADMINISTRADOR)) {
                return true;
            }
        }
        return false;
    }

    // ==========================================
    // 2. DATOS DEL PERFIL DEL USUARIO
    // ==========================================
    public static Map<String, String> obtenerPerfilUsuario() {
        Map<String, String> perfil = new HashMap<>();
        perfil.put("nombre_completo", "Jose Antonio Fonseca Labastida");
        perfil.put("carrera", "Licenciado en Ingeniería en Software (ISW-F16)");
        perfil.put("promedio_general", "9.2");
        perfil.put("avance_creditos", "44.91%");
        perfil.put("situacion", "Regular");
        return perfil;
    }

    // ==========================================
    // 3. MATERIAS Y HORARIOS (Quinto Periodo)
    // ==========================================
    public static List<Map<String,String>> obtenerMateriasRegistradas() {
        List<Map<String,String>> materias = new ArrayList<>();

        materias.add(crearMateria("ISWK11", "BASES DE DATOS", "M. EN CCO. MARCELA CAMACHO AVILA", "S6", "LUNES 11:30 A 13:30 / MIERCOLES 11:30 A 13:30"));
        materias.add(crearMateria("ISWK12", "INNOVACION Y SUSTENTABILIDAD", "M. EN ADM. TEC. DE INF. JOSE ESTEBAN RUIZ MELO", "SH", "LUNES 20:00 A 22:00 / MIERCOLES 20:00 A 22:00"));
        materias.add(crearMateria("ISWK13", "PROGRAMACION PARALELA", "I. EN T.I.C. JESUS MARES MONTES", "S5", "LUNES 09:00 A 11:00 / MIERCOLES 09:00 A 11:00"));
        materias.add(crearMateria("LMU505", "INGLES 8", "L.E.I. ERIKA LIZBETH ALANIS CONTRERAS", "01", "LUNES 17:00 A 19:00 / MIERCOLES 17:00 A 19:00"));
        materias.add(crearMateria("L40817", "METODOS Y MODELOS DE DESARROLLO DE SOFTWARE", "M. EN C.C. JOSE RAFAEL CRUZ REYES", "S6", "MARTES 17:00 A 19:30 / JUEVES 17:00 A 19:30"));
        materias.add(crearMateria("L40834", "ANALISIS Y DISEÑO DE SOFTWARE", "DR. EN C. DE LA COMP. GRISELDA ARELI MATIAS MENDOZA", "S6", "MARTES 09:30 A 12:00 / JUEVES 09:30 A 12:00"));
        materias.add(crearMateria("L40851", "REDES Y TELECOMUNICACIONES", "ING. ISMAEL GONZALEZ DEL CAMPO", "S6", "MARTES 07:00 A 09:30 / JUEVES 07:00 A 09:30"));
        materias.add(crearMateria("L40853", "SIMULACION", "DR. EN C. DE LA COMP. YANET HERNANDEZ CASIMIRO", "S7", "MARTES 15:00 A 16:30 / JUEVES 15:00 A 16:30"));

        return materias;
    }

    private static Map<String, String> crearMateria(String clave, String nombre, String profesor, String grupo, String horario) {
        Map<String, String> mat = new HashMap<>();
        mat.put("clave", clave);
        mat.put("nombre", nombre);
        mat.put("profesor", profesor);
        mat.put("grupo", grupo);
        mat.put("horario", horario);
        
        // --- SIMULACIÓN DE DATOS DINÁMICOS POR MATERIA ---
        if (nombre.contains("SOFTWARE") || nombre.contains("BASES")) {
            mat.put("reunion", "Teams - Jueves 11:30 AM");
            mat.put("tarea", "Práctica 3 (Vence hoy 23:59)");
            mat.put("mensaje", "Favor de repasar el capítulo 4 para la clase de hoy.");
        } else if (nombre.contains("PROGRAMACION")) {
            mat.put("reunion", ""); 
            mat.put("tarea", "Proyecto Final Fase 1 (Vence en 2 días)");
            mat.put("mensaje", "Ya subí las diapositivas al repositorio.");
        } else {
            mat.put("reunion", "");
            mat.put("tarea", "");
            mat.put("mensaje", "Sin avisos recientes.");
        }
        return mat;
    }

    // =========================================================================
    // DATOS DE REINSCRIPCIÓN (PERIODO 6, REZAGOS Y ADELANTOS)
    // =========================================================================
    public static List<com.optem.controllers.ReinscripcionController.MateriaDTO> obtenerMateriasReinscripcion() {
        List<com.optem.controllers.ReinscripcionController.MateriaDTO> lista = new java.util.ArrayList<>();
        
        // REGULARES
        lista.add(new com.optem.controllers.ReinscripcionController.MateriaDTO("Bases de Datos Avanzadas", "8 créditos", false, "#2196F3", "REGULAR", new String[]{"— No cursar —", "G6A · Lun/Mié 07:00-09:00", "G6B · Mar/Jue 13:00-15:00", "G6C · Vie 07:00-11:00"}));
        lista.add(new com.optem.controllers.ReinscripcionController.MateriaDTO("Graficación", "8 créditos", false, "#00BCD4", "REGULAR", new String[]{"— No cursar —", "G6A · Mar/Jue 07:00-09:00", "G6B · Lun/Mié 15:00-17:00", "G6C · Sáb 08:00-12:00"}));
        lista.add(new com.optem.controllers.ReinscripcionController.MateriaDTO("Desarrollo de Aplicaciones Web", "8 créditos", true, "#3F51B5", "REGULAR", new String[]{"— No cursar —", "G6A · Lun/Mié 09:00-11:00", "G6B · Mar/Jue 17:00-19:00"}));
        lista.add(new com.optem.controllers.ReinscripcionController.MateriaDTO("Arquitectura de Software", "8 créditos", true, "#3F51B5", "REGULAR", new String[]{"— No cursar —", "G6A · Mar/Jue 09:00-11:00", "G6B (Línea) · Vie 15:00-19:00"}));
        lista.add(new com.optem.controllers.ReinscripcionController.MateriaDTO("Pruebas y Manten. de Software", "6 créditos", true, "#009688", "REGULAR", new String[]{"— No cursar —", "G6A · Lun/Mié 11:00-13:00", "G6B · Mar/Jue 15:00-17:00"}));

        // REZAGADAS
        lista.add(new com.optem.controllers.ReinscripcionController.MateriaDTO("Probabilidad y Estadística (2da Inscripción)", "8 créditos", false, "#D32F2F", "REZAGO", new String[]{"— No cursar —", "G4A · Mar/Jue 11:00-13:00", "G4B · Vie 11:00-15:00"}));

        // ADELANTAR
        lista.add(new com.optem.controllers.ReinscripcionController.MateriaDTO("Datawarehouse (Adelanto 8vo)", "6 créditos", false, "#9C27B0", "ADELANTO", new String[]{"— No cursar —", "G8A · Lun/Mié 13:00-15:00", "G8B · Sáb 13:00-17:00"}));
        lista.add(new com.optem.controllers.ReinscripcionController.MateriaDTO("Calidad del Software (Adelanto 8vo)", "6 créditos", true, "#9C27B0", "ADELANTO", new String[]{"— No cursar —", "G8A · Mar/Jue 11:00-13:00", "G8B (Línea) · Vie 15:00-19:00"}));

        return lista;
    }

    public static java.util.Map<String, String> obtenerHorarioIA(String preferencia) {
        java.util.Map<String, String> rec = new java.util.HashMap<>();
        switch (preferencia) {
            case "MATUTINO": 
                rec.put("Bases de Datos Avanzadas", "G6A · Lun/Mié 07:00-09:00");
                rec.put("Graficación", "G6A · Mar/Jue 07:00-09:00");
                rec.put("Desarrollo de Aplicaciones Web", "G6A · Lun/Mié 09:00-11:00");
                rec.put("Arquitectura de Software", "G6A · Mar/Jue 09:00-11:00");
                rec.put("Pruebas y Manten. de Software", "G6A · Lun/Mié 11:00-13:00");
                break;
            case "TARDES": 
                rec.put("Bases de Datos Avanzadas", "G6B · Mar/Jue 13:00-15:00");
                rec.put("Graficación", "G6B · Lun/Mié 15:00-17:00");
                rec.put("Desarrollo de Aplicaciones Web", "G6B · Mar/Jue 17:00-19:00");
                rec.put("Arquitectura de Software", "G6B (Línea) · Vie 15:00-19:00");
                rec.put("Pruebas y Manten. de Software", "G6B · Mar/Jue 15:00-17:00");
                break;
            case "SIN_VIERNES": 
                rec.put("Bases de Datos Avanzadas", "G6B · Mar/Jue 13:00-15:00");
                rec.put("Graficación", "G6B · Lun/Mié 15:00-17:00");
                rec.put("Desarrollo de Aplicaciones Web", "G6A · Lun/Mié 09:00-11:00");
                rec.put("Arquitectura de Software", "G6A · Mar/Jue 09:00-11:00");
                rec.put("Pruebas y Manten. de Software", "G6A · Lun/Mié 11:00-13:00");
                break;
            case "CON_ADELANTO":
                rec.put("Bases de Datos Avanzadas", "G6A · Lun/Mié 07:00-09:00");
                rec.put("Desarrollo de Aplicaciones Web", "G6A · Lun/Mié 09:00-11:00");
                rec.put("Arquitectura de Software", "G6A · Mar/Jue 09:00-11:00");
                rec.put("Datawarehouse (Adelanto 8vo)", "G8A · Lun/Mié 13:00-15:00");
                break;
            case "CON_REZAGO":
                rec.put("Bases de Datos Avanzadas", "G6A · Lun/Mié 07:00-09:00");
                rec.put("Graficación", "G6A · Mar/Jue 07:00-09:00");
                rec.put("Desarrollo de Aplicaciones Web", "G6A · Lun/Mié 09:00-11:00");
                rec.put("Probabilidad y Estadística (2da Inscripción)", "G4A · Mar/Jue 11:00-13:00");
                break;
        }
        return rec;
    }
}