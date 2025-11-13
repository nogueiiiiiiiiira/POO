package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class CadastrarAlunoController implements HttpHandler {
    private AlunoController alunoController;

    public CadastrarAlunoController(AlunoController alunoController) {
        this.alunoController = alunoController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                // Breno: Adicionado encoding UTF-8 explícito na leitura do corpo da requisição
                String corpoRequisicao = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");

                var aluno = alunoController.converterJsonParaAluno(corpoRequisicao);
                
                // Breno: Adicionada validação básica do nome antes de inserir
                if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
                    String erro = "{\"sucesso\": false, \"erro\": \"Nome é obrigatório\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(400, erro.getBytes("UTF-8").length);
                    OutputStream saida = exchange.getResponseBody();
                    saida.write(erro.getBytes("UTF-8"));
                    saida.close();
                    return;
                }
                
                alunoController.getAlunoDAO().insereAluno(aluno);
                String resposta = "{\"sucesso\": true}";
                
                // Breno: Adicionado charset UTF-8 para garantir encoding correto
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, resposta.getBytes("UTF-8").length);
                
                OutputStream saida = exchange.getResponseBody();
                saida.write(resposta.getBytes("UTF-8"));
                saida.close();
            } catch (Exception e) {
                // Breno: Adicionado tratamento de erros com resposta JSON adequada
                e.printStackTrace();
                String erro = "{\"sucesso\": false, \"erro\": \"" + e.getMessage() + "\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(500, erro.getBytes("UTF-8").length);
                OutputStream saida = exchange.getResponseBody();
                saida.write(erro.getBytes("UTF-8"));
                saida.close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
        }
    }
}