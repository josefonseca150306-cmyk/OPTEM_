package com.optem.ui;

import com.optem.controllers.NotasController;
import com.optem.logic.NotificacionManager;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class NotasView {
    
    private final NotasController controller = new NotasController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    
    private FlowPane gridNotas;
    private Stage stagePrincipal; 

    public VBox getView() {
        leerPreferencias();
        controller.getMateriasDisponibles(); 

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        
        Label titulo = new Label("Mi Cuaderno de Notas");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        
        Label subtitulo = new Label("Crea apuntes con formato avanzado. Da clic en una tarjeta para leer tu documento.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNuevo = new Button("✍ Escribir Apunte");
        btnNuevo.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnNuevo.setOnAction(e -> mostrarEditorAvanzado());

        header.getChildren().addAll(titleBox, spacer, btnNuevo);

        gridNotas = new FlowPane();
        gridNotas.setHgap(20);
        gridNotas.setVgap(20);
        gridNotas.setAlignment(Pos.TOP_LEFT);

        ScrollPane scroll = new ScrollPane(gridNotas);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        
        actualizarTableroNotas();

        layoutMain.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) newS.windowProperty().addListener((o, oldW, newW) -> { if(newW != null) stagePrincipal = (Stage) newW; });
        });

        layoutMain.getChildren().addAll(header, scroll);
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

    private void actualizarTableroNotas() {
        gridNotas.getChildren().clear();
        // CORRECCIÓN: Se llama al método cargarApuntes en lugar de getTodosLosApuntes
        List<NotasController.ApunteDTO> apuntes = controller.cargarApuntes();

        if (apuntes.isEmpty()) {
            VBox vacioBox = new VBox(15);
            vacioBox.setAlignment(Pos.CENTER);
            vacioBox.setPadding(new Insets(100, 0, 0, 0));
            Label iconoVacio = new Label("📭");
            iconoVacio.setStyle("-fx-font-size: 60px; -fx-font-family: 'Segoe UI Emoji';");
            Label msjVacio = new Label("Aún no tienes apuntes. Tus notas aparecerán aquí.");
            msjVacio.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 16px;");
            vacioBox.getChildren().addAll(iconoVacio, msjVacio);
            gridNotas.getChildren().add(vacioBox);
            return;
        }

        for (NotasController.ApunteDTO apunte : apuntes) {
            gridNotas.getChildren().add(crearTarjetaKeep(apunte));
        }
    }

    private VBox crearTarjetaKeep(NotasController.ApunteDTO apunte) {
        VBox card = new VBox(10);
        card.setPrefWidth(280); 
        card.setMaxWidth(280);
        card.setPadding(new Insets(20));
        
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; " +
                      "-fx-border-color: " + apunte.colorHex + "; -fx-border-width: 4 1 1 1; -fx-border-radius: 12; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 3); -fx-cursor: hand;");

        String icono = apunte.materia.equals("Nota personal") ? "📌 " : (apunte.materia.equals("Recordatorio") ? "⏰ " : "📚 ");
        
        Label lblMateria = new Label(icono + apunte.materia);
        lblMateria.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + apunte.colorHex + "; -fx-background-color: " + apunte.colorHex + "20; -fx-padding: 3 8; -fx-background-radius: 10;");
        
        Label lblTit = new Label(apunte.titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + "; -fx-wrap-text: true;");
        
        Label lblFecha = new Label(apunte.fecha);
        lblFecha.setStyle("-fx-font-size: 10px; -fx-text-fill: " + colorTextMuted + ";");

        String previewLimpio = apunte.contenido.replaceAll("<[^>]*>", "").replace("&nbsp;", " ").trim();
        Label lblTexto = new Label(previewLimpio);
        lblTexto.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-wrap-text: true; -fx-line-spacing: 4px;");
        lblTexto.setMaxHeight(100); 
        
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        Button btnEliminar = new Button("🗑");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-cursor: hand; -fx-font-family: 'Segoe UI Emoji';");
        btnEliminar.setOnAction(e -> {
            e.consume();
            controller.eliminarApunte(apunte.id);
            actualizarTableroNotas();
            NotificacionManager.mostrarNotificacion("Eliminado", "El apunte ha sido borrado.", "INFO");
        });
        footer.getChildren().add(btnEliminar);

        card.getChildren().addAll(lblMateria, lblTit, lblFecha, lblTexto, footer);

        card.setOnMouseClicked(e -> mostrarVisorDocumento(apunte));

        ScaleTransition stIn = new ScaleTransition(Duration.millis(150), card); stIn.setToX(1.03); stIn.setToY(1.03);
        ScaleTransition stOut = new ScaleTransition(Duration.millis(150), card); stOut.setToX(1.0); stOut.setToY(1.0);
        card.setOnMouseEntered(e -> stIn.playFromStart());
        card.setOnMouseExited(e -> stOut.playFromStart());

        return card;
    }

    private void mostrarEditorAvanzado() {
        Stage popup = new Stage();
        if (stagePrincipal != null) popup.initOwner(stagePrincipal);
        popup.initStyle(StageStyle.UNDECORATED);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20, 30, 20, 30));
        root.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> cmbMateria = new ComboBox<>();
        cmbMateria.setPromptText("📌 Selecciona una categoría...");
        cmbMateria.getItems().addAll(controller.getMateriasDisponibles());
        
        cmbMateria.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2px; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 5 10;");
        
        cmbMateria.setCellFactory(lv -> new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { 
                    setText(null); 
                    setStyle("-fx-background-color: " + colorBgPanel + ";"); 
                } else {
                    String icon = item.equals("Nota personal") ? "📌 " : (item.equals("Recordatorio") ? "⏰ " : "📚 ");
                    setText(icon + item);
                    setStyle("-fx-background-color: " + colorBgPanel + "; -fx-text-fill: " + colorTextMain + "; -fx-font-weight: bold; -fx-padding: 8;");
                }
            }
        });
        cmbMateria.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle("-fx-text-fill: " + colorTextMain + ";"); } 
                else {
                    String icon = item.equals("Nota personal") ? "📌 " : (item.equals("Recordatorio") ? "⏰ " : "📚 ");
                    setText(icon + item);
                    setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-weight: bold;");
                }
            }
        });

        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> popup.close());
        
        Button btnGuardar = new Button("Guardar Documento");
        btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");

        topBar.getChildren().addAll(cmbMateria, sp1, btnCancelar, btnGuardar);
        root.setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

        VBox editorBox = new VBox(10);
        
        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Escribe el Título del Documento");
        txtTitulo.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 24px; -fx-font-weight: bold; -fx-prompt-text-fill: " + colorTextMuted + "80;");
        
        HTMLEditor editorHtml = new HTMLEditor();
        editorHtml.setPrefHeight(450);
        VBox.setVgrow(editorHtml, Priority.ALWAYS);

        editorBox.getChildren().addAll(txtTitulo, editorHtml);
        root.setCenter(editorBox);

        btnGuardar.setOnAction(e -> {
            // CORRECCIÓN: el método original devolvía void, así que se cambió la validación
            if (!txtTitulo.getText().isEmpty() && cmbMateria.getValue() != null && !editorHtml.getHtmlText().isEmpty()) {
                controller.guardarApunte(txtTitulo.getText(), cmbMateria.getValue(), editorHtml.getHtmlText());
                actualizarTableroNotas();
                popup.close();
                NotificacionManager.mostrarNotificacion("Apunte Guardado", "Documento respaldado en tu archivo local.", "INFO");
            } else {
                NotificacionManager.mostrarNotificacion("Faltan Datos", "Selecciona una materia, pon un título y escribe contenido.", "URGENTE");
            }
        });

        Scene scene = new Scene(root, 850, 650);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        
        if (stagePrincipal != null) {
            popup.setX(stagePrincipal.getX() + (stagePrincipal.getWidth() - scene.getWidth()) / 2);
            popup.setY(stagePrincipal.getY() + (stagePrincipal.getHeight() - scene.getHeight()) / 2);
        }
        popup.show();
    }

    private void mostrarVisorDocumento(NotasController.ApunteDTO apunte) {
        Stage popup = new Stage();
        if (stagePrincipal != null) popup.initOwner(stagePrincipal);
        popup.initStyle(StageStyle.UNDECORATED);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20, 30, 20, 30));
        root.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + apunte.colorHex + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Button btnCerrar = new Button("‹ Volver");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorFuerte + "; -fx-cursor: hand; -fx-font-weight: bold;");
        btnCerrar.setOnAction(e -> popup.close());
        
        Label lblTit = new Label(apunte.titulo);
        lblTit.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        
        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        
        Label lblMateria = new Label(apunte.materia);
        lblMateria.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: " + apunte.colorHex + "; -fx-padding: 5 12; -fx-background-radius: 15;");
        
        topBar.getChildren().addAll(btnCerrar, lblTit, sp1, lblMateria);
        root.setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

        WebView visorWeb = new WebView();
        String htmlVisual = "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #ffffff; color: #000000; height: 100%;'>" 
                          + apunte.contenido + "</div>";
        visorWeb.getEngine().loadContent(htmlVisual);
        
        root.setCenter(visorWeb);

        Scene scene = new Scene(root, 750, 600);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        
        if (stagePrincipal != null) {
            popup.setX(stagePrincipal.getX() + (stagePrincipal.getWidth() - scene.getWidth()) / 2);
            popup.setY(stagePrincipal.getY() + (stagePrincipal.getHeight() - scene.getHeight()) / 2);
        }
        popup.show();
    }
}