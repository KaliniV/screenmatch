package br.com.alura.screenmatch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverteDados implements IConverteDados{
            // do Jackson que nem o GSON, realiza a conversao
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            
            // ObjectMapper mapper = new ObjectMapper();
            // mapper.readValue(json, classe);
            // readValue -> ler o json e converte para classe que foi passada. 
            return mapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

   
}
