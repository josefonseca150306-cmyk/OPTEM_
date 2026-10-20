package com.optem.controllers;

import com.optem.logic.ActividadManager;
import java.time.LocalDate;

public class LogrosController {

    /**
     * Obtiene la cantidad de actividades completadas hoy.
     */
    public long getActividadesHechasHoy() {
        return ActividadManager.getInstance().getActividadesDelDia(LocalDate.now())
                .stream()
                .filter(a -> a.isHecho())
                .count();
    }

    /**
     * Obtiene el total de actividades agendadas para hoy.
     */
    public long getTotalActividadesHoy() {
        return ActividadManager.getInstance().getActividadesDelDia(LocalDate.now()).size();
    }

    /**
     * Calcula los XP actuales del usuario.
     */
    public int calcularXpActual() {
        return (int) getActividadesHechasHoy() * 100;
    }

    /**
     * Calcula la meta de XP para el día de hoy. Mínimo 100 XP.
     */
    public int calcularXpMeta() {
        long totales = getTotalActividadesHoy();
        return totales == 0 ? 100 : (int) totales * 100;
    }

    /**
     * Calcula la racha actual del usuario en días.
     */
    public int calcularDiasRacha(int xpActual, int xpMeta) {
        return (xpActual == xpMeta && xpMeta > 0 && xpActual > 0) ? 1 : 0;
    }

    /**
     * Calcula el bono multiplicador de XP según la racha.
     */
    public double calcularBonoRacha(int diasRacha) {
        return 1.0; // Aquí en el futuro puedes hacer que a los 5 días de racha el bono sea 1.1, etc.
    }

    /**
     * Determina el nivel del usuario según sus puntos XP.
     */
    public String obtenerNivelActual(int xpActual) {
        if (xpActual >= 6000) return "Experto";
        if (xpActual >= 3000) return "Avanzado";
        if (xpActual >= 1000) return "Intermedio";
        return "Novato";
    }
}