package com.optem;

import com.optem.ui.SplashScreen;

import atlantafx.base.theme.CupertinoLight;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // 1. Aplicamos el tema visual base (Cupertino)
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        // 2. Iniciamos la pantalla de carga (El Splash se encargará de abrir el Login solito al terminar)
        SplashScreen splash = new SplashScreen();
        splash.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}