package com.example.demo.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Factura {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idfactura;
	private Double precio;
	
	@ManyToOne
	private Alquiler alquiler;
	
	public Factura() {
		super();
	}
	
	public Factura(Integer idfactura) {
		super();
		this.idfactura = idfactura;
	}

	public Factura(Integer idfactura, Double precio, Alquiler alquiler) {
		super();
		this.idfactura = idfactura;
		this.precio = precio;
		this.alquiler = alquiler;
	}

	public Factura(Double precio, Alquiler alquiler) {
		super();
		this.precio = precio;
		this.alquiler = alquiler;
	}

	public Integer getIdfactura() {
		return idfactura;
	}
	public void setIdfactura(Integer idfactura) {
		this.idfactura = idfactura;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Alquiler getAlquiler() {
		return alquiler;
	}
	public void setAlquiler(Alquiler alquiler) {
		this.alquiler = alquiler;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idfactura);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Factura other = (Factura) obj;
		return Objects.equals(idfactura, other.idfactura);
	}
	
	
	
	
}
