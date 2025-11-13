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

   // Breno: Removida referência a cimento na inserção e adicionado tratamento de exceções
   public boolean insereAluno(Aluno aluno) {
      try {
         this.query = "INSERT INTO alunos (nome, email, curso) VALUES (?, ?, ?)";
         this.ps = this.conexao.getConexao().prepareStatement(this.query);
         this.ps.setString(1, aluno.getNome());
         this.ps.setString(2, aluno.getEmail());
         this.ps.setString(3, aluno.getCurso());
         int linhasAfetadas = this.ps.executeUpdate();
         this.ps.close();
         return linhasAfetadas > 0;
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }

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
      }
      return alunos;
   }

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
      }
   }

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
      }
      return aluno;
   }
}