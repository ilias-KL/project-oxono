package g61258.dev3.oxono.model;

import java.awt.*;
import java.util.Objects;

/**
 * Represents a Totem in the game, which is a special type of token with a position on the board.
 * A Totem has both a color and shape (which are inherited from the Token class) and a position
 * (x, y) on the board. It is used to track the movement and placement of special pieces during the game.
 */
public class Totem extends Token {
    private Shape shape;
    private int x;
    private int y;

    /**
     * Constructs a Totem with the specified shape and initial position.
     * @param shape The shape of the totem (CROSS or ROUND).
     * @param x The initial x-coordinate of the totem's position.
     * @param y The initial y-coordinate of the totem's position.
     */
    public Totem(Color color, Shape shape, int x, int y) {
        super(color, shape);
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the current x-coordinate of the totem's position.
     * @return The x-coordinate of the totem's position.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the current y-coordinate of the totem's position.
     * @return The y-coordinate of the totem's position.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x-coordinate of the totem's position.
     * <p>Use this method to update the x-coordinate when the totem is moved to a new position on the board.</p>
     * @param x the new x-coordinate of the totem's position
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-coordinate of the totem's position.
     * <p>Use this method to update the y-coordinate when the totem is moved to a new position on the board.</p>
     * @param y the new y-coordinate of the totem's position
     */
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Totem totem = (Totem) o;
        return shape == totem.shape;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape);
    }
}