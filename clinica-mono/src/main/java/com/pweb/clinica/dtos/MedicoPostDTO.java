package com.pweb.clinica.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record MedicoPostDTO(
		@NotBlank(message = "O campo nome não pode ser nulo ou vazio") String nome,
		@NotBlank(message = "O campo email não pode ser nulo ou vazio") String email,
		@NotBlank(message = "O campo telefone não pode ser nulo ou vazio") String telefone,
		@NotBlank(message = "O campo crm não pode ser nulo ou vazio") String crm,
		@NotBlank(message = "O campo especialidade não pode ser nulo ou vazio") String especialidade,
		@Valid EnderecoFormDTO endereco) {
}
