package com.example.demo.model;
/**
 * Clase con el archivo
 * @author fran
 *
 */
public class FileModel {
	///Atributos
	private String file;
	//Constructor
	public FileModel() {
		super();
	}

	public FileModel( String file) {
		super();
		this.file = file;
	}
//Getters y Setters
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	
}
