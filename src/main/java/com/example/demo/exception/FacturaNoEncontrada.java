package com.example.demo.exception;

public class FacturaNoEncontrada extends RuntimeException {
	public FacturaNoEncontrada() {
		super("Factura no encontrada");
	}
}
