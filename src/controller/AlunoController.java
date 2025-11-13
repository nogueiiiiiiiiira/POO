package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.AlunoDAO;
import model.Aluno;
import java.io.IOException;
import java.util.ArrayList;

public class AlunoController {
    private AlunoDAO alunoDAO;
    private ObjectMapper objectMapper;

    public AlunoController() {
        this.alunoDAO = new AlunoDAO();
        this.objectMapper = new ObjectMapper();
    }

    public AlunoDAO getAlunoDAO() {
        return alunoDAO;
    }

    public String converterAlunosParaJson(ArrayList<Aluno> alunos) {
        try {
            return objectMapper.writeValueAsString(alunos);
        } catch (IOException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    public Aluno converterJsonParaAluno(String json) {
        try {
            return objectMapper.readValue(json, Aluno.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Aluno();
        }
    }

    public Aluno converterJsonParaAlunoComId(String json) {
        try {
            return objectMapper.readValue(json, Aluno.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Aluno();
        }
    }
}
