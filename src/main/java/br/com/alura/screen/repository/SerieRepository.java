package br.com.alura.screen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.screen.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long>{

}
