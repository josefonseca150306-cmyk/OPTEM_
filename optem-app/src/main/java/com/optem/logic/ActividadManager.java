package com.optem.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.optem.models.Actividad;

public class ActividadManager {
    private static ActividadManager instance;
    private String usuarioActual;
    private List<Actividad> listaActividades;

    private ActividadManager() {
        listaActividades = new ArrayList<>();
        cargarDatosDePrueba();
    }

    public static ActividadManager getInstance() {
        if (instance == null) {
            instance = new ActividadManager();
        }
        return instance;
    }

    public void setUsuarioActual(String correoUsuario) {
        this.usuarioActual = correoUsuario;
    }

    // Generamos algunas tareas iniciales para que el tablero no se vea vacío
    private void cargarDatosDePrueba() {
        LocalDate hoy = LocalDate.now();
        listaActividades.add(new Actividad("1", "Práctica 3 de Redes", "Tarea", "Configurar VLANs en Cisco Packet Tracer.", hoy, LocalTime.of(23, 59), "REDES Y TELECOMUNICACIONES"));
        listaActividades.add(new Actividad("2", "Resumen Capítulo 4", "Estudio", "Leer el libro de Sommerville y hacer mapa conceptual.", hoy, LocalTime.of(15, 30), "ANALISIS Y DISEÑO DE SOFTWARE"));
        
        Actividad a3 = new Actividad("3", "Instalación de SQL Server", "Tarea", "Dejar configurado el entorno para la clase práctica.", hoy.minusDays(1), LocalTime.of(12, 0), "BASES DE DATOS");
        a3.setEstado("COMPLETADO");
        listaActividades.add(a3);
        
        Actividad a4 = new Actividad("4", "Diagramas de Clase JODA", "Tarea", "Terminar la documentación del compilador.", hoy, LocalTime.of(18, 0), "DESARROLLO DE SOFTWARE");
        a4.setEstado("EN_PROCESO");
        listaActividades.add(a4);
    }

    public List<Actividad> getAllActividades() {
        return listaActividades;
    }

    public List<Actividad> getActividadesDelDia(LocalDate fecha) {
        return listaActividades.stream()
                .filter(a -> a.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public void agregarActividad(Actividad nuevaActividad) {
        listaActividades.add(nuevaActividad);
    }

    public void actualizarEstadoActividad(String id, String nuevoEstado) {
        for (Actividad act : listaActividades) {
            if (act.getId().equals(id)) {
                act.setEstado(nuevoEstado);
                break;
            }
        }
    }
}