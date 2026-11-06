package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.DashboardController;
import com.optem.logic.NotificacionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AdminEstadisticasView {

    private final DashboardController controller = new DashboardController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;

    public VBox getView() {
        leerPreferencias();

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("📊 Estadísticas y Tráfico de Servidores");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Monitoreo en tiempo real del uso de Optem y carga de Base de Datos.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnRespaldo = new Button("💾 Ejecutar Respaldo Manual");
        btnRespaldo.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnRespaldo.setOnAction(e -> NotificacionManager.mostrarNotificacion("Backup SQL", "Iniciando volcado de Base de Datos OptemDB...", "INFO"));

        header.getChildren().addAll(titleBox, spacer, btnRespaldo);

        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-padding: 12 20; -fx-background-radius: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");
        
        Circle dotStatus = new Circle(5, Color.web("#10b981"));
        Label lblStatus = new Label("Estado: Óptimo  |  Latencia: 24ms  |  MySQL: Conectada y Sincronizada");
        lblStatus.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-weight: bold;");
        statusBox.getChildren().addAll(dotStatus, lblStatus);

        HBox kpiBox = new HBox(20);
        kpiBox.getChildren().addAll(
            crearTarjetaKPI("👥 Usuarios Activos", "1,240", "Estudiantes y Maestros", "#3b82f6"), // Azul
            crearTarjetaKPI("💻 Carga de CPU", "45%", "Servidor Optem-Core", colorFuerte),
            crearTarjetaKPI("🗄️ Memoria RAM", "12.4 GB", "Usado de 32 GB", "#f59e0b"), // Naranja
            crearTarjetaKPI("🔄 Consultas BD", "840/s", "Picos estables", "#8b5cf6") // Morado
        );

        VBox chartBox = new VBox(15);
        chartBox.setPadding(new Insets(20));
        chartBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");
        
        Label lblChartTit = new Label("Tráfico de Servidores (Últimas 24h)");
        lblChartTit.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");

        NumberAxis xAxis = new NumberAxis(0, 24, 2);
        xAxis.setLabel("Hora del Día (24 hrs)");
        NumberAxis yAxis = new NumberAxis(0, 2000, 250);
        yAxis.setLabel("Usuarios Conectados");
        
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("");
        lineChart.setPrefHeight(350);
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Tráfico de Hoy");
        series.getData().add(new XYChart.Data<>(7, 1200));
        series.getData().add(new XYChart.Data<>(9, 1500));
        series.getData().add(new XYChart.Data<>(12, 800));
        series.getData().add(new XYChart.Data<>(14, 1800));
        series.getData().add(new XYChart.Data<>(20, 400));
        
        lineChart.getData().add(series);

        String chartCss = "CHART_COLOR_1: " + colorFuerte + "; " +
                          "-fx-background-color: transparent; " +
                          "-fx-text-fill: " + colorTextMain + ";";
        lineChart.setStyle(chartCss);
        
        chartBox.getChildren().addAll(lblChartTit, lineChart);

        VBox logBox = new VBox(10);
        Label lblLogTit = new Label("Terminal de Registros (System Logs)");
        lblLogTit.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        
        TextArea txtLogs = new TextArea();
        txtLogs.setEditable(false);
        txtLogs.setPrefRowCount(6);
        String logStyle = "-fx-control-inner-background: #0f172a; -fx-background-color: #0f172a; -fx-text-fill: #10b981; -fx-font-family: 'Consolas'; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;";
        txtLogs.setStyle(logStyle);
        txtLogs.setText("[02:45:10] INFO: Sistema iniciado correctamente.\n" +
                        "[02:45:12] DB_CONN: Conexión exitosa a MySQL jdbc:mysql://localhost:3306/optem_db.\n" +
                        "[02:50:01] AUTH: Inicio de sesión exitoso - Administrador (jfonsecal001@alumno.uaemex.mx).\n" +
                        "[02:52:15] WARN: Pico de tráfico detectado en módulo de Tareas. Asignando más memoria RAM...");

        logBox.getChildren().addAll(lblLogTit, txtLogs);

        layoutMain.getChildren().addAll(header, statusBox, kpiBox, chartBox, logBox);
        
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
        colorBgBase = isDark ? "#121212" : "#f1f5f9";
        colorBgPanel = isDark ? "#1e1e1e" : "#ffffff";
        colorTextMain = isDark ? "#f8f9fa" : "#1e293b";
        colorTextMuted = isDark ? "#94a3b8" : "#64748b";
        colorBorder = isDark ? "#333333" : "#e2e8f0";
    }

    private VBox crearTarjetaKPI(String titulo, String valor, String subtitulo, String colorHex) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(20));
        box.setPrefWidth(250);
        box.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 8, 0, 2, 2);");
        
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        Label lblVal = new Label(valor);
        lblVal.setStyle("-fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: " + colorHex + ";");
        
        Label lblSub = new Label(subtitulo);
        lblSub.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 11px;");
        
        box.getChildren().addAll(lblTit, lblVal, lblSub);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }
}