package com.redacao.corretor_redacoes.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CorretorRedacoesService {

    private static final Logger logger = LoggerFactory.getLogger(CorretorRedacoesService.class);
    private final PromptService promptService;

    @Autowired
    public CorretorRedacoesService(PromptService promptService) {
        this.promptService = promptService;
    }

    public String processarRedacaoDeTexto(String textoRedacao) {
        logger.info("Processando redação de texto.");
        return promptService.enviarParaCorrecao(textoRedacao);
    }

    public String processarRedacaoDePDF(MultipartFile arquivoPDF) {
        logger.info("Processando redação de PDF: {}", arquivoPDF.getOriginalFilename());

        try (InputStream inputStream = arquivoPDF.getInputStream()) {
            // Ler o texto do PDF utilizando o seu método de leitura
            String textoRedacao = lerTextoDoPDF(inputStream);
            return promptService.enviarParaCorrecao(textoRedacao);
        } catch (IOException e) {
            logger.error("Erro ao processar o arquivo PDF", e);
            throw new RuntimeException("Erro ao processar o arquivo PDF", e);
        }
    }


    public String lerTextoDoPDF(InputStream inputStream) {
        StringBuilder texto = new StringBuilder();

        try (PDDocument document = PDDocument.load(inputStream)) {
            // Verifica se o documento está vazio
            if (!document.isEncrypted()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                texto.append(pdfStripper.getText(document));
            } else {
                throw new IOException("O PDF está criptografado e não pode ser lido.");
            }
        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo PDF: {}", e.getMessage());
            throw new RuntimeException("Erro ao ler o arquivo PDF", e);
        }

        return texto.toString();
    }
}
