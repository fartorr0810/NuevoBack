package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Patinete;
/**
 * Repositorio de servicios.
 * @author fartorr0810
 *
 */
public interface PatineteRepository extends JpaRepository<Patinete, Integer>{
	/**
	 * Consulta personalizada que devuelve una lista con los patinetes que sean disponibles
	 * @return devuelve una lista de patinetes.
	 */
	@Query(value="SELECT * FROM bgblrez3xfufam9bmyci.patinete pa WHERE pa.disponible=true",nativeQuery=true)
	List<Patinete> findPatinetesDisponibles();
}
