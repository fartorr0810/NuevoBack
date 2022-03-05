package com.example.demo.model;
/**
 * Clase DTO del archivo
 * @author fran
 *
 */
public class FileDTO {
//Atributos
	private String fileSource;
	//Constructor
	public FileDTO() {
		super();
	}
//Getters y Setters
	public FileDTO(String fileSource) {
		super();
		this.fileSource = fileSource;
	}

	public String getFileSource() {
		return fileSource;
	}

	public void setFileSource(String fileSource) {
		this.fileSource = fileSource;
	}
	
	
	
}
