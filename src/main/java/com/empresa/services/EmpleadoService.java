package com.empresa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.models.Empleado;
import com.empresa.repository.EmpleadoRepository;

@Service
public class EmpleadoService {

	private static final double SUELDO_BASE[] = { 50000, 70000, 90000, 110000, 130000, 
												150000,	170000, 190000, 210000, 230000 };

	@Autowired
	private EmpleadoRepository empleadoRepository;

	@Transactional
	public List<Empleado> obtenerTodosLosEmpleados() {
		return empleadoRepository.findAll();
	}

	// Buscar empleados por filtros
	public List<Empleado> buscarEmpleados(String nombre, String dni, String sexo, Integer categoria, Integer anyos) {
	    return empleadoRepository.findByFilters(nombre, dni, sexo, categoria, anyos);
	}


	// Obtener empleado por DNI
	public Empleado obtenerEmpleadoPorDni(String dni) {
		return empleadoRepository.findByDni(dni);
	}

	// Método para verificar si un empleado existe por su DNI
	public boolean existeEmpleadoPorDni(String dni) {
	    // Busca al empleado por su DNI
	    Empleado empleado = empleadoRepository.findByDni(dni);
	    return empleado != null;  // Si el empleado existe, devuelve true, si no, false
	}

	
	@Transactional
	public boolean crearEmpleado(Empleado empleado) {
	    try {
	        System.out.println("Creando empleado: " + empleado);
	        Empleado savedEmpleado = empleadoRepository.saveAndFlush(empleado);
	        System.out.println("Empleado guardado con ID: " + savedEmpleado.getId());
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Fallo al crear empleado");
	        return false;
	    }
	}


	public boolean modificarEmpleado(Empleado empleado) {
	    // Buscar el empleado por su DNI
	    Empleado empleadoExistente = empleadoRepository.findByDni(empleado.getDni());

	    // Si el empleado existe, lo actualizamos
	    if (empleadoExistente != null) {
	        // Actualizar los detalles del empleado
	        empleadoExistente.setNombre(empleado.getNombre());
	        empleadoExistente.setSexo(empleado.getSexo());
	        empleadoExistente.setCategoria(empleado.getCategoria());
	        empleadoExistente.setAnyos(empleado.getAnyos());

	        // Guardar el empleado con los nuevos datos
	        empleadoRepository.save(empleadoExistente);
	        return true;  // Modificación exitosa
	    }
	    
	    // Si el empleado no existe, devolver false
	    return false;
	}



	// Obtener el sueldo de un empleado por DNI
	public Double obtenerSueldoPorDni(String dni) {
		Empleado empleado = empleadoRepository.findByDni(dni);
		if(empleado == null) {
			return null;
		}
		return calculaSueldo(empleado.getCategoria(), empleado.getAnyos());
	}

	// Eliminar un empleado y su nómina
	@Transactional
	public boolean eliminarEmpleado(String dni) {
		try {
			Optional<Empleado> empleadoOpt = Optional.ofNullable(empleadoRepository.findByDni(dni));

			if (empleadoOpt.isPresent()) {
				Empleado empleado = empleadoOpt.get();
				empleadoRepository.delete(empleado); // Elimina el empleado
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	
	
	// Guardar un empleado
	public void guardarEmpleado(Empleado empleado) {
		empleadoRepository.save(empleado);
	}

	/**
	 * Calcula el sueldo total del empleado basado en su categoría y años
	 * trabajados.
	 *
	 * @param categoria      La categoría del empleado (valor entre 1 y 10).
	 * @param anyos Los años que el empleado ha trabajado.
	 * @return El sueldo total calculado del empleado.
	 */
	public double calculaSueldo(int categoria, int anyos) {
		return SUELDO_BASE[categoria - 1] + anyos * 5000;
	}
}
