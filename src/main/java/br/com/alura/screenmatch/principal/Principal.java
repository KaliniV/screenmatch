package br.com.alura.screenmatch.principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {

    Scanner sc = new Scanner(System.in);

    // declado como privado.
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    // por ser dados FIXOS são declarados como constantes ("final"), pois não pode ser
    // alterado. Valor precisa ser atribuido já!
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=711a4316";

    public void exibeMenu() {
        // Consumo da API (antes):
        // var consumoApi = new ConsumoApi();
        // var json =
        // consumoApi.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=711a4316");
        // System.out.println(json);
        // System.out.println();
        System.out.println("Digite o nome da série para busca: ");
        var nomeSerie = sc.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        System.out.println("IMPRESSÃO DO JSON: ");
        System.out.println(json);
        System.out.println();

        // DadosSerie
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println("IMPRESSÃO SOBRE A SÉRIE: \n(titulo, total de temporadas, avaliação):");
        System.out.println(dadosSerie);
        System.out.println();

        // DadosTemporada
        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);

        }
        System.out.println("IMPRESSÃO DA TEMPORADA + DETALHES DOS EPISODIOS: \n (titulo, numero do ep, avaliacao de cada ep, data lancamento)");
        temporadas.forEach(System.out::println);

        // antes do forEach
        // for (int i =0; i<dadosSerie.totalTemporadas(); i++){
        // List <DadosEpisodio> episodiosPorTemporada = temporadas.get(i).episodios();
        // for(int j=0; j<episodiosPorTemporada.size(); j++){
        // System.out.println(episodiosPorTemporada.get(j).titulo());
        // }
        // }

        // recurso para utilizar o for acima mais facil e com menos linhas.
        // pegamos o nome da coleçao (temporadas), e coolocamos um metodo embutido
        // (.forEach) para conseguir iterar por todas as temporadas.
        // Para cada temporada t pegamos o episodio e depois percorremos os episodios utilizando o e. 
        // para cada episodio e, imprimimos e.titulo (titulo do episodio);
        System.out.println("NOME DOS EPISODIOS: ");
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

    }

}
