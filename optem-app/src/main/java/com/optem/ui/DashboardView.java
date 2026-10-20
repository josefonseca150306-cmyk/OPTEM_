package com.optem.ui;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import com.optem.controllers.DashboardController;
import com.optem.logic.ActividadManager;
import com.optem.models.Actividad;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DashboardView {

    private final DashboardController controller = new DashboardController();

    private boolean isSidebarExpanded = true;
    private static final double EXPANDED_WIDTH = 260;
    private static final double COLLAPSED_WIDTH = 75;
    
    private Label lblCurrentBook;
    private Button btnResumeBook;
    private Timeline mediaTrackerTimeline;
    private ProgressBar xpBar; 

    private boolean isChatOpen = false;
    private VBox chatWindow;
    private Button btnChatToggle;
    private TextArea txtChatHistory; 
    private double dragStartX = 0;
    private double dragStartY = 0;

    private Circle userPic;
    private Label lblName;
    private String correoActualGlobal;
    
    private List<Animation> hexAnimations = new ArrayList<>();
    private ImageView ivEscudoGlobal;
    private Button btnToggle; 

    private Button btnInicio, btnAgenda, btnActividades, btnGrupos, btnPomodoro, btnLogros, btnReinscripcion, btnAjustes;
    private BorderPane root;
    private ScrollPane scroll;
    private TableView<Actividad> tableActivities;

    private VBox voiceOverlay;
    private Label lblVoiceStatus;
    private TextField txtVoiceMock;
    private List<ScaleTransition> voiceWaves = new ArrayList<>();

    public void show(Stage stage, String correoUsuario, String fileKey) {
        
        controller.inicializarUsuario(correoUsuario);
        this.correoActualGlobal = correoUsuario;

        String nombreEstudianteActivo = controller.getNombreEstudiante();
        String colorFuerte = controller.getColorFuerte();
        boolean animacionesActivas = controller.isAnimacionesActivas();
        
        String fontBase = "14px";
        if (controller.getTamanoFuente().equals("Pequeño")) fontBase = "12px";
        else if (controller.getTamanoFuente().equals("Grande")) fontBase = "16px";
        
        StackPane mainRoot = new StackPane();
        root = new BorderPane();
        root.setStyle("-fx-font-size: " + fontBase + ";"); 

        // --- 1. SIDEBAR RETRÁCTIL ---
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(EXPANDED_WIDTH);
        sidebar.setMinWidth(COLLAPSED_WIDTH);
        sidebar.setMaxWidth(EXPANDED_WIDTH);
        sidebar.setPadding(new Insets(15, 20, 15, 20));
        sidebar.getStyleClass().add("dashboard-sidebar");

        btnToggle = new Button("≡");
        btnToggle.getStyleClass().add("menu-toggle-btn");
        btnToggle.setAlignment(Pos.CENTER_LEFT);
        applyAccessibility(btnToggle, "Botón de Menú lateral");

        VBox userBox = new VBox(10);
        userBox.setAlignment(Pos.CENTER);
        userBox.setPadding(new Insets(10, 0, 20, 0));
        
        userPic = new Circle(35);
        userPic.setFill(Color.web(colorFuerte));
        userPic.setStyle("-fx-cursor: hand;");
        
        String savedPhotoPath = controller.getFotoPerfil();
        if (!savedPhotoPath.isEmpty()) {
            File photoFile = new File(savedPhotoPath);
            if (photoFile.exists()) {
                try { 
                    Image rawImg = new Image(photoFile.toURI().toString());
                    userPic.setFill(new ImagePattern(getSquareImage(rawImg))); 
                } catch (Exception ex) {}
            }
        }

        lblName = new Label(nombreEstudianteActivo);
        lblName.getStyleClass().add("user-name-label");
        userBox.getChildren().addAll(userPic, lblName);
        applyAccessibility(userPic, "Opciones de Perfil");

        ContextMenu profileMenu = new ContextMenu();
        profileMenu.getStyleClass().add("menu-dominio-oro"); 

        MenuItem mnuCambiarFoto = new MenuItem("📷  Cambiar foto de perfil");
        MenuItem mnuCambiarCuenta = new MenuItem("🔄  Cambiar de usuario");
        MenuItem mnuCerrarSesion = new MenuItem("🚪  Cerrar sesión");
        
        profileMenu.getItems().addAll(mnuCambiarFoto, mnuCambiarCuenta, mnuCerrarSesion);

        mnuCambiarFoto.setOnAction(e -> { if (btnAjustes != null) btnAjustes.fire(); });
        mnuCambiarCuenta.setOnAction(e -> mostrarVentanaMultiCuentas(stage, correoUsuario, fileKey));
        mnuCerrarSesion.setOnAction(e -> { 
            try { 
                if (mediaTrackerTimeline != null) mediaTrackerTimeline.stop();
                controller.cerrarSesion();
                new LoginView().show(stage); 
            } catch (Exception ex) {} 
        });

        userPic.setOnMouseClicked(evt -> profileMenu.show(userPic, evt.getScreenX(), evt.getScreenY()));

        // --- 3. BOTONES DEL MENÚ ---
        VBox menuButtonsBox = new VBox(12);
        Runnable themeUpdater = () -> show(stage, correoUsuario, fileKey); 

        String rol = controller.getRolUsuario(); 

        btnInicio = createMenuButton("🏠", "Inicio");
        btnInicio.setOnAction(e -> {
            if (tableActivities != null) {
                int totalItems = ActividadManager.getInstance().getActividadesDelDia(LocalDate.now()).size(); 
                double fixedHeight = (Math.max(totalItems, 1) * 45) + 55; 
                tableActivities.setPrefHeight(fixedHeight); 
                tableActivities.setMinHeight(fixedHeight); 
                tableActivities.setMaxHeight(fixedHeight);
            }
            if (xpBar != null) xpBar.setProgress(ActividadManager.getInstance().calcularProgresoDelDia());
            root.setCenter(scroll); 
            actualizarSeleccionMenu(btnInicio);
            controller.hablar("Pantalla de Inicio abierta");
        });

        btnAgenda = createMenuButton("📅", "Mi Agenda");
        btnAgenda.setOnAction(e -> { abrirAgenda(); actualizarSeleccionMenu(btnAgenda); });

        btnPomodoro = createMenuButton("⏱", "Pomodoro");
        btnPomodoro.setOnAction(e -> { root.setCenter(new PomodoroView()); actualizarSeleccionMenu(btnPomodoro); });

        btnLogros = createMenuButton("🏆", "Logros");
        btnLogros.setOnAction(e -> { root.setCenter(new LogrosView()); actualizarSeleccionMenu(btnLogros); });

        btnAjustes = createMenuButton("⚙", "Ajustes");
        btnAjustes.setOnAction(e -> {
            root.setCenter(new AjustesView(themeUpdater, correoUsuario).getView());
            actualizarSeleccionMenu(btnAjustes);
            controller.hablar("Sección de Ajustes abierta");
        });

        if (rol.equals("MAESTRO")) {
            btnGrupos = createMenuButton("👥", "Mis Grupos");
            btnGrupos.setOnAction(e -> { 
                root.setCenter(new GruposMaestroView()); 
                actualizarSeleccionMenu(btnGrupos); 
            });
            menuButtonsBox.getChildren().addAll(btnInicio, btnAgenda, btnGrupos, btnPomodoro, btnLogros, btnAjustes);
        } else {
            btnActividades = createMenuButton("🎯", "Actividades");
            btnActividades.setOnAction(e -> { root.setCenter(new ActividadesView()); actualizarSeleccionMenu(btnActividades); });

            btnReinscripcion = createMenuButton("📑", "Reinscripción");
            btnReinscripcion.setOnAction(e -> { root.setCenter(new ReinscripcionView()); actualizarSeleccionMenu(btnReinscripcion); });

            menuButtonsBox.getChildren().addAll(btnInicio, btnAgenda, btnActividades, btnPomodoro, btnLogros, btnReinscripcion, btnAjustes);
        }

        actualizarSeleccionMenu(btnInicio);

        sidebar.getChildren().addAll(btnToggle, userBox, menuButtonsBox);

        btnToggle.setOnAction(e -> {
            isSidebarExpanded = !isSidebarExpanded;
            double targetWidth = isSidebarExpanded ? EXPANDED_WIDTH : COLLAPSED_WIDTH;
            
            sidebar.setCache(true); sidebar.setCacheHint(CacheHint.SPEED);
            if (root.getCenter() != null) { root.getCenter().setCache(true); root.getCenter().setCacheHint(CacheHint.SPEED); }

            Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(sidebar.prefWidthProperty(), sidebar.getWidth()), new KeyValue(userPic.radiusProperty(), userPic.getRadius())),
                new KeyFrame(Duration.millis(200), new KeyValue(sidebar.prefWidthProperty(), targetWidth), new KeyValue(userPic.radiusProperty(), isSidebarExpanded ? 35 : 22))
            );
            
            timeline.setOnFinished(evt -> {
                if (!isSidebarExpanded) { 
                    sidebar.setPadding(new Insets(15, 10, 15, 10)); 
                    userBox.getChildren().remove(lblName); 
                    toggleButtonTexts(false, btnInicio, btnAgenda, btnActividades, btnGrupos, btnPomodoro, btnLogros, btnReinscripcion, btnAjustes); 
                } else { 
                    sidebar.setPadding(new Insets(15, 20, 15, 20)); 
                    if (!userBox.getChildren().contains(lblName)) userBox.getChildren().add(lblName); 
                    toggleButtonTexts(true, btnInicio, btnAgenda, btnActividades, btnGrupos, btnPomodoro, btnLogros, btnReinscripcion, btnAjustes); 
                }
                sidebar.setCache(false);
                if (root.getCenter() != null) root.getCenter().setCache(false);
            });
            timeline.play();
            controller.hablar("Menú lateral " + (isSidebarExpanded ? "desplegado" : "contraído"));
        });

        // --- 2. ÁREA DE CONTENIDO PRINCIPAL ---
        VBox mainContent = new VBox(25);
        mainContent.setPadding(new Insets(30, 35, 30, 35));
        scroll = new ScrollPane(mainContent);
        scroll.setFitToWidth(true);

        // --- BANNER HEXÁGONOS ---
        StackPane bannerContainer = new StackPane(); 
        bannerContainer.setPrefHeight(170); 
        bannerContainer.setMinHeight(170);
        
        Pane bannerBackground = new Pane(); 
        bannerBackground.getStyleClass().add("banner-bg");

        Pane hexagonBackgroundPane = new Pane(); hexagonBackgroundPane.setMouseTransparent(true);
        
        double hexRadius = 22; double hSpacing = hexRadius * Math.sqrt(3); double vSpacing = hexRadius * 1.5; double baseCalculatedWidth = 1150; 

        Platform.runLater(() -> {
            for (double y = -hexRadius; y < 170 + hexRadius; y += vSpacing) {
                boolean rowOffset = ((int)(y / vSpacing) % 2 == 0);
                for (double x = -hexRadius; x < baseCalculatedWidth + hexRadius; x += hSpacing) {
                    double finalX = x + (rowOffset ? hSpacing / 2 : 0); 
                    double factorOpacidad = 1.0 - (finalX / baseCalculatedWidth);
                    double calculatedOpacity = 0.01 + (Math.max(0, Math.min(1, factorOpacidad)) * 0.12);
                    
                    HexagonPolygon hex = new HexagonPolygon(hexRadius, calculatedOpacity); 
                    hex.setLayoutX(finalX); hex.setLayoutY(y);
                    hexagonBackgroundPane.getChildren().add(hex);
                    
                    RotateTransition rt = new RotateTransition(Duration.seconds(14 + (Math.random() * 8)), hex); 
                    rt.setByAngle(360); rt.setInterpolator(Interpolator.LINEAR); rt.setCycleCount(Animation.INDEFINITE); 
                    
                    hexAnimations.add(rt); 
                    if (animacionesActivas) rt.play();
                }
            }
        });
        
        Rectangle bannerClip = new Rectangle(); bannerClip.setArcWidth(60); bannerClip.setArcHeight(60);
        bannerContainer.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> { bannerClip.setWidth(newVal.getWidth()); bannerClip.setHeight(newVal.getHeight()); });
        bannerContainer.setClip(bannerClip); bannerBackground.getChildren().add(hexagonBackgroundPane);
        
        HBox topBannerContent = new HBox(); topBannerContent.setPadding(new Insets(20, 40, 20, 40)); topBannerContent.setAlignment(Pos.CENTER_LEFT);
        VBox greetBox = new VBox(12); greetBox.setAlignment(Pos.CENTER_LEFT);
        
        Label lblGreet = new Label(getDynamicGreetingText()); 
        lblGreet.getStyleClass().add("banner-text-greet");
        applyAccessibility(lblGreet, "Mensaje de bienvenida: " + lblGreet.getText());
        
        xpBar = new ProgressBar(ActividadManager.getInstance().calcularProgresoDelDia()); 
        xpBar.setPrefWidth(300); xpBar.setPrefHeight(10);
        xpBar.getStyleClass().add("dashboard-xp-bar");
        applyAccessibility(xpBar, "Barra de progreso de experiencia");
        
        greetBox.getChildren().addAll(lblGreet, xpBar);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        
        VBox clockContainer = new VBox(-10); clockContainer.setAlignment(Pos.CENTER_RIGHT);
        Label lblClock = new Label(); lblClock.getStyleClass().add("clock-time");
        Label lblDateGlass = new Label(); lblDateGlass.getStyleClass().add("clock-date");
        applyAccessibility(lblClock, "Reloj del sistema");
        
        InnerShadow innerGlow = new InnerShadow(2, 1, 1, Color.rgb(255, 255, 255, 0.4)); 
        DropShadow glassShadow = new DropShadow(15, 0, 6, Color.rgb(0, 0, 0, 0.25)); glassShadow.setInput(innerGlow);
        lblClock.setEffect(glassShadow); lblDateGlass.setEffect(glassShadow);
        initClock(lblClock, lblDateGlass); 
        
        clockContainer.getChildren().addAll(lblClock, lblDateGlass); 
        topBannerContent.getChildren().addAll(greetBox, spacer, clockContainer); 
        bannerContainer.getChildren().addAll(bannerBackground, topBannerContent);

        // --- 3. GALLERY VIEW ---
        GridPane galleryGrid = new GridPane(); galleryGrid.setHgap(25); galleryGrid.setVgap(25); galleryGrid.setAlignment(Pos.CENTER);
        galleryGrid.add(createSectionCard("Notas", "Tus apuntes", "/images/card_notes.jpg", -50), 0, 0); 
        VBox cardAgenda = createSectionCard("Agenda", "Horario semanal", "/images/card_agenda.jpg", -140);
        cardAgenda.setOnMouseClicked(e -> abrirAgenda());
        galleryGrid.add(cardAgenda, 1, 0);
        galleryGrid.add(createSectionCard("Tareas", "Lista de pendientes", "/images/card_tasks.jpg", -90), 2, 0);
        galleryGrid.add(createSectionCard("Pomodoro", "Concentración", "/images/card_pomodoro.jpg", 0), 0, 1); 
        galleryGrid.add(createSectionCard("Logros", "XP y rachas", "/images/card_achievements.jpg", -100), 1, 1); 
        galleryGrid.add(createSectionCard("Recursos", "Material de apoyo", "/images/card_resources.jpg", -60), 2, 1);

        // --- 4. MONITOR DE SISTEMA ---
        VBox currentlyPane = new VBox(15); 
        currentlyPane.getStyleClass().add("section-panel");

        Label lblCurrentlyTitle = new Label("En este momento..."); 
        lblCurrentlyTitle.getStyleClass().add("section-title");
        applyAccessibility(lblCurrentlyTitle, "Sección En este momento");

        GridPane currentlyGrid = new GridPane(); currentlyGrid.setHgap(40); currentlyGrid.setVgap(18);
        
        Label lblMusicHead = new Label("🎵 Escuchando:"); lblMusicHead.getStyleClass().add("label-muted-14");
        Label lblMusicTrack = new Label("Detectando reproductor activo..."); lblMusicTrack.getStyleClass().add("label-bold-14");
        startSystemMediaTracker(lblMusicTrack); 
        currentlyGrid.add(lblMusicHead, 0, 0); currentlyGrid.add(lblMusicTrack, 1, 0);

        Label lblReadingHead = new Label("📖 Leyendo:"); lblReadingHead.getStyleClass().add("label-muted-14");
        lblCurrentBook = new Label("Seleccionar documento de estudio..."); lblCurrentBook.getStyleClass().add("label-link");
        applyAccessibility(lblCurrentBook, "Seleccionar documento PDF");

        btnResumeBook = new Button("Continuar leyendo"); 
        btnResumeBook.getStyleClass().add("btn-resume-book"); 
        btnResumeBook.setVisible(false);
        applyAccessibility(btnResumeBook, "Continuar leyendo documento");

        String lastBookPath = controller.getUltimoLibroRuta();
        if (!lastBookPath.isEmpty()) {
            File lastBookFile = new File(lastBookPath);
            if (lastBookFile.exists()) {
                lblCurrentBook.setText(lastBookFile.getName() + " (Pág. " + controller.getUltimoLibroPagina() + ")"); 
                lblCurrentBook.getStyleClass().add("label-bold-14"); 
                lblCurrentBook.getStyleClass().remove("label-link");
                btnResumeBook.setVisible(true);
            }
        }

        lblCurrentBook.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser(); fileChooser.setTitle("Seleccionar documento (PDF)"); fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) { 
                controller.guardarProgresoLibro(selectedFile.getAbsolutePath(), 1); 
                lblCurrentBook.setText(selectedFile.getName() + " (Pág. 1)"); 
                lblCurrentBook.getStyleClass().add("label-bold-14");
                lblCurrentBook.getStyleClass().remove("label-link"); 
                btnResumeBook.setVisible(true); 
                controller.abrirDocumentoEnOS(selectedFile); 
            }
        });

        btnResumeBook.setOnAction(e -> { 
            String path = controller.getUltimoLibroRuta(); 
            if (!path.isEmpty()) {
                File savedFile = new File(path);
                if (savedFile.exists()) { 
                    controller.abrirDocumentoEnOS(savedFile); 
                    int currentPage = controller.getUltimoLibroPagina() + 1; 
                    controller.guardarProgresoLibro(path, currentPage); 
                    lblCurrentBook.setText(savedFile.getName() + " (Pág. " + currentPage + ")"); 
                }
            } 
        });

        HBox readingBox = new HBox(15, lblCurrentBook, btnResumeBook); readingBox.setAlignment(Pos.CENTER_LEFT); 
        currentlyGrid.add(lblReadingHead, 0, 1); currentlyGrid.add(readingBox, 1, 1);

        Label lblWorkingHead = new Label("💻 Trabajando en:"); lblWorkingHead.getStyleClass().add("label-muted-14");
        Label lblTaskLink = new Label("Proyecto de compilador JODA - Sprint 2"); lblTaskLink.getStyleClass().add("label-link"); 
        applyAccessibility(lblTaskLink, "Proyecto actual en curso");
        currentlyGrid.add(lblWorkingHead, 0, 2); currentlyGrid.add(lblTaskLink, 1, 2); 
        currentlyPane.getChildren().addAll(lblCurrentlyTitle, currentlyGrid);

        // --- 5. SECCIÓN TABLA DE ACTIVIDADES DE HOY ---
        VBox agendaPane = new VBox(15); 
        agendaPane.getStyleClass().add("agenda-panel");
        
        Label lblAgendaTitle = new Label("📋 Vista rápida de actividades de hoy"); 
        lblAgendaTitle.getStyleClass().add("section-title"); 
        applyAccessibility(lblAgendaTitle, "Tabla de actividades del día");

        tableActivities = new TableView<>(); 
        tableActivities.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); 
        tableActivities.setFixedCellSize(45); 
        
        int totalItems = ActividadManager.getInstance().getActividadesDelDia(LocalDate.now()).size(); 
        double fixedHeight = (Math.max(totalItems, 1) * 45) + 55; 
        
        tableActivities.setPrefHeight(fixedHeight); tableActivities.setMinHeight(fixedHeight); tableActivities.setMaxHeight(fixedHeight);

        tableActivities.setRowFactory(tv -> {
            TableRow<Actividad> row = new TableRow<>();
            row.setOnDragDetected(event -> { 
                if (!row.isEmpty()) { 
                    Actividad actividad = row.getItem(); 
                    Dragboard db = row.startDragAndDrop(TransferMode.COPY); 
                    ClipboardContent content = new ClipboardContent(); 
                    content.putString("ACT|" + actividad.getNombre() + "|" + actividad.getTipo() + "|" + actividad.getDescripcion() + "|" + actividad.getHoraVencimiento() + "|" + actividad.getFecha().toString()); 
                    db.setContent(content); event.consume(); 
                } 
            });
            return row;
        });

        TableColumn<Actividad, String> colNombre = new TableColumn<>("actividad"); colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); colNombre.setStyle("-fx-font-size: 14; -fx-font-weight: 500; -fx-alignment: CENTER-LEFT; -fx-padding: 0 0 0 15; -fx-text-fill: -color-text-main;");
        TableColumn<Actividad, String> colTipo = new TableColumn<>("tipo"); colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        
        colTipo.setCellFactory(column -> new TableCell<Actividad, String>() {
            @Override 
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); 
                if (empty || item == null) { 
                    setGraphic(null); 
                } else { 
                    Label badge = new Label(item); 
                    badge.setPadding(new Insets(5, 15, 5, 15)); 
                    badge.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 11));
                    switch (item.toLowerCase()) { 
                        case "estudio": badge.setStyle("-fx-background-color: #E2ECE9; -fx-text-fill: #2D6A4F; -fx-background-radius: 12;"); break; 
                        case "cultural": badge.setStyle("-fx-background-color: #E0EBF1; -fx-text-fill: #2B6CB0; -fx-background-radius: 12;"); break; 
                        case "personal": badge.setStyle("-fx-background-color: #F7EBE6; -fx-text-fill: #C05621; -fx-background-radius: 12;"); break; 
                        case "reunión": badge.setStyle("-fx-background-color: #EDE7F6; -fx-text-fill: #673AB7; -fx-background-radius: 12;"); break; 
                        case "deporte": badge.setStyle("-fx-background-color: #FFF3E0; -fx-text-fill: #E65100; -fx-background-radius: 12;"); break; 
                        default: badge.setStyle("-fx-background-color: #EEEEEE; -fx-text-fill: #616161; -fx-background-radius: 12;"); break; 
                    }
                    setGraphic(badge); 
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Actividad, LocalTime> colHora = new TableColumn<>("hora"); colHora.setCellValueFactory(new PropertyValueFactory<>("horaVencimiento"));
        colHora.setCellFactory(column -> new TableCell<Actividad, LocalTime>(){
            private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            @Override 
            protected void updateItem(LocalTime item, boolean empty) { 
                super.updateItem(item, empty); 
                if (empty || item == null) { 
                    setText(null); 
                } else { 
                    setText(item.format(timeFormatter)); 
                    setStyle("-fx-font-size: 13; -fx-text-fill: -color-text-muted; -fx-font-weight: 600;"); 
                    setAlignment(Pos.CENTER); 
                } 
            }
        });

        TableColumn<Actividad, Boolean> colHecho = new TableColumn<>("echo"); colHecho.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().isHecho()));
        colHecho.setCellFactory(column -> new TableCell<Actividad, Boolean>() {
            private final Circle statusCircle = new Circle(8);
            @Override 
            protected void updateItem(Boolean hecho, boolean empty) {
                super.updateItem(hecho, empty); 
                if (empty || hecho == null) { 
                    setGraphic(null); 
                } else { 
                    if (hecho) { 
                        statusCircle.setFill(Color.web(colorFuerte)); 
                        statusCircle.setStroke(Color.web(colorFuerte)); 
                        statusCircle.setStrokeWidth(1); 
                    } else { 
                        statusCircle.setFill(Color.web("#FFFFFF", 0.1)); 
                        statusCircle.setStroke(Color.web("#ccc")); 
                        statusCircle.setStrokeWidth(2); 
                    } 
                    setGraphic(statusCircle); 
                    setAlignment(Pos.CENTER); 
                }
            }
        });

        tableActivities.getColumns().addAll(colNombre, colTipo, colHora, colHecho); 
        tableActivities.setItems(ActividadManager.getInstance().getActividadesDelDia(LocalDate.now())); 
        agendaPane.getChildren().addAll(lblAgendaTitle, tableActivities);

        VBox institutionalFooter = createAnimatedShieldFooter(animacionesActivas);
        mainContent.getChildren().addAll(bannerContainer, galleryGrid, currentlyPane, agendaPane, institutionalFooter);

        root.setLeft(sidebar); 
        root.setCenter(scroll); 
        mainRoot.getChildren().add(root);

        // --- 7. BURBUJA CHAT ---
        btnChatToggle = new Button("💬"); 
        btnChatToggle.getStyleClass().add("chat-btn");
        btnChatToggle.setTranslateX(550); 
        btnChatToggle.setTranslateY(320);
        applyAccessibility(btnChatToggle, "Botón de Chatbot");

        btnChatToggle.setOnMousePressed(evt -> { 
            dragStartX = evt.getSceneX() - btnChatToggle.getTranslateX(); 
            dragStartY = evt.getSceneY() - btnChatToggle.getTranslateY(); 
        });
        
        btnChatToggle.setOnMouseDragged(evt -> { 
            btnChatToggle.setTranslateX(evt.getSceneX() - dragStartX); 
            btnChatToggle.setTranslateY(evt.getSceneY() - dragStartY); 
            if (isChatOpen) { 
                chatWindow.setTranslateX(btnChatToggle.getTranslateX() - 160); 
                chatWindow.setTranslateY(btnChatToggle.getTranslateY() - 270); 
            } 
        });
        
        btnChatToggle.setOnMouseClicked(evt -> { 
            if (evt.isStillSincePress()) { 
                toggleChatWindow(); 
            } 
        });

        // --- 8. VENTANA CHAT ---
        chatWindow = new VBox(10); 
        chatWindow.setMaxSize(380, 480); 
        chatWindow.getStyleClass().add("chat-window");
        chatWindow.setVisible(false); 
        chatWindow.setOpacity(0);

        HBox chatHeader = new HBox(); 
        chatHeader.setAlignment(Pos.CENTER_LEFT); 
        chatHeader.getStyleClass().add("chat-header");
        
        Label lblChatTitle = new Label("Optem Inteligencia Artificial"); 
        lblChatTitle.getStyleClass().add("chat-title");
        
        Region headerSpacer = new Region(); 
        HBox.setHgrow(headerSpacer, Priority.ALWAYS); 
        
        Button btnCloseChat = new Button("✕"); 
        btnCloseChat.getStyleClass().add("btn-close-chat");
        
        chatHeader.getChildren().addAll(chatPicContainer(colorFuerte), lblChatTitle, headerSpacer, btnCloseChat);

        txtChatHistory = new TextArea(); 
        txtChatHistory.setEditable(false); 
        txtChatHistory.setWrapText(true); 
        VBox.setVgrow(txtChatHistory, Priority.ALWAYS);
        txtChatHistory.getStyleClass().add("chat-history");
        txtChatHistory.appendText("Optem Bot: ¡Hola " + nombreEstudianteActivo + "! Soy tu asistente. Puedes arrastrar actividades de la tabla aquí para analizarlas. 🚀\n\n");

        txtChatHistory.setOnDragOver(evt -> { 
            if (evt.getDragboard().hasString()) { 
                evt.acceptTransferModes(TransferMode.COPY); 
            } 
            evt.consume(); 
        });
        
        txtChatHistory.setOnDragDropped(evt -> { 
            Dragboard db = evt.getDragboard(); 
            boolean success = false; 
            if (db.hasString()) { 
                String datosActividad = db.getString(); 
                txtChatHistory.appendText("Tú: [Arrastró] " + datosActividad + "\n"); 
                String respuestaAnalisis = controller.analizarArrastreChatbot(datosActividad); 
                Platform.runLater(() -> { 
                    txtChatHistory.appendText("Optem Bot: " + respuestaAnalisis + "\n\n"); 
                    txtChatHistory.setScrollTop(Double.MAX_VALUE); 
                }); 
                success = true; 
            } 
            evt.setDropCompleted(success); 
            evt.consume(); 
        });

        HBox chatInputArea = new HBox(8); 
        TextField txtChatInput = new TextField(); 
        txtChatInput.setPromptText("Escribe o arrastra una tarea aquí..."); 
        HBox.setHgrow(txtChatInput, Priority.ALWAYS);
        txtChatInput.getStyleClass().add("chat-input");
        
        Button btnSendChat = new Button("➤"); 
        btnSendChat.getStyleClass().add("chat-send-btn");

        chatInputArea.getChildren().addAll(txtChatInput, btnSendChat); 
        chatWindow.getChildren().addAll(chatHeader, txtChatHistory, chatInputArea); 
        
        crearAsistenteDeVozOverlay(colorFuerte); 
        mainRoot.getChildren().addAll(chatWindow, btnChatToggle, voiceOverlay);

        Runnable ejecutarEnvio = () -> { 
            String prompt = txtChatInput.getText().trim(); 
            if (!prompt.isEmpty()) { 
                txtChatHistory.appendText("Tú: " + prompt + "\n"); 
                txtChatInput.clear(); 
                String respuestaBot = controller.responderMensajeChatbot(prompt); 
                Thread simThread = new Thread(() -> { 
                    try { Thread.sleep(500); } catch (Exception ex) {} 
                    Platform.runLater(() -> { 
                        txtChatHistory.appendText("Optem Bot: " + respuestaBot + "\n\n"); 
                        txtChatHistory.setScrollTop(Double.MAX_VALUE); 
                    }); 
                }); 
                simThread.setDaemon(true); 
                simThread.start(); 
            } 
        };
        
        btnSendChat.setOnAction(e -> ejecutarEnvio.run()); 
        txtChatInput.setOnAction(e -> ejecutarEnvio.run()); 
        btnCloseChat.setOnAction(e -> toggleChatWindow());

        Scene scene = new Scene(mainRoot, 1300, 850);
        aplicarTemaGlobal(scene, correoUsuario);
        configurarNavegacionPorTecladoGlobal(scene); 

        stage.setOnCloseRequest(e -> { 
            if (mediaTrackerTimeline != null) { 
                mediaTrackerTimeline.stop(); 
            } 
        });
        
        stage.setScene(scene); 
        stage.setMaximized(false); 
        stage.show();
        
        Platform.runLater(() -> { 
            stage.setMaximized(true); 
            stage.toFront(); 
        });
    }

    // =========================================================
    // LÓGICA DE ACCESIBILIDAD (TECLADO, VOZ Y TTS)
    // =========================================================

    private void applyAccessibility(Node node, String description) {
        node.setOnMouseEntered(e -> controller.hablar(description));
        node.focusedProperty().addListener((obs, oldVal, newVal) -> { if (newVal) controller.hablar(description); });
    }

    private void configurarNavegacionPorTecladoGlobal(Scene scene) {
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            if (controller.isTecladoActivo() && e.getCode() == KeyCode.CONTROL) { btnToggle.fire(); e.consume(); }
            if (controller.isVozActivo() && e.getCode() == KeyCode.F2) { abrirAsistenteDeVoz(); e.consume(); }
        });
    }

    private void crearAsistenteDeVozOverlay(String colorFuerte) {
        voiceOverlay = new VBox(15); voiceOverlay.setAlignment(Pos.CENTER); voiceOverlay.setMaxSize(400, 150); 
        voiceOverlay.getStyleClass().add("voice-overlay");
        StackPane.setAlignment(voiceOverlay, Pos.BOTTOM_CENTER); StackPane.setMargin(voiceOverlay, new Insets(0, 0, 40, 0)); voiceOverlay.setVisible(false); voiceOverlay.setOpacity(0);
        
        lblVoiceStatus = new Label("Asistente activo..."); lblVoiceStatus.getStyleClass().add("voice-status");
        HBox waves = new HBox(8); waves.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) { 
            Rectangle bar = new Rectangle(8, 20, Color.web(colorFuerte)); bar.setArcWidth(4); bar.setArcHeight(4); 
            ScaleTransition st = new ScaleTransition(Duration.millis(300 + (Math.random() * 200)), bar); st.setByY(1.5); st.setAutoReverse(true); st.setCycleCount(Animation.INDEFINITE); 
            voiceWaves.add(st); waves.getChildren().add(bar); 
        }
        
        txtVoiceMock = new TextField(); txtVoiceMock.setPromptText("Presiona Win + H para dictar con tu voz..."); 
        txtVoiceMock.getStyleClass().add("voice-input");
        
        txtVoiceMock.setOnAction(e -> {
            for (ScaleTransition st : voiceWaves) st.play();
            lblVoiceStatus.setText("Procesando comando..."); 
            
            String accion = controller.procesarComandoVoz(txtVoiceMock.getText().toLowerCase());
            switch(accion) {
                case "INICIO": btnInicio.fire(); lblVoiceStatus.setText("Cargando Inicio..."); break;
                case "AJUSTES": btnAjustes.fire(); lblVoiceStatus.setText("Cargando Ajustes..."); break;
                case "OSCURO": btnAjustes.fire(); lblVoiceStatus.setText("Modo oscuro activado"); break;
                case "CLARO": btnAjustes.fire(); lblVoiceStatus.setText("Modo claro activado"); break;
                case "ABRIR_MENU": if (!isSidebarExpanded) btnToggle.fire(); lblVoiceStatus.setText("Menú abierto"); break;
                case "CERRAR_MENU": if (isSidebarExpanded) btnToggle.fire(); lblVoiceStatus.setText("Menú cerrado"); break;
                case "PERFIL": 
                    javafx.scene.input.MouseEvent event = new javafx.scene.input.MouseEvent(javafx.scene.input.MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, javafx.scene.input.MouseButton.PRIMARY, 1, true, true, true, true, true, true, true, true, true, true, null); 
                    javafx.event.Event.fireEvent(userPic, event); 
                    lblVoiceStatus.setText("Abriendo opciones de perfil..."); 
                    break;
                case "SALIR": new LoginView().show((Stage) root.getScene().getWindow()); break;
                case "APAGAR": cerrarAsistenteDeVoz(); return;
                default: lblVoiceStatus.setText("Comando no reconocido."); controller.hablar("No encontré una acción para ese comando.");
            }
            txtVoiceMock.clear();
            PauseTransition pt = new PauseTransition(Duration.seconds(1.5)); pt.setOnFinished(evt -> { for (ScaleTransition st : voiceWaves) st.pause(); lblVoiceStatus.setText("Esperando siguiente comando..."); }); pt.play();
        });

        Button btnCerrarVoz = new Button("✕ Cerrar Asistente"); 
        btnCerrarVoz.getStyleClass().add("btn-close-voice");
        btnCerrarVoz.setOnAction(e -> cerrarAsistenteDeVoz());
        voiceOverlay.getChildren().addAll(lblVoiceStatus, waves, txtVoiceMock, btnCerrarVoz);
    }

    private void abrirAsistenteDeVoz() { 
        voiceOverlay.setVisible(true); FadeTransition ft = new FadeTransition(Duration.millis(300), voiceOverlay); ft.setToValue(1); ft.play(); txtVoiceMock.clear(); txtVoiceMock.requestFocus(); lblVoiceStatus.setText("Escuchando..."); controller.hablar("Asistente activado. Presiona la tecla Windows más Hache para dictar tu comando."); 
    }

    private void cerrarAsistenteDeVoz() { 
        FadeTransition ft = new FadeTransition(Duration.millis(200), voiceOverlay); ft.setToValue(0); ft.setOnFinished(e -> voiceOverlay.setVisible(false)); ft.play(); for (ScaleTransition st : voiceWaves) st.pause(); controller.hablar("Asistente cerrado.");
    }

    private void startSystemMediaTracker(Label label) {
        mediaTrackerTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            label.setText(controller.obtenerPistaMusicalActual());
        }));
        mediaTrackerTimeline.setCycleCount(Animation.INDEFINITE); mediaTrackerTimeline.play();
    }

    public void aplicarTemaGlobal(Scene scene, String correoUsuario) {
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        boolean isDark = prefs.getBoolean("PREF_DARK_MODE_" + correoUsuario, false);
        String colorFuerte = prefs.get("PREF_ACCENT_COLOR_" + correoUsuario, "#3D5A4D");
        Color c = Color.web(colorFuerte);
        String accentLight = String.format("rgba(%d,%d,%d,0.15)", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
        
        String bgBase = isDark ? "#121212" : "#f8f9fa"; 
        String bgPanel = isDark ? "#1e1e1e" : "#ffffff"; 
        String textMain = isDark ? "#f8f9fa" : "#1B263B"; 
        String textMuted = isDark ? "#aaaaaa" : "#666666"; 
        String borderCol = isDark ? "#333333" : "#eeeeee"; 
        String bgPanelTrans = isDark ? "rgba(30, 30, 30, 0.8)" : "rgba(255, 255, 255, 0.45)"; 
        String headerBg = isDark ? "rgba(255, 255, 255, 0.05)" : "rgba(220, 225, 222, 0.7)";

        // Inyectamos las variables de color a la escena raíz para que todo el CSS las reconozca
        scene.getRoot().setStyle(
            "-color-bg-base: " + bgBase + "; " +
            "-color-bg-panel: " + bgPanel + "; " +
            "-color-bg-panel-trans: " + bgPanelTrans + "; " +
            "-color-text-main: " + textMain + "; " +
            "-color-text-muted: " + textMuted + "; " +
            "-color-border: " + borderCol + "; " +
            "-color-accent: " + colorFuerte + "; " +
            "-color-accent-light: " + accentLight + "; " +
            "-color-header-bg: " + headerBg + ";"
        );

        try {
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            if (!scene.getStylesheets().contains(css)) {
                scene.getStylesheets().add(css);
            }
        } catch(Exception e) {
            System.out.println("Error cargando styles.css en DashboardView");
        }
    }

    private Image getSquareImage(Image original) {
        if (original == null || original.isError()) return original;
        int w = (int) original.getWidth(); int h = (int) original.getHeight(); int size = Math.min(w, h); int x = (w - size) / 2; int y = (h - size) / 2;
        return new WritableImage(original.getPixelReader(), x, y, size, size);
    }

    private void toggleButtonTexts(boolean showText, Button... buttons) {
        for (Button btn : buttons) {
            if (btn == null) continue;
            String icon = (String) btn.getUserData();
            if (showText) { btn.setText(icon + "  " + btn.getId()); btn.setAlignment(Pos.CENTER_LEFT); } else { btn.setText(icon); btn.setAlignment(Pos.CENTER); }
        }
    }

    private void mostrarVentanaMultiCuentas(Stage mainStage, String correoActual, String fileKey) {
        Stage popStage = new Stage(); popStage.initStyle(StageStyle.TRANSPARENT);
        VBox layout = new VBox(15); layout.setPadding(new Insets(25)); layout.setAlignment(Pos.CENTER); 
        layout.getStyleClass().add("chat-window");

        Label title = new Label("Cuentas en este Dispositivo"); 
        title.getStyleClass().add("section-title");
        
        Button btnCerrar = new Button("✕"); 
        btnCerrar.getStyleClass().add("btn-close-chat"); 
        btnCerrar.setOnAction(e -> popStage.close());

        HBox header = new HBox(title, new Region(), btnCerrar); HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS); header.setAlignment(Pos.CENTER);
        VBox accountsBox = new VBox(8); accountsBox.setAlignment(Pos.CENTER);

        Button btnCuenta1 = new Button("👤  " + controller.getNombreEstudiante() + " (Activa)"); btnCuenta1.setMaxWidth(Double.MAX_VALUE); 
        btnCuenta1.getStyleClass().add("menu-btn-active"); accountsBox.getChildren().add(btnCuenta1);
        
        for(String email : controller.getCuentasExtras()) {
            String nom = controller.getNombreDeCuenta(email); 
            Button btnCuentaExtra = new Button("👤  " + nom); btnCuentaExtra.setMaxWidth(Double.MAX_VALUE); 
            btnCuentaExtra.getStyleClass().add("menu-btn");
            btnCuentaExtra.setOnAction(evt -> { popStage.close(); show(mainStage, email, fileKey); }); accountsBox.getChildren().add(btnCuentaExtra);
        }

        Button btnAdministrar = new Button("⚙ Administrar cuentas"); btnAdministrar.getStyleClass().add("label-link"); btnAdministrar.setOnAction(e -> { popStage.close(); mostrarVentanaAdministrarCuentas(mainStage, correoActual, fileKey); });
        Separator sep = new Separator(); sep.setStyle("-fx-background-color: -color-border;");
        Button btnVincular = new Button("➕  Vincular nueva cuenta"); btnVincular.setMaxWidth(Double.MAX_VALUE); btnVincular.setStyle("-fx-background-color: -color-accent; -fx-background-radius: 12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 11; -fx-font-family: 'Segoe UI';");

        btnVincular.setOnAction(e -> {
            popStage.close(); 
            AddAccountDialog addDialog = new AddAccountDialog(); 
            AddAccountDialog.UserData data = addDialog.showAndWait(mainStage);
            if (data != null && !data.user.equalsIgnoreCase(correoActual)) {
                controller.registrarNuevaCuenta(data.user, data.nombre);
                show(mainStage, data.user, fileKey);
            }
        });

        layout.getChildren().addAll(header, accountsBox, btnAdministrar, sep, btnVincular);
        Scene popScene = new Scene(layout, 360, 310); popScene.setFill(Color.TRANSPARENT); popStage.setScene(popScene); popStage.initOwner(mainStage); aplicarTemaGlobal(popScene, correoActual); popStage.show();
    }

    private void mostrarVentanaAdministrarCuentas(Stage mainStage, String correoActual, String fileKey) {
        Stage popStage = new Stage(); popStage.initStyle(StageStyle.TRANSPARENT);
        VBox layout = new VBox(15); layout.setPadding(new Insets(25)); layout.setAlignment(Pos.CENTER); layout.getStyleClass().add("chat-window");

        Label title = new Label("Eliminar Cuentas"); title.getStyleClass().add("section-title");
        Button btnAtras = new Button("✕"); btnAtras.getStyleClass().add("btn-close-chat");
        btnAtras.setOnAction(e -> { popStage.close(); mostrarVentanaMultiCuentas(mainStage, correoActual, fileKey); });

        HBox header = new HBox(title, new Region(), btnAtras); HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS); header.setAlignment(Pos.CENTER);
        VBox accountsBox = new VBox(8); accountsBox.setAlignment(Pos.CENTER);

        List<String> cuentasExtras = controller.getCuentasExtras();
        for(String email : cuentasExtras) {
            String nombreSegunda = controller.getNombreDeCuenta(email);
            HBox row = new HBox(10); row.setAlignment(Pos.CENTER_LEFT); row.setStyle("-fx-background-color: transparent; -fx-background-radius: 12; -fx-padding: 11; -fx-border-color: -color-border; -fx-border-radius: 12;");
            Label lblNom = new Label("👤  " + nombreSegunda); lblNom.getStyleClass().add("label-bold-14");
            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            Button btnDelete = new Button("🗑"); btnDelete.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14;");
            
            btnDelete.setOnAction(e -> {
                controller.eliminarCuenta(email);
                accountsBox.getChildren().remove(row);
                if(accountsBox.getChildren().isEmpty()) { Label lblEmptyDynamic = new Label("No hay cuentas guardadas para eliminar."); lblEmptyDynamic.getStyleClass().add("label-muted-14"); accountsBox.getChildren().add(lblEmptyDynamic); }
            });
            row.getChildren().addAll(lblNom, spacer, btnDelete); accountsBox.getChildren().add(row);
        }
        if (cuentasExtras.isEmpty()) { Label lblEmpty = new Label("No hay cuentas guardadas para eliminar."); lblEmpty.getStyleClass().add("label-muted-14"); accountsBox.getChildren().add(lblEmpty); }

        layout.getChildren().addAll(header, accountsBox);
        Scene popScene = new Scene(layout, 360, 250); popScene.setFill(javafx.scene.paint.Color.TRANSPARENT); popStage.setScene(popScene); popStage.initOwner(mainStage); aplicarTemaGlobal(popScene, correoActual); popStage.show();
    }

    private VBox createAnimatedShieldFooter(boolean animacionesActivas) {
        VBox footerRoot = new VBox(15); footerRoot.setAlignment(Pos.CENTER); footerRoot.setPadding(new Insets(35, 0, 20, 0));
        try {
            InputStream is = getClass().getResourceAsStream("/images/escudo_uaemex.png");
            if (is != null) {
                ivEscudoGlobal = new ImageView(new Image(is)); ivEscudoGlobal.setPreserveRatio(true); ivEscudoGlobal.setFitHeight(110);
                if (animacionesActivas) {
                    FadeTransition breathingAnimation = new FadeTransition(Duration.seconds(4.5), ivEscudoGlobal); breathingAnimation.setFromValue(0.12); breathingAnimation.setToValue(0.55); breathingAnimation.setCycleCount(Animation.INDEFINITE); breathingAnimation.setAutoReverse(true); breathingAnimation.setInterpolator(Interpolator.EASE_BOTH); breathingAnimation.play();
                } else { ivEscudoGlobal.setOpacity(0.55); }
                footerRoot.getChildren().add(ivEscudoGlobal);
            }
        } catch (Exception ex) { Label lblFallback = new Label("UNIVERSIDAD AUTÓNOMA DEL ESTADO DE MÉXICO"); lblFallback.setStyle("-fx-text-fill: rgba(61, 90, 77, 0.4); -fx-font-weight: bold;"); footerRoot.getChildren().add(lblFallback); }
        return footerRoot;
    }

    private void toggleChatWindow() {
        isChatOpen = !isChatOpen;
        if (isChatOpen) {
            chatWindow.setTranslateX(btnChatToggle.getTranslateX() - 160); 
            chatWindow.setTranslateY(btnChatToggle.getTranslateY() - 270); 
            chatWindow.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), chatWindow); 
            fadeIn.setToValue(1.0); 
            fadeIn.play();
        } else {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), chatWindow); 
            fadeOut.setToValue(0.0); 
            fadeOut.setOnFinished(evt -> chatWindow.setVisible(false)); 
            fadeOut.play();
        }
    }

    private StackPane chatPicContainer(String colorFuerte) {
        StackPane container = new StackPane(); 
        container.setPadding(new Insets(0, 8, 0, 0)); 
        Circle pic = new Circle(12, Color.web(colorFuerte)); 
        Label letter = new Label("O"); 
        letter.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;"); 
        container.getChildren().addAll(pic, letter); 
        return container;
    }

    private Button createMenuButton(String icon, String text) {
        Button btn = new Button(icon + "  " + text); btn.setId(text); btn.setUserData(icon); btn.setMaxWidth(Double.MAX_VALUE); btn.setAlignment(Pos.CENTER_LEFT);
        btn.getStyleClass().add("menu-btn");
        applyAccessibility(btn, "Sección " + text); return btn;
    }

    private String getDynamicGreetingText() {
        int hora = LocalTime.now().getHour();
        if (hora >= 6 && hora < 12) return "¡Buenos días!"; else if (hora >= 12 && hora < 19) return "¡Buenas tardes!"; else return "¡Buenas noches!";
    }

    private void initClock(Label lblClock, Label lblDate) {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm"); DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", new Locale("es", "ES"));
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> { LocalDateTime now = LocalDateTime.now(); lblClock.setText(now.format(timeFmt)); lblDate.setText(now.format(dateFmt)); }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE); clock.play();
    }

    private VBox createSectionCard(String title, String sub, String imgPath, double offsetY) {
        VBox card = new VBox(0); card.setPrefSize(280, 210); 
        card.getStyleClass().add("dashboard-card");
        Pane imgContainer = new Pane(); imgContainer.setPrefSize(280, 130);
        try {
            InputStream is = getClass().getResourceAsStream(imgPath);
            if (is != null) {
                ImageView iv = new ImageView(new Image(is)); iv.setPreserveRatio(true); iv.setFitWidth(280); iv.setLayoutY(offsetY); 
                Rectangle clip = new Rectangle(280, 130); clip.setArcWidth(40); clip.setArcHeight(40); imgContainer.setClip(clip); imgContainer.getChildren().add(iv);
            }
        } catch(Exception e) { imgContainer.setStyle("-fx-background-color: -color-border;"); }
        
        VBox info = new VBox(3); info.setPadding(new Insets(12, 15, 12, 15));
        Label t = new Label(title); t.getStyleClass().add("dashboard-card-title");
        Label s = new Label(sub); s.getStyleClass().add("dashboard-card-sub");
        
        info.getChildren().addAll(t, s); card.getChildren().addAll(imgContainer, info);
        applyAccessibility(card, "Tarjeta de acceso a: " + title);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), card); scaleIn.setToX(1.03); scaleIn.setToY(1.03);
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), card); scaleOut.setToX(1.0); scaleOut.setToY(1.0);
        card.setOnMouseEntered(e -> scaleIn.playFromStart()); card.setOnMouseExited(e -> scaleOut.playFromStart());
        return card;
    }

    class HexagonPolygon extends Polygon {
        HexagonPolygon(double radius, double opacity) {
            for (int i = 0; i < 6; i++) { double angle = 2 * Math.PI / 6 * i; getPoints().addAll(radius * Math.cos(angle), radius * Math.sin(angle)); }
            setFill(Color.web("#FFFFFF", opacity)); setStroke(Color.web("#FFFFFF", opacity * 1.5)); setStrokeWidth(1.2);
        }
    }

    private void abrirAgenda() {
        AgendaView agenda = new AgendaView(); root.setCenter(agenda.getView()); actualizarSeleccionMenu(btnAgenda); controller.hablar("Agenda abierta");
    }

    private void actualizarSeleccionMenu(Button botonSeleccionado) {
        Button[] botones = {btnInicio, btnAgenda, btnActividades, btnGrupos, btnPomodoro, btnLogros, btnReinscripcion, btnAjustes};

        for (Button b : botones) {
            if (b == null) continue;
            b.getStyleClass().remove("menu-btn-active");
            b.getStyleClass().remove("menu-btn");
            
            if (b == botonSeleccionado) {
                b.getStyleClass().add("menu-btn-active");
            } else {
                b.getStyleClass().add("menu-btn");
            }
        }
    }
}