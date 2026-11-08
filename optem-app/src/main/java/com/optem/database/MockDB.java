package com.optem.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.optem.controllers.ReinscripcionController.MateriaDTO;

public class MockDB {

    private static final String CORREO_UNIVERSAL = "jfonsecal001@alumno.uaemex.mx";
    private static final String CUENTA_ESTUDIANTE_MAESTRO = "2113911";
    private static final String CUENTA_ADMINISTRADOR = "1503061";

    // =========================================================================
    // "TABLAS" EN MEMORIA (Estado global y mutables)
    // =========================================================================
    
    private static final List< Map<String, String> > tablaCatalogoMaterias = new ArrayList<>();
    private static final List< Map<String, String> > tablaGrupos = new ArrayList<>();
    private static final List< Map<String, String> > tablaInscripciones = new ArrayList<>();
    
    // --- NUEVAS TABLAS PARA EL ADMINISTRADOR ---
    private static final List< Map<String, String> > tablaEstudiantes = new ArrayList<>();
    private static final Map< String, List<String> > tablaDocumentos = new HashMap<>();
    private static final Map< String, String > tablaRecompensas = new HashMap<>();

    static {
        inicializarMapaCurricularOficial();
        
        // --- HORARIO OFICIAL UAEMÉX (Sexto Semestre) ---
        tablaGrupos.add(crearMateria("ISWK11", "BASES DE DATOS", "M. EN CCO. MARCELA CAMACHO AVILA", "S6", "Lun 11:30-13:30 / Mié 11:30-13:30"));
        tablaGrupos.add(crearMateria("ISWK12", "INNOVACION Y SUSTENTABILIDAD", "M. EN ADM. TEC. DE INF. JOSE ESTEBAN RUIZ MELO", "SH", "Lun 20:00-22:00 / Mié 20:00-22:00"));
        tablaGrupos.add(crearMateria("ISWK13", "PROGRAMACION PARALELA", "I. EN T.I.C. JESUS MARES MONTES", "S5", "Lun 09:00-11:00 / Mié 09:00-11:00"));
        tablaGrupos.add(crearMateria("LMU505", "INGLES 8", "L.E.I. ERIKA LIZBETH ALANIS CONTRERAS", "01", "Lun 17:00-19:00 / Mié 17:00-19:00"));
        tablaGrupos.add(crearMateria("L40817", "METODOS Y MODELOS DE DESARROLLO DE SOFTWARE", "M. EN C.C. JOSE RAFAEL CRUZ REYES", "S6", "Mar 17:00-19:30 / Jue 17:00-19:30"));
        tablaGrupos.add(crearMateria("L40834", "ANALISIS Y DISEÑO DE SOFTWARE", "DR. EN C. DE LA COMP. GRISELDA ARELI MATIAS MENDOZA", "S6", "Mar 09:30-12:00 / Jue 09:30-12:00"));
        tablaGrupos.add(crearMateria("L40851", "REDES Y TELECOMUNICACIONES", "ING. ISMAEL GONZALEZ DEL CAMPO", "S6", "Mar 07:00-09:30 / Jue 07:00-09:30"));
        tablaGrupos.add(crearMateria("L40853", "SIMULACION", "DR. EN C. DE LA COMP. YANET HERNANDEZ CASIMIRO", "S7", "Mar 15:00-16:30 / Jue 15:00-16:30"));

        // Inscripciones iniciales simuladas para tu cuenta (Las 8 materias)
        for (Map<String, String> grupo : tablaGrupos) {
            Map<String, String> ins = new HashMap<>(); 
            ins.put("correo_alumno", CORREO_UNIVERSAL); 
            ins.put("clave_materia", grupo.get("clave")); 
            tablaInscripciones.add(ins);
        }

        // --- INICIALIZAR DATOS DEL ADMINISTRADOR ---
        inicializarDatosAdmin();
    }

