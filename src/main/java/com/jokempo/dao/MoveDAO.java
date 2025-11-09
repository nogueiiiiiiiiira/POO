package com.jokempo.dao;

import com.jokempo.model.Move;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoveDAO {

    public void create(Move move) throws SQLException {
        String sql = "INSERT INTO moves (game_id, player_id, move, move_order) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, move.getGameId());
            stmt.setInt(2, move.getPlayerId());
            stmt.setString(3, move.getMove().toString());
            stmt.setInt(4, move.getMoveOrder());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    move.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Move> findByGameId(int gameId) throws SQLException {
        List<Move> moves = new ArrayList<>();
        String sql = "SELECT * FROM moves WHERE game_id = ? ORDER BY move_order";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    moves.add(new Move(rs.getInt("id"), rs.getInt("game_id"), rs.getInt("player_id"),
                                     Move.MoveType.valueOf(rs.getString("move")), rs.getInt("move_order"),
                                     rs.getTimestamp("created_at")));
                }
            }
        }
        return moves;
    }

    public Move findById(int id) throws SQLException {
        String sql = "SELECT * FROM moves WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Move(rs.getInt("id"), rs.getInt("game_id"), rs.getInt("player_id"),
                                  Move.MoveType.valueOf(rs.getString("move")), rs.getInt("move_order"),
                                  rs.getTimestamp("created_at"));
                }
            }
        }
        return null;
    }

    public void deleteByGameId(int gameId) throws SQLException {
        String sql = "DELETE FROM moves WHERE game_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameId);
            stmt.executeUpdate();
        }
    }
}
