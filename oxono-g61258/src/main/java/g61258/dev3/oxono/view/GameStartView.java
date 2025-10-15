package g61258.dev3.oxono.view;

import g61258.dev3.oxono.controller.GameController;
import g61258.dev3.oxono.model.Board;
import g61258.dev3.oxono.model.Game;
import g61258.dev3.oxono.model.OpponentStrategyLevel2;
import g61258.dev3.oxono.model.RandomOpponentStrategy;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Represents the view for starting a new game, allowing the user to configure the game settings
 * such as the board size and the AI difficulty level before launching the game.
 */
public class GameStartView extends VBox {

    private GameController controller;

    private GameInfoView gameInfoView;

    private BoardView boardView;

    /**
     * Constructs a new GameStartView instance for configuring the game.
     */
    public GameStartView() {

        // Initialize layout and style
        setupLayout();

        // Create UI components
        Text title = new Text("Configurer la partie");
        ComboBox<Integer> boardSizeComboBox = createBoardSizeComboBox();
        ComboBox<Integer> aiLevelComboBox = createAILevelComboBox();
        Button startButton = createStartButton();

        // Add event handlers
        addComboBoxHandlers(boardSizeComboBox, aiLevelComboBox, startButton);
        addStartButtonHandler(boardSizeComboBox, aiLevelComboBox, startButton);

        // Add components to the view
        getChildren().addAll(title, boardSizeComboBox, aiLevelComboBox, startButton);
    }

    /**
     * Sets up the layout and style for the GameStartView.
     */
    private void setupLayout() {
        setSpacing(10);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);
    }

    /**
     * Creates a ComboBox for selecting the board size.
     * @return the ComboBox for board size selection
     */
    private ComboBox<Integer> createBoardSizeComboBox() {
        ComboBox<Integer> boardSizeComboBox = new ComboBox<>();
        boardSizeComboBox.getItems().addAll(4, 6, 8);
        boardSizeComboBox.setPromptText("Taille du plateau");
        return boardSizeComboBox;
    }

    /**
     * Creates a ComboBox for selecting the AI level.
     * @return the ComboBox for AI level selection
     */
    private ComboBox<Integer> createAILevelComboBox() {
        ComboBox<Integer> aiLevelComboBox = new ComboBox<>();
        aiLevelComboBox.getItems().addAll(0, 1);
        aiLevelComboBox.setPromptText("Niveau de l'ordinateur");
        return aiLevelComboBox;
    }

    /**
     * Creates the start button for initiating the game.
     * @return the start Button
     */
    private Button createStartButton() {
        Button startButton = new Button("Démarrer la partie");
        startButton.setDisable(true); // Disabled by default
        return startButton;
    }

    /**
     * Adds event handlers for enabling the start button based on input selection.
     * @param boardSizeComboBox the ComboBox for board size selection
     * @param aiLevelComboBox the ComboBox for AI level selection
     * @param startButton the Button to start the game
     */
    private void addComboBoxHandlers(ComboBox<Integer> boardSizeComboBox, ComboBox<Integer> aiLevelComboBox, Button startButton) {
        boardSizeComboBox.setOnAction(event -> checkInputs(boardSizeComboBox, aiLevelComboBox, startButton));
        aiLevelComboBox.setOnAction(event -> checkInputs(boardSizeComboBox, aiLevelComboBox, startButton));
    }

    /**
     * Adds an event handler for the start button to configure and start the game.
     * @param boardSizeComboBox the ComboBox for board size selection
     * @param aiLevelComboBox the ComboBox for AI level selection
     * @param startButton the Button to start the game
     */
    private void addStartButtonHandler(ComboBox<Integer> boardSizeComboBox, ComboBox<Integer> aiLevelComboBox, Button startButton) {
        startButton.setOnAction(event -> {
            int boardSize = boardSizeComboBox.getValue();
            int aiLevel = aiLevelComboBox.getValue();

            controller.configureGame(boardSize, aiLevel);
            initializeViews(boardSize);
            setSceneWithLayout(boardSize);
        });
    }

    /**
     * Initializes the BoardView and GameInfoView for the game.
     * @param boardSize the size of the game board
     */
    private void initializeViews(int boardSize) {
        boardView =  new BoardView(boardSize);
        gameInfoView = new GameInfoView(boardView);

        boardView.setGameInfoView(gameInfoView);
        gameInfoView.setController(controller);
        boardView.setController(controller);

        controller.addObserver(boardView);
        controller.addObserver(gameInfoView);
    }

    /**
     * Sets the scene with the main game layout.
     * @param boardSize the size of the game board
     */
    private void setSceneWithLayout(int boardSize) {
        BorderPane mainLayout = createGameLayout(boardView, gameInfoView, boardSize);
        Scene gameScene = new Scene(mainLayout, boardSize * 60 + 100, boardSize * 60 + 200);
        controller.setScene(gameScene);
    }

    /**
     * Creates the main layout for the game, positioning the board and game info view.
     * @param boardView the board view for displaying the game board
     * @param gameInfoView the game info view displaying game-related information
     * @param boardSize the size of the game board
     * @return the main layout as a BorderPane
     */
    private BorderPane createGameLayout(BoardView boardView, GameInfoView gameInfoView, int boardSize) {
        // Container to center the board
        StackPane boardContainer = new StackPane(boardView.getGridPane());
        StackPane.setAlignment(boardView.getGridPane(), Pos.TOP_CENTER);

        // Main layout with the board at the top and the game info at the center
        BorderPane mainLayout = new BorderPane();
      //  mainLayout.setTop(boardContainer);
      //  mainLayout.setCenter(gameInfoView.getInfoContainer());
        mainLayout.setTop(gameInfoView.getInfoContainer());
        mainLayout.setCenter(boardContainer);
        BorderPane.setMargin(boardContainer, new Insets(30)); // Margins around the board

        return mainLayout;
    }

    /**
     * Checks if both inputs (board size and AI level) are selected and enables or disables the start button.
     * @param boardSizeComboBox the ComboBox for selecting the board size
     * @param aiLevelComboBox the ComboBox for selecting the AI level
     * @param startButton the button to start the game
     */
    private void checkInputs(ComboBox<Integer> boardSizeComboBox, ComboBox<Integer> aiLevelComboBox, Button startButton) {
        startButton.setDisable(boardSizeComboBox.getValue() == null || aiLevelComboBox.getValue() == null);
    }

    /**
     * Sets the controller for this view and updates the view accordingly.
     * @param controller the GameController to be used for this view
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Returns the BoardView associated with this start view.
     * @return the BoardView for the current game
     */
    public BoardView getBoardView() {
        return boardView;
    }

    /**
     * Returns the GameInfoView associated with this start view.
     * The GameInfoView provides information about the game's progress,
     * including the current player, remaining tokens, and other statistics.
     * @return the GameInfoView for the current game
     */
    public GameInfoView getGameInfoView() {
        return gameInfoView;
    }
}

