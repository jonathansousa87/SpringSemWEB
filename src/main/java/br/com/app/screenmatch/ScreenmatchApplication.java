package br.com.app.screenmatch;

import br.com.app.screenmatch.model.DadosEpisodio;
import br.com.app.screenmatch.model.DadosSerie;
import br.com.app.screenmatch.model.DadosTemporada;
import br.com.app.screenmatch.principal.Principal;
import br.com.app.screenmatch.service.ConsumoApi;
import br.com.app.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		new Principal().exibeMenu();
	}
}
