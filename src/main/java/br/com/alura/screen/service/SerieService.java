package br.com.alura.screen.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.screen.dto.SerieDTO;
import br.com.alura.screen.model.Serie;
import br.com.alura.screen.repository.SerieRepository;

@Service
public class SerieService {

	@Autowired
	private SerieRepository repositorio;

	public List<SerieDTO> obterTodasAsSeries() {
		
		return converteDados(repositorio.findAll());
	}

	public List<SerieDTO> obterTop5Series() {

		return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
	}

	/**
	 * Evitar duplicação de código
	 * 
	 * @param series
	 * @return
	 */
	private List<SerieDTO> converteDados(List<Serie> series) {

		return series.stream().map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getAvaliacao(),
				s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse())).collect(Collectors.toList());
	}

}