package com.pweb.clinica.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pweb.clinica.dtos.EnderecoFormDTO;
import com.pweb.clinica.dtos.MedicoDTO;
import com.pweb.clinica.dtos.MedicoGetDTO;
import com.pweb.clinica.dtos.MedicoPostDTO;
import com.pweb.clinica.dtos.MedicoPutDTO;
import com.pweb.clinica.exceptions.DuplicateMedicoException;
import com.pweb.clinica.exceptions.EntityNotFoundException;
import com.pweb.clinica.models.Endereco;
import com.pweb.clinica.models.Especialidade;
import com.pweb.clinica.models.Medico;
import com.pweb.clinica.repositories.EspecialidadeRepository;
import com.pweb.clinica.repositories.MedicoRepository;

@Service
public class MedicoService implements PessoaService<MedicoGetDTO, MedicoPostDTO, MedicoPutDTO, MedicoDTO> {

	@Autowired
	private MedicoRepository medicoRepository;
	@Autowired
	private EnderecoService enderecoService;
	@Autowired
	private EspecialidadeRepository especialidadeRepository;

	@Override
	public Page<MedicoGetDTO> getPagina(Pageable pageable, String type) {
		if(type.equalsIgnoreCase("all")) {
			return medicoRepository.findAll(pageable).map(medico -> new MedicoGetDTO(medico, medico.getEspecialidade()));			
		}
		return medicoRepository.findAllByAtivoTrue(pageable).map(medico -> new MedicoGetDTO(medico, medico.getEspecialidade()));
	}
	
	public List<MedicoGetDTO> buscarMedicosPorEspecialidade(Long id) throws EntityNotFoundException {
		Especialidade especialidade = especialidadeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Especialidade"));
		
		List<Medico> medicos = medicoRepository.findByEspecialidade_idOrderByNomeAsc(id).orElse(null);
		return medicos.stream().map(medico -> new MedicoGetDTO(medico, especialidade)).collect(Collectors.toList());
	}

	@Override
	public MedicoDTO cadastrar(MedicoPostDTO medicoForm) throws EntityNotFoundException, DuplicateMedicoException {
		Optional<Medico> crmExistente = medicoRepository.findByCrm(medicoForm.crm());
		if(crmExistente.isPresent()) {
			throw new DuplicateMedicoException(new MedicoDTO(crmExistente.get()));
		}
		
		Especialidade especialidade = especialidadeRepository.findByTituloIgnoreCase(medicoForm.especialidade())
				.orElseThrow(() -> new EntityNotFoundException("Especialidade"));

		Endereco endereco = enderecoService.atribuirEndereco(medicoForm.endereco());
		Medico medico = new Medico(medicoForm, especialidade, endereco);
		
		medicoRepository.save(medico);
		return new MedicoDTO(medico);
	}

	@Override
	public MedicoDTO atualizar(Long id, MedicoPutDTO medicoForm) throws EntityNotFoundException {
		Medico medico = medicoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Medico"));
		
		medico.setNome(medicoForm.nome());
		medico.setTelefone(medicoForm.telefone());

		Endereco endereco = enderecoService.ajustarCampos(medico.getEndereco(), medicoForm.endereco());
		EnderecoFormDTO enderecoDto = new EnderecoFormDTO(endereco);
		
		medico.setEndereco(enderecoService.atribuirEndereco(enderecoDto));
		
		medicoRepository.save(medico);
		return new MedicoDTO(medico);
	}

	@Override
	public void tornarInativo(Long id) throws EntityNotFoundException {
		Medico medico = medicoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Medico"));

		medico.setAtivo(false);
		medicoRepository.save(medico);
	}
	
	public Medico buscarMedicoAtivo(Long idMedico) throws EntityNotFoundException {
		return medicoRepository.findByIdAndAtivoTrue(idMedico).orElseThrow(() -> new EntityNotFoundException("Medico"));
	}
	
	public List<Medico> buscarMedicosDisponiveis(Long idEspecialidade, Long idMedico, LocalDate data, LocalTime horario) {
		return medicoRepository.findMedicosDisponiveis(
				idEspecialidade, data, horario.minusHours(1), horario.plusHours(1));
	}
}
