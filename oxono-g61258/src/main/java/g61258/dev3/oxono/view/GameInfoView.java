package g61258.dev3.oxono.view;

import g61258.dev3.oxono.controller.GameController;
import g61258.dev3.oxono.model.GameStat;
import g61258.dev3.oxono.model.Shape;
import g61258.dev3.oxono.utils.Observer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Represents the view displaying information about the current game state,
 * including player tokens, remaining cells, and actions for undo, redo, quitting, etc.
 * This view is responsible for updating the UI based on game changes and handling user actions
 * like abandoning the game or making a move.
 */
public class GameInfoView implements Observer {

    private GameController controller;
    private BoardView boardView;

    private VBox infoContainer;
    private Label crossCountLabel;
    private Label circleCountLabel;
    private Label emptyCellCountLabel;
    private Label currentPlayerLabel;
    private Button abandonButton;
    private Button undoButton;
    private Button redoButton;
    private Button quitButton;

    private EndGameView endGameView; // End game view to display the end result

    /**
     * Constructs a new GameInfoView instance.
     * @param boardView the view of the game board
     */
    public GameInfoView(BoardView boardView) {
        this.boardView = boardView;
        this.endGameView = new EndGameView(); // Initialize end game view
        initializeUI(); // Setup the user interface
    }

    /**
     * Initializes the UI components and layout for the game information view.
     */
    private void initializeUI() {
        infoContainer = new VBox(10);
        infoContainer.setPadding(new Insets(10));

        // Initialize the labels for displaying game information
        crossCountLabel = new Label();
        circleCountLabel = new Label();
        emptyCellCountLabel = new Label();
        currentPlayerLabel = new Label();

        // Initialize the buttons for game actions
        abandonButton = new Button("Abandon");
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");
        quitButton = new Button("Quitter");

        // Set the actions for each button
        setButtonActions();

        // Arrange buttons in a horizontal layout
        HBox buttonContainer = new HBox(10, quitButton, abandonButton, undoButton, redoButton/*, pressButton*/);
        buttonContainer.setAlignment(Pos.CENTER);

        // Add all components to the container
        infoContainer.getChildren().addAll(
                crossCountLabel,
                circleCountLabel,
                emptyCellCountLabel,
                currentPlayerLabel,
                buttonContainer
        );
    }

    /**
     * Sets the actions for buttons such as abandon, undo, redo, press, and quit.
     */
    private void setButtonActions() {
        abandonButton.setOnAction(event -> {
            disableActionButtons();
            controller.abandonGame();
            update();
            boardView.grayOutBoard();
            endGameView.display(controller.getWinnerName());
        });

        undoButton.setOnAction(event -> {
            if (controller.getGameStat() == GameStat.MOVE){
                    controller.switchPlayer();
                    controller.undo();
                    controller.undo();
                    controller.switchPlayer();
                    controller.undo();
                } else {
                controller.undo();
            }
            update();
        });

        redoButton.setOnAction(event -> {
            if (controller.getGameStat() == GameStat.INSERT) {
                controller.redo();
                controller.switchPlayer();
                controller.redo();
                controller.redo();
                controller.switchPlayer();
            } else {
                controller.redo();
            }
            update();
        });

        quitButton.setOnAction(event -> Platform.exit());
    }

    /**
     * Disables action buttons when the game is over.
     */
    public void disableActionButtons() {
        abandonButton.setDisable(true);
        undoButton.setDisable(true);
        redoButton.setDisable(true);
    }

    /**
     * Returns the container holding the game information UI components.
     * @return the VBox containing the game information UI
     */
    public VBox getInfoContainer() {
        return infoContainer;
    }

    /**
     * Updates the game information labels with the latest data from the game.
     */
    @Override
    public void update() {
        crossCountLabel.setText("Cross tokens remaining: " +
                controller.getCurrentPlayerName() + " : " +
                controller.getCurrentPlayerTokenCount(Shape.CROSS) +
                " | " + controller.getOpponentPlayerName() + " " +
                controller.getOpponentPlayerTokenCount(Shape.CROSS));

        circleCountLabel.setText("Circle tokens remaining: " +
                controller.getCurrentPlayerName() + " : " +
                controller.getCurrentPlayerTokenCount(Shape.CIRCLE) +
                " | " + controller.getOpponentPlayerName() + " " +
                controller.getOpponentPlayerTokenCount(Shape.CIRCLE));

        emptyCellCountLabel.setText("Empty cells: " + controller.getEmptyCellCount());
        currentPlayerLabel.setText("Current player: " + controller.getCurrentPlayerName());
    }


    /**
     * Sets the controller for this view and updates the view based on the current game state.
     * @param controller the GameController to be used for this view
     */
    public void setController(GameController controller) {
        this.controller = controller;
        update();
    }

    /**
     * Return the view that displays the end game screen.
     * The EndGameView contains the final results and options for restarting or exiting the game.
     * @return the EndGameView instance
     */
    public EndGameView getEndGameView() {
        return endGameView;
    }
}
