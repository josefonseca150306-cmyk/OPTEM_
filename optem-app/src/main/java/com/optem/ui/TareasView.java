package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.TareasController;
import com.optem.logic.NotificacionManager;
import com.optem.models.Actividad;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class TareasView {

    private final TareasController controller = new TareasController();
    private String colorFuerte;
    private String colorTextMain, colorTextMuted, colorBgPanel, colorBgBase, colorBorder;

    private VBox colPendientes, colEnProceso, colCompletadas;
    private Stage stagePrincipal; 

    public VBox getView() {
        leerPreferenciasVisuales();

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        // --- CABECERA ---
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("Tablero de Tareas");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Organiza tus pendientes y aumenta tu XP Semanal");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNuevaTarea = new Button("➕ Crear Nueva Tarea");
        btnNuevaTarea.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
        
        VBox formNuevaTarea = crearFormularioNuevaTarea();
        btnNuevaTarea.setOnAction(e -> {
            boolean isVisible = !formNuevaTarea.isVisible();
            formNuevaTarea.setVisible(isVisible);
            formNuevaTarea.setManaged(isVisible);
            if(isVisible) {
                FadeTransition ft = new FadeTransition(Duration.millis(300), formNuevaTarea);
                ft.setFromValue(0); ft.setToValue(1); ft.play();
            }
        });

        header.getChildren().addAll(titleBox, spacer, btnNuevaTarea);

        // --- TABLERO KANBAN ---
        HBox kanbanBoard = new HBox(20);
        kanbanBoard.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(kanbanBoard, Priority.ALWAYS);

        colPendientes = crearColumnaKanban("📌 Pendientes", "#E65100");
        colEnProceso = crearColumnaKanban("⏳ En Proceso", "#0288D1");
        colCompletadas = crearColumnaKanban("✅ Completadas", "#2E7D32");

        kanbanBoard.getChildren().addAll(colPendientes, colEnProceso, colCompletadas);

        cargarTareasEnTablero();

        layoutMain.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) {
                newS.windowProperty().addListener((o, oldW, newW) -> { if(newW != null) stagePrincipal = (Stage) newW; });
            }
        });

        layoutMain.getChildren().addAll(header, formNuevaTarea, kanbanBoard);
        return layoutMain;
    }

    private void leerPreferenciasVisuales() {
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

    private VBox crearFormularioNuevaTarea() {
        VBox form = new VBox(20);
        form.setPadding(new Insets(25, 30, 25, 30));
        form.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 15; -fx-border-color: " + colorBorder + "; -fx-border-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);");
        form.setVisible(false); form.setManaged(false);

        Label lblCabecera = new Label("✍ Redactar Nueva Asignación");
        lblCabecera.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorFuerte + ";");

        GridPane grid = new GridPane(); grid.setHgap(20); grid.setVgap(15);
        
        TextField txtNombre = new TextField(); txtNombre.setPromptText("Ej. Práctica 4: Base de Datos");
        txtNombre.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        txtNombre.setPrefWidth(300);
        
        ComboBox<String> cmbMateria = new ComboBox<>();
        cmbMateria.setPromptText("Selecciona la materia...");
        cmbMateria.getItems().addAll(controller.getNombresMaterias());
        cmbMateria.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");
        cmbMateria.setPrefWidth(300);

        TextArea txtDesc = new TextArea(); txtDesc.setPromptText("Escribe los detalles, instrucciones o notas importantes...");
        txtDesc.setPrefRowCount(3);
        txtDesc.setStyle("-fx-control-inner-background: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");

        grid.add(crearLabelConIcono("Título de la tarea:", "📝"), 0, 0); grid.add(txtNombre, 0, 1);
        grid.add(crearLabelConIcono("Materia vinculada:", "📚"), 1, 0); grid.add(cmbMateria, 1, 1);
        grid.add(crearLabelConIcono("Detalles e Instrucciones:", "📋"), 0, 2, 2, 1); grid.add(txtDesc, 0, 3, 2, 1);

        HBox footerForm = new HBox(15); footerForm.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand; -fx-font-weight: bold;");
        btnCancelar.setOnAction(e -> { form.setVisible(false); form.setManaged(false); });

        Button btnGuardar = new Button("Guardar y Publicar");
        btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> {
            if(!txtNombre.getText().isEmpty() && cmbMateria.getValue() != null) {
                controller.crearNuevaTarea(txtNombre.getText(), cmbMateria.getValue(), txtDesc.getText());
                txtNombre.clear(); txtDesc.clear(); cmbMateria.setValue(null);
                form.setVisible(false); form.setManaged(false);
                cargarTareasEnTablero(); 
            }
        });

        footerForm.getChildren().addAll(btnCancelar, btnGuardar);
        form.getChildren().addAll(lblCabecera, grid, footerForm);
        return form;
    }

    private VBox crearLabelConIcono(String texto, String icono) {
        Label lbl = new Label(icono + " " + texto);
        lbl.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        VBox box = new VBox(lbl);
        return box;
    }

    private VBox crearColumnaKanban(String titulo, String colorHeader) {
        VBox columna = new VBox(15);
        HBox.setHgrow(columna, Priority.ALWAYS);
        columna.setMaxWidth(Double.MAX_VALUE);
        
        VBox contenedorTarjetas = new VBox(12);
        contenedorTarjetas.setPadding(new Insets(15));
        contenedorTarjetas.setStyle("-fx-background-color: " + colorBgBase + "; -fx-background-radius: 0 0 12 12;");
        VBox.setVgrow(contenedorTarjetas, Priority.ALWAYS);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white; -fx-background-color: " + colorHeader + "; -fx-padding: 12; -fx-background-radius: 12 12 0 0; -fx-alignment: center;");
        lblTitulo.setMaxWidth(Double.MAX_VALUE);

        ScrollPane scroll = new ScrollPane(contenedorTarjetas);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        columna.getChildren().addAll(lblTitulo, scroll);
        columna.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 2, 2);");
        
        columna.setUserData(contenedorTarjetas);
        return columna;
    }

    private void cargarTareasEnTablero() {
        ((VBox)colPendientes.getUserData()).getChildren().clear();
        ((VBox)colEnProceso.getUserData()).getChildren().clear();
        ((VBox)colCompletadas.getUserData()).getChildren().clear();

        for (Actividad act : controller.getTareasPorEstado("PENDIENTE")) {
            ((VBox)colPendientes.getUserData()).getChildren().add(crearTarjetaTarea(act, "EN_PROCESO", "Iniciar ▶", colorFuerte));
        }

        for (Actividad act : controller.getTareasPorEstado("EN_PROCESO")) {
            VBox card = crearTarjetaTarea(act, "COMPLETADO", "Finalizar ✅", "#2E7D32");
            Button btnRegresar = new Button("◀ Pausar");
            btnRegresar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand; -fx-font-size: 11px;");
            btnRegresar.setOnAction(e -> { controller.moverTarea(act, "PENDIENTE"); cargarTareasEnTablero(); e.consume(); });
            ((HBox)card.getChildren().get(2)).getChildren().add(0, btnRegresar);
            
            ((VBox)colEnProceso.getUserData()).getChildren().add(card);
        }

        for (Actividad act : controller.getTareasPorEstado("COMPLETADO")) {
            VBox card = crearTarjetaTarea(act, "PENDIENTE", "Deshacer ↺", "#E65100");
            
            if (controller.isTareaCaducada(act)) {
                card.setStyle("-fx-background-color: " + colorBgBase + "; -fx-background-radius: 10; -fx-border-color: #888888; -fx-border-radius: 10; -fx-border-width: 1; -fx-opacity: 0.7;");
                ((Label)card.getChildren().get(1)).setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #888888; -fx-strikethrough: true;");
                card.getChildren().remove(2); 
                
                Label lblCaducada = new Label("❌ Tarea Caducada");
                lblCaducada.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-font-size: 11px;");
                card.getChildren().add(lblCaducada);
            } else {
                card.setOpacity(0.6); 
            }
            ((VBox)colCompletadas.getUserData()).getChildren().add(card);
        }
    }

    private VBox crearTarjetaTarea(Actividad tarea, String estadoDestino, String textoBoton, String colorBtn) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 10; -fx-border-width: 1; -fx-cursor: hand;");

        Label lblMateria = new Label(tarea.getMateria());
        lblMateria.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + colorFuerte + "; -fx-background-color: " + colorFuerte + "20; -fx-padding: 3 8; -fx-background-radius: 10;");

        Label lblTitulo = new Label(tarea.getNombre());
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + colorTextMain + "; -fx-wrap-text: true;");

        Label lblVence = new Label("Vence a las " + tarea.getHoraVencimiento());
        lblVence.setStyle("-fx-font-size: 12px; -fx-text-fill: " + colorTextMuted + ";");

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_RIGHT);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnMover = new Button(textoBoton);
        btnMover.setStyle("-fx-background-color: " + colorBtn + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        
        btnMover.setOnAction(e -> {
            controller.moverTarea(tarea, estadoDestino);
            cargarTareasEnTablero(); 
            e.consume(); 
        });

        footer.getChildren().addAll(spacer, btnMover);
        card.getChildren().addAll(lblMateria, lblTitulo, footer);

        card.setOnMouseClicked(e -> {
            if (controller.isTareaCaducada(tarea) && tarea.getEstado().equals("COMPLETADO")) {
                NotificacionManager.mostrarNotificacion("Acceso Denegado", "Esta tarea ya caducó y ha sido cerrada por el profesor.", "URGENTE");
            } else {
                mostrarVentanaDetallesTeams(tarea);
            }
        });

        ScaleTransition stIn = new ScaleTransition(Duration.millis(150), card); stIn.setToX(1.02); stIn.setToY(1.02);
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), card); stOut.setToX(1.0); stOut.setToY(1.0);
        card.setOnMouseEntered(e -> { if(!controller.isTareaCaducada(tarea)) stIn.playFromStart(); });
        card.setOnMouseExited(e -> { if(!controller.isTareaCaducada(tarea)) stOut.playFromStart(); });

        return card;
    }

    private void mostrarVentanaDetallesTeams(Actividad tarea) {
        Stage popup = new Stage();
        if (stagePrincipal != null) popup.initOwner(stagePrincipal);
        popup.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-width: 0 0 1 0;");
        
        Button btnCerrar = new Button("‹ Volver");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorFuerte + "; -fx-cursor: hand; -fx-font-weight: bold;");
        btnCerrar.setOnAction(e -> popup.close());
        
        Region spacerHeader = new Region(); HBox.setHgrow(spacerHeader, Priority.ALWAYS);
        
        Label lblDevolucion = new Label("Estado: En curso");
        lblDevolucion.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-style: italic; -fx-font-size: 12px;");
        
        Button btnEntregar = new Button("Entregar Tarea");
        btnEntregar.setStyle("-fx-background-color: #5C5FC8; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
        btnEntregar.setOnAction(e -> {
            controller.moverTarea(tarea, "COMPLETADO");
            cargarTareasEnTablero();
            popup.close();
            NotificacionManager.mostrarNotificacion("Éxito", "Tarea enviada al profesor correctamente.", "INFO");
        });

        topBar.getChildren().addAll(btnCerrar, spacerHeader, lblDevolucion, btnEntregar);

        HBox body = new HBox(40);
        body.setPadding(new Insets(30, 40, 40, 40));

        VBox colIzq = new VBox(25);
        colIzq.setPrefWidth(450);
        
        VBox infoTitulo = new VBox(5);
        Label lblTitle = new Label(tarea.getNombre());
        lblTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        // CORRECCIÓN: Usamos getFecha() en la vista
        Label lblSubtitle = new Label("Vence el " + tarea.getFecha() + " a las " + tarea.getHoraVencimiento() + " • Se permiten varios envíos");
        lblSubtitle.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");
        infoTitulo.getChildren().addAll(lblTitle, lblSubtitle);

        VBox miTrabajoBox = new VBox(10);
        Label lblMiTrabajo = new Label("Mi trabajo");
        lblMiTrabajo.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        
        HBox archivoBox = new HBox(15);
        archivoBox.setAlignment(Pos.CENTER_LEFT);
        archivoBox.setPadding(new Insets(15));
        archivoBox.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        Label lblIconoPdf = new Label("📄"); lblIconoPdf.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 18px;");
        Label lblNombreArchivo = new Label("Mi_Practica_Terminada.pdf"); lblNombreArchivo.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-weight: bold;");
        Region spArchivo = new Region(); HBox.setHgrow(spArchivo, Priority.ALWAYS);
        Label lblPuntosExtra = new Label("•••"); lblPuntosExtra.setStyle("-fx-text-fill: " + colorTextMuted + ";");
        archivoBox.getChildren().addAll(lblIconoPdf, lblNombreArchivo, spArchivo, lblPuntosExtra);

        HBox accionesTrabajo = new HBox(20);
        Button btnAdjuntar = new Button("📎 Adjuntar"); btnAdjuntar.setStyle("-fx-background-color: transparent; -fx-text-fill: #5C5FC8; -fx-font-weight: bold; -fx-cursor: hand;");
        Button btnNuevo = new Button("➕ Nuevo"); btnNuevo.setStyle("-fx-background-color: transparent; -fx-text-fill: #5C5FC8; -fx-font-weight: bold; -fx-cursor: hand;");
        accionesTrabajo.getChildren().addAll(btnAdjuntar, btnNuevo);

        miTrabajoBox.getChildren().addAll(lblMiTrabajo, archivoBox, accionesTrabajo);

        VBox puntosBox = new VBox(5);
        Label lblPtsTitulo = new Label("Puntos"); lblPtsTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 12px;");
        Label lblPtsValor = new Label("Sin calificar / 10"); lblPtsValor.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 16px;");
        puntosBox.getChildren().addAll(lblPtsTitulo, lblPtsValor);

        colIzq.getChildren().addAll(infoTitulo, miTrabajoBox, puntosBox);

        VBox colDer = new VBox(20);
        colDer.setPrefWidth(400);

        VBox instruccionesBox = new VBox(10);
        Label lblInstTitulo = new Label("Instrucciones");
        lblInstTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 14px; -fx-border-color: " + colorBorder + "; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 5 0;");
        lblInstTitulo.setMaxWidth(Double.MAX_VALUE);
        Label lblDescReal = new Label(tarea.getDescripcion());
        lblDescReal.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-wrap-text: true; -fx-line-spacing: 5px;");
        instruccionesBox.getChildren().addAll(lblInstTitulo, lblDescReal);

        VBox refBox = new VBox(10);
        Label lblRefTitulo = new Label("Materiales de referencia");
        lblRefTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 14px; -fx-border-color: " + colorBorder + "; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 5 0;");
        lblRefTitulo.setMaxWidth(Double.MAX_VALUE);
        
        VBox listaMateriales = new VBox(8);
        listaMateriales.getChildren().addAll(
            crearArchivoReferencia("📄", "Guia_de_estudio.txt"),
            crearArchivoReferencia("📘", "Manual_de_Usuario.docx")
        );
        refBox.getChildren().addAll(lblRefTitulo, listaMateriales);

        colDer.getChildren().addAll(instruccionesBox, refBox);

        body.getChildren().addAll(colIzq, colDer);
        root.getChildren().addAll(topBar, body);

        Scene scene = new Scene(root, 950, 600);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        
        if (stagePrincipal != null) {
            popup.setX(stagePrincipal.getX() + (stagePrincipal.getWidth() - scene.getWidth()) / 2);
            popup.setY(stagePrincipal.getY() + (stagePrincipal.getHeight() - scene.getHeight()) / 2);
        }
        popup.show();
    }
    
    private HBox crearArchivoReferencia(String icono, String nombre) {
        HBox arch = new HBox(10);
        arch.setAlignment(Pos.CENTER_LEFT);
        arch.setPadding(new Insets(10));
        arch.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-cursor: hand;");
        Label lblIcon = new Label(icono);
        Label lblNom = new Label(nombre); lblNom.setStyle("-fx-text-fill: " + colorTextMain + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblPuntos = new Label("•••"); lblPuntos.setStyle("-fx-text-fill: " + colorTextMuted + ";");
        arch.getChildren().addAll(lblIcon, lblNom, sp, lblPuntos);
        
        arch.setOnMouseEntered(e -> arch.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-cursor: hand;"));
        arch.setOnMouseExited(e -> arch.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-cursor: hand;"));
        return arch;
    }
}