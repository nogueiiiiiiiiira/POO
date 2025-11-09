package com.jokempo.model;

import java.sql.Timestamp;

public class Game {
    public enum GameMode { PVC, PVP }
    public enum GameStatus { ONGOING, FINISHED }

    private int id;
    private int player1Id;
    private Integer player2Id; // Nullable for PVC
    private GameMode gameMode;
    private GameStatus status;
    private Integer winnerId; // Nullable if draw or ongoing
    private Timestamp createdAt;
    private Timestamp finishedAt;

    public Game() {}

    public Game(int player1Id, GameMode gameMode) {
        this.player1Id = player1Id;
        this.gameMode = gameMode;
        this.status = GameStatus.ONGOING;
    }

    public Game(int id, int player1Id, Integer player2Id, GameMode gameMode, GameStatus status,
                Integer winnerId, Timestamp createdAt, Timestamp finishedAt) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.gameMode = gameMode;
        this.status = status;
        this.winnerId = winnerId;
        this.createdAt = createdAt;
        this.finishedAt = finishedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(int player1Id) { this.player1Id = player1Id; }

    public Integer getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(Integer player2Id) { this.player2Id = player2Id; }

    public GameMode getGameMode() { return gameMode; }
    public void setGameMode(GameMode gameMode) { this.gameMode = gameMode; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public Integer getWinnerId() { return winnerId; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Timestamp finishedAt) { this.finishedAt = finishedAt; }

    @Override
    public String toString() {
        return "Game{id=" + id + ", player1Id=" + player1Id + ", player2Id=" + player2Id +
               ", gameMode=" + gameMode + ", status=" + status + ", winnerId=" + winnerId + "}";
    }
}
