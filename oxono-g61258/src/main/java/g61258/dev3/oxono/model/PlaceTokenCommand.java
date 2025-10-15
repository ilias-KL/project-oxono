package g61258.dev3.oxono.model;

/**
 * Command implementation that encapsulates the action of placing a token on the board.
 * This command can be executed and undone to manage the state of the game.
 */
public class PlaceTokenCommand implements Command {
    private Player currentPlayer;

    private Board board;

    private Game game;
    private int x, y;

    private Token token;

    /**
     * Constructs a new PlaceTokenCommand.
     * @param currentPlayer the current player of the game
     * @param x the X-coordinate where the token will be placed
     * @param y the Y-coordinate where the token will be placed
    // * @param totem the totem associated with the current player's token to place
     */
    public PlaceTokenCommand(Game game, Player currentPlayer,Board board, int x, int y, Token token) {
        this.game = game;
        this.currentPlayer = currentPlayer;
        this.board = board;
        this.x = x;
        this.y = y;
        this.token = token;
    }

    /**
     * Executes the command to place a token on the board.
     * The token is placed at the specified coordinates and the current player loses one token.
     * The game state is changed to allow the choice of further moves.
     */
    @Override
    public void execute() {
        board.placeToken(x, y, token);
        }

    /**
     * Reverts the action of placing a token on the board.
     * The token is removed from the specified coordinates and the game state is reset to allow token placement.
     */
    @Override
    public void unexecute() {
        Token token = board.getToken(x, y);
        currentPlayer.addToken(token);
        board.removeToken(x, y);
    }
}

