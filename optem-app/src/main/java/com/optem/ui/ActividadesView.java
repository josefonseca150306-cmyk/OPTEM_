package com.optem.ui;

import com.optem.controllers.ActividadesController;
import com.optem.models.Actividad;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActividadesView extends VBox {

    private VBox listaContenedor;
    private TextField buscador;
    private ComboBox<String> comboMaterias;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    // Instancia del controlador
    private final ActividadesController controller = new ActividadesController();

    public ActividadesView() {
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: transparent;");

        // --- 1. CABECERA ---
        Label titulo = new Label("Mis Actividades Personales");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: -color-text-main;");
        
        Button btnNueva = new Button("+ Nueva actividad");
        btnNueva.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 25; -fx-cursor: hand; -fx-font-weight: bold;");
        btnNueva.setOnAction(e -> abrirModalNuevaActividad());

        HBox header = new HBox(titulo, new Region(), btnNueva);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setAlignment(Pos.CENTER_LEFT);

        // --- 2. FILTROS ---
        HBox filtros = new HBox(10);
        String[] cats = {"Todas", "Estudio", "Deporte", "Ocio", "Reunión", "Salud", "Viaje", "Cultural", "Trabajo", "Tarea", "Personal"};
        for (String cat : cats) {
            Button btn = new Button(cat);
            btn.setStyle("-fx-background-color: -color-bg-panel; -fx-text-fill: -color-text-main; -fx-background-radius: 20; -fx-padding: 6 20; -fx-border-color: -color-border; -fx-cursor: hand;");
            
            btn.setOnAction(e -> {
                for(var node : filtros.getChildren()) {
                    node.setStyle("-fx-background-color: -color-bg-panel; -fx-text-fill: -color-text-main; -fx-background-radius: 20; -fx-padding: 6 20; -fx-border-color: -color-border; -fx-cursor: hand;");
                }
                btn.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 20; -fx-cursor: hand;");
                filtrar(cat, buscador.getText(), comboMaterias.getValue());
            });
            filtros.getChildren().add(btn);
        }
        ((Button)filtros.getChildren().get(0)).setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 20; -fx-cursor: hand;");

        // --- 3. BÚSQUEDA Y MATERIAS ---
        buscador = new TextField();
        buscador.setPromptText("Buscar por nombre...");
        buscador.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 30; -fx-padding: 10 20; -fx-border-color: -color-border; -fx-border-radius: 30; -fx-text-fill: -color-text-main;");
        HBox.setHgrow(buscador, Priority.ALWAYS);
        buscador.textProperty().addListener((o, old, newVal) -> filtrar("Todas", newVal, comboMaterias.getValue()));

        comboMaterias = new ComboBox<>(FXCollections.observableArrayList("Todas", "Cálculo III", "Prog. Web", "Física II", "Base de Datos"));
        comboMaterias.setValue("Todas");
        comboMaterias.setStyle("-fx-background-color: -color-bg-panel; -fx-text-fill: -color-text-main; -fx-background-radius: 20; -fx-border-color: -color-border; -fx-border-radius: 20;");
        comboMaterias.setOnAction(e -> filtrar("Todas", buscador.getText(), comboMaterias.getValue()));

        HBox controlesBusqueda = new HBox(15, buscador, comboMaterias);
        controlesBusqueda.setAlignment(Pos.CENTER_LEFT);

        // --- 4. LISTA DE ACTIVIDADES ---
        listaContenedor = new VBox(15);
        ScrollPane scroll = new ScrollPane(listaContenedor);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        filtrar("Todas", "", "Todas");
        this.getChildren().addAll(header, filtros, controlesBusqueda, scroll);
    }

    private void filtrar(String cat, String busqueda, String mat) {
        listaContenedor.getChildren().clear();
        
        // El controlador nos devuelve la lista ya filtrada y procesada
        List<Actividad> filtradas = controller.getActividadesFiltradas(cat, busqueda, mat);
        
        for (Actividad act : filtradas) {
            VBox card = crearTarjeta(act);
            card.setOnMouseClicked(e -> abrirModalInstrucciones(act));
            listaContenedor.getChildren().add(card);
        }
    }

    private VBox crearTarjeta(Actividad act) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 20; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 2); -fx-cursor: hand; -fx-border-color: transparent; -fx-border-radius: 15;");
        Label lblNom = new Label(act.getNombre());
        lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-text-main;");
        if (act.isAsignadaPorAdmin()) lblNom.setText("🏛 " + act.getNombre()); 
        String horaFormateada = act.getHoraVencimiento().format(timeFormatter);
        Label lblInfo = new Label(horaFormateada + " · " + act.getTipo() + (act.getMateria().equals("General") ? "" : " · " + act.getMateria()));
        lblInfo.setStyle("-fx-text-fill: -color-text-muted;");
        card.getChildren().addAll(lblNom, lblInfo);

        // --- LÓGICA DE ARRASTRE AL CHATBOT ---
        card.setOnDragDetected(evt -> {
            javafx.scene.input.Dragboard db = card.startDragAndDrop(javafx.scene.input.TransferMode.COPY);
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            
            // Le pedimos al controlador que nos arme el texto
            content.putString(controller.generarDatosArrastre(act));
            
            db.setContent(content);
            evt.consume();
        });

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 20; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 4); -fx-cursor: hand; -fx-border-color: -color-accent; -fx-border-radius: 15;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: -color-bg-panel; -fx-padding: 20; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 2); -fx-cursor: hand; -fx-border-color: transparent; -fx-border-radius: 15;"));
        return card;
    }

    private void abrirModalNuevaActividad() {
        Stage modal = new Stage(); 
        modal.setTitle("Nueva actividad");
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: -color-bg-base; -fx-font-size: 14px;");

        Label lblTitle = new Label("Nueva actividad");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: -color-text-main;");
        Label lblSub = new Label("Completa los campos y guarda");
        lblSub.setStyle("-fx-text-fill: -color-text-muted;");

        VBox groupTitle = new VBox(5, new Label("Título *") {{ setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;"); }});
        TextField txtTitulo = new TextField();
        txtTitulo.setPromptText("Ej: Clase de natación");
        txtTitulo.setStyle("-fx-background-color: -color-bg-panel; -fx-text-fill: -color-text-main; -fx-background-radius: 10; -fx-padding: 12; -fx-border-color: -color-border; -fx-border-radius: 10;");
        groupTitle.getChildren().add(txtTitulo);

        VBox groupCat = new VBox(5, new Label("Categoría") {{ setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;"); }});
        ComboBox<String> comboCat = new ComboBox<>(FXCollections.observableArrayList("📖 Estudio", "🏃 Deporte", "🎮 Ocio", "👥 Reunión", "🏥 Salud", "✈ Viaje", "🎵 Cultural", "💼 Trabajo", "📝 Tarea", "⭐ Personal"));
        comboCat.setValue("📖 Estudio");
        comboCat.setMaxWidth(Double.MAX_VALUE);
        comboCat.setStyle("-fx-background-color: -color-bg-panel; -fx-text-fill: -color-text-main; -fx-border-color: -color-border; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 5;");
        groupCat.getChildren().add(comboCat);

        HBox rowHoras = new HBox(30);
        VBox gInicio = new VBox(5, new Label("Hora entrega") {{ setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;"); }}, new ComboBox<String>(FXCollections.observableArrayList("09:00","10:00","11:00", "12:00", "15:00", "18:00")) {{ setValue("09:00"); setStyle("-fx-background-color: -color-bg-panel; -fx-text-fill: -color-text-main; -fx-border-color: -color-border;"); }});
        rowHoras.getChildren().addAll(gInicio);

        HBox rowButtons = new HBox(15);
        rowButtons.setAlignment(Pos.CENTER);
        Button btnGuardar = new Button("💾 Guardar");
        btnGuardar.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 30; -fx-cursor: hand;");
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-border-color: -color-accent; -fx-border-radius: 10; -fx-text-fill: -color-accent; -fx-padding: 12 30; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> modal.close());

        btnGuardar.setOnAction(e -> {
            String titulo = txtTitulo.getText();
            String catCompleta = comboCat.getValue();
            String horaStr = ((ComboBox<String>)gInicio.getChildren().get(1)).getValue();
            
            // Le pedimos al controlador que guarde. Nos dice si tuvo éxito o no.
            boolean guardado = controller.guardarNuevaActividad(titulo, catCompleta, horaStr);
            
            if (guardado) {
                filtrar("Todas", "", "Todas"); 
                modal.close();
            } else {
                txtTitulo.setStyle("-fx-border-color: red; -fx-background-color: -color-bg-panel; -fx-border-radius: 10; -fx-padding: 12;");
            }
        });
        rowButtons.getChildren().addAll(btnGuardar, btnCancelar);

        root.getChildren().addAll(lblTitle, lblSub, groupTitle, groupCat, rowHoras, rowButtons);
        Scene scene = new Scene(root, 450, 500);
        if(this.getScene() != null) scene.getStylesheets().addAll(this.getScene().getStylesheets()); 
        modal.setScene(scene);
        modal.show();
    }

    private void abrirModalInstrucciones(Actividad act) {
        Stage modal = new Stage();
        modal.setTitle("Detalles de Actividad");
        modal.initModality(Modality.APPLICATION_MODAL);
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: -color-bg-base; -fx-font-size: 14px;");

        Label lblTitulo = new Label(act.getNombre());
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: -color-text-main; -fx-wrap-text: true;");
        
        String horaFormateada = act.getHoraVencimiento().format(timeFormatter);
        Label lblInfo = new Label(horaFormateada + " · " + act.getTipo() + " · " + act.getMateria());
        lblInfo.setStyle("-fx-text-fill: -color-accent; -fx-font-weight: bold;");

        Label lblDescTitulo = new Label("Instrucciones / Detalles:");
        lblDescTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-main;");
        Label lblDesc = new Label(act.getDescripcion());
        lblDesc.setStyle("-fx-text-fill: -color-text-muted; -fx-wrap-text: true;");
        
        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_LEFT);

        Button btnSubir = new Button("Marcar como entregado ✓");
        btnSubir.setStyle("-fx-background-color: -color-accent; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-background-radius: 10; -fx-cursor: hand;");
        btnSubir.setOnAction(e -> { 
            controller.marcarComoEntregado(act); 
            modal.close(); 
            filtrar("Todas", buscador.getText(), comboMaterias.getValue()); 
        });
        
        Button btnEliminar = new Button("🗑 Eliminar Tarea");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-padding: 12 25; -fx-cursor: hand;");
        
        btnEliminar.setOnAction(e -> {
            boolean seBorro = controller.eliminarActividad(act);
            
            if (seBorro) {
                filtrar("Todas", buscador.getText(), comboMaterias.getValue());
                modal.close();
            } else {
                lblDescTitulo.setText("⚠️ Bloqueado: No tienes permiso para borrar esta actividad (Asignada por el Docente/Admin).");
                lblDescTitulo.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold;");
                btnEliminar.setDisable(true);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        botones.getChildren().addAll(btnSubir, spacer, btnEliminar);

        root.getChildren().addAll(lblTitulo, lblInfo, new Separator(), lblDescTitulo, lblDesc, new Region(), botones);
        VBox.setVgrow(root.getChildren().get(5), Priority.ALWAYS); 
        
        Scene scene = new Scene(root, 500, 350);
        if(this.getScene() != null) scene.getStylesheets().addAll(this.getScene().getStylesheets());
        modal.setScene(scene);
        modal.show();
    }
}