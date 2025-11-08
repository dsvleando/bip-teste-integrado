package com.example.backend.api.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.backend.api.dto.ErrorResponseDTO;
import com.example.beneficio.domain.exception.BeneficioException;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BeneficioException.class)
	public ResponseEntity<ErrorResponseDTO> handleBeneficioException(BeneficioException ex) {
		log.warn("Exceção de domínio: {}", ex.getMessage());
		HttpStatus status = ex.getMessage().contains("não encontrado") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
		ErrorResponseDTO error = ErrorResponseDTO.builder().timestamp(LocalDateTime.now()).status(status.value())
				.error(status.getReasonPhrase()).message(ex.getMessage()).build();
		return ResponseEntity.status(status).body(error);
	}

	@ExceptionHandler(OptimisticLockException.class)
	public ResponseEntity<ErrorResponseDTO> handleOptimisticLock(OptimisticLockException ex) {
		log.error("Conflito de versão otimista: {}", ex.getMessage());
		ErrorResponseDTO error = ErrorResponseDTO.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.CONFLICT.value()).error("Conflict")
				.message("Conflito de versão. A entidade foi modificada por outro processo. Tente novamente.").build();
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
		log.warn("Erros de validação: {}", ex.getMessage());
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		Map<String, Object> response = new HashMap<>();
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.BAD_REQUEST.value());
		response.put("error", "Validation Error");
		response.put("message", "Erros de validação");
		response.put("errors", errors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
		log.error("Erro inesperado: ", ex);
		ErrorResponseDTO error = ErrorResponseDTO.builder().timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).error("Internal Server Error")
				.message("Ocorreu um erro inesperado. Tente novamente mais tarde.").build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
