package com.optem.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.prefs.Preferences;

import com.optem.database.MockDB;
import com.optem.ui.DashboardView;

public class NotasMaestroController {
    private final String usuarioActual;
    private final Map<String, String> mapaColores = new HashMap<>();
    
    private final String archivoNotas;

    public NotasMaestroController() {
        Preferences p = Preferences.userNodeForPackage(DashboardView.class);
        this.usuarioActual = p.get("LAST_ACTIVE_ACCOUNT", "default");
        
        // Se genera un archivo de notas exclusivo para este maestro
        String cuentaLimpia = this.usuarioActual.replaceAll("[^a-zA-Z0-9]", "_");
        this.archivoNotas = "notas_maestro_" + cuentaLimpia + ".dat";
    }

    public static class ApunteDocenteDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, titulo, categoria, grupo, contenido, fecha, colorHex;
        public ApunteDocenteDTO(String id, String titulo, String categoria, String grupo, String contenido, String fecha, String colorHex) {
            this.id = id; this.titulo = titulo; this.categoria = categoria; this.grupo = grupo;
            this.contenido = contenido; this.fecha = fecha; this.colorHex = colorHex;
        }
    }

    public List<String> getGruposDisponibles() {
        List<String> grupos = new ArrayList<>();
        grupos.add("General / Ninguno");
        List< Map<String, String> > misGrupos = MockDB.obtenerTodosLosGrupos(); 
        if (misGrupos != null) {
            for(Map<String, String> g : misGrupos) {
                grupos.add(g.get("nombre") + " (" + g.get("grupo") + ")");
            }
        }
        return grupos;
    }

    public List<String> getCategoriasDisponibles() {
        List<String> cat = new ArrayList<>();
        cat.add("Planeación de Clase");
        cat.add("Rúbrica de Evaluación");
        cat.add("Reunión de Academia");
        cat.add("Nota Personal Docente");
        
        mapaColores.put("Planeación de Clase", "#A0C4FF");
        mapaColores.put("Rúbrica de Evaluación", "#FFADAD");
        mapaColores.put("Reunión de Academia", "#FDFFB6");
        mapaColores.put("Nota Personal Docente", "#CAFFBF");
        return cat;
    }

    public String getColorParaCategoria(String cat) {
        return mapaColores.getOrDefault(cat, "#ffffff");
    }

    public void guardarApunte(String titulo, String categoria, String grupo, String contenido) {
        List<ApunteDocenteDTO> apuntes = cargarApuntes();
        String id = UUID.randomUUID().toString();
        String fecha = java.time.LocalDate.now().toString();
        String color = getColorParaCategoria(categoria);
        
        apuntes.add(0, new ApunteDocenteDTO(id, titulo, categoria, grupo, contenido, fecha, color));
        guardarDatosFichero(apuntes);
    }

    public void eliminarApunte(String id) {
        List<ApunteDocenteDTO> apuntes = cargarApuntes();
        apuntes.removeIf(a -> a.id.equals(id));
        guardarDatosFichero(apuntes);
    }

    @SuppressWarnings("unchecked")
    public List<ApunteDocenteDTO> cargarApuntes() {
        File file = new File(archivoNotas);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ApunteDocenteDTO>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void guardarDatosFichero(List<ApunteDocenteDTO> apuntes) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoNotas))) {
            oos.writeObject(apuntes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}