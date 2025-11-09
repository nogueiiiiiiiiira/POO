package com.jokempo.model;

import java.sql.Timestamp;

public class Move {
    public enum MoveType { ROCK, PAPER, SCISSORS }

    private int id;
    private int gameId;
    private int playerId;
    private MoveType move;
    private int moveOrder; // 1 for first move, 2 for second
    private Timestamp createdAt;

    public Move() {}

    public Move(int gameId, int playerId, MoveType move, int moveOrder) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.move = move;
        this.moveOrder = moveOrder;
    }

    public Move(int id, int gameId, int playerId, MoveType move, int moveOrder, Timestamp createdAt) {
        this.id = id;
        this.gameId = gameId;
        this.playerId = playerId;
        this.move = move;
        this.moveOrder = moveOrder;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public MoveType getMove() { return move; }
    public void setMove(MoveType move) { this.move = move; }

    public int getMoveOrder() { return moveOrder; }
    public void setMoveOrder(int moveOrder) { this.moveOrder = moveOrder; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Move{id=" + id + ", gameId=" + gameId + ", playerId=" + playerId +
               ", move=" + move + ", moveOrder=" + moveOrder + "}";
    }

    // Utility method to determine winner
    public static MoveType getWinningMove(MoveType move1, MoveType move2) {
        if (move1 == move2) return null; // Draw

        switch (move1) {
            case ROCK:
                return (move2 == MoveType.SCISSORS) ? move1 : move2;
            case PAPER:
                return (move2 == MoveType.ROCK) ? move1 : move2;
            case SCISSORS:
                return (move2 == MoveType.PAPER) ? move1 : move2;
        }
        return null;
    }
}
