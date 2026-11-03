package com.optem.ui;

import java.io.File;
import java.util.Map;

import com.optem.controllers.AjustesController;
import com.optem.logic.NotificacionManager;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class AjustesView {

    private final Runnable onThemeChange;
    private final String correoUsuario;
    private final AjustesController controller;

    public AjustesView(Runnable onThemeChange, String correoUsuario) {
        this.onThemeChange = onThemeChange;
        this.correoUsuario = correoUsuario;
        this.controller = new AjustesController(correoUsuario);
    }

    public VBox getView() {
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: transparent;");

        VBox contenido = new VBox(35);
        contenido.setPadding(new Insets(10, 40, 40, 40));
        contenido.setStyle("-fx-background-color: transparent;");

        String colorFuerte = controller.getColorFuerte();

        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("⚙ Ajustes y Personalización");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;");
        lblTitle.getStyleClass().add("lbl-titulo-banner"); 
        
        Label lblSubtitle = new Label("Personaliza tu experiencia en Optem");
        lblSubtitle.setStyle("-fx-text-fill: -color-text-muted;");
        lblSubtitle.getStyleClass().add("lbl-text-pequeno"); 
        headerBox.getChildren().addAll(lblTitle, lblSubtitle);

        Map<String, String> datosDB = controller.getPerfilDesdeMock();
        String finalNombre = datosDB.get("nombre_completo");
        String finalCarrera = datosDB.get("carrera");
        String finalSemestre = "4to Semestre";

        // --- SECCIÓN 1: PERFIL ---
        VBox panelPerfil = crearPanelSeccion("👤 Perfil del Estudiante (Control Escolar)", colorFuerte);
        HBox infoPerfil = new HBox(25); infoPerfil.setAlignment(Pos.CENTER_LEFT);
        VBox avatarLayout = new VBox(10); avatarLayout.setAlignment(Pos.CENTER);
        
        StackPane avatarBox = new StackPane(); Circle fondoAvatar = new Circle(40, Color.web(colorFuerte));
        String inicial = finalNombre.substring(0, 1).toUpperCase();
        Label lblAvatar = new Label(inicial); lblAvatar.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        String savedPhotoPath = controller.getRutaFotoPerfil();
        if (!savedPhotoPath.isEmpty()) { 
            File photoFile = new File(savedPhotoPath); 
            if (photoFile.exists()) { 
                try { 
                    Image croppedImg = controller.recortarImagenCuadrada(new Image(photoFile.toURI().toString()));
                    fondoAvatar.setFill(new ImagePattern(croppedImg)); 
                    lblAvatar.setVisible(false); 
                } catch (Exception e) {} 
            } 
        }
        
        avatarBox.getChildren().addAll(fondoAvatar, lblAvatar);
        Button btnCambiarFoto = new Button("Cambiar foto"); 
        btnCambiarFoto.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorFuerte + "; -fx-cursor: hand; -fx-underline: true;");
        btnCambiarFoto.getStyleClass().add("lbl-text-pequeno"); 
        
        btnCambiarFoto.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser(); fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) { 
                Image newImg = new Image(selectedFile.toURI().toString()); 
                fondoAvatar.setFill(new ImagePattern(controller.recortarImagenCuadrada(newImg))); 
                lblAvatar.setVisible(false); 
                controller.guardarRutaFotoPerfil(selectedFile.getAbsolutePath()); 
                if (onThemeChange != null) onThemeChange.run(); 
            }
        });
        
        avatarLayout.getChildren().addAll(avatarBox, btnCambiarFoto);
        
        GridPane gridPerfil = new GridPane(); gridPerfil.setHgap(20); gridPerfil.setVgap(15);
        TextField txtNombre = crearInput(finalNombre); txtNombre.setEditable(false);
        TextField txtCorreo = crearInput(correoUsuario); txtCorreo.setEditable(false);
        TextField txtCarrera = crearInput(finalCarrera); txtCarrera.setEditable(false);
        TextField txtSemestre = crearInput(finalSemestre); txtSemestre.setEditable(false);
        
        gridPerfil.add(envolverConLabel("Nombre completo", txtNombre), 0, 0); gridPerfil.add(envolverConLabel("Correo institucional", txtCorreo), 1, 0);
        gridPerfil.add(envolverConLabel("Carrera", txtCarrera), 0, 1); gridPerfil.add(envolverConLabel("Semestre actual", txtSemestre), 1, 1);
        infoPerfil.getChildren().addAll(avatarLayout, gridPerfil); panelPerfil.getChildren().add(infoPerfil);

        // --- SECCIÓN 2: APARIENCIA ---
        VBox aparienciaBox = crearPanelSeccion("🎨 Apariencia", colorFuerte);
        HBox rowModoOscuro = createSettingRow("🌙 Modo oscuro"); rowModoOscuro.getChildren().add(createToggleSwitch("PREF_DARK_MODE", false, colorFuerte));
        HBox rowColor = createSettingRow("🎨 Color de acento");
        ColorPicker colorPicker = new ColorPicker(Color.web(colorFuerte)); colorPicker.setStyle("-fx-background-color: transparent; -fx-color-label-visible: false; -fx-cursor: hand;");
        colorPicker.setOnAction(e -> { controller.guardarColoresAcento(colorPicker.getValue()); if (onThemeChange != null) onThemeChange.run(); });
        Label lblAvisoColor = new Label(" (Aplica globalmente)"); lblAvisoColor.setStyle("-fx-text-fill: -color-text-muted;"); lblAvisoColor.getStyleClass().add("lbl-text-pequeno");
        rowColor.getChildren().addAll(colorPicker, lblAvisoColor);
        
        HBox rowFuente = createSettingRow("🔤 Tamaño de fuente global");
        ComboBox<String> cbFuente = new ComboBox<>(); cbFuente.getItems().addAll("Pequeño", "Normal", "Grande"); cbFuente.setValue(controller.getPrefString("PREF_FONT_SIZE", "Normal"));
        cbFuente.setStyle("-fx-background-color: -color-bg-base; -fx-border-color: -color-border; -fx-border-radius: 8; -fx-text-inner-color: -color-text-main; -fx-prompt-text-fill: -color-text-main;");
        cbFuente.setOnAction(e -> { controller.setPrefString("PREF_FONT_SIZE", cbFuente.getValue()); if (onThemeChange != null) onThemeChange.run(); });
        rowFuente.getChildren().add(cbFuente);
        aparienciaBox.getChildren().addAll(rowModoOscuro, rowColor, rowFuente);

        // --- SECCIÓN 3: NOTIFICACIONES ---
        VBox notificacionesBox = crearPanelSeccion("🔔 Notificaciones del Sistema", colorFuerte);
        ToggleGroup notiGroup = new ToggleGroup();
        RadioButton rbApagado = createStyledRadioButton("Apagado", notiGroup); 
        RadioButton rbPrioritario = createStyledRadioButton("Solo prioritario (Reuniones y Urgentes)", notiGroup); 
        RadioButton rbCompleto = createStyledRadioButton("Completo (Tareas, Recordatorios, Clases)", notiGroup);

        String savedNoti = controller.getPrefString("PREF_NOTIFICATIONS", "COMPLETO");
        if (savedNoti.equals("APAGADO")) rbApagado.setSelected(true); else if (savedNoti.equals("PRIORITARIO")) rbPrioritario.setSelected(true); else rbCompleto.setSelected(true);
        
        notiGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> { 
            if (newVal == rbApagado) controller.setPrefString("PREF_NOTIFICATIONS", "APAGADO"); 
            else if (newVal == rbPrioritario) controller.setPrefString("PREF_NOTIFICATIONS", "PRIORITARIO"); 
            else if (newVal == rbCompleto) controller.setPrefString("PREF_NOTIFICATIONS", "COMPLETO"); 
        });

        Button btnPruebaNoti = new Button("Probar Notificación");
        btnPruebaNoti.setStyle("-fx-background-color: -color-bg-base; -fx-border-color: -color-border; -fx-text-fill: -color-text-main; -fx-border-radius: 5;");
        btnPruebaNoti.setOnAction(e -> NotificacionManager.mostrarNotificacion("🔔 Nueva Tarea Asignada", "El docente Marcela Camacho asignó: Práctica 3.", "INFO"));

        notificacionesBox.getChildren().addAll(rbApagado, rbPrioritario, rbCompleto, btnPruebaNoti);
        
        // --- SECCIÓN 4: ACCESIBILIDAD ---
        VBox accesibilidadBox = crearPanelSeccion("♿ Accesibilidad (Global)", colorFuerte);
        HBox rowTeclado = createSettingRowHelp("⌨ Navegación por teclado", "Tab: Menú · Flechas: Mover cursor · Enter: Clic"); rowTeclado.getChildren().add(createToggleSwitch("PREF_ACC_KEYBOARD", false, colorFuerte));
        
        HBox rowVoz = createSettingRowHelp("🎙 Comandos de voz (Micrófono)", "Activa el reconocimiento de voz presionando F2 (Requiere estar activado)"); 
        Node toggleVoz = createToggleSwitchVoz("PREF_ACC_VOICE", false, colorFuerte);
        rowVoz.getChildren().add(toggleVoz);

        accesibilidadBox.getChildren().addAll(rowTeclado, rowVoz);

        contenido.getChildren().addAll(headerBox, panelPerfil, aparienciaBox, notificacionesBox, accesibilidadBox);
        ScrollPane scroll = new ScrollPane(contenido); scroll.setFitToWidth(true); scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;"); VBox.setVgrow(scroll, Priority.ALWAYS); mainContainer.getChildren().add(scroll);

        return mainContainer;
    }

    private VBox crearPanelSeccion(String tituloSeccion, String colorFuerte) {
        VBox panel = new VBox(20); panel.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 12; -fx-padding: 25; -fx-border-color: -color-border; -fx-border-radius: 12;");
        Label lblTitulo = new Label(tituloSeccion); lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorFuerte + "; -fx-border-color: -color-border; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 10 0;"); 
        lblTitulo.getStyleClass().add("lbl-text-normal");
        lblTitulo.setMaxWidth(Double.MAX_VALUE);
        panel.getChildren().add(lblTitulo); return panel;
    }

    private TextField crearInput(String valor) {
        TextField txt = new TextField(valor); txt.setStyle("-fx-background-color: -color-bg-base; -fx-border-color: -color-border; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-text-fill: -color-text-main; -fx-opacity: 0.8;"); txt.setPrefWidth(250); return txt;
    }

    private VBox envolverConLabel(String etiqueta, TextField input) { 
        VBox box = new VBox(5); Label lbl = new Label(etiqueta); lbl.setStyle("-fx-text-fill: -color-text-muted; -fx-font-weight: bold;"); 
        lbl.getStyleClass().add("lbl-text-pequeno");
        box.getChildren().addAll(lbl, input); return box; 
    }
    
    private HBox createSettingRow(String text) { 
        HBox row = new HBox(); row.setAlignment(Pos.CENTER_LEFT); Label lbl = new Label(text); lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;"); 
        lbl.getStyleClass().add("lbl-text-normal");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS); row.getChildren().addAll(lbl, spacer); return row; 
    }
    
    private HBox createSettingRowHelp(String text, String helpText) { 
        HBox row = new HBox(15); row.setAlignment(Pos.CENTER_LEFT); 
        Label lbl = new Label(text); lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;"); lbl.getStyleClass().add("lbl-text-normal");
        Label lblHelp = new Label(helpText); lblHelp.setStyle("-fx-text-fill: -color-text-muted;"); lblHelp.getStyleClass().add("lbl-text-pequeno");
        VBox texts = new VBox(2, lbl, lblHelp); Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS); row.getChildren().addAll(texts, spacer); return row; 
    }

    // --- CORRECCIÓN FINAL PARA EL RECUADRO BLANCO DE NOTIFICACIONES ---
    private RadioButton createStyledRadioButton(String text, ToggleGroup group) {
        RadioButton rb = new RadioButton(text); 
        rb.setToggleGroup(group); 
        // Forzamos explícitamente el fondo a transparente para solucionar el bug de JavaFX en modo oscuro
        rb.setStyle("-fx-text-fill: -color-text-main; -fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-cursor: hand;"); 
        return rb;
    }

    private Node createToggleSwitch(String prefKeyBase, boolean defaultValue, String colorFuerte) {
        boolean isOn = controller.getPrefBoolean(prefKeyBase, defaultValue);
        Rectangle background = new Rectangle(40, 20); background.setArcWidth(20); background.setArcHeight(20); background.setStyle("-fx-fill: " + (isOn ? colorFuerte : "-color-border") + ";");
        Circle trigger = new Circle(10); trigger.setCenterX(10); trigger.setCenterY(10); trigger.setFill(Color.WHITE); trigger.setEffect(new DropShadow(2, Color.rgb(0,0,0,0.2)));
        TranslateTransition trans = new TranslateTransition(Duration.millis(200), trigger); if (isOn) trigger.setTranslateX(20);
        StackPane toggleSwitch = new StackPane(background, trigger); toggleSwitch.setAlignment(Pos.CENTER_LEFT); toggleSwitch.setFocusTraversable(true); toggleSwitch.setStyle("-fx-cursor: hand;");
        
        Runnable ejecutarCambio = () -> {
            boolean newState = controller.alternarPrefBoolean(prefKeyBase, defaultValue); 
            trans.setToX(newState ? 20 : 0); background.setStyle("-fx-fill: " + (newState ? colorFuerte : "-color-border") + ";"); trans.play(); 
            if (!prefKeyBase.startsWith("PREF_ACC_") && onThemeChange != null) onThemeChange.run();
        };
        toggleSwitch.setOnMouseClicked(e -> ejecutarCambio.run()); return toggleSwitch;
    }

    private Node createToggleSwitchVoz(String prefKeyBase, boolean defaultValue, String colorFuerte) {
        Node toggle = createToggleSwitch(prefKeyBase, defaultValue, colorFuerte);
        toggle.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, e -> {
            if (controller.getPrefBoolean(prefKeyBase, false)) {
                mostrarGuiaComandosVoz(colorFuerte);
            }
        });
        return toggle;
    }

    private void mostrarGuiaComandosVoz(String colorFuerte) {
        Stage stage = new Stage(); stage.initStyle(StageStyle.UNDECORATED);
        VBox root = new VBox(15); root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: -color-bg-panel; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12;");
        
        Label lblTit = new Label("🎙 Comandos de Voz Activados");
        lblTit.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorFuerte + ";"); lblTit.getStyleClass().add("lbl-titulos-seccion");
        
        Label lblDesc = new Label("Al presionar F2 se activará el reconocimiento de voz.\nPuedes decir las siguientes palabras clave:");
        lblDesc.setStyle("-fx-text-fill: -color-text-main;"); lblDesc.getStyleClass().add("lbl-text-pequeno");
        
        VBox lista = new VBox(8);
        lista.getChildren().addAll(
            new Label("• \"Ir a inicio\" / \"Pantalla principal\""),
            new Label("• \"Mostrar agenda\" / \"Mi horario\""),
            new Label("• \"Abrir tareas\" / \"Mis pendientes\""),
            new Label("• \"Ver notas\" / \"Mis apuntes\""),
            new Label("• \"Reuniones\" / \"Videollamadas\""),
            new Label("• \"Pomodoro\" / \"Reloj de estudio\""),
            new Label("• \"Mis logros\" / \"Progreso\""),
            new Label("• \"Reinscripción\" / \"Nuevo semestre\""),
            new Label("• \"Ajustes\" / \"Configuración\"")
        );
        for(Node n : lista.getChildren()) { n.setStyle("-fx-text-fill: -color-text-muted; -fx-font-style: italic;"); n.getStyleClass().add("lbl-text-pequeno"); }
        
        Button btn = new Button("Entendido");
        btn.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 20; -fx-cursor: hand;");
        btn.setOnAction(ev -> stage.close());
        
        root.getChildren().addAll(lblTit, lblDesc, lista, btn);
        Scene s = new Scene(root); s.setFill(Color.TRANSPARENT);
        stage.setScene(s); stage.show();
    }
}