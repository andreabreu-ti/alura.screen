package br.com.alura.screen;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.alura.screen.service.ConsumoAPI;

@SpringBootApplication
public class Application implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		var consumoAPI = new ConsumoAPI();
		var json = consumoAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=ed670f10"); 
		System.out.println(json);
		
		//		json = consumoAPI.obterDados("https://coffee.alexflipnote.dev/random.json");
		//		System.out.println(json);
	}

}
