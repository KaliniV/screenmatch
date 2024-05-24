package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
// ignora o que não esta na busca.
public record DadosSerie(@JsonAlias("Title") String titulo, 
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao) {
    // Tem tambem o @JsonProperty() -> ele ler, e na hora de aparecer, ira aparecer o nome original.
}