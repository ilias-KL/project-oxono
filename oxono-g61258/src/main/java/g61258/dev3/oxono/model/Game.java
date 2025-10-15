package g61258.dev3.oxono.model;

import g61258.dev3.oxono.utils.Observable;
import g61258.dev3.oxono.utils.Observer;

import java.util.List;
import java.util.ArrayList;

public class Game implements Observable {
    private  Board board;
    private final Player playerRose;
    private final Player playerNoir;
    private Player currentPlayer;
    private boolean gameOver;
    private String winner;
    private CommandManager commandManager;
    private GameStat gameStat;
    private int[] lastPlacedTokenCoords;

    private OpponentStrategy opponentStrategy;

    private List<Observer> observers = new ArrayList<>();

    /**
     * Constructs a new Game instance, initializes the board and players, and sets the starting player.
     */

    public Game(int boardSize, int aiLevel) {
        this.board = new Board(boardSize);
        this.playerRose = new Player(Color.PINK);
        this.playerNoir = new Player(Color.BLACK);
        this.currentPlayer = playerRose;
        this.gameOver = false;
        this.winner = null;
        this.commandManager = new CommandManager();
        this.gameStat = GameStat.MOVE;
        if (aiLevel == 0) {
            this.opponentStrategy = new RandomOpponentStrategy();
        } else {
            this.opponentStrategy = new OpponentStrategyLevel2();
        }
    }

    // GETTERS

    /**
     * Return the board of the game, only used for test
     * @return the board of the game
     */
    protected Board getBoard(){
        return board;
    }

    /**
     * Return the name of the current player.
     * @return The current player's name as a string.
     */
    public String getCurrentPlayerName() {
        return currentPlayer.toString();
    }

    public String getOpponentPlayerName() {
        return (currentPlayer.equals(playerRose)) ? playerNoir.toString() : playerRose.toString();
    }

    /**
     * Return the count of tokens of a specific shape for the current player.
     * @param shape The shape of the tokens (e.g., circle or cross).
     * @return The number of tokens of the specified shape that the current player has.
     */
    public int getCurrentPlayerTokenCount(Shape shape) {
        return currentPlayer.getNbTokens(shape);
    }

    /**
     * Gets the color of the current player.
     * @return the Color representing the current player's color
     */
    public Color getCurrentPlayerColor() {
        return currentPlayer.getColor();
    }

    /**
     * Gets the number of tokens of the specified shape that the current player has.
     * @param shape the Shape of the tokens to count
     * @return the number of tokens of the specified shape the current player has
     */
    public int getCurrentPlayerNbTokens(Shape shape) {
        return currentPlayer.getNbTokens(shape);
    }


    public int getOpponentPlayerNbTokens(Shape shape) {
        return (currentPlayer.equals(playerRose)) ? playerNoir.getNbTokens(shape) : playerRose.getNbTokens(shape);

    }

    /**
     * Gets a token of the specified shape from the current player's collection.
     * @param shape the Shape of the token to retrieve
     * @return the Token of the specified shape belonging to the current player
     */
    public Token getCurrentPlayerToken(Shape shape) {
        return currentPlayer.getToken(shape);
    }


    /**
     * Return a defensive copy of the list of tokens held by the player of the specified color.
     * @param color the Color of the player whose tokens are being retrieved
     * @return a defensive copy of the list of tokens held by the specified player
     */
    public List<Token> getPlayerTokens(Color color) {
        if (color.equals(Color.PINK)) {
            return new ArrayList<>(playerRose.getTokens());
        }
        return new ArrayList<>(playerNoir.getTokens());
    }


    /**
     * Getter for the game over state.
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Getter for the winner of the game.
     * @return the name of the winner, or null if there is no winner.
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Getter for the current game state.
     * @return the GameStat object representing the current state of the game.
     */
    public GameStat getGameStat() {
        return gameStat;
    }

    /**
     * Getter for the last moved totem.
     * @return the Totem object representing the last moved totem, or null if no totem has been moved.
     */
    public Totem getLastMovedTotem() {
        return board.getLastMovedTotem();
    }

    /**
     * Getter for the coordinates of the last placed token.
     * @return an integer array representing the coordinates [x, y] of the last placed token.
     */
    public int[] getLastPlacedTokenCoords() {
        return lastPlacedTokenCoords;
    }

    /**
     * Getter for the number of empty cells on the board.
     * @return the number of currently empty cells on the board.
     */
    public int getEmptyCellsCount() {
        return this.board.getAllEmptyCells().size();
    }

