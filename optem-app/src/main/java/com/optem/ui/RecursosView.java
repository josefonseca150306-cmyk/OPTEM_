package com.optem.ui;

import com.optem.controllers.RecursosController;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RecursosView extends VBox {

    // Instancia de nuestro nuevo controlador
    private final RecursosController controller = new RecursosController();

    public RecursosView() {
        this.setPadding(new Insets(30));
        this.setSpacing(25);
        this.setStyle("-fx-background-color: transparent;");

        Label titulo = new Label("Recursos y Biblioteca");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: -color-text-main;");
        Label subtitulo = new Label("Material educativo específico para tus materias inscritas");
        subtitulo.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 14px;");

        VBox listaRecursos = new VBox(15);
        
        // Obtenemos los recursos directamente del controlador y los dibujamos
        List<String[]> recursos = controller.getRecursosPorUsuario();
        for (String[] recurso : recursos) {
            agregarRecurso(listaRecursos, recurso[0], recurso[1]);
        }

        this.getChildren().addAll(titulo, subtitulo, listaRecursos);
    }

    private void agregarRecurso(VBox container, String title, String desc) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        
        Label lblT = new Label(title);
        lblT.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-accent;");
        
        Label lblD = new Label(desc);
        lblD.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 13px;");
        lblD.setWrapText(true);
        
        card.getChildren().addAll(lblT, lblD);
        container.getChildren().add(card);
    }
}