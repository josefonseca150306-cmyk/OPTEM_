package com.optem.ui;

import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import com.optem.controllers.ReunionesController;
import com.optem.logic.NotificacionManager;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ReunionesView {

    private final ReunionesController controller = new ReunionesController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    private Stage stagePrincipal;
    
    private VBox panelResultadosBusqueda;
    private VBox panelClasesEnVivo;

    public VBox getView() {
        leerPreferencias();

        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("Centro de Comunicaciones Optem");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        
        Label subtitulo = new Label("Videollamadas grupales, asesorías y mensajes directos integrados.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacerHeader = new Region(); 
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        Button btnReunionRapida = new Button("➕ Iniciar Reunión Rápida");
        btnReunionRapida.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnReunionRapida.setOnAction(e -> abrirSalaDeLlamada("Reunión Rápida", "Sala Personal"));

        header.getChildren().addAll(titleBox, spacerHeader, btnReunionRapida);

        HBox bodySplit = new HBox(30);
        VBox.setVgrow(bodySplit, Priority.ALWAYS);

        VBox colDirectorio = new VBox(15);
        colDirectorio.setPrefWidth(400);
        colDirectorio.setPadding(new Insets(20));
        colDirectorio.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

        Label lblDirectorio = new Label("🔍 Buscar Contactos");
        lblDirectorio.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");
        
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por nombre o matrícula...");
        txtBuscar.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");
        
        panelResultadosBusqueda = new VBox(10);
        
        txtBuscar.textProperty().addListener((obs, oldV, newV) -> actualizarResultadosBusqueda(newV));
        
        ScrollPane scrollResultados = new ScrollPane(panelResultadosBusqueda);
        scrollResultados.setFitToWidth(true);
        scrollResultados.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollResultados, Priority.ALWAYS);
        
        colDirectorio.getChildren().addAll(lblDirectorio, txtBuscar, scrollResultados);

        VBox colGrupos = new VBox(15);
        HBox.setHgrow(colGrupos, Priority.ALWAYS);
        
        Label lblClases = new Label("📡 Salas de Clase Activas");
        lblClases.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 10 0;");
        lblClases.setMaxWidth(Double.MAX_VALUE);

        panelClasesEnVivo = new VBox(15);
        cargarSalasDeClase();
        
        ScrollPane scrollClases = new ScrollPane(panelClasesEnVivo);
        scrollClases.setFitToWidth(true);
        scrollClases.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollClases, Priority.ALWAYS);

        colGrupos.getChildren().addAll(lblClases, scrollClases);

        bodySplit.getChildren().addAll(colDirectorio, colGrupos);

        layoutMain.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) newS.windowProperty().addListener((o, oldW, newW) -> { if(newW != null) stagePrincipal = (Stage) newW; });
        });

        layoutMain.getChildren().addAll(header, bodySplit);
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

    private void actualizarResultadosBusqueda(String query) {
        panelResultadosBusqueda.getChildren().clear();
        
        if (query == null || query.trim().isEmpty()) {
            Label lblIndicacion = new Label("Escribe un nombre para buscar en el directorio de la universidad.");
            lblIndicacion.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-wrap-text: true; -fx-font-size: 12px;");
            panelResultadosBusqueda.getChildren().add(lblIndicacion);
            return;
        }

        List<ReunionesController.UsuarioDirectorio> resultados = controller.buscarUsuarios(query);
        
        if (resultados.isEmpty()) {
            Label lblVacio = new Label("No se encontró ningún usuario con ese nombre o matrícula.");
            lblVacio.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");
            panelResultadosBusqueda.getChildren().add(lblVacio);
            return;
        }

        for (ReunionesController.UsuarioDirectorio usr : resultados) {
            panelResultadosBusqueda.getChildren().add(crearTarjetaUsuario(usr));
        }
    }

    private HBox crearTarjetaUsuario(ReunionesController.UsuarioDirectorio usr) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: " + colorBgBase + "; -fx-background-radius: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");

        Circle avatar = new Circle(20, Color.web(colorFuerte, 0.2));
        Label inicial = new Label(usr.nombre.substring(0, 1).toUpperCase());
        inicial.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorFuerte + ";");
        StackPane avatarBox = new StackPane(avatar, inicial);
        
        Circle puntoEstado = new Circle(5);
        if (usr.estado.equals("Conectado")) puntoEstado.setFill(Color.web("#388E3C"));
        else if (usr.estado.equals("Ocupado")) puntoEstado.setFill(Color.web("#D32F2F"));
        else puntoEstado.setFill(Color.GRAY);
        
        StackPane avatarConEstado = new StackPane(avatarBox, puntoEstado);
        StackPane.setAlignment(puntoEstado, Pos.BOTTOM_RIGHT);

        VBox infoBox = new VBox(2);
        Label lblNombre = new Label(usr.nombre);
        lblNombre.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 14px;");
        Label lblDetalle = new Label(usr.rol + " • " + usr.matricula);
        lblDetalle.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 11px;");
        infoBox.getChildren().addAll(lblNombre, lblDetalle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnChat = new Button("💬");
        btnChat.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-text-fill: " + colorFuerte + ";");
        btnChat.setOnAction(e -> NotificacionManager.mostrarNotificacion("Chat Abierto", "Iniciando conversación con " + usr.nombre, "INFO"));

        Button btnLlamar = new Button("📞");
        btnLlamar.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; -fx-text-fill: #388E3C;");
        btnLlamar.setOnAction(e -> abrirSalaDeLlamada("Llamada Directa", usr.nombre));

        card.getChildren().addAll(avatarConEstado, infoBox, spacer, btnChat, btnLlamar);
        
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + colorFuerte + "10; -fx-background-radius: 8; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 8;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + colorBgBase + "; -fx-background-radius: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;"));

        return card;
    }

    private void cargarSalasDeClase() {
        panelClasesEnVivo.getChildren().clear();
        List< Map < String , String >> clases = controller.getGruposParaLlamada();

        if (clases.isEmpty()) {
            Label lblVacio = new Label("No estás inscrito en ningún grupo para mostrar salas de clase.");
            lblVacio.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
            panelClasesEnVivo.getChildren().add(lblVacio);
            return;
        }

        for (Map<String, String> clase : clases) {
            HBox card = new HBox(20);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(20));
            card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

            Circle circle = new Circle(25, Color.web(colorFuerte, 0.15));
            Label icon = new Label(clase.get("nombre").substring(0,1));
            icon.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: " + colorFuerte + ";");
            StackPane iconPane = new StackPane(circle, icon);

            VBox info = new VBox(5);
            Label lblMateria = new Label(clase.get("nombre"));
            lblMateria.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");
            Label lblProfesor = new Label("Titular: " + clase.get("profesor"));
            lblProfesor.setStyle("-fx-font-size: 13px; -fx-text-fill: " + colorTextMuted + ";");
            
            Label lblSala = new Label("Sala Permanente: Grupo " + clase.get("grupo"));
            lblSala.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + colorFuerte + "; -fx-padding: 3 8; -fx-background-radius: 10;");
            
            info.getChildren().addAll(lblMateria, lblProfesor, lblSala);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button btnUnirse = new Button("📹 Unirse al Aula");
            btnUnirse.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");
            btnUnirse.setOnAction(e -> abrirSalaDeLlamada(clase.get("nombre"), "Clase con " + clase.get("profesor")));

            card.getChildren().addAll(iconPane, info, spacer, btnUnirse);

            ScaleTransition stIn = new ScaleTransition(Duration.millis(150), card); stIn.setToX(1.02); stIn.setToY(1.02);
            ScaleTransition stOut = new ScaleTransition(Duration.millis(150), card); stOut.setToX(1.0); stOut.setToY(1.0);
            card.setOnMouseEntered(e -> stIn.playFromStart());
            card.setOnMouseExited(e -> stOut.playFromStart());

            panelClasesEnVivo.getChildren().add(card);
        }
    }

    private void abrirSalaDeLlamada(String tituloReunion, String subtitulo) {
        Stage salaStage = new Stage();
        if (stagePrincipal != null) salaStage.initOwner(stagePrincipal);
        salaStage.initStyle(StageStyle.UNDECORATED);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2;");

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        Label lblSeguridad = new Label("🔒 Cifrado de extremo a extremo");
        lblSeguridad.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        Region spTop = new Region(); HBox.setHgrow(spTop, Priority.ALWAYS);
        
        Button btnMinimizar = new Button("—");
        btnMinimizar.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
        btnMinimizar.setOnAction(e -> salaStage.setIconified(true));
        
        topBar.getChildren().addAll(lblSeguridad, spTop, btnMinimizar);
        root.setTop(topBar);

        VBox centroGrid = new VBox(10);
        centroGrid.setAlignment(Pos.CENTER);
        
        Circle avatarLlamada = new Circle(60, Color.web(colorFuerte));
        Label lblAvatar = new Label(tituloReunion.substring(0,1));
        lblAvatar.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: white;");
        StackPane videoPlaceholder = new StackPane(avatarLlamada, lblAvatar);
        
        Label lblTitulo = new Label(tituloReunion);
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label lblSub = new Label(subtitulo);
        lblSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8;");
        
        centroGrid.getChildren().addAll(videoPlaceholder, lblTitulo, lblSub);
        root.setCenter(centroGrid);

        HBox bottomBar = new HBox(20);
        bottomBar.setPadding(new Insets(20));
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setStyle("-fx-background-color: rgba(0,0,0,0.8);");

        Button btnMic = crearBotonControlLlamada("🎤", "Silenciar", "#334155");
        Button btnCam = crearBotonControlLlamada("📷", "Detener Video", "#334155");
        Button btnPantalla = crearBotonControlLlamada("💻", "Compartir", "#334155");
        Button btnColgar = crearBotonControlLlamada("📞", "Salir", "#ef4444"); 
        btnColgar.setOnAction(e -> salaStage.close());

        bottomBar.getChildren().addAll(btnMic, btnCam, btnPantalla, btnColgar);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 900, 600);
        salaStage.setScene(scene);
        
        if (stagePrincipal != null) {
            salaStage.setX(stagePrincipal.getX() + (stagePrincipal.getWidth() - scene.getWidth()) / 2);
            salaStage.setY(stagePrincipal.getY() + (stagePrincipal.getHeight() - scene.getHeight()) / 2);
        }
        
        salaStage.show();
    }

    private Button crearBotonControlLlamada(String icono, String texto, String colorBg) {
        Button btn = new Button(icono + " " + texto);
        btn.setStyle("-fx-background-color: " + colorBg + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-background-radius: 20; -fx-cursor: hand;");
        return btn;
    }
}