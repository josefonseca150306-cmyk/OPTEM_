package com.optem.ui;

import com.optem.controllers.SplashController;
import com.optem.controllers.SplashController.SplashTheme;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration; 

public class SplashScreen {

    public interface OnLoadingComplete { void run(); }

    // Instancia del controlador
    private final SplashController controller = new SplashController();

    public void show(Stage stage, OnLoadingComplete callback) {
        
        // Pedimos los colores del tema al controlador
        SplashTheme theme = controller.getTheme();

        AnchorPane root = new AnchorPane();
        root.setPrefSize(900, 600);
        root.setStyle("-fx-background-color: " + theme.bgRoot + "; -fx-background-radius: 20;");

        // CÍRCULOS DE FONDO (Animaciones visuales se quedan en la vista)
        addAnimatedCircle(root, 150, "#B38E5D", 0.6, -100, 50, 150, 100, 10); 
        addAnimatedCircle(root, 100, "#2D463E", 0.8, 400, -50, 450, 20, 12);  
        addAnimatedCircle(root, 130, "#84A98C", 0.7, 750, 450, 800, 400, 8);  
        addAnimatedCircle(root, 110, "#52796F", 0.6, 50, 500, 120, 450, 9);   
        addAnimatedCircle(root, 90, "#354F52", 0.9, 800, 100, 750, 50, 15);  

        // CONTENEDOR PRINCIPAL 
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPrefSize(900, 600);
        content.setStyle("-fx-background-color: transparent;");

        // Logos 
        HBox logos = new HBox(40); 
        logos.setAlignment(Pos.CENTER);
        try {
            ImageView logoUaem = new ImageView(new Image(getClass().getResourceAsStream("/images/uaem_escudo.png")));
            logoUaem.setFitWidth(100); 
            logoUaem.setPreserveRatio(true);
            
            ImageView logoOptem = new ImageView(new Image(getClass().getResourceAsStream("/images/logo_optem.png")));
            logoOptem.setFitWidth(200); 
            logoOptem.setPreserveRatio(true);
            
            logos.getChildren().addAll(logoUaem, logoOptem);
        } catch (Exception e) { System.out.println("Error al cargar logos en resources/images/"); }

        Label lblOptem = new Label("OPTEM");
        lblOptem.setStyle("-fx-font-size: 55; -fx-font-weight: bold; -fx-text-fill: " + theme.textTitle + ";");

        Label lblSlogan = new Label("Agenda Virtual Inteligente · UAEMéx");
        lblSlogan.setStyle("-fx-font-size: 16; -fx-text-fill: " + theme.textSlogan + ";");

        // SPINNER DE CARGA
        ProgressIndicator pi = new ProgressIndicator();
        pi.setPrefSize(60, 60);
        pi.setStyle("-fx-progress-color: " + theme.textStatus + ";");

        Label lblStatus = new Label("Iniciando...");
        lblStatus.setStyle("-fx-font-size: 13; -fx-text-fill: " + theme.textStatus + "; -fx-font-weight: bold;");

        content.getChildren().addAll(logos, lblOptem, lblSlogan, pi, lblStatus);
        root.getChildren().add(content);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        // Delegamos el proceso asíncrono al controlador
        controller.simulateLoading(() -> {
            stage.close();
            callback.run();
        });
    }

    private void addAnimatedCircle(AnchorPane root, double radius, String color, double opacity, double x, double y, double tx, double ty, int duration) {
        Circle c = new Circle(radius, Color.web(color, opacity));
        c.setLayoutX(x);
        c.setLayoutY(y);
        root.getChildren().add(c);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(duration), c);
        tt.setToX(tx - x);
        tt.setToY(ty - y);
        tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.play();
    }
}