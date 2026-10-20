package com.optem.ui;

import com.optem.controllers.LogrosController;

import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LogrosView extends VBox {

    // Instancia del nuevo controlador
    private final LogrosController controller = new LogrosController();

    public LogrosView() {
        this.setStyle("-fx-background-color: transparent;");

        VBox contenido = new VBox(35);
        contenido.setPadding(new Insets(30, 40, 30, 40));
        contenido.setStyle("-fx-background-color: transparent;");

        // --- 1. CABECERA ---
        Label titulo = new Label("⭐ Sistema de Logros y Niveles");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: -color-text-main;");
        Label subtitulo = new Label("Gana XP entregando tareas a tiempo y completando sesiones Pomodoro");
        subtitulo.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 14px;");
        VBox header = new VBox(5, titulo, subtitulo);

        // --- 2. BANNER DE MOTIVACIÓN ANIMADO ---
        HBox banner = new HBox(30);
        banner.setStyle("-fx-background-color: -color-accent-light; -fx-background-radius: 20; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        banner.setAlignment(Pos.CENTER_LEFT);

        Node animacionEscudo = crearAnimacionEscudo();
        banner.getChildren().add(animacionEscudo);

        Label lblBannerText = new Label("¡Sigue así! Cada sesión\nte acerca al siguiente nivel 🚀");
        lblBannerText.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: -color-accent; -fx-wrap-text: true;");
        banner.getChildren().add(lblBannerText);
        HBox.setHgrow(lblBannerText, Priority.ALWAYS);

        // --- 3. ESTADÍSTICAS PRINCIPALES (Datos solicitados al controlador) ---
        HBox statsBox = new HBox(50);
        statsBox.setAlignment(Pos.CENTER);
        
        int xpActual = controller.calcularXpActual();
        int xpMeta = controller.calcularXpMeta();
        int diasRacha = controller.calcularDiasRacha(xpActual, xpMeta);
        double bonoRacha = controller.calcularBonoRacha(diasRacha);
        String nivelActual = controller.obtenerNivelActual(xpActual);
        
        VBox boxXP = crearProgresoXP(xpActual, xpMeta, nivelActual);
        VBox boxRacha = crearRachaDias(diasRacha, bonoRacha);
        
        HBox.setHgrow(boxXP, Priority.ALWAYS);
        HBox.setHgrow(boxRacha, Priority.ALWAYS);
        statsBox.getChildren().addAll(boxXP, boxRacha);

        // --- 4. MAPA DE NIVELES ---
        VBox mapaNiveles = new VBox(15);
        Label lblMapa = new Label("🗺 Mapa de niveles");
        lblMapa.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: -color-text-main;");
        
        mapaNiveles.getChildren().add(lblMapa);
        mapaNiveles.getChildren().addAll(
            crearTarjetaNivel("🌱", "Novato", "0 – 999 XP", nivelActual.equals("Novato")),
            crearTarjetaNivel("⚡", "Intermedio", "1,000 – 2,999 XP", nivelActual.equals("Intermedio")),
            crearTarjetaNivel("🚀", "Avanzado", "3,000 – 5,999 XP", nivelActual.equals("Avanzado")),
            crearTarjetaNivel("👑", "Experto", "6,000 – 9,999 XP", nivelActual.equals("Experto"))
        );

        // --- 5. BONIFICACIONES DE RACHA ---
        VBox bonificaciones = new VBox(25);
        Label lblBonos = new Label("🔥 Bonificaciones de racha");
        lblBonos.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: -color-text-main;");
        
        HBox rowRachas = new HBox(40);
        rowRachas.setAlignment(Pos.CENTER);
        rowRachas.getChildren().addAll(
            crearItemRacha(5, 10),
            crearItemRacha(10, 20),
            crearItemRacha(20, 40),
            crearItemRacha(50, 100)
        );
        bonificaciones.getChildren().addAll(lblBonos, rowRachas);

        contenido.getChildren().addAll(header, banner, statsBox, mapaNiveles, bonificaciones);

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        this.getChildren().add(scroll);
    }

    private Node crearAnimacionEscudo() {
        StackPane contenedorAnimacion = new StackPane();
        contenedorAnimacion.setPrefSize(120, 120);
        contenedorAnimacion.setAlignment(Pos.CENTER);

        Text textIcono = new Text("🛡⭐");
        textIcono.setFont(Font.font("Segoe UI Symbol", FontWeight.BOLD, 45));
        textIcono.setStyle("-fx-fill: -color-accent;");

        RotateTransition rotate = new RotateTransition(Duration.millis(6000), textIcono);
        rotate.setByAngle(15);
        rotate.setCycleCount(Timeline.INDEFINITE);
        rotate.setAutoReverse(true);
        rotate.play();

        contenedorAnimacion.getChildren().addAll(textIcono);
        return contenedorAnimacion;
    }

    private VBox crearProgresoXP(int xpActual, int xpMeta, String nivelActual) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        Group grupoCirculos = new Group();

        Circle anilloFondo = new Circle(0, 0, 60);
        anilloFondo.setFill(Color.TRANSPARENT);
        anilloFondo.setStyle("-fx-stroke: -color-border;");
        anilloFondo.setStrokeWidth(12);

        double porcentaje = xpMeta == 0 ? 0 : Math.min(1.0, (double) xpActual / xpMeta);
        Arc anilloProgreso = new Arc(0, 0, 60, 60, 90, -360 * porcentaje);
        anilloProgreso.setFill(Color.TRANSPARENT);
        anilloProgreso.setStyle("-fx-stroke: -color-accent;");
        anilloProgreso.setStrokeWidth(12);
        anilloProgreso.setStrokeLineCap(StrokeLineCap.ROUND); 

        grupoCirculos.getChildren().addAll(anilloFondo, anilloProgreso);

        StackPane contenedorCirculo = new StackPane(grupoCirculos);
        contenedorCirculo.setMinSize(140, 140);

        Label lblNivel = new Label(nivelActual);
        lblNivel.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-accent;");
        
        Label lblXP = new Label(xpActual + " XP");
        lblXP.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: -color-text-main;");
        
        Label lblFaltan = new Label("Faltan " + Math.max(0, xpMeta - xpActual) + " XP para el siguiente nivel");
        lblFaltan.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");

        layout.getChildren().addAll(contenedorCirculo, lblNivel, lblXP, lblFaltan);
        return layout;
    }

    private VBox crearRachaDias(int dias, double bono) {
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        Label iconoFuego = new Label("🔥");
        iconoFuego.setStyle("-fx-font-size: 40px;");
        
        Label lblDias = new Label(String.valueOf(dias));
        lblDias.setStyle("-fx-font-size: 38px; -fx-font-weight: bold; -fx-text-fill: #D98E32;"); 
        
        Label lblTexto = new Label("Días consecutivos");
        lblTexto.setStyle("-fx-text-fill: -color-text-muted;");
        
        Label lblBono = new Label("Bono activo: x" + bono + " XP");
        lblBono.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-accent;");

        layout.getChildren().addAll(iconoFuego, lblDias, lblTexto, lblBono);
        return layout;
    }

    private HBox crearTarjetaNivel(String icono, String titulo, String xpRango, boolean esActual) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: transparent; -fx-padding: 15;");

        StackPane iconBox = new StackPane();
        iconBox.setStyle("-fx-background-color: -color-bg-base; -fx-background-radius: 15; -fx-min-width: 60px; -fx-min-height: 60px; -fx-border-color: -color-border; -fx-border-radius: 15;");
        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 24px;");
        iconBox.getChildren().add(lblIcono);

        VBox textos = new VBox(5);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-text-main;");
        Label lblRango = new Label(xpRango);
        lblRango.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 12px;");
        textos.getChildren().addAll(lblTitulo, lblRango);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label status = new Label();
        if (esActual) {
            status.setText("● Nivel actual");
            status.setStyle("-fx-background-color: -color-accent-light; -fx-text-fill: -color-accent; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 20;");
        } else {
            status.setText("🔒 Bloqueado");
            status.setStyle("-fx-background-color: -color-bg-base; -fx-text-fill: -color-text-muted; -fx-padding: 8 20; -fx-background-radius: 20;");
        }

        card.getChildren().addAll(iconBox, textos, spacer, status);
        return card;
    }

    private VBox crearItemRacha(int dias, int porcentaje) {
        VBox layout = new VBox(5);
        layout.setAlignment(Pos.CENTER);
        Label lblDias = new Label(dias + " 🔥");
        lblDias.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #D98E32;");
        Label lblBono = new Label("+" + porcentaje + "% XP");
        lblBono.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-accent;");
        layout.getChildren().addAll(lblDias, lblBono);
        return layout;
    }
}