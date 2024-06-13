package br.com.alura.screen.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

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

	public Principal(SerieRepository repositorio) {
		// TODO Auto-generated constructor stub

		this.repositorio = repositorio;
	}

	public void exibeMenu() {

		var opcao = -1;

		while (opcao != 0) {

			var menu = """
					1 - Buscar séries
					2 - Buscar episódios
					3 - Listar séires buscadas
					4 - Buscar série por título
					5 - Buscar series por ator

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
			case 0:
				System.out.println("Saindo...");
				break;
			default:
				System.out.println("Opção inválida");
			}
		}
	}

	private void buscarSerieWeb() {

		// DadosSerie dados = getDadosSerie();
		// dadosSeries.add(dados);
		// System.out.println(dados);

		/**
		 * Chamar o repositório Interface para salvar, Utilizar injeção de dependências
		 */
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

//		Optional<Serie> serie = series.stream()
//				.filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase())).findFirst();

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

		/*
		 * DadosSerie dadosSerie = getDadosSerie(); List<DadosTemporada> temporadas =
		 * new ArrayList<>();
		 * 
		 * for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) { var json =
		 * consumo.obterDados(ENDERECO + dadosSerie.titulo().replace(" ", "+") +
		 * "&season=" + i + API_KEY); DadosTemporada dadosTemporada =
		 * conversor.obterDados(json, DadosTemporada.class);
		 * temporadas.add(dadosTemporada); } temporadas.forEach(System.out::println);
		 */
	}

	private void listarSeriesBuscadas() {

//		List<Serie> series = new ArrayList<>();
//		series = dadosSeries.stream().map(d -> new Serie(d)).collect(Collectors.toList());
//		series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);

		series = repositorio.findAll();
		series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);

	}

	private void buscarSeriePorTitulo() {

		System.out.println("Escolha uma série pelo nome: ");
		var nomeSerie = leitura.nextLine();

		Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

		if (serieBuscada.isPresent()) {

			System.out.println("Dados da série: " + serieBuscada.get());

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

}
