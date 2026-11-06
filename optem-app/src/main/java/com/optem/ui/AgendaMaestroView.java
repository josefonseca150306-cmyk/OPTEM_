package com.optem.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.prefs.Preferences;

import com.optem.controllers.AjustesController;
import com.optem.controllers.DashboardController;
import com.optem.logic.NotificacionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class AgendaMaestroView {

    private final DashboardController controller = new DashboardController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    
    private VBox timelineContainer;
    private HBox selectorDias;
    private LocalDate fechaSeleccionada = LocalDate.now();

    public VBox getView() {
        leerPreferencias();

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("📅 Agenda Docente");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Gestiona tus horarios, asesorías y cierres de actividades.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        DatePicker dpCalendario = new DatePicker(fechaSeleccionada);
        dpCalendario.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-control-inner-background: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");
        dpCalendario.setOnAction(e -> {
            if (dpCalendario.getValue() != null) {
                fechaSeleccionada = dpCalendario.getValue();
                actualizarVistaDiasYEventos();
            }
        });

        Button btnNuevoEvento = new Button("➕ Nuevo Evento");
        btnNuevoEvento.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnNuevoEvento.setOnAction(e -> abrirModalNuevoEvento(layoutMain.getScene().getWindow()));

        header.getChildren().addAll(titleBox, spacer, dpCalendario, btnNuevoEvento);

        selectorDias = new HBox(15);
        selectorDias.setAlignment(Pos.CENTER);
        selectorDias.setPadding(new Insets(15));
        selectorDias.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 2, 2);");

        timelineContainer = new VBox(0);
        timelineContainer.setPadding(new Insets(20, 0, 20, 0));
        
        ScrollPane scrollTimeline = new ScrollPane(timelineContainer);
        scrollTimeline.setFitToWidth(true);
        scrollTimeline.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollTimeline, Priority.ALWAYS);

        actualizarVistaDiasYEventos();

        layoutMain.getChildren().addAll(header, selectorDias, scrollTimeline);
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

    private void actualizarVistaDiasYEventos() {
        selectorDias.getChildren().clear();
        for (int i = -3; i <= 3; i++) {
            LocalDate dia = fechaSeleccionada.plusDays(i);
            selectorDias.getChildren().add(crearBotonDia(dia, dia.equals(fechaSeleccionada)));
        }

        timelineContainer.getChildren().clear();
        
        if (fechaSeleccionada.equals(LocalDate.now())) {
            timelineContainer.getChildren().addAll(
                crearEventoTimeline("07:00", "Clase: Bases de Datos Avanzadas", "Grupo S5 • Aula H-104", colorFuerte, "clase"),
                crearEventoTimeline("10:00", "Reunión de Academia", "Plataforma Microsoft Teams", "#64748b", "reunion"),
                crearEventoTimeline("13:00", "Clase: Ingeniería de Software", "Grupo S6 • Laboratorio 2", colorFuerte, "clase"),
                crearEventoTimeline("16:30", "Asesoría: Daniela Sanchez", "Revisión de proyecto de titulación", "#3b82f6", "asesoria"),
                crearEventoTimeline("23:59", "CIERRE DE ENTREGA: Práctica 3 - UML", "64 alumnos asignados. Las entregas se bloquearán a esta hora.", "#ef4444", "tarea")
            );
        } else if (fechaSeleccionada.isBefore(LocalDate.now())) {
            timelineContainer.getChildren().addAll(
                crearEventoTimeline("09:00", "Clase Anterior", "Clase ya impartida en este día.", "#10b981", "clase"),
                crearEventoTimeline("14:00", "Asesoría Completada", "Se resolvió la duda del alumno.", "#10b981", "asesoria")
            );
        } else {
            Label lblVacio = new Label("No hay eventos programados para este día aún.");
            lblVacio.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px; -fx-padding: 20;");
            timelineContainer.getChildren().add(lblVacio);
        }
    }

    private VBox crearBotonDia(LocalDate fecha, boolean activo) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(90);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-radius: 8; -fx-cursor: hand; -fx-background-color: " + (activo ? colorFuerte : "transparent") + ";");

        String nombreDia = fecha.format(DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es-ES"))).toUpperCase();
        Label lblDia = new Label(nombreDia);
        lblDia.setStyle("-fx-font-weight: bold; -fx-font-size: 1.1em; -fx-text-fill: " + (activo ? "white" : colorTextMuted) + ";");

        Label lblNum = new Label(String.valueOf(fecha.getDayOfMonth()));
        lblNum.setStyle("-fx-font-weight: bold; -fx-font-size: 1.6em; -fx-text-fill: " + (activo ? "white" : colorTextMain) + ";");

        box.getChildren().addAll(lblDia, lblNum);

        box.setOnMouseClicked(e -> {
            fechaSeleccionada = fecha;
            actualizarVistaDiasYEventos();
        });

        if (!activo) {
            box.setOnMouseEntered(e -> box.setStyle("-fx-background-radius: 8; -fx-cursor: hand; -fx-background-color: " + colorBgBase + ";"));
            box.setOnMouseExited(e -> box.setStyle("-fx-background-radius: 8; -fx-cursor: hand; -fx-background-color: transparent;"));
        }

        return box;
    }

    private HBox crearEventoTimeline(String hora, String titulo, String descripcion, String colorEvento, String tipo) {
        HBox row = new HBox();
        row.setAlignment(Pos.TOP_LEFT);

        Label lblHora = new Label(hora);
        lblHora.setPrefWidth(60);
        lblHora.setAlignment(Pos.CENTER_RIGHT);
        lblHora.setStyle("-fx-font-weight: bold; -fx-font-size: 1.1em; -fx-text-fill: " + colorTextMain + "; -fx-padding: 0 15 0 0;");

        VBox separador = new VBox();
        separador.setAlignment(Pos.TOP_CENTER);
        
        Circle nodo = new Circle(7, Color.web(colorBgPanel));
        nodo.setStroke(Color.web(colorEvento));
        nodo.setStrokeWidth(3);

        Line linea = new Line(0, 0, 0, 60); 
        linea.setStroke(Color.web(colorBorder));
        linea.setStrokeWidth(2);

        separador.getChildren().addAll(nodo, linea);

        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefWidth(500);
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 10; -fx-cursor: hand;");
        HBox.setMargin(card, new Insets(0, 0, 20, 20)); 
        
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 10; -fx-border-color: " + colorEvento + "; -fx-border-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 2, 2); -fx-cursor: hand;");
            nodo.setFill(Color.web(colorEvento)); 
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 10; -fx-cursor: hand;");
            nodo.setFill(Color.web(colorBgPanel));
        });

        card.setOnMouseClicked(e -> abrirDetalleEvento(titulo, descripcion, hora, tipo, colorEvento, row.getScene().getWindow()));

        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 1.1em; -fx-text-fill: " + colorTextMain + ";");
        Label lblDesc = new Label(descripcion);
        lblDesc.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 0.9em; -fx-wrap-text: true;");

        Label badge = new Label();
        badge.setStyle("-fx-font-size: 0.8em; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 5;");
        if (tipo.equals("clase")) { badge.setText("🎓 Clase"); badge.setStyle(badge.getStyle() + "-fx-background-color: " + colorEvento + ";"); }
        else if (tipo.equals("tarea")) { badge.setText("⚠️ Límite de Entrega"); badge.setStyle(badge.getStyle() + "-fx-background-color: " + colorEvento + ";"); }
        else if (tipo.equals("asesoria")) { badge.setText("👤 Asesoría"); badge.setStyle(badge.getStyle() + "-fx-background-color: " + colorEvento + ";"); }
        else { badge.setText("📌 General"); badge.setStyle(badge.getStyle() + "-fx-background-color: " + colorEvento + ";"); }

        card.getChildren().addAll(badge, lblTit, lblDesc);

        row.getChildren().addAll(lblHora, separador, card);
        return row;
    }

    private void abrirModalNuevoEvento(Window owner) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        
        String fontPref = new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal");
        String fontSize = fontPref.equals("Pequeño") ? "12px" : fontPref.equals("Grande") ? "17px" : "14px";
        
        layout.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: " + fontSize + ";");

        Label lblTitulo = new Label("Programar Nuevo Evento");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 1.5em; -fx-text-fill: " + colorTextMain + ";");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Título (Ej. Asesoría Proyecto)");
        txtNombre.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("🎓 Clase", "👤 Asesoría", "⚠️ Límite de Entrega", "📌 General");
        cbTipo.setValue("📌 General");
        cbTipo.setMaxWidth(Double.MAX_VALUE);
        cbTipo.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        TextField txtHora = new TextField();
        txtHora.setPromptText("Hora (Ej. 14:00)");
        txtHora.setStyle(txtNombre.getStyle());

        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Descripción o detalles...");
        txtDesc.setPrefRowCount(3);
        txtDesc.setStyle("-fx-control-inner-background: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> modal.close());

        Button btnGuardar = new Button("Guardar Evento");
        btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> {
            NotificacionManager.mostrarNotificacion("Agenda Actualizada", "Evento '" + txtNombre.getText() + "' guardado correctamente.", "INFO");
            modal.close();
            actualizarVistaDiasYEventos(); 
        });

        botones.getChildren().addAll(btnCancelar, btnGuardar);
        layout.getChildren().addAll(lblTitulo, txtNombre, cbTipo, txtHora, txtDesc, botones);

        Scene scene = new Scene(layout, 400, 420);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.show();
        modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2);
        modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2);
    }

    private void abrirDetalleEvento(String titulo, String descripcion, String hora, String tipo, String colorEvento, Window owner) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        
        String fontPref = new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal");
        String fontSize = fontPref.equals("Pequeño") ? "12px" : fontPref.equals("Grande") ? "17px" : "14px";
        
        layout.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorEvento + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: " + fontSize + ";");

        HBox header = new HBox();
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 1.4em; -fx-text-fill: " + colorTextMain + ";");
        lblTit.setWrapText(true);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnCerrar = new Button("✕");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 18px; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> modal.close());
        header.getChildren().addAll(lblTit, sp, btnCerrar);

        Label lblInfo = new Label("Programado para las " + hora + " hrs.");
        lblInfo.setStyle("-fx-text-fill: " + colorEvento + "; -fx-font-weight: bold;");

        Label lblDesc = new Label(descripcion);
        lblDesc.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-wrap-text: true;");

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCompletar = new Button("✔ Marcar como Completado");
        btnCompletar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCompletar.setOnAction(e -> {
            NotificacionManager.mostrarNotificacion("¡Actividad Completada!", "Has ganado +50 XP. Revisa tu progreso en la pestaña de Logros.", "INFO");
            modal.close();
        });

        if(tipo.equals("tarea")) {
            btnCompletar.setText("Ir a calificar tareas");
            btnCompletar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
            btnCompletar.setOnAction(e -> {
                NotificacionManager.mostrarNotificacion("Gestor de Tareas", "Redirigiendo al panel de calificación...", "INFO");
                modal.close();
            });
        }

        botones.getChildren().addAll(btnCompletar);
        layout.getChildren().addAll(header, lblInfo, lblDesc, botones);

        Scene scene = new Scene(layout, 400, 250);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.show();
        modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2);
        modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2);
    }
}