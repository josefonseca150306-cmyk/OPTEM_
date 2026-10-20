package com.optem.ui;

import com.optem.controllers.AjustesController;

import java.io.File;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
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
        lblTitle.setStyle("-fx-font-size: 26; -fx-font-weight: bold; -fx-text-fill: -color-text-main; -fx-font-family: 'Segoe UI';");
        Label lblSubtitle = new Label("Personaliza tu experiencia en Optem");
        lblSubtitle.setStyle("-fx-font-size: 14; -fx-text-fill: -color-text-muted; -fx-font-family: 'Segoe UI';");
        headerBox.getChildren().addAll(lblTitle, lblSubtitle);

        // Se le pide al controlador los datos correspondientes
        String finalNombre = controller.getNombreInicial();
        String finalCarrera = controller.getCarreraInicial();
        String finalSemestre = controller.getSemestreInicial();

        // --- SECCIÓN 1: PERFIL ---
        VBox panelPerfil = crearPanelSeccion("👤 Perfil del Estudiante", colorFuerte);
        HBox infoPerfil = new HBox(25); infoPerfil.setAlignment(Pos.CENTER_LEFT);
        VBox avatarLayout = new VBox(10); avatarLayout.setAlignment(Pos.CENTER);
        
        StackPane avatarBox = new StackPane(); Circle fondoAvatar = new Circle(40, Color.web(colorFuerte));
        String inicial = finalNombre.length() > 0 ? finalNombre.substring(0, 1).toUpperCase() : "A";
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
        Button btnCambiarFoto = new Button("Cambiar foto"); btnCambiarFoto.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorFuerte + "; -fx-font-size: 12px; -fx-cursor: hand; -fx-underline: true;");
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
        TextField txtNombre = crearInput(finalNombre); TextField txtCorreo = crearInput(correoUsuario); txtCorreo.setDisable(true);
        TextField txtCarrera = crearInput(finalCarrera); TextField txtSemestre = crearInput(finalSemestre);
        gridPerfil.add(envolverConLabel("Nombre completo", txtNombre), 0, 0); gridPerfil.add(envolverConLabel("Correo universitario", txtCorreo), 1, 0);
        gridPerfil.add(envolverConLabel("Carrera", txtCarrera), 0, 1); gridPerfil.add(envolverConLabel("Semestre actual", txtSemestre), 1, 1);
        infoPerfil.getChildren().addAll(avatarLayout, gridPerfil); panelPerfil.getChildren().add(infoPerfil);

        // --- SECCIÓN 2: APARIENCIA ---
        VBox aparienciaBox = new VBox(25); aparienciaBox.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 15; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");
        Label lblAparienciaTitle = new Label("🎨 Apariencia"); lblAparienciaTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: " + colorFuerte + "; -fx-font-family: 'Segoe UI';");

        HBox rowModoOscuro = createSettingRow("🌙 Modo oscuro"); rowModoOscuro.getChildren().add(createToggleSwitch("PREF_DARK_MODE", false, colorFuerte));

        HBox rowColor = createSettingRow("🎨 Color de acento");
        ColorPicker colorPicker = new ColorPicker(); colorPicker.setStyle("-fx-background-color: transparent; -fx-color-label-visible: false; -fx-cursor: hand;"); colorPicker.setValue(Color.web(colorFuerte));
        colorPicker.setOnAction(e -> { 
            controller.guardarColoresAcento(colorPicker.getValue()); 
            if (onThemeChange != null) onThemeChange.run(); 
        });
        Label lblAvisoColor = new Label(" (Aplica al navegar)"); lblAvisoColor.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12;"); rowColor.getChildren().addAll(colorPicker, lblAvisoColor);

        HBox rowFuente = createSettingRow("🔤 Tamaño de fuente");
        ComboBox<String> cbFuente = new ComboBox<>(); cbFuente.getItems().addAll("Pequeño", "Normal", "Grande"); cbFuente.setValue(controller.getPrefString("PREF_FONT_SIZE", "Normal"));
        cbFuente.setStyle("-fx-background-color: -color-bg-base; -fx-text-fill: -color-text-main; -fx-background-radius: 8; -fx-padding: 2 10; -fx-font-family: 'Segoe UI'; -fx-cursor: hand;");
        cbFuente.setOnAction(e -> { controller.setPrefString("PREF_FONT_SIZE", cbFuente.getValue()); if (onThemeChange != null) onThemeChange.run(); });
        rowFuente.getChildren().add(cbFuente);

        HBox rowAnimaciones = createSettingRow("✨ Animaciones"); rowAnimaciones.getChildren().add(createToggleSwitch("PREF_ANIMATIONS", true, colorFuerte));
        aparienciaBox.getChildren().addAll(lblAparienciaTitle, rowModoOscuro, rowColor, rowFuente, rowAnimaciones);

        // --- SECCIÓN 3: NOTIFICACIONES ---
        VBox notificacionesBox = new VBox(25); notificacionesBox.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 15; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");
        Label lblNotificacionesTitle = new Label("🔔 Notificaciones"); lblNotificacionesTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: " + colorFuerte + "; -fx-font-family: 'Segoe UI';");

        ToggleGroup notiGroup = new ToggleGroup();
        RadioButton rbApagado = createStyledRadioButton("Apagado", notiGroup, colorFuerte); RadioButton rbPrioritario = createStyledRadioButton("Solo prioritario", notiGroup, colorFuerte); RadioButton rbCompleto = createStyledRadioButton("Completo", notiGroup, colorFuerte);

        String savedNoti = controller.getPrefString("PREF_NOTIFICATIONS", "COMPLETO");
        if (savedNoti.equals("APAGADO")) rbApagado.setSelected(true); else if (savedNoti.equals("PRIORITARIO")) rbPrioritario.setSelected(true); else rbCompleto.setSelected(true);
        notiGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> { 
            if (newVal == rbApagado) controller.setPrefString("PREF_NOTIFICATIONS", "APAGADO"); 
            else if (newVal == rbPrioritario) controller.setPrefString("PREF_NOTIFICATIONS", "PRIORITARIO"); 
            else if (newVal == rbCompleto) controller.setPrefString("PREF_NOTIFICATIONS", "COMPLETO"); 
        });

        notificacionesBox.getChildren().addAll(lblNotificacionesTitle, rbApagado, rbPrioritario, rbCompleto);
        
        // --- SECCIÓN 4: ACCESIBILIDAD ---
        VBox accesibilidadBox = new VBox(25); accesibilidadBox.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 15; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 15, 0, 0, 5);");
        Label lblAccTitle = new Label("♿ Accesibilidad"); lblAccTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: " + colorFuerte + "; -fx-font-family: 'Segoe UI';");

        HBox rowTeclado = createSettingRowHelp("⌨ Navegación por teclado", "Ctrl: Menú · Tab: Navegar · Flechas: Mover · Enter: Activar"); rowTeclado.getChildren().add(createToggleSwitch("PREF_ACC_KEYBOARD", false, colorFuerte));
        HBox rowVoz = createSettingRowHelp("🎙 Comandos de voz", "Abre un asistente dictado al presionar F2"); rowVoz.getChildren().add(createToggleSwitch("PREF_ACC_VOICE", false, colorFuerte));
        HBox rowLector = createSettingRowHelp("🗣 Lector de pantalla", "Lee secciones al pasar el mouse o navegar por teclado"); rowLector.getChildren().add(createToggleSwitch("PREF_ACC_TTS", false, colorFuerte));

        accesibilidadBox.getChildren().addAll(lblAccTitle, rowTeclado, rowVoz, rowLector);

        HBox botonesAccion = new HBox(15); botonesAccion.setAlignment(Pos.CENTER_RIGHT);
        Button btnGuardar = new Button("💾 Guardar cambios"); btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 30; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> { 
            controller.guardarPerfil(txtNombre.getText(), txtCarrera.getText(), txtSemestre.getText()); 
            btnGuardar.setText("¡Datos guardados!"); 
            if (onThemeChange != null) onThemeChange.run(); 
        });

        botonesAccion.getChildren().add(btnGuardar);
        contenido.getChildren().addAll(headerBox, panelPerfil, aparienciaBox, notificacionesBox, accesibilidadBox, botonesAccion);
        ScrollPane scroll = new ScrollPane(contenido); scroll.setFitToWidth(true); scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;"); VBox.setVgrow(scroll, Priority.ALWAYS); mainContainer.getChildren().add(scroll);

        return mainContainer;
    }

    private VBox crearPanelSeccion(String tituloSeccion, String colorFuerte) {
        VBox panel = new VBox(20); panel.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 12; -fx-padding: 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        Label lblTitulo = new Label(tituloSeccion); lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorFuerte + "; -fx-border-color: -color-border; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 10 0;"); lblTitulo.setMaxWidth(Double.MAX_VALUE);
        panel.getChildren().add(lblTitulo); return panel;
    }

    private TextField crearInput(String valorInicial) {
        TextField txt = new TextField(valorInicial); txt.setStyle("-fx-background-color: -color-bg-base; -fx-border-color: -color-border; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8; -fx-text-fill: -color-text-main;"); txt.setPrefWidth(250); return txt;
    }

    private VBox envolverConLabel(String etiqueta, TextField input) { VBox box = new VBox(5); Label lbl = new Label(etiqueta); lbl.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px; -fx-font-weight: bold;"); box.getChildren().addAll(lbl, input); return box; }
    private HBox createSettingRow(String text) { HBox row = new HBox(); row.setAlignment(Pos.CENTER_LEFT); Label lbl = new Label(text); lbl.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: -color-text-main; -fx-font-family: 'Segoe UI';"); Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS); row.getChildren().addAll(lbl, spacer); return row; }
    private HBox createSettingRowHelp(String text, String helpText) { HBox row = new HBox(15); row.setAlignment(Pos.CENTER_LEFT); Label lbl = new Label(text); lbl.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: -color-text-main; -fx-font-family: 'Segoe UI';"); Label lblHelp = new Label(helpText); lblHelp.setStyle("-fx-font-size: 12; -fx-text-fill: -color-text-muted; -fx-font-family: 'Segoe UI Light';"); VBox texts = new VBox(2, lbl, lblHelp); Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS); row.getChildren().addAll(texts, spacer); return row; }

    private RadioButton createStyledRadioButton(String text, ToggleGroup group, String color) {
        RadioButton rb = new RadioButton(text); rb.setToggleGroup(group); rb.setStyle("-fx-font-size: 14; -fx-text-fill: -color-text-main; -fx-font-family: 'Segoe UI'; -fx-cursor: hand; -fx-accent: " + color + ";"); return rb;
    }

    private Node createToggleSwitch(String prefKeyBase, boolean defaultValue, String colorFuerte) {
        boolean isOn = controller.getPrefBoolean(prefKeyBase, defaultValue);
        Rectangle background = new Rectangle(40, 20); background.setArcWidth(20); background.setArcHeight(20); background.setStyle("-fx-fill: " + (isOn ? colorFuerte : "-color-border") + ";");
        javafx.scene.shape.Circle trigger = new javafx.scene.shape.Circle(10); trigger.setCenterX(10); trigger.setCenterY(10); trigger.setFill(Color.WHITE); trigger.setEffect(new DropShadow(2, Color.rgb(0,0,0,0.2)));
        TranslateTransition trans = new TranslateTransition(Duration.millis(200), trigger); if (isOn) trigger.setTranslateX(20);
        StackPane toggleSwitch = new StackPane(background, trigger); toggleSwitch.setAlignment(Pos.CENTER_LEFT); toggleSwitch.setFocusTraversable(true); toggleSwitch.setStyle("-fx-cursor: hand;");
        
        Runnable ejecutarCambio = () -> {
            boolean newState = controller.alternarPrefBoolean(prefKeyBase, defaultValue); 
            trans.setToX(newState ? 20 : 0); background.setStyle("-fx-fill: " + (newState ? colorFuerte : "-color-border") + ";"); trans.play(); 
            controller.ejecutarLectorDeVozSiEstaActivo(prefKeyBase, newState);
            if (!prefKeyBase.startsWith("PREF_ACC_") && onThemeChange != null) onThemeChange.run();
        };

        toggleSwitch.setOnMouseClicked(e -> ejecutarCambio.run());
        toggleSwitch.setOnKeyPressed(e -> { if (e.getCode() == javafx.scene.input.KeyCode.SPACE || e.getCode() == javafx.scene.input.KeyCode.ENTER) ejecutarCambio.run(); }); return toggleSwitch;
    }
}