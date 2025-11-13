package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Aluno;

public class AlunoDAO {
   private Conexao conexao = Conexao.getInstance();
   private String query;
   private PreparedStatement ps;

   public AlunoDAO() {}

   // Breno: Removida referência a data_nascimento na inserção e adicionado tratamento de exceções
   public void insereAluno(Aluno aluno) {
      try {
         this.query = "INSERT INTO alunos (nome, email, curso) VALUES (?, ?, ?)";
         this.ps = this.conexao.getConexao().prepareStatement(this.query);
         this.ps.setString(1, aluno.getNome());
         this.ps.setString(2, aluno.getEmail());
         this.ps.setString(3, aluno.getCurso());
         this.ps.executeUpdate();
         this.ps.close();
      } catch (SQLException e) {
         e.printStackTrace();
         throw new RuntimeException("Erro ao inserir aluno: " + e.getMessage(), e);
      }
   }

   // Breno: Removida referência a data_nascimento na listagem e adicionado tratamento de exceções
   public ArrayList<Aluno> listarAlunos() {
      ArrayList<Aluno> alunos = new ArrayList<>();
      try {
         this.query = "SELECT id, nome, email, curso FROM alunos";
         this.ps = this.conexao.getConexao().prepareStatement(this.query);
         ResultSet rs = this.ps.executeQuery();
         while (rs.next()) {
            Aluno aluno = new Aluno();
            aluno.setId(rs.getInt("id"));
            aluno.setNome(rs.getString("nome"));
            aluno.setEmail(rs.getString("email"));
            aluno.setCurso(rs.getString("curso"));
            alunos.add(aluno);
         }
         rs.close();
         this.ps.close();
      } catch (SQLException e) {
         e.printStackTrace();
         throw new RuntimeException("Erro ao listar alunos: " + e.getMessage(), e);
      }
      return alunos;
   }

   // Breno: Removida referência a data_nascimento na atualização e adicionado tratamento de exceções
   public void atualizarAluno(Aluno aluno) {
      try {
         this.query = "UPDATE alunos SET nome = ?, email = ?, curso = ? WHERE id = ?";
         this.ps = this.conexao.getConexao().prepareStatement(this.query);
         this.ps.setString(1, aluno.getNome());
         this.ps.setString(2, aluno.getEmail());
         this.ps.setString(3, aluno.getCurso());
         this.ps.setInt(4, aluno.getId());
         this.ps.executeUpdate();
         this.ps.close();
      } catch (SQLException e) {
         e.printStackTrace();
         throw new RuntimeException("Erro ao atualizar aluno: " + e.getMessage(), e);
      }
   }

   // Breno: Adicionado tratamento de exceções com propagação de erros
   public void excluirAluno(int id) {
      try {
         this.query = "DELETE FROM alunos WHERE id = ?";
         this.ps = this.conexao.getConexao().prepareStatement(this.query);
         this.ps.setInt(1, id);
         this.ps.executeUpdate();
         this.ps.close();
      } catch (SQLException e) {
         e.printStackTrace();
         throw new RuntimeException("Erro ao excluir aluno: " + e.getMessage(), e);
      }
   }

   // Breno: Removida referência a data_nascimento na busca e adicionado tratamento de exceções
   public Aluno buscarAlunoPorId(int id) {
      Aluno aluno = null;
      try {
         this.query = "SELECT id, nome, email, curso FROM alunos WHERE id = ?";
         this.ps = this.conexao.getConexao().prepareStatement(this.query);
         this.ps.setInt(1, id);
         ResultSet rs = this.ps.executeQuery();
         if (rs.next()) {
            aluno = new Aluno();
            aluno.setId(rs.getInt("id"));
            aluno.setNome(rs.getString("nome"));
            aluno.setEmail(rs.getString("email"));
            aluno.setCurso(rs.getString("curso"));
         }
         rs.close();
         this.ps.close();
      } catch (SQLException e) {
         e.printStackTrace();
         throw new RuntimeException("Erro ao buscar aluno: " + e.getMessage(), e);
      }
      return aluno;
   }
}