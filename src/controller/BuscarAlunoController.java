package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class BuscarAlunoController implements HttpHandler {
    private AlunoController alunoController;

    public BuscarAlunoController(AlunoController alunoController) {
        this.alunoController = alunoController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Breno: Adicionado suporte a CORS para permitir requisições do navegador
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        
        // Breno: Responde a requisições OPTIONS (preflight) do CORS
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            return;
        }
        
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                String caminho = exchange.getRequestURI().getPath();
                String[] partes = caminho.split("/");
                
                if (partes.length < 5) {
                    String resposta = "{\"erro\": \"ID não fornecido\"}";
                    // Breno: Adicionado charset UTF-8 para garantir encoding correto
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(400, resposta.getBytes("UTF-8").length);
                    OutputStream saida = exchange.getResponseBody();
                    saida.write(resposta.getBytes("UTF-8"));
                    saida.close();
                    return;
                }
                
                int id = Integer.parseInt(partes[4]);
                var aluno = alunoController.getAlunoDAO().buscarAlunoPorId(id);
                String resposta;
                
                // Breno: Removida referência a data_nascimento do JSON de resposta
                if (aluno != null) {
                    resposta = String.format("{\"id\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"curso\": \"%s\"}",
                        aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getCurso());
                } else {
                    resposta = "{\"erro\": \"Aluno não encontrado\"}";
                }
                
                // Breno: Adicionado charset UTF-8 para garantir encoding correto
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, resposta.getBytes("UTF-8").length);
                
                OutputStream saida = exchange.getResponseBody();
                saida.write(resposta.getBytes("UTF-8"));
                saida.close();
            } catch (NumberFormatException e) {
                String resposta = "{\"erro\": \"ID inválido\"}";
                // Breno: Adicionado charset UTF-8 para garantir encoding correto
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(400, resposta.getBytes("UTF-8").length);
                OutputStream saida = exchange.getResponseBody();
                saida.write(resposta.getBytes("UTF-8"));
                saida.close();
            } catch (Exception e) {
                // Breno: Adicionado tratamento de erros com resposta JSON adequada
                e.printStackTrace();
                String erro = "{\"erro\": \"Erro ao buscar aluno: " + e.getMessage() + "\"}";
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