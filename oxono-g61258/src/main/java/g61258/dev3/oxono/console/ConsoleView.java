package g61258.dev3.oxono.console;

import g61258.dev3.oxono.model.*;
import g61258.dev3.oxono.model.Shape;

import java.util.List;

public class ConsoleView {

    /**
     * Displays the current state of the game.
     */
    public void displayGameState(Game game) {
        System.out.println("\nÉtat actuel du plateau :");
        String playerColor = game.getCurrentPlayerColor().equals(Color.BLACK) ? "Black" : "Rose";
        System.out.println("Joueur courant : " + playerColor);
        System.out.println("Jetons restants pour le joueur Rose : " + displayPlayerTokens(game.getPlayerTokens(Color.PINK)));
        System.out.println("Jetons restants pour le joueur Noir : " + displayPlayerTokens(game.getPlayerTokens(Color.PINK)));
        System.out.println("Cases vides restantes : " + game.getEmptyCellsCount());
    }

    /**
     * Displays the end of game message and the winner.
     */
    public void displayEndOfGame(Game game) {
        System.out.println("\nFin de la partie !");
        String winner = game.isGameOver() ? game.getWinner() : null;
        if (winner != null) {
            System.out.println("Le gagnant est : " + winner);
        } else {
            System.out.println("La partie est terminée sans gagnant.");
        }
    }

    /**
     * Displays the current state of the board, including tokens and totems.
     */
    public void displayBoard(Game game, int size, Totem totemX,
                             Totem totemO) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (x == totemX.getX() && y == totemX.getY()) {
                    // Affiche le totem X en bleu
                    System.out.print("\u001B[34m X \u001B[0m");
                } else if (x == totemO.getX() && y == totemO.getY()) {
                    // Affiche le totem O en bleu
                    System.out.print("\u001B[34m O \u001B[0m");
                } else {
                    Token token = game.getToken(x, y);
                    if (token != null) {
                        // Affiche le symbole du jeton avec la couleur correspondante
                        String symbol = token.getShape() == Shape.CROSS ? "X" : "O";
                        String colorCode = token.getColor() == Color.BLACK ? "\u001B[30m" : "\u001B[35m";
                        System.out.print(colorCode + " " + symbol + " \u001B[0m");
                    } else {
                        // Affiche une case vide
                        System.out.print(" . ");
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * Displays the list of tokens held by a player in a readable format.
     * @param tokens the list of Token objects held by the player
     * @return a string representation of the player's tokens in the form "Player{ tokens= CIRCLE CROSS }"
     */
    public String displayPlayerTokens(List<Token> tokens) {
        String display = "Player{ tokens= ";
        for(Token t : tokens) {
            String shape = t.getShape().equals(Shape.CIRCLE) ? "CIRCLE" : "CROSS";
            display = display + " " + shape;
        }
        display = display + " " +  '}';
        return  display;
    }

}