    private static void inicializarDatosAdmin() {
        // Datos del Estudiante (Jose Antonio)
        Map<String, String> est1 = new HashMap<>();
        est1.put("numero_cuenta", "2113911");
        est1.put("nombre_completo", "JOSE ANTONIO FONSECA LABASTIDA");
        est1.put("telefono", "7226032706");
        est1.put("correo", "josefonceca3@gmail.com");
        est1.put("calle", "Av. Universidad 123");
        est1.put("colonia", "Centro");
        est1.put("municipio", "Toluca de Lerdo");
        est1.put("cp", "50000");
        est1.put("pais", "México");
        est1.put("estado", "Estado de México");
        est1.put("ciudad", "Toluca");
        est1.put("avance", "44.91%");
        est1.put("promedio_general", "9.2");
        est1.put("promedio_ultimo", "9.5");
        est1.put("situacion", "REGULAR");
        est1.put("fecha_actualizacion", "22/10/2026");
        est1.put("riesgo", "Sin riesgo");
        est1.put("analisis_ia", "🤖 Análisis IA de Trayectoria (Alumno 2113911):\n\n🔹 Materias Sugeridas (No chocan horarios):\n   - Arquitectura de Software (Lun-Mié 09:00)\n   - Bases de Datos Avanzadas (Mar-Jue 11:00)\n\n⚠️ Bloqueo de Adelanto de Materia:\n   - No se puede adelantar 'Compiladores' porque requiere aprobar previamente 'Autómatas y Lenguajes Formales'.");
        tablaEstudiantes.add(est1);

        // Documentos de Ingreso (Jose Antonio)
        List<String> docs = new ArrayList<>();
        docs.add("✅ Protesta universitaria");
        docs.add("✅ Carta compromiso de entrega de documentos");
        docs.add("✅ Autorización de consulta de trayectoria");
        docs.add("✅ Autorización de validación de documentos");
        docs.add("✅ Documentos de ingreso");
        tablaDocumentos.put("2113911", docs);

        // Recompensas Generales
        tablaRecompensas.put("general", "🤖 Análisis de Rendimiento:\n\n🏆 MAESTROS DESTACADOS:\n   - Ing. Roberto Martínez (98% XP)\n\n🎓 ALUMNOS PARA BECA:\n   - 2113911 - JOSE ANTONIO FONSECA (Beca Transporte Aprobada)\n   - 2113912 - DANIELA PEREZ (Beca Académica Aprobada)");
    }

