package com.optem.ui;

import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import com.optem.controllers.DashboardController;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ReunionesView {

    private final DashboardController controller = new DashboardController();

    public VBox getView() {
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        String correoActual = prefs.get("LAST_ACTIVE_ACCOUNT", "");
        boolean isDark = prefs.getBoolean("PREF_DARK_MODE_" + correoActual, false);
        String colorFuerte = prefs.get("PREF_ACCENT_COLOR_" + correoActual, "#3D5A4D");

        String colorBgBase = isDark ? "#121212" : "#f1f5f9";
        String colorBgPanel = isDark ? "#1e1e1e" : "#ffffff";
        String colorTextMain = isDark ? "#f8f9fa" : "#1e293b";
        String colorTextMuted = isDark ? "#94a3b8" : "#64748b";
        String colorBorder = isDark ? "#333333" : "#e2e8f0";

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        // --- CABECERA ---
        VBox titleBox = new VBox(5);
        Label titulo = new Label("🎥 Mis Reuniones Virtuales");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Enlaces directos a tus clases en línea y asesorías (Microsoft Teams)");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        // --- CONTENEDOR DE TARJETAS ---
        VBox listaReuniones = new VBox(15);
        
        List< Map < String , String >> clases = controller.getClasesRegistradas();
        boolean hayReuniones = false;

        for (Map<String, String> clase : clases) {
            String reunion = clase.get("reunion");
            if (reunion != null && !reunion.isEmpty()) {
                hayReuniones = true;
                
                HBox card = new HBox(20);
                card.setAlignment(Pos.CENTER_LEFT);
                card.setPadding(new Insets(20));
                card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

                // Icono de Teams/Video
                Circle circle = new Circle(25, Color.web("#5B5FC7", 0.15)); // Color morado Teams
                Label icon = new Label("T");
                icon.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: #5B5FC7;");
                javafx.scene.layout.StackPane iconPane = new javafx.scene.layout.StackPane(circle, icon);

                // Información de la llamada
                VBox info = new VBox(5);
                Label lblMateria = new Label(clase.get("nombre"));
                lblMateria.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");
                Label lblProfesor = new Label("Organizador: " + clase.get("profesor"));
                lblProfesor.setStyle("-fx-font-size: 13px; -fx-text-fill: " + colorTextMuted + ";");
                Label lblFecha = new Label("Programado: " + reunion);
                lblFecha.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + colorFuerte + ";");
                info.getChildren().addAll(lblMateria, lblProfesor, lblFecha);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Botón de Unirse
                Button btnUnirse = new Button("Unirse a la reunión");
                btnUnirse.setStyle("-fx-background-color: #5B5FC7; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
                btnUnirse.setOnAction(e -> {
                    btnUnirse.setText("Abriendo Teams...");
                    btnUnirse.setStyle("-fx-background-color: #494ca0; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-font-size: 14px;");
                });

                card.getChildren().addAll(iconPane, info, spacer, btnUnirse);

                // Animación hover
                ScaleTransition stIn = new ScaleTransition(Duration.millis(150), card); stIn.setToX(1.02); stIn.setToY(1.02);
                ScaleTransition stOut = new ScaleTransition(Duration.millis(150), card); stOut.setToX(1.0); stOut.setToY(1.0);
                card.setOnMouseEntered(e -> stIn.playFromStart());
                card.setOnMouseExited(e -> stOut.playFromStart());

                listaReuniones.getChildren().add(card);
            }
        }

        if (!hayReuniones) {
            Label lblVacio = new Label("No tienes ninguna reunión virtual programada por el momento.");
            lblVacio.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px; -fx-padding: 20 0;");
            listaReuniones.getChildren().add(lblVacio);
        }

        ScrollPane scroll = new ScrollPane(listaReuniones);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        layoutMain.getChildren().addAll(titleBox, scroll);
        return layoutMain;
    }
}