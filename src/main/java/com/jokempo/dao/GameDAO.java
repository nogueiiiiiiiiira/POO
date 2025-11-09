package com.jokempo.dao;

import com.jokempo.model.Game;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    public void create(Game game) throws SQLException {
        String sql = "INSERT INTO games (player1_id, player2_id, game_mode, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, game.getPlayer1Id());
            if (game.getPlayer2Id() != null) {
                stmt.setInt(2, game.getPlayer2Id());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, game.getGameMode().toString());
            stmt.setString(4, game.getStatus().toString());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    game.setId(rs.getInt(1));
                }
            }
        }
    }

    public Game findById(int id) throws SQLException {
        String sql = "SELECT * FROM games WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer player2Id = rs.getInt("player2_id");
                    if (rs.wasNull()) player2Id = null;
                    Integer winnerId = rs.getInt("winner_id");
                    if (rs.wasNull()) winnerId = null;

                    return new Game(rs.getInt("id"), rs.getInt("player1_id"), player2Id,
                                  Game.GameMode.valueOf(rs.getString("game_mode")),
                                  Game.GameStatus.valueOf(rs.getString("status")),
                                  winnerId, rs.getTimestamp("created_at"), rs.getTimestamp("finished_at"));
                }
            }
        }
        return null;
    }

    public List<Game> findByPlayerId(int playerId) throws SQLException {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games WHERE player1_id = ? OR player2_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playerId);
            stmt.setInt(2, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Integer player2Id = rs.getInt("player2_id");
                    if (rs.wasNull()) player2Id = null;
                    Integer winnerId = rs.getInt("winner_id");
                    if (rs.wasNull()) winnerId = null;

                    games.add(new Game(rs.getInt("id"), rs.getInt("player1_id"), player2Id,
                                     Game.GameMode.valueOf(rs.getString("game_mode")),
                                     Game.GameStatus.valueOf(rs.getString("status")),
                                     winnerId, rs.getTimestamp("created_at"), rs.getTimestamp("finished_at")));
                }
            }
        }
        return games;
    }

    public void update(Game game) throws SQLException {
        String sql = "UPDATE games SET status = ?, winner_id = ?, finished_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getStatus().toString());
            if (game.getWinnerId() != null) {
                stmt.setInt(2, game.getWinnerId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setTimestamp(3, game.getFinishedAt());
            stmt.setInt(4, game.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM games WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