    /**
     * Return the token located at the specified coordinates.
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @return The token at the specified position, or null if no token is present.
     */
    public Token getToken(int x, int y) {
        return board.getToken(x,y);
    }


    // SETTERS

    /**
     * Sets the game-over status.
     * @param gameOver true to mark the game as over, false to continue playing
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        notifyObservers();
    }

    /**
     * Sets the game state.
     * @param gameStat the new state of the game
     */
    public void setGameStat(GameStat gameStat) {
        this.gameStat = gameStat;
        notifyObservers();
    }

    /**
     * Sets the last moved totem.
     * @param lastMovedTotem the last moved totem
     */
    public void setLastMovedTotem(Totem lastMovedTotem) {
        board.setLastMovedTotem(lastMovedTotem);
        notifyObservers();
    }

    /**
     * Sets the last placed token coordinates.
     * @param x the X-coordinate of the last placed token
     * @param y the Y-coordinate of the last placed token
     */
    private void setLastPlacedTokenCoords(int x, int y) {
        this.lastPlacedTokenCoords = new int[]{x, y};
        notifyObservers();
    }

    /**
     * Sets the winner of the game.
     */
    public void setWinner() {
        this.winner = currentPlayer.toString();
        notifyObservers();
    }


    /**
     * Executes the opponent's move if it's their turn.
     */
    public void playOpponentTurn() {
        if (this.currentPlayer.getColor() == Color.BLACK) {
            if (opponentStrategy != null) {
                opponentStrategy.play(this,this.board);
            }
        }
        notifyObservers();
    }


    /**
     * Starts a new game by resetting the board and setting the current player.
     */
    public void start() {
        gameOver = false;
        winner = null;
        currentPlayer = playerRose;
        notifyObservers();
    }

    /**
     * Ends the game immediately by setting the game state to over.
     */
    public void abandonGame() {
        gameOver = true;
        notifyObservers();
    }


    /**
     * Chooses a totem based on the given shape.
     * @param shape the shape of the totem to be chosen
     * @return the totem with the specified shape
     */
    public Totem choseTotem(Shape shape) {
        Totem totem = shape.equals(Shape.CIRCLE) ? this.board.getTotemO() : this.board.getTotemX();
        int x = totem.getX();
        int y = totem.getY();
        Shape shape1 = totem.getShape();
        Totem copy = new Totem(Color.BLUE,shape1,x,y);
       // return shape.equals(Shape.CIRCLE) ? this.board.getTotemO() : this.board.getTotemX();
        return copy;
    }

    /**
     * Places a token on the board at the specified coordinates.
     * This method executes the command to place the token and notifies observers of the change.
     * @param x the x-coordinate where the token should be placed
     * @param y the y-coordinate where the token should be placed
     * @param totem the totem associated with the token to be placed
     */
    public void placeToken(int x, int y, Totem totem) {
        Token token = getCurrentPlayerToken(totem.getShape());
        Command placeAnywhereCommand = new PlaceTokenCommand(this,currentPlayer,this.board,x,y,token);
        commandManager.executeCommand(placeAnywhereCommand);
        removeTokenFromCurrentPlayer(totem.getShape());
        setGameStat(GameStat.MOVE);
        setLastPlacedTokenCoords(x,y);

        notifyObservers();
    }

    public void placeTokenAnywhere(int x, int y,Token token){
        board.placeToken(x,y,token);
    }

    /**
     * Checks if a token can be placed on any given cell, based on the current player
     * and the last moved totem.
     * @param x the X-coordinate of the cell to place the token
     * @param y the Y-coordinate of the cell to place the token
     * @param lastMovedTotem the last totem that was moved
     * @return true if the token can be placed, false otherwise
     */
    public boolean canPlaceTokenAnywhere(int x, int y, Totem lastMovedTotem) {
        Token token = currentPlayer.getToken(lastMovedTotem.getShape());
        return token != null && board.isCellEmpty(x, y);
    }

    /**
     * Checks if a token can be placed at the specified coordinates, under the condition
     * that the cell is adjacent to the last moved totem.
     * @param x the X-coordinate of the cell to place the token
     * @param y the Y-coordinate of the cell to place the token
     * @param lastMovedTotem the last totem that was moved
     * @return true if the token can be placed, false otherwise
     */
    public boolean canPlaceToken(int x, int y, Totem lastMovedTotem) {
        if (!isAdjacentToTotem(x, y, lastMovedTotem) || !board.isCellEmpty(x, y)) {
            return false;
        }
     return canPlaceTokenAnywhere(x,y,lastMovedTotem);
    }

