package com.example.demo.exception;

public class InvalidRegisterException extends RuntimeException{
	public InvalidRegisterException() {
		super("Registro invalido");
	}
}
