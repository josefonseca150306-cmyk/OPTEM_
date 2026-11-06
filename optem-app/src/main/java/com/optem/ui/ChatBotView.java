package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.ChatBotController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox; 

public class ChatBotView {

    private final ChatBotController controller = new ChatBotController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    private VBox cajaMensajes;
    private ScrollPane scrollChat;

    public VBox getView(Runnable onClose) {
        leerPreferencias();

        VBox widgetChat = new VBox();
        widgetChat.setPrefSize(360, 520);
        widgetChat.setMaxSize(360, 520);
        widgetChat.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 16; -fx-border-color: " + colorBorder + "; -fx-border-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 5, 5);");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: " + colorFuerte + "; -fx-background-radius: 15 15 0 0;");

        Label iconoIA = new Label("🤖");
        iconoIA.setStyle("-fx-font-size: 24px;");
        
        VBox titulos = new VBox();
        Label titulo = new Label("Optem AI");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
        Label sub = new Label("En línea");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: #e2e8f0;");
        titulos.getChildren().addAll(titulo, sub);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnCerrar = new Button("✖");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> onClose.run());

        header.getChildren().addAll(iconoIA, titulos, spacer, btnCerrar);

        cajaMensajes = new VBox(12);
        cajaMensajes.setPadding(new Insets(15));
        cajaMensajes.setStyle("-fx-background-color: " + colorBgBase + ";");

        scrollChat = new ScrollPane(cajaMensajes);
        scrollChat.setFitToWidth(true);
        scrollChat.setStyle("-fx-background: " + colorBgBase + "; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollChat, Priority.ALWAYS);
        
        cajaMensajes.heightProperty().addListener((obs, oldVal, newVal) -> scrollChat.setVvalue(1.0));

        agregarMensajeIA("¡Hola! Soy tu asistente virtual. Conozco tu agenda, tus tareas y el estado del sistema. ¿Qué necesitas?");

        HBox inputBar = new HBox(10);
        inputBar.setPadding(new Insets(10));
        inputBar.setAlignment(Pos.CENTER);
        inputBar.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 0 0 15 15; -fx-border-color: " + colorBorder + "; -fx-border-width: 1 0 0 0;");

        TextField txtInput = new TextField();
        txtInput.setPromptText("Escribe un mensaje...");
        txtInput.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 10; -fx-background-radius: 20;");
        HBox.setHgrow(txtInput, Priority.ALWAYS);

        Button btnEnviar = new Button("➤");
        btnEnviar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50; -fx-cursor: hand;");

        Runnable enviarAccion = () -> {
            String texto = txtInput.getText().trim();
            if (!texto.isEmpty()) {
                agregarMensajeUsuario(texto);
                txtInput.clear();
                String respuesta = controller.procesarMensaje(texto);
                agregarMensajeIA(respuesta);
            }
        };

        btnEnviar.setOnAction(e -> enviarAccion.run());
        txtInput.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) enviarAccion.run(); });

        inputBar.getChildren().addAll(txtInput, btnEnviar);

        widgetChat.getChildren().addAll(header, scrollChat, inputBar);
        return widgetChat;
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

    private void agregarMensajeUsuario(String mensaje) {
        HBox fila = new HBox();
        fila.setAlignment(Pos.CENTER_RIGHT);
        Label burbuja = new Label(mensaje);
        burbuja.setWrapText(true); burbuja.setMaxWidth(260);
        burbuja.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-padding: 10 14; -fx-background-radius: 14 14 0 14; -fx-font-size: 13px;");
        fila.getChildren().add(burbuja);
        cajaMensajes.getChildren().add(fila);
    }

    private void agregarMensajeIA(String mensaje) {
        HBox fila = new HBox(8);
        fila.setAlignment(Pos.CENTER_LEFT);
        Label icono = new Label("🤖");
        icono.setStyle("-fx-font-size: 18px; -fx-background-color: " + colorFuerte + "30; -fx-background-radius: 50; -fx-padding: 5;");
        Label burbuja = new Label(mensaje);
        burbuja.setWrapText(true); burbuja.setMaxWidth(250);
        burbuja.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 14 14 14 0; -fx-background-radius: 14 14 14 0; -fx-padding: 10 14; -fx-font-size: 13px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        fila.getChildren().addAll(icono, burbuja);
        cajaMensajes.getChildren().add(fila);
    }
}