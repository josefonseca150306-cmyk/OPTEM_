package com.optem.ui;

import java.util.List;
import java.util.prefs.Preferences;

import com.optem.controllers.NotasMaestroController;
import com.optem.logic.NotificacionManager;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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

public class NotasMaestroView {
    
    private final NotasMaestroController controller = new NotasMaestroController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    
    private FlowPane gridNotas;
    private Stage stagePrincipal; 

    public VBox getView() {
        leerPreferencias();

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("📖 Cuaderno Docente");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Planeación de clases, rúbricas y notas de juntas académicas.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNuevo = new Button("✍ Nueva Nota Docente");
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
        List<NotasMaestroController.ApunteDocenteDTO> apuntes = controller.cargarApuntes();

        if (apuntes.isEmpty()) {
            VBox vacioBox = new VBox(15);
            vacioBox.setAlignment(Pos.CENTER);
            vacioBox.setPadding(new Insets(100, 0, 0, 0));
            Label msjVacio = new Label("Aún no tienes planeaciones ni notas registradas.");
            msjVacio.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 16px;");
            vacioBox.getChildren().add(msjVacio);
            gridNotas.getChildren().add(vacioBox);
            return;
        }

        for (NotasMaestroController.ApunteDocenteDTO apunte : apuntes) {
            gridNotas.getChildren().add(crearTarjetaKeep(apunte));
        }
    }

    private VBox crearTarjetaKeep(NotasMaestroController.ApunteDocenteDTO apunte) {
        VBox card = new VBox(10);
        card.setPrefWidth(280); 
        card.setMaxWidth(280);
        card.setPadding(new Insets(20));
        
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; " +
                      "-fx-border-color: " + apunte.colorHex + "; -fx-border-width: 4 1 1 1; -fx-border-radius: 12; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 3); -fx-cursor: hand;");

        Label lblCat = new Label(apunte.categoria);
        lblCat.setStyle("-fx-font-weight: bold; -fx-font-size: 10px; -fx-text-fill: " + apunte.colorHex + "; -fx-background-color: " + apunte.colorHex + "20; -fx-padding: 3 8; -fx-background-radius: 10;");
        
        Label lblTit = new Label(apunte.titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + "; -fx-wrap-text: true;");
        
        Label lblGrupoInfo = new Label("Grupo: " + apunte.grupo);
        lblGrupoInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold;");

        String previewLimpio = apunte.contenido.replaceAll("<[^>]*>", "").replace("&nbsp;", " ").trim();
        Label lblTexto = new Label(previewLimpio);
        lblTexto.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-wrap-text: true; -fx-line-spacing: 4px;");
        lblTexto.setMaxHeight(80); 
        
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        Label lblFecha = new Label(apunte.fecha);
        lblFecha.setStyle("-fx-font-size: 10px; -fx-text-fill: " + colorTextMuted + ";");
        Region spF = new Region(); HBox.setHgrow(spF, Priority.ALWAYS);
        
        Button btnEliminar = new Button("🗑");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-cursor: hand;");
        btnEliminar.setOnAction(e -> {
            e.consume();
            controller.eliminarApunte(apunte.id);
            actualizarTableroNotas();
            NotificacionManager.mostrarNotificacion("Eliminado", "La nota docente ha sido borrada.", "INFO");
        });
        footer.getChildren().addAll(lblFecha, spF, btnEliminar);

        card.getChildren().addAll(lblCat, lblTit, lblGrupoInfo, lblTexto, footer);
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
        
        ComboBox<String> cmbCat = new ComboBox<>();
        cmbCat.setPromptText("Tipo de nota...");
        cmbCat.getItems().addAll(controller.getCategoriasDisponibles());
        cmbCat.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 8;");
        
        ComboBox<String> cmbGrupo = new ComboBox<>();
        cmbGrupo.setPromptText("Vincular a grupo...");
        cmbGrupo.getItems().addAll(controller.getGruposDisponibles());
        cmbGrupo.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");

        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> popup.close());
        
        Button btnGuardar = new Button("Guardar Documento");
        btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");

        topBar.getChildren().addAll(cmbCat, cmbGrupo, sp1, btnCancelar, btnGuardar);
        root.setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

        VBox editorBox = new VBox(10);
        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Escribe el Título de la Planeación o Nota");
        txtTitulo.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        HTMLEditor editorHtml = new HTMLEditor();
        editorHtml.setPrefHeight(450);
        VBox.setVgrow(editorHtml, Priority.ALWAYS);

        editorBox.getChildren().addAll(txtTitulo, editorHtml);
        root.setCenter(editorBox);

        btnGuardar.setOnAction(e -> {
            if (!txtTitulo.getText().isEmpty() && cmbCat.getValue() != null && cmbGrupo.getValue() != null) {
                controller.guardarApunte(txtTitulo.getText(), cmbCat.getValue(), cmbGrupo.getValue(), editorHtml.getHtmlText());
                actualizarTableroNotas();
                popup.close();
                NotificacionManager.mostrarNotificacion("Guardado", "Nota docente guardada con éxito.", "INFO");
            } else {
                NotificacionManager.mostrarNotificacion("Faltan Datos", "Selecciona categoría, grupo y título.", "URGENTE");
            }
        });

        Scene scene = new Scene(root, 900, 650);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        
        if (stagePrincipal != null) {
            popup.setX(stagePrincipal.getX() + (stagePrincipal.getWidth() - scene.getWidth()) / 2);
            popup.setY(stagePrincipal.getY() + (stagePrincipal.getHeight() - scene.getHeight()) / 2);
        }
        popup.show();
    }

    private void mostrarVisorDocumento(NotasMaestroController.ApunteDocenteDTO apunte) {
        Stage popup = new Stage();
        if (stagePrincipal != null) popup.initOwner(stagePrincipal);
        popup.initStyle(StageStyle.UNDECORATED);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20, 30, 20, 30));
        root.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + apunte.colorHex + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 30, 0, 0, 10);");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Button btnCerrar = new Button("‹ Volver");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorFuerte + "; -fx-cursor: hand; -fx-font-weight: bold;");
        btnCerrar.setOnAction(e -> popup.close());
        
        Label lblTit = new Label(apunte.titulo);
        lblTit.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        
        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        
        Label lblCat = new Label(apunte.categoria + " | " + apunte.grupo);
        lblCat.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: " + apunte.colorHex + "; -fx-padding: 5 12; -fx-background-radius: 15;");
        
        topBar.getChildren().addAll(btnCerrar, lblTit, sp1, lblCat);
        root.setTop(topBar);
        BorderPane.setMargin(topBar, new Insets(0, 0, 20, 0));

        WebView visorWeb = new WebView();
        String htmlVisual = "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #ffffff; color: #000000; height: 100%;'>" 
                          + apunte.contenido + "</div>";
        visorWeb.getEngine().loadContent(htmlVisual);
        
        root.setCenter(visorWeb);

        Scene scene = new Scene(root, 800, 600);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        
        if (stagePrincipal != null) {
            popup.setX(stagePrincipal.getX() + (stagePrincipal.getWidth() - scene.getWidth()) / 2);
            popup.setY(stagePrincipal.getY() + (stagePrincipal.getHeight() - scene.getHeight()) / 2);
        }
        popup.show();
    }
}