package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.AdminController;
import com.optem.controllers.DashboardController;
import com.optem.logic.NotificacionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AdminReinscripcionView {

    private final DashboardController controller = new DashboardController();
    private final AdminController adminController = new AdminController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;

    private boolean periodoAbierto = false;
    private Preferences prefsGlobales;

    public VBox getView() {
        leerPreferencias();
        
        prefsGlobales = Preferences.userRoot().node("optem_erp_global");
        periodoAbierto = prefsGlobales.getBoolean("REINSCRIPCION_ABIERTA", false);

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("📑 Centro de Reinscripciones y Oferta");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Controla la apertura del ciclo, asigna maestros a materias y visualiza la matrícula.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNotificar = new Button("📢 Notificar a Estudiantes");
        btnNotificar.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 8; -fx-text-fill: " + colorFuerte + "; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        btnNotificar.setOnAction(e -> NotificacionManager.mostrarNotificacion("Aviso Masivo", "Se ha enviado un correo y notificación a todos los estudiantes.", "INFO"));

        header.getChildren().addAll(titleBox, spacer, btnNotificar);

        // --- SECCIÓN 1: ESTADO DEL SISTEMA ---
        HBox controlBox = new HBox(20);
        controlBox.setAlignment(Pos.CENTER_LEFT);
        controlBox.setPadding(new Insets(25));
        controlBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

        VBox infoEstado = new VBox(5);
        Label lblEstadoTit = new Label("Estado Actual del Sistema:");
        lblEstadoTit.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        
        HBox estadoVisual = new HBox(10);
        estadoVisual.setAlignment(Pos.CENTER_LEFT);
        Circle dotEstado = new Circle(6, Color.web(periodoAbierto ? "#10b981" : "#ef4444"));
        Label lblEstadoVal = new Label(periodoAbierto ? "PERIODO DE REINSCRIPCIÓN: ABIERTO" : "PERIODO DE REINSCRIPCIÓN: CERRADO");
        lblEstadoVal.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + (periodoAbierto ? "#10b981" : "#ef4444") + ";");
        estadoVisual.getChildren().addAll(dotEstado, lblEstadoVal);
        
        Label lblAlerta = new Label(periodoAbierto ? "Los estudiantes ahora pueden acceder al módulo y generar sus referencias." : "El módulo de estudiantes está bloqueado. Nadie puede reinscribirse.");
        lblAlerta.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-size: 12px;");
        infoEstado.getChildren().addAll(lblEstadoTit, estadoVisual, lblAlerta);

        Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button btnToggleEstado = new Button(periodoAbierto ? "🔒 CERRAR PERIODO" : "🔓 HABILITAR REINSCRIPCIONES");
        btnToggleEstado.setStyle("-fx-background-color: " + (periodoAbierto ? "#ef4444" : "#10b981") + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 8; -fx-cursor: hand;");
        
        btnToggleEstado.setOnAction(e -> {
            periodoAbierto = !periodoAbierto;
            prefsGlobales.putBoolean("REINSCRIPCION_ABIERTA", periodoAbierto); 
            
            dotEstado.setFill(Color.web(periodoAbierto ? "#10b981" : "#ef4444"));
            lblEstadoVal.setText(periodoAbierto ? "PERIODO DE REINSCRIPCIÓN: ABIERTO" : "PERIODO DE REINSCRIPCIÓN: CERRADO");
            lblEstadoVal.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + (periodoAbierto ? "#10b981" : "#ef4444") + ";");
            lblAlerta.setText(periodoAbierto ? "Los estudiantes ahora pueden acceder al módulo y generar sus referencias." : "El módulo de estudiantes está bloqueado. Nadie puede reinscribirse.");
            
            btnToggleEstado.setText(periodoAbierto ? "🔒 CERRAR PERIODO" : "🔓 HABILITAR REINSCRIPCIONES");
            btnToggleEstado.setStyle("-fx-background-color: " + (periodoAbierto ? "#ef4444" : "#10b981") + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 25; -fx-background-radius: 8; -fx-cursor: hand;");
            
            NotificacionManager.mostrarNotificacion("Cambio de Estado", periodoAbierto ? "Se ha habilitado la reinscripción para todo el campus." : "Las reinscripciones han concluido.", "INFO");
        });
        controlBox.getChildren().addAll(infoEstado, spacer2, btnToggleEstado);

        // --- SECCIÓN 2: CREACIÓN DE GRUPOS Y ASIGNACIÓN (NUEVO) ---
        VBox creacionGruposBox = new VBox(15);
        creacionGruposBox.setPadding(new Insets(20));
        creacionGruposBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-background-radius: 12;");
        
        Label lblOfertaTit = new Label("👨‍🏫 Apertura de Grupos (Asignación de Maestros)");
        lblOfertaTit.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");

        GridPane gridOferta = new GridPane();
        gridOferta.setHgap(15); gridOferta.setVgap(15);

        TextField txtMateria = new TextField(); txtMateria.setPromptText("Ej: Arquitectura de Software");
        txtMateria.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        
        TextField txtMaestro = new TextField(); txtMaestro.setPromptText("Nombre del Profesor");
        txtMaestro.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        
        TextField txtHorario = new TextField(); txtHorario.setPromptText("Ej: Lun/Mié 09:00-11:00");
        txtHorario.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        
        TextField txtGrupo = new TextField(); txtGrupo.setPromptText("Ej: G6A");
        txtGrupo.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        gridOferta.add(crearLabelSimple("Materia a ofertar:"), 0, 0); gridOferta.add(txtMateria, 0, 1);
        gridOferta.add(crearLabelSimple("Maestro Asignado:"), 1, 0); gridOferta.add(txtMaestro, 1, 1);
        gridOferta.add(crearLabelSimple("Horario Asignado:"), 2, 0); gridOferta.add(txtHorario, 2, 1);
        gridOferta.add(crearLabelSimple("Clave de Grupo:"), 3, 0); gridOferta.add(txtGrupo, 3, 1);

        Button btnCrearGrupo = new Button("Registrar Grupo en Sistema");
        btnCrearGrupo.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCrearGrupo.setOnAction(e -> {
            if (adminController.crearGrupoOficial(txtMateria.getText(), txtMaestro.getText(), txtGrupo.getText(), txtHorario.getText())) {
                NotificacionManager.mostrarNotificacion("Grupo Registrado", "La materia ya está disponible en el MockDB para maestros y alumnos.", "INFO");
                txtMateria.clear(); txtMaestro.clear(); txtHorario.clear(); txtGrupo.clear();
            } else {
                NotificacionManager.mostrarNotificacion("Error", "Debes llenar Materia y Maestro.", "ERROR");
            }
        });

        creacionGruposBox.getChildren().addAll(lblOfertaTit, gridOferta, btnCrearGrupo);

        // --- SECCIÓN 3: PROGRESO Y CONFIGURACIÓN ---
        HBox confProgesoBox = new HBox(20);

        VBox configBox = new VBox(15);
        configBox.setPrefWidth(450);
        configBox.setPadding(new Insets(20));
        configBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-background-radius: 12;");
        
        Label lblConfTit = new Label("⚙️ Parámetros del Ciclo Escolar");
        lblConfTit.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");

        HBox fechasBox = new HBox(15);
        VBox boxInicio = crearCajaInput("Fecha de Inicio", new DatePicker());
        VBox boxFin = crearCajaInput("Fecha de Cierre Límite", new DatePicker());
        fechasBox.getChildren().addAll(boxInicio, boxFin);

        HBox datosBox = new HBox(15);
        TextField txtCiclo = new TextField("2026-B");
        txtCiclo.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-text-fill: " + colorTextMain + "; -fx-border-radius: 6;");
        VBox boxCiclo = crearCajaInputStr("Nombre del Ciclo", txtCiclo);
        
        TextField txtCuota = new TextField("$ 3,500.00 MXN");
        txtCuota.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-text-fill: " + colorTextMain + "; -fx-border-radius: 6;");
        VBox boxCuota = crearCajaInputStr("Cuota Semestral", txtCuota);
        datosBox.getChildren().addAll(boxCiclo, boxCuota);

        Button btnGuardarConf = new Button("Guardar Parámetros");
        btnGuardarConf.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
        btnGuardarConf.setMaxWidth(Double.MAX_VALUE);
        btnGuardarConf.setOnAction(e -> NotificacionManager.mostrarNotificacion("Guardado", "Parámetros actualizados correctamente.", "INFO"));

        configBox.getChildren().addAll(lblConfTit, fechasBox, datosBox, btnGuardarConf);

        VBox progresoBox = new VBox(15);
        HBox.setHgrow(progresoBox, Priority.ALWAYS);
        progresoBox.setPadding(new Insets(20));
        progresoBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-background-radius: 12;");
        
        Label lblProgTit = new Label("📊 Progreso de Matrícula (Campus)");
        lblProgTit.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");

        HBox stats = new HBox(40);
        stats.getChildren().addAll(
            crearMiniStat("Total Esperados", "1,250", colorTextMain),
            crearMiniStat("Reinscritos", "840", "#10b981"),
            crearMiniStat("Faltantes", "410", "#f59e0b")
        );

        ProgressBar pbMatricula = new ProgressBar(840.0 / 1250.0);
        pbMatricula.setMaxWidth(Double.MAX_VALUE);
        pbMatricula.setPrefHeight(15);
        pbMatricula.setStyle("-fx-accent: " + colorFuerte + "; -fx-control-inner-background: " + colorBgBase + ";");
        
        Label lblPorcentaje = new Label("67.2% de avance general");
        lblPorcentaje.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");

        progresoBox.getChildren().addAll(lblProgTit, stats, pbMatricula, lblPorcentaje);

        confProgesoBox.getChildren().addAll(configBox, progresoBox);

        layoutMain.getChildren().addAll(header, controlBox, creacionGruposBox, confProgesoBox);
        
        ScrollPane scroll = new ScrollPane(layoutMain);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        
        VBox layoutFinal = new VBox(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return layoutFinal;
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

    private Label crearLabelSimple(String texto) {
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        return lbl;
    }

    private VBox crearCajaInput(String titulo, DatePicker dp) {
        VBox box = new VBox(5);
        Label lbl = crearLabelSimple(titulo);
        dp.setStyle("-fx-control-inner-background: " + colorBgBase + "; -fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        dp.getEditor().setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + ";");
        box.getChildren().addAll(lbl, dp);
        return box;
    }

    private VBox crearCajaInputStr(String titulo, TextField tf) {
        VBox box = new VBox(5);
        Label lbl = crearLabelSimple(titulo);
        box.getChildren().addAll(lbl, tf);
        return box;
    }

    private VBox crearMiniStat(String titulo, String valor, String colorHex) {
        VBox box = new VBox(0);
        Label lblVal = new Label(valor);
        lblVal.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: " + colorHex + ";");
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");
        box.getChildren().addAll(lblVal, lblTit);
        return box;
    }
}