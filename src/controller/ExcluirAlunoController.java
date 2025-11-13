package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

public class ExcluirAlunoController implements HttpHandler {
    private AlunoController alunoController;

    public ExcluirAlunoController(AlunoController alunoController) {
        this.alunoController = alunoController;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("DELETE".equals(exchange.getRequestMethod())) {
            try {
                String caminho = exchange.getRequestURI().getPath();
                String[] partes = caminho.split("/");
                int id = Integer.parseInt(partes[partes.length - 1]);
                
                alunoController.getAlunoDAO().excluirAluno(id);
                String resposta = "{\"sucesso\": true}";
                
                // Breno: Adicionado charset UTF-8 para garantir encoding correto
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, resposta.getBytes("UTF-8").length);
                
                OutputStream saida = exchange.getResponseBody();
                saida.write(resposta.getBytes("UTF-8"));
                saida.close();
            } catch (NumberFormatException e) {
                String erro = "{\"sucesso\": false, \"erro\": \"ID inválido\"}";
                // Breno: Adicionado charset UTF-8 para garantir encoding correto
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(400, erro.getBytes("UTF-8").length);
                OutputStream saida = exchange.getResponseBody();
                saida.write(erro.getBytes("UTF-8"));
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
