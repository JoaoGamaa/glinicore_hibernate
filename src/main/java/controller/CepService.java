package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import model.Endereco;

public class CepService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/%s/json/";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CepService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public Endereco buscarEnderecoPorCep(String cepInformado) {
        String cep = FuncoesUteis.somenteNumeros(cepInformado);
        if (cep.length() != 8) {
            throw new IllegalArgumentException("Informe um CEP com 8 numeros.");
        }

        String cepUrl = URLEncoder.encode(cep, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(VIACEP_URL, cepUrl)))
                .timeout(Duration.ofSeconds(12))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 400) {
                throw new IllegalArgumentException("CEP invalido.");
            }
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Nao foi possivel consultar o CEP agora.");
            }

            JsonNode json = objectMapper.readTree(response.body());
            if (json.path("erro").asBoolean(false)) {
                throw new IllegalArgumentException("CEP nao encontrado.");
            }

            Endereco endereco = new Endereco();
            endereco.setCep(texto(json, "cep"));
            endereco.setRua(texto(json, "logradouro"));
            endereco.setBairro(texto(json, "bairro"));
            endereco.setCidade(texto(json, "localidade"));
            endereco.setEstado(texto(json, "uf"));
            endereco.setComplemento(texto(json, "complemento"));
            return endereco;
        } catch (IOException ex) {
            throw new IllegalStateException("Falha de comunicacao com a API de CEP.", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consulta de CEP interrompida.", ex);
        }
    }

    private String texto(JsonNode json, String campo) {
        String valor = json.path(campo).asText("");
        return valor.isBlank() ? null : valor;
    }
}
