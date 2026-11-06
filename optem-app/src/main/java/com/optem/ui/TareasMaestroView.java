package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.AjustesController;
import com.optem.controllers.DashboardController;
import com.optem.logic.NotificacionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class TareasMaestroView {

    private final DashboardController controller = new DashboardController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    
    private VBox contenedorTareas;
    private Button btnActivas;
    private Button btnCerradas;

    public VBox getView() {
        leerPreferencias();

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("✏️ Gestor de Evaluaciones");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Crea asignaciones, revisa entregas y califica a tus grupos.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNuevaTarea = new Button("➕ Crear Asignación");
        btnNuevaTarea.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnNuevaTarea.setOnAction(e -> abrirModalCrearTarea(layoutMain.getScene().getWindow()));

        header.getChildren().addAll(titleBox, spacer, btnNuevaTarea);

        HBox filtrosBox = new HBox(15);
        filtrosBox.setAlignment(Pos.CENTER_LEFT);
        
        btnActivas = new Button("Activas / Por Calificar");
        btnCerradas = new Button("Cerradas / Calificadas");
        
        btnActivas.setOnAction(e -> cargarTareasActivas());
        btnCerradas.setOnAction(e -> cargarTareasCerradas());
        
        filtrosBox.getChildren().addAll(btnActivas, btnCerradas);
        contenedorTareas = new VBox(15);
        ScrollPane scroll = new ScrollPane(contenedorTareas);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        cargarTareasActivas();

        layoutMain.getChildren().addAll(header, filtrosBox, scroll);
        return layoutMain;
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

    private void actualizarEstiloFiltros(Button activo, Button inactivo) {
        activo.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-text-fill: " + colorFuerte + "; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 20; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 20; -fx-cursor: hand;");
        inactivo.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 20; -fx-border-color: transparent; -fx-cursor: hand;");
    }

    private void cargarTareasActivas() {
        actualizarEstiloFiltros(btnActivas, btnCerradas);
        contenedorTareas.getChildren().clear();
        contenedorTareas.getChildren().addAll(
            crearTarjetaTarea("Práctica 3 - Diagramas UML", "Ingeniería de Software (S6)", "Vence hoy a las 23:59", 20, 24, false),
            crearTarjetaTarea("Investigación: Patrones de Diseño", "Ingeniería de Software (S6)", "Venció ayer", 24, 24, false),
            crearTarjetaTarea("Ejercicios SQL - Joins", "Bases de Datos Avanzadas (S5)", "Vence el 05/11/2026", 5, 18, false)
        );
    }

    private void cargarTareasCerradas() {
        actualizarEstiloFiltros(btnCerradas, btnActivas);
        contenedorTareas.getChildren().clear();
        contenedorTareas.getChildren().addAll(
            crearTarjetaTarea("Práctica 1 - Conceptos Básicos", "Ingeniería de Software (S6)", "Calificada el 20/09", 24, 24, true),
            crearTarjetaTarea("Diseño de Base de Datos", "Bases de Datos Avanzadas (S5)", "Calificada el 15/09", 18, 18, true),
            crearTarjetaTarea("Ensayo Inicial", "Desarrollo Web Integrado (S6)", "Calificada el 05/09", 22, 22, true)
        );
    }

    private HBox crearTarjetaTarea(String titulo, String materia, String vencimiento, int entregados, int total, boolean esCerrada) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

        VBox info = new VBox(5);
        info.setPrefWidth(250);
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 1.1em; -fx-text-fill: " + colorTextMain + ";");
        Label lblMat = new Label("📚 " + materia);
        lblMat.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 0.9em;");
        Label lblVen = new Label((esCerrada ? "✅ " : "⏳ ") + vencimiento);
        lblVen.setStyle("-fx-text-fill: " + (esCerrada ? "#10b981" : colorFuerte) + "; -fx-font-weight: bold; -fx-font-size: 0.85em;");
        info.getChildren().addAll(lblTit, lblMat, lblVen);

        VBox progreso = new VBox(5);
        progreso.setAlignment(Pos.CENTER);
        progreso.setPrefWidth(200);
        Label lblProg = new Label(entregados + " de " + total + " entregas");
        lblProg.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-weight: bold; -fx-font-size: 0.9em;");
        ProgressBar pb = new ProgressBar((double) entregados / total);
        pb.setPrefWidth(180);
        pb.setStyle("-fx-accent: " + (entregados == total ? "#10b981" : colorFuerte) + "; -fx-control-inner-background: " + colorBgBase + ";");
        progreso.getChildren().addAll(lblProg, pb);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAccion = new Button(esCerrada ? "Ver Calificaciones ➔" : "Revisar Entregas ➔");
        String colorBoton = esCerrada ? colorTextMuted : colorFuerte;
        btnAccion.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorBoton + "; -fx-font-weight: bold; -fx-padding: 10 20; -fx-border-color: " + colorBoton + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        btnAccion.setOnAction(e -> abrirModalCalificar(titulo, materia, card.getScene().getWindow(), esCerrada));

        card.getChildren().addAll(info, progreso, spacer, btnAccion);
        
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBoton + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 2, 2);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);"));

        return card;
    }

    private void abrirModalCrearTarea(Window owner) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        String fontSize = new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal").equals("Pequeño") ? "12px" : new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal").equals("Grande") ? "17px" : "14px";
        layout.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: " + fontSize + ";");

        Label lblTitulo = new Label("Crear Nueva Asignación");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 1.5em; -fx-text-fill: " + colorTextMain + ";");

        ComboBox<String> cbGrupos = new ComboBox<>();
        cbGrupos.getItems().addAll("Ingeniería de Software (S6)", "Bases de Datos Avanzadas (S5)", "Desarrollo Web Integrado (S6)");
        cbGrupos.setPromptText("Selecciona el Grupo...");
        cbGrupos.setMaxWidth(Double.MAX_VALUE);
        cbGrupos.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Título de la tarea");
        txtNombre.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Instrucciones para los alumnos...");
        txtDesc.setPrefRowCount(3);
        txtDesc.setStyle("-fx-control-inner-background: " + colorBgBase + "; -fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        HBox fechaCaja = new HBox(15);
        DatePicker dpFecha = new DatePicker();
        dpFecha.setPromptText("Fecha Límite");
        dpFecha.setStyle("-fx-control-inner-background: " + colorBgBase + "; -fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        dpFecha.getEditor().setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + ";");
        
        TextField txtPuntos = new TextField();
        txtPuntos.setPromptText("Puntos (ej. 100)");
        txtPuntos.setStyle(txtNombre.getStyle());
        fechaCaja.getChildren().addAll(dpFecha, txtPuntos);

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> modal.close());

        Button btnGuardar = new Button("Asignar a Alumnos");
        btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> {
            NotificacionManager.mostrarNotificacion("Tarea Asignada", "La tarea '" + txtNombre.getText() + "' se asignó al grupo. Su cierre se registró automáticamente en tu Agenda.", "INFO");
            modal.close();
            cargarTareasActivas(); 
        });

        botones.getChildren().addAll(btnCancelar, btnGuardar);
        layout.getChildren().addAll(lblTitulo, cbGrupos, txtNombre, txtDesc, fechaCaja, botones);

        Scene scene = new Scene(layout, 450, 500);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.show();
        modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2);
        modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2);
    }

    private void abrirModalCalificar(String tarea, String materia, Window owner, boolean modoCerrado) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        String fontSize = new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal").equals("Pequeño") ? "12px" : new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal").equals("Grande") ? "17px" : "14px";
        layout.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + (modoCerrado ? colorTextMuted : colorFuerte) + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: " + fontSize + ";");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titulos = new VBox(2);
        Label lblTit = new Label((modoCerrado ? "Visualizando: " : "Calificando: ") + tarea);
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 1.3em; -fx-text-fill: " + colorTextMain + ";");
        Label lblSub = new Label(materia);
        lblSub.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 0.9em;");
        titulos.getChildren().addAll(lblTit, lblSub);
        
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnCerrar = new Button("✕");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 18px; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> modal.close());
        header.getChildren().addAll(titulos, spacer, btnCerrar);

        VBox listaAlumnos = new VBox(15);
        
        if (modoCerrado) {
            listaAlumnos.getChildren().addAll(
                crearFilaCalificacion("Jose Antonio Fonseca", "Calificado", "UML_Fonseca.pdf", "10", modoCerrado),
                crearFilaCalificacion("Daniela Sanchez", "Calificado", "Practica3_Dani.zip", "8", modoCerrado),
                crearFilaCalificacion("Alexis Hernandez", "Sin entregar", null, "0", modoCerrado)
            );
        } else {
            listaAlumnos.getChildren().addAll(
                crearFilaCalificacion("Jose Antonio Fonseca", "Entregado a tiempo", "UML_Fonseca.pdf", "", modoCerrado),
                crearFilaCalificacion("Daniela Sanchez", "Entregado con retraso", "Practica3_Dani.zip", "", modoCerrado),
                crearFilaCalificacion("Alexis Hernandez", "Sin entregar", null, "", modoCerrado)
            );
        }

        ScrollPane scroll = new ScrollPane(listaAlumnos);
        scroll.setPrefHeight(350);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        layout.getChildren().addAll(header, scroll);

        Scene scene = new Scene(layout, 650, 500);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.show();
        modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2);
        modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2);
    }

    private HBox crearFilaCalificacion(String nombre, String estatus, String archivoAdjunto, String calificacionPrevia, boolean modoCerrado) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: " + colorBgBase + "; -fx-background-radius: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");

        VBox info = new VBox(5);
        info.setPrefWidth(200);
        Label lblNom = new Label("👤 " + nombre);
        lblNom.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 1em;");
        
        Label lblEst = new Label(estatus);
        lblEst.setStyle("-fx-text-fill: " + (estatus.contains("tiempo") || estatus.contains("Calificado") ? "#10b981" : estatus.contains("retraso") ? "#f59e0b" : "#D32F2F") + "; -fx-font-size: 0.8em; -fx-font-weight: bold;");
        info.getChildren().addAll(lblNom, lblEst);

        VBox fileBox = new VBox();
        fileBox.setAlignment(Pos.CENTER_LEFT);
        fileBox.setPrefWidth(150);
        if (archivoAdjunto != null) {
            Button btnFile = new Button("📎 " + archivoAdjunto);
            btnFile.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-text-fill: " + colorTextMain + "; -fx-cursor: hand; -fx-font-size: 0.85em;");
            fileBox.getChildren().add(btnFile);
        } else {
            Label lblNoFile = new Label("Sin archivos");
            lblNoFile.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 0.85em;");
            fileBox.getChildren().add(lblNoFile);
        }

        TextField txtCalifa = new TextField(calificacionPrevia);
        txtCalifa.setPromptText("/10");
        txtCalifa.setPrefWidth(50);
        txtCalifa.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 4; -fx-text-fill: " + colorTextMain + ";");
        if(archivoAdjunto == null || modoCerrado) txtCalifa.setDisable(true);

        TextField txtFeedback = new TextField(modoCerrado && archivoAdjunto != null ? "Buen trabajo." : "");
        txtFeedback.setPromptText("Comentario...");
        txtFeedback.setPrefWidth(120);
        txtFeedback.setStyle(txtCalifa.getStyle());
        if(archivoAdjunto == null || modoCerrado) txtFeedback.setDisable(true);

        Button btnSave = new Button(modoCerrado ? "Bloqueado" : "✔");
        if (modoCerrado || archivoAdjunto == null) {
            btnSave.setDisable(true);
            btnSave.setStyle("-fx-background-color: " + colorTextMuted + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;");
        } else {
            btnSave.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
            btnSave.setOnAction(e -> {
                NotificacionManager.mostrarNotificacion("Evaluación Completada", "Calificación enviada a " + nombre + ". ¡Has ganado +15 XP por tu revisión!", "INFO");
                btnSave.setText("Edit");
                btnSave.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4;");
            });
        }

        row.getChildren().addAll(info, fileBox, txtCalifa, txtFeedback, btnSave);
        return row;
    }
}