package g61258.dev3.oxono.model;

import java.util.List;
import java.util.Random;

// todo Pour niveau 1 il faut vérifier si il y a 3 cases alignés
/**
 * A strategy for an opponent that makes random moves during gameplay.
 * The strategy randomly chooses a totem (X or O) to move and then randomly selects
 * an available position to move the totem to. If the totem is enclaved after the move,
 * it places a token in an empty cell. Otherwise, it places a token in one of the adjacent cells to the totem.
 */
//public class RandomOpponentStrategy implements OpponentStrategy {
//
//    private final Random random = new Random();
//
//    /**
//     * Executes the random opponent's turn by making a random move with a totem
//     * and placing a token according to specific rules.
//     *
//     * <p>The strategy first selects a totem (either X or O) at random. Then, it attempts
//     * to move the selected totem to a random available position. If the totem is enclaved,
//     * a token is placed in an empty cell. If not, the token is placed in an adjacent cell
//     * to the totem.</p>
//     *
//     * @param game the game instance, which is used to interact with the game state,
//     *             including the board, totems, and tokens
//     */
//    @Override
//    public void play(Game game,Board board) {
//
//        int[] movePosition = null;
//
//        // Choisir aléatoirement un totem (X ou O) à déplacer
//        Totem totemX = board.getTotemX();
//        Totem totemO = board.getTotemO();
//
//        int nbCurrentCrossTokens = game.getCurrentPlayer().getNbTokens(Shape.CROSS);
//        int nbCurrentCircleTokens = game.getCurrentPlayer().getNbTokens(Shape.CIRCLE);
//
//// Vérifier si un totem est invalide en fonction des jetons restants
//        boolean canChooseTotemX = nbCurrentCrossTokens > 0;
//        boolean canChooseTotemO = nbCurrentCircleTokens > 0;
//
//// Choisir un totem en fonction de la validité
//        Totem chosenTotem = null;
//        if (canChooseTotemX && canChooseTotemO) {
//            // Les deux totems sont valides, choix aléatoire
//            chosenTotem = random.nextBoolean() ? totemX : totemO;
//        } else if (canChooseTotemX) {
//            // Seul le totem X est valide
//            chosenTotem = totemX;
//        } else if (canChooseTotemO) {
//            // Seul le totem O est valide
//            chosenTotem = totemO;
//        }
//
//        // Essayer de déplacer le totem choisi
//        boolean moveSuccess = false;
//        while (!moveSuccess) {
//            // Choisir une position aléatoire parmi les positions disponibles
//            List<int[]> availableTotemPositions = board.getAllEmptyCells();
//            if (!availableTotemPositions.isEmpty()) {
//                int[] randomPosition = availableTotemPositions.get(random.nextInt(availableTotemPositions.size()));
//
//                // Vérifier si le déplacement du totem est possible
//                if (game.isMoveTotemPossible(randomPosition[0], randomPosition[1], chosenTotem)) {
//                    game.moveTotem(randomPosition[0], randomPosition[1],chosenTotem);
//                    movePosition = randomPosition;
//                    moveSuccess = true;
//                }
//            }
//        }
//
//        // Après avoir déplacé le totem, vérifier si le totem est enclavé
//        boolean totemIsEnclaved = game.isTotemEnclaved(chosenTotem);
//        if (totemIsEnclaved) {
//            // Si le totem est enclavé, placer le token dans une case vide
//            List<int[]> emptyCells = board.getAllEmptyCells();
//            if (!emptyCells.isEmpty()) {
//                int[] randomEmptyCell = emptyCells.get(random.nextInt(emptyCells.size()));
//                game.placeToken(randomEmptyCell[0], randomEmptyCell[1], chosenTotem);
//            }
//        } else {
//            // Sinon, placer le token sur une des cases adjacentes au totem
//            List<int[]> adjacentPositions = game.getFreeAdjacentCells( movePosition[0], movePosition[1]);
//            for (int[] pos : adjacentPositions) {
//                if (game.canPlaceToken(pos[0], pos[1], chosenTotem)) {
//                    game.placeToken(pos[0], pos[1], chosenTotem);
//                    break;
//                }
//            }
//        }
//    }
//}
public class RandomOpponentStrategy implements OpponentStrategy {

