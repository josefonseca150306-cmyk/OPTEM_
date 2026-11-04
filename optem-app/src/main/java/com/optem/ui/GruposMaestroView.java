package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.GruposMaestroController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GruposMaestroView extends VBox {

    private final GruposMaestroController controller = new GruposMaestroController();
    private VBox listaGrupos;
    
    // Variables para Modo Oscuro y Color de Acento
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;

    public GruposMaestroView() {
        leerPreferencias();
        this.setPadding(new Insets(30));
        this.setSpacing(25);
        this.setStyle("-fx-background-color: transparent;");

        // --- CABECERA ESTANDARIZADA ---
        Label titulo = new Label("Gestión de Grupos y Clases");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Administra tus asignaturas, genera códigos de acceso o inscribe alumnos manualmente.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");

        Button btnNuevoGrupo = new Button("➕ Crear Nuevo Grupo");
        btnNuevoGrupo.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        btnNuevoGrupo.setOnAction(e -> abrirModalCrearGrupo());

        HBox header = new HBox(15, new VBox(5, titulo, subtitulo), new Region(), btnNuevoGrupo);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        // --- LISTA DE GRUPOS ACTIVOS ---
        listaGrupos = new VBox(15);
        
        // Mocks de ejemplo (Aquí se cargarán desde la BD después)
        agregarTarjetaGrupo("Ingeniería de Software", "Lun/Mié 13:00 - 15:00", controller.generarCodigoClase(), 24);
        agregarTarjetaGrupo("Bases de Datos Avanzadas", "Mar/Jue 07:00 - 09:00", controller.generarCodigoClase(), 18);

        ScrollPane scroll = new ScrollPane(listaGrupos);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        this.getChildren().addAll(header, scroll);
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

    private void agregarTarjetaGrupo(String materia, String horario, String codigoAcceso, int totalAlumnos) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-padding: 20; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

        Label lblMateria = new Label("📚 " + materia);
        lblMateria.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + colorTextMain + ";");
        
        Label lblInfo = new Label("Horario: " + horario + "  |  Alumnos inscritos: " + totalAlumnos);
        lblInfo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");

        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        Label lblCodigo = new Label("Código de clase: " + codigoAcceso);
        lblCodigo.setStyle("-fx-background-color: " + colorBgBase + "; -fx-padding: 8 15; -fx-background-radius: 8; -fx-font-weight: bold; -fx-text-fill: " + colorFuerte + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");

        Button btnInscribirManual = new Button("👤 Añadir Alumno Manual");
        btnInscribirManual.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 8; -fx-text-fill: " + colorFuerte + "; -fx-font-weight: bold; -fx-padding: 8 15; -fx-cursor: hand;");
        btnInscribirManual.setOnAction(e -> abrirModalAgregarAlumnoManual(materia));

        Button btnVerAlumnos = new Button("Ver lista de alumnos");
        btnVerAlumnos.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-underline: true; -fx-cursor: hand;");

        actionBox.getChildren().addAll(lblCodigo, btnInscribirManual, new Region(), btnVerAlumnos);
        HBox.setHgrow(actionBox.getChildren().get(2), Priority.ALWAYS);

        card.getChildren().addAll(lblMateria, lblInfo, actionBox);
        listaGrupos.getChildren().add(card);
    }

    private void abrirModalCrearGrupo() {
        Stage modal = new Stage(); 
        modal.setTitle("Crear Nuevo Grupo");
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + colorBgBase + ";");

        Label lblTitle = new Label("Crear Nueva Clase");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la materia (Ej. Álgebra Lineal)");
        txtNombre.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-text-fill: " + colorTextMain + ";");

        TextField txtHorario = new TextField();
        txtHorario.setPromptText("Horario (Ej. Mar/Jue 11:00 - 13:00)");
        txtHorario.setStyle(txtNombre.getStyle());

        Button btnGuardar = new Button("Generar Clase y Código");
        btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        btnGuardar.setOnAction(e -> {
            boolean exito = controller.crearGrupo(txtNombre.getText(), txtHorario.getText());
            if (exito) {
                agregarTarjetaGrupo(txtNombre.getText(), txtHorario.getText(), controller.generarCodigoClase(), 0);
                modal.close();
            }
        });

        root.getChildren().addAll(lblTitle, txtNombre, txtHorario, btnGuardar);
        Scene scene = new Scene(root, 400, 300);
        if (this.getScene() != null) scene.getStylesheets().addAll(this.getScene().getStylesheets());
        modal.setScene(scene);
        modal.show();
    }

    private void abrirModalAgregarAlumnoManual(String materia) {
        Stage modal = new Stage(); 
        modal.setTitle("Añadir Alumno");
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + colorBgBase + ";");

        Label lblTitle = new Label("Añadir alumno a: " + materia);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");

        TextField txtBusqueda = new TextField();
        txtBusqueda.setPromptText("Número de cuenta o correo institucional...");
        txtBusqueda.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-text-fill: " + colorTextMain + ";");

        Button btnBuscar = new Button("Buscar e Inscribir");
        btnBuscar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        btnBuscar.setMaxWidth(Double.MAX_VALUE);

        btnBuscar.setOnAction(e -> {
            if (controller.agregarAlumnoManual(txtBusqueda.getText())) {
                modal.close();
            }
        });

        root.getChildren().addAll(lblTitle, txtBusqueda, btnBuscar);
        Scene scene = new Scene(root, 400, 250);
        if (this.getScene() != null) scene.getStylesheets().addAll(this.getScene().getStylesheets());
        modal.setScene(scene);
        modal.show();
    }
}