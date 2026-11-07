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

public class NotasController {

    private final String usuarioActual;
    private final Map<String, String> mapaColores = new HashMap<>();
    private final String[] coloresKeep = {"#F28B82", "#FBBC04", "#FFF475", "#CCFF90", "#A7FFEB", "#CBF0F8", "#AECBFA", "#D7AEFB", "#FDCFE8", "#E6C9A8"};
    
    // Archivo donde se guardarán las notas localmente
    private final String archivoNotas = "mis_apuntes.dat";

    public NotasController() {
        Preferences p = Preferences.userNodeForPackage(DashboardView.class);
        this.usuarioActual = p.get("LAST_ACTIVE_ACCOUNT", "default");
    }

    public static class ApunteDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        public String id, titulo, materia, contenido, fecha, colorHex;
        public ApunteDTO(String id, String titulo, String materia, String contenido, String fecha, String colorHex) {
            this.id = id; this.titulo = titulo; this.materia = materia;
            this.contenido = contenido; this.fecha = fecha; this.colorHex = colorHex;
        }
    }

    public List<String> getMateriasDisponibles() {
        List<String> nombresMaterias = new ArrayList<>();
        
        nombresMaterias.add("Nota personal");
        nombresMaterias.add("Recordatorio");
        
        mapaColores.put("Nota personal", "#90A4AE");
        mapaColores.put("Recordatorio", "#FFB300");
        
        // CONEXIÓN CORREGIDA AL MOCKDB
        List< Map<String, String> > clases = MockDB.obtenerMateriasDelAlumno(usuarioActual);
        
        int colorIndex = 0;
        if (clases != null) {
            for(Map<String, String> clase : clases) {
                String nombre = clase.get("nombre");
                nombresMaterias.add(nombre);
                if (!mapaColores.containsKey(nombre)) {
                    mapaColores.put(nombre, coloresKeep[colorIndex % coloresKeep.length]);
                    colorIndex++;
                }
            }
        }
        return nombresMaterias;
    }

    public String getColorParaMateria(String materia) {
        return mapaColores.getOrDefault(materia, "#ffffff");
    }

    public void guardarApunte(String titulo, String materia, String contenido) {
        List<ApunteDTO> apuntes = cargarApuntes();
        String id = UUID.randomUUID().toString();
        String fecha = java.time.LocalDate.now().toString();
        String color = getColorParaMateria(materia);
        
        apuntes.add(0, new ApunteDTO(id, titulo, materia, contenido, fecha, color));
        guardarDatosFichero(apuntes);
    }

    public void eliminarApunte(String id) {
        List<ApunteDTO> apuntes = cargarApuntes();
        apuntes.removeIf(a -> a.id.equals(id));
        guardarDatosFichero(apuntes);
    }

    @SuppressWarnings("unchecked")
    public List<ApunteDTO> cargarApuntes() {
        File file = new File(archivoNotas);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ApunteDTO>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void guardarDatosFichero(List<ApunteDTO> apuntes) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoNotas))) {
            oos.writeObject(apuntes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}