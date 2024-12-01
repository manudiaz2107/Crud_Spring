package com.empresa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.empresa.models.Empleado;
import com.empresa.services.EmpleadoService;
import com.empresa.services.EmpleadoValidationService;

@Controller
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private EmpleadoValidationService empleadoValidationService;

    @GetMapping("/empleados")
    public ModelAndView listar() {
        ModelAndView mav = new ModelAndView("listar");
        List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();
        Map<String, Double> sueldos = new HashMap<>();
        for (Empleado empleado : empleados) {
            sueldos.put(empleado.getDni(), empleadoService.obtenerSueldoPorDni(empleado.getDni()));
        }
        mav.addObject("empleados", empleados);
        mav.addObject("sueldo", sueldos);
        return mav;
    }

    @GetMapping
    public String inicio() {
        return "index";
    }

    @PostMapping("/empleados/crearEmpleado")
    public String crearEmpleado(@RequestParam("nombre") String nombre,
                                @RequestParam("dni") String dni,
                                @RequestParam("sexo") String sexo,
                                @RequestParam("categoria") int categoria,
                                @RequestParam("anyos") int anyos,
                                Model model) {

        if (!empleadoValidationService.validarEmpleado(nombre, dni, sexo, categoria, anyos, model)) {
            return cargarEmpleadosConErrores(model);
        }

        if (empleadoService.existeEmpleadoPorDni(dni)) {
            model.addAttribute("error", "El empleado con el DNI " + dni + " ya existe.");
            return cargarEmpleadosConErrores(model);
        }

        Empleado nuevoEmpleado = new Empleado(nombre, dni, sexo, categoria, anyos);
        empleadoService.crearEmpleado(nuevoEmpleado);

        return "redirect:/empleados";
    }

    private String cargarEmpleadosConErrores(Model model) {
        List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();
        Map<String, Double> sueldos = new HashMap<>();
        for (Empleado empleado : empleados) {
            sueldos.put(empleado.getDni(), empleadoService.obtenerSueldoPorDni(empleado.getDni()));
        }
        model.addAttribute("empleados", empleados);
        model.addAttribute("sueldo", sueldos);
        return "listar";
    }

    @GetMapping("/empleados/buscar")
    public ModelAndView buscarEmpleados(@RequestParam(required = false) String nombre,
                                        @RequestParam(required = false) String dni,
                                        @RequestParam(required = false) String sexo,
                                        @RequestParam(required = false) Integer categoria,
                                        @RequestParam(required = false) Integer anyos) {

        if (dni != null && dni.isEmpty()) dni = null;
        if (sexo != null && sexo.isEmpty()) sexo = null;
        if (categoria != null && categoria == 0) categoria = null;
        if (anyos != null && anyos == 0) anyos = null;

        List<Empleado> encontrados = empleadoService.buscarEmpleados(nombre, dni, sexo, categoria, anyos);

        ModelAndView mav = new ModelAndView("buscarEmpleados");
        mav.addObject("encontrados", encontrados);
        return mav;
    }

    @GetMapping("/empleados/{dni}")
    public Empleado obtenerEmpleadoPorDni(@PathVariable String dni) {
        return empleadoService.obtenerEmpleadoPorDni(dni);
    }

    @GetMapping("/empleados/mostrarSalario")
    public ModelAndView buscarSalario(@RequestParam(required = false) String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return new ModelAndView("salario").addObject("message", "El DNI no puede estar vacío.");
        }

        Empleado empleado = empleadoService.obtenerEmpleadoPorDni(dni);
        if (empleado == null) {
            return new ModelAndView("salario").addObject("message", "Empleado no encontrado.");
        }

        double salario = empleadoService.calculaSueldo(empleado.getCategoria(), empleado.getAnyos());
        ModelAndView mav = new ModelAndView("salario");
        mav.addObject("empleado", empleado);
        mav.addObject("salario", salario);

        return mav;
    }

    @GetMapping("/empleados/mostrarFormularioBusqueda")
    public ModelAndView mostrarFormularioBusqueda() {
        return new ModelAndView("salario");
    }

    @GetMapping("/empleados/mostrarFormularioCrear")
    public ModelAndView mostrarFormularioCrear() {
        return new ModelAndView("crear");
    }

    @PostMapping("/empleados/modificarEmpleado")
    public String modificarEmpleado(@RequestParam("nombre") String nombre,
                                    @RequestParam("dni") String dni,
                                    @RequestParam("sexo") String sexo,
                                    @RequestParam("categoria") int categoria,
                                    @RequestParam("anyos") int anyos,
                                    Model model) {

        if (!empleadoValidationService.validarEmpleado(nombre, dni, sexo, categoria, anyos, model)) {
            return cargarEmpleadoParaEdicion(dni, model);
        }

        Empleado empleado = new Empleado(nombre, dni, sexo, categoria, anyos);
        boolean success = empleadoService.modificarEmpleado(empleado);

        if (success) {
            return "redirect:/empleados";
        } else {
            model.addAttribute("error", "No se pudo modificar el empleado. Verifique que los datos sean correctos.");
            return cargarEmpleadoParaEdicion(dni, model);
        }
    }

    private String cargarEmpleadoParaEdicion(String dni, Model model) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorDni(dni);
        if (empleado == null) {
            model.addAttribute("error", "Empleado no encontrado.");
            return "redirect:/empleados";
        }
        model.addAttribute("empleado", empleado);
        return "editarEmpleado";
    }

    @GetMapping("/empleados/editar/{dni}")
    public ModelAndView editarEmpleado(@PathVariable String dni) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorDni(dni);
        if (empleado == null) {
            return new ModelAndView("error").addObject("message", "Empleado no encontrado.");
        }
        ModelAndView mav = new ModelAndView("editarEmpleado");
        mav.addObject("empleado", empleado);
        return mav;
    }

    @GetMapping("/empleados/eliminar/{dni}")
    public String eliminarEmpleado(@PathVariable String dni) {
        boolean success = empleadoService.eliminarEmpleado(dni);
        if (success) {
            return "redirect:/empleados";
        } else {
            return "redirect:/empleados?error=true";
        }
    }
}