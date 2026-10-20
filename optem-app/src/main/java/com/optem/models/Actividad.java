package com.optem.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Actividad {
    private String nombre;
    private String materia;
    private String tipo;
    private String descripcion;
    private LocalTime horaVencimiento;
    private LocalDate fecha;
    private boolean hecho;
    private boolean asignadaPorAdmin;

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

    // Getters
    public String getNombre() { return nombre; }
    public String getMateria() { return materia; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public LocalTime getHoraVencimiento() { return horaVencimiento; }
    public LocalDate getFecha() { return fecha; }
    public boolean isHecho() { return hecho; }
    public boolean isAsignadaPorAdmin() { return asignadaPorAdmin; }
    
    // Setters
    public void setHecho(boolean hecho) { this.hecho = hecho; }
}