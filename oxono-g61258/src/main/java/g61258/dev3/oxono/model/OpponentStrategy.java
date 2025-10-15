package g61258.dev3.oxono.model;

public interface OpponentStrategy {
    /**
     * Chooses and executes a move for the opponent.
     * @param game The current game instance.
     */
    void play(Game game, Board board);
}