    private static void inicializarMapaCurricularOficial() {
        // PERIODO 1
        agregarBloqueCatalogo("1", "BASICO", "Algebra", "Algoritmica", "Computacion", "Fisica", "Matematicas discretas", "Quimica");
        
        // PERIODO 2
        agregarBloqueCatalogo("2", "BASICO", "Calculo diferencial e integral", "Programacion", "Circuitos electricos", "Teoria de algoritmos");
        agregarBloqueCatalogo("2", "SUSTANTIVO", "Teoria de sistemas");

        // PERIODO 3
        agregarBloqueCatalogo("3", "BASICO", "Logica digital", "Programacion orientada a objetos", "Probabilidad y estadistica", "Ingenieria de Software", "Teoria de automatas y lenguajes formales", "Desarrollo de habilidades directivas");
        agregarBloqueCatalogo("3", "SUSTANTIVO", "Ingles 5");

        // PERIODO 4
        agregarBloqueCatalogo("4", "BASICO", "Arquitectura de computadoras", "Estructura de datos", "Programacion de microcontroladores", "Requisitos y especificacion de software", "Diseño de compiladores e intérpretes", "Interaccion humano computadora");
        agregarBloqueCatalogo("4", "SUSTANTIVO", "Ingles 6");

        // PERIODO 5
        agregarBloqueCatalogo("5", "BASICO", "Bases de datos");
        agregarBloqueCatalogo("5", "SUSTANTIVO", "Programacion paralela", "Redes y telecomunicaciones", "Analisis y diseño de software", "Modelos de desarrollo de software", "Ingles 7", "Innovacion y sustentabilidad");

        // PERIODO 6
        agregarBloqueCatalogo("6", "SUSTANTIVO", "Bases de datos avanzadas", "Teoria de lenguajes de programacion", "Graficacion");
        agregarBloqueCatalogo("6", "INTEGRAL", "Pruebas y mantenimiento de software", "Desarrollo de aplicaciones web", "Arquitectura de software", "Administracion");
        agregarBloqueCatalogo("6", "SUSTANTIVO", "Ingles 8");

        // PERIODO 7
        agregarBloqueCatalogo("7", "SUSTANTIVO", "Administracion de bases de datos", "Sistemas operativos", "Sistemas distribuidos");
        agregarBloqueCatalogo("7", "INTEGRAL", "Inteligencia artificial", "Simulacion", "Metricas de software", "Etica y derecho informatico");

        // PERIODO 8
        agregarBloqueCatalogo("8", "INTEGRAL", "Calidad del software", "Herramientas para la administracion y programacion de sistemas", "Seguridad informatica", "Datawarehouse", "Integrativa profesional", "Tecnicas y metodos de procesamiento", "Administracion y organizacion de proyectos de software", "Optativa 1");

        // PERIODO 9
        agregarBloqueCatalogo("9", "INTEGRAL", "Auditoria de sistemas", "Computo evolutivo", "Mineria de datos", "Programacion aplicada a sistemas de...", "Sistemas de informacion geografica", "Optativa 2");
        
        // PERIODO 10
        agregarBloqueCatalogo("10", "INTEGRAL", "Practica profesional");
        
        // OPTATIVAS DISPONIBLES (Periodo 8 y 9)
        agregarBloqueCatalogo("OPT", "INTEGRAL", "Tecnologias para el tratamiento de la informacion", "Bases de datos orientadas a objetos", "Desarrollo multimedia", "Administracion de empresas de desarrollo de software", "Software embebido", "Desarrollo de aplicaciones para Sistemas de Informacion Geografica", "Redes neuronales y logica difusa", "Reconocimiento de patrones");
    }

    private static void agregarBloqueCatalogo(String semestre, String nucleo, String... materias) {
        for (String mat : materias) {
            Map<String, String> registro = new HashMap<>();
            registro.put("semestre", semestre);
            registro.put("nucleo", nucleo);
            registro.put("nombre", mat.toUpperCase());
            tablaCatalogoMaterias.add(registro);
        }
    }

    // =========================================================================
    // MÉTODOS DE AUTENTICACIÓN Y PERFIL
    // =========================================================================
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

    public static Map<String, String> obtenerPerfilUsuario() {
        Map<String, String> perfil = new HashMap<>();
        perfil.put("nombre_completo", "Jose Antonio Fonseca Labastida");
        perfil.put("carrera", "Licenciatura en Ingeniería en Software (2016)");
        perfil.put("promedio_general", "9.2");
        perfil.put("avance_creditos", "44.91%");
        perfil.put("creditos_obtenidos", "185/412");
        perfil.put("situacion", "Regular");
        return perfil;
    }

    // =========================================================================
    // MÉTODOS CRUD PARA EL ADMINISTRADOR (NUEVOS)
    // =========================================================================

    public static Map<String, String> obtenerEstudiantePorCuenta(String numeroCuenta) {
        for (Map<String, String> estudiante : tablaEstudiantes) {
            if (estudiante.get("numero_cuenta").equals(numeroCuenta)) {
                return estudiante;
            }
        }
        return null; 
    }

    public static List<String> obtenerDocumentos(String numeroCuenta) {
        return tablaDocumentos.getOrDefault(numeroCuenta, new ArrayList<>());
    }

    public static String obtenerRecompensasGenerales() {
        return tablaRecompensas.getOrDefault("general", "No hay datos de recompensas disponibles.");
    }

