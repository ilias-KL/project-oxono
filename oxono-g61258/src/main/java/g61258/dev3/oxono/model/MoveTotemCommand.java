package g61258.dev3.oxono.model;

/**
 * Command implementation that encapsulates the action of moving a totem on the board.
 * This command can be executed and undone to manage the state of the game.
 */
public class MoveTotemCommand implements Command {
    private Board board;

    private Game game;
    private Totem totem;
    private Totem previousTotem;
    private int fromX, fromY, toX, toY;




    /**
     * Constructs a new MoveTotemCommand.
     * @param board the board where the totem will be moved
     * @param totem the totem to move
     * @param fromX the original X-coordinate of the totem
     * @param fromY the original Y-coordinate of the totem
     * @param toX the new X-coordinate of the totem
     * @param toY the new Y-coordinate of the totem
     */
    public MoveTotemCommand(Game game, Board board, Totem totem, int fromX, int fromY, int toX, int toY, Totem previousTotem) {
        this.game = game;
        this.board = board;
        this.totem = totem;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.previousTotem = previousTotem;
    }

    /**
     * Executes the command to move the totem to the specified new position on the board.
     * Updates the game state to reflect the last moved totem, allowing the next action to be placing a token.
     */
    @Override
    public void execute() {
        board.setLastMovedTotem(totem);
        board.moveTotem(toX, toY, totem);
    }

    /**
     * Reverts the command by moving the totem back to its original position on the board.
     * Restores the game state to allow a different move if needed.
     */
    @Override
    public void unexecute() {
        board.setLastMovedTotem(previousTotem);
        board.moveTotem(fromX, fromY, totem);
    }

}
