package com.optem.ui;

import com.optem.controllers.PomodoroController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class PomodoroView extends VBox {

    // Instancia del controlador
    private final PomodoroController controller = new PomodoroController();
    private Timeline timeline;

    // Componentes visuales que van a cambiar
    private Label lblTiempo;
    private Label lblEstado;
    private Arc anilloProgreso;
    private Button btnIniciar;

    public PomodoroView() {
        this.setSpacing(25);
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: transparent;");

        // --- 1. CABECERA ---
        Label titulo = new Label("🎯 Técnica Pomodoro");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: -color-text-main;");
        Label subtitulo = new Label("Maximiza tu concentración con bloques de estudio enfocados");
        subtitulo.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 14px;");
        VBox header = new VBox(5, titulo, subtitulo);

        // --- CONTENEDOR PRINCIPAL DIVIDIDO (Izquierda / Derecha) ---
        HBox splitLayout = new HBox(40);
        splitLayout.setAlignment(Pos.TOP_CENTER);
        
        // --- 2. COLUMNA IZQUIERDA: TEMPORIZADOR ---
        VBox colIzquierda = crearColumnaTemporizador();
        HBox.setHgrow(colIzquierda, Priority.ALWAYS); 
        colIzquierda.setMaxWidth(600);

        // --- 3. COLUMNA DERECHA: ESTADÍSTICAS Y TIPS ---
        VBox colDerecha = crearColumnaEstadisticas();
        colDerecha.setPrefWidth(350);
        colDerecha.setMinWidth(350);

        splitLayout.getChildren().addAll(colIzquierda, colDerecha);

        this.getChildren().addAll(header, splitLayout);

        // Configurar el motor del reloj en la UI
        configurarReloj();
    }

    private VBox crearColumnaTemporizador() {
        VBox layout = new VBox(30);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20, 0, 0, 0));

        // Botones superiores (Tipos de descanso)
        HBox toggleButtons = new HBox(15);
        toggleButtons.setAlignment(Pos.CENTER);
        Button btnPomodoro = crearBotonToggle("🎯 Pomodoro", true);
        Button btnCorto = crearBotonToggle("☕ Descanso corto", false);
        Button btnLargo = crearBotonToggle("🛋 Descanso largo", false);
        toggleButtons.getChildren().addAll(btnPomodoro, btnCorto, btnLargo);

        // --- CÍRCULO DEL TEMPORIZADOR ---
        StackPane contenedorCirculo = new StackPane();
        
        Circle anilloFondo = new Circle(120);
        anilloFondo.setFill(Color.TRANSPARENT);
        anilloFondo.setStroke(Color.web("#E0E0E0")); 
        anilloFondo.setStrokeWidth(12);

        anilloProgreso = new Arc(0, 0, 120, 120, 90, -360);
        anilloProgreso.setFill(Color.TRANSPARENT);
        anilloProgreso.setStroke(Color.web("#508A63")); 
        anilloProgreso.setStrokeWidth(12);
        
        contenedorCirculo.getChildren().addAll(anilloFondo, anilloProgreso);

        // Texto del tiempo y estado
        VBox timerTextLayout = new VBox(10);
        timerTextLayout.setAlignment(Pos.CENTER);
        
        lblTiempo = new Label(controller.getTiempoFormateado());
        lblTiempo.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: #508A63;");
        
        lblEstado = new Label("🎯 Pomodoro — Listo para iniciar");
        lblEstado.setStyle("-fx-text-fill: -color-text-muted;");
        
        // --- BOTONES DE CONTROL ---
        HBox controlesTimer = new HBox(20);
        controlesTimer.setAlignment(Pos.CENTER);
        
        btnIniciar = new Button("▶ Iniciar");
        btnIniciar.setStyle("-fx-background-color: #508A63; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 30; -fx-cursor: hand;");
        btnIniciar.setOnAction(e -> alternarTemporizador());
        
        Button btnResetear = new Button("↺ Resetear");
        btnResetear.setStyle("-fx-background-color: transparent; -fx-text-fill: #508A63; -fx-border-color: #508A63; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10 30; -fx-cursor: hand;");
        btnResetear.setOnAction(e -> resetearTemporizador());
        
        controlesTimer.getChildren().addAll(btnIniciar, btnResetear);

        layout.getChildren().addAll(toggleButtons, contenedorCirculo, lblTiempo, lblEstado, controlesTimer);
        return layout;
    }

    // ==========================================
    // DELEGACIÓN DEL RELOJ AL CONTROLADOR
    // ==========================================

    private void configurarReloj() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            boolean terminado = controller.tick();
            if (terminado) {
                timeline.pause();
                btnIniciar.setText("▶ Iniciar");
                lblEstado.setText("🎉 ¡Pomodoro completado!");
            } else {
                actualizarInterfazReloj();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void alternarTemporizador() {
        boolean enMarcha = controller.alternarTemporizador();
        if (!enMarcha) {
            timeline.pause();
            btnIniciar.setText("▶ Iniciar");
            lblEstado.setText("🎯 Pomodoro — Pausado");
        } else {
            timeline.play();
            btnIniciar.setText("⏹ Pausar");
            lblEstado.setText("🎯 Pomodoro — En curso");
        }
        actualizarInterfazReloj();
    }

    private void resetearTemporizador() {
        timeline.pause();
        controller.resetearTemporizador();
        btnIniciar.setText("▶ Iniciar");
        lblEstado.setText("🎯 Pomodoro — Listo para iniciar");
        actualizarInterfazReloj();
    }

    private void actualizarInterfazReloj() {
        lblTiempo.setText(controller.getTiempoFormateado());
        anilloProgreso.setStartAngle(controller.getAnguloInicioCirculo());
        anilloProgreso.setLength(controller.getLongitudArcoCirculo());
    }

    // ==========================================
    // MÉTODOS DE LA COLUMNA DERECHA (ESTILO)
    // ==========================================

    private Button crearBotonToggle(String texto, boolean activo) {
        Button btn = new Button(texto);
        if (activo) {
            btn.setStyle("-fx-background-color: #508A63; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 20; -fx-font-weight: bold;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: -color-text-muted; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand; -fx-border-color: #E0E0E0; -fx-border-radius: 20;");
        }
        return btn;
    }

    private VBox crearColumnaEstadisticas() {
        VBox layout = new VBox(25);

        VBox cardSesiones = crearTarjetaEstatica("📅 Sesiones hoy");
        Label lblSesiones = new Label("0 / 4");
        lblSesiones.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #508A63;");
        HBox dots = new HBox(10);
        dots.setAlignment(Pos.CENTER);
        for(int i=0; i<4; i++) dots.getChildren().add(new Circle(8, Color.web("#D9D9D9")));
        cardSesiones.getChildren().addAll(lblSesiones, dots);
        cardSesiones.setAlignment(Pos.CENTER);

        VBox cardTips = crearTarjetaEstatica("💡 Tips Pomodoro");
        cardTips.setAlignment(Pos.TOP_LEFT);
        
        for (String tip : controller.getTips()) {
            Label lblTip = new Label("•  " + tip);
            lblTip.setStyle("-fx-text-fill: -color-text-muted; -fx-padding: 5 0;");
            lblTip.setWrapText(true);
            cardTips.getChildren().add(lblTip);
        }

        VBox imagenMotivacion = new VBox(10);
        imagenMotivacion.setAlignment(Pos.CENTER);
        imagenMotivacion.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius: 15; -fx-min-height: 150px;");
        Label lblImgPlaceholder = new Label("¡Tú puedes! 💪 Keep going");
        lblImgPlaceholder.setStyle("-fx-text-fill: white; -fx-text-alignment: center;");
        imagenMotivacion.getChildren().add(lblImgPlaceholder);

        VBox cardXP = crearTarjetaEstatica("⚡ XP por sesión");
        cardXP.setAlignment(Pos.CENTER);
        Label lblXP = new Label("+100 XP");
        lblXP.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #508A63;");
        Label lblXPSub = new Label("Al completar un Pomodoro de 25 min");
        lblXPSub.setStyle("-fx-text-fill: -color-text-muted; -fx-font-size: 11px;");
        cardXP.getChildren().addAll(lblXP, lblXPSub);

        layout.getChildren().addAll(cardSesiones, cardTips, imagenMotivacion, cardXP);
        return layout;
    }

    private VBox crearTarjetaEstatica(String titulo) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: transparent; -fx-padding: 10 0;");
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: -color-text-main;");
        card.getChildren().add(lblTitulo);
        return card;
    }
}