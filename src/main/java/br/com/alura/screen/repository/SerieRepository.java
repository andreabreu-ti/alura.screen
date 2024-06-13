package br.com.alura.screen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.screen.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {

	Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

	List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);

	List<Serie> findTop5ByOrderByAvaliacaoDesc();

}
