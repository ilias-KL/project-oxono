package g61258.dev3.oxono.console;

import g61258.dev3.oxono.model.*;
import g61258.dev3.oxono.model.Shape;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Controller class manages user interactions, coordinates
 * between the view and model, and facilitates gameplay.
 */
public class ConsoleController {
    private final Game game;
    private final Totem totemX;
    private final Totem totemO;
    private final ConsoleView view;
    private final Scanner scanner;

    private int boardSize;

    private static final Pattern COORDINATE_PATTERN = Pattern.compile("^\\s*(\\d+)\\s+(\\d+)\\s*$");

    /**
     * Constructs a new Controller.
     * Initializes the game with the requested board size and sets up the view.
     */
    public ConsoleController() {
        this.scanner = new Scanner(System.in);
        this.boardSize = askForBoardSize();
        Board board = new Board(boardSize);
        this.game = new Game(6,0);
        this.totemX = board.getTotemX();
        this.totemO = board.getTotemO();
        this.view = new ConsoleView();
    }

    /**
     * Starts the main game loop.
     * This method runs the game, handles user inputs, displays the board and game state,
     * and checks the win conditions until the game is over.
     */
    public void start() {
        game.start();
        System.out.println("Bienvenue dans Oxono ! La partie commence.");
        view.displayBoard(game,boardSize,totemX,totemO);

        while (!game.isGameOver()) {

            view.displayBoard(game,boardSize,totemX,totemO);

            if (game.stillHasTokens()) {
                Totem chosenTotem = null;


                while (chosenTotem == null) {
                    chosenTotem = getTotemChoice();
                }

                do {
                    moveTotem(chosenTotem);
                    view.displayBoard(game,boardSize,totemX,totemO);
                } while (askUndoRedo());


                int[] coords;
                do {
                    coords = placeToken(chosenTotem);
                    view.displayBoard(game,boardSize,totemX,totemO);
                } while (askUndoRedo());


                this.game.setGameOver(this.game.checkVictory(coords[0],coords[1]));
                if (this.game.isGameOver()) {
                    this.game.setWinner();
                } else {
                    game.switchPlayer();
                }

            } else {
                game.switchPlayer();
            }
            view.displayBoard(game,boardSize,totemX,totemO);
        }

        view.displayBoard(game,boardSize,totemX,totemO);
    }

