package com.optem.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.optem.logic.ActividadManager;
import com.optem.logic.NotificacionManager;
import com.optem.models.Actividad;

public class PomodoroController {

    private int tiempoTotalSegundos = 25 * 60; 
    private int tiempoActualSegundos = tiempoTotalSegundos;
    private boolean enMarcha = false;
    private boolean esDescanso = false;
    private Actividad tareaSeleccionada = null;

    public void setModo(boolean descanso) {
        this.esDescanso = descanso;
        this.tiempoTotalSegundos = descanso ? 5 * 60 : 25 * 60;
        resetearTemporizador();
    }
    
    public boolean isDescanso() {
        return esDescanso;
    }

    public void setTareaSeleccionada(Actividad tarea) {
        this.tareaSeleccionada = tarea;
    }

    public List<Actividad> getTareasPendientes() {
        return ActividadManager.getInstance().getAllActividades().stream()
                .filter(a -> a.getEstado().equals("PENDIENTE") || a.getEstado().equals("EN_PROCESO"))
                .collect(Collectors.toList());
    }

    public boolean alternarTemporizador() {
        if (enMarcha) {
            enMarcha = false;
        } else {
            if (tiempoActualSegundos <= 0) resetearTemporizador(); 
            enMarcha = true;
        }
        return enMarcha;
    }

    public void resetearTemporizador() {
        tiempoActualSegundos = tiempoTotalSegundos;
        enMarcha = false;
    }

    public boolean tick() {
        if (tiempoActualSegundos > 0) {
            tiempoActualSegundos--;
            return false;
        } else {
            enMarcha = false;
            finalizarPomodoro();
            return true; 
        }
    }
    
    private void finalizarPomodoro() {
        if (!esDescanso) {
            if (tareaSeleccionada != null) {
                ActividadManager.getInstance().actualizarEstadoActividad(tareaSeleccionada.getId(), "COMPLETADO");
                NotificacionManager.mostrarNotificacion("🍅 Pomodoro Terminado", "¡Genial! Has completado la tarea: " + tareaSeleccionada.getNombre() + " (+50 XP)", "INFO");
            } else {
                NotificacionManager.mostrarNotificacion("🍅 Pomodoro Terminado", "Ciclo de estudio libre finalizado. ¡Buen trabajo!", "INFO");
            }
        } else {
            NotificacionManager.mostrarNotificacion("☕ Descanso Terminado", "El tiempo de receso acabó. Es hora de volver al enfoque.", "INFO");
        }
    }

    public String getTiempoFormateado() {
        int minutos = tiempoActualSegundos / 60;
        int segundos = tiempoActualSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
}