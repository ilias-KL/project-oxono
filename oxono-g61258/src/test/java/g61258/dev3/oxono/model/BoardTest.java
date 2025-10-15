package g61258.dev3.oxono.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(6); // Initialize a 6x6 board
    }

    @Test
    void testInitialize() {
        // Ensure the grid is empty except for the totems
        Totem totemX = board.getTotemX();
        Totem totemO = board.getTotemO();

        assertNotNull(totemX, "Totem X should not be null after initialization");
        assertNotNull(totemO, "Totem O should not be null after initialization");

        for (int x = 0; x < board.getSize(); x++) {
            for (int y = 0; y < board.getSize(); y++) {
                if ((x == totemX.getX() && y == totemX.getY()) ||
                        (x == totemO.getX() && y == totemO.getY())) {
                    assertNotNull(board.getToken(x, y), "Totem should be placed at its position");
                } else {
                    assertNull(board.getToken(x, y), "Cells should be empty except for totems");
                }
            }
        }
    }

    @Test
    void testPlaceToken() {
        Token token = new Token(Color.PINK, Shape.CIRCLE);
        int x = 2;
        int y = 3;

        board.placeToken(x, y, token);
        assertEquals(token, board.getToken(x, y), "Token should be placed at the specified position");
    }

    @Test
    void testPlaceTokenOnOccupiedCell() {
        Token token1 = new Token(Color.PINK, Shape.CIRCLE);
        Token token2 = new Token(Color.BLACK, Shape.CROSS);
        int x = 2;
        int y = 3;

        board.placeToken(x, y, token1);
        board.placeToken(x, y, token2);

        assertEquals(token1, board.getToken(x, y), "The first token should not be replaced");
    }

    @Test
    void testMoveTotem() {
        Totem totemX = board.getTotemX();
        int oldX = totemX.getX();
        int oldY = totemX.getY();

        int newX = oldX + 1;
        int newY = oldY;

        // Ensure the target cell is empty before moving
        if (!board.isCellEmpty(newX, newY)) {
            newX = oldX;
            newY = oldY + 1; // Try a different valid position
        }

        board.moveTotem(newX, newY, totemX);

        assertEquals(newX, totemX.getX(), "Totem X should have updated X coordinate");
        assertEquals(newY, totemX.getY(), "Totem X should have updated Y coordinate");
        assertNull(board.getToken(oldX, oldY), "Old position should be empty after moving the totem");
        assertEquals(totemX, board.getToken(newX, newY), "Totem should be at the new position");
    }


    @Test
    void testMoveTotemToOccupiedCell() {
        Totem totemX = board.getTotemX();
        Token token = new Token(Color.PINK, Shape.CIRCLE);
        int newX = totemX.getX() + 1;
        int newY = totemX.getY() + 1;

        board.placeToken(newX, newY, token);
        board.moveTotem(newX, newY, totemX);

        assertNotEquals(newX, totemX.getX(), "Totem X should not move to an occupied cell");
        assertNotEquals(newY, totemX.getY(), "Totem X should not move to an occupied cell");
    }

    @Test
    void testRemoveToken() {
        Token token = new Token(Color.BLACK, Shape.CROSS);
        int x = 3;
        int y = 4;

        board.placeToken(x, y, token);
        board.removeToken(x, y);

        assertNull(board.getToken(x, y), "Token should be removed from the specified position");
    }

    @Test
    void testIsValidPosition() {
        assertTrue(board.isValidPosition(0, 0), "(0, 0) should be a valid position");
        assertTrue(board.isValidPosition(5, 5), "(5, 5) should be a valid position");
        assertFalse(board.isValidPosition(-1, 0), "(-1, 0) should not be a valid position");
        assertFalse(board.isValidPosition(6, 6), "(6, 6) should not be a valid position");
    }

    @Test
    void testIsCellEmpty() {
        int x = 2;
        int y = 3;
        assertTrue(board.isCellEmpty(x, y), "Cell should be empty initially");

        Token token = new Token(Color.PINK, Shape.CIRCLE);
        board.placeToken(x, y, token);
        assertFalse(board.isCellEmpty(x, y), "Cell should not be empty after placing a token");
    }

    @Test
    void testGetAllEmptyCells() {
        List<int[]> emptyCells = board.getAllEmptyCells();
        assertEquals(34, emptyCells.size(), "Initially, there should be 34 empty cells on a 6x6 board with 2 totems");

        // Place a token and check the updated count
        board.placeToken(0, 0, new Token(Color.PINK, Shape.CIRCLE));
        emptyCells = board.getAllEmptyCells();
        assertEquals(33, emptyCells.size(), "After placing a token, there should be 33 empty cells");
    }

    @Test
    void testGetToken() {
        Token token = new Token(Color.PINK, Shape.CIRCLE);
        int x = 1;
        int y = 2;

        board.placeToken(x, y, token);
        assertEquals(token, board.getToken(x, y), "getToken should return the correct token at the specified position");
    }

    @Test
    void testGetSize() {
        assertEquals(6, board.getSize(), "Board size should be 6");
    }

    @Test
    void testTotemsPlacement() {
        Totem totemX = board.getTotemX();
        Totem totemO = board.getTotemO();

        assertNotNull(totemX, "Totem X should be initialized");
        assertNotNull(totemO, "Totem O should be initialized");

        assertTrue(
                totemX.getX() != totemO.getX() || totemX.getY() != totemO.getY(),
                "Totems X and O should be placed at different positions"
        );
    }

}
