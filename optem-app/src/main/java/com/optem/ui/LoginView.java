package com.optem.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.optem.controllers.LoginController;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class LoginView {

    private final LoginController controller = new LoginController();

    public void show(Stage stage) {
        
        if (controller.checkAutoLogin(stage)) {
            return;
        }

        HBox root = new HBox();
        root.getStyleClass().add("login-root");

        // ================= PANEL IZQUIERDO =================
        StackPane leftPanel = new StackPane();
        leftPanel.setPrefWidth(550);
        leftPanel.getStyleClass().add("login-panel-izquierdo");
        
        Pane bubblesPane = new Pane();
        bubblesPane.setMouseTransparent(false); 
        
        for (int i = 0; i < 25; i++) {
            bubblesPane.getChildren().add(createNormalBubble(bubblesPane));
        }

        AnimationTimer physicsEngine = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double w = bubblesPane.getWidth() > 0 ? bubblesPane.getWidth() : 550;
                double h = bubblesPane.getHeight() > 0 ? bubblesPane.getHeight() : 800;

                List<Node> toRemove = new ArrayList<>();

                for (Node n : bubblesPane.getChildren()) {
                    if (n instanceof Circle) {
                        Circle c = (Circle) n;
                        Double dx = (Double) c.getProperties().get("dx");
                        Double dy = (Double) c.getProperties().get("dy");
                        Double life = (Double) c.getProperties().get("life");

                        if (dx != null && dy != null) {
                            double nx = c.getLayoutX() + dx;
                            double ny = c.getLayoutY() + dy;
                            double r = c.getRadius();

                            if (nx - r < 0) { nx = r; dx = -dx; c.getProperties().put("dx", dx); }
                            if (nx + r > w) { nx = w - r; dx = -dx; c.getProperties().put("dx", dx); }
                            if (ny - r < 0) { ny = r; dy = -dy; c.getProperties().put("dy", dy); }
                            if (ny + r > h) { ny = h - r; dy = -dy; c.getProperties().put("dy", dy); }

                            c.setLayoutX(nx);
                            c.setLayoutY(ny);
                        }

                        if (life != null) {
                            life -= 0.005; 
                            c.setOpacity(life);
                            c.getProperties().put("life", life);
                            if (life <= 0) {
                                toRemove.add(c);
                            }
                        }
                    }
                }
                bubblesPane.getChildren().removeAll(toRemove);
            }
        };
        physicsEngine.start();
        leftPanel.getChildren().add(bubblesPane);

        VBox brandingBox = new VBox(20);
        brandingBox.setAlignment(Pos.CENTER_LEFT);
        brandingBox.setPadding(new Insets(50, 50, 50, 80));
        brandingBox.setMouseTransparent(true); 

        HBox logosBox = new HBox(40);
        logosBox.setAlignment(Pos.CENTER_LEFT);
        
        try {
            InputStream isUaem = getClass().getResourceAsStream("/images/uaem_escudo.png");
            if (isUaem != null) { ImageView ivUaem = new ImageView(new Image(isUaem)); ivUaem.setPreserveRatio(true); ivUaem.setFitHeight(90); logosBox.getChildren().add(ivUaem); }

            InputStream isOptem = getClass().getResourceAsStream("/images/logo_optem_white.png");
            if (isOptem != null) { ImageView ivOptem = new ImageView(new Image(isOptem)); ivOptem.setPreserveRatio(true); ivOptem.setFitHeight(110); logosBox.getChildren().add(ivOptem); }
        } catch(Exception ex) {}

        Label lblTitleOptem = new Label("OPTEM");
        lblTitleOptem.getStyleClass().add("login-titulo-optem");
        lblTitleOptem.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.1)));
        VBox.setMargin(lblTitleOptem, new Insets(10, 0, 10, 0));

        VBox featuresBox = new VBox(25);
        featuresBox.getChildren().addAll(
            createFeatureLabel("📅", "Calendario Académico", "Agenda y horario académico"),
            createFeatureLabel("⏱", "Administración del Tiempo", "Técnica Pomodoro"),
            createFeatureLabel("🏆", "XP, Rachas y Logros", "Sistema de recompensas"),
            createFeatureLabel("🔔", "Alertas y Notificaciones", "Alertas de entregas")
        );

        brandingBox.getChildren().addAll(logosBox, lblTitleOptem, featuresBox);
        leftPanel.getChildren().add(brandingBox);

        // ================= PANEL DERECHO =================
        VBox rightPanel = new VBox(30);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(40, 120, 40, 120));
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        VBox formBox = new VBox(18);
        formBox.setMaxWidth(450);
        formBox.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label("Iniciar sesión");
        lblTitle.getStyleClass().add("login-titulo-derecho");
        VBox.setMargin(lblTitle, new Insets(0, 0, 10, 0));

        TextField txtNombre = new TextField(); txtNombre.setPromptText("Nombre(s)"); txtNombre.getStyleClass().add("login-input");
        TextField txtApellido = new TextField(); txtApellido.setPromptText("Apellidos (Opcional)"); txtApellido.getStyleClass().add("login-input");
        TextField txtCorreo = new TextField(); txtCorreo.setPromptText("Correo institucional"); txtCorreo.getStyleClass().add("login-input");

        ContextMenu domainMenu = new ContextMenu();
        domainMenu.getStyleClass().add("menu-dominio-oro"); 
        
        MenuItem optAlumno = new MenuItem("@alumno.uaemex.mx");
        MenuItem optUaemex = new MenuItem("@uaemex.mx");
        domainMenu.getItems().addAll(optAlumno, optUaemex);

        txtCorreo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.endsWith("@")) { double x = txtCorreo.localToScreen(txtCorreo.getBoundsInLocal()).getMinX(); double y = txtCorreo.localToScreen(txtCorreo.getBoundsInLocal()).getMaxY(); domainMenu.show(txtCorreo, x, y); } 
            else if (!newVal.contains("@")) { domainMenu.hide(); }
        });

        optAlumno.setOnAction(e -> { txtCorreo.setText(txtCorreo.getText() + "alumno.uaemex.mx"); Platform.runLater(txtCorreo::end); });
        optUaemex.setOnAction(e -> { txtCorreo.setText(txtCorreo.getText() + "uaemex.mx"); Platform.runLater(txtCorreo::end); });

        StackPane passContainer = new StackPane();
        PasswordField txtPassHidden = new PasswordField(); txtPassHidden.setPromptText("Número de cuenta"); txtPassHidden.getStyleClass().addAll("login-input", "login-input-password");
        TextField txtPassShown = new TextField(); txtPassShown.setPromptText("Número de cuenta"); txtPassShown.getStyleClass().addAll("login-input", "login-input-password"); txtPassShown.setVisible(false);

        Button btnTogglePass = new Button("👁");
        btnTogglePass.setStyle("-fx-background-color: transparent; -fx-text-fill: #555; -fx-cursor: hand; -fx-font-size: 16;");
        StackPane.setAlignment(btnTogglePass, Pos.CENTER_RIGHT); StackPane.setMargin(btnTogglePass, new Insets(0, 10, 0, 0));

        btnTogglePass.setOnAction(e -> {
            if (txtPassHidden.isVisible()) { txtPassShown.setText(txtPassHidden.getText()); txtPassHidden.setVisible(false); txtPassShown.setVisible(true); } 
            else { txtPassHidden.setText(txtPassShown.getText()); txtPassShown.setVisible(false); txtPassHidden.setVisible(true); }
        });
        passContainer.getChildren().addAll(txtPassHidden, txtPassShown, btnTogglePass);

        Label lblError = new Label(); lblError.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 13; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';"); lblError.setVisible(false);
        Label lblTipo = new Label("Tipo de cuenta"); lblTipo.setStyle("-fx-font-size: 13; -fx-text-fill: #888; -fx-margin-top: 10; -fx-font-family: 'Segoe UI';");

        HBox buttonsBox = new HBox(20); buttonsBox.setAlignment(Pos.CENTER_LEFT);
        Button btnEstudiante = createAccountTypeButton("🎓", "Estudiante");
        Button btnMaestro = createAccountTypeButton("👨‍🏫", "Maestro");
        buttonsBox.getChildren().addAll(btnEstudiante, btnMaestro);

        btnEstudiante.setOnAction(e -> {
            String password = txtPassHidden.isVisible() ? txtPassHidden.getText() : txtPassShown.getText();
            controller.handleLogin(txtNombre.getText(), txtApellido.getText(), txtCorreo.getText(), password, "ESTUDIANTE", lblError, stage);
        });

        btnMaestro.setOnAction(e -> {
            String password = txtPassHidden.isVisible() ? txtPassHidden.getText() : txtPassShown.getText();
            
            // TRUCO PARA EL ADMINISTRADOR (Si escribe ese correo, se le da acceso especial)
            String rolAAsignar = txtCorreo.getText().equals("admin@uaemex.mx") ? "ADMINISTRADOR" : "MAESTRO";
            
            controller.handleLogin(txtNombre.getText(), txtApellido.getText(), txtCorreo.getText(), password, rolAAsignar, lblError, stage);
        });

        formBox.getChildren().addAll(lblTitle, txtNombre, txtApellido, txtCorreo, passContainer, lblError, lblTipo, buttonsBox);
        rightPanel.getChildren().add(formBox);

        root.getChildren().addAll(leftPanel, rightPanel);

        Scene scene = new Scene(root, 1280, 720);
        
        try {
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch(Exception e) {
            System.out.println("No se encontro el archivo styles.css en src/main/resources/css/");
        }

        stage.setScene(scene);
        stage.setTitle("Optem - Iniciar Sesión");
        stage.centerOnScreen();
        stage.setMaximized(true);
        
        stage.setOnCloseRequest(e -> physicsEngine.stop());
        stage.show();
    }

    private Circle createNormalBubble(Pane pane) {
        Circle bubble = new Circle(Math.random() * 45 + 15);
        bubble.setFill(Color.web("#FFFFFF", 0.05 + Math.random() * 0.1)); bubble.setStroke(Color.web("#FFFFFF", 0.15));
        bubble.setLayoutX(Math.random() * 400 + 50); bubble.setLayoutY(Math.random() * 600 + 50);
        bubble.getProperties().put("dx", (Math.random() - 0.5) * 2.5); bubble.getProperties().put("dy", (Math.random() - 0.5) * 2.5);

        bubble.setOnMouseEntered(e -> bubble.setFill(Color.web("#FFFFFF", 0.2))); bubble.setOnMouseExited(e -> bubble.setFill(Color.web("#FFFFFF", 0.05 + Math.random() * 0.1)));

        bubble.setOnMouseClicked(e -> {
            pane.getChildren().remove(bubble);
            for (int i = 0; i < (int)(Math.random() * 3) + 6; i++) {
                Circle mini = new Circle(bubble.getRadius() / 4); mini.setFill(Color.web("#FFFFFF", 0.4)); mini.setLayoutX(bubble.getLayoutX()); mini.setLayoutY(bubble.getLayoutY());
                mini.getProperties().put("dx", (Math.random() - 0.5) * 8); mini.getProperties().put("dy", (Math.random() - 0.5) * 8); mini.getProperties().put("life", 1.0); 
                pane.getChildren().add(mini);
            }
            pane.getChildren().add(createNormalBubble(pane));
        });

        return bubble;
    }

    private HBox createFeatureLabel(String icon, String title, String description) {
        StackPane iconCircle = new StackPane(); iconCircle.setPrefSize(40, 40); iconCircle.setMinSize(40, 40);
        iconCircle.getStyleClass().add("feature-icon-circle"); 
        
        Label iconLbl = new Label(icon); iconLbl.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-family: 'Segoe UI Emoji';"); iconCircle.getChildren().add(iconLbl);
        
        VBox textBox = new VBox(0); textBox.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title); titleLbl.getStyleClass().add("feature-title");
        Label descLbl = new Label(description); descLbl.getStyleClass().add("feature-desc");
        textBox.getChildren().addAll(titleLbl, descLbl);
        
        HBox card = new HBox(15, iconCircle, textBox); card.setAlignment(Pos.CENTER_LEFT); return card;
    }

    private Button createAccountTypeButton(String icon, String text) {
        VBox box = new VBox(8); box.setAlignment(Pos.CENTER);
        Label iconLbl = new Label(icon); iconLbl.getStyleClass().add("boton-tipo-cuenta-icono");
        Label textLbl = new Label(text); textLbl.getStyleClass().add("boton-tipo-cuenta-texto");
        Label subLbl = new Label("Entrar"); subLbl.getStyleClass().add("boton-tipo-cuenta-sub");
        
        box.getChildren().addAll(iconLbl, textLbl, subLbl);
        Button btn = new Button(); btn.setGraphic(box); btn.setPrefSize(180, 140);
        btn.getStyleClass().add("boton-tipo-cuenta");
        
        return btn;
    }
}