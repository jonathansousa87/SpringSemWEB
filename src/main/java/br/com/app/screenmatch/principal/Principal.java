package br.com.app.screenmatch.principal;

import br.com.app.screenmatch.model.DadosEpisodio;
import br.com.app.screenmatch.model.DadosSerie;
import br.com.app.screenmatch.model.DadosTemporada;
import br.com.app.screenmatch.model.Episodios;
import br.com.app.screenmatch.service.ConsumoApi;
import br.com.app.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner sc = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=84504f08";

    public void exibeMenu() {
        System.out.println("Digite o nome da serie para busca");
        var nomeSerie = sc.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);


        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas
                .stream().filter(n -> n.numero() != null && n.episodios() != null)
                .forEach(System.out::println);

//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> dadosEpisodios = temporadas.get(i).episodios();
//            for (int j = 0; j < dadosEpisodios.size(); j++) {
//                System.out.println(dadosEpisodios.get(j).titulo());
//            }
//        }
        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        dadosEpisodios.add(new DadosEpisodio("teste", 3, "10.0", "2020-01-01"));
        dadosEpisodios.forEach(System.out::println);

        List<Episodios> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodios(t.numero(), d))
                ).collect(Collectors.toList());
        episodios.forEach(System.out::println);

        System.out.println("Digite o trecho do titulo do episodio");
        var trechoDoTitulo = sc.nextLine();
        Optional<Episodios> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoDoTitulo.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()){
            System.out.println("Episodio encontrado");
            System.out.println("Temporada:" +episodioBuscado.get().getTemporada()+", Episodio: "+ episodioBuscado.get().getNumeroEpisodio()+" Titulo: "+episodioBuscado.get().getTitulo());
        }else {
            System.out.println("Episodio não encontrado!");
        }

        System.out.println("\nTop 10 episodios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Primeiro filto N/A " + e))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .peek(e -> System.out.println("Ordenação " + e))
                .limit(10)
                .peek(e -> System.out.println("Limite de episodios " + e))
                .map(e -> e.titulo().toUpperCase())
                .peek(e -> System.out.println("Titulo e letra maiuscula " + e))
                .forEach(System.out::println);

        System.out.println("A partir de que ano você deseja os episodios");
        var ano = sc.nextInt();
        sc.nextLine();
        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporado: " + e.getTemporada() +
                        " Episodio: " + e.getTitulo() +
                        " Data lançamento: " + e.getDataLancamento().format(formatter)
                ));

        Map<Integer, Double> avaliacoesPorTempordas = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodios::getTemporada,
                        Collectors.averagingDouble(Episodios::getAvaliacao)));
        System.out.println(avaliacoesPorTempordas);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodios::getAvaliacao));

        System.out.println("Média:"+ est.getAverage());
        System.out.println("Melhor episodio:"+ est.getMax());
        System.out.println("Pior episodio:"+ est.getMin());
        System.out.println("Quantidade:"+ est.getCount());

    }
}
