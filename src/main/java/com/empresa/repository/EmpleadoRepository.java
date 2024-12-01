package com.empresa.repository;

import com.empresa.models.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
	
	Empleado findByDni(String dni);
    
	@Query("SELECT e FROM Empleado e WHERE " +
		       "(:nombre IS NULL OR e.nombre LIKE %:nombre%) AND " +
		       "(:dni IS NULL OR e.dni = :dni) AND " +
		       "(:sexo IS NULL OR e.sexo = :sexo) AND " +
		       "(:categoria IS NULL OR e.categoria = :categoria) AND " +
		       "(:anyos IS NULL OR e.anyos = :anyos)")    
    	List<Empleado> findByFilters(@Param("nombre") String nombre, 
    	                             @Param("dni") String dni, 
    	                             @Param("sexo") String sexo, 
    	                             @Param("categoria") Integer categoria, 
    	                             @Param("anyos") Integer anyos);

	boolean existsByDni(String dni);
	
}
