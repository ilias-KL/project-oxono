package g61258.dev3.oxono.model;

/**
 * Enum representing the two possible shapes of tokens in the game: CROSS and CIRCLE.
 * The shapes are used to distinguish between different types of tokens placed on the board.
 */
public enum Shape {
    CROSS, CIRCLE;

    @Override
    public String toString() {
        switch (this) {
            case CROSS:
                return "X";
            case CIRCLE:
                return "O";
            default:
                return super.toString();
        }
    }
}
