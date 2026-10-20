package com.optem.ui;

import com.optem.controllers.NotasController;

import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class NotasView extends VBox {
    
    // Instancia del controlador
    private final NotasController controller = new NotasController();
    
    private ComboBox<String> comboMaterias;
    private TextArea areaTexto;
    private VBox historialBox;

    public NotasView() {
        this.setPadding(new Insets(30));
        this.setSpacing(25);
        this.setStyle("-fx-background-color: transparent;");

        // --- CABECERA ---
        Label titulo = new Label("Mis Apuntes de Clase");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: -color-text-main;");
        
        // --- SECCIÓN 1: NUEVO APUNTE ---
        VBox sectionNuevo = new VBox(15);
        sectionNuevo.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 20; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        
        Label lblNuevo = new Label("📝 Crear Nuevo Resumen");
        lblNuevo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-accent;");
        
        comboMaterias = new ComboBox<>();
        comboMaterias.setPromptText("Selecciona de qué materia es tu apunte...");
        comboMaterias.setStyle("-fx-background-color: -color-bg-base; -fx-text-fill: -color-text-main; -fx-border-color: -color-border; -fx-border-radius: 8;");
        comboMaterias.setPrefWidth(300);
        
        // Carga dinámica pidiéndole los datos al controlador
        comboMaterias.getItems().addAll(controller.getMateriasDisponibles());

        areaTexto = new TextArea();
        areaTexto.setPromptText("Escribe aquí lo más importante de la clase para repasarlo luego...");
        areaTexto.setPrefRowCount(6);
        areaTexto.setStyle("-fx-control-inner-background: -color-bg-base; -fx-background-color: transparent; -fx-text-fill: -color-text-main; -fx-prompt-text-fill: -color-text-muted;");

        Button btnGuardar = new Button("💾 Guardar Apunte");
        btnGuardar.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        
        btnGuardar.setOnAction(e -> {
            boolean exito = controller.guardarApunte(comboMaterias.getValue(), areaTexto.getText());
            if (exito) {
                areaTexto.clear();
                actualizarHistorial();
            }
        });

        sectionNuevo.getChildren().addAll(lblNuevo, comboMaterias, areaTexto, btnGuardar);

        // --- SECCIÓN 2: APUNTES GUARDADOS ---
        VBox sectionHistorial = new VBox(15);
        sectionHistorial.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 20; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        
        Label lblHistorial = new Label("📚 Historial para Estudiar");
        lblHistorial.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-accent;");
        
        historialBox = new VBox(10);
        actualizarHistorial();

        sectionHistorial.getChildren().addAll(lblHistorial, historialBox);

        ScrollPane scroll = new ScrollPane(new VBox(25, sectionNuevo, sectionHistorial));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        this.getChildren().addAll(titulo, scroll);
    }

    private void actualizarHistorial() {
        historialBox.getChildren().clear();
        
        // Pedimos los datos guardados al controlador
        Map<String, String> misApuntes = controller.getApuntesDelUsuario();

        if (misApuntes.isEmpty()) {
            Label vacio = new Label("Aún no tienes apuntes guardados. ¡Empieza a escribir arriba!");
            vacio.setStyle("-fx-text-fill: -color-text-muted;");
            historialBox.getChildren().add(vacio);
            return;
        }

        for (Map.Entry<String, String> entry : misApuntes.entrySet()) {
            VBox card = new VBox(5);
            card.setStyle("-fx-background-color: -color-bg-base; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: -color-border; -fx-border-radius: 8;");
            
            Label lblMat = new Label("📖 " + entry.getKey());
            lblMat.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;");
            
            Label lblTexto = new Label(entry.getValue());
            lblTexto.setStyle("-fx-text-fill: -color-text-muted;");
            lblTexto.setWrapText(true);
            
            card.getChildren().addAll(lblMat, lblTexto);
            historialBox.getChildren().add(card);
        }
    }
}