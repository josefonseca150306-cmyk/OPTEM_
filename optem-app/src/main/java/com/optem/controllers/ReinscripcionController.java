package com.optem.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.optem.database.MockDB;

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

    // ALGORITMO DINÁMICO: Calcula el horario sin importar el día ni la hora, leyendo el String.
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

        int horaInicio = 7, duracion = 2; // Por defecto
        
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

    // MAPA CURRICULAR: Centralizado aquí para limpiar la Vista
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