    // =========================================================================
    // MÉTODOS CRUD PARA GRUPOS Y MATERIAS
    // =========================================================================

    public static List< Map<String, String> > obtenerMateriasDelAlumno(String correoAlumno) {
        List< Map<String, String> > misMaterias = new ArrayList<>();
        for (Map<String, String> inscripcion : tablaInscripciones) {
            if (inscripcion.get("correo_alumno").equals(correoAlumno)) {
                String claveInscrita = inscripcion.get("clave_materia");
                for (Map<String, String> grupo : tablaGrupos) {
                    if (grupo.get("clave").equals(claveInscrita)) {
                        misMaterias.add(grupo);
                        break;
                    }
                }
            }
        }
        return misMaterias;
    }

    public static List< Map<String, String> > obtenerTodosLosGrupos() {
        return tablaGrupos;
    }

    public static void guardarNuevoGrupo(String clave, String nombre, String profesor, String grupo, String horario) {
        tablaGrupos.add(crearMateria(clave, nombre, profesor, grupo, horario));
    }

    public static void inscribirAlumno(String correoAlumno, String claveMateria) {
        Map<String, String> nuevaInscripcion = new HashMap<>();
        nuevaInscripcion.put("correo_alumno", correoAlumno);
        nuevaInscripcion.put("clave_materia", claveMateria);
        tablaInscripciones.add(nuevaInscripcion);
    }