    /**
     * Moves a totem to a new position on the board.
     * Executes the move command, updates the game state, and notifies observers.
     * @param newX the new x-coordinate for the totem
     * @param newY the new y-coordinate for the totem
     * @param totem the totem to be moved
     */
    public void moveTotem(int newX, int newY, Totem totem) {
        Command moveAnywhere = new MoveTotemCommand(this,board,totem,totem.getX(),totem.getY(),newX,newY, /*lastMovedTotem*/ getLastMovedTotem());
        commandManager.executeCommand(moveAnywhere);
        setGameStat(GameStat.INSERT);
        setLastMovedTotem(totem);
        notifyObservers();
    }


    /**
     * Determines if it is possible to move a totem to a new position.
     * This method checks the game rules to validate if the move can be made, including checking for adjacent cells,
     * vertical and horizontal clearances, and row/column occupation.
     * @param newX the new x-coordinate for the totem
     * @param newY the new y-coordinate for the totem
     * @param totem the totem to be moved
     * @return true if the move is valid, false otherwise
     */
    public boolean isMoveTotemPossible(int newX, int newY, Totem totem) {
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Vérifier si la nouvelle position est valide (dans les limites du plateau)
        if (!isValidPosition(newX, newY) || !this.board.isCellEmpty(newX, newY)) {
            return false; // Hors des limites
        }

        if (newX == currentX && newY == currentY) {
            return false;
        }

        // Vérifier si le mouvement est diagonal
        if (newX != currentX && newY != currentY) {
            if (!areRowsAndColumnsOccupied(totem)) {
                return false; // Déplacement diagonal non autorisé
            }
        }

        if(areRowsAndColumnsOccupied(totem)) {
            if (this.board.isCellEmpty(newX,newY)) {
                return true;
            }
        }
        // Vérifier les cases adjacentes libres
        List<int[]> freeAdjacentCells = getFreeAdjacentCells(currentX, currentY);

        // Si des cases adjacentes sont disponibles, autoriser le déplacement vers l'une d'elles
        if (!freeAdjacentCells.isEmpty()) {
            for (int[] p : freeAdjacentCells) {
                if (p[0] == newX && p[1] == newY) {
                    return true; // Déplacement vers une case libre adjacente
                }
            }

            if (currentX == newX) { // Déplacement vertical
                int minY = Math.min(currentY, newY);
                int maxY = Math.max(currentY, newY);

                for (int y = minY + 1; y < maxY; y++) {
                    if (!this.board.isCellEmpty(currentX, y)) {
                        return false; // Une case non vide (totem ou token) a été trouvée
                    }
                }
                return true; // Toutes les cases entre les deux positions sont vides
            } else if (currentY == newY) { // Déplacement horizontal
                int minX = Math.min(currentX, newX);
                int maxX = Math.max(currentX, newX);

                for (int x = minX + 1; x < maxX; x++) {
                    if (!this.board.isCellEmpty(x, currentY)) {
                        return false; // Une case non vide (totem ou token) a été trouvée
                    }
                }

                return true; // Toutes les cases entre les deux positions sont vides
            }
        }

        // Si aucune case adjacente n'est libre, vérifier les mouvements en ligne droite
        System.out.println("Aucune case libre adjacente. Vérification des conditions pour un mouvement valide.");

       return isNoAdjacentCellsMoveValid(currentX,currentY,newX,newY);
    }

    /**
     * Verifies whether the move of a totem is valid when no adjacent cells are available.
     * This method checks if the cells between the current and new positions are either empty or non-empty.
     * @param currentX the current x-coordinate of the totem
     * @param currentY the current y-coordinate of the totem
     * @param newX the new x-coordinate for the totem
     * @param newY the new y-coordinate for the totem
     * @return true if the move is valid, false otherwise
     */
    private boolean isNoAdjacentCellsMoveValid(int currentX, int currentY, int newX, int newY){
        int min, max;
        boolean isVertical;
        if (currentX == newX) { // Déplacement vertical
            min = Math.min(currentY, newY);
            max = Math.max(currentY, newY);
            isVertical = true;
        } else { // Déplacement horizontal
            min = Math.min(currentX, newX);
            max = Math.max(currentX, newX);
            isVertical = false;
        }

        boolean allEmpty = true;
        for (int i = min + 1; i < max; i++) {
            if (isVertical) {
                if (!this.board.isCellEmpty(currentX, i)) {
                    allEmpty = false;
                    break;
                }
            } else {
                if (!this.board.isCellEmpty(i, currentY)) {
                    allEmpty = false;
                    break;
                }
            }
        }
            if (allEmpty) {
                return true; // Autoriser si toutes les cases entre les deux positions sont vides
            }

        boolean allNonEmpty = true;
        for (int i = min + 1; i < max; i++) {
            if (isVertical) {
                if (this.board.isCellEmpty(currentX, i)) {
                    allNonEmpty = false;
                    break;
                }
            } else {
                if (this.board.isCellEmpty(i, currentY)) {
                    allNonEmpty = false;
                    break;
                }
            }
        }
        return allNonEmpty;
    }


