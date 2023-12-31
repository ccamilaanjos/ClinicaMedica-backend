package com.pweb.clinica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pweb.clinica.dtos.ConsultaCancelDTO;
import com.pweb.clinica.dtos.ConsultaCreateDTO;
import com.pweb.clinica.dtos.ConsultaDTO;
import com.pweb.clinica.exceptions.ClinicaUnavailableException;
import com.pweb.clinica.exceptions.ConflictingScheduleException;
import com.pweb.clinica.exceptions.ConsultaCanceladaException;
import com.pweb.clinica.exceptions.ConsultaNotFoundException;
import com.pweb.clinica.exceptions.EmptyListException;
import com.pweb.clinica.exceptions.EntityNotFoundException;
import com.pweb.clinica.services.ConsultaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class ConsultaController {
	@Autowired
	private ConsultaService consultaService;

	@PostMapping("/marcarConsulta")
	public ResponseEntity<?> marcarConsulta(
			@RequestBody @Valid ConsultaCreateDTO consultaForm) {
		try {
			ConsultaDTO consulta = consultaService.marcarConsulta(consultaForm);
			return ResponseEntity.status(HttpStatus.CREATED).body(consulta);
		} catch (ClinicaUnavailableException | ConflictingScheduleException | EmptyListException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (EntityNotFoundException  e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@PutMapping("/cancelarConsulta")
	public ResponseEntity<?> cancelarConsulta(
			@RequestParam(required=true) Long id, @RequestBody @Valid ConsultaCancelDTO consultaForm) {
		try {
			consultaService.cancelarConsulta(consultaForm, id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (ConsultaNotFoundException | ConsultaCanceladaException | ConflictingScheduleException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());			
		}
	}
}
