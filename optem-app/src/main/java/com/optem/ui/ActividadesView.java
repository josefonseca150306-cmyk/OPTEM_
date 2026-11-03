package com.optem.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.optem.logic.ActividadManager;
import com.optem.models.Actividad;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ActividadesView extends VBox {

    private TableView<Actividad> tablaActividades;

    public ActividadesView() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Label lblTitulo = new Label("🎯 Gestión de Actividades y Tareas");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Dividimos la pantalla: Izquierda (Formulario) | Derecha (Tabla)
        HBox contenedorPrincipal = new HBox(30);
        VBox.setVgrow(contenedorPrincipal, Priority.ALWAYS);

        // ==========================================
        // 1. FORMULARIO DE NUEVA ACTIVIDAD
        // ==========================================
        VBox panelFormulario = new VBox(15);
        panelFormulario.setPrefWidth(350);
        panelFormulario.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 20;");

        Label lblForm = new Label("Nueva Actividad");
        lblForm.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la tarea (Ej. Proyecto BD)");

        ComboBox<String> cmbTipo = new ComboBox<>(FXCollections.observableArrayList(
                "Estudio", "Tarea", "Reunión", "Deporte", "Cultural", "Personal"
        ));
        cmbTipo.setPromptText("Tipo de actividad");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);

        DatePicker dpFecha = new DatePicker(LocalDate.now());
        dpFecha.setMaxWidth(Double.MAX_VALUE);

        // Selector de Hora simple
        HBox boxHora = new HBox(10);
        boxHora.setAlignment(Pos.CENTER_LEFT);
        ComboBox<Integer> cmbHora = new ComboBox<>();
        for (int i = 0; i < 24; i++) cmbHora.getItems().add(i);
        cmbHora.setValue(12);
        
        ComboBox<Integer> cmbMinuto = new ComboBox<>();
        for (int i = 0; i < 60; i+=5) cmbMinuto.getItems().add(i);
        cmbMinuto.setValue(0);
        
        boxHora.getChildren().addAll(new Label("Hora (24h):"), cmbHora, new Label(":"), cmbMinuto);

        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción o detalles breves...");
        txtDescripcion.setPrefRowCount(3);

        Button btnGuardar = new Button("💾 Guardar Actividad");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);
        btnGuardar.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");

        Label lblMensaje = new Label();
        lblMensaje.setStyle("-fx-text-fill: green;");

        panelFormulario.getChildren().addAll(
                lblForm, 
                new Label("Título:"), txtNombre, 
                new Label("Categoría:"), cmbTipo, 
                new Label("Fecha límite:"), dpFecha, 
                boxHora, 
                new Label("Detalles:"), txtDescripcion, 
                btnGuardar, lblMensaje
        );

        // ==========================================
        // 2. TABLA DE ACTIVIDADES (VISTA GLOBAL)
        // ==========================================
        VBox panelTabla = new VBox(10);
        HBox.setHgrow(panelTabla, Priority.ALWAYS);

        tablaActividades = new TableView<>();
        VBox.setVgrow(tablaActividades, Priority.ALWAYS);

        TableColumn<Actividad, String> colNombre = new TableColumn<>("Actividad");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Actividad, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<Actividad, LocalDate> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        TableColumn<Actividad, LocalTime> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(new PropertyValueFactory<>("horaVencimiento"));

        tablaActividades.getColumns().addAll(colNombre, colTipo, colFecha, colHora);
        actualizarTabla(dpFecha.getValue()); // Cargar las de hoy por defecto

        Button btnEliminar = new Button("🗑 Eliminar Seleccionada");
        
        // Evento para filtrar la tabla cuando cambies la fecha en el calendario
        dpFecha.setOnAction(e -> actualizarTabla(dpFecha.getValue()));

        panelTabla.getChildren().addAll(new Label("Actividades registradas para la fecha seleccionada:"), tablaActividades, btnEliminar);

        contenedorPrincipal.getChildren().addAll(panelFormulario, panelTabla);
        this.getChildren().addAll(lblTitulo, contenedorPrincipal);

        // ==========================================
        // 3. LOGICA Y FUNCIONALIDAD DE LOS BOTONES
        // ==========================================

        btnGuardar.setOnAction(e -> {
            if (txtNombre.getText().isEmpty() || cmbTipo.getValue() == null) {
                lblMensaje.setStyle("-fx-text-fill: red;");
                lblMensaje.setText("⚠️ El título y el tipo son obligatorios.");
                return;
            }

            // Crear el objeto y meter los datos del formulario (CONSTRUCTOR NUEVO)
            Actividad nuevaAct = new Actividad(
                UUID.randomUUID().toString(),
                txtNombre.getText(),
                cmbTipo.getValue(),
                txtDescripcion.getText(),
                dpFecha.getValue(),
                LocalTime.of(cmbHora.getValue(), cmbMinuto.getValue()),
                "General"
            );

            // Guardar en la memoria global (Manager)
            ActividadManager.getInstance().agregarActividad(nuevaAct);

            // Refrescar la tabla, limpiar campos y avisar
            actualizarTabla(dpFecha.getValue());
            txtNombre.clear();
            txtDescripcion.clear();
            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("✅ Actividad guardada correctamente.");
        });

        btnEliminar.setOnAction(e -> {
            Actividad seleccionada = tablaActividades.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                ActividadManager.getInstance().getAllActividades().remove(seleccionada);
                actualizarTabla(dpFecha.getValue());
                lblMensaje.setText("🗑 Actividad eliminada.");
            }
        });
    }

    // Método para refrescar la tabla
    private void actualizarTabla(LocalDate fecha) {
        tablaActividades.getItems().clear();
        // Llama a la memoria central para traer las actividades de ese día
        tablaActividades.getItems().addAll(ActividadManager.getInstance().getActividadesDelDia(fecha));
    }
}