    /**
     * Finds and returns a list of free cells adjacent to the specified coordinates.
     * @param x the x-coordinate to check around
     * @param y the y-coordinate to check around
     * @return a list of free adjacent cells
     */
    public List<int[]> getFreeAdjacentCells(int x, int y) {
        List<int[]> freeCells = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        // bas droite haut gauche

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isValidPosition(newX, newY) && this.board.isCellEmpty(newX, newY)) {
                freeCells.add(new int[]{newX, newY});
            }
        }

        return freeCells;
    }

    /**
     * Checks if the totem is enclosed, meaning it has no free adjacent cells.
     * @param totem the totem to check
     * @return true if the totem is enclosed (no free cells around it), false otherwise
     */
    public boolean isTotemEnclaved(Totem totem) {
        List<int[]> adjacentCells = getFreeAdjacentCells(totem.getX(), totem.getY());
        return adjacentCells.isEmpty();  // Si la liste est vide, le totem est enclavé
    }

    /**
     * Checks if the given coordinates are within the valid range of the board.
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the position is valid, false otherwise
     */
    private boolean isValidPosition(int x, int y) {
        return board.isValidPosition(x,y);
    }

    /**
     * Checks if all the cells in the row and column corresponding to the given totem are occupied.
     * The method verifies that there are no empty cells in both the row (y) and column (x) where the totem is placed.
     * @param totem the totem whose row and column are to be checked
     * @return true if all cells in the row and column are occupied, false otherwise
     */
    public boolean areRowsAndColumnsOccupied(Totem totem) {
        int x = totem.getX();
        int y = totem.getY();
        int boardSize = board.getSize();  // Size of the board

        // Check if all cells in the row (y) are occupied
        for (int i = 0; i < boardSize; i++) {
            if (board.isCellEmpty(i, y)) {
                return false;  // If any cell in the row is empty, return false
            }
        }

        // Check if all cells in the column (x) are occupied
        for (int j = 0; j < boardSize; j++) {
            if (board.isCellEmpty(x, j)) {
                return false;  // If any cell in the column is empty, return false
            }
        }

        // If all cells in the row and column are occupied, return true
        return true;
    }

    /**
     * Checks if the given coordinates are adjacent to the last moved totem.
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @param totem the last moved totem
     * @return true if the coordinates are adjacent to the totem, false otherwise
     */
    private boolean isAdjacentToTotem(int x, int y, Totem totem) {
        int totemX = totem.getX();
        int totemY = totem.getY();
        return (Math.abs(x - totemX) + Math.abs(y - totemY) == 1);
    }

    /**
     * Checks if a specific cell in the grid is empty.
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @return true if the cell is empty, otherwise false.
     */
    public boolean isCellEmpty(int x, int y) {
        return this.board.isCellEmpty(x,y);
    }

    /**
     * Switches the current player to the other player.
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == playerRose) ? playerNoir : playerRose;
        notifyObservers();
    }


    /**
     * Checks if the player has met the victory conditions at the specified coordinates.
     * @param x the x-coordinate of the last placed token
     * @param y the y-coordinate of the last placed token
     * @return true if victory conditions are met, false otherwise
     */
    public boolean checkVictory(int x, int y) {
        return checkVictory(x, y, true) || checkVictory(x, y, false);
    }

    /**
     * Checks for a sequence of matching tokens horizontally or vertically from the given coordinates.
     * @param x the x-coordinate of the token to check from
     * @param y the y-coordinate of the token to check from
     * @param isHorizontal if true, checks horizontally; otherwise, checks vertically
     * @return true if a matching sequence is found, false otherwise
     */
    private boolean checkVictory(int x, int y, boolean isHorizontal) {
        int colorCount = 0;
        int shapeCount = 0;

        Token colorReferenceToken = null;
        Token shapeReferenceToken = null;

        for (int i = 0; i < this.board.getSize(); i++) {
            Token currentToken = isHorizontal ? this.board.getToken(i, y) : this.board.getToken(x, i);

            // Ignore totems and null tokens
            if (currentToken == null || currentToken instanceof Totem) {
                colorCount = 0;
                shapeCount = 0;
                colorReferenceToken = null;
                shapeReferenceToken = null;
                continue;
            }

            // Vérification des couleurs
            if (colorReferenceToken == null || !colorReferenceToken.getColor().equals(currentToken.getColor())) {
                colorCount = 1; // Réinitialise le compteur de couleur
                colorReferenceToken = currentToken; // Met à jour le token de référence pour la couleur
            } else {
                colorCount++;
                if (colorCount == 4) return true;
            }

            // Vérification des formes
            if (shapeReferenceToken == null || !shapeReferenceToken.getShape().equals(currentToken.getShape())) {
                shapeCount = 1; // Réinitialise le compteur de forme
                shapeReferenceToken = currentToken; // Met à jour le token de référence pour la forme
            } else {
                shapeCount++;
                if (shapeCount == 4) return true;
            }
        }

        return false; // Aucun alignement trouvé
    }

    /**
     * Checks if the current player or the opponent still has tokens to play.
     * @return  true if the current player has tokens remaining to play, false otherwise
     */
    public boolean stillHasTokens() {
        // Identify the opponent based on the current player's color
        Player other = currentPlayer.getColor().equals(Color.PINK)
                ? playerNoir
                : playerRose;

        // Retrieve the number of tokens each player has
        int nbOtherCrossTokens = other.getNbTokens(Shape.CROSS);
        int nbOtherCircleTokens = other.getNbTokens(Shape.CIRCLE);
        int nbCurrentCrossTokens = currentPlayer.getNbTokens(Shape.CROSS);
        int nbCurrentCircleTokens = currentPlayer.getNbTokens(Shape.CIRCLE);

        // If the current player has no tokens remaining
        if (nbCurrentCrossTokens == 0 && nbCurrentCircleTokens == 0) {
            // Check if the opponent also has no tokens left
            if (nbOtherCrossTokens == 0 && nbOtherCircleTokens == 0) {
                // Neither player has tokens, abandon the game
                abandonGame();
                // Return false because the current player has no tokens left
                return false;
            }

        }

        // The current player still has tokens remaining
        return true;
    }


    /**
     * Removes a token of the specified shape from the current player.
     * @param shape the Shape of the token to be removed from the current player
     */
    public void removeTokenFromCurrentPlayer(Shape shape) {
        currentPlayer.removeToken(shape);
    }


    /**
     * Checks if the player still has tokens of the specified shape.
     * @param shape the shape of the token to check
     * @return true if the current player still has tokens of the specified shape, false otherwise
     */
    public boolean hasTokenShape(Shape shape) {
        return currentPlayer.hasTokenShape(shape);
    }

    /**
     * Undoes the last executed command, reverting the game state to its previous configuration.
     * This method invokes the undo functionality of the command manager and notifies observers of the change.
     */
    public void undo(){
        if (commandManager.canUndo()) {
            commandManager.undo();
            if (gameStat.equals(GameStat.INSERT)) {
                this.setGameStat(GameStat.MOVE);
            } else {
                this.setGameStat(GameStat.INSERT);
            }
            notifyObservers();
        }
    }

    /**
     * Redoes the last undone command, reapplying the previous change to the game state.
     * This method invokes the redo functionality of the command manager and notifies observers of the change.
     */
    public void redo(){
        if (commandManager.canRedo()){
            commandManager.redo();
            if(gameStat.equals(GameStat.MOVE)){
                this.setGameStat(GameStat.INSERT);
            } else {
               removeTokenFromCurrentPlayer(getLastMovedTotem().getShape());
                setGameStat(GameStat.MOVE);
                setLastPlacedTokenCoords(getLastMovedTotem().getX(),getLastMovedTotem().getY());

            }
            notifyObservers();
        }

    }

    /**
     * Adds an observer to the list of observers that will be notified of changes.
     * This method is part of the Observer design pattern.
     * @param o the observer to be added
     */
    @Override
    public void addObserver(Observer o){
        this.observers.add(o);
    }

    /**
     * Removes an observer from the list of observers.
     * This method is part of the Observer design pattern.
     * @param o the observer to be removed
     */
    @Override
    public void removeObserver(Observer o){
        this.observers.remove(o);
    }

    /**
     * Notifies all observers of a change in the game state.
     * This method is part of the Observer design pattern and calls the update method on each observer.
     */
    @Override
    public void notifyObservers() {
        for (Observer o: this.observers) {
            o.update();
        }
    }
}
