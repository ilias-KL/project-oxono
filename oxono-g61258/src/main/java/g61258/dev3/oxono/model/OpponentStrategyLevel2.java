package g61258.dev3.oxono.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a level 2 strategy for an automatic opponent in the Oxono game.
 * This strategy attempts to block the opponent or create a winning alignment
 * based on the current state of the board.
 */
public class OpponentStrategyLevel2 implements OpponentStrategy {

    /**
     * Executes the strategy for the automatic opponent's move.
     *
     * @param game  The current game instance.
     * @param board The game board.
     */
    @Override
    public void play(Game game, Board board) {

        // Choose the most suitable totem for the move
        Totem chosenTotem = chooseTotem(game, board);

        // Find all potential alignments on the board
        List<int[]> alignmentPositions = findAlignments(board);

        // Attempt a strategic move if alignments exist
        if (!alignmentPositions.isEmpty() && attemptStrategicMove(game, board, alignmentPositions, chosenTotem)) {
            return; // Successfully executed a strategic move
        }

        // Fallback to a random move if no strategic move is possible
        new RandomOpponentStrategy().play(game, board);
    }

    /**
     * Chooses the totem (either X or O) for the current move.
     *
     * @param game  The current game instance.
     * @param board The game board.
     * @return The selected totem, or null if none is available.
     */
    private Totem chooseTotem(Game game, Board board) {
        int nbCurrentCrossTokens = game.getCurrentPlayerTokenCount(Shape.CROSS);
        int nbCurrentCircleTokens = game.getCurrentPlayerTokenCount(Shape.CIRCLE);


        boolean canChooseTotemX = nbCurrentCrossTokens > 0;
        boolean canChooseTotemO = nbCurrentCircleTokens > 0;

        Totem totemX = board.getTotemX();
        Totem totemO = board.getTotemO();

        if (canChooseTotemX && !canChooseTotemO) {
            return totemX;
        } else if (!canChooseTotemX && canChooseTotemO) {
            return totemO;
        }
        return null; // Default to no specific totem
    }

    /**
     * Attempts to make a strategic move by moving a totem and placing a token.
     *
     * @param game              The current game instance.
     * @param board             The game board.
     * @param alignmentPositions A list of positions representing alignments.
     * @param chosenTotem       The totem to use for the move.
     * @return True if a strategic move was successfully executed, false otherwise.
     */
    private boolean attemptStrategicMove(Game game, Board board, List<int[]> alignmentPositions, Totem chosenTotem) {
        for (int[] alignment : alignmentPositions) {
            List<int[]> surroundingPositions = getSurroundingPositions(game, alignment);

            for (int[] pos : surroundingPositions) {
                if (attemptMoveAndPlace(game, board, pos, alignment, chosenTotem)) {
                    return true; // Move and placement succeeded
                }
            }
        }
        return false;
    }

