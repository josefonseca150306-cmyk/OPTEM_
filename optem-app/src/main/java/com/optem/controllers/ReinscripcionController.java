package com.optem.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.optem.database.MockDB;

// ¡Aseguramos que la clase sea public!
public class ReinscripcionController {

    public static class MateriaDTO {
        public String titulo, creditos, colorBorde, categoria;
        public boolean mixta;
        public String[] opciones;
        public MateriaDTO(String t, String c, boolean m, String cb, String cat, String[] o) { 
            titulo=t; creditos=c; mixta=m; colorBorde=cb; categoria=cat; opciones=o; 
        }
    }

    public static class BloqueHorario {
        public String texto, colorHex;
        public int colDia, rowHora, duracion;
        public BloqueHorario(String t, int c, int r, int d, String ch) { texto=t; colDia=c; rowHora=r; duracion=d; colorHex=ch; }
    }

    private static List<Map<String,String>> solicitudesInscripcion = new ArrayList<>();
    private static final String ARCHIVO_INSCRIPCIONES = "solicitudes_reinscripcion.dat";

    static {
        cargarSolicitudes();
    }

    public List<MateriaDTO> getMateriasDisponibles() {
        return MockDB.obtenerMateriasReinscripcion();
    }

    public Map<String, String> generarHorarioInteligente(String preferencia) {
        return MockDB.obtenerHorarioIA(preferencia);
    }

    public void inscribirAlumnoMateria(String correoAlumno, String nombreMateria) {
        if (correoAlumno == null || nombreMateria == null || nombreMateria.trim().isEmpty()) return;
        
        String claveMateria = "";
        for (Map<String, String> grupo : MockDB.obtenerTodosLosGrupos()) {
            if (grupo.get("nombre").equalsIgnoreCase(nombreMateria)) {
                claveMateria = grupo.get("clave");
                break;
            }
        }
        
        if (!claveMateria.isEmpty()) {
            MockDB.inscribirAlumno(correoAlumno, claveMateria);
        }
    }

    public void registrarSolicitudCompleta(String correoAlumno, String nombreAlumno) {
        solicitudesInscripcion.removeIf(s -> s.get("correo").equals(correoAlumno));
        
        Map<String, String> solicitud = new HashMap<>();
        solicitud.put("correo", correoAlumno);
        solicitud.put("nombre", nombreAlumno);
        solicitud.put("estado", "PENDIENTE DE PAGO");
        solicitud.put("ruta_hoja_pago", ""); 
        
        solicitudesInscripcion.add(0, solicitud);
        guardarSolicitudes();
    }

    public List<Map<String,String>> obtenerTodasLasSolicitudes() {
        return solicitudesInscripcion;
    }

    public void subirHojaDePago(String correoAlumno, String rutaPdf) {
        for (Map<String, String> sol : solicitudesInscripcion) {
            if (sol.get("correo").equals(correoAlumno)) {
                sol.put("ruta_hoja_pago", rutaPdf);
                sol.put("estado", "HOJA ENVIADA");
                break;
            }
        }
        guardarSolicitudes();
    }

    // CORRECCIÓN: Método con la sintaxis correcta
    public Map<String, String> verificarEstadoInscripcion(String correoAlumno) {
        for (Map<String, String> sol : solicitudesInscripcion) {
            if (sol.get("correo").equals(correoAlumno)) {
                return sol;
            }
        }
        return null;
    }

    private static void guardarSolicitudes() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_INSCRIPCIONES))) {
            oos.writeObject(solicitudesInscripcion);
        } catch (Exception e) {}
    }

    @SuppressWarnings("unchecked")
    private static void cargarSolicitudes() {
        File file = new File(ARCHIVO_INSCRIPCIONES);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            solicitudesInscripcion = (List<Map<String,String>>) ois.readObject();
        } catch (Exception e) { solicitudesInscripcion = new ArrayList<>(); }
    }

    public List<BloqueHorario> procesarSeleccion(String materia, String seleccion, String color) {
        List<BloqueHorario> bloques = new ArrayList<>();
        if (seleccion == null || seleccion.contains("— No cursar —")) return bloques;
        
        String nombreCorto = materia.split("\\(")[0].trim();
        
        List<Integer> dias = new ArrayList<>();
        if (seleccion.contains("Lun")) dias.add(1);
        if (seleccion.contains("Mar")) dias.add(2);
        if (seleccion.contains("Mié")) dias.add(3);
        if (seleccion.contains("Jue")) dias.add(4);
        if (seleccion.contains("Vie")) dias.add(5);
        if (seleccion.contains("Sáb")) dias.add(6);

        int horaInicio = 7, duracion = 2; 
        
        Matcher m = Pattern.compile("(\\d{2}):00-(\\d{2}):00").matcher(seleccion);
        if (m.find()) {
            horaInicio = Integer.parseInt(m.group(1));
            int horaFin = Integer.parseInt(m.group(2));
            duracion = horaFin - horaInicio;
        }

        int rowHora = horaInicio - 7;
        for (int dia : dias) {
            bloques.add(new BloqueHorario(nombreCorto, dia, rowHora, duracion, color));
        }
        return bloques;
    }

    public Map<String, List<String>> getMapaCurricularEstudiante() {
        Map<String, List<String>> mapa = new LinkedHashMap<>();
        mapa.put("Periodo 4", Arrays.asList("Inglés 7|#008000", "Diseño Compiladores|#008000", "Interacción H-C|#008000", "Arq. Computadoras|#008000", "Estructuras Datos|#008000", "Microcontroladores|#008000", "Requisitos Soft|#008000"));
        mapa.put("Periodo 5", Arrays.asList("Inglés 8|#8BC34A", "Bases de Datos|#8BC34A", "Innovación Sust.|#8BC34A", "Prog. Paralela|#8BC34A", "Análisis y Diseño|#8BC34A", "Redes y Telecom.|#8BC34A", "Met. Desarrollo|#8BC34A"));
        mapa.put("Periodo 6", Arrays.asList("Administración|#008000", "BD Avanzadas|transparent", "Graficación|transparent", "Teoría Lenguajes|#008000", "Desarrollo Web|transparent", "Arq. de Software|transparent", "Pruebas y Mant.|transparent"));
        mapa.put("Periodo 7", Arrays.asList("Inteligencia Art.|transparent", "Admin BD|transparent", "Ética Informática|transparent", "Métricas Software|transparent", "Simulación|transparent", "Sistemas Op.|transparent", "Sistemas Dist.|transparent"));
        mapa.put("Periodo 8", Arrays.asList("Proc. Imágenes|transparent", "Organización Proyectos|transparent", "Datawarehouse|transparent", "Integradora Prof.|transparent", "Calidad Software|transparent", "Herr. Admin. Sis|transparent", "Seguridad Inf.|transparent"));
        return mapa;
    }
}