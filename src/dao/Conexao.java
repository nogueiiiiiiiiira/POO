package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {
    private static Conexao conex;
    private final String con_banco;
    private final String usuario_mysql;
    private final String senha_mysql;
    private Connection conn;

    private Conexao(){
        usuario_mysql = "root";
        senha_mysql = "PUC@1234";
        con_banco = "jdbc:mysql://127.0.0.1:3306/?useSSL=false";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection(con_banco, usuario_mysql, senha_mysql);
            java.sql.Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS escola");
            stmt.executeUpdate("USE escola");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS alunos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "curso VARCHAR(50) NOT NULL" +
                ")");
            stmt.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Conexao getInstance(){
        if(conex == null){
            conex = new Conexao();
        }
        return conex;
    }

    public Connection getConexao(){
        return this.conn;
    }
}