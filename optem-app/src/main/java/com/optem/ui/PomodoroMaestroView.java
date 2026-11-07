package com.optem.ui;

import java.util.prefs.Preferences;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class PomodoroMaestroView {

    private String colorFuerte, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    private int tiempoRestante = 45 * 60; // 45 minutos por defecto para calificar
    private boolean enPausa = true;
    private Timeline timeline;
    private Label lblTemporizador;

    public VBox getView() {
        leerPreferencias();
        VBox layoutMain = new VBox(20);
        layoutMain.setPadding(new Insets(40));
        layoutMain.setAlignment(Pos.CENTER);
        layoutMain.setStyle("-fx-background-color: transparent;");

        Label titulo = new Label("⏱ Modo Enfoque Docente");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Recomendamos sesiones de 45 minutos para calificación continua.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");

        VBox relojBox = new VBox(15);
        relojBox.setAlignment(Pos.CENTER);
        relojBox.setMaxWidth(400);
        relojBox.setPadding(new Insets(50));
        relojBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 4; -fx-border-radius: 200; -fx-background-radius: 200; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        lblTemporizador = new Label("45:00");
        lblTemporizador.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-text-fill: " + colorFuerte + ";");
        relojBox.getChildren().add(lblTemporizador);

        HBox controles = new HBox(20);
        controles.setAlignment(Pos.CENTER);
        
        Button btnIniciar = new Button("▶ Iniciar / Pausar");
        btnIniciar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button btnReiniciar = new Button("🔄 Reiniciar");
        btnReiniciar.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorTextMuted + "; -fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold; -fx-padding: 12 25; -fx-border-radius: 8; -fx-cursor: hand;");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (tiempoRestante > 0) {
                tiempoRestante--;
                actualizarReloj();
            } else {
                timeline.stop();
                lblTemporizador.setText("¡Tiempo!");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        btnIniciar.setOnAction(e -> {
            if (enPausa) { timeline.play(); enPausa = false; }
            else { timeline.pause(); enPausa = true; }
        });

        btnReiniciar.setOnAction(e -> {
            timeline.pause(); enPausa = true;
            tiempoRestante = 45 * 60;
            actualizarReloj();
        });

        controles.getChildren().addAll(btnIniciar, btnReiniciar);
        layoutMain.getChildren().addAll(titulo, subtitulo, relojBox, controles);
        return layoutMain;
    }

    private void actualizarReloj() {
        int m = tiempoRestante / 60;
        int s = tiempoRestante % 60;
        lblTemporizador.setText(String.format("%02d:%02d", m, s));
    }

    private void leerPreferencias() {
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        String correoActual = prefs.get("LAST_ACTIVE_ACCOUNT", "");
        boolean isDark = prefs.getBoolean("PREF_DARK_MODE_" + correoActual, false);
        colorFuerte = prefs.get("PREF_ACCENT_COLOR_" + correoActual, "#3D5A4D");
        colorBgPanel = isDark ? "#1e1e1e" : "#ffffff";
        colorTextMain = isDark ? "#f8f9fa" : "#1e293b"; 
        colorTextMuted = isDark ? "#94a3b8" : "#64748b";
        colorBorder = isDark ? "#333333" : "#e2e8f0";
    }
}