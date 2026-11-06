package com.optem.controllers;

import java.time.LocalDate;

import com.optem.logic.ActividadManager;

public class LogrosController {

    public long getActividadesHechasHoy() {
        return ActividadManager.getInstance().getActividadesDelDia(LocalDate.now())
                .stream()
                .filter(a -> a.getEstado().equals("COMPLETADO"))
                .count();
    }
    
    public long getActividadesPendientes() {
        return ActividadManager.getInstance().getAllActividades()
                .stream()
                .filter(a -> a.getEstado().equals("PENDIENTE"))
                .count();
    }

    public long getTotalActividadesHoy() {
        return ActividadManager.getInstance().getActividadesDelDia(LocalDate.now()).size();
    }

    public int calcularXpActual() {
        return (int) getActividadesHechasHoy() * 100;
    }

    public int calcularXpMeta() {
        long totales = getTotalActividadesHoy();
        return totales == 0 ? 100 : (int) totales * 100;
    }

    public int calcularDiasRacha(int xpActual, int xpMeta) {
        return (xpActual >= xpMeta && xpMeta > 0 && xpActual > 0) ? 1 : 0;
    }

    public double calcularBonoRacha(int diasRacha) {
        if (diasRacha >= 5) return 1.5;
        if (diasRacha >= 3) return 1.2;
        return 1.0; 
    }

    public String obtenerNivelActual(int xpActual) {
        if (xpActual >= 600) return "Experto";
        if (xpActual >= 300) return "Avanzado";
        if (xpActual >= 100) return "Intermedio";
        return "Novato";
    }
    
    public boolean tieneMedallaPrimeraSangre() {
        return getActividadesHechasHoy() >= 1;
    }
    
    public boolean tieneMedallaImparable() {
        return getActividadesHechasHoy() >= 3;
    }
    
    public boolean tieneMedallaSobreviviente() {
        return getActividadesPendientes() >= 5;
    }
}