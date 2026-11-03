package com.optem.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Actividad {
    private String id;
    private String nombre;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime horaVencimiento;
    private String estado; // "PENDIENTE", "EN_PROCESO", "COMPLETADO"
    private String materia;

    public Actividad(String id, String nombre, String tipo, String descripcion, LocalDate fecha, LocalTime horaVencimiento, String materia) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.horaVencimiento = horaVencimiento;
        this.materia = materia;
        this.estado = "PENDIENTE"; // Por defecto, toda tarea nueva está pendiente
    }

    // --- GETTERS Y SETTERS ---
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHoraVencimiento() { return horaVencimiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMateria() { return materia; }

    // Método crucial para que el Dashboard sepa si sumar XP o no
    public boolean isHecho() {
        return "COMPLETADO".equals(estado);
    }
}