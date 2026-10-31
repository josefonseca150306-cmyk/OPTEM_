package com.optem.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Actividad {

    private String nombre;
    private String materia;           // <- Faltaba esta variable
    private String tipo;
    private String descripcion;
    private LocalTime horaVencimiento;
    private LocalDate fecha;
    private boolean hecho;
    private boolean asignadaPorAdmin; // <- Faltaba esta variable

    // Constructor vacío (necesario para crear tareas desde la vista)
    public Actividad() {
    }

    // Constructor de 8 parámetros (El que pide el ActividadManager)
    public Actividad(String nombre, String materia, String tipo, String descripcion, LocalTime horaVencimiento, LocalDate fecha, boolean hecho, boolean asignadaPorAdmin) {
        this.nombre = nombre;
        this.materia = materia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.horaVencimiento = horaVencimiento;
        this.fecha = fecha;
        this.hecho = hecho;
        this.asignadaPorAdmin = asignadaPorAdmin;
    }

    // ==========================================
    // GETTERS
    // ==========================================
    public String getNombre() { return nombre; }
    public String getMateria() { return materia; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public LocalTime getHoraVencimiento() { return horaVencimiento; }
    public LocalDate getFecha() { return fecha; }
    public boolean isHecho() { return hecho; }
    public boolean isAsignadaPorAdmin() { return asignadaPorAdmin; }

    // ==========================================
    // SETTERS
    // ==========================================
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setMateria(String materia) { this.materia = materia; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setHoraVencimiento(LocalTime horaVencimiento) { this.horaVencimiento = horaVencimiento; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHecho(boolean hecho) { this.hecho = hecho; }
    public void setAsignadaPorAdmin(boolean asignadaPorAdmin) { this.asignadaPorAdmin = asignadaPorAdmin; }
}