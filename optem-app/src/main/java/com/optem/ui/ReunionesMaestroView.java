package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.AjustesController;
import com.optem.controllers.DashboardController;
import com.optem.logic.NotificacionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class ReunionesMaestroView {

    private final DashboardController controller = new DashboardController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    
    public VBox getView() {
        leerPreferencias();

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("🎥 Salas Virtuales (Docente)");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Inicia y administra tus clases en línea, o programa asesorías por videollamada.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNuevaSala = new Button("➕ Programar Reunión");
        btnNuevaSala.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnNuevaSala.setOnAction(e -> abrirModalNuevaReunion(layoutMain.getScene().getWindow()));

        header.getChildren().addAll(titleBox, spacer, btnNuevaSala);

        Label lblSalasHoy = new Label("🔴 Clases de Hoy (Anfitrión)");
        lblSalasHoy.setStyle("-fx-font-weight: bold; -fx-font-size: 1.2em; -fx-text-fill: " + colorTextMain + ";");

        VBox contenedorSalas = new VBox(15);
        
        contenedorSalas.getChildren().addAll(
            crearTarjetaSala("Ingeniería de Software", "Grupo S6", "13:00 - 15:00 hrs", "Microsoft Teams", true),
            crearTarjetaSala("Bases de Datos Avanzadas", "Grupo S5", "07:00 - 09:00 hrs", "Microsoft Teams", false),
            crearTarjetaSala("Asesoría: Daniela Sanchez", "Revisión Proyecto", "16:30 hrs", "Zoom", true)
        );

        ScrollPane scroll = new ScrollPane(contenedorSalas);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        layoutMain.getChildren().addAll(header, lblSalasHoy, scroll);
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

    private HBox crearTarjetaSala(String titulo, String subtitulo, String horario, String plataforma, boolean estaPendiente) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

        StackPane iconBox = new StackPane();
        Circle bgIcon = new Circle(22, Color.web(colorFuerte, 0.15));
        Label lblIcon = new Label(plataforma.contains("Teams") ? "T" : "Z");
        lblIcon.setStyle("-fx-font-weight: bold; -fx-font-size: 1.2em; -fx-text-fill: " + colorFuerte + ";");
        iconBox.getChildren().addAll(bgIcon, lblIcon);

        VBox info = new VBox(5);
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 1.1em; -fx-text-fill: " + colorTextMain + ";");
        Label lblSub = new Label(subtitulo + " | " + plataforma);
        lblSub.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 0.9em;");
        info.getChildren().addAll(lblTit, lblSub);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox accionBox = new VBox(8);
        accionBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblHora = new Label("🕒 " + horario);
        lblHora.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 0.9em; -fx-font-weight: bold;");
        
        Button btnAccion = new Button(estaPendiente ? "📹 Iniciar Clase" : "Clase Finalizada");
        if (estaPendiente) {
            btnAccion.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-cursor: hand;");
            
            btnAccion.setOnAction(e -> {
                NotificacionManager.mostrarNotificacion("Abriendo " + plataforma, "Iniciando sesión como anfitrión para la clase de " + titulo + "...", "INFO");
                btnAccion.setText("Sala Activa");
                btnAccion.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8;");
                abrirAulaVirtualMaestro(titulo, subtitulo, plataforma, btnAccion, card.getScene().getWindow());
            });
            
            card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 2, 2);"));
            card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);"));
        } else {
            btnAccion.setDisable(true);
            btnAccion.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorBorder + "; -fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-border-radius: 8;");
        }

        accionBox.getChildren().addAll(lblHora, btnAccion);
        card.getChildren().addAll(iconBox, info, spacer, accionBox);
        return card;
    }

    private void abrirAulaVirtualMaestro(String materia, String grupo, String plataforma, Button btnTrigger, Window owner) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initModality(Modality.APPLICATION_MODAL);

        BorderPane layout = new BorderPane();
        String fontSize = new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal").equals("Pequeño") ? "12px" : new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal").equals("Grande") ? "17px" : "14px";
        layout.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: " + fontSize + ";");
        layout.setPadding(new Insets(15));

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        Label lblTit = new Label("🔴 " + materia + " (" + plataforma + ")");
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 1.3em; -fx-text-fill: " + colorTextMain + ";");
        
        Label lblHost = new Label("Panel de Anfitrión");
        lblHost.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 5; -fx-font-size: 0.8em; -fx-font-weight: bold;");

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnCerrar = new Button("✕");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 18px; -fx-cursor: hand;");
        btnCerrar.setOnAction(e -> modal.close());
        header.getChildren().addAll(lblTit, lblHost, spacer, btnCerrar);
        layout.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 15, 0));

        StackPane videoArea = new StackPane();
        videoArea.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 8;");
        videoArea.setPrefSize(500, 350);
        Label lblVideoTxt = new Label("Cámaras de los alumnos desactivadas.\n(Vista de Galería)");
        lblVideoTxt.setStyle("-fx-text-fill: #475569; -fx-font-size: 1.2em; -fx-text-alignment: center;");
        videoArea.getChildren().add(lblVideoTxt);
        layout.setCenter(videoArea);

        VBox panelRight = new VBox(10);
        panelRight.setPrefWidth(220);
        panelRight.setPadding(new Insets(0, 0, 0, 15));
        
        Label lblPart = new Label("Participantes (24)");
        lblPart.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        
        VBox lista = new VBox(8);
        lista.getChildren().addAll(
            crearParticipante("Tú (Anfitrión)", true),
            crearParticipante("Jose Antonio Fonseca", false),
            crearParticipante("Daniela Sanchez", false),
            crearParticipante("Alexis Hernandez", false)
        );
        ScrollPane scrollPart = new ScrollPane(lista);
        scrollPart.setFitToWidth(true);
        scrollPart.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPart, Priority.ALWAYS);
        
        Button btnMuteAll = new Button("🔇 Silenciar a todos");
        btnMuteAll.setMaxWidth(Double.MAX_VALUE);
        btnMuteAll.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-cursor: hand;");
        btnMuteAll.setOnAction(e -> NotificacionManager.mostrarNotificacion("Control de Sala", "Todos los micrófonos de los alumnos han sido silenciados.", "INFO"));
        
        panelRight.getChildren().addAll(lblPart, scrollPart, btnMuteAll);
        layout.setRight(panelRight);

        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15, 0, 0, 0));

        Button btnMic = crearBotonControl("🎤 Micrófono");
        Button btnCam = crearBotonControl("📷 Cámara");
        Button btnShare = crearBotonControl("💻 Compartir Pantalla");
        btnShare.setStyle(btnShare.getStyle() + "-fx-text-fill: " + colorFuerte + "; -fx-border-color: " + colorFuerte + ";");
        
        Region fSpacer = new Region(); HBox.setHgrow(fSpacer, Priority.ALWAYS);
        
        Button btnFinalizar = new Button("🔴 Finalizar Clase");
        btnFinalizar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnFinalizar.setOnAction(e -> {
            NotificacionManager.mostrarNotificacion("Clase Finalizada", "Has terminado la sesión para todos. +30 XP de docente.", "INFO");
            btnTrigger.setText("Clase Finalizada");
            btnTrigger.setDisable(true);
            btnTrigger.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorBorder + "; -fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8; -fx-border-radius: 8;");
            modal.close();
        });

        footer.getChildren().addAll(btnMic, btnCam, btnShare, fSpacer, btnFinalizar);
        layout.setBottom(footer);

        Scene scene = new Scene(layout, 850, 550);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.show();
        modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2);
        modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2);
    }
    
    private Button crearBotonControl(String texto) {
        Button b = new Button(texto);
        b.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-padding: 8 15; -fx-cursor: hand;");
        return b;
    }

    private HBox crearParticipante(String nombre, boolean isHost) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Circle dot = new Circle(4, isHost ? Color.web(colorFuerte) : Color.web("#10b981"));
        Label lbl = new Label(nombre);
        lbl.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-size: 0.9em;");
        if(isHost) lbl.setStyle(lbl.getStyle() + "-fx-font-weight: bold;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label mute = new Label(isHost ? "🎤" : "🔇");
        mute.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 0.9em;");
        row.getChildren().addAll(dot, lbl, sp, mute);
        return row;
    }

    private void abrirModalNuevaReunion(Window owner) {
        Stage modal = new Stage();
        modal.initOwner(owner);
        modal.initStyle(StageStyle.UNDECORATED);
        modal.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        String fontSize = new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal").equals("Pequeño") ? "12px" : new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal").equals("Grande") ? "17px" : "14px";
        layout.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: " + fontSize + ";");

        Label lblTitulo = new Label("Programar Videollamada");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 1.5em; -fx-text-fill: " + colorTextMain + ";");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Título de la Reunión...");
        txtNombre.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        ComboBox<String> cbPlataforma = new ComboBox<>();
        cbPlataforma.getItems().addAll("Microsoft Teams", "Zoom", "Google Meet");
        cbPlataforma.setValue("Microsoft Teams");
        cbPlataforma.setMaxWidth(Double.MAX_VALUE);
        cbPlataforma.setStyle("-fx-background-color: " + colorBgBase + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> modal.close());

        Button btnGuardar = new Button("Generar Enlace");
        btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnGuardar.setOnAction(e -> {
            NotificacionManager.mostrarNotificacion("Enlace Generado", "La reunión se ha programado y notificado al grupo seleccionado.", "INFO");
            modal.close();
        });

        botones.getChildren().addAll(btnCancelar, btnGuardar);
        layout.getChildren().addAll(lblTitulo, txtNombre, cbPlataforma, botones);

        Scene scene = new Scene(layout, 400, 300);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.show();
        modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2);
        modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2);
    }
}