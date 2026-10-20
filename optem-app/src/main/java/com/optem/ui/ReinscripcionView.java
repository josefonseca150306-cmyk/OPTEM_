package com.optem.ui;

import com.optem.controllers.ReinscripcionController;
import com.optem.controllers.ReinscripcionController.BloqueHorario;
import com.optem.controllers.ReinscripcionController.MateriaDTO;
import com.optem.controllers.ReinscripcionController.SugerenciaDTO;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ReinscripcionView extends VBox {

    private final ReinscripcionController controller = new ReinscripcionController();

    private StackPane contenedorIzquierdo;
    private VBox vistaManual;
    private VBox vistaSugerencia;
    private GridPane gridHorario;

    public ReinscripcionView() {
        this.setStyle("-fx-background-color: transparent;");
        this.setPadding(new Insets(30, 40, 30, 40));
        this.setSpacing(20);

        // --- 1. CABECERA ---
        VBox header = new VBox(5);
        header.setStyle("-fx-background-color: -color-accent; -fx-padding: 20; -fx-background-radius: 10;");
        Label titulo = new Label("📋 Reinscripción — Constructor de Horario Semestral");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: white;");
        Label subtitulo = new Label("Selecciona tus materias y grupos · Vista semanal automática");
        subtitulo.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        header.getChildren().addAll(titulo, subtitulo);

        // --- 2. CONTROLES DE MODO ---
        HBox controles = new HBox(30);
        controles.setAlignment(Pos.CENTER_LEFT);
        controles.setPadding(new Insets(10, 0, 10, 0));
        
        Label lblModo = new Label("Modo:");
        lblModo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-text-main;");
        
        ToggleGroup modoGroup = new ToggleGroup();
        RadioButton rbManual = new RadioButton("Manual");
        rbManual.setStyle("-fx-font-size: 14px; -fx-cursor: hand; -fx-text-fill: -color-text-main;");
        rbManual.setToggleGroup(modoGroup);
        rbManual.setSelected(true); 
        
        RadioButton rbSugerencia = new RadioButton("Sugerencia inteligente");
        rbSugerencia.setStyle("-fx-font-size: 14px; -fx-cursor: hand; -fx-text-fill: -color-text-main;");
        rbSugerencia.setToggleGroup(modoGroup);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnConfirmar = new Button("✅ Confirmar horario");
        btnConfirmar.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20; -fx-cursor: hand;");
        
        Button btnLimpiar = new Button("🗑 Limpiar todo");
        btnLimpiar.setStyle("-fx-background-color: transparent; -fx-border-color: -color-accent; -fx-border-radius: 8; -fx-text-fill: -color-accent; -fx-font-weight: bold; -fx-padding: 8 20; -fx-cursor: hand;");
        btnLimpiar.setOnAction(e -> limpiarHorario());

        controles.getChildren().addAll(lblModo, rbManual, rbSugerencia, spacer, btnConfirmar, btnLimpiar);

        HBox splitLayout = new HBox(20);
        VBox.setVgrow(splitLayout, Priority.ALWAYS);

        contenedorIzquierdo = new StackPane();
        contenedorIzquierdo.setMinWidth(450);
        contenedorIzquierdo.setPrefWidth(450);
        
        crearVistaManual();
        crearVistaSugerencia();
        
        contenedorIzquierdo.getChildren().add(vistaManual); 

        modoGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            contenedorIzquierdo.getChildren().clear();
            limpiarHorario();
            if (newVal == rbManual) {
                contenedorIzquierdo.getChildren().add(vistaManual);
            } else {
                contenedorIzquierdo.getChildren().add(vistaSugerencia);
            }
        });

        // -- COLUMNA DERECHA (Calendario) --
        VBox contenedorDerecho = new VBox(15);
        HBox.setHgrow(contenedorDerecho, Priority.ALWAYS);
        
        Label lblCalendario = new Label("📅 Vista semanal");
        lblCalendario.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-text-main;");
        
        crearGridHorario();
        ScrollPane scrollGrid = new ScrollPane(gridHorario);
        scrollGrid.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: -color-border; -fx-border-radius: 10;");
        scrollGrid.setFitToWidth(true);
        VBox.setVgrow(scrollGrid, Priority.ALWAYS);
        
        contenedorDerecho.getChildren().addAll(lblCalendario, scrollGrid);

        splitLayout.getChildren().addAll(contenedorIzquierdo, contenedorDerecho);
        this.getChildren().addAll(header, controles, splitLayout);
    }

    private void crearVistaManual() {
        vistaManual = new VBox(15);
        Label lblTitle = new Label("📚 Materias disponibles");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-text-main;");
        vistaManual.getChildren().add(lblTitle);

        VBox listaMaterias = new VBox(15);
        
        // Obtenemos los datos desde el controlador
        for (MateriaDTO mat : controller.getMateriasDisponibles()) {
            listaMaterias.getChildren().add(
                crearTarjetaMateria(mat.titulo, mat.creditos, mat.mixta, mat.colorBorde, mat.opciones)
            );
        }

        ScrollPane scroll = new ScrollPane(listaMaterias);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        vistaManual.getChildren().add(scroll);
    }

    private VBox crearTarjetaMateria(String titulo, String creditos, boolean mixta, String colorBorde, String[] opciones) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 8; -fx-padding: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");
        card.setBorder(new Border(new BorderStroke(Color.web(colorBorde), Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, new CornerRadii(8), new BorderWidths(5, 0, 0, 0), Insets.EMPTY)));

        Label lblTitulo = new Label(titulo); lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: -color-text-main;"); lblTitulo.setWrapText(true);
        HBox tags = new HBox(10); tags.setAlignment(Pos.CENTER_LEFT);
        Label lblCred = new Label(creditos); lblCred.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;"); tags.getChildren().add(lblCred);
        if (mixta) { Label lblMixta = new Label("🔄 Mixta"); lblMixta.setStyle("-fx-background-color: #6A1B9A; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-radius: 10;"); tags.getChildren().add(lblMixta); }

        ComboBox<String> cbGrupos = new ComboBox<>(); cbGrupos.getItems().addAll(opciones); cbGrupos.setValue("— No cursar —"); cbGrupos.setMaxWidth(Double.MAX_VALUE);
        cbGrupos.setStyle("-fx-background-color: -color-bg-base; -fx-border-color: -color-border; -fx-border-radius: 5; -fx-text-fill: -color-text-main;");
        
        cbGrupos.setOnAction(e -> {
            List<BloqueHorario> bloques = controller.procesarSeleccionManual(titulo, cbGrupos.getValue(), colorBorde);
            dibujarBloques(bloques);
        });

        card.getChildren().addAll(lblTitulo, tags, cbGrupos); return card;
    }

    private void crearVistaSugerencia() {
        vistaSugerencia = new VBox(20);
        Label lblTitle = new Label("🤖 Horarios generados por IA");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-text-main;");
        vistaSugerencia.getChildren().add(lblTitle);

        VBox contenedorTarjetas = new VBox(20);
        
        // Obtenemos sugerencias desde el controlador
        for (SugerenciaDTO sug : controller.getSugerenciasIA()) {
            contenedorTarjetas.getChildren().add(
                crearTarjetaSugerencia(sug.titulo, sug.creditos, sug.materias, sug.detalle)
            );
        }

        ScrollPane scroll = new ScrollPane(contenedorTarjetas); 
        scroll.setFitToWidth(true); 
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS); vistaSugerencia.getChildren().add(scroll);
    }

    private VBox crearTarjetaSugerencia(String titulo, String cred, String mat, String detalle) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: -color-bg-base; -fx-background-radius: 10; -fx-padding: 20; -fx-border-color: -color-border; -fx-border-radius: 10;");
        Label lblTitulo = new Label("📄 " + titulo); lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: -color-accent;");
        Label lblSubs = new Label("Créditos: " + cred + " · Materias: " + mat); lblSubs.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        Label lblDetalle = new Label(detalle); lblDetalle.setStyle("-fx-text-fill: -color-text-main; -fx-line-spacing: 5px;");

        Button btnUsar = new Button("☑ Usar " + titulo);
        btnUsar.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
        btnUsar.setMaxWidth(Double.MAX_VALUE);
        
        btnUsar.setOnAction(e -> {
            limpiarHorario();
            List<BloqueHorario> bloques = controller.procesarSugerenciaIA(detalle);
            dibujarBloques(bloques);
        });
        
        card.getChildren().addAll(lblTitulo, lblSubs, lblDetalle, btnUsar); return card;
    }

    private void crearGridHorario() {
        gridHorario = new GridPane();
        gridHorario.setGridLinesVisible(true);
        gridHorario.setStyle("-fx-background-color: -color-bg-panel;");

        String[] dias = {"Hora", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        for (int i = 0; i < dias.length; i++) {
            Label lblDia = new Label(dias[i]);
            lblDia.setStyle("-fx-font-weight: bold; -fx-padding: 10; -fx-alignment: center; -fx-background-color: -color-bg-base; -fx-text-fill: -color-text-main;");
            lblDia.setMaxWidth(Double.MAX_VALUE);
            gridHorario.add(lblDia, i, 0);
            ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(i == 0 ? 10 : 15); gridHorario.getColumnConstraints().add(cc);
        }

        for (int i = 7; i <= 20; i++) {
            Label lblHora = new Label(String.format("%02d:00", i));
            lblHora.setStyle("-fx-padding: 10; -fx-text-fill: -color-text-muted; -fx-font-size: 11px;");
            gridHorario.add(lblHora, 0, i - 6);
        }
    }

    private void limpiarHorario() {
        gridHorario.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) > 0 && GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
    }

    private void dibujarBloques(List<BloqueHorario> bloques) {
        for (BloqueHorario bh : bloques) {
            Label bloque = new Label(bh.texto); 
            bloque.setWrapText(true);
            bloque.setStyle("-fx-background-color: " + bh.colorHex + "dd; -fx-text-fill: white; -fx-padding: 5; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 5;");
            bloque.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            gridHorario.add(bloque, bh.colDia, bh.rowHora, 1, bh.duracion); 
            GridPane.setMargin(bloque, new Insets(2));
        }
    }
}