package com.jokempo.controller;

import com.google.gson.Gson;
import com.jokempo.dao.*;
import com.jokempo.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;

public class GameServlet extends HttpServlet {
    private Gson gson = new Gson();
    private PlayerDAO playerDAO = new PlayerDAO();
    private GameDAO gameDAO = new GameDAO();
    private MoveDAO moveDAO = new MoveDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (path == null || path.equals("/")) {
                // Return game modes
                Map<String, Object> response = new HashMap<>();
                response.put("modes", Arrays.asList("PVC", "PVP"));
                resp.getWriter().write(gson.toJson(response));
            } else if (path.equals("/history")) {
                // Return game history
                List<Game> games = gameDAO.findByPlayerId(1); // TODO: Get from session
                resp.getWriter().write(gson.toJson(games));
            }
        } catch (Exception e) {
            resp.setStatus(500);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (path.equals("/start")) {
                handleStartGame(req, resp);
            } else if (path.equals("/move")) {
                handleMakeMove(req, resp);
            }
        } catch (Exception e) {
            resp.setStatus(500);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(error));
        }
    }

    private void handleStartGame(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Map<String, String> body = gson.fromJson(req.getReader(), Map.class);
        String playerName = body.get("playerName");
        String mode = body.get("mode");

        // Create or find player
        Player player = new Player(playerName);
        playerDAO.create(player);

        // Create game
        Game.GameMode gameMode = Game.GameMode.valueOf(mode.toUpperCase());
        Game game = new Game(player.getId(), gameMode);
        gameDAO.create(game);

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", game.getId());
        response.put("playerId", player.getId());
        resp.getWriter().write(gson.toJson(response));
    }

    private void handleMakeMove(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Map<String, String> body = gson.fromJson(req.getReader(), Map.class);
        int gameId = Integer.parseInt(body.get("gameId"));
        int playerId = Integer.parseInt(body.get("playerId"));
        String moveStr = body.get("move");

        Game game = gameDAO.findById(gameId);
        if (game == null) {
            resp.setStatus(404);
            return;
        }

        Move.MoveType moveType = Move.MoveType.valueOf(moveStr.toUpperCase());
        List<Move> existingMoves = moveDAO.findByGameId(gameId);
        int moveOrder = existingMoves.size() + 1;

        Move move = new Move(gameId, playerId, moveType, moveOrder);
        moveDAO.create(move);

        Map<String, Object> response = new HashMap<>();
        response.put("moveId", move.getId());

        // Check if game is complete
        if (game.getGameMode() == Game.GameMode.PVC && moveOrder == 1) {
            // Computer move
            Move.MoveType computerMove = getRandomMove();
            Move computerMoveObj = new Move(gameId, 0, computerMove, 2); // 0 for computer
            moveDAO.create(computerMoveObj);

            // Determine winner
            Move.MoveType winnerMove = Move.getWinningMove(moveType, computerMove);
            Integer winnerId = null;
            if (winnerMove == moveType) {
                winnerId = playerId;
            } else if (winnerMove == computerMove) {
                winnerId = 0; // Computer
            }

            game.setStatus(Game.GameStatus.FINISHED);
            game.setWinnerId(winnerId);
            game.setFinishedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            gameDAO.update(game);

            response.put("computerMove", computerMove.toString());
            response.put("result", winnerId == null ? "DRAW" : (winnerId == playerId ? "WIN" : "LOSE"));
        } else if (game.getGameMode() == Game.GameMode.PVP && moveOrder == 2) {
            // PVP: both moves made, determine winner
            Move player1Move = existingMoves.get(0);
            Move player2Move = move;

            Move.MoveType winnerMove = Move.getWinningMove(player1Move.getMove(), player2Move.getMove());
            Integer winnerId = null;
            if (winnerMove == player1Move.getMove()) {
                winnerId = player1Move.getPlayerId();
            } else if (winnerMove == player2Move.getMove()) {
                winnerId = player2Move.getPlayerId();
            }

            game.setStatus(Game.GameStatus.FINISHED);
            game.setWinnerId(winnerId);
            game.setFinishedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            gameDAO.update(game);

            response.put("result", winnerId == null ? "DRAW" : (winnerId == playerId ? "WIN" : "LOSE"));
        }

        resp.getWriter().write(gson.toJson(response));
    }

    private Move.MoveType getRandomMove() {
        Move.MoveType[] moves = Move.MoveType.values();
        return moves[new Random().nextInt(moves.length)];
    }
}
