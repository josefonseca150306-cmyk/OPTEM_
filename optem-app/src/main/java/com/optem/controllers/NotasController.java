package com.optem.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.optem.database.MockDB;

public class NotasController {

    private final String RUTA_ARCHIVO = "mis_apuntes.dat";
    
    private final String[] coloresKeep = {"#E57373", "#81C784", "#64B5F6", "#FFD54F", "#BA68C8", "#4DB6AC", "#FF8A65"};
    private final Map<String, String> mapaColores = new HashMap<>();

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
        
        List< Map < String , String >> clases = MockDB.obtenerMateriasRegistradas();
        
        int colorIndex = 0;
        for (Map<String, String> clase : clases) {
            String nombre = clase.get("nombre");
            nombresMaterias.add(nombre);
            if (!mapaColores.containsKey(nombre)) {
                mapaColores.put(nombre, coloresKeep[colorIndex % coloresKeep.length]);
                colorIndex++;
            }
        }
        return nombresMaterias;
    }

    public boolean guardarApunte(String titulo, String materia, String textoHtml) {
        String textoLimpio = textoHtml.replaceAll("<[^>]*>", "").replace("&nbsp;", "").trim();
        
        if (titulo == null || titulo.trim().isEmpty() || materia == null || textoLimpio.isEmpty()) {
            return false;
        }
        
        String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy • hh:mm a"));
        String colorMateria = mapaColores.getOrDefault(materia, "#9E9E9E"); 
        
        ApunteDTO nuevoApunte = new ApunteDTO(
            String.valueOf(System.currentTimeMillis()), titulo, materia, textoHtml, fechaActual, colorMateria
        );

        List<ApunteDTO> listaActual = getTodosLosApuntes();
        listaActual.add(0, nuevoApunte); 
        
        return guardarEnArchivo(listaActual);
    }
    
    public boolean eliminarApunte(String id) {
        List<ApunteDTO> listaActual = getTodosLosApuntes();
        listaActual.removeIf(a -> a.id.equals(id));
        return guardarEnArchivo(listaActual);
    }

    private boolean guardarEnArchivo(List<ApunteDTO> lista) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(lista);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ApunteDTO> getTodosLosApuntes() {
        File file = new File(RUTA_ARCHIVO);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ApunteDTO>) ois.readObject();
        } catch (Exception e) {
            System.err.println("No se pudo leer el archivo de notas. Se iniciará vacío.");
            return new ArrayList<>();
        }
    }
}