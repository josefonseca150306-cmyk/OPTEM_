package com.optem.ui;

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

    public GruposMaestroView() {
        this.setPadding(new Insets(30));
        this.setSpacing(25);
        this.setStyle("-fx-background-color: transparent;");

        // --- CABECERA ---
        Label titulo = new Label("👥 Gestión de Grupos y Clases");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: -color-text-main;");
        Label subtitulo = new Label("Administra tus asignaturas, genera códigos de acceso o inscribe alumnos manualmente.");
        subtitulo.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 14px;");

        Button btnNuevoGrupo = new Button("+ Crear Nuevo Grupo");
        btnNuevoGrupo.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
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

    private void agregarTarjetaGrupo(String materia, String horario, String codigoAcceso, int totalAlumnos) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);");

        Label lblMateria = new Label("📚 " + materia);
        lblMateria.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: -color-text-main;");
        
        Label lblInfo = new Label("Horario: " + horario + "  |  Alumnos inscritos: " + totalAlumnos);
        lblInfo.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 14px;");

        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        Label lblCodigo = new Label("Código de clase: " + codigoAcceso);
        lblCodigo.setStyle("-fx-background-color: -color-bg-base; -fx-padding: 8 15; -fx-background-radius: 8; -fx-font-weight: bold; -fx-text-fill: -color-accent; -fx-border-color: -color-border; -fx-border-radius: 8;");

        Button btnInscribirManual = new Button("👤 Añadir Alumno Manual");
        btnInscribirManual.setStyle("-fx-background-color: transparent; -fx-border-color: -color-accent; -fx-border-radius: 8; -fx-text-fill: -color-accent; -fx-font-weight: bold; -fx-padding: 8 15; -fx-cursor: hand;");
        btnInscribirManual.setOnAction(e -> abrirModalAgregarAlumnoManual(materia));

        Button btnVerAlumnos = new Button("Ver lista de alumnos");
        btnVerAlumnos.setStyle("-fx-background-color: transparent; -fx-text-fill: -color-text-muted; -fx-underline: true; -fx-cursor: hand;");

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
        root.setStyle("-fx-background-color: -color-bg-base;");

        Label lblTitle = new Label("Crear Nueva Clase");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -color-text-main;");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la materia (Ej. Álgebra Lineal)");
        txtNombre.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 10; -fx-border-color: -color-border; -fx-border-radius: 8; -fx-text-fill: -color-text-main;");

        TextField txtHorario = new TextField();
        txtHorario.setPromptText("Horario (Ej. Mar/Jue 11:00 - 13:00)");
        txtHorario.setStyle(txtNombre.getStyle());

        Button btnGuardar = new Button("Generar Clase y Código");
        btnGuardar.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
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
        root.setStyle("-fx-background-color: -color-bg-base;");

        Label lblTitle = new Label("Añadir alumno a: " + materia);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -color-text-main;");

        TextField txtBusqueda = new TextField();
        txtBusqueda.setPromptText("Número de cuenta o correo institucional...");
        txtBusqueda.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 10; -fx-border-color: -color-border; -fx-border-radius: 8; -fx-text-fill: -color-text-main;");

        Button btnBuscar = new Button("Buscar e Inscribir");
        btnBuscar.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
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