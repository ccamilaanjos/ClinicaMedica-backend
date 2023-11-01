package com.pweb.consulta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pweb.consulta.dtos.ConsultaCancelDTO;
import com.pweb.consulta.dtos.ConsultaCreateDTO;
import com.pweb.consulta.dtos.ConsultaDTO;
import com.pweb.consulta.services.ConsultaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("consultas")
public class ConsultaController {
	@Autowired
	private ConsultaService consultaService;

	@PostMapping("/marcar")
	public ResponseEntity<?> marcarConsulta(@RequestBody @Valid ConsultaCreateDTO consultaForm) {
		ConsultaDTO consulta = consultaService.marcarConsulta(consultaForm);
		return ResponseEntity.status(HttpStatus.CREATED).body(consulta);
	}

	@PutMapping("/cancelar")
	public ResponseEntity<?> cancelarConsulta(@RequestParam(required = true) Long id,
			@RequestBody @Valid ConsultaCancelDTO consultaForm) {
		consultaService.cancelarConsulta(consultaForm, id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}