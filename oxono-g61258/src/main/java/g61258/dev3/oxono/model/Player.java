package g61258.dev3.oxono.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game, including their color and a collection of tokens.
 * Each player starts with 8 tokens: 4 CROSS tokens and 4 CIRCLE tokens.
 */
public class Player {
    private Color color;
    private List<Token> tokens;
    public final int NB_TOKENS = 8;

    /**
     * Constructs a Player with the specified color and initializes their tokens.
     * Each player receives 4 CROSS tokens and 4 CIRCLE tokens of the specified color.
     * @param color the color of the player's tokens
     */
    public Player(Color color) {
        this.color = color;
        this.tokens = new ArrayList<>();
        initializeTokens();
    }

    /**
     * Initializes the player's tokens. The player is given 8 tokens: 4 of each shape
     * (CROSS and CIRCLE) in the specified color.
     */
    private void initializeTokens() {
        for (int i = 0; i < NB_TOKENS; i++) {
            tokens.add(new Token(color, Shape.CROSS));
            tokens.add(new Token(color, Shape.CIRCLE));
        }
    }

    /**
     * Gets the color of the player.
     * @return the color of the player
     */
    public Color getColor() {
        return color;
    }

    /**
     * Retrieves and removes a token of the specified shape from the player's collection.
     * If no token of the specified shape is available, it attempts to provide a token
     * of the alternate shape instead. If no tokens are available at all, it returns null.
     * @param shape the shape of the token to retrieve (CROSS or CIRCLE)
     * @return a token of the specified shape, an alternate shape if the requested shape
     *         is unavailable, or {@code null} if no tokens are available
     */
    public Token getToken(Shape shape) {
        for (Token token : tokens) {
            if (token.getShape().equals(shape)) {
                return token;
            }
        }

        return null;
    }

    /**
     * Removes a token of the specified shape from the player's collection.
     * If the player has a token of the requested shape, it is removed from their tokens.
     * @param shape the shape of the token to remove (CROSS or CIRCLE)
     */
    public void removeToken(Shape shape) {
        Token delete = getToken(shape);
        if (delete != null) {
            tokens.remove(delete);
        }

    }

    /**
     * Gets the number of tokens of the specified shape that the player currently has.
     * @param shape the shape of the tokens to count (CROSS or CIRCLE)
     * @return the count of tokens with the specified shape
     */
    public int getNbTokens(Shape shape) {
        int count = 0;
        for (Token token : tokens) {
            if (token.getShape().equals(shape)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the list of tokens associated with the player.
     * @return a list of tokens owned by the player
     */
    public List<Token> getTokens() {
        return tokens;
    }

    /**
     * Returns a string representation of the player, including the color of the player.
     * @return a string indicating the player's color (either "Black" or "Rose")
     */
    @Override
    public String toString() {
        String playerColor = color.equals(Color.BLACK) ? "Black" : "Rose";
        return "Le joueur " + playerColor;
    }

    /**
     * Adds a token to the player's collection.
     * @param token the token to add to the player's collection
     */
    public void addToken(Token token) {
        tokens.add(token);
    }

    /**
     * Checks if the player still has tokens of the specified shape.
     * @param shape the shape of the token to check
     * @return true if the current player still has tokens of the specified shape, false otherwise
     */
    public boolean hasTokenShape(Shape shape) {
        for (Token token : tokens) {
            if (token.getShape().equals(shape)) {
                return true;
            }
        }
        return false;
    }

}