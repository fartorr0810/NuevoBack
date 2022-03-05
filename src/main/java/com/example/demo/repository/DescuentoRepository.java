package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Alquiler;
import com.example.demo.model.Comentario;
import com.example.demo.model.Descuento;
/**
 * Repositorio con los descuentos
 * @author fran
 *
 */
public interface DescuentoRepository extends JpaRepository<Descuento, Integer>{
	/**
	 * Query para comprobar si el codigo de descuento es valido o no.
	 * @param codigo
	 * @return
	 */
	@Query(value="SELECT * FROM alquiler.descuento des WHERE des.codigo = :codigo",nativeQuery=true)
	Descuento findDescuentoByCodigo(@Param("codigo") String codigo);
}
