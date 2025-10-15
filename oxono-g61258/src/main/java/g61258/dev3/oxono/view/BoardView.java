package g61258.dev3.oxono.view;

import g61258.dev3.oxono.controller.GameController;
import g61258.dev3.oxono.model.*;
import g61258.dev3.oxono.utils.Observer;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Represents the view of the game board in the user interface, which displays the board's grid and manages user interactions.
 * This class is responsible for rendering the game board, updating the view when the board changes,
 * and handling interactions with the cells (such as selecting totems and placing tokens).
 * It implements the Observer interface to listen for updates from the model (Game) and reflect those changes on the UI.
 */
public class BoardView implements Observer {

    private GridPane gridPane;
    private Totem selectedTotem = null;
    private int boardSize;
    private GameInfoView gameInfoView;

    private GameController controller;

    private final int GRID_PANE_GAPE = 5;

    private final int CELL_VIEW_SET_FIT = 50;


    /**
     * Constructs a BoardView with the specified game and board model.
     * <p>This constructor initializes the GridPane, creates empty cells, and sets up the board's visual layout.</p>
     * @param boardSize the size of the board displayed
     */
    public BoardView(int boardSize) {
        this.boardSize = boardSize;
        gridPane = new GridPane();
        gridPane.setHgap(GRID_PANE_GAPE);
        gridPane.setVgap(GRID_PANE_GAPE);
    }

    /**
     * Sets the GameInfoView to be updated when the board changes.
     * @param gameInfoView the GameInfoView to update with game status
     */
    public void setGameInfoView(GameInfoView gameInfoView) {
        this.gameInfoView = gameInfoView;
    }

    /**
     * Sets the GameController to manage user input and game logic for the board.
     * @param controller the controller for handling clicks and other actions on the board
     */
    public void setController(GameController controller) {
        this.controller = controller;
        update();
    }

    /**
     * Returns the GridPane representing the board view.
     * @return the GridPane of the game board
     */
    public GridPane getGridPane() {
        return gridPane;
    }

    /**
     * Clears the selected totem, resetting the selection state.
     */
    public void clearSelectedTotem() {
        selectedTotem = null;
    }

    /**
     * Updates the board view to reflect the current game state.
     * This method is called whenever the game state changes, and it updates the visual representation
     * of the board, including updating tokens, totems, and their interactions.
     */
    @Override
    public void update() {
        updateBoardView();
    }

    /**
     * Updates the board view by clearing the previous display and rendering the current game state.
     * This method is responsible for creating the appropriate images for each cell (totem or token),
     * and setting up event handlers for user interaction.
     */
    public void updateBoardView() {
        gridPane.getChildren().clear();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                createAndConfigureCell(i, j);
            }
        }
    }

    /**
     * Creates and configures a cell at the specified row and column.
     * Depending on the presence of a token, the method determines whether the cell is empty
     * or occupied and applies the appropriate visual configuration and interactions.
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    private void createAndConfigureCell(int row, int col) {
        ImageView cellView = createImageView();
        Token token = controller.getToken(row, col);

        if (token == null) {
            configureEmptyCell(cellView, row, col);
        } else {
            configureOccupiedCell(cellView, token);
        }
        gridPane.add(cellView, col, row);
    }

    /**
     * Creates a new ImageView instance configured for a board cell.
     * The size of the ImageView is set to a standard size suitable for the board.
     * @return a configured ImageView instance
     */
    private ImageView createImageView() {
        ImageView cellView = new ImageView();
        cellView.setFitWidth(CELL_VIEW_SET_FIT);
        cellView.setFitHeight(CELL_VIEW_SET_FIT);
        return cellView;
    }

    /**
     * Configures a cell to represent an empty state.
     * Sets an empty cell image and enables or disables interactions based on the game's state.
     * @param cellView the ImageView representing the cell
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    private void configureEmptyCell(ImageView cellView, int row, int col) {
        cellView.setImage(new Image(getClass().getResource("/images/empty_cell_image.png").toString()));

        if (controller.getGameStat() == GameStat.CHOICE) {
            disableCellInteractions(cellView);
        } else {
            enableEmptyCellInteractions(cellView, row, col);
        }
    }

    /**
     * Disables all interactions for a cell, effectively making it static.
     * @param cellView the ImageView representing the cell
     */
    private void disableCellInteractions(ImageView cellView) {
        cellView.setOnMouseEntered(null);
        cellView.setOnMouseExited(null);
        cellView.setOnMouseClicked(null);
    }

    /**
     * Enables interactions for an empty cell, allowing hover effects and click handling.
     * @param cellView the ImageView representing the cell
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    private void enableEmptyCellInteractions(ImageView cellView, int row, int col) {
        cellView.setOnMouseEntered(event -> {
            ColorAdjust colorAdjust = controller.OnMouseEntered(row, col, selectedTotem, event);
            cellView.setEffect(colorAdjust);
        });

        cellView.setOnMouseExited(event -> cellView.setEffect(null));

        cellView.setOnMouseClicked(event -> {
            controller.EmptyCellOnMouseClicked(row, col, selectedTotem, event);
            selectedTotem = null;
        });
    }

    /**
     * Configures a cell to represent a token or totem that occupies it.
     * Delegates to specific methods based on the token type.
     * @param cellView the ImageView representing the cell
     * @param token the token occupying the cell
     */
    private void configureOccupiedCell(ImageView cellView, Token token) {
        if (token instanceof Totem) {
            configureTotemCell(cellView, (Totem) token);
        } else {
            configureTokenCell(cellView, token);
        }
    }

    /**
     * Configures a cell to display a totem image and handles interactions for selecting and deselecting the totem.
     * @param cellView the ImageView representing the cell
     * @param totem the totem occupying the cell
     */
    private void configureTotemCell(ImageView cellView, Totem totem) {
        String imagePath = "/images/totem_blue_" + totem.getShape().toString().toLowerCase() + ".png";
        cellView.setImage(new Image(getClass().getResource(imagePath).toString()));

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.4);

        if (totem == selectedTotem) {
            cellView.setEffect(colorAdjust);
        }

        cellView.setOnMouseClicked(event -> {
            if (selectedTotem == null && controller.hasTokenShape(totem.getShape())) {
                selectedTotem = totem;
                cellView.setEffect(colorAdjust);
            } else if (selectedTotem == totem) {
                selectedTotem = null;
                cellView.setEffect(null);
            }
        });
    }


    /**
     * Configures a cell to display a token image without interactions.
     * @param cellView the ImageView representing the cell
     * @param token the token occupying the cell
     */
    private void configureTokenCell(ImageView cellView, Token token) {
        String imagePath = "/images/token_" + token.getColor().toString().toLowerCase() + "_" + token.getShape().toString().toLowerCase() + ".png";
        cellView.setImage(new Image(getClass().getResource(imagePath).toString()));
    }


    /**
     * Grays out the entire board by applying a color filter to each cell.
     * This effect can be used to indicate that the board is disabled or to simulate a game pause.
     */
    public void grayOutBoard() {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ImageView) {
                ImageView cellView = (ImageView) node;
                // Appliquer un filtre de couleur grise et réduire l'opacité
                ColorAdjust grayEffect = new ColorAdjust();
                grayEffect.setSaturation(-1); // Désature pour rendre gris
                grayEffect.setBrightness(-0.4); // Réduit la luminosité

                cellView.setEffect(grayEffect);
            }
        }
    }
}


