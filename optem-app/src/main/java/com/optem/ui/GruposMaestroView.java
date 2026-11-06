package com.optem.ui;

import java.util.prefs.Preferences;

import com.optem.controllers.AjustesController;
import com.optem.controllers.GruposMaestroController;
import com.optem.logic.NotificacionManager;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class GruposMaestroView extends VBox {

    private final GruposMaestroController controller = new GruposMaestroController();
    private VBox listaGrupos;
    
    private String colorFuerte, colorBgBase, colorBgPanel, colorTextMain, colorTextMuted, colorBorder;

    public GruposMaestroView() {
        leerPreferencias();
        this.setPadding(new Insets(30, 40, 30, 40));
        this.setSpacing(25);
        this.setStyle("-fx-background-color: transparent;");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(5);
        Label titulo = new Label("👥 Gestión de Grupos y Clases");
        titulo.setStyle("-fx-font-weight: 800; -fx-font-size: 28px; -fx-text-fill: " + colorTextMain + ";");
        Label subtitulo = new Label("Administra tus asignaturas, genera códigos de acceso o inscribe alumnos.");
        subtitulo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        titleBox.getChildren().addAll(titulo, subtitulo);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNuevoGrupo = new Button("➕ Crear Nuevo Grupo");
        btnNuevoGrupo.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        btnNuevoGrupo.setOnAction(e -> abrirModalCrearGrupo(this.getScene().getWindow()));

        header.getChildren().addAll(titleBox, spacer, btnNuevoGrupo);

        HBox metricasHeader = new HBox(20);
        metricasHeader.getChildren().addAll(
            crearMetricaRapida("Total de Grupos Activos", "2"),
            crearMetricaRapida("Alumnos Totales", "42")
        );

        listaGrupos = new VBox(15);
        
        agregarTarjetaGrupo("Ingeniería de Software", "Lun/Mié 13:00 - 15:00", controller.generarCodigoClase(), 24, 8.5);
        agregarTarjetaGrupo("Bases de Datos Avanzadas", "Mar/Jue 07:00 - 09:00", controller.generarCodigoClase(), 18, 7.2);

        ScrollPane scroll = new ScrollPane(listaGrupos);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        this.getChildren().addAll(header, metricasHeader, scroll);
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

    private VBox crearMetricaRapida(String titulo, String valor) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(15, 25, 15, 25));
        box.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        Label lblVal = new Label(valor);
        lblVal.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: " + colorFuerte + ";");
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 12px;");
        box.getChildren().addAll(lblVal, lblTit);
        return box;
    }

    private void agregarTarjetaGrupo(String materia, String horario, String codigoAcceso, int totalAlumnos, double promedioGrupo) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-padding: 25; -fx-background-radius: 12; -fx-border-color: " + colorBorder + "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 2, 2);");

        HBox topFila = new HBox();
        topFila.setAlignment(Pos.CENTER_LEFT);
        
        VBox info = new VBox(5);
        Label lblMateria = new Label("📚 " + materia);
        lblMateria.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + colorTextMain + ";");
        Label lblInfo = new Label("Horario: " + horario + "  |  Alumnos inscritos: " + totalAlumnos);
        lblInfo.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 14px;");
        info.getChildren().addAll(lblMateria, lblInfo);

        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);

        VBox boxPromedio = new VBox(3);
        boxPromedio.setAlignment(Pos.CENTER_RIGHT);
        Label lblPromTit = new Label("Promedio General");
        lblPromTit.setStyle("-fx-text-fill: " + colorTextMuted + "; -fx-font-size: 11px;");
        
        HBox barCaja = new HBox(8);
        barCaja.setAlignment(Pos.CENTER_RIGHT);
        ProgressBar pb = new ProgressBar(promedioGrupo / 10.0);
        pb.setPrefWidth(80);
        pb.setStyle("-fx-accent: " + (promedioGrupo >= 8 ? "#10b981" : promedioGrupo >= 6 ? "#f59e0b" : "#ef4444") + "; -fx-control-inner-background: " + colorBgBase + ";");
        Label lblPromVal = new Label(String.valueOf(promedioGrupo));
        lblPromVal.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");
        barCaja.getChildren().addAll(pb, lblPromVal);
        
        boxPromedio.getChildren().addAll(lblPromTit, barCaja);

        topFila.getChildren().addAll(info, sp1, boxPromedio);

        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        actionBox.setStyle("-fx-padding: 15 0 0 0; -fx-border-color: " + colorBorder + "; -fx-border-width: 1 0 0 0;");

        Label lblCodigoTag = new Label("Código:");
        lblCodigoTag.setStyle("-fx-text-fill: " + colorTextMuted + ";");
        Label lblCodigoVal = new Label(codigoAcceso);
        lblCodigoVal.setStyle("-fx-background-color: " + colorBgBase + "; -fx-padding: 6 12; -fx-background-radius: 6; -fx-font-weight: bold; -fx-text-fill: " + colorFuerte + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6;");

        Button btnInscribirManual = new Button("👤 Añadir Alumno");
        btnInscribirManual.setStyle("-fx-background-color: transparent; -fx-border-color: " + colorFuerte + "; -fx-border-radius: 6; -fx-text-fill: " + colorFuerte + "; -fx-font-weight: bold; -fx-padding: 6 12; -fx-cursor: hand;");
        btnInscribirManual.setOnAction(e -> abrirModalAgregarAlumnoManual(materia, card.getScene().getWindow()));

        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);

        Button btnOpciones = new Button("Opciones ⚙");
        btnOpciones.setStyle("-fx-background-color: " + colorBgBase + "; -fx-text-fill: " + colorTextMain + "; -fx-border-color: " + colorBorder + "; -fx-border-radius: 6; -fx-padding: 6 15; -fx-cursor: hand;");
        
        Popup popup = new Popup();
        popup.setAutoHide(true);
        
        VBox menuBox = new VBox(2);
        menuBox.setPadding(new Insets(5));
        menuBox.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorBorder + "; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 2, 2);");
        
        Button btnLista = crearBotonOpcion("👥 Ver lista completa de alumnos");
        btnLista.setOnAction(e -> { 
            popup.hide(); 
            NotificacionManager.mostrarNotificacion("Gestión de Alumnos", "Redirigiendo a la lista de " + materia + "...", "INFO"); 
        });
        
        Button btnHorario = crearBotonOpcion("🕒 Editar Horario");
        btnHorario.setOnAction(e -> { 
            popup.hide(); 
            NotificacionManager.mostrarNotificacion("Edición", "El horario de " + materia + " ahora es editable.", "INFO"); 
        });
        
        Button btnArchivar = crearBotonOpcion("📦 Archivar Grupo");
        btnArchivar.setOnAction(e -> { 
            popup.hide(); 
            NotificacionManager.mostrarNotificacion("Archivo", "El grupo " + materia + " ha sido archivado.", "INFO"); 
        });
        
        menuBox.getChildren().addAll(btnLista, btnHorario, btnArchivar);
        popup.getContent().add(menuBox);

        btnOpciones.setOnAction(e -> {
            Bounds bounds = btnOpciones.localToScreen(btnOpciones.getBoundsInLocal());
            if (bounds != null) {
                popup.show(btnOpciones, bounds.getMinX(), bounds.getMaxY() + 5);
            }
        });

        actionBox.getChildren().addAll(lblCodigoTag, lblCodigoVal, btnInscribirManual, sp2, btnOpciones);

        card.getChildren().addAll(topFila, actionBox);
        listaGrupos.getChildren().add(card);
    }

    private Button crearBotonOpcion(String texto) {
        Button btn = new Button(texto);
        btn.setPrefWidth(220);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand; -fx-background-radius: 6;");
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMain + "; -fx-font-size: 13px; -fx-padding: 8 12; -fx-cursor: hand; -fx-background-radius: 6;"));
        
        return btn;
    }

    private void abrirModalCrearGrupo(Window owner) {
        Stage modal = new Stage(); 
        modal.setTitle("Crear Nuevo Grupo");
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(owner);
        modal.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        
        String fontPref = new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal");
        String fontSize = fontPref.equals("Pequeño") ? "12px" : fontPref.equals("Grande") ? "17px" : "14px";
        root.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: " + fontSize + ";");

        Label lblTitle = new Label("Crear Nueva Clase");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la materia (Ej. Álgebra Lineal)");
        txtNombre.setStyle("-fx-background-color: " + colorBgBase + "; -fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-text-fill: " + colorTextMain + ";");

        TextField txtHorario = new TextField();
        txtHorario.setPromptText("Horario (Ej. Mar/Jue 11:00 - 13:00)");
        txtHorario.setStyle(txtNombre.getStyle());

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> modal.close());

        Button btnGuardar = new Button("Generar Clase y Código");
        btnGuardar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        
        btnGuardar.setOnAction(e -> {
            boolean exito = controller.crearGrupo(txtNombre.getText(), txtHorario.getText());
            if (exito) {
                agregarTarjetaGrupo(txtNombre.getText(), txtHorario.getText(), controller.generarCodigoClase(), 0, 0.0);
                NotificacionManager.mostrarNotificacion("Grupo Creado", "La clase " + txtNombre.getText() + " se generó con éxito.", "INFO");
                modal.close();
            }
        });

        botones.getChildren().addAll(btnCancelar, btnGuardar);
        root.getChildren().addAll(lblTitle, txtNombre, txtHorario, botones);
        
        Scene scene = new Scene(root, 400, 280);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.show();
        modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2);
        modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2);
    }

    private void abrirModalAgregarAlumnoManual(String materia, Window owner) {
        Stage modal = new Stage(); 
        modal.setTitle("Añadir Alumno");
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(owner);
        modal.initStyle(StageStyle.UNDECORATED);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        
        String fontPref = new AjustesController("").getPrefString("PREF_FONT_SIZE", "Normal");
        String fontSize = fontPref.equals("Pequeño") ? "12px" : fontPref.equals("Grande") ? "17px" : "14px";
        root.setStyle("-fx-background-color: " + colorBgPanel + "; -fx-border-color: " + colorFuerte + "; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12; -fx-font-size: " + fontSize + ";");

        Label lblTitle = new Label("Añadir alumno a: " + materia);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + colorTextMain + ";");

        TextField txtBusqueda = new TextField();
        txtBusqueda.setPromptText("Número de cuenta o correo institucional...");
        txtBusqueda.setStyle("-fx-background-color: " + colorBgBase + "; -fx-padding: 10; -fx-border-color: " + colorBorder + "; -fx-border-radius: 8; -fx-text-fill: " + colorTextMain + ";");

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle("-fx-background-color: transparent; -fx-text-fill: " + colorTextMuted + "; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> modal.close());

        Button btnBuscar = new Button("Buscar e Inscribir");
        btnBuscar.setStyle("-fx-background-color: " + colorFuerte + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        
        btnBuscar.setOnAction(e -> {
            if (controller.agregarAlumnoManual(txtBusqueda.getText())) {
                NotificacionManager.mostrarNotificacion("Alumno Inscrito", "Se ha añadido al alumno exitosamente a " + materia, "INFO");
                modal.close();
            }
        });

        botones.getChildren().addAll(btnCancelar, btnBuscar);
        root.getChildren().addAll(lblTitle, txtBusqueda, botones);
        
        Scene scene = new Scene(root, 400, 220);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.show();
        modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2);
        modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2);
    }
}