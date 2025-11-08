package com.example.beneficio.domain.exception;

public class BeneficioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BeneficioException(String message) {
		super(message);
	}

	public BeneficioException(String message, Throwable cause) {
		super(message, cause);
	}
}
