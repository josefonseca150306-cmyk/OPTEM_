package com.optem.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreen {

    public void show(Stage primaryStage) {
        
        // 1. CREAMOS EL SPLASH TEMPORAL (Como una capa superior)
        Stage splashStage = new Stage();
        // Lo hacemos "Transparente" para poder desvanecerlo y ver qué hay detrás
        splashStage.initStyle(StageStyle.TRANSPARENT); 
        
        StackPane root = new StackPane();
        // Fondo institucional de la universidad
        root.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 70%, #1a2e23, #050505);");

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);

        ImageView ivLogo = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/uaem_escudo.png"));
            ivLogo.setImage(img);
            ivLogo.setFitHeight(220); 
            ivLogo.setPreserveRatio(true);
        } catch (Exception e) {}

        Label lblOptem = new Label("O P T E M");
        lblOptem.setStyle("-fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: white; -fx-letter-spacing: 8px;");
        
        Label lblSub = new Label("DOMINA TU TIEMPO. POTENCIA TU FUTURO.");
        lblSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888; -fx-letter-spacing: 3px;");

        content.getChildren().addAll(ivLogo, lblOptem, lblSub);
        
        ivLogo.setOpacity(0);
        lblOptem.setOpacity(0);
        lblSub.setOpacity(0);

        root.getChildren().add(content);

        Scene scene = new Scene(root);
        // Fundamental para que al quitarle el color, la ventana sea invisible
        scene.setFill(Color.TRANSPARENT); 
        splashStage.setScene(scene);
        
        // Hacemos que la capa del Splash cubra exactamente tu pantalla respetando tu barra de tareas
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        splashStage.setX(bounds.getMinX());
        splashStage.setY(bounds.getMinY());
        splashStage.setWidth(bounds.getWidth());
        splashStage.setHeight(bounds.getHeight());
        
        // Mostramos el Splash inmediatamente
        splashStage.show();

        // ==========================================
        // MOTOR DE ANIMACIÓN
        // ==========================================
        FadeTransition fadeInLogo = new FadeTransition(Duration.seconds(1.5), ivLogo);
        fadeInLogo.setFromValue(0);
        fadeInLogo.setToValue(1);

        ScaleTransition scaleLogo = new ScaleTransition(Duration.seconds(2.5), ivLogo);
        scaleLogo.setFromX(0.9);
        scaleLogo.setFromY(0.9);
        scaleLogo.setToX(1.05);
        scaleLogo.setToY(1.05);

        FadeTransition fadeInText = new FadeTransition(Duration.seconds(1), lblOptem);
        fadeInText.setFromValue(0);
        fadeInText.setToValue(1);
        
        FadeTransition fadeInSub = new FadeTransition(Duration.seconds(1), lblSub);
        fadeInSub.setFromValue(0);
        fadeInSub.setToValue(1);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));

        // Magia de Revelado: Desvanecemos lentamente la capa negra (root), 
        // lo que revelará automáticamente a la ventana que esté detrás.
        FadeTransition fadeOutAll = new FadeTransition(Duration.seconds(1.5), root);
        fadeOutAll.setFromValue(1);
        fadeOutAll.setToValue(0);

        SequentialTransition secuencia = new SequentialTransition(
            fadeInLogo,
            fadeInText,
            fadeInSub,
            scaleLogo, 
            pause,
            fadeOutAll
        );

        secuencia.setOnFinished(e -> {
            // Cuando la animación termina y ya todo es transparente, simplemente 
            // destruimos la ventana temporal. El Login ya quedó enfrente.
            splashStage.close();
        });

        // 2. EL TRUCO MAESTRO: Cargar el Login ATRÁS en silencio
        Platform.runLater(() -> {
            // El sistema decide si carga el Login o se salta directo al Dashboard
            new LoginView().show(primaryStage);
            
            // ESTA ES LA LÍNEA MÁGICA: Obliga al Splash a quedarse por encima de TODO, 
            // sin importar si el Dashboard se maximiza o intenta taparlo.
            splashStage.setAlwaysOnTop(true);
            
            // Aseguramos que el Splash siga al frente bloqueando la vista
            splashStage.toFront();
            
            // Ahora sí, arrancamos la película
            secuencia.play();
        });
    }
}