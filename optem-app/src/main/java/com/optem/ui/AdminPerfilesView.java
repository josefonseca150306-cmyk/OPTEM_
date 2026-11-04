package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.DashboardController;
import com.optem.logic.NotificacionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AdminPerfilesView {

    private final DashboardController controller = new DashboardController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;

    public VBox getView() {
        leerPreferencias();
        VBox layoutMain = new VBox(25);
        layoutMain.setPadding(new Insets(30, 40, 30, 40));
        layoutMain.setStyle("-fx-background-color: transparent;");

        // Cabecera
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(5);
        Label titulo = new Label("👤 Gestión de Perfiles y Usuarios");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Administra cuentas de estudiantes, maestros y personal administrativo.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnNuevo = new Button("➕ Crear Usuario");
        btnNuevo.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnNuevo.setOnAction(e -> NotificacionManager.mostrarNotificacion("Módulo Perfiles", "Abriendo formulario de creación de usuario...", "INFO"));
        header.getChildren().addAll(titleBox, spacer, btnNuevo);

        // Barra de Búsqueda y Filtros
        HBox filtrosBox = new HBox(15);
        filtrosBox.setAlignment(Pos.CENTER_LEFT);
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("🔍 Buscar por nombre, correo o matrícula...");
        txtBuscar.setPrefWidth(350);
        txtBuscar.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-text-fill: " + colorTextMain + "; -fx-padding: 10;");
        
        ComboBox<String> cbRol = new ComboBox<>();
        cbRol.getItems().addAll("Todos los Roles", "Estudiantes", "Maestros", "Administradores");
        cbRol.setValue("Todos los Roles");
        cbRol.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-text-fill: " + colorTextMain + ";");
        
        filtrosBox.getChildren().addAll(txtBuscar, cbRol);

        // Lista de Usuarios
        VBox listaUsuarios = new VBox(10);
        listaUsuarios.getChildren().addAll(
            crearTarjetaUsuario("Jose Antonio Fonseca", "jfonsecal001@alumno.uaemex.mx", "Estudiante", "Activo"),
            crearTarjetaUsuario("Ing. Ricardo Goméz", "rgomez@uaemex.mx", "Maestro", "Activo"),
            crearTarjetaUsuario("Admin Sistemas", "admin@optem.com", "Administrador", "Activo"),
            crearTarjetaUsuario("Daniela Sanchez", "dsanchez@alumno.uaemex.mx", "Estudiante", "Suspendido")
        );

        ScrollPane scroll = new ScrollPane(listaUsuarios);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        layoutMain.getChildren().addAll(header, filtrosBox, scroll);
        return layoutMain;
    }

    private void leerPreferencias() {
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        String correoActual = prefs.get("LAST_ACTIVE_ACCOUNT", "");
        boolean isDark = prefs.getBoolean("PREF_DARK_MODE_" + correoActual, false);
        colorFuerte = prefs.get("PREF_ACCENT_COLOR_" + correoActual, "#3D5A4D");
        colorBgBase = isDark ? "#121212" : "#f1f5f9"; colorBgPanel = isDark ? "#1e1e1e" : "#ffffff";
        colorTextMain = isDark ? "#f8f9fa" : "#1e293b"; colorTextMuted = isDark ? "#94a3b8" : "#64748b";
        colorBorder = isDark ? "#333333" : "#e2e8f0";
    }

    private HBox crearTarjetaUsuario(String nombre, String correo, String rol, String estado) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");

        Circle avatar = new Circle(20, Color.web(colorFuerte, 0.2));
        Label lblInicial = new Label(nombre.substring(0, 1));
        lblInicial.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorFuerte + ";");
        javafx.scene.layout.StackPane iconBox = new javafx.scene.layout.StackPane(avatar, lblInicial);

        VBox infoBox = new VBox(3);
        infoBox.setPrefWidth(250);
        Label lblNom = new Label(nombre);
        lblNom.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 14px;");
        Label lblCorr = new Label(correo);
        lblCorr.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");
        infoBox.getChildren().addAll(lblNom, lblCorr);

        Label lblRol = new Label(rol);
        lblRol.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-size: 12px;");
        lblRol.setPrefWidth(100);

        Label lblEstado = new Label(estado);
        String colorEstado = estado.equals("Activo") ? "#10b981" : "#ef4444";
        lblEstado.setStyle("-fx-text-fill: " + colorEstado + "; -fx-font-weight: bold; -fx-font-size: 12px;");
        lblEstado.setPrefWidth(80);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = new Button("✏️ Editar");
        btnEdit.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-text-fill: " + colorTextMain + "; -fx-cursor: hand;");
        
        Button btnSuspender = new Button(estado.equals("Activo") ? "🚫 Suspender" : "✅ Activar");
        btnSuspender.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorEstado + "; -fx-border-radius: 6; -fx-text-fill: " + colorEstado + "; -fx-cursor: hand;");

        card.getChildren().addAll(iconBox, infoBox, lblRol, lblEstado, spacer, btnEdit, btnSuspender);
        return card;
    }
}