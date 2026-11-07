package com.optem.ui;

import java.time.LocalDate;
import java.util.Map;
import java.util.prefs.Preferences;

import com.optem.controllers.TareasMaestroController;
import com.optem.logic.NotificacionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TareasMaestroView {

    private final TareasMaestroController controller = new TareasMaestroController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    private VBox contenedorTarjetas;

    public VBox getView() {
        leerPreferencias();
        VBox layoutMain = new VBox(20);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        VBox titleBox = new VBox(5);
        Label titulo = new Label("📤 Panel de Asignaciones (Para Alumnos)");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Crea tareas, pon fechas límite, monitorea entregas y cierra el acceso cuando decidas.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        HBox body = new HBox(25);
        body.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(body, Priority.ALWAYS);

        // --- PANEL IZQUIERDO: CREAR TAREA ---
        VBox panelCrear = new VBox(15);
        panelCrear.setPrefWidth(400);
        panelCrear.setPadding(new Insets(25));
        panelCrear.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label lblCrearTit = new Label("Nueva Asignación");
        lblCrearTit.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + colorFuerte + ";");

        ComboBox<String> cmbMateria = new ComboBox<>();
        cmbMateria.setPromptText("Selecciona el Grupo...");
        cmbMateria.getItems().addAll(controller.getMisGruposOficiales());
        cmbMateria.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        cmbMateria.setMaxWidth(Double.MAX_VALUE);

        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Título de la tarea...");
        txtTitulo.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Instrucciones detalladas...");
        txtDesc.setPrefRowCount(4);
        txtDesc.setStyle("-fx-control-inner-background: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        HBox boxFechaHora = new HBox(10);
        DatePicker dpFecha = new DatePicker(LocalDate.now());
        dpFecha.setStyle("-fx-control-inner-background: " + colorBgBase + ";");
        dpFecha.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dpFecha, Priority.ALWAYS);

        ComboBox<String> cmbHora = new ComboBox<>();
        cmbHora.getItems().addAll("12:00 PM", "06:00 PM", "11:59 PM");
        cmbHora.setValue("11:59 PM");
        cmbHora.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + ";");
        boxFechaHora.getChildren().addAll(dpFecha, cmbHora);

        Button btnAdjuntar = new Button("📎 Adjuntar Material de Apoyo");
        btnAdjuntar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorFuerte + "; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 6; -fx-cursor: hand;");
        btnAdjuntar.setMaxWidth(Double.MAX_VALUE);

        Button btnPublicar = new Button("Publicar Asignación");
        btnPublicar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        btnPublicar.setMaxWidth(Double.MAX_VALUE);

        btnPublicar.setOnAction(e -> {
            if (cmbMateria.getValue() != null && !txtTitulo.getText().isEmpty()) {
                controller.publicarNuevaTarea(txtTitulo.getText(), cmbMateria.getValue(), txtDesc.getText(), dpFecha.getValue(), cmbHora.getValue());
                NotificacionManager.mostrarNotificacion("Tarea Publicada", "Los alumnos de " + cmbMateria.getValue() + " ya pueden ver la asignación.", "INFO");
                txtTitulo.clear(); txtDesc.clear(); cmbMateria.setValue(null);
                actualizarListaTareas();
            } else {
                NotificacionManager.mostrarNotificacion("Error", "Falta seleccionar grupo o escribir título.", "ERROR");
            }
        });

        panelCrear.getChildren().addAll(lblCrearTit, cmbMateria, txtTitulo, txtDesc, new Label("Límite de Entrega:"), boxFechaHora, btnAdjuntar, new Region(), btnPublicar);

        // --- PANEL DERECHO: TAREAS ACTIVAS ---
        VBox panelLista = new VBox(15);
        HBox.setHgrow(panelLista, Priority.ALWAYS);
        
        contenedorTarjetas = new VBox(15);
        ScrollPane scroll = new ScrollPane(contenedorTarjetas);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panelLista.getChildren().add(scroll);
        
        body.getChildren().addAll(panelCrear, panelLista);
        layoutMain.getChildren().addAll(titleBox, body);
        
        actualizarListaTareas();
        return layoutMain;
    }

    private void actualizarListaTareas() {
        contenedorTarjetas.getChildren().clear();
        for (Map<String, String> tarea : controller.getTodasLasAsignaciones()) {
            contenedorTarjetas.getChildren().add(crearTarjetaAsignacion(tarea));
        }
    }

    private VBox crearTarjetaAsignacion(Map<String, String> tarea) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label lblMat = new Label(tarea.get("materia"));
        lblMat.setStyle("-fx-background-color: " + colorFuerte + "20; -fx-text-fill: " + colorFuerte + "; -fx-font-weight: bold; -fx-padding: 4 8; -fx-background-radius: 6; -fx-font-size: 11px;");
        
        boolean estaAbierta = tarea.get("estado").equals("ABIERTA");
        Label lblEstado = new Label(estaAbierta ? "🟢 ABIERTA" : "🔴 CERRADA");
        lblEstado.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: " + (estaAbierta ? "#10b981" : "#ef4444") + ";");
        
        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        header.getChildren().addAll(lblMat, sp1, lblEstado);

        Label lblTit = new Label(tarea.get("titulo"));
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");
        
        Label lblFecha = new Label("Límite: " + tarea.get("fecha") + " a las " + tarea.get("hora"));
        lblFecha.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");

        // Progreso de entregas
        VBox boxProgreso = new VBox(5);
        double entregas = Double.parseDouble(tarea.get("entregas_actuales"));
        double total = Double.parseDouble(tarea.get("entregas_total"));
        
        HBox textProg = new HBox();
        Label lblEnt = new Label("Entregas completadas:"); lblEnt.setStyle("-fx-text-fill: " + colorTextMuted + ";");
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        Label lblNum = new Label((int)entregas + " de " + (int)total); lblNum.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        textProg.getChildren().addAll(lblEnt, sp2, lblNum);
        
        ProgressBar pb = new ProgressBar(total == 0 ? 0 : entregas / total);
        pb.setMaxWidth(Double.MAX_VALUE);
        pb.setStyle("-fx-accent: " + colorFuerte + "; -fx-control-inner-background: " + colorBgBase + ";");
        boxProgreso.getChildren().addAll(textProg, pb);

        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnVer = new Button("Ver Trabajos");
        btnVer.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorBorder + "; -fx-text-fill: " + colorTextMain + "; -fx-border-radius: 6; -fx-cursor: hand;");

        Button btnCerrar = new Button("Cerrar Recepción");
        btnCerrar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> {
            controller.cerrarRecepcionTarea(tarea.get("id"));
            actualizarListaTareas(); // Refresca la vista
            NotificacionManager.mostrarNotificacion("Asignación Cerrada", "Los alumnos ya no podrán subir archivos a esta tarea.", "INFO");
        });

        if (estaAbierta) {
            footer.getChildren().addAll(btnVer, btnCerrar);
        } else {
            footer.getChildren().add(btnVer);
            card.setOpacity(0.7); // Se ve apagada si ya se cerró
        }

        card.getChildren().addAll(header, lblTit, lblFecha, boxProgreso, footer);
        return card;
    }

    private void leerPreferencias() {
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        String correoActual = prefs.get("LAST_ACTIVE_ACCOUNT", "");
        boolean isDark = prefs.getBoolean("PREF_DARK_MODE_" + correoActual, false);
        colorFuerte = prefs.get("PREF_ACCENT_COLOR_" + correoActual, "#3D5A4D");
        colorBgBase = isDark ? "#121212" : "#f1f5f9"; colorBgPanel = isDark ? "#1e1e1e" : "#ffffff";
        colorTextMain = isDark ? "#f8f9fa" : "#1e293b"; colorTextMuted = isDark ? "#94a3b8" : "#64748b";
        colorBorder = isDark ? "#333333" : "#e2e8f0";
    }
}