package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import datamodel.GameData;
import datamodel.*;

import java.util.*;

public class GameService {
    private final DataAccess dataAccess;
    private List<Integer> gameIDs = new ArrayList<>();

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Integer createGame(String authToken, GameData gameData) throws Exception {
        if (dataAccess.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }
        if (gameData.gameName() == null) {
            throw new Exception("bad request");
        }

        GameData tempGame = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), new ChessGame());
        return dataAccess.createGame(tempGame);
//        int num = 1;
//        while (true) {
//            if (!gameIDs.contains(num)) {
//                gameIDs.add(num);
//                GameData game = new GameData(num, null, null, gameData.gameName(), null);
//                dataAccess.createGame(game);
//                break;
//            }
//            num++;
//        }
    }

    public void joinGame(String authToken, JoinGameData joinData) throws Exception {
        if (dataAccess.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }
        if (joinData.gameID() == null || joinData.playerColor() == null) {
            throw new Exception("bad request");
        }

        AuthData authData = dataAccess.getAuth(authToken);
        GameData gameData = dataAccess.getGame(joinData.gameID());
        if (gameData == null) {
            throw new Exception("bad request");
        }
        if (joinData.playerColor().equalsIgnoreCase("white")) {
            if (gameData.whiteUsername() != null) {
                throw new Exception("already taken");
            }
            dataAccess.updateGame(joinData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName());
        } else if (joinData.playerColor().equalsIgnoreCase("black")) {
            if (gameData.blackUsername() != null) {
                throw new Exception("already taken");
            }
            dataAccess.updateGame(joinData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName());
        } else {
            throw new Exception("bad request");
        }
    }

    public List<GameData> listGames(String authToken) throws Exception {
        if (dataAccess.getAuth(authToken) == null) {
            throw new Exception("unauthorized");
        }
        return dataAccess.getGameList();
    }
}
