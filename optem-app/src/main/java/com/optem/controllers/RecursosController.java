package com.optem.controllers;

import com.optem.logic.ActividadManager;
import java.util.ArrayList;
import java.util.List;

public class RecursosController {

    /**
     * Obtiene la lista de recursos (título y descripción) correspondientes al usuario actual.
     * Retorna una lista de arreglos de String donde index 0 es el título y el index 1 es la descripción.
     */
    public List<String[]> getRecursosPorUsuario() {
        List<String[]> recursos = new ArrayList<>();
        String correo = ActividadManager.getInstance().getCorreoActual();

        if (correo.contains("jfonsecal001")) {
            recursos.add(new String[]{"📕 Libro: Arquitectura de Computadoras", "Autor: Morris Mano. PDF de la 3ra edición para estudiar procesadores. Guardado localmente en tu biblioteca."});
            recursos.add(new String[]{"📘 Apuntes PDF: Estructuras de Datos", "Ejercicios de árboles binarios y listas enlazadas en Java descargados del repositorio de la Mtra. Marcela."});
            recursos.add(new String[]{"📄 Documentación Oficial: Lexer JFLEX", "Manual en formato PDF con las reglas gramaticales base para el proyecto compilador JODA."});
            recursos.add(new String[]{"📰 Revista Digital: Ingeniería de Software", "Artículo en PDF sobre las nuevas tendencias y levantamiento de requisitos de software según estándares del IEEE."});
        } else if (correo.contains("fmartinezc017")) {
            recursos.add(new String[]{"📕 Código Civil Anotado del Edomex", "Material base para la materia de Derecho Civil en formato PDF."});
            recursos.add(new String[]{"📘 Revista: Criminología Moderna", "Estudios de caso y perfiles psicológicos completos."});
            recursos.add(new String[]{"📄 Documento Histórico: Leyes de Reforma", "Antecedentes históricos completos y apuntes descargados de la clase."});
        } else if (correo.contains("ahernandezh058")) {
            recursos.add(new String[]{"📕 Libro: Sistemas Operativos Modernos", "Autor: Andrew S. Tanenbaum. Conceptos de hilos y concurrencia completos en PDF."});
            recursos.add(new String[]{"📘 Manual Práctico: Cisco Packet Tracer", "Guía paso a paso en PDF para simular topologías de red complejas."});
            recursos.add(new String[]{"📄 Guía: Bases de Datos Avanzadas", "Ejercicios de subconsultas, vistas y triggers en formato PDF."});
        } else {
            recursos.add(new String[]{"📘 Guía de Bienvenida Optem", "Manual de uso rápido de las herramientas de Optem para estudiantes de primer ingreso en PDF."});
        }

        return recursos;
    }
}