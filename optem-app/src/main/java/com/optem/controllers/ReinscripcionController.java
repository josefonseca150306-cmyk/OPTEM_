package com.optem.controllers;

import java.util.ArrayList;
import java.util.List;

public class ReinscripcionController {

    // --- DTOs (Objetos para transferir datos a la Vista) ---
    public static class MateriaDTO {
        public String titulo, creditos, colorBorde;
        public boolean mixta;
        public String[] opciones;
        public MateriaDTO(String t, String c, boolean m, String cb, String[] o) { titulo=t; creditos=c; mixta=m; colorBorde=cb; opciones=o; }
    }

    public static class SugerenciaDTO {
        public String titulo, creditos, materias, detalle;
        public SugerenciaDTO(String t, String c, String m, String d) { titulo=t; creditos=c; materias=m; detalle=d; }
    }

    public static class BloqueHorario {
        public String texto, colorHex;
        public int colDia, rowHora, duracion;
        public BloqueHorario(String t, int c, int r, int d, String ch) { texto=t; colDia=c; rowHora=r; duracion=d; colorHex=ch; }
    }

    // --- DATOS DUROS ---
    public List<MateriaDTO> getMateriasDisponibles() {
        List<MateriaDTO> lista = new ArrayList<>();
        lista.add(new MateriaDTO("Programación de Microcontroladores (L40848)", "6 créditos", false, "#4CAF50", new String[]{"— No cursar —", "Grupo SE · Lun/Mié 13:00-15:00", "Grupo SF · Lun/Mié 11:00-13:00", "Grupo SG · Mié/Vie 07:30-09:30"}));
        lista.add(new MateriaDTO("Requisitos y Especificación de Software (L40852)", "6 créditos", true, "#2196F3", new String[]{"— No cursar —", "Grupo SE · Lun/Mié 19:00-20:30", "Grupo S3 · Mar/Jue 07:00-08:30", "Grupo S4 · Mar/Jue 08:30-10:00"}));
        lista.add(new MateriaDTO("Administración (L40831)", "6 créditos", false, "#9C27B0", new String[]{"— No cursar —", "Grupo SH · Lun/Mié 09:00-10:30"}));
        lista.add(new MateriaDTO("Arquitectura de Software (L40836)", "6 créditos", true, "#FF9800", new String[]{"— No cursar —", "Grupo SH · Mar/Jue 13:30-15:30"}));
        lista.add(new MateriaDTO("Desarrollo de Aplicaciones Web (ISWK14)", "6 créditos", false, "#E53935", new String[]{"— No cursar —", "Grupo SH · Vie 07:00-11:00"}));
        lista.add(new MateriaDTO("Graficación (L40844)", "6 créditos", true, "#009688", new String[]{"— No cursar —", "Grupo SI · Mar/Jue 10:00-12:00"}));
        lista.add(new MateriaDTO("Teoría de Lenguajes de Programación (L40858)", "6 créditos", true, "#795548", new String[]{"— No cursar —", "Grupo S5 · Jue 18:00-19:30"}));
        lista.add(new MateriaDTO("Estructuras de Datos (L40840)", "8 créditos", false, "#607D8B", new String[]{"— No cursar —", "Grupo SG · Sáb 08:00-13:00"}));
        return lista;
    }

    public List<SugerenciaDTO> getSugerenciasIA() {
        List<SugerenciaDTO> lista = new ArrayList<>();
        lista.add(new SugerenciaDTO("Opción 1", "55", "9", "• Programación Micro.: Lun/Mié 13:00-15:00\nRequisitos de Software: Mar/Jue 07:00-08:30\nAdministración: Lun/Mié 09:00-10:30\nDesarrollo Web: Vie 07:00-11:00\nGraficación: Mar/Jue 10:00-12:00\nEstructuras de Datos: Sáb 08:00-13:00"));
        lista.add(new SugerenciaDTO("Opción 2", "55", "9", "• Programación Micro.: Lun/Mié 13:00-15:00\nRequisitos de Software: Lun/Mié 19:00-20:30\nArquitectura Software: Mar/Jue 13:30-15:30\nTeoría Lenguajes: Jue 18:00-19:30\nEstructuras de Datos: Sáb 08:00-13:00"));
        return lista;
    }

    // --- LÓGICA DE MAPEO DE HORARIOS ---
    public List<BloqueHorario> procesarSeleccionManual(String materia, String seleccion, String color) {
        List<BloqueHorario> bloques = new ArrayList<>();
        if (seleccion.equals("— No cursar —")) return bloques;
        
        String nombreCorto = materia.split("\\(")[0].trim();
        
        if (seleccion.contains("Lun/Mié") && seleccion.contains("13:00")) { bloques.add(new BloqueHorario(nombreCorto, 1, 7, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 3, 7, 2, color)); } 
        else if (seleccion.contains("Mar/Jue") && seleccion.contains("07:00")) { bloques.add(new BloqueHorario(nombreCorto, 2, 1, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 4, 1, 2, color)); } 
        else if (seleccion.contains("Vie") && seleccion.contains("07:00")) { bloques.add(new BloqueHorario(nombreCorto, 5, 1, 4, color)); } 
        else if (seleccion.contains("Sáb")) { bloques.add(new BloqueHorario(nombreCorto, 6, 2, 5, color)); } 
        else { bloques.add(new BloqueHorario(nombreCorto, 2, 4, 2, color)); } // Fallback genérico
        
        return bloques;
    }

    public List<BloqueHorario> procesarSugerenciaIA(String detalle) {
        List<BloqueHorario> bloques = new ArrayList<>();
        if (detalle.contains("Desarrollo Web")) {
            bloques.add(new BloqueHorario("Prog. Micro", 1, 7, 2, "#4CAF50")); bloques.add(new BloqueHorario("Prog. Micro", 3, 7, 2, "#4CAF50"));
            bloques.add(new BloqueHorario("Req. Software", 2, 1, 2, "#2196F3")); bloques.add(new BloqueHorario("Req. Software", 4, 1, 2, "#2196F3"));
            bloques.add(new BloqueHorario("Administración", 1, 3, 2, "#9C27B0")); bloques.add(new BloqueHorario("Administración", 3, 3, 2, "#9C27B0"));
            bloques.add(new BloqueHorario("Desarrollo Web", 5, 1, 4, "#E53935"));
            bloques.add(new BloqueHorario("Graficación", 2, 4, 2, "#009688")); bloques.add(new BloqueHorario("Graficación", 4, 4, 2, "#009688"));
            bloques.add(new BloqueHorario("Estructuras Datos", 6, 2, 5, "#607D8B"));
        } else {
            bloques.add(new BloqueHorario("Prog. Micro", 1, 7, 2, "#4CAF50")); bloques.add(new BloqueHorario("Prog. Micro", 3, 7, 2, "#4CAF50"));
            bloques.add(new BloqueHorario("Req. Software", 1, 13, 2, "#2196F3")); bloques.add(new BloqueHorario("Req. Software", 3, 13, 2, "#2196F3"));
            bloques.add(new BloqueHorario("Arq. Software", 2, 7, 2, "#FF9800")); bloques.add(new BloqueHorario("Arq. Software", 4, 7, 2, "#FF9800"));
            bloques.add(new BloqueHorario("Teoría Lenguajes", 4, 12, 2, "#795548"));
            bloques.add(new BloqueHorario("Estructuras Datos", 6, 2, 5, "#607D8B"));
        }
        return bloques;
    }
}