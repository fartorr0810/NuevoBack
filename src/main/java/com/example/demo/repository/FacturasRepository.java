package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Factura;
import com.example.demo.model.Patinete;

public interface FacturasRepository extends JpaRepository<Factura, Integer>{
	@Query(value="SELECT * FROM factura f ,alquiler WHERE f.alquiler_idalquiler=:idalquiler",nativeQuery=true)
	List<Factura> findFacturasDeAlquiler(@Param("idalquiler") Integer idalquiler);
}
