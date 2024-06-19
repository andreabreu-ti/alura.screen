package br.com.alura.screen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.screen.dto.SerieDTO;
import br.com.alura.screen.service.SerieService;

@RestController
@RequestMapping("/series")
public class SerieController {

	@Autowired
	private SerieService servico;

	@GetMapping
	public List<SerieDTO> obterSeries() {

		return servico.obterTodasAsSeries();
	}

	@GetMapping("/top5")
	public List<SerieDTO> obterTop5Series() {

		return servico.obterTop5Series();
	}

}