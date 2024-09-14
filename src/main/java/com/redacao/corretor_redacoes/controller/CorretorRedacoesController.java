package com.redacao.corretor_redacoes.controller;

import com.redacao.corretor_redacoes.service.CorretorRedacoesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/corrigir")
public class CorretorRedacoesController {

    private final CorretorRedacoesService corretorRedacoesService;

    public CorretorRedacoesController(CorretorRedacoesService corretorRedacoesService) {
        this.corretorRedacoesService = corretorRedacoesService;
    }

    @PostMapping("/texto")
    public ResponseEntity<String> corrigirTexto(@RequestBody String textoRedacao) {
        String resultado = corretorRedacoesService.processarRedacaoDeTexto(textoRedacao);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/pdf")
    public ResponseEntity<String> processarRedacaoPDF(@RequestParam("arquivoPDF") MultipartFile arquivoPDF) {
        String resultado = corretorRedacoesService.processarRedacaoDePDF(arquivoPDF);
        return ResponseEntity.ok(resultado);
    }

}
