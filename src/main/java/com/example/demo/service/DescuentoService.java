package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Descuento;
import com.example.demo.model.Patinete;
/**
 * Interfaz con metodos comunes CRUD
 * @author fran
 *
 */
public interface DescuentoService {
	public Descuento addDescuento(Descuento d);
	public Descuento findById(Integer id);
	public List<Descuento> findAll();
	

}
