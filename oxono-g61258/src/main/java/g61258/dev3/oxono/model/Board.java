package g61258.dev3.oxono.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Board class represents the game board for the Oxono game.
 * It contains the game grid, the totems, and methods to manage token placements.
 */
public class Board {
    private final Token[][] grid;
    private Totem totemX;
    private Totem totemO;

    private Totem lastMovedTotem;

    /**
     * Constructor for the Board class.
     * Initializes an empty 6x6 game grid.
     */
    public Board(int size) {
        grid = new Token[size][size];
        initialize();
    }

    /**
     * Initializes the game board.
     * Clears the grid and randomly places the totems at positions (2,2) and (3,3).
     */
    private void initialize() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = null;
            }
        }
        Random random = new Random();

        int half = grid.length/2;
        if (random.nextBoolean()) {
            totemX = new Totem(Color.BLUE, Shape.CROSS, half-1, half-1);
            totemO = new Totem(Color.BLUE, Shape.CIRCLE, half, half);
        } else {
            totemX = new Totem(Color.BLUE, Shape.CROSS, half, half);
            totemO = new Totem(Color.BLUE, Shape.CIRCLE, half-1, half-1);
        }

        grid[totemX.getX()][totemX.getY()] = totemX;
        grid[totemO.getX()][totemO.getY()] = totemO;
    }

    /**
     * Places a token on a specific cell in the grid if it is empty.
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @param token The token to place on the cell.
     */
    public void placeToken(int x, int y, Token token) {
        if (isCellEmpty(x, y)) {
            grid[x][y] = token;
        }
    }

      /**
     * Moves a totem to a new position on the board.
     * The method first clears the current position of the totem, updates its coordinates,
     * and then places it in the new position on the board.
     * @param newX the new x-coordinate where the totem should be placed
     * @param newY the new y-coordinate where the totem should be placed
     * @param totem the totem to be moved
     */
    public void moveTotem(int newX, int newY, Totem totem) {
        if (!isCellEmpty(newX, newY)) {
            return; // Ne pas déplacer si la cellule est occupée
        }
        int x = totem.getX();
        int y = totem.getY();
        grid[x][y] = null;

        totem.setX(newX);
        totem.setY(newY);

        placeToken(newX, newY, totem);
    }


    /**
     * Removes a token from a specific cell on the board.
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     */
    public void removeToken(int x, int y) {
        if (isValidPosition(x, y)) {
            grid[x][y] = null;
        }
    }

    /**
     * Checks if the specified coordinates are within the bounds of the board.
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return true if the position is valid, otherwise false.
     */
    public boolean isValidPosition(int x, int y) {
        // Vérifie si les coordonnées sont dans les limites du plateau
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    /**
     * Checks if a specific cell in the grid is empty.
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @return true if the cell is empty, otherwise false.
     */
    public boolean isCellEmpty(int x, int y) {
        return grid[x][y] == null;
    }

    /**
     * Retrieves a list of all empty cells on the board.
     * @return A list of int arrays, where each array represents the coordinates of an empty cell.
     */
    public List<int[]> getAllEmptyCells() {
        List<int[]> emptyCells = new ArrayList<>();

        // Parcourir toutes les cases du plateau
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                // Vérifier si la case est vide
                if (grid[x][y] == null) {
                    emptyCells.add(new int[] { x, y });
                }
            }
        }
        return emptyCells;
    }


    // GETTERS

    /**
     * Retrieves the token located at the specified coordinates.
     * @param x The x-coordinate of the cell.
     * @param y The y-coordinate of the cell.
     * @return The token at the specified position, or null if no token is present.
     */
    public Token getToken(int x, int y) {
        return grid[x][y];
    }

    /**
     * Returns the size of the game board.
     * @return The size of the board (6).
     */
    public int getSize() {
        return grid.length;
    }

    /**
     * Returns the totem with the cross (X) symbol.
     * @return The X totem.
     */
    public Totem getTotemX() {
        return totemX;
    }

    /**
     * Returns the totem with the round (O) symbol.
     * @return The O totem.
     */
    public Totem getTotemO() {
        return totemO;
    }

    /**
     * Return the last totem that was moved on the board.
     * @return The last moved totem, or  null if no totem has been moved yet.
     */
    public Totem getLastMovedTotem() {
        return lastMovedTotem;
    }

    /**
     * Sets the last moved totem on the board.
     * This method should be called whenever a totem is moved to keep track of the last moved totem.
     * @param lastMovedTotem The totem that was last moved.
     */
    public void setLastMovedTotem(Totem lastMovedTotem) {
        this.lastMovedTotem = lastMovedTotem;
    }

}

