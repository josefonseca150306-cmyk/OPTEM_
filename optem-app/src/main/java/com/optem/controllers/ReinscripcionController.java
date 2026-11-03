package com.optem.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.optem.database.MockDB;

public class ReinscripcionController {

    public static class MateriaDTO {
        public String titulo, creditos, colorBorde, categoria;
        public boolean mixta;
        public String[] opciones;
        public MateriaDTO(String t, String c, boolean m, String cb, String cat, String[] o) { 
            titulo=t; creditos=c; mixta=m; colorBorde=cb; categoria=cat; opciones=o; 
        }
    }

    public static class BloqueHorario {
        public String texto, colorHex;
        public int colDia, rowHora, duracion;
        public BloqueHorario(String t, int c, int r, int d, String ch) { texto=t; colDia=c; rowHora=r; duracion=d; colorHex=ch; }
    }

    public List<MateriaDTO> getMateriasDisponibles() {
        return MockDB.obtenerMateriasReinscripcion();
    }

    public Map<String, String> generarHorarioInteligente(String preferencia) {
        return MockDB.obtenerHorarioIA(preferencia);
    }

    public List<BloqueHorario> procesarSeleccion(String materia, String seleccion, String color) {
        List<BloqueHorario> bloques = new ArrayList<>();
        if (seleccion == null || seleccion.contains("— No cursar —")) return bloques;
        
        String nombreCorto = materia.split("\\(")[0].trim();
        
        if (seleccion.contains("Lun/Mié") && seleccion.contains("07:00")) { bloques.add(new BloqueHorario(nombreCorto, 1, 0, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 3, 0, 2, color)); }
        else if (seleccion.contains("Lun/Mié") && seleccion.contains("09:00")) { bloques.add(new BloqueHorario(nombreCorto, 1, 2, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 3, 2, 2, color)); }
        else if (seleccion.contains("Lun/Mié") && seleccion.contains("11:00")) { bloques.add(new BloqueHorario(nombreCorto, 1, 4, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 3, 4, 2, color)); }
        else if (seleccion.contains("Lun/Mié") && seleccion.contains("13:00")) { bloques.add(new BloqueHorario(nombreCorto, 1, 6, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 3, 6, 2, color)); }
        else if (seleccion.contains("Lun/Mié") && seleccion.contains("15:00")) { bloques.add(new BloqueHorario(nombreCorto, 1, 8, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 3, 8, 2, color)); }
        
        else if (seleccion.contains("Mar/Jue") && seleccion.contains("07:00")) { bloques.add(new BloqueHorario(nombreCorto, 2, 0, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 4, 0, 2, color)); }
        else if (seleccion.contains("Mar/Jue") && seleccion.contains("09:00")) { bloques.add(new BloqueHorario(nombreCorto, 2, 2, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 4, 2, 2, color)); }
        else if (seleccion.contains("Mar/Jue") && seleccion.contains("11:00")) { bloques.add(new BloqueHorario(nombreCorto, 2, 4, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 4, 4, 2, color)); }
        else if (seleccion.contains("Mar/Jue") && seleccion.contains("13:00")) { bloques.add(new BloqueHorario(nombreCorto, 2, 6, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 4, 6, 2, color)); }
        else if (seleccion.contains("Mar/Jue") && seleccion.contains("15:00")) { bloques.add(new BloqueHorario(nombreCorto, 2, 8, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 4, 8, 2, color)); }
        else if (seleccion.contains("Mar/Jue") && seleccion.contains("17:00")) { bloques.add(new BloqueHorario(nombreCorto, 2, 10, 2, color)); bloques.add(new BloqueHorario(nombreCorto, 4, 10, 2, color)); }
        
        else if (seleccion.contains("Vie") && seleccion.contains("07:00")) { bloques.add(new BloqueHorario(nombreCorto, 5, 0, 4, color)); }
        else if (seleccion.contains("Vie") && seleccion.contains("11:00")) { bloques.add(new BloqueHorario(nombreCorto, 5, 4, 4, color)); }
        else if (seleccion.contains("Vie") && seleccion.contains("15:00")) { bloques.add(new BloqueHorario(nombreCorto, 5, 8, 4, color)); }
        
        else if (seleccion.contains("Sáb") && seleccion.contains("08:00")) { bloques.add(new BloqueHorario(nombreCorto, 6, 1, 4, color)); }
        else if (seleccion.contains("Sáb") && seleccion.contains("13:00")) { bloques.add(new BloqueHorario(nombreCorto, 6, 6, 4, color)); }
        
        return bloques;
    }
}