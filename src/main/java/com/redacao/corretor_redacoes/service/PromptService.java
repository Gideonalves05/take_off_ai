package com.redacao.corretor_redacoes.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PromptService {

    private static final Logger logger = LoggerFactory.getLogger(PromptService.class);

    @Value("${spring.gemini.apiKey}")
    private String apiKey;

    private static final String API_URL = "https://take-off-ai-150874964383.us-central1.run.app/";

    // configurar o RestTemplate com timeouts
    private RestTemplate restTemplateWithTimeout(int connectTimeout, int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(connectTimeout));
        factory.setReadTimeout((int) TimeUnit.SECONDS.toMillis(readTimeout));
        return new RestTemplate(factory);
    }

    public String enviarParaCorrecao(String textoRedacao) {
        RestTemplate restTemplate = restTemplateWithTimeout(10, 20); // Timeout de 10s para conectar e 20s para leitura

        // Configurar o cabeçalho de autenticação com a chave da API
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);

        // Montar o payload da requisição com o novo formato de prompt
        String prompt = criarPrompt(textoRedacao);

        // Criar a requisição HTTP
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of("prompt", prompt), headers);

        // Enviar a requisição para a API da Gemini
        try {
            logger.info("Enviando requisição para a API de correção...");
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Correção recebida com sucesso!");
                return response.getBody();
            } else {
                logger.error("Erro na correção da redação: Status {}", response.getStatusCode());
                return "Erro na correção da redação. Tente novamente mais tarde.";
            }
        } catch (Exception e) {
            logger.error("Falha ao conectar à API: {}", e.getMessage());
            return "Erro ao conectar ao serviço de correção. Verifique sua conexão ou tente mais tarde.";
        }
    }

    private String criarPrompt(String textoRedacao) {
        return String.format(
                "Você é um corretor de redações altamente qualificado. Sua tarefa é avaliar a redação abaixo, considerando as 5 competências do ENEM e as características do gênero dissertativo-argumentativo.\n\n" +
                        "**Redação:**\n%s\n\n" +
                        "Avalie a redação e forneça feedback detalhado sobre as seguintes competências:\n" +
                        "1. Competência I: Demonstrar domínio da norma padrão da língua escrita.\n" +
                        "2. Competência II: Compreender a proposta de intervenção proposta na redação.\n" +
                        "3. Competência III: Elaborar argumentos coerentes e consistentes.\n" +
                        "4. Competência IV: Organizar a estrutura da redação.\n" +
                        "5. Competência V: Utilizar recursos coesivos de maneira adequada.\n\n" +
                        "Ao final, forneça uma nota de 0 a 100, de acordo com os critérios do ENEM.\n",
                textoRedacao);
    }
}
