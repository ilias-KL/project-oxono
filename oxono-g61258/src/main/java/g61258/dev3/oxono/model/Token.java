package g61258.dev3.oxono.model;

/**
 * Represents a token in the game, consisting of a color and a shape.
 * Tokens are used by players to mark positions on the game board.
 */
public class Token {
    private Color color;
    private Shape shape;


    /**
     * Constructs a Token with the specified color and shape.
     * @param color the color of the token
     * @param shape the shape of the token
     */
    public Token(Color color, Shape shape) {
        this.color = color;
        this.shape = shape;
    }

    /**
     * Gets the color of the token.
     * @return the color of the token
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the shape of the token.
     * @return the shape of the token
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Returns a string representation of the token,
     * showing its color and shape.
     * @return a string in the format "color shape"
     */
    @Override
    public String toString() {
        return color + " " + shape;
    }
}