    private static Map<String, String> crearMateria(String clave, String nombre, String profesor, String grupo, String horario) {
        Map<String, String> mat = new HashMap<>();
        mat.put("clave", clave);
        mat.put("nombre", nombre.toUpperCase());
        mat.put("profesor", profesor);
        mat.put("grupo", grupo);
        mat.put("horario", horario);
        
        if (nombre.toUpperCase().contains("SOFTWARE") || nombre.toUpperCase().contains("DATOS")) {
            mat.put("reunion", "Teams - Jueves 11:30 AM");
            mat.put("tarea", "Práctica 3 (Vence hoy 23:59)");
            mat.put("mensaje", "Favor de repasar el capítulo 4 para la clase de hoy.");
        } else if (nombre.toUpperCase().contains("PROGRAMACION")) {
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
    // MÓDULO DE REINSCRIPCIÓN ESTÁTICA
    // =========================================================================
    public static List<MateriaDTO> obtenerMateriasReinscripcion() {
        List<MateriaDTO> lista = new ArrayList<>();
        lista.add(new MateriaDTO("BASES DE DATOS AVANZADAS", "8 créditos", false, "#9E9E9E", "REGULAR", new String[]{"— No cursar —", "G6A · Lun/Mié 07:00-09:00", "G6B · Mar/Jue 13:00-15:00"}));
        lista.add(new MateriaDTO("TEORIA DE LENGUAJES DE PROGRAMACION", "6 créditos", false, "#9E9E9E", "REGULAR", new String[]{"— No cursar —", "G6A · Mar/Jue 09:00-11:00", "G6B · Lun/Mié 11:00-13:00"}));
        lista.add(new MateriaDTO("GRAFICACION", "8 créditos", false, "#9E9E9E", "REGULAR", new String[]{"— No cursar —", "G6A · Lun/Mié 09:00-11:00", "G6B · Sáb 08:00-12:00"}));
        lista.add(new MateriaDTO("PRUEBAS Y MANTENIMIENTO DE SOFTWARE", "6 créditos", false, "#616161", "REGULAR", new String[]{"— No cursar —", "G6A · Lun/Mié 15:00-17:00", "G6B · Mar/Jue 15:00-17:00"}));
        lista.add(new MateriaDTO("DESARROLLO DE APLICACIONES WEB", "8 créditos", true, "#616161", "REGULAR", new String[]{"— No cursar —", "G6A · Lun/Mié 13:00-15:00", "G6B (Línea) · Vie 15:00-19:00"}));
        lista.add(new MateriaDTO("ARQUITECTURA DE SOFTWARE", "8 créditos", true, "#616161", "REGULAR", new String[]{"— No cursar —", "G6A · Mar/Jue 11:00-13:00", "G6B · Lun/Mié 17:00-19:00"}));
        lista.add(new MateriaDTO("ADMINISTRACION", "6 créditos", false, "#616161", "REGULAR", new String[]{"— No cursar —", "G6A · Lun/Mié 07:00-09:00", "G6B · Mar/Jue 13:00-15:00"}));
        lista.add(new MateriaDTO("INGLES 8", "4 créditos", false, "#9E9E9E", "REGULAR", new String[]{"— No cursar —", "G6A · Vie 07:00-09:00", "G6B · Vie 09:00-11:00"}));

        return lista;
    }

    public static Map<String, String> obtenerHorarioIA(String preferencia) {
        Map<String, String> rec = new HashMap<>();
        switch (preferencia) {
            case "MATUTINO": 
                rec.put("ADMINISTRACION", "G6A · Lun/Mié 07:00-09:00");
                rec.put("BASES DE DATOS AVANZADAS", "G6A · Mar/Jue 07:00-09:00");
                rec.put("GRAFICACION", "G6A · Lun/Mié 09:00-11:00");
                rec.put("TEORIA DE LENGUAJES DE PROGRAMACION", "G6A · Mar/Jue 09:00-11:00");
                rec.put("DESARROLLO DE APLICACIONES WEB", "G6A · Lun/Mié 13:00-15:00");
                rec.put("ARQUITECTURA DE SOFTWARE", "G6A · Mar/Jue 11:00-13:00");
                rec.put("PRUEBAS Y MANTENIMIENTO DE SOFTWARE", "G6A · Lun/Mié 15:00-17:00");
                rec.put("INGLES 8", "G6A · Vie 07:00-09:00");
                break;
            case "TARDES": 
                rec.put("ADMINISTRACION", "G6B · Mar/Jue 13:00-15:00");
                rec.put("BASES DE DATOS AVANZADAS", "G6B · Lun/Mié 15:00-17:00");
                rec.put("GRAFICACION", "G6B · Sáb 08:00-12:00");
                rec.put("TEORIA DE LENGUAJES DE PROGRAMACION", "G6B · Lun/Mié 11:00-13:00");
                rec.put("DESARROLLO DE APLICACIONES WEB", "G6B (Línea) · Vie 15:00-19:00");
                rec.put("ARQUITECTURA DE SOFTWARE", "G6B · Lun/Mié 17:00-19:00");
                rec.put("PRUEBAS Y MANTENIMIENTO DE SOFTWARE", "G6B · Mar/Jue 15:00-17:00");
                rec.put("INGLES 8", "G6B · Vie 09:00-11:00");
                break;
            case "SIN_VIERNES": 
                rec.put("ADMINISTRACION", "G6A · Lun/Mié 07:00-09:00");
                rec.put("BASES DE DATOS AVANZADAS", "G6B · Lun/Mié 15:00-17:00");
                rec.put("GRAFICACION", "G6A · Lun/Mié 09:00-11:00");
                rec.put("TEORIA DE LENGUAJES DE PROGRAMACION", "G6A · Mar/Jue 09:00-11:00");
                rec.put("DESARROLLO DE APLICACIONES WEB", "G6A · Lun/Mié 13:00-15:00");
                rec.put("ARQUITECTURA DE SOFTWARE", "G6A · Mar/Jue 11:00-13:00");
                rec.put("PRUEBAS Y MANTENIMIENTO DE SOFTWARE", "G6B · Mar/Jue 15:00-17:00");
                break;
            default:
                rec.put("ADMINISTRACION", "G6A · Lun/Mié 07:00-09:00");
                rec.put("BASES DE DATOS AVANZADAS", "G6A · Mar/Jue 07:00-09:00");
                break;
        }
        return rec;
    }
}