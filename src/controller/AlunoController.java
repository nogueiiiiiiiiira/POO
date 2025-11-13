package controller;

import dao.AlunoDAO;
import model.Aluno;
import java.util.ArrayList;

public class AlunoController {
    // Breno: Campo marcado como final para melhorar imutabilidade
    private final AlunoDAO alunoDAO;

    public AlunoController() {
        this.alunoDAO = new AlunoDAO();
    }

    public AlunoDAO getAlunoDAO() {
        return alunoDAO;
    }

    // Breno: Melhorada conversão para JSON removendo referência a data_nascimento e adicionando escape de caracteres especiais
    public String converterAlunosParaJson(ArrayList<Aluno> alunos) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < alunos.size(); i++) {
            Aluno aluno = alunos.get(i);
            json.append(String.format(
                "{\"id\": %d, \"nome\": \"%s\", \"email\": \"%s\", \"curso\": \"%s\"}",
                aluno.getId(), 
                escaparJson(aluno.getNome()), 
                escaparJson(aluno.getEmail()), 
                escaparJson(aluno.getCurso())
            ));
            if (i < alunos.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
    
    // Breno: Método adicionado para escapar caracteres especiais no JSON
    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    // Breno: Reimplementado parser JSON mais robusto e simples, com tratamento de erros e logs
    public Aluno converterJsonParaAluno(String json) {
        Aluno aluno = new Aluno();
        try {
            if (json == null || json.trim().isEmpty()) {
                return aluno;
            }
            
            // Remove chaves externas e espaços
            json = json.trim();
            if (json.startsWith("{")) {
                json = json.substring(1);
            }
            if (json.endsWith("}")) {
                json = json.substring(0, json.length() - 1);
            }
            
            // Divide por vírgulas simples
            String[] pares = json.split(",");
            
            for (String par : pares) {
                par = par.trim();
                if (par.isEmpty()) continue;
                
                int doisPontos = par.indexOf(':');
                if (doisPontos == -1) continue;
                
                String chave = par.substring(0, doisPontos).trim();
                String valor = par.substring(doisPontos + 1).trim();
                
                // Remove aspas das chaves e valores
                chave = chave.replaceAll("^[\"']|[\"']$", "");
                valor = valor.replaceAll("^[\"']|[\"']$", "");
                
                // Breno: Convertido para switch expression moderno (arrow syntax) do Java 14+
                switch (chave) {
                    case "nome" -> aluno.setNome(valor);
                    case "email" -> aluno.setEmail(valor);
                    case "curso" -> aluno.setCurso(valor);
                    default -> {
                        // Ignora chaves desconhecidas
                    }
                }
            }
            
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            // Breno: Tratamento de exceções específicas substituindo printStackTrace
            System.err.println("Erro ao converter JSON: " + e.getMessage());
        } catch (Exception e) {
            // Breno: Tratamento genérico de exceções sem printStackTrace
            System.err.println("Erro inesperado ao converter JSON: " + e.getMessage());
        }
        return aluno;
    }

    // Breno: Reimplementado parser JSON com ID, usando a mesma lógica robusta do método anterior
    public Aluno converterJsonParaAlunoComId(String json) {
        Aluno aluno = new Aluno();
        try {
            if (json == null || json.trim().isEmpty()) {
                return aluno;
            }
            
            // Remove chaves externas e espaços
            json = json.trim();
            if (json.startsWith("{")) {
                json = json.substring(1);
            }
            if (json.endsWith("}")) {
                json = json.substring(0, json.length() - 1);
            }
            
            // Divide por vírgulas simples
            String[] pares = json.split(",");
            
            for (String par : pares) {
                par = par.trim();
                if (par.isEmpty()) continue;
                
                int doisPontos = par.indexOf(':');
                if (doisPontos == -1) continue;
                
                String chave = par.substring(0, doisPontos).trim();
                String valor = par.substring(doisPontos + 1).trim();
                
                // Remove aspas das chaves e valores
                chave = chave.replaceAll("^[\"']|[\"']$", "");
                valor = valor.replaceAll("^[\"']|[\"']$", "");
                
                // Breno: Convertido para switch expression moderno (arrow syntax) do Java 14+
                switch (chave) {
                    case "id" -> aluno.setId(Integer.parseInt(valor));
                    case "nome" -> aluno.setNome(valor);
                    case "email" -> aluno.setEmail(valor);
                    case "curso" -> aluno.setCurso(valor);
                    default -> {
                        // Ignora chaves desconhecidas
                    }
                }
            }
        } catch (NumberFormatException e) {
            // Breno: Tratamento específico para erro de conversão de número
            System.err.println("Erro ao converter ID do JSON: " + e.getMessage());
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            // Breno: Tratamento de exceções específicas substituindo printStackTrace
            System.err.println("Erro ao converter JSON com ID: " + e.getMessage());
        } catch (Exception e) {
            // Breno: Tratamento genérico de exceções sem printStackTrace
            System.err.println("Erro inesperado ao converter JSON com ID: " + e.getMessage());
        }
        return aluno;
    }
}