    /**
     * Attempts to move a totem and place a token at specific positions.
     *
     * @param game      The current game instance.
     * @param board     The game board.
     * @param movePos   The position to move the totem to.
     * @param placePos  The position to place the token.
     * @param chosenTotem The totem to use for the move.
     * @return True if the move and placement were successful, false otherwise.
     */
    private boolean attemptMoveAndPlace(Game game, Board board, int[] movePos, int[] placePos, Totem chosenTotem) {
        if (chosenTotem == null) {
            Totem totemX = board.getTotemX();
            Totem totemO = board.getTotemO();

            if (attemptWithSpecificTotem(game, board, movePos, placePos, totemO) ||
                    attemptWithSpecificTotem(game, board, movePos, placePos, totemX)) {
                return true;
            }
        } else {
            if (attemptWithSpecificTotem(game, board, movePos, placePos, chosenTotem)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to move and place a specific totem at given positions.
     *
     * @param game      The current game instance.
     * @param board     The game board.
     * @param movePos   The position to move the totem to.
     * @param placePos  The position to place the token.
     * @param totem     The specific totem to use for the move.
     * @return True if the move and placement were successful, false otherwise.
     */
    private boolean attemptWithSpecificTotem(Game game, Board board, int[] movePos, int[] placePos, Totem totem) {
        if (game.isMoveTotemPossible(movePos[0], movePos[1], totem) &&
                canPlaceToken(game, board, placePos[0], placePos[1], totem)) {
            game.moveTotem(movePos[0], movePos[1], totem);
            game.placeToken(placePos[0], placePos[1], totem);
            return true;
        }
        return false;
    }

    /**
     * Finds all potential alignments of three tokens on the board.
     *
     * @param board The game board.
     * @return A list of positions representing alignments.
     */
    private List<int[]> findAlignments(Board board) {
        List<int[]> alignments = new ArrayList<>();
        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                Token token = board.getToken(x, y);
                if (token != null) {
                    alignments.addAll(checkAlignmentOfThree(board, x, y, true)); // Horizontal
                    alignments.addAll(checkAlignmentOfThree(board, x, y, false)); // Vertical
                }
            }
        }
        return alignments;
    }

    /**
     * Checks for a sequence of 3 matching tokens horizontally or vertically
     * and returns the positions of cells before and after the alignment.
     * @param x the x-coordinate of the token to check from
     * @param y the y-coordinate of the token to check from
     * @param isHorizontal if true, checks horizontally; otherwise, checks vertically
     * @return a list containing two positions (before and after) or an empty list if no alignment is found
     */
    private List<int[]> checkAlignmentOfThree(Board board, int x, int y, boolean isHorizontal) {
        List<int[]> result = new ArrayList<>();

        // Process color alignment
        List<int[]> colorAlignment = checkAlignment(board, x, y, isHorizontal, true);
        result.addAll(colorAlignment);

        // Process shape alignment
        List<int[]> shapeAlignment = checkAlignment(board, x, y, isHorizontal, false);
        result.addAll(shapeAlignment);

        return result;
    }

    /**
     * Checks for an alignment of 3 tokens based on a specific property (color or shape).
     * @param board the game board
     * @param x the x-coordinate of the starting point
     * @param y the y-coordinate of the starting point
     * @param isHorizontal true if checking horizontally, false if vertically
     * @param checkColor true to check color, false to check shape
     * @return a list of positions before and after the alignment if found
     */
    private List<int[]> checkAlignment(Board board, int x, int y, boolean isHorizontal, boolean checkColor) {
        int count = 0;
        Token referenceToken = null;
        int[] start = null;
        int[] end = null;
        List<int[]> result = new ArrayList<>();

        for (int i = 0; i < board.getSize(); i++) {
            int currentX = isHorizontal ? x : i;
            int currentY = isHorizontal ? i : y;

            Token currentToken = board.getToken(currentX, currentY);

            // Reset the count if token is null or a totem
            if (currentToken == null || currentToken instanceof Totem) {
                count = 0;
                referenceToken = null;
                start = null;
                continue;
            }

            // Check if the token matches the property (color or shape)
            if (!matchesProperty(referenceToken, currentToken, checkColor)) {
                count = 1;
                referenceToken = currentToken;
                start = new int[] { currentX, currentY };
            } else {
                count++;
                if (count == 3) {
                    end = new int[] { currentX, currentY };
                    break;
                }
            }
        }

        // If an alignment is found, calculate positions before and after
        if (count == 3) {
            addBoundaryPositions(board, result, start, end, isHorizontal);
        }

        return result;
    }

    /**
     * Checks if the property (color or shape) of two tokens matches.
     * @param referenceToken the reference token
     * @param currentToken the current token being checked
     * @param checkColor true to check color, false to check shape
     * @return true if the property matches, false otherwise
     */
    private boolean matchesProperty(Token referenceToken, Token currentToken, boolean checkColor) {
        if (referenceToken == null) return false;
        return checkColor ? referenceToken.getColor().equals(currentToken.getColor())
                : referenceToken.getShape().equals(currentToken.getShape());
    }

    /**
     * Adds the boundary positions (before and after alignment) to the result list.
     * @param board the game board
     * @param result the list to add positions to
     * @param start the start position of the alignment
     * @param end the end position of the alignment
     * @param isHorizontal true if alignment is horizontal, false if vertical
     */
    private void addBoundaryPositions(Board board, List<int[]> result, int[] start, int[] end, boolean isHorizontal) {
        int[] before = { start[0] - (isHorizontal ? 0 : 1), start[1] - (isHorizontal ? 1 : 0) };
        int[] after = { end[0] + (isHorizontal ? 0 : 1), end[1] + (isHorizontal ? 1 : 0) };

        if (board.isValidPosition(before[0], before[1]) && board.isCellEmpty(before[0], before[1])) {
            result.add(before);
        }
        if (board.isValidPosition(after[0], after[1]) && board.isCellEmpty(after[0], after[1])) {
            result.add(after);
        }
    }


    /**
     * Gets the surrounding positions of a given alignment for potential moves.
     *
     * @param game      The current game instance.
     * @param alignment The alignment position to evaluate.
     * @return A list of surrounding free positions.
     */
    private List<int[]> getSurroundingPositions(Game game, int[] alignment) {
        return game.getFreeAdjacentCells(alignment[0], alignment[1]);
    }

    /**
     * Checks if placing a token at the given position results in victory.
     *
     * @param game  The current game instance.
     * @param board The game board.
     * @param x     The x-coordinate of the position.
     * @param y     The y-coordinate of the position.
     * @param totem The totem being used to place the token.
     * @return True if the placement results in victory, false otherwise.
     */
    private boolean canPlaceToken(Game game, Board board, int x, int y, Totem totem) {
        Color color = game.getCurrentPlayerColor();
        Shape shape = totem.getShape();
        board.placeToken(x, y, new Token(color, shape));
        boolean victory = game.checkVictory(x, y);
        board.removeToken(x, y);
        return victory;
    }
}




