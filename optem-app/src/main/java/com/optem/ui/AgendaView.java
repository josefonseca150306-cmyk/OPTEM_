package com.optem.ui;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.prefs.Preferences;

import com.optem.controllers.AgendaController;
import com.optem.models.Actividad;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AgendaView {

    private final AgendaController controller = new AgendaController();
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    
    private LocalDate fechaActual = LocalDate.now();
    private String vistaActual = "WEEK"; // DAY, WEEK, MONTH
    
    private VBox view; // El contenedor raíz que espera tu Dashboard
    private BorderPane layoutMain;
    private VBox panelCentral;
    private GridPane gridMiniCalendario;
    private Label lblMesMini;
    private Stage stagePrincipal;

    public AgendaView() {
        leerPreferencias();

        view = new VBox();
        view.setStyle("-fx-background-color: transparent;");

        layoutMain = new BorderPane();
        layoutMain.setPadding(new Insets(25, 30, 25, 30));
        VBox.setVgrow(layoutMain, Priority.ALWAYS);

        // --- CABECERA (TOP) ---
        HBox header = crearCabeceraAgenda();
        layoutMain.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 20, 0));

        // --- PANEL IZQUIERDO (MINI CALENDARIO) ---
        VBox panelIzquierdo = crearMiniCalendarioLateral();
        layoutMain.setLeft(panelIzquierdo);
        BorderPane.setMargin(panelIzquierdo, new Insets(0, 20, 0, 0));

        // --- PANEL CENTRAL (AGENDA DINÁMICA) ---
        panelCentral = new VBox();
        panelCentral.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");
        
        ScrollPane scrollCentral = new ScrollPane(panelCentral);
        scrollCentral.setFitToWidth(true);
        scrollCentral.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        layoutMain.setCenter(scrollCentral);

        renderizarVistaActual();

        view.getChildren().add(layoutMain);

        view.sceneProperty().addListener((obs, oldS, newS) -> {
            if (newS != null) newS.windowProperty().addListener((o, oldW, newW) -> { if(newW != null) stagePrincipal = (Stage) newW; });
        });
    }

    public VBox getView() {
        return view;
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

    private HBox crearCabeceraAgenda() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titulo = new Label("📅 Mi Agenda Universitaria");
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 26px; -fx-text-fill: " + colorTextMain + "; -fx-font-family: 'Segoe UI Emoji';");

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox selectorVista = new HBox(0);
        selectorVista.setStyle("-fx-background-color: " + colorBgBase + "; -fx-background-radius: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8;");
        
        Button btnDia = crearBotonVista("Día", "DAY");
        Button btnSemana = crearBotonVista("Semana", "WEEK");
        Button btnMes = crearBotonVista("Mes", "MONTH");
        selectorVista.getChildren().addAll(btnDia, btnSemana, btnMes);

        HBox navBox = new HBox(5);
        Button btnAtras = new Button("◀"); btnAtras.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-cursor: hand; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        Button btnAdelante = new Button("▶"); btnAdelante.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-cursor: hand; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");
        Button btnHoy = new Button("Hoy"); btnHoy.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-cursor: hand; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-font-weight: bold;");

        btnAtras.setOnAction(e -> { fechaActual = controller.navegarFecha(fechaActual, vistaActual, -1); renderizarVistaActual(); });
        btnAdelante.setOnAction(e -> { fechaActual = controller.navegarFecha(fechaActual, vistaActual, 1); renderizarVistaActual(); });
        btnHoy.setOnAction(e -> { fechaActual = LocalDate.now(); renderizarVistaActual(); });
        
        navBox.getChildren().addAll(btnAtras, btnHoy, btnAdelante);
        header.getChildren().addAll(titulo, spacer, selectorVista, navBox);
        return header;
    }

    private Button crearBotonVista(String texto, String idVista) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
        btn.setOnAction(e -> {
            vistaActual = idVista;
            renderizarVistaActual();
            HBox parent = (HBox) btn.getParent();
            for (javafx.scene.Node n : parent.getChildren()) {
                n.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
            }
            btn.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 8 15;");
        });
        if(idVista.equals(vistaActual)) btn.fire(); 
        return btn;
    }

    private VBox crearMiniCalendarioLateral() {
        VBox panel = new VBox(15);
        panel.setPrefWidth(250);
        panel.setMaxWidth(250);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        lblMesMini = new Label();
        lblMesMini.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + colorTextMain + ";");

        gridMiniCalendario = new GridPane();
        gridMiniCalendario.setHgap(5); gridMiniCalendario.setVgap(5);
        
        String[] diasCortos = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"};
        for (int i = 0; i < 7; i++) {
            Label l = new Label(diasCortos[i]);
            l.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            GridPane.setHalignment(l, HPos.CENTER);
            gridMiniCalendario.add(l, i, 0);
        }

        panel.getChildren().addAll(lblMesMini, gridMiniCalendario);
        actualizarMiniCalendario();
        return panel;
    }

    private void actualizarMiniCalendario() {
        gridMiniCalendario.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
        
        YearMonth ym = YearMonth.from(fechaActual);
        lblMesMini.setText(ym.format(DateTimeFormatter.ofPattern("MMMM yyyy")).toUpperCase());
        
        LocalDate primerDiaMes = ym.atDay(1);
        int offset = primerDiaMes.getDayOfWeek().getValue() - 1; 
        int diasEnMes = ym.lengthOfMonth();

        int row = 1; int col = offset;
        
        for (int dia = 1; dia <= diasEnMes; dia++) {
            LocalDate fechaCelda = ym.atDay(dia);
            Label lblNum = new Label(String.valueOf(dia));
            lblNum.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-size: 12px;");
            
            VBox btnDia = new VBox(lblNum);
            btnDia.setAlignment(Pos.CENTER);
            btnDia.setMinSize(25, 25);
            btnDia.setStyle("-fx-background-color: transparent; -fx-background-radius: 15; -fx-cursor: hand;");
            
            if (fechaCelda.equals(LocalDate.now())) {
                btnDia.setStyle("-fx-background-color: " + colorFuerte + "40; -fx-background-radius: 15; -fx-cursor: hand;");
                lblNum.setStyle("-fx-text-fill: " + colorFuerte + "; -fx-font-weight: bold; -fx-font-size: 12px;");
            }
            if (fechaCelda.equals(fechaActual)) {
                btnDia.setStyle("-fx-background-color: " + colorFuerte + "; -fx-background-radius: 15; -fx-cursor: hand;");
                lblNum.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
            }

            btnDia.setOnMouseClicked(e -> {
                fechaActual = fechaCelda;
                vistaActual = "DAY"; 
                renderizarVistaActual();
            });

            GridPane.setHalignment(btnDia, HPos.CENTER);
            gridMiniCalendario.add(btnDia, col, row);
            
            col++;
            if (col > 6) { col = 0; row++; }
        }
    }

    private void renderizarVistaActual() {
        panelCentral.getChildren().clear();
        actualizarMiniCalendario();

        if (vistaActual.equals("DAY")) {
            renderizarVistaDia(fechaActual);
        } else if (vistaActual.equals("WEEK")) {
            LocalDate inicioSemana = fechaActual.with(DayOfWeek.MONDAY);
            renderizarVistaSemana(inicioSemana);
        } else {
            renderizarVistaMes(YearMonth.from(fechaActual));
        }
    }

    private void renderizarVistaDia(LocalDate dia) {
        Label lblDia = new Label(dia.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM")).toUpperCase());
        lblDia.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + colorTextMain + "; -fx-padding: 20;");
        panelCentral.getChildren().add(lblDia);

        List<Actividad> actividades = controller.getActividadesDelDia(dia);
        
        if (actividades == null || actividades.isEmpty()) {
            panelCentral.getChildren().add(crearMensajeVacio("Día Libre", "No tienes actividades programadas para este día."));
            return;
        }

        VBox lista = new VBox(10);
        lista.setPadding(new Insets(0, 20, 20, 20));
        for (Actividad act : actividades) {
            lista.getChildren().add(crearTarjetaEventoListado(act));
        }
        panelCentral.getChildren().add(lista);
    }

    private void renderizarVistaSemana(LocalDate inicioSemana) {
        Label lblSemana = new Label("SEMANA DEL " + inicioSemana.format(DateTimeFormatter.ofPattern("d MMM")) + " AL " + inicioSemana.plusDays(6).format(DateTimeFormatter.ofPattern("d MMM yyyy")));
        lblSemana.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + colorTextMain + "; -fx-padding: 20;");
        panelCentral.getChildren().add(lblSemana);

        HBox gridSemana = new HBox(10);
        gridSemana.setPadding(new Insets(0, 20, 20, 20));
        
        String[] nombresDias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        
        for (int i = 0; i < 7; i++) {
            LocalDate dia = inicioSemana.plusDays(i);
            VBox colDia = new VBox(10);
            colDia.setPrefWidth(200); HBox.setHgrow(colDia, Priority.ALWAYS);
            
            Label lblNum = new Label(String.valueOf(dia.getDayOfMonth()));
            lblNum.setStyle("-fx-font-weight: bold; -fx-font-size: 24px; -fx-text-fill: " + (dia.equals(LocalDate.now()) ? colorFuerte : colorTextMain) + ";");
            
            Label lblNom = new Label(nombresDias[i]);
            lblNom.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");
            
            VBox headerDia = new VBox(lblNom, lblNum);
            headerDia.setAlignment(Pos.CENTER);
            headerDia.setStyle("-fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-width: 0 0 2 0;");
            colDia.getChildren().add(headerDia);

            List<Actividad> acts = controller.getActividadesDelDia(dia);
            if(acts != null) {
                for (Actividad act : acts) {
                    colDia.getChildren().add(crearBloqueEventoMini(act));
                }
            }
            gridSemana.getChildren().add(colDia);
        }
        panelCentral.getChildren().add(gridSemana);
    }

    private void renderizarVistaMes(YearMonth mes) {
        Label lblMes = new Label(mes.format(DateTimeFormatter.ofPattern("MMMM yyyy")).toUpperCase());
        lblMes.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + colorTextMain + "; -fx-padding: 20;");
        panelCentral.getChildren().add(lblMes);

        GridPane gridMes = new GridPane();
        gridMes.setPadding(new Insets(0, 20, 20, 20));
        gridMes.setHgap(5); gridMes.setVgap(5);
        
        String[] diasCortos = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
        for (int i = 0; i < 7; i++) {
            Label l = new Label(diasCortos[i]);
            l.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMuted + "; -fx-padding: 10;");
            GridPane.setHalignment(l, HPos.CENTER);
            
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(14.28);
            gridMes.getColumnConstraints().add(cc);
            gridMes.add(l, i, 0);
        }

        LocalDate primerDia = mes.atDay(1);
        int offset = primerDia.getDayOfWeek().getValue() - 1; 
        int diasEnMes = mes.lengthOfMonth();
        int row = 1; int col = offset;

        for (int dia = 1; dia <= diasEnMes; dia++) {
            LocalDate fechaCelda = mes.atDay(dia);
            VBox celdaDia = new VBox(2);
            celdaDia.setPadding(new Insets(5));
            celdaDia.setMinHeight(80);
            celdaDia.setStyle("-fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-background-color: " + (fechaCelda.equals(LocalDate.now()) ? colorFuerte+"10" : colorBgBase) + ";");
            
            Label lblNum = new Label(String.valueOf(dia));
            lblNum.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (fechaCelda.equals(LocalDate.now()) ? colorFuerte : colorTextMain) + ";");
            celdaDia.getChildren().add(lblNum);

            List<Actividad> acts = controller.getActividadesDelDia(fechaCelda);
            if(acts != null) {
                int mostrados = 0;
                for (Actividad act : acts) {
                    if (mostrados >= 2) {
                        Label lblMas = new Label("+" + (acts.size() - 2) + " más");
                        lblMas.setStyle("-fx-font-size: 9px; -fx-text-fill: " + colorFuerte + "; -fx-font-weight: bold;");
                        celdaDia.getChildren().add(lblMas);
                        break;
                    }
                    celdaDia.getChildren().add(crearMicroPildora(act));
                    mostrados++;
                }
            }
            
            celdaDia.setOnMouseClicked(e -> {
                fechaActual = fechaCelda;
                vistaActual = "DAY";
                renderizarVistaActual();
            });

            gridMes.add(celdaDia, col, row);
            col++;
            if (col > 6) { col = 0; row++; }
        }
        panelCentral.getChildren().add(gridMes);
    }

    private HBox crearTarjetaEventoListado(Actividad act) {
        boolean caducada = controller.isActividadCaducada(act) && "COMPLETADO".equals(act.getEstado());
        boolean completadaReal = "COMPLETADO".equals(act.getEstado()) && !caducada;
        
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + colorBgBase + "; -fx-background-radius: 8; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-border-width: 0 0 0 5; -fx-cursor: hand;");
        
        if (caducada) card.setStyle(card.getStyle().replace("-color-border", "#D32F2F") + " -fx-opacity: 0.7;");
        else if (completadaReal) card.setStyle(card.getStyle().replace("-color-border", "#388E3C") + " -fx-opacity: 0.8;");
        else card.setStyle(card.getStyle().replace("-color-border", colorFuerte));

        String textoHora = (act.getHoraVencimiento() != null) ? act.getHoraVencimiento().toString() : "--:--";
        Label lblHora = new Label(textoHora);
        lblHora.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorFuerte + "; -fx-min-width: 80px;");
        
        VBox infoBox = new VBox(3);
        String titulo = (act.getNombre() != null) ? act.getNombre() : "Sin Título";
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";" + (completadaReal ? " -fx-strikethrough: true;" : ""));
        
        String materiaInfo = (act.getMateria() != null) ? act.getMateria() : "General";
        String tipoInfo = (act.getTipo() != null) ? act.getTipo() : "Actividad";
        Label lblSub = new Label(materiaInfo + " • " + tipoInfo);
        lblSub.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");
        
        infoBox.getChildren().addAll(lblTit, lblSub);
        
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        
        Label status = new Label();
        status.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 5 10; -fx-background-radius: 12;");
        if(caducada) { status.setText("❌ CADUCADA"); status.setStyle(status.getStyle() + "-fx-text-fill: #D32F2F; -fx-background-color: #ffe6e6;"); }
        else if(completadaReal) { status.setText("✅ COMPLETADA"); status.setStyle(status.getStyle() + "-fx-text-fill: #388E3C; -fx-background-color: #E8F5E9;"); }
        else { status.setText("⏳ PENDIENTE"); status.setStyle(status.getStyle() + "-fx-text-fill: " + colorFuerte + "; -fx-background-color: " + colorFuerte + "20;"); }

        card.getChildren().addAll(lblHora, infoBox, sp, status);
        card.setOnMouseClicked(e -> mostrarVentanaDetalles(act, caducada, completadaReal));
        return card;
    }

    private VBox crearBloqueEventoMini(Actividad act) {
        boolean caducada = controller.isActividadCaducada(act) && "COMPLETADO".equals(act.getEstado());
        VBox bloque = new VBox(2);
        bloque.setPadding(new Insets(8));
        bloque.setStyle("-fx-background-color: " + (caducada ? colorBgBase : colorFuerte+"20") + "; -fx-background-radius: 6; -fx-border-color: " + (caducada ? "#D32F2F" : colorFuerte) + "; -fx-border-width: 0 0 0 3; -fx-cursor: hand;");
        if(caducada) bloque.setOpacity(0.6);

        String titulo = (act.getNombre() != null) ? act.getNombre() : "Evento";
        Label lTit = new Label(titulo);
        lTit.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        
        String textoHora = (act.getHoraVencimiento() != null) ? act.getHoraVencimiento().toString() : "00:00";
        Label lHora = new Label(textoHora);
        lHora.setStyle("-fx-font-size: 10px; -fx-text-fill: " + colorTextMuted + ";");
        
        bloque.getChildren().addAll(lTit, lHora);
        bloque.setOnMouseClicked(e -> mostrarVentanaDetalles(act, caducada, "COMPLETADO".equals(act.getEstado()) && !caducada));
        return bloque;
    }

    private Label crearMicroPildora(Actividad act) {
        boolean caducada = controller.isActividadCaducada(act) && "COMPLETADO".equals(act.getEstado());
        String titulo = (act.getNombre() != null) ? act.getNombre() : "Evento";
        Label lbl = new Label("• " + titulo);
        lbl.setStyle("-fx-font-size: 9px; -fx-text-fill: " + (caducada ? "#D32F2F" : colorFuerte) + "; -fx-font-weight: bold;");
        return lbl;
    }

    private VBox crearMensajeVacio(String titulo, String msj) {
        VBox b = new VBox(10); b.setAlignment(Pos.CENTER); b.setPadding(new Insets(60, 0, 60, 0));
        Label l1 = new Label("☕ " + titulo); l1.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMuted + "; -fx-font-family: 'Segoe UI Emoji';");
        Label l2 = new Label(msj); l2.setStyle("-fx-text-fill: " + colorTextMuted + ";");
        b.getChildren().addAll(l1, l2);
        return b;
    }

    private void mostrarVentanaDetalles(Actividad act, boolean caducada, boolean completada) {
        Stage popup = new Stage();
        if (stagePrincipal != null) popup.initOwner(stagePrincipal);
        popup.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + (caducada ? "#D32F2F" : colorFuerte) + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 40, 0, 0, 10);");

        HBox topBar = new HBox(15); topBar.setAlignment(Pos.CENTER_LEFT);
        String tipoInfo = (act.getTipo() != null) ? act.getTipo() : "Actividad";
        Label icon = new Label(tipoInfo.equals("Tarea") ? "📝" : "📅");
        icon.setStyle("-fx-font-size: 30px; -fx-font-family: 'Segoe UI Emoji';");
        
        VBox titBox = new VBox(2);
        String titulo = (act.getNombre() != null) ? act.getNombre() : "Sin Título";
        Label lblT = new Label(titulo); lblT.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        
        String materiaInfo = (act.getMateria() != null) ? act.getMateria() : "General";
        Label lblM = new Label(materiaInfo + " • " + tipoInfo); lblM.setStyle("-fx-text-fill: " + colorTextMuted + ";");
        titBox.getChildren().addAll(lblT, lblM);
        
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button btnCerrar = new Button("✖");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand; -fx-font-size: 16px;");
        btnCerrar.setOnAction(e -> popup.close());
        
        topBar.getChildren().addAll(icon, titBox, sp, btnCerrar);

        GridPane gridTiempo = new GridPane();
        gridTiempo.setHgap(30); gridTiempo.setVgap(10);
        gridTiempo.setStyle("-fx-background-color: " + colorBgBase + "; -fx-padding: 15; -fx-background-radius: 8;");
        
        gridTiempo.add(crearInfoLabel("FECHA ASIGNADA", "Hoy, 07:00 AM"), 0, 0); 
        
        String fechaTexto = (act.getFecha() != null) ? act.getFecha().toString() : "---";
        String horaTexto = (act.getHoraVencimiento() != null) ? act.getHoraVencimiento().toString() : "---";
        gridTiempo.add(crearInfoLabel(tipoInfo.equals("Tarea") ? "FECHA LÍMITE" : "HORA EVENTO", fechaTexto + " • " + horaTexto), 1, 0);
        
        Label lblEstado = new Label(caducada ? "❌ CADUCADA" : (completada ? "✅ COMPLETADA" : "⏳ PENDIENTE"));
        lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (caducada ? "#D32F2F" : (completada ? "#388E3C" : colorFuerte)) + ";");
        gridTiempo.add(crearInfoLabel("ESTADO ACTUAL", lblEstado.getText()), 2, 0);

        VBox descBox = new VBox(10);
        Label lD = new Label("Mensaje / Instrucciones:"); lD.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        String descripcion = (act.getDescripcion() != null) ? act.getDescripcion() : "Sin detalles adicionales.";
        Label txtD = new Label(descripcion); txtD.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-wrap-text: true; -fx-line-spacing: 5px;");
        descBox.getChildren().addAll(lD, txtD);

        root.getChildren().addAll(topBar, gridTiempo, descBox);

        Scene scene = new Scene(root, 600, 400);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        
        if (stagePrincipal != null) {
            popup.setX(stagePrincipal.getX() + (stagePrincipal.getWidth() - scene.getWidth()) / 2);
            popup.setY(stagePrincipal.getY() + (stagePrincipal.getHeight() - scene.getHeight()) / 2);
        }
        popup.show();
    }
    
    private VBox crearInfoLabel(String titulo, String valor) {
        VBox v = new VBox(2);
        Label t = new Label(titulo); t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMuted + ";");
        Label val = new Label(valor); val.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        v.getChildren().addAll(t, val);
        return v;
    }
}