package com.empresa.services;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class EmpleadoValidationService {

    public boolean validarEmpleado(String nombre, String dni, String sexo, int categoria, int anyos, Model model) {
        if (nombre == null || nombre.trim().isEmpty()) {
            model.addAttribute("error", "El nombre no puede estar vacío.");
            return false;
        }

        if (!dni.matches("\\d{8}[A-Za-z]")) {
            model.addAttribute("error", "El DNI debe tener 8 numeros seguidos de una letra.");
            return false;
        }

        if (categoria < 1 || categoria > 10) {
            model.addAttribute("error", "La categoría debe estar entre 1 y 10.");
            return false;
        }

        if (anyos < 0) {
            model.addAttribute("error", "Los años de servicio no pueden ser negativos.");
            return false;
        }

        if (!sexo.equals("M") && !sexo.equals("F")) {
            model.addAttribute("error", "El sexo debe ser 'M' o 'F'.");
            return false;
        }

        return true;
    }
}
