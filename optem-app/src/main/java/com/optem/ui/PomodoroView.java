package com.optem.ui;

import com.optem.controllers.PomodoroController;
import com.optem.models.Actividad;
import java.util.prefs.Preferences;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class PomodoroView extends BorderPane {

    private final PomodoroController controller = new PomodoroController();
    private Timeline timeline;
    private ScaleTransition animacionLatido;
    private RotateTransition animacionRotacion;
    
    private Label lblReloj;
    private Group grupoSegmentos;
    private Line[] segmentosReloj;
    
    private Button btnMainAction;
    private Button btnModoEnfoque, btnModoDescanso;
    private ComboBox<Actividad> cmbTareas;
    private Button btnVerDetalles; 

    private String colorFuerteGlobal;
    private String colorRelojActual;
    private String colorBgBase, colorBgPanel, colorBorder, colorTextMain, colorTextMuted;
    
    public PomodoroView() {
        leerPreferencias();
        colorRelojActual = colorFuerteGlobal; 
        
        this.setPadding(new Insets(30, 40, 40, 40));
        this.setStyle("-fx-background-color: transparent;");

        // ==========================================
        // 1. TOP BAR: MENÚ DE MODOS 
        // ==========================================
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        HBox panelModos = new HBox(0); 
        btnModoEnfoque = new Button("🍅 Enfoque");
        btnModoDescanso = new Button("☕ Descanso");
        btnModoEnfoque.getStyleClass().add("lbl-text-pequeno");
        btnModoDescanso.getStyleClass().add("lbl-text-pequeno");
        
        panelModos.getChildren().addAll(btnModoEnfoque, btnModoDescanso);
        topBar.getChildren().add(panelModos);
        this.setTop(topBar);

        // ==========================================
        // 2. CENTRO: TARJETA PREMIUM
        // ==========================================
        VBox cardCentral = new VBox(25);
        cardCentral.setAlignment(Pos.CENTER);
        cardCentral.setPadding(new Insets(35, 50, 35, 50));
        cardCentral.setMaxWidth(600);
        cardCentral.setMaxHeight(650);
        cardCentral.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 20; -fx-border-color: -color-border; -fx-border-radius: 20; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 20, 0, 0, 5);");

        // --- CABECERA ---
        VBox header = new VBox(8); header.setAlignment(Pos.CENTER);
        Label lblTitulo = new Label("Temporizador de Estudio");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;"); 
        lblTitulo.getStyleClass().add("lbl-titulo-banner"); 
        
        Label lblSub = new Label("Selecciona una tarea pendiente y comienza a concentrarte.");
        lblSub.setStyle("-fx-text-fill: -color-text-muted;");
        lblSub.getStyleClass().add("lbl-text-normal");
        header.getChildren().addAll(lblTitulo, lblSub);

        // --- SELECTOR DE TAREAS Y BOTÓN DE DETALLES ---
        HBox panelSelector = new HBox(15);
        panelSelector.setAlignment(Pos.CENTER);

        cmbTareas = new ComboBox<>();
        cmbTareas.setPromptText("📌 Selecciona una tarea...");
        cmbTareas.getItems().addAll(controller.getTareasPendientes());
        
        cmbTareas.setStyle("-fx-background-color: " + colorBgBase + "; " +
                           "-fx-border-color: " + colorFuerteGlobal + "; " +
                           "-fx-border-width: 2px; " +
                           "-fx-border-radius: 25; " +
                           "-fx-background-radius: 25; " +
                           "-fx-padding: 8 20; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");
        cmbTareas.setPrefWidth(350);
        
        cmbTareas.setCellFactory(lv -> new ListCell<Actividad>() {
            @Override
            protected void updateItem(Actividad act, boolean empty) {
                super.updateItem(act, empty);
                if (empty || act == null) { setText(null); setStyle("-fx-background-color: -color-bg-panel;"); } 
                else { setText("📌 " + act.getNombre()); setStyle("-fx-background-color: -color-bg-panel; -fx-text-fill: -color-text-main; -fx-font-weight: bold;"); }
            }
        });
        cmbTareas.setButtonCell(new ListCell<Actividad>() {
            @Override
            protected void updateItem(Actividad act, boolean empty) {
                super.updateItem(act, empty);
                if (empty || act == null) { setText(null); setStyle("-fx-text-fill: -color-text-main;"); } 
                else { setText("🎯 " + act.getNombre()); setStyle("-fx-text-fill: -color-text-main; -fx-font-weight: bold;"); }
            }
        });

        btnVerDetalles = new Button("👁 Detalles");
        btnVerDetalles.setStyle("-fx-background-color: transparent; -fx-border-color: -color-border; -fx-border-radius: 20; -fx-text-fill: -color-text-muted; -fx-padding: 10 15; -fx-cursor: hand; -fx-font-weight: bold;");
        btnVerDetalles.setVisible(false);
        btnVerDetalles.setManaged(false); 
        
        btnVerDetalles.setOnMouseEntered(e -> btnVerDetalles.setStyle("-fx-background-color: -color-bg-base; -fx-border-color: " + colorFuerteGlobal + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: -color-text-main; -fx-padding: 10 15; -fx-cursor: hand; -fx-font-weight: bold;"));
        btnVerDetalles.setOnMouseExited(e -> btnVerDetalles.setStyle("-fx-background-color: transparent; -fx-border-color: -color-border; -fx-border-radius: 20; -fx-text-fill: -color-text-muted; -fx-padding: 10 15; -fx-cursor: hand; -fx-font-weight: bold;"));

        cmbTareas.setOnAction(e -> {
            Actividad seleccion = cmbTareas.getValue();
            controller.setTareaSeleccionada(seleccion);
            if (seleccion != null) {
                btnVerDetalles.setVisible(true);
                btnVerDetalles.setManaged(true);
            }
        });

        // CONEXIÓN CORREGIDA: Ahora abre el Popup directo desde el Pomodoro
        btnVerDetalles.setOnAction(e -> {
            Actividad act = cmbTareas.getValue();
            if (act != null) {
                mostrarDetallesTarea(act);
            }
        });

        panelSelector.getChildren().addAll(cmbTareas, btnVerDetalles);

        // --- RELOJ RADIAL ANIMADO ---
        StackPane relojUI = crearRelojRadial();

        // --- PALETA DE COLORES DEL RELOJ ---
        HBox panelColores = crearPaletaColoresReloj();

        // --- CONTROLES PRINCIPALES ---
        HBox panelControles = new HBox(20); panelControles.setAlignment(Pos.CENTER);
        
        btnMainAction = new Button("▶ Iniciar");
        btnMainAction.setStyle("-fx-background-color: " + colorFuerteGlobal + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 25; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);");
        btnMainAction.getStyleClass().add("lbl-text-normal");
        
        Button btnReset = new Button("↺ Reiniciar");
        btnReset.setStyle("-fx-background-color: transparent; -fx-text-fill: -color-text-muted; -fx-border-color: -color-border; -fx-border-radius: 25; -fx-padding: 12 30; -fx-cursor: hand;");
        btnReset.getStyleClass().add("lbl-text-normal");

        panelControles.getChildren().addAll(btnMainAction, btnReset);

        cardCentral.getChildren().addAll(header, panelSelector, relojUI, panelColores, panelControles);
        this.setCenter(cardCentral);

        // ==========================================
        // 3. EVENTOS Y ANIMACIÓN
        // ==========================================
        btnModoEnfoque.setOnAction(e -> seleccionarModo(true));
        btnModoDescanso.setOnAction(e -> seleccionarModo(false));
        
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            boolean termino = controller.tick();
            actualizarTextoReloj();
            if(termino) {
                detenerReloj();
                cmbTareas.getItems().setAll(controller.getTareasPendientes());
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);

        btnMainAction.setOnAction(e -> {
            boolean enMarcha = controller.alternarTemporizador();
            if (enMarcha) {
                timeline.play();
                animacionLatido.play();
                btnMainAction.setText("⏸ Pausar");
            } else {
                detenerReloj();
                btnMainAction.setText("▶ Reanudar");
            }
        });

        btnReset.setOnAction(e -> {
            detenerReloj();
            controller.resetearTemporizador();
            btnMainAction.setText("▶ Iniciar");
            actualizarTextoReloj();
        });

        seleccionarModo(true);
    }

    private void leerPreferencias() {
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        String correoActual = prefs.get("LAST_ACTIVE_ACCOUNT", "");
        boolean isDark = prefs.getBoolean("PREF_DARK_MODE_" + correoActual, false);
        colorFuerteGlobal = prefs.get("PREF_ACCENT_COLOR_" + correoActual, "#3D5A4D");
        colorBgBase = isDark ? "#121212" : "#f1f5f9";
        colorBgPanel = isDark ? "#1e1e1e" : "#ffffff";
        colorTextMain = isDark ? "#f8f9fa" : "#1e293b";
        colorTextMuted = isDark ? "#94a3b8" : "#64748b";
        colorBorder = isDark ? "#333333" : "#e2e8f0";
    }
    
    private void detenerReloj() {
        timeline.pause();
        animacionLatido.stop();
        lblReloj.setScaleX(1.0);
        lblReloj.setScaleY(1.0);
    }

    private void seleccionarModo(boolean isEnfoque) {
        controller.setModo(!isEnfoque);
        
        String rIzq = "15 0 0 15";
        String rDer = "0 15 15 0";
        String azulDescanso = "#0288D1"; 
        
        String stEnfActivo = "-fx-background-color: " + colorFuerteGlobal + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: " + rIzq + "; -fx-cursor: hand;";
        String stEnfInactivo = "-fx-background-color: transparent; -fx-text-fill: -color-text-muted; -fx-font-weight: bold; -fx-padding: 8 20; -fx-border-color: -color-border; -fx-border-radius: " + rIzq + "; -fx-cursor: hand;";
        
        String stDesActivo = "-fx-background-color: " + azulDescanso + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: " + rDer + "; -fx-cursor: hand;";
        String stDesInactivo = "-fx-background-color: transparent; -fx-text-fill: -color-text-muted; -fx-font-weight: bold; -fx-padding: 8 20; -fx-border-color: -color-border; -fx-border-radius: " + rDer + "; -fx-cursor: hand;";
        
        btnModoEnfoque.setStyle(isEnfoque ? stEnfActivo : stEnfInactivo);
        btnModoDescanso.setStyle(!isEnfoque ? stDesActivo : stDesInactivo);
        
        if(timeline != null) detenerReloj();
        if(btnMainAction != null) btnMainAction.setText("▶ Iniciar");
        
        actualizarTextoReloj();
    }

    // ==========================================
    // DISEÑO RADIAL Y COLORES 
    // ==========================================
    private StackPane crearRelojRadial() {
        StackPane root = new StackPane();
        grupoSegmentos = new Group();
        
        int numSegmentos = 48; 
        segmentosReloj = new Line[numSegmentos];
        double innerRadius = 115;
        double outerRadius = 145;

        for (int i = 0; i < numSegmentos; i++) {
            double angle = 2 * Math.PI * i / numSegmentos;
            double x1 = Math.cos(angle) * innerRadius;
            double y1 = Math.sin(angle) * innerRadius;
            double x2 = Math.cos(angle) * outerRadius;
            double y2 = Math.sin(angle) * outerRadius;

            Line segmento = new Line(x1, y1, x2, y2);
            segmento.setStrokeWidth(6);
            segmento.setStrokeLineCap(StrokeLineCap.ROUND); 
            
            segmentosReloj[i] = segmento;
            grupoSegmentos.getChildren().add(segmento);
        }

        animacionRotacion = new RotateTransition(Duration.seconds(20), grupoSegmentos);
        animacionRotacion.setByAngle(360);
        animacionRotacion.setCycleCount(Animation.INDEFINITE);
        animacionRotacion.setInterpolator(Interpolator.LINEAR);
        animacionRotacion.play();

        lblReloj = new Label("25:00");
        lblReloj.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        
        animacionLatido = new ScaleTransition(Duration.seconds(0.8), lblReloj);
        animacionLatido.setFromX(1.0); animacionLatido.setFromY(1.0);
        animacionLatido.setToX(1.05); animacionLatido.setToY(1.05);
        animacionLatido.setAutoReverse(true);
        animacionLatido.setCycleCount(Animation.INDEFINITE);
        
        aplicarColorAlReloj(colorRelojActual);
        
        root.getChildren().addAll(grupoSegmentos, lblReloj);
        return root;
    }
    
    private HBox crearPaletaColoresReloj() {
        HBox caja = new HBox(12);
        caja.setAlignment(Pos.CENTER);
        
        String[] colores = {colorFuerteGlobal, "#F57C00", "#1976D2", "#388E3C", "#7B1FA2", "RGB"};
        
        for (String col : colores) {
            Circle circuloColor = new Circle(12);
            circuloColor.setStyle("-fx-cursor: hand;");
            
            if (col.equals("RGB")) {
                Stop[] stops = new Stop[] { new Stop(0, Color.RED), new Stop(0.33, Color.LIME), new Stop(0.66, Color.BLUE), new Stop(1, Color.RED) };
                circuloColor.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops));
            } else {
                circuloColor.setFill(Color.web(col));
            }
            
            circuloColor.setOnMouseEntered(e -> {
                Paint relleno = circuloColor.getFill();
                Color shadowColor = (relleno instanceof Color) ? (Color) relleno : Color.GRAY;
                circuloColor.setEffect(new DropShadow(10, shadowColor));
            });
            circuloColor.setOnMouseExited(e -> circuloColor.setEffect(null));
            
            circuloColor.setOnMouseClicked(e -> {
                colorRelojActual = col;
                aplicarColorAlReloj(col);
            });
            
            caja.getChildren().add(circuloColor);
        }
        return caja;
    }
    
    private void aplicarColorAlReloj(String tipoColor) {
        for (int i = 0; i < segmentosReloj.length; i++) {
            if (tipoColor.equals("RGB")) {
                double hue = i * (360.0 / segmentosReloj.length);
                segmentosReloj[i].setStroke(Color.hsb(hue, 0.85, 0.9));
            } else {
                Color base = Color.web(tipoColor);
                double opacidad = 0.3 + (0.7 * ((double)i / segmentosReloj.length));
                segmentosReloj[i].setStroke(Color.color(base.getRed(), base.getGreen(), base.getBlue(), opacidad));
            }
        }
        
        String textColor = tipoColor.equals("RGB") ? colorFuerteGlobal : tipoColor;
        lblReloj.setStyle("-fx-font-size: 80px; -fx-font-weight: bold; -fx-text-fill: " + textColor + "; -fx-font-family: 'Segoe UI';");
    }

    private void actualizarTextoReloj() {
        lblReloj.setText(controller.getTiempoFormateado());
    }

    // ====================================================================
    // VENTANA DE DETALLES (RÉPLICA DE TEAMS) EN MODO LECTURA PARA POMODORO
    // ====================================================================
    private void mostrarDetallesTarea(Actividad tarea) {
        Stage popup = new Stage();
        Window owner = this.getScene().getWindow();
        if (owner != null) popup.initOwner(owner);
        popup.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox();
        root.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-width: 0 0 1 0;");
        
        Button btnCerrar = new Button("‹ Volver al Pomodoro");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorFuerteGlobal + "; -fx-cursor: hand; -fx-font-weight: bold;");
        btnCerrar.setOnAction(e -> popup.close());
        
        Region spacerHeader = new Region(); HBox.setHgrow(spacerHeader, Priority.ALWAYS);
        
        Label lblDevolucion = new Label("Modo Enfoque Activado");
        lblDevolucion.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-style: italic; -fx-font-size: 12px;");

        topBar.getChildren().addAll(btnCerrar, spacerHeader, lblDevolucion);

        HBox body = new HBox(40);
        body.setPadding(new Insets(30, 40, 40, 40));

        // COLUMNA IZQUIERDA
        VBox colIzq = new VBox(25);
        colIzq.setPrefWidth(450);
        
        VBox infoTitulo = new VBox(5);
        Label lblTitle = new Label(tarea.getNombre());
        lblTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        Label lblSubtitle = new Label("Vence el " + tarea.getFecha() + " a las " + tarea.getHoraVencimiento());
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
        Label lblNombreArchivo = new Label("Borrador_en_progreso.pdf"); lblNombreArchivo.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-weight: bold;");
        Region spArchivo = new Region(); HBox.setHgrow(spArchivo, Priority.ALWAYS);
        archivoBox.getChildren().addAll(lblIconoPdf, lblNombreArchivo, spArchivo);

        miTrabajoBox.getChildren().addAll(lblMiTrabajo, archivoBox);
        colIzq.getChildren().addAll(infoTitulo, miTrabajoBox);

        // COLUMNA DERECHA
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
        
        if (owner != null) {
            popup.setX(owner.getX() + (owner.getWidth() - scene.getWidth()) / 2);
            popup.setY(owner.getY() + (owner.getHeight() - scene.getHeight()) / 2);
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