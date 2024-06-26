package br.com.alura.screen.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screen.model.Categoria;
import br.com.alura.screen.model.DadosSerie;
import br.com.alura.screen.model.DadosTemporada;
import br.com.alura.screen.model.Episodio;
import br.com.alura.screen.model.Serie;
import br.com.alura.screen.repository.SerieRepository;
import br.com.alura.screen.service.ConsumoAPI;
import br.com.alura.screen.service.ConverteDados;

public class Principal {

	private Scanner leitura = new Scanner(System.in);
	private ConsumoAPI consumo = new ConsumoAPI();
	private ConverteDados conversor = new ConverteDados();

	private final String ENDERECO = "https://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=ed670f10";

	private List<DadosSerie> dadosSeries = new ArrayList<DadosSerie>();

	private SerieRepository repositorio;

	private List<Serie> series = new ArrayList<Serie>();

	private Optional<Serie> serieBusca;

	public Principal(SerieRepository repositorio) {

		this.repositorio = repositorio;
	}

	public void exibeMenu() {

		var opcao = -1;

		while (opcao != 0) {

			var menu = """
					1 - Buscar séries
					2 - Buscar episódios
					3 - Listar séries buscadas
					4 - Buscar série por título
					5 - Buscar series por ator
					6 - Top 5 Séries
					7 - Buscar Séries por Categoria
					8 - Filtrar Séries por Temporada
					9 - Buscar episódios por trecho
					10 - Top 5 episódios por série
					11 - Buscar episódios a partir de uma data

					0 - Sair
					""";

			System.out.println(menu);
			opcao = leitura.nextInt();
			leitura.nextLine();

			switch (opcao) {
			case 1:
				buscarSerieWeb();
				break;
			case 2:
				buscarEpisodioPorSerie();
				break;
			case 3:
				listarSeriesBuscadas();
				break;
			case 4:
				buscarSeriePorTitulo();
				break;
			case 5:
				buscarSeriesPorAtor();
				break;
			case 6:
				buscarTop5Series();
				break;
			case 7:
				buscarSeriesPorCategoria();
				break;
			case 8:
				filtrarSeriesPorTemporadaEAvaliacao();
				break;
			case 9:
				buscarEpisodioPorTrecho();
				break;
			case 10:
				topEpisodiosPorSerie();
				break;
			case 11:
				buscarEpisodiosDepoisDeUmaData();
				break;
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida");
			}
		}
	}

	private void buscarEpisodiosDepoisDeUmaData() {

		buscarSeriePorTitulo();
		if (serieBusca.isPresent()) {
			Serie serie = serieBusca.get();
			System.out.println("Digite o ano limite de lançamento: ");
			var anoLancamento = leitura.nextInt();
			leitura.nextLine();
			List<Episodio> episodiosAno = repositorio.episodioPorSerieEAno(serie, anoLancamento);
			episodiosAno.forEach(System.out::println);	
		}
		
	}

	private void topEpisodiosPorSerie() {

		buscarSeriePorTitulo();
		if (serieBusca.isPresent()) {
			Serie serie = serieBusca.get();
			List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
			topEpisodios.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s  - Avaliação:  %s\n",
					e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
		}

	}

	private void buscarEpisodioPorTrecho() {

		System.out.println("Qual o nome do episódio para busca?");
		var trechoEpisodio = leitura.nextLine();
		List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
		episodiosEncontrados.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
				e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));

	}

	private void filtrarSeriesPorTemporadaEAvaliacao() {

		System.out.println("Filtrar séries até quantas temporadas? ");
		var totalTemporadas = leitura.nextInt();
		leitura.nextLine();
		System.out.println("Com avaliação a partir de que valor? ");
		var avaliacao = leitura.nextDouble();
		leitura.nextLine();
		List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
		System.out.println("***Séries filtradas***");
		filtroSeries.forEach(s -> System.out.println(s.getTitulo() + " - avaliação: " + s.getAvaliacao()));

	}

	private void buscarSerieWeb() {

		DadosSerie dados = getDadosSerie();
		Serie serie = new Serie(dados);
		repositorio.save(serie);
		System.out.println(dados);

	}

	private DadosSerie getDadosSerie() {

		System.out.println("Digite o nome da série para busca: \n");
		var nomeSerie = leitura.nextLine();
		var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		return dados;
	}

	private void buscarEpisodioPorSerie() {

		listarSeriesBuscadas();
		System.out.println("Escolha uma série pelo nome: ");
		var nomeSerie = leitura.nextLine();
		Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
		if (serie.isPresent()) {
			var serieEncontrada = serie.get();
			List<DadosTemporada> temporadas = new ArrayList<>();
			for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
				var json = consumo.obterDados(
						ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
				DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
				temporadas.add(dadosTemporada);
			}
			temporadas.forEach(System.out::println);
			List<Episodio> episodios = temporadas.stream()
					.flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
					.collect(Collectors.toList());
			serieEncontrada.setEpisodios(episodios);
			repositorio.save(serieEncontrada);
		} else {
			System.out.println("Série não encontrada!");
		}
	}

	private void listarSeriesBuscadas() {

		series = repositorio.findAll();
		series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);

	}

	private void buscarSeriePorTitulo() {

		System.out.println("Escolha uma série pelo nome: ");
		var nomeSerie = leitura.nextLine();
		serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
		if (serieBusca.isPresent()) {
			System.out.println("Dados da série: " + serieBusca.get());
		} else {
			System.out.println("Série não encontrada!");
		}
	}

	private void buscarSeriesPorAtor() {

		System.out.println("Qual o nome para busca? ");
		var nomeAtor = leitura.nextLine();
		System.out.println("Avaliações a partir de que valor? ");
		var avaliacao = leitura.nextDouble();
		List<Serie> seriresEncontradas = repositorio
				.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
		System.out.println("Séries em que " + nomeAtor + " trabalho: ");
		seriresEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));

	}

	private void buscarTop5Series() {

		List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
		serieTop.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
	}

	private void buscarSeriesPorCategoria() {

		System.out.println("Deseja buscar séires de que categoria/genêro? ");
		var nomeGenero = leitura.nextLine();
		Categoria categoria = Categoria.fromPortugues(nomeGenero);
		List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
		System.out.println("Séries da categoria: " + nomeGenero);
		seriesPorCategoria.forEach(System.out::println);

	}
}
