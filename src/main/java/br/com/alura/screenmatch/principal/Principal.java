package br.com.alura.screenmatch.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

public class Principal {

        Scanner sc = new Scanner(System.in);

        // declado como privado.
        private ConsumoApi consumo = new ConsumoApi();
        private ConverteDados conversor = new ConverteDados();

        // por ser dados FIXOS são declarados como constantes ("final"), pois não pode
        // ser
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
                System.out.println(
                                "IMPRESSÃO DA TEMPORADA + DETALHES DOS EPISODIOS: \n (titulo, numero do ep, avaliacao de cada ep, data lancamento)");
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
                // Para cada temporada t pegamos o episodio e depois percorremos os episodios
                // utilizando o e.
                // para cada episodio e, imprimimos e.titulo (titulo do episodio);
                System.out.println("NOME DOS EPISODIOS: ");
                temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

                // para pegar os episodios bem avaliados usando o LAMBDA
                // flatMap -> seria a forma de ter uma lista dentro da outra e trazer todas as
                // listas juntas.

                List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                                .flatMap(t -> t.episodios().stream())
                                .collect(Collectors.toList());

                System.out.println();
                System.out.println("Top 5 episódios:");

                dadosEpisodios.stream()
                                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                                .limit(5)
                                .forEach(System.out::println);
                System.out.println();

                // criando uma classe episodio
                System.out.println("LISTA DE EPISODIOS COM A SUA TEMPORADA INFORMADA: ");
                List<Episodio> episodios = temporadas.stream()
                                .flatMap(t -> t.episodios().stream()
                                .map(d -> new Episodio(t.numero(), d)))
                                .collect(Collectors.toList());
                // impressao com toString
                episodios.forEach(System.out::println);

                // Qual temporada é o episodio digitado
                // optional = é um objeto do container, onde guarda o "episodio" e vemos se
                // realmente tem o episodio buscado la dentro ou não
                System.out.println("Digite um trecho do título do episódio: ");
                var trechoTitulo = sc.nextLine();
                Optional<Episodio> episodioBuscado = episodios.stream()
                                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                                .findFirst();
                if (episodioBuscado.isPresent()) {
                        System.out.println("Episódio encontrado!");
                        System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
                } else {
                        System.out.println("Episódio não encontrado!");
                }

                // a partir do ano:
                System.out.println("A partir de que ano você deseja ver os episódios? ");
                var ano = sc.nextInt();
                sc.nextLine();
                LocalDate dataBusca = LocalDate.of(ano, 1, 1);
                DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                episodios.stream()
                                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                                .forEach(e -> System.out.println(
                                                "Temporada: " + e.getTemporada() +
                                                " Episódio: " + e.getTitulo() +
                                                " Data Lançamento: "
                                                + e.getDataLancamento().format(formatador)));

                // avaliacoes por temporada: 
                //chave valor, pegamos uma media da avaliação e atribuimos a uma temporada
                Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                                .filter(e -> e.getAvaliacao()>0.0)
                                .collect(Collectors.groupingBy(Episodio::getTemporada, 
                                        Collectors.averagingDouble(Episodio::getAvaliacao)));
                
                System.out.println("AVALIAÇÕES POR TEMPORADA: ");
                System.out.println(avaliacoesPorTemporada);

                // verificar as avaliacoes com estatisticas, utilizando uma classe do java que facilita. 
                DoubleSummaryStatistics est = episodios.stream()
                                .filter(e -> e.getAvaliacao()>0.0)
                                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

                System.out.println("Média:" + est.getAverage());
                System.out.println("Melhor episódio: " + est.getMax());
                System.out.println("Pior episódio: "+ est.getMin());
                System.out.println("Quantidade: "+ est.getCount());

        
        }
}