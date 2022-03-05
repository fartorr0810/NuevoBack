package com.example.demo.model;
/**
 * Clase para devolver un mensaje cuando se realicen las subidas de archivo
 * @author fran
 *
 */
public class FileMessage {
//Atributos
	private String message;
	//constructores
	public FileMessage(String message) {
		super();
		this.message = message;
	}

	public FileMessage() {
		super();
	}
//Getters y Setters
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
