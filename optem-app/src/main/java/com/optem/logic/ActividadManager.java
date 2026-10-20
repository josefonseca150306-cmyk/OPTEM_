package com.optem.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.optem.models.Actividad;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ActividadManager {
    private static ActividadManager instance;
    private final Map<String, ObservableList<Actividad>> usuariosActividades = new HashMap<>();
    private String correoActual;

    public static ActividadManager getInstance() {
        if (instance == null) instance = new ActividadManager();
        return instance;
    }

    public void setUsuarioActual(String correo) {
        this.correoActual = correo;
        if (!usuariosActividades.containsKey(correo)) {
            ObservableList<Actividad> lista = FXCollections.observableArrayList();
            cargarHorarios(lista, correo);
            usuariosActividades.put(correo, lista);
        }
    }

    public String getCorreoActual() {
        return this.correoActual;
    }

    private void cargarHorarios(ObservableList<Actividad> lista, String correo) {
        LocalDate hoy = LocalDate.now();
        LocalDate haceDosSemanas = hoy.minusWeeks(2);
        LocalDate enDosSemanas = hoy.plusWeeks(2);

        // --- CLASE COMÚN (Se repite todos los jueves) ---
        generarClaseSemanal(lista, "Interacción Humano Computadora", "IHC", "Estudio", "Mtra. Brenda Yazmin Reza Curiel - Aula: D22", LocalTime.of(7, 0), java.time.DayOfWeek.THURSDAY, haceDosSemanas, enDosSemanas, true);

        // ====================================================================
        // HORARIO 1: JOSÉ ANTONIO FONSECA 
        // ====================================================================
        if (correo.equalsIgnoreCase("jfonsecal001@alumno.uaemex.mx")) {
            // Lunes
            generarClaseSemanal(lista, "Req. y Especificación de Software", "Software", "Estudio", "Mtro. Jose Rafael Cruz Reyes - Aula: N/A", LocalTime.of(17, 30), java.time.DayOfWeek.MONDAY, haceDosSemanas, enDosSemanas, true);
            // Martes
            generarClaseSemanal(lista, "Arquitectura de Computadoras", "Hardware", "Estudio", "Mtro. Ismael Gonzalez Del Campo - Aula F12", LocalTime.of(9, 0), java.time.DayOfWeek.TUESDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Diseño de Compiladores", "Software", "Estudio", "Mtra. Griselda Areli Matias - Aula F13", LocalTime.of(13, 0), java.time.DayOfWeek.TUESDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Inglés 7", "Idiomas", "Cultural", "Mtra. Erika Lizbeth Alanis - VIRTUAL", LocalTime.of(19, 0), java.time.DayOfWeek.TUESDAY, haceDosSemanas, enDosSemanas, true);
            // Miércoles
            generarClaseSemanal(lista, "Estructuras de Datos", "Software", "Estudio", "Mtra. Marcela Camacho Avila - Aula D22", LocalTime.of(7, 0), java.time.DayOfWeek.WEDNESDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Prog. de Microcontroladores", "Hardware", "Estudio", "Mtro. Oliver David Melgarejo - Aula: N/A", LocalTime.of(9, 30), java.time.DayOfWeek.WEDNESDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Administración", "Admin", "Estudio", "Mtro. Carlos Landeros Guzman - Aula D24", LocalTime.of(12, 30), java.time.DayOfWeek.WEDNESDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Req. y Especificación de Software", "Software", "Estudio", "Mtro. Jose Rafael Cruz Reyes - Aula: N/A", LocalTime.of(17, 30), java.time.DayOfWeek.WEDNESDAY, haceDosSemanas, enDosSemanas, true);
            // Jueves (IHC ya está)
            generarClaseSemanal(lista, "Arquitectura de Computadoras", "Hardware", "Estudio", "Mtro. Ismael Gonzalez Del Campo - Aula F12", LocalTime.of(9, 0), java.time.DayOfWeek.THURSDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Diseño de Compiladores", "Software", "Estudio", "Mtra. Griselda Areli Matias - Aula F13", LocalTime.of(13, 0), java.time.DayOfWeek.THURSDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Inglés 7", "Idiomas", "Cultural", "Mtra. Erika Lizbeth Alanis - VIRTUAL", LocalTime.of(19, 0), java.time.DayOfWeek.THURSDAY, haceDosSemanas, enDosSemanas, true);
            // Viernes
            generarClaseSemanal(lista, "Prog. de Microcontroladores", "Hardware", "Estudio", "Mtro. Oliver David Melgarejo - Aula: N/A", LocalTime.of(9, 30), java.time.DayOfWeek.FRIDAY, haceDosSemanas, enDosSemanas, true);
            // Sábado
            generarClaseSemanal(lista, "Teoría de Lenguajes", "Software", "Estudio", "Mtro. Jose Esteban Ruiz - VIRTUAL", LocalTime.of(7, 0), java.time.DayOfWeek.SATURDAY, haceDosSemanas, enDosSemanas, true);

            // TAREAS ESPECÍFICAS (Asignadas para hoy y mañana)
            lista.add(new Actividad("Avance Lexer JODA", "Software", "Tarea", "Terminar las reglas de tokens", LocalTime.of(23, 59), hoy, false, false));
            lista.add(new Actividad("Entrenamiento Americano", "Deportes", "Deporte", "Práctica en campo", LocalTime.of(18, 0), hoy, false, false));

        } else if (correo.equalsIgnoreCase("ahernandezh058@alumno.uaemex.mx")) {
            generarClaseSemanal(lista, "Sistemas Operativos", "Computación", "Estudio", "Mtro. Fernando Pérez - Aula D10", LocalTime.of(9, 0), java.time.DayOfWeek.WEDNESDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Bases de Datos", "Ingeniería", "Estudio", "Mtra. Laura Gómez - Aula F14", LocalTime.of(11, 0), java.time.DayOfWeek.WEDNESDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Redes", "Ingeniería", "Estudio", "Laboratorio Cisco", LocalTime.of(10, 0), java.time.DayOfWeek.THURSDAY, haceDosSemanas, enDosSemanas, true);
            lista.add(new Actividad("Práctica de Redes", "Ingeniería", "Tarea", "Entregar simulación Packet Tracer", LocalTime.of(23, 59), hoy, false, false));
        } else if (correo.equalsIgnoreCase("fmartinezc017@alumno.uaemex.mx")) {
            generarClaseSemanal(lista, "Derecho Civil", "Derecho", "Estudio", "Mtra. Sofia Ruiz - Aula B05", LocalTime.of(12, 0), java.time.DayOfWeek.WEDNESDAY, haceDosSemanas, enDosSemanas, true);
            generarClaseSemanal(lista, "Criminología", "Derecho", "Estudio", "Auditorio Principal", LocalTime.of(11, 0), java.time.DayOfWeek.THURSDAY, haceDosSemanas, enDosSemanas, true);
            lista.add(new Actividad("Ensayo Reformas", "Derecho", "Tarea", "Ensayo de 5 cuartillas", LocalTime.of(20, 0), hoy, false, false));
        } else {
            lista.add(new Actividad("Bienvenida a Optem", "General", "Personal", "Hola, esta es tu nueva agenda inteligente.", LocalTime.of(10, 0), hoy, false, false));
        }
    }

    private void generarClaseSemanal(ObservableList<Actividad> lista, String nombre, String mat, String tipo, String desc, LocalTime hora, java.time.DayOfWeek diaSemana, LocalDate inicio, LocalDate fin, boolean isAdmin) {
        LocalDate current = inicio.with(TemporalAdjusters.nextOrSame(diaSemana));
        while (!current.isAfter(fin)) {
            lista.add(new Actividad(nombre, mat, tipo, desc, hora, current, false, isAdmin));
            current = current.plusWeeks(1);
        }
    }

    public ObservableList<Actividad> getTodasLasActividades() { 
        return usuariosActividades.getOrDefault(correoActual, FXCollections.observableArrayList()); 
    }

    public ObservableList<Actividad> getActividadesDelDia(LocalDate fecha) {
        ObservableList<Actividad> todas = getTodasLasActividades();
        ObservableList<Actividad> delDia = FXCollections.observableArrayList();
        for (Actividad a : todas) {
            if (a.getFecha() != null && a.getFecha().equals(fecha)) {
                delDia.add(a);
            }
        }
        FXCollections.sort(delDia, Comparator.comparing(Actividad::getHoraVencimiento));
        return delDia;
    }

    public void agregarActividad(Actividad act) { getTodasLasActividades().add(act); }
    public boolean eliminarActividad(Actividad act) { return !act.isAsignadaPorAdmin() && getTodasLasActividades().remove(act); }
    public double calcularProgresoDelDia() {
        ObservableList<Actividad> deHoy = getActividadesDelDia(LocalDate.now());
        if (deHoy.isEmpty()) return 0.0;
        return (double) deHoy.stream().filter(Actividad::isHecho).count() / deHoy.size();
    }
}