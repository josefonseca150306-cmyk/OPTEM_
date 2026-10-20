package com.optem.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import com.optem.controllers.AgendaController;
import com.optem.models.Actividad;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AgendaView {
    private VBox container;
    private StackPane contentArea;
    private Label lblDateRange;
    private LocalDate baseDate = LocalDate.now();
    private ViewMode currentMode = ViewMode.WEEK;
    private Button btnDay, btnWeek, btnMonth;
    private String colorFuerte, colorClaro;

    // Instancia de nuestro nuevo controlador
    private final AgendaController controller = new AgendaController();

    public enum ViewMode { DAY, WEEK, MONTH }

    public AgendaView() {
        // Obtenemos los colores a través del controlador
        this.colorFuerte = controller.getThemeColor("PREF_ACCENT_COLOR", "#3D5A4D");
        this.colorClaro = controller.getThemeColor("PREF_ACCENT_LIGHT", "#E9EDC9");

        container = new VBox(25);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: transparent;");

        renderHeader();
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 20; -fx-padding: 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 15, 0, 0, 4);");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        
        updateView();
        container.getChildren().add(contentArea);
    }

    private void renderHeader() {
        HBox header = new HBox(25);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("📅 Mi Agenda");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: 800; -fx-text-fill: -color-text-main;");

        btnDay = createModeBtn("Día", ViewMode.DAY);
        btnWeek = createModeBtn("Semana", ViewMode.WEEK);
        btnMonth = createModeBtn("Mes", ViewMode.MONTH);

        HBox selectors = new HBox(5);
        selectors.setStyle("-fx-background-color: -color-bg-base; -fx-background-radius: 12; -fx-padding: 5; -fx-border-color: -color-border; -fx-border-radius: 12;");
        selectors.getChildren().addAll(btnDay, btnWeek, btnMonth);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox nav = new HBox(15);
        nav.setAlignment(Pos.CENTER);
        Button btnPrev = createNavBtn("◀");
        lblDateRange = new Label();
        lblDateRange.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -color-text-main; -fx-min-width: 240; -fx-alignment: center;");
        Button btnNext = createNavBtn("▶");
        
        btnPrev.setOnAction(e -> { 
            baseDate = controller.navegarFecha(baseDate, currentMode.name(), -1); 
            updateView(); 
        });
        btnNext.setOnAction(e -> { 
            baseDate = controller.navegarFecha(baseDate, currentMode.name(), 1); 
            updateView(); 
        });
        nav.getChildren().addAll(btnPrev, lblDateRange, btnNext);
        
        header.getChildren().addAll(title, selectors, spacer, nav);
        container.getChildren().add(header);
    }

    private void updateView() {
        refreshButtonStyles();
        contentArea.getChildren().clear();
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        if (currentMode == ViewMode.DAY) {
            lblDateRange.setText(baseDate.format(DateTimeFormatter.ofPattern("d 'de' MMMM", new Locale("es", "ES"))));
            scroll.setContent(createDayLayout(baseDate));
        } else if (currentMode == ViewMode.WEEK) {
            LocalDate start = baseDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            lblDateRange.setText(start.format(DateTimeFormatter.ofPattern("d MMM", new Locale("es", "ES"))) + " - " + start.plusDays(6).format(DateTimeFormatter.ofPattern("d MMM", new Locale("es", "ES"))));
            scroll.setContent(createWeekLayout(start));
        } else {
            lblDateRange.setText(baseDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES"))).toUpperCase());
            scroll.setContent(createMonthLayout());
        }
        contentArea.getChildren().add(scroll);
    }

    private VBox createDayLayout(LocalDate date) {
        VBox layout = new VBox(0);
        for (int h = 7; h < 22; h++) {
            HBox row = new HBox(20); row.setPrefHeight(80); row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-border-color: -color-border; -fx-border-width: 0 0 1 0; -fx-padding: 10 0;");
            Label time = new Label(String.format("%02d:00", h)); 
            time.setStyle("-fx-font-weight: bold; -fx-text-fill: -color-text-muted; -fx-font-size: 14px;");
            time.setPrefWidth(60);
            
            FlowPane events = new FlowPane(15, 15);
            for (Actividad act : controller.getActividadesDelDia(date)) {
                if (act.getHoraVencimiento().getHour() == h) events.getChildren().add(createCard(act, date));
            }
            row.getChildren().addAll(time, events);
            layout.getChildren().add(row);
        }
        return layout;
    }

    private GridPane createWeekLayout(LocalDate start) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        for (int i = 0; i < 7; i++) {
            LocalDate day = start.plusDays(i);
            VBox col = new VBox(12);
            col.setStyle("-fx-border-color: -color-border; -fx-border-width: 0 1 0 0; -fx-padding: 10;");
            
            Label lblDay = new Label(day.getDayOfWeek().toString().substring(0,3) + " " + day.getDayOfMonth());
            lblDay.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + (day.equals(LocalDate.now()) ? colorFuerte : "-color-text-main") + ";");
            col.getChildren().add(lblDay);
            
            for (Actividad act : controller.getActividadesDelDia(day)) {
                col.getChildren().add(createCard(act, day));
            }
            ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(100.0/7); grid.getColumnConstraints().add(cc);
            grid.add(col, i, 0);
        }
        return grid;
    }

    private GridPane createMonthLayout() {
        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        LocalDate first = baseDate.with(TemporalAdjusters.firstDayOfMonth());
        int offset = first.getDayOfWeek().getValue() - 1;
        
        for (int i = 0; i < 42; i++) {
            LocalDate d = first.plusDays(i - offset);
            VBox cell = new VBox(5); cell.setPrefSize(120, 100); 
            
            boolean isHoliday = d.getMonthValue() == 5 && (d.getDayOfMonth() == 1 || d.getDayOfMonth() == 5 || d.getDayOfMonth() == 15);
            String bg = d.equals(LocalDate.now()) ? colorClaro : (isHoliday ? "#FFF0F5" : "transparent");
            
            cell.setStyle("-fx-background-color: " + bg + "; -fx-border-color: -color-border; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8; -fx-cursor: hand;");
            
            Label dayNum = new Label(String.valueOf(d.getDayOfMonth()));
            dayNum.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (d.equals(LocalDate.now()) ? colorFuerte : "-color-text-main") + ";");
            
            HBox dots = new HBox(4);
            for (Actividad act : controller.getActividadesDelDia(d)) {
                dots.getChildren().add(new Circle(4, Color.web(controller.getBorderColor(act.getTipo()))));
            }
            
            if (isHoliday) cell.getChildren().addAll(dayNum, new Label("Festivo"){{setStyle("-fx-font-size:10px;-fx-text-fill:#D32F2F;");}}, dots);
            else cell.getChildren().addAll(dayNum, dots);
            
            // Drag and Drop de días completos al Chatbot
            cell.setOnDragDetected(evt -> {
                Dragboard db = cell.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString(controller.generarDatosArrastreDia(d));
                db.setContent(content);
                evt.consume();
            });

            grid.add(cell, i % 7, (i / 7) + 1);
        }
        return grid;
    }

    private VBox createCard(Actividad act, LocalDate d) {
        VBox card = new VBox(4);
        boolean isPast = d.isBefore(LocalDate.now());
        
        // Asignamos colores usando el controlador
        String bgCard = controller.getBgColor(act.getTipo());
        String borderCard = controller.getBorderColor(act.getTipo());

        card.setStyle("-fx-background-color: " + bgCard + "; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: " + borderCard + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 8; -fx-cursor: hand;");
        if (isPast) card.setOpacity(0.65);

        Label lCat = new Label(act.getTipo().toUpperCase() + (isPast ? " 🔒" : ""));
        lCat.setStyle("-fx-font-size: 10px; -fx-text-fill: " + borderCard + "; -fx-font-weight: 900;");
        
        Label lTitle = new Label(act.getNombre());
        lTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1B263B; -fx-wrap-text: true;");
        
        card.getChildren().addAll(lCat, lTitle);
        
        // Drag and Drop para Chatbot
        card.setOnDragDetected(evt -> {
            Dragboard db = card.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(controller.generarDatosArrastreActividad(act));
            db.setContent(content);
            evt.consume();
        });

        card.setOnMouseClicked(e -> {
            if (isPast) new Alert(Alert.AlertType.INFORMATION, "🔒 Esta actividad ya venció y no se puede modificar.").show();
            else System.out.println("Abrir edición de: " + act.getNombre()); // Aquí irá el modal
        });
        return card;
    }

    private Button createModeBtn(String txt, ViewMode mode) {
        Button b = new Button(txt);
        b.setOnAction(e -> { currentMode = mode; updateView(); });
        return b;
    }

    private void refreshButtonStyles() {
        String active = "-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 20; -fx-font-weight: bold;";
        String inactive = "-fx-background-color: transparent; -fx-text-fill: -color-text-muted; -fx-padding: 8 20; -fx-font-weight: bold; -fx-cursor: hand;";
        btnDay.setStyle(currentMode == ViewMode.DAY ? active : inactive);
        btnWeek.setStyle(currentMode == ViewMode.WEEK ? active : inactive);
        btnMonth.setStyle(currentMode == ViewMode.MONTH ? active : inactive);
    }

    private Button createNavBtn(String txt) {
        Button b = new Button(txt);
        b.setStyle("-fx-background-color: -color-bg-panel; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 5 15; -fx-border-color: -color-border; -fx-border-radius: 8;");
        return b;
    }

    public VBox getView() { return container; }
}