    /**
     * Asks the user to input the board size.
     * The board size must be an even number and at least 6.
     * @return the size of the game board
     */
    private int askForBoardSize() {
        int size = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Veuillez entrer la taille du tableau (un nombre entier pair et >= 6) : ");
            if (scanner.hasNextInt()) {
                size = scanner.nextInt();
                scanner.nextLine(); // Consume the remaining newline
                if (size >= 6 && size % 2 == 0) {
                    validInput = true;
                } else if (size < 6) {
                    System.out.println("La taille doit être égale ou supérieure à 6.");
                } else {
                    System.out.println("La taille doit être un nombre pair.");
                }
            } else {
                System.out.println("Entrée invalide. Veuillez entrer un nombre entier.");
                scanner.next(); // Consume invalid input
            }
        }
        return size;
    }


    /**
     * Prompts the user to choose the totem to move.
     * @return the chosen Totem
     */
    private Totem getTotemChoice() {
        int nbCrossTokens = game.getCurrentPlayerNbTokens(Shape.CROSS);
        int nbCircleTokens = game.getCurrentPlayerNbTokens(Shape.CIRCLE);
        if (nbCrossTokens == 0 && nbCircleTokens != 0) {
            System.out.println("Attention : Il n’y a plus de jetons en forme de 'X' en réserve. Le totem 'O' sera utilisé.");
            return this.game.choseTotem(Shape.CIRCLE);
        } else if (nbCircleTokens == 0 && nbCrossTokens != 0) {
            System.out.println("Attention : Il n’y a plus de jetons en forme de 'O' en réserve. Le totem 'X' sera utilisé.");
            return this.game.choseTotem(Shape.CROSS);
        } else {
            System.out.print("Choisissez le symbole du totem à déplacer (X ou O) : ");
            String totemSymbol = scanner.nextLine().toUpperCase();
            if (totemSymbol.equals("X")) {
                return this.game.choseTotem(Shape.CROSS);
            } else if (totemSymbol.equals("O")) {
                return this.game.choseTotem(Shape.CIRCLE);
            } else {
                System.out.println("Symbole invalide. Veuillez entrer 'X' ou 'O'.");
                return null;
            }
        }

    }

    /**
     * Moves the chosen totem based on user input.
     * @param totem the totem to be moved
     */
    private void moveTotem(Totem totem) {
        if (game.areRowsAndColumnsOccupied(totem)) {
            System.out.print("Entrez les coordonnées de déplacement du totem (x y) n'import quelle case libre: ");
        } else {
            System.out.print("Entrez les coordonnées de déplacement du totem (x y) : ");
        }

        String input = scanner.nextLine();

        Matcher matcher = COORDINATE_PATTERN.matcher(input);
        if (matcher.matches()) {
            int totemX = Integer.parseInt(matcher.group(1));
            int totemY = Integer.parseInt(matcher.group(2));

            if (game.areRowsAndColumnsOccupied(totem)) {
                if (game.isCellEmpty(totemX,totemY)) {
                    //dans Game
//                    Command moveAnywhere = new MoveTotemCommand(game,totem,totem.getX(),totem.getY(),totemX,totemY);
//                    commandManager.executeCommand(moveAnywhere);
                    game.moveTotem(totemX,totemY,totem);
                } else {
                    System.out.println("Déplacement du totem impossible, réessayez.");
                    moveTotem(totem); // demander à nouveau
                }
            } else {

                if (game.isMoveTotemPossible(totemX, totemY, totem)) {
                    // dans Game
//                    Command moveCommand = new MoveTotemCommand(game,totem,totem.getX(),totem.getY(),totemX,totemY);
//                    commandManager.executeCommand(moveCommand);
                    game.moveTotem(totemX,totemY,totem);
                    System.out.println("Totem déplacé avec succès !");
                } else {
                    System.out.println("Déplacement du totem impossible, réessayez.");
                    moveTotem(totem); // demander à nouveau
                }
            }

        } else {
            System.out.println("Format de coordonnées invalide. Veuillez entrer deux nombres séparés par un espace.");
            moveTotem(totem); // demander à nouveau
        }
    }

    /**
     * Places a token for the current player based on user input.
     * The token is placed either on an adjacent empty cell or a free cell if the totem is enclaved.
     * If the placement is invalid, the user will be prompted to try again.
     * @param lastMovedTotem the totem last moved by the player
     */
    private int[] placeToken(Totem lastMovedTotem) {
        boolean enclavedTotem = game.isTotemEnclaved(lastMovedTotem);
        if (enclavedTotem) {
            System.out.print("Entrez les coordonnées de n'importe quelle case libre pour placer votre jeton (x y) : ");
        } else {
            System.out.print("Entrez les coordonnées pour placer votre jeton (x y) : ");
        }

        String input = scanner.nextLine();

        Matcher matcher = COORDINATE_PATTERN.matcher(input);
        if (matcher.matches()) {
            int tokenX = Integer.parseInt(matcher.group(1));
            int tokenY = Integer.parseInt(matcher.group(2));

            if (enclavedTotem) {
                if (game.canPlaceTokenAnywhere(tokenX,tokenY, lastMovedTotem)) {
                    // Dans Game
//                    Command placeAnywhereCommand = new PlaceTokenCommand(game,tokenX,tokenY,lastMovedTotem);
//                    commandManager.executeCommand(placeAnywhereCommand);
                    game.placeToken(tokenX,tokenY, lastMovedTotem);
                    System.out.println("Jeton placé avec succès !");
                } else {
                    System.out.println("Placement du jeton impossible, réessayez.");
                    placeToken(lastMovedTotem); // demander à nouveau
                }
            } else {
                if (game.canPlaceToken(tokenX, tokenY, lastMovedTotem)) {
                    // Dans Game
//                    Command placeTokenCommand = new PlaceTokenCommand(game,tokenX,tokenY,lastMovedTotem);
//                    commandManager.executeCommand(placeTokenCommand);
                    game.placeToken(tokenX,tokenY, lastMovedTotem);
                    System.out.println("Jeton placé avec succès !");
                } else {
                    System.out.println("Placement du jeton impossible, réessayez.");
                    placeToken(lastMovedTotem); // demander à nouveau
                }
            }
            return new int[] {tokenX, tokenY};
        } else {
            System.out.println("Format de coordonnées invalide. Veuillez entrer deux nombres séparés par un espace.");
            placeToken(lastMovedTotem); // demander à nouveau
        }
        return null;
    }

    private boolean askUndoRedo() {
        while (true) {
            System.out.println("Voulez-vous annuler ou refaire l'action ? (a) pour annuler, (r) pour refaire, ou appuyez sur Entrée pour continuer.");
            String input = scanner.nextLine();

            if (input.equals("a")) {
                // Annule la dernière action

                // dans game
                //  commandManager.undo();
                game.undo();
                view.displayBoard(game,boardSize,totemX,totemO);
                System.out.println("Action annulée.");

                // Après avoir annulé, demande si l'on souhaite refaire l'action
                System.out.println("Souhaitez-vous refaire l'action ? (r) pour refaire, ou appuyez sur Entrée pour continuer.");
                input = scanner.nextLine();
                if (input.equals("r")) {
                    // Refait la dernière action annulée

                    // Dans Game
                    //  commandManager.redo();
                    game.redo();
                    view.displayBoard(game,boardSize,totemX,totemO);
                    System.out.println("Action refaite.");
                } else {
                    // Si on ne refait pas, on continue sans refaire
                    return true;
                }
            } else if (input.equals("r")) {
                // Refait la dernière action annulée
                // commandManager.redo();
                game.redo();
                view.displayBoard(game,boardSize,totemX,totemO);
                System.out.println("Action refaite.");

                // Après avoir refait, demande si l'on souhaite annuler l'action
                System.out.println("Souhaitez-vous annuler l'action ? (a) pour annuler, ou appuyez sur Entrée pour continuer.");
                input = scanner.nextLine();
                if (input.equals("a")) {
                    // Annule la dernière action
                    //     commandManager.undo();
                    game.undo();
                    view.displayBoard(game,boardSize,totemX,totemO);
                    System.out.println("Action annulée.");
                } else {
                    // Si on ne l'annule pas, on continue sans annuler
                    return false;
                }
            } else {
                // Si l'utilisateur appuie sur Entrée, l'action est validée
                System.out.println("Action validée, on continue...");
                return false;
            }
        }
    }

    public static void main(String[] args) {
        ConsoleController c = new ConsoleController();
        c.start();
    }

}