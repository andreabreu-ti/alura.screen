package br.com.alura.screen.dto;

import br.com.alura.screen.model.Categoria;

public record SerieDTO(

		Long id, 
		String titulo, 
		Integer totalTemporadas, 
		Double avaliacao, 
		Categoria genero, 
		String atores,
		String poster, 
		String sinopse

) {
}
