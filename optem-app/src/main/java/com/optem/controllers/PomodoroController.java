package com.optem.controllers;

public class PomodoroController {

    private final int tiempoTotalSegundos = 25 * 60; // 25 minutos
    private int tiempoActualSegundos = tiempoTotalSegundos;
    private boolean enMarcha = false;

    /**
     * Alterna el estado del temporizador (En marcha / Pausado).
     * Retorna true si quedó en marcha, false si se pausó.
     */
    public boolean alternarTemporizador() {
        if (enMarcha) {
            enMarcha = false;
        } else {
            if (tiempoActualSegundos == 0) {
                resetearTemporizador(); 
            }
            enMarcha = true;
        }
        return enMarcha;
    }

    /**
     * Resetea el tiempo al valor original y lo pausa.
     */
    public void resetearTemporizador() {
        tiempoActualSegundos = tiempoTotalSegundos;
        enMarcha = false;
    }

    /**
     * Reduce un segundo al contador. 
     * Retorna true si el Pomodoro ha terminado (llegó a 0).
     */
    public boolean tick() {
        if (tiempoActualSegundos > 0) {
            tiempoActualSegundos--;
            return false;
        } else {
            enMarcha = false;
            return true; // Pomodoro completado
        }
    }

    /**
     * Devuelve el tiempo formateado en cadena "MM:SS".
     */
    public String getTiempoFormateado() {
        int minutos = tiempoActualSegundos / 60;
        int segundos = tiempoActualSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    /**
     * Calcula el ángulo de inicio para la animación de vaciado del círculo (Clockwise).
     */
    public double getAnguloInicioCirculo() {
        double porcentajeRestante = (double) tiempoActualSegundos / tiempoTotalSegundos;
        double porcentajeTranscurrido = 1.0 - porcentajeRestante;
        return 90 - (porcentajeTranscurrido * 360);
    }

    /**
     * Calcula la longitud del arco verde (Cuánto círculo queda visible).
     */
    public double getLongitudArcoCirculo() {
        double porcentajeRestante = (double) tiempoActualSegundos / tiempoTotalSegundos;
        return -(360 * porcentajeRestante);
    }

    /**
     * Retorna la lista de tips estáticos para el Pomodoro.
     */
    public String[] getTips() {
        return new String[]{
            "Elimina distracciones antes de empezar", 
            "Cada 4 pomodoros toma un descanso largo", 
            "Apunta tu progreso al terminar", 
            "Si surge algo urgente, anótalo y continúa"
        };
    }
}