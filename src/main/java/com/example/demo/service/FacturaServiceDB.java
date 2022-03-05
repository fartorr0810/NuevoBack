package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Factura;
import com.example.demo.repository.FacturasRepository;

@Service
public class FacturaServiceDB {

	@Autowired
	private FacturasRepository repositoriofactura;
	
	public Factura addFactura(Factura f) {
		return repositoriofactura.save(f);
	}
	
	public Factura findById(Integer id) {
		return repositoriofactura.findById(id).orElse(null);
	}
	
	public Factura edit(Factura f) {
		return repositoriofactura.save(f);
	}
	public void deleteFactura(Factura f) {
		repositoriofactura.delete(f);
	}
	public void deleteFacturaById(Integer id) {
		repositoriofactura.deleteById(id);
	}
	public List<Factura> buscarTodasFacturas(Integer idalquiler){
		return repositoriofactura.findFacturasDeAlquiler(idalquiler);
	}
}
