package g61258.dev3.oxono.controller;

import g61258.dev3.oxono.model.*;
import g61258.dev3.oxono.utils.Observer;
import g61258.dev3.oxono.view.*;

import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GameController {

    private  Game game;
    private final GameStartView gameStartView;
    private Stage primaryStage; // Référence au Stage principal

    /**
     * Constructs a GameController.
     * @param gameStartView The initial view of the game.
     */
    public GameController(GameStartView gameStartView) {
        this.gameStartView = gameStartView;
    }

    /**
     * Handles mouse entry events on the game board.
     * Applies visual effects to indicate valid or invalid moves.
     * @param rowIndex The row index of the cell.
     * @param colIndex The column index of the cell.
     * @param selectedTotem The currently selected totem.
     * @param event The mouse event.
     * @return A ColorAdjust effect to apply to the cell.
     */
    public ColorAdjust OnMouseEntered(int rowIndex, int colIndex, Totem selectedTotem, MouseEvent event) {
        Totem lastMovedTotem = game.getLastMovedTotem();
        ColorAdjust colorAdjust = new ColorAdjust();
        if (game.getGameStat() == GameStat.MOVE && selectedTotem != null) {
            if (game.isMoveTotemPossible(rowIndex, colIndex, selectedTotem)) {
                colorAdjust.setHue(-0.5); // Teinte verte
                colorAdjust.setBrightness(-0.3); // Assombrir
                colorAdjust.setSaturation(0.8); // Augmenter la saturation pour un vert plus riche
            } else {
                colorAdjust.setHue(0.5);  // Teinte rouge
                colorAdjust.setBrightness(-0.3); // Assombrir
                colorAdjust.setSaturation(0.8); // Augmenter la saturation
            }
        } else if (game.getGameStat() == GameStat.INSERT) {
            boolean enclavedTotem = game.isTotemEnclaved(lastMovedTotem);
            if (enclavedTotem) {
                if (game.canPlaceTokenAnywhere(rowIndex, colIndex, lastMovedTotem)) {
                    colorAdjust.setHue(-0.5); // Teinte verte
                    colorAdjust.setBrightness(-0.3); // Assombrir
                    colorAdjust.setSaturation(0.8); // Augmenter la saturation
                } else {
                    colorAdjust.setHue(0.5);  // Teinte rouge
                    colorAdjust.setBrightness(-0.3); // Assombrir
                    colorAdjust.setSaturation(0.8); // Augmenter la saturation
                }
            } else {
                if (game.canPlaceToken(rowIndex, colIndex, lastMovedTotem)) {
                    colorAdjust.setHue(-0.5); // Teinte verte
                    colorAdjust.setBrightness(-0.3); // Assombrir
                    colorAdjust.setSaturation(0.8); // Augmenter la saturation
                } else {
                    colorAdjust.setHue(0.5);  // Teinte rouge
                    colorAdjust.setBrightness(-0.3); // Assombrir
                    colorAdjust.setSaturation(0.8); // Augmenter la saturation
                }
            }
        }
        return colorAdjust;
    }


    /**
     * Handles mouse clicks on empty cells of the game board.
     * Executes appropriate actions based on the current game state.
     * @param rowIndex The row index of the clicked cell.
     * @param colIndex The column index of the clicked cell.
     * @param selectedTotem The currently selected totem.
     * @param event The mouse click event.
     */
    public void EmptyCellOnMouseClicked(int rowIndex, int colIndex, Totem selectedTotem, MouseEvent event) {
        Totem lastMovedTotem = game.getLastMovedTotem();
        if (game.getGameStat() == GameStat.MOVE && selectedTotem != null && game.isMoveTotemPossible(rowIndex, colIndex, selectedTotem)) {
            game.moveTotem(rowIndex, colIndex, selectedTotem);
        } else if (game.getGameStat() == GameStat.INSERT) {
            boolean enclavedTotem = game.isTotemEnclaved(lastMovedTotem);
            if (enclavedTotem) {
                if (game.canPlaceTokenAnywhere(rowIndex, colIndex, lastMovedTotem)) {
                    game.placeToken(rowIndex, colIndex, lastMovedTotem);
                    handlePressAction();
                }
            } else {
                if (game.canPlaceToken(rowIndex, colIndex, lastMovedTotem)) {
                    game.placeToken(rowIndex, colIndex, lastMovedTotem);
                    handlePressAction();
                }
            }
        }
    }

    /**
     * Handles the pressing action, checking for victory conditions or switching turns.
     * @return True if the game is over, false otherwise.
     */
    public void handlePressAction() {
        int[] coords = game.getLastPlacedTokenCoords();
        if (coords != null) {
            game.setGameOver(game.checkVictory(coords[0], coords[1]));
            if (game.isGameOver()) {
                game.setWinner();
                handleEndGame();
                return;
            } else {
                game.switchPlayer();
                game.playOpponentTurn();

                if (game.stillHasTokens()) {
                    coords = game.getLastPlacedTokenCoords();
                    if (coords != null) {
                        game.setGameOver(game.checkVictory(coords[0], coords[1]));
                        if (game.isGameOver()) {
                            game.setWinner();
                            handleEndGame();
                            return;
                        } else {
                            // Après le tour de l'adversaire, on passe au joueur humain
                            game.switchPlayer();
                        }
                    }
                } else {
                    handleEndGame();
                    return;
                }

            }
        }
        game.setGameStat(GameStat.MOVE);
        gameStartView.getBoardView().update();
        gameStartView.getBoardView().clearSelectedTotem();
    }

    /**
     * Handles the end-of-game process.
     * This method performs all necessary actions to transition the game into its end state,
     * including displaying the winner, updating the game status, and disabling further actions.
     */
    private void handleEndGame() {
        String winnerName = getWinnerName(); // Obtenez le nom du gagnant
        game.setGameStat(GameStat.CHOICE);
        gameStartView.getGameInfoView().getEndGameView().display(winnerName); // Affichez la vue de fin de jeu avec le nom du gagnant
        gameStartView.getBoardView().grayOutBoard(); // Grise le plateau pour indiquer que le jeu est terminé
        gameStartView.getGameInfoView().disableActionButtons(); // Désactive les boutons d’action pour empêcher d’autres interactions
        gameStartView.getGameInfoView().update(); // Mettez à jour la vue pour refléter l'état final du jeu
    }


    /**
     * Adds an observer to the game model.
     * @param o The observer to add.
     */
    public void addObserver(Observer o) {
        game.addObserver(o);
    }

    /**
     * Handles the event of a game abandonment.
     */
    public void abandonGame() {
        game.setGameStat(GameStat.CHOICE);
        game.abandonGame();
    }

    /**
     * Executes the undo action for the last command.
     */
    public void undo() {
        game.undo();
    }

    /**
     * Executes the redo action for the last undone command.
     */
    public void redo() {
        game.redo();
    }

    /**
     * Checks if the current player still has tokens of the specified shape.
     * @param shape the shape of the token to check
     * @return true if the current player still has tokens of the specified shape, false otherwise
     */
    public boolean hasTokenShape(Shape shape) {
        return game.hasTokenShape(shape);
    }

    /**
     * Configures the game model with the selected board size and AI level.
     * @param boardSize the size of the game board
     * @param aiLevel the level of the AI opponent
     */
    public void configureGame(int boardSize, int aiLevel) {
        this.game = new Game(boardSize, aiLevel);
        game.start();
    }

    // GETTERS

    /**
     * Return the name of the current player.
     * @return The current player's name as a string.
     */
    public String getCurrentPlayerName() {
        return game.getCurrentPlayerName();
    }

    /**
     * Return the count of tokens of a specific shape for the current player.
     * @param shape The shape of the tokens (e.g., circle or cross).
     * @return The number of tokens of the specified shape that the current player has.
     */
    public int getCurrentPlayerTokenCount(Shape shape) {
        return game.getCurrentPlayerTokenCount(shape);
    }

    /**
     * Return the name of the opponent player.
     * @return The opponent player's name as a string.
     */
    public String getOpponentPlayerName() {
        return game.getOpponentPlayerName();
    }

    /**
     * Return the count of tokens of a specific shape for the opponent player.
     * @param shape The shape of the tokens (e.g., circle or cross).
     * @return The number of tokens of the specified shape that the opponent player has.
     */
    public int getOpponentPlayerTokenCount(Shape shape) {
        return game.getOpponentPlayerNbTokens(shape);
    }

    /**
     * Return the count of empty cells on the board.
     * @return The number of empty cells on the game board.
     */
    public int getEmptyCellCount() {
        return game.getEmptyCellsCount();
    }

    /**
     * Return the name of the winner, if the game has ended.
     * @return The winner's name as a string, or null if no winner has been determined yet.
     */
    public String getWinnerName() {
        if (game.getWinner() == null) {
            return null;
        }
        return game.getWinner();
    }

    /**
     * Return the token located at a given position on the board.
     * @param rowIndex The row index of the position.
     * @param colIndex The column index of the position.
     * @return The token at the specified position, or null if the cell is empty.
     */
    public Token getToken(int rowIndex, int colIndex) {
        return game.getToken(rowIndex, colIndex);
    }

    /**
     * Return the current game state.
     * @return the current  GameStat.
     */
    public GameStat getGameStat() {
        return game.getGameStat();
    }

    /**
     * Switches the turn to the next player.
     */
    public void switchPlayer() {
        game.switchPlayer();
    }


    // SETTERS

    /**
     * Sets the scene to be displayed in the main Stage.
     * @param scene The new scene to display.
     */
    public void setScene(Scene scene) {
        if (primaryStage != null) {
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            System.err.println("Erreur : Le Stage principal n'a pas été initialisé !");
        }
    }

    /**
     * Associates the main Stage with the controller.
     * This method should be called during application initialization.
     * @param primaryStage The main application Stage.
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}

