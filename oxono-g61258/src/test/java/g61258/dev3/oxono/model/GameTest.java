package g61258.dev3.oxono.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    private Board board;


    @BeforeEach
    public void setup() {
        this.game = new Game(6,0);
        board = game.getBoard();
    }

    @Test
    public void testInitialGameState() {
        assertNotNull(board, "Le plateau doit être initialisé");
        assertEquals(Color.PINK, game.getCurrentPlayerColor(), "Le premier joueur doit être rose");
        assertFalse(game.isGameOver(), "Le jeu ne doit pas être terminé au démarrage");
        assertNull(game.getWinner(), "Aucun gagnant ne doit être défini au début");
    }


    @Test
    public void testChoseTotem() {
        assertEquals(board.getTotemO(), game.choseTotem(Shape.CIRCLE));
        assertEquals(board.getTotemX(), game.choseTotem(Shape.CROSS));
    }

    @Test
    public void testPlaceTokenTrue() {
        int x = board.getTotemX().getX();
        int y = board.getTotemX().getY();

        Totem totem = board.getTotemX();


        // Coordonnées des cases adjacentes
        int xAbove = x;
        int yAbove = y - 1;

        int xLeft = x - 1;
        int yLeft = y;

        int xRight = x + 1;
        int yRight = y;

        int xBelow = x;
        int yBelow = y + 1;

        // Vérification initiale : toutes les cases adjacentes doivent être vides
        assertNull(game.getToken(xBelow, yBelow));
        assertNull(game.getToken(xAbove, yAbove));
        assertNull(game.getToken(xRight, yRight));
        assertNull(game.getToken(xLeft, yLeft));

        // Placement des jetons et récupération pour vérification
        game.placeToken(xBelow, yBelow, totem);
        Token below = game.getToken(xBelow, yBelow);
        assertNotNull(below); // Vérifie que le jeton a bien été placé
        assertEquals(totem.getShape(), below.getShape()); // Vérifie que la forme correspond

        game.placeToken(xAbove, yAbove, totem);
        Token above = game.getToken(xAbove, yAbove);
        assertNotNull(above);
        assertEquals(totem.getShape(), above.getShape());

        game.placeToken(xRight, yRight, totem);
        Token right = game.getToken(xRight, yRight);
        assertNotNull(right);
        assertEquals(totem.getShape(), right.getShape());

        game.placeToken(xLeft, yLeft, totem);
        Token left = game.getToken(xLeft, yLeft);
        assertNotNull(left);
        assertEquals(totem.getShape(), left.getShape());

        // Vérification finale : toutes les cases doivent contenir un jeton correspondant
        assertEquals(totem.getShape(), below.getShape());
        assertEquals(totem.getShape(), above.getShape());
        assertEquals(totem.getShape(), right.getShape());
        assertEquals(totem.getShape(), left.getShape());
    }

    @Test
    public void testCanPlaceTokenAnywhereTrue() {
       assertTrue(game.isCellEmpty(0,0));
       assertTrue(game.canPlaceTokenAnywhere(0,0,board.getTotemX()));
       assertTrue(game.canPlaceTokenAnywhere(0,0,board.getTotemO()));
    }

    @Test
    public void testCanPlaceTokenAnywhereFalse() {
        // Cell Not Empty
        board.placeToken(0,0, board.getTotemX());
        assertFalse(board.isCellEmpty(0,0));
        assertFalse(game.canPlaceTokenAnywhere(0,0,board.getTotemX()));
        assertFalse(game.canPlaceTokenAnywhere(0,0,board.getTotemO()));

        // Player has no tokens of the same Shape
        for (int i = 0; i < 8; i++) {
            game.removeTokenFromCurrentPlayer(Shape.CIRCLE);
            game.removeTokenFromCurrentPlayer(Shape.CROSS);
        }
        assertTrue(game.isCellEmpty(1,1));
        assertFalse(game.canPlaceTokenAnywhere(1,1,board.getTotemX()));
        assertFalse(game.canPlaceTokenAnywhere(1,1,board.getTotemO()));

    }

    @Test
    public void testCanPlaceTokenTrue() {
        int x = board.getTotemX().getX();
        int y = board.getTotemX().getY() + 1;
        assertTrue(board.isCellEmpty(x,y));
        assertTrue(game.canPlaceToken(x,y,board.getTotemX()));

        int x2 = board.getTotemO().getX();
        int y2 = board.getTotemO().getY() + 1;
        assertTrue(board.isCellEmpty(x2,y2));
        assertTrue(game.canPlaceToken(x2,y2,board.getTotemO()));
    }

    @Test
    public void testCanPlaceTokenFalse() {
        // Not adjacent to Totem
        assertTrue(board.isCellEmpty(0,0));
        assertFalse(game.canPlaceToken(0,0,board.getTotemX()));

        // Not on empty Cell
        int x = board.getTotemX().getX();
        int y = board.getTotemX().getY();
        assertFalse(board.isCellEmpty(x,y));
        assertFalse(game.canPlaceToken(x,y,board.getTotemX()));
    }

    @Test
    public void testMoveTotem(){
        int x = board.getTotemX().getX();
        int y = board.getTotemX().getY() + 1;
        assertTrue(board.isCellEmpty(x,y));
        game.moveTotem(x,y,board.getTotemX());
        assertFalse(board.isCellEmpty(x,y));
        assertEquals(board.getTotemX(),game.getToken(x,y));
    }

    @Test
    public void testMoveTotemAdjacent() {
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        int newX = currentX + 1; // Déplacement d'une case à droite
        int newY = currentY;

        assertTrue(game.isMoveTotemPossible(newX, newY, totem));
    }

    @Test
    public void testMoveTotemLine() {
        assertTrue(board.getTotemX().getX() == 2 || board.getTotemX().getX() == 3);
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Assurez-vous que toutes les cases entre les deux positions sont vides
        int newX = currentX + 2; // Déplacement horizontal de 2 cases
        int newY = currentY;
        System.out.println(board);
        assertTrue(game.isMoveTotemPossible(newX, newY, totem));
    }

    @Test
    public void testMoveEnclosedTotemOverPieces() {
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Placer des pièces autour du totem pour l'enclaver
        board.placeToken(currentX + 1, currentY, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(currentX - 1, currentY, new Token(Color.BLACK, Shape.CROSS));
        board.placeToken(currentX, currentY + 1, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(currentX, currentY - 1, new Token(Color.BLACK, Shape.CROSS));

        // Déplacement par-dessus une série de pièces vers la première case libre
        int newX = currentX + 2;
        int newY = currentY;

        assertTrue(game.isMoveTotemPossible(newX, newY, totem));
    }

    @Test
    public void testMoveEnclosedTotemToAnyFreeCell() {
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Placer des pièces pour bloquer toutes les cases adjacentes et en ligne droite
        for (int i = 0; i < board.getSize(); i++) {
            board.placeToken(i, currentY, new Token(Color.PINK, Shape.CIRCLE));
            board.placeToken(currentX, i, new Token(Color.BLACK, Shape.CROSS));
        }

        // Essayer de déplacer le totem vers une case libre ailleurs
        int newX = 5;
        int newY = 5;

        assertTrue(game.isMoveTotemPossible(newX, newY, totem));
    }

    @Test
    public void testMoveTotemToOccupiedCell() {
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Placer un token sur une case adjacente
        int newX = currentX + 1;
        int newY = currentY;
        board.placeToken(newX, newY, new Token(Color.PINK, Shape.CIRCLE));

        // Tenter de déplacer le totem sur cette case occupée
        assertFalse(game.isMoveTotemPossible(newX, newY, totem));
    }

    @Test
    public void testMoveTotemDiagonal() {
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Déplacement diagonal (non valide si le totem n'est pas enclavé)
        int newX = currentX + 1;
        int newY = currentY + 1;

        assertFalse(game.isMoveTotemPossible(newX, newY, totem));
    }

    @Test
    public void testMoveTotemNonAligned() {
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Déplacement non aligné horizontalement ou verticalement
        int newX = currentX + 2;
        int newY = currentY + 1;

        assertFalse(game.isMoveTotemPossible(newX, newY, totem));
    }

    @Test
    public void testMoveTotemToSamePosition() {
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Tenter de déplacer le totem sur sa position actuelle
        assertFalse(game.isMoveTotemPossible(currentX, currentY, totem));
    }

    @Test
    public void testMoveTotemOutOfBounds() {
        Totem totem = board.getTotemX();
        int currentX = totem.getX();
        int currentY = totem.getY();

        // Déplacement hors des limites du plateau
        int newX = -1;
        int newY = -1;

        assertFalse(game.isMoveTotemPossible(newX, newY, totem));

        newX = board.getSize() + 1;
        newY = board.getSize() + 1;

        assertFalse(game.isMoveTotemPossible(newX, newY, totem));
    }

    @Test
    public void testGetFreeAdjacentCells_AllAdjacentFree() {
        int x = 3;
        int y = 3;

        List<int[]> freeCells = game.getFreeAdjacentCells(x, y);

        // Vérifier que toutes les cellules adjacentes (haut, bas, gauche, droite) sont dans la liste
        assertEquals(4, freeCells.size());
        assertTrue(freeCells.get(0)[0] == x && freeCells.get(0)[1] == y + 1); // bas
        assertTrue(freeCells.get(1)[0] == x + 1 && freeCells.get(1)[1] == y); // droite
        assertTrue(freeCells.get(2)[0] == x && freeCells.get(2)[1] == y -1); // Haut
        assertTrue(freeCells.get(3)[0] == x - 1 && freeCells.get(3)[1] == y); // Gauche
    }

    @Test
    public void testGetFreeAdjacentCells_SomeOccupied() {
        int x = 3;
        int y = 3;

        // Occuper quelques cellules autour de (3,3)
        board.placeToken(x + 1, y, new Token(Color.PINK, Shape.CIRCLE)); // Droite occupée
        board.placeToken(x, y - 1, new Token(Color.BLACK, Shape.CROSS)); // Haut occupé

        List<int[]> freeCells = game.getFreeAdjacentCells(x, y);

        // Vérifier que seules les cellules libres sont retournées
        assertEquals(2, freeCells.size());
        assertTrue(freeCells.get(0)[0] == x && freeCells.get(0)[1] == y + 1); // bas
        assertTrue(freeCells.get(1)[0] == x - 1 && freeCells.get(1)[1] == y); // Gauche
    }

    @Test
    public void testGetFreeAdjacentCells_BorderPosition() {
        int x = 0; // Coin supérieur gauche
        int y = 0;

        // S'assurer que toutes les positions autour du coin supérieur gauche sont valides
        board.removeToken(0, 1); // Bas libre
        board.removeToken(1, 0); // Droite libre

        List<int[]> freeCells = game.getFreeAdjacentCells(x, y);

        // Vérifier que seules les cellules valides sont retournées
        assertEquals(2, freeCells.size());
        assertTrue(freeCells.get(0)[0] == 0 && freeCells.get(0)[1] == 1); // Bas
        assertTrue(freeCells.get(1)[0] == 1 && freeCells.get(1)[1] == 0); // Droite
    }

    @Test
    public void testGetFreeAdjacentCells_NoFreeCells() {
        int x = 2;
        int y = 2;

        // Occuper toutes les cellules autour de (2,2)
        board.placeToken(x + 1, y, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(x - 1, y, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(x, y + 1, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(x, y - 1, new Token(Color.PINK, Shape.CIRCLE));

        List<int[]> freeCells = game.getFreeAdjacentCells(x, y);

        // Vérifier qu'aucune cellule n'est libre
        assertTrue(freeCells.isEmpty());
    }

    @Test
    public void testGetFreeAdjacentCells_OutOfBounds() {
        int x = board.getSize() - 1; // Coin inférieur droit
        int y = board.getSize() - 1;

        // Libérer les cellules valides autour
        board.removeToken(x, y - 1); // Haut libre
        board.removeToken(x - 1, y); // Gauche libre

        List<int[]> freeCells = game.getFreeAdjacentCells(x, y);

        // Vérifier que seules les cellules dans les limites sont retournées
        assertEquals(2, freeCells.size());
        assertTrue(freeCells.get(0)[0] == x && freeCells.get(0)[1] == y -1); // Haut
        assertTrue(freeCells.get(1)[0] == x - 1 && freeCells.get(1)[1] == y); // Gauche
    }

    @Test
    public void testIsTotemEnclaved_AllAdjacentFree() {
        Totem totem = board.getTotemX();

        // S'assurer que toutes les cellules autour du totem sont libres
        for (int[] dir : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
            int newX = totem.getX() + dir[0];
            int newY = totem.getY() + dir[1];
            board.removeToken(newX, newY);
        }

        assertFalse(game.isTotemEnclaved(totem)); // Le totem ne devrait pas être enclavé
    }

    @Test
    public void testIsTotemEnclaved_PartiallyEnclaved() {
        Totem totem = board.getTotemO();

        // Occuper quelques cellules autour du totem
        board.placeToken(totem.getX() + 1, totem.getY(), new Token(Color.BLACK, Shape.CROSS)); // Droite occupée
        board.placeToken(totem.getX(), totem.getY() - 1, new Token(Color.PINK, Shape.CIRCLE)); // Haut occupé

        // S'assurer que les autres cellules sont libres
        board.removeToken(totem.getX() - 1, totem.getY()); // Gauche libre
        board.removeToken(totem.getX(), totem.getY() + 1); // Bas libre

        assertFalse(game.isTotemEnclaved(totem)); // Le totem ne devrait pas être enclavé
    }

    @Test
    public void testIsTotemEnclaved_CompletelyEnclaved() {
        Totem totem = board.getTotemO();
        // Occuper toutes les cellules autour du totem
        for (int[] dir : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
            int newX = totem.getX() + dir[0];
            int newY = totem.getY() + dir[1];
            board.placeToken(newX, newY, new Token(Color.BLACK, Shape.CROSS));
        }

        assertTrue(game.isTotemEnclaved(totem)); // Le totem devrait être enclavé
    }

    @Test
    public void testIsTotemEnclaved_BorderPosition() {
        Totem totem = new Totem(Color.PINK, Shape.CIRCLE,0,0); // Totem placé en coin supérieur gauche

        board.placeToken(0,0,totem);

        // Occuper les cellules accessibles autour (droite et bas)
        board.placeToken(0, 1, new Token(Color.BLACK, Shape.CROSS)); // Bas occupé
        board.placeToken(1, 0, new Token(Color.PINK, Shape.CIRCLE)); // Droite occupée

        assertTrue(game.isTotemEnclaved(totem)); // Le totem devrait être enclavé
    }

    @Test
    public void testIsTotemEnclaved_BorderWithFreeCells() {
        Totem totem = new Totem(Color.PINK, Shape.CIRCLE,0,0); // Totem placé en coin supérieur gauche

        board.placeToken(0,0,totem);

        // S'assurer que les cellules accessibles autour sont libres
        board.removeToken(0, 1); // Bas libre
        board.removeToken(1, 0); // Droite libre

        assertFalse(game.isTotemEnclaved(totem)); // Le totem ne devrait pas être enclavé
    }


    @Test
    public void testAreRowsAndColumnsOccupied_AllOccupied() {
        int boardSize = board.getSize();
        Totem totemX = board.getTotemX();
        Totem totemO = board.getTotemO();
        Totem totem = totemX.getX() == 3 ? totemX : totemO;


        // Occuper toutes les cellules de la ligne (y = 3) et de la colonne (x = 3)
        for (int i = 0; i < boardSize; i++) {
            if (i != 3) { // Ne pas écraser la cellule du totem
                board.placeToken(i, 3, new Token(Color.BLACK, Shape.CROSS)); // Ligne
                board.placeToken(3, i, new Token(Color.PINK, Shape.CIRCLE)); // Colonne
            }
        }
        assertTrue(game.areRowsAndColumnsOccupied(totem)); // Toutes les cellules sont occupées
    }

    @Test
    public void testAreRowsAndColumnsOccupied_RowNotFullyOccupied() {
        int boardSize = board.getSize();
        Totem totemX = board.getTotemX();
        Totem totemO = board.getTotemO();
        Totem totem = totemX.getX() == 3 ? totemX : totemO;

        // Occuper toutes les cellules sauf une dans la ligne (y = 3)
        for (int i = 0; i < boardSize; i++) {
            if (i != 5) { // Garder une cellule libre
                board.placeToken(i, 3, new Token(Color.BLACK, Shape.CROSS)); // Ligne
            }
            board.placeToken(3, i, new Token(Color.PINK, Shape.CIRCLE)); // Colonne
        }

        assertFalse(game.areRowsAndColumnsOccupied(totem)); // La ligne n'est pas complètement occupée
    }

    @Test
    public void testAreRowsAndColumnsOccupied_ColumnNotFullyOccupied() {
        int boardSize = board.getSize();
        Totem totemX = board.getTotemX();
        Totem totemO = board.getTotemO();
        Totem totem = totemX.getX() == 3 ? totemX : totemO;

        // Occuper toutes les cellules sauf une dans la colonne (x = 3)
        for (int i = 0; i < boardSize; i++) {
            if (i != 4) { // Garder une cellule libre
                board.placeToken(3, i, new Token(Color.PINK, Shape.CIRCLE)); // Colonne
            }
            board.placeToken(i, 3, new Token(Color.BLACK, Shape.CROSS)); // Ligne
        }

        assertFalse(game.areRowsAndColumnsOccupied(totem)); // La colonne n'est pas complètement occupée
    }

    @Test
    public void testAreRowsAndColumnsOccupied_RowAndColumnNotFullyOccupied() {
        int boardSize = board.getSize();
        Totem totemX = board.getTotemX();
        Totem totemO = board.getTotemO();
        Totem totem = totemX.getX() == 3 ? totemX : totemO;

        // Occuper toutes les cellules sauf une dans la ligne (y = 3) et une dans la colonne (x = 3)
        for (int i = 0; i < boardSize; i++) {
            if (i != 5) { // Garder une cellule libre dans la ligne
                board.placeToken(i, 3, new Token(Color.BLACK, Shape.CROSS));
            }
            if (i != 2) { // Garder une cellule libre dans la colonne
                board.placeToken(3, i, new Token(Color.PINK, Shape.CIRCLE));
            }
        }

        assertFalse(game.areRowsAndColumnsOccupied(totem)); // Ni la ligne ni la colonne ne sont complètement occupées
    }

    @Test
    public void testAreRowsAndColumnsOccupied_TotemOnBorder() {
        int boardSize = board.getSize();
        Totem totem = board.getTotemX();
        board.placeToken(0,0,totem);

        // Occuper toutes les cellules de la colonne (x = 0) sauf une
        for (int i = 1; i < boardSize; i++) {
            board.placeToken(0, i, new Token(Color.BLACK, Shape.CROSS)); // Colonne
        }

        // Laisser toutes les cellules de la ligne (y = 0) libres
        assertFalse(game.areRowsAndColumnsOccupied(totem)); // La ligne n'est pas complètement occupée
    }

    @Test
    public void testCheckVictory_HorizontalByColor() {
        // Préparation : placer 4 jetons roses alignés horizontalement
        board.placeToken(0, 1, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(1, 1, new Token(Color.PINK, Shape.CROSS));
        board.placeToken(2, 1, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(3, 1, new Token(Color.PINK, Shape.CROSS));

        // Vérifier si la victoire est détectée
        assertTrue(game.checkVictory(2, 1));
    }

    @Test
    public void testCheckVictory_VerticalByShape() {
        // Préparation : placer 4 jetons en forme de croix alignés verticalement
        board.placeToken(4, 0, new Token(Color.BLACK, Shape.CROSS));
        board.placeToken(4, 1, new Token(Color.PINK, Shape.CROSS));
        board.placeToken(4, 2, new Token(Color.BLACK, Shape.CROSS));
        board.placeToken(4, 3, new Token(Color.PINK, Shape.CROSS));

        // Vérifier si la victoire est détectée
        assertTrue(game.checkVictory(4, 2));
    }

    @Test
    public void testCheckVictory_InterruptedSequence() {
        // Préparation : placer des jetons avec une interruption
        board.placeToken(0, 1, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(1, 1, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(2, 1, new Token(Color.BLACK, Shape.CROSS)); // Interruption
        board.placeToken(3, 1, new Token(Color.PINK, Shape.CIRCLE));

        // Vérifier qu'aucune victoire n'est détectée
        assertFalse(game.checkVictory(2, 1));
    }

    @Test
    public void testCheckVictory_NoAlignment() {
        // Préparation : placer un jeton seul
        board.placeToken(1, 1, new Token(Color.PINK, Shape.CIRCLE));

        // Vérifier qu'aucune victoire n'est détectée
        assertFalse(game.checkVictory(1, 1));
    }

    @Test
    public void testCheckVictory_MixedVictory() {
        // Préparation : aligner 4 jetons identiques (couleur et forme)
        board.placeToken(0, 1, new Token(Color.BLACK, Shape.CIRCLE));
        board.placeToken(1, 1, new Token(Color.BLACK, Shape.CIRCLE));
        board.placeToken(2, 1, new Token(Color.BLACK, Shape.CIRCLE));
        board.placeToken(3, 1, new Token(Color.BLACK, Shape.CIRCLE));

        // Vérifier si la victoire est détectée
        assertTrue(game.checkVictory(2, 1));
    }

    @Test
    public void testCheckVictory_BoardEdge() {
        // Préparation : placer des jetons au bord sans alignement
        board.placeToken(0, 0, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(1, 0, new Token(Color.BLACK, Shape.CROSS));
        board.placeToken(2, 0, new Token(Color.PINK, Shape.CIRCLE));
        board.placeToken(3, 0, new Token(Color.BLACK, Shape.CROSS));

        // Vérifier qu'aucune victoire n'est détectée
        assertFalse(game.checkVictory(0, 0));
    }

    @Test
    public void testStillHasTokens_CurrentPlayerHasTokens() {
        // Vérifier que le joueur rose a encore des jetons
        assertTrue(game.stillHasTokens());
    }

    @Test
    public void testStillHasTokens_CurrentPlayerNoTokens_OpponentHasTokens() {
        // Supprimer tous les jetons du joueur actuel
        while (game.getCurrentPlayerNbTokens(Shape.CROSS) > 0) {
           game.removeTokenFromCurrentPlayer(Shape.CROSS);
        }
        while (game.getCurrentPlayerNbTokens(Shape.CIRCLE) > 0) {
            game.removeTokenFromCurrentPlayer(Shape.CIRCLE);
        }

        assertTrue(game.stillHasTokens());
    }
}