package br.com.alura.screenmatch.service;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);
//  Generics => <T> quando não sabemos o que sera utilizado.
}