    private final Random random = new Random();

    @Override
    public void play(Game game, Board board) {
        Totem chosenTotem = chooseTotem(game, board);
        if (chosenTotem == null) return;

        int[] movePosition = moveTotem(game, board, chosenTotem);
        if (movePosition == null) return;

        placeToken(game, board, chosenTotem, movePosition);
    }

    /**
     * Chooses a valid totem to move based on the current player's available tokens.
     * @param game  the current game instance
     * @param board the game board
     * @return the chosen totem or null if no valid totem can be chosen
     */
    private Totem chooseTotem(Game game, Board board) {
        Totem totemX = board.getTotemX();
        Totem totemO = board.getTotemO();

        int nbCurrentCrossTokens = game.getCurrentPlayerTokenCount(Shape.CROSS);
        int nbCurrentCircleTokens = game.getCurrentPlayerTokenCount(Shape.CIRCLE);

        boolean canChooseTotemX = nbCurrentCrossTokens > 0;
        boolean canChooseTotemO = nbCurrentCircleTokens > 0;

        if (canChooseTotemX && canChooseTotemO) {
            return random.nextBoolean() ? totemX : totemO;
        } else if (canChooseTotemX) {
            return totemX;
        } else if (canChooseTotemO) {
            return totemO;
        }
        return null;
    }

    /**
     * Moves the chosen totem to a random valid position.
     * @param game        the current game instance
     * @param board       the game board
     * @param chosenTotem the totem to move
     * @return the position to which the totem was moved, or null if no move was possible
     */
    private int[] moveTotem(Game game, Board board, Totem chosenTotem) {
        List<int[]> availablePositions = board.getAllEmptyCells();
        while (!availablePositions.isEmpty()) {
            int[] randomPosition = availablePositions.get(random.nextInt(availablePositions.size()));
            if (game.isMoveTotemPossible(randomPosition[0], randomPosition[1], chosenTotem)) {
                game.moveTotem(randomPosition[0], randomPosition[1], chosenTotem);
                return randomPosition;
            }
            availablePositions.remove(randomPosition);
        }
        return null;
    }

    /**
     * Places a token after moving the totem, either in an empty cell if the totem is enclaved,
     * or in a valid adjacent position.
     * @param game        the current game instance
     * @param board       the game board
     * @param chosenTotem the totem to place the token near
     * @param movePosition the position where the totem was moved
     */
    private void placeToken(Game game, Board board, Totem chosenTotem, int[] movePosition) {
        if (game.isTotemEnclaved(chosenTotem)) {
            placeTokenInRandomEmptyCell(game, board, chosenTotem);
        } else {
            placeTokenInAdjacentCell(game, movePosition, chosenTotem);
        }
    }

    /**
     * Places a token in a random empty cell.
     * @param game        the current game instance
     * @param board       the game board
     * @param chosenTotem the totem to place the token for
     */
    private void placeTokenInRandomEmptyCell(Game game, Board board, Totem chosenTotem) {
        List<int[]> emptyCells = board.getAllEmptyCells();
        if (!emptyCells.isEmpty()) {
            int[] randomEmptyCell = emptyCells.get(random.nextInt(emptyCells.size()));
            game.placeToken(randomEmptyCell[0], randomEmptyCell[1], chosenTotem);
        }
    }

    /**
     * Places a token in a valid adjacent cell near the specified position.
     * @param game        the current game instance
     * @param movePosition the position to check adjacent cells
     * @param chosenTotem the totem to place the token for
     */
    private void placeTokenInAdjacentCell(Game game, int[] movePosition, Totem chosenTotem) {
        List<int[]> adjacentPositions = game.getFreeAdjacentCells(movePosition[0], movePosition[1]);
        for (int[] pos : adjacentPositions) {
            if (game.canPlaceToken(pos[0], pos[1], chosenTotem)) {
                game.placeToken(pos[0], pos[1], chosenTotem);
                break;
            }
        }
    }
}


