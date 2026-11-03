package com.optem.ui;

import com.optem.controllers.AdminController;
import com.optem.controllers.ReinscripcionController;
import com.optem.logic.NotificacionManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ReinscripcionView extends BorderPane {

    private final AdminController adminController = new AdminController();
    private final ReinscripcionController controller = new ReinscripcionController();

    // Variables unificadas y limpias (sin errores ni advertencias)
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;
    private final Map<String, ComboBox<String>> combosMaterias = new HashMap<>();
    private GridPane gridHorario;
    private boolean existeEmpalme = false;

    public ReinscripcionView() {
        leerPreferencias();
        this.setPadding(new Insets(20, 30, 20, 30));
        this.setStyle("-fx-background-color: transparent;");

        // --- CABECERA CON BOTÓN LIMPIAR ---
        BorderPane header = new BorderPane();
        VBox titleBox = new VBox(5);
        Label titulo = new Label("📑 Reinscripción ISW (6to Semestre)");
        titulo.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-family: 'Segoe UI Emoji';");
        titulo.getStyleClass().add("lbl-titulo-banner"); 
        
        Label subtitulo = new Label("Arma tu horario. Cuidado con los empalmes.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + ";");
        subtitulo.getStyleClass().add("lbl-text-normal");
        titleBox.getChildren().addAll(titulo, subtitulo);
        header.setLeft(titleBox);

        Button btnLimpiar = new Button("🗑 Borrar Horario");
        btnLimpiar.setStyle("-fx-background-color: transparent; -fx-border-color: #D32F2F; -fx-text-fill: #D32F2F; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8 15;");
        btnLimpiar.setOnAction(e -> {
            for (ComboBox<String> combo : combosMaterias.values()) {
                combo.getSelectionModel().select(0);
            }
            repintarHorario();
            NotificacionManager.mostrarNotificacion("Limpieza", "El horario ha sido reiniciado.", "INFO");
        });
        header.setRight(btnLimpiar);
        BorderPane.setAlignment(btnLimpiar, Pos.CENTER_RIGHT);

        this.setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 20, 0));

        if (!adminController.isReinscripcionActiva()) {
            mostrarPantallaCerrada();
        } else {
            construirPantallaInscripcion();
        }
    }

    private void construirPantallaInscripcion() {
        VBox layoutVertical = new VBox(20);
        
        HBox body = new HBox(20);
        body.setAlignment(Pos.TOP_CENTER);

        // ==========================================
        // COLUMNA IZQUIERDA: CATÁLOGO CLASIFICADO
        // ==========================================
        VBox colIzq = new VBox(15);
        colIzq.setPrefWidth(420);
        colIzq.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-padding: 20; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12;");

        VBox listaMaterias = new VBox(10);
        
        listaMaterias.getChildren().add(crearTituloSeccion("📚 Materias Pendientes (6to Semestre)"));
        for (ReinscripcionController.MateriaDTO m : controller.getMateriasDisponibles()) {
            if(m.categoria.equals("REGULAR")) listaMaterias.getChildren().add(crearCardMateria(m));
        }

        listaMaterias.getChildren().add(crearTituloSeccion("⚠️ Segunda Inscripción / Rezagadas"));
        for (ReinscripcionController.MateriaDTO m : controller.getMateriasDisponibles()) {
            if(m.categoria.equals("REZAGO")) listaMaterias.getChildren().add(crearCardMateria(m));
        }

        listaMaterias.getChildren().add(crearTituloSeccion("🚀 Adelantar Materias (Semestres Pares)"));
        for (ReinscripcionController.MateriaDTO m : controller.getMateriasDisponibles()) {
            if(m.categoria.equals("ADELANTO")) listaMaterias.getChildren().add(crearCardMateria(m));
        }

        ScrollPane scrollMaterias = new ScrollPane(listaMaterias);
        scrollMaterias.setFitToWidth(true);
        scrollMaterias.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollMaterias, Priority.ALWAYS);

        Button btnInscribir = new Button("Validar e Inscribir Horario");
        btnInscribir.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
        btnInscribir.setMaxWidth(Double.MAX_VALUE);
        btnInscribir.setOnAction(e -> {
            if (existeEmpalme) {
                NotificacionManager.mostrarNotificacion("❌ Error de Validación", "No puedes inscribirte. Tienes materias chocando en el mismo horario.", "URGENTE");
            } else {
                mostrarAlertaHojaPago();
            }
        });

        colIzq.getChildren().addAll(scrollMaterias, btnInscribir);

        // ==========================================
        // COLUMNA DERECHA: CALENDARIO Y GENERADOR IA
        // ==========================================
        VBox colDer = new VBox(15);
        colDer.setPrefWidth(700);
        HBox.setHgrow(colDer, Priority.ALWAYS);

        HBox panelIA = new HBox(10);
        panelIA.setAlignment(Pos.CENTER_LEFT);
        panelIA.setStyle("-fx-background-color: " + colorFuerte + "15; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;");
        
        Label lblIA = new Label("🤖 Auto-IA:");
        lblIA.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorFuerte + ";");
        
        panelIA.getChildren().addAll(
            lblIA,
            crearBotonIA("Matutino", "MATUTINO"),
            crearBotonIA("Mañanas Libres", "TARDES"),
            crearBotonIA("Fines Largos", "SIN_VIERNES"),
            crearBotonIA("Con Adelanto", "CON_ADELANTO"),
            crearBotonIA("Con Rezago", "CON_REZAGO")
        );

        VBox contCalendario = new VBox(10);
        contCalendario.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-padding: 15; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12;");
        VBox.setVgrow(contCalendario, Priority.ALWAYS);

        gridHorario = new GridPane();
        gridHorario.setGridLinesVisible(false);
        gridHorario.setHgap(5); gridHorario.setVgap(5);
        
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(i == 0 ? 10 : 15);
            gridHorario.getColumnConstraints().add(cc);
        }

        String[] dias = {"Hora", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        for (int i = 0; i < dias.length; i++) {
            Label l = new Label(dias[i]);
            l.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-alignment: center;");
            l.setMaxWidth(Double.MAX_VALUE);
            gridHorario.add(l, i, 0);
        }

        for (int i = 0; i < 13; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPrefHeight(25); 
            gridHorario.getRowConstraints().add(rc);
            
            Label lHora = new Label((i + 7) + ":00");
            lHora.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 10px; -fx-alignment: top_right; -fx-padding: 0 5 0 0;");
            lHora.setMaxWidth(Double.MAX_VALUE);
            gridHorario.add(lHora, 0, i + 1);
        }

        contCalendario.getChildren().add(gridHorario);
        colDer.getChildren().addAll(panelIA, contCalendario);

        body.getChildren().addAll(colIzq, colDer);
        layoutVertical.getChildren().addAll(body, crearMapaCurricularInteractivo());
        
        ScrollPane scrollGlobal = new ScrollPane(layoutVertical);
        scrollGlobal.setFitToWidth(true);
        scrollGlobal.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        this.setCenter(scrollGlobal);
        
        repintarHorario();
    }

    private Label crearTituloSeccion(String texto) {
        Label lbl = new Label(texto);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + colorTextMain + "; -fx-padding: 10 0 5 0; -fx-border-color: " + colorBorder + "; -fx-border-width: 0 0 1 0;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        return lbl;
    }

    private VBox crearCardMateria(ReinscripcionController.MateriaDTO m) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: " + m.colorBorde + "; -fx-border-width: 0 0 0 4; -fx-background-color: " + colorBgBase + "; -fx-background-radius: 0 6 6 0;");

        Label lblNom = new Label(m.titulo);
        lblNom.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 12px;");
        
        Label lblCred = new Label(m.creditos + (m.mixta ? " • Opción en Línea" : ""));
        lblCred.setStyle("-fx-font-size: 10px; -fx-text-fill: " + colorTextMuted + ";");

        ComboBox<String> cmbGrupos = new ComboBox<>();
        cmbGrupos.getItems().addAll(m.opciones);
        cmbGrupos.getSelectionModel().selectFirst();
        cmbGrupos.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 4; -fx-font-size: 11px;");
        cmbGrupos.setMaxWidth(Double.MAX_VALUE);
        
        cmbGrupos.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-background-color: " + colorBgPanel + "; -fx-text-fill: " + colorTextMain + ";");
            }
        });
        cmbGrupos.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle("-fx-text-fill: " + colorTextMain + ";");
            }
        });

        cmbGrupos.setOnAction(e -> repintarHorario());
        combosMaterias.put(m.titulo, cmbGrupos);
        card.getChildren().addAll(lblNom, lblCred, cmbGrupos);
        return card;
    }

    private Button crearBotonIA(String texto, String perfil) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 6; -fx-text-fill: " + colorTextMain + "; -fx-cursor: hand; -fx-font-size: 11px;");
        btn.setOnAction(e -> {
            for (ComboBox<String> combo : combosMaterias.values()) combo.getSelectionModel().select(0);
            
            Map<String, String> recomendacion = controller.generarHorarioInteligente(perfil);
            for (Map.Entry<String, String> entry : recomendacion.entrySet()) {
                if (combosMaterias.containsKey(entry.getKey())) {
                    combosMaterias.get(entry.getKey()).setValue(entry.getValue());
                }
            }
            repintarHorario();
            NotificacionManager.mostrarNotificacion("IA Aplicada", "Horario generado: " + texto, "INFO");
        });
        return btn;
    }

    private void repintarHorario() {
        existeEmpalme = false;
        gridHorario.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) > 0 && GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);

        for (int col = 1; col < 7; col++) {
            for (int row = 1; row < 14; row++) {
                Region celdaVacia = new Region();
                celdaVacia.setStyle("-fx-background-color: " + colorBgBase + "; -fx-background-radius: 4; -fx-border-color: " + colorBorder + "; -fx-border-width: 0.5;");
                gridHorario.add(celdaVacia, col, row);
            }
        }

        boolean[][] ocupacion = new boolean[7][15]; 
        boolean mostroNotificacion = false;

        for (ReinscripcionController.MateriaDTO m : controller.getMateriasDisponibles()) {
            ComboBox<String> combo = combosMaterias.get(m.titulo);
            if (combo == null) continue;
            
            String seleccion = combo.getValue();
            List<ReinscripcionController.BloqueHorario> bloques = controller.procesarSeleccion(m.titulo, seleccion, m.colorBorde);
            
            for (ReinscripcionController.BloqueHorario b : bloques) {
                boolean hayEmpalmeLocal = false;
                for (int i = 0; i < b.duracion; i++) {
                    if (ocupacion[b.colDia][b.rowHora + i]) {
                        hayEmpalmeLocal = true;
                        existeEmpalme = true;
                    }
                    ocupacion[b.colDia][b.rowHora + i] = true;
                }

                VBox tarjetaClase = new VBox(2);
                tarjetaClase.setAlignment(Pos.CENTER);
                tarjetaClase.setPadding(new Insets(2));
                
                if (hayEmpalmeLocal) {
                    tarjetaClase.setStyle("-fx-background-color: #ffe6e6; -fx-border-color: #D32F2F; -fx-border-width: 2; -fx-background-radius: 6; -fx-border-radius: 6;");
                    Label lblN = new Label("¡EMPALME!"); lblN.setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-font-size: 10px;");
                    tarjetaClase.getChildren().add(lblN);
                    
                    if(!mostroNotificacion) {
                        NotificacionManager.mostrarNotificacion("⚠️ Horario Ocupado", "Las materias seleccionadas chocan en el mismo día y hora.", "URGENTE");
                        mostroNotificacion = true;
                    }
                } else {
                    tarjetaClase.setStyle("-fx-background-color: " + b.colorHex + "30; -fx-border-color: " + b.colorHex + "; -fx-border-width: 0 0 0 4; -fx-background-radius: 4;");
                    Label lblN = new Label(b.texto); lblN.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-weight: bold; -fx-font-size: 9px; -fx-alignment: center; -fx-text-alignment: center; -fx-wrap-text: true;");
                    tarjetaClase.getChildren().add(lblN);
                }

                gridHorario.add(tarjetaClase, b.colDia, b.rowHora + 1, 1, b.duracion);
                GridPane.setHalignment(tarjetaClase, HPos.CENTER);
                GridPane.setValignment(tarjetaClase, VPos.CENTER);
            }
        }
    }
    
    private void mostrarAlertaHojaPago() {
        VBox alertBox = new VBox(15);
        alertBox.setAlignment(Pos.CENTER);
        alertBox.setPadding(new Insets(20));
        alertBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12;");

        Label icon = new Label("📧"); icon.setStyle("-fx-font-size: 40px; -fx-font-family: 'Segoe UI Emoji';");
        Label title = new Label("¡Reinscripción Exitosa!"); title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + colorTextMain + ";");
        Label msg = new Label("Tu hoja de referencia para pago te llegará a tu correo institucional.");
        msg.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Button btnOk = new Button("Entendido");
        btnOk.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        alertBox.getChildren().addAll(icon, title, msg, btnOk);
        
        javafx.stage.Stage popup = new javafx.stage.Stage();
        popup.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        btnOk.setOnAction(e -> popup.close());
        
        javafx.scene.Scene scene = new javafx.scene.Scene(alertBox);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.show();
    }

    private VBox crearMapaCurricularInteractivo() {
        VBox mapaContainer = new VBox(15);
        mapaContainer.setPadding(new Insets(20));
        mapaContainer.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        HBox topLegends = new HBox(20);
        topLegends.setAlignment(Pos.CENTER);
        Label lblMapTit = new Label("Trayectoria ideal INGENIERO EN SOFTWARE (ISW-F16)");
        lblMapTit.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 14px;");
        
        HBox boxSimbologia = new HBox(15);
        boxSimbologia.setAlignment(Pos.CENTER);
        boxSimbologia.getChildren().addAll(
            crearEtiquetaSimbologia("Acreditada", "#008000"), 
            crearEtiquetaSimbologia("Inscrita", "#8BC34A"),   
            crearEtiquetaSimbologia("Pendiente", "transparent", colorBorder)
        );

        topLegends.getChildren().addAll(lblMapTit, new Region(), boxSimbologia);
        HBox.setHgrow(topLegends.getChildren().get(1), Priority.ALWAYS);

        HBox malla = new HBox(15);
        malla.setPadding(new Insets(10));

        malla.getChildren().addAll(
            crearColumnaPeriodo("Periodo_4", new String[]{"Inglés 7|#008000", "Diseño Compiladores|#008000", "Interacción H-C|#008000", "Arq. Computadoras|#008000", "Estructuras Datos|#008000", "Microcontroladores|#008000", "Requisitos Soft|#008000"}),
            crearColumnaPeriodo("Periodo_5", new String[]{"Inglés 8|#8BC34A", "Bases de Datos|#8BC34A", "Innovación Sust.|#8BC34A", "Prog. Paralela|#8BC34A", "Análisis y Diseño|#8BC34A", "Redes y Telecom.|#8BC34A", "Met. Desarrollo|#8BC34A"}),
            crearColumnaPeriodo("Periodo_6", new String[]{"Administración|#008000", "BD Avanzadas|transparent", "Graficación|transparent", "Teoría Lenguajes|#008000", "Desarrollo Web|transparent", "Arq. de Software|transparent", "Pruebas y Mant.|transparent"}),
            crearColumnaPeriodo("Periodo_7", new String[]{"Inteligencia Art.|transparent", "Admin BD|transparent", "Ética Informática|transparent", "Métricas Software|transparent", "Simulación|transparent", "Sistemas Op.|transparent", "Sistemas Dist.|transparent"}),
            crearColumnaPeriodo("Periodo_8", new String[]{"Proc. Imágenes|transparent", "Organización Proyectos|transparent", "Datawarehouse|transparent", "Integradora Prof.|transparent", "Calidad Software|transparent", "Herr. Admin. Sis|transparent", "Seguridad Inf.|transparent"})
        );

        ScrollPane scrollMapa = new ScrollPane(malla);
        scrollMapa.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollMapa.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        mapaContainer.getChildren().addAll(topLegends, scrollMapa);
        return mapaContainer;
    }

    private HBox crearEtiquetaSimbologia(String texto, String colorFill, String... colorBorderStr) {
        HBox box = new HBox(5); box.setAlignment(Pos.CENTER);
        Circle c = new Circle(6);
        c.setFill(Color.web(colorFill));
        if (colorBorderStr.length > 0) {
            c.setStyle("-fx-stroke: " + colorBorderStr[0] + "; -fx-stroke-width: 2;");
        }
        Label l = new Label(texto); l.setStyle("-fx-text-fill: " + colorTextMain + "; -fx-font-size: 11px;");
        box.getChildren().addAll(c, l);
        return box;
    }

    private VBox crearColumnaPeriodo(String titulo, String[] materiasData) {
        VBox col = new VBox(10);
        col.setAlignment(Pos.TOP_CENTER);
        col.setPrefWidth(140);
        
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");
        col.getChildren().add(lblTit);

        for (String md : materiasData) {
            String[] parts = md.split("\\|");
            String nombre = parts[0];
            String bgColor = parts[1];
            
            StackPane nodo = new StackPane();
            nodo.setMinHeight(50);
            nodo.setPadding(new Insets(5));
            
            String borderStyle = bgColor.equals("transparent") ? "-fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 15;" : "-fx-background-radius: 15;";
            nodo.setStyle("-fx-background-color: " + (bgColor.equals("transparent") ? colorBgBase : bgColor) + "; " + borderStyle);
            
            Label lblNom = new Label(nombre.toUpperCase());
            lblNom.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: " + (bgColor.equals("transparent") ? colorTextMain : "white") + "; -fx-alignment: center; -fx-text-alignment: center; -fx-wrap-text: true;");
            
            nodo.getChildren().add(lblNom);
            col.getChildren().add(nodo);
        }
        return col;
    }

    private void mostrarPantallaCerrada() {
        VBox panelCerrado = new VBox(20);
        panelCerrado.setAlignment(Pos.CENTER);
        panelCerrado.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-padding: 50; -fx-border-color: #D32F2F; -fx-border-width: 2; -fx-border-radius: 20; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(211,47,47,0.2), 30, 0, 0, 10);");
        
        Label lblIcono = new Label("🔒");
        lblIcono.setStyle("-fx-font-size: 70px; -fx-font-family: 'Segoe UI Emoji'; -fx-text-fill: " + colorTextMain + ";");
        
        Label lblMensaje = new Label("El periodo de reinscripción se encuentra CERRADO.");
        lblMensaje.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        
        Label lblSub = new Label("Por favor, mantente atento a los comunicados oficiales de la UAEMéx.");
        lblSub.setStyle("-fx-font-size: 16px; -fx-text-fill: " + colorTextMain + ";");
        
        panelCerrado.getChildren().addAll(lblIcono, lblMensaje, lblSub);
        
        VBox centerWrap = new VBox(panelCerrado);
        centerWrap.setAlignment(Pos.CENTER);
        this.setCenter(centerWrap);
    }

    private void leerPreferencias() {
        Preferences prefs = Preferences.userNodeForPackage(DashboardView.class);
        String correoActual = prefs.get("LAST_ACTIVE_ACCOUNT", "");
        boolean isDark = prefs.getBoolean("PREF_DARK_MODE_" + correoActual, false);
        
        // CORRECCIÓN APLICADA: Variable unificada a colorFuerte
        colorFuerte = prefs.get("PREF_ACCENT_COLOR_" + correoActual, "#3D5A4D");

        colorBgBase = isDark ? "#121212" : "#f1f5f9";
        colorBgPanel = isDark ? "#1e1e1e" : "#ffffff";
        colorTextMain = isDark ? "#f8f9fa" : "#1e293b";
        colorTextMuted = isDark ? "#94a3b8" : "#64748b";
        colorBorder = isDark ? "#333333" : "#e2e8f0";
    }
}