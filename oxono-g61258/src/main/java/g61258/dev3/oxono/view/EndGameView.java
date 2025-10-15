package g61258.dev3.oxono.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Represents the view that displays a window at the end of the game, showing the result (winner or draw).
 * This class is responsible for presenting a message indicating the outcome of the game and providing an option to close the window.
 */
public class EndGameView {

    /**
     * Displays a window indicating the end of the game and the winner (if applicable).
     * <p>If a winner's name is provided, it will be displayed. If no winner is provided, a message indicating
     * that the game ended in a draw will be shown.</p>
     * @param winnerName the name of the winner, or null if there is no winner
     */
    public void display(String winnerName) {
        VBox endGameContainer = new VBox(10);
        endGameContainer.setPadding(new Insets(20));

        Label endGameLabel = new Label("Fin de la partie !");
        if (winnerName != null) {
            endGameLabel.setText("Le gagnant est : " + winnerName);
        } else {
            endGameLabel.setText("La partie est terminée sans gagnant.");
        }

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(event -> endGameContainer.getScene().getWindow().hide());

        endGameContainer.getChildren().addAll(endGameLabel, closeButton);
        endGameContainer.setAlignment(Pos.CENTER);

        Scene endGameScene = new Scene(endGameContainer, 300, 150);
        Stage endGameStage = new Stage();
        endGameStage.setTitle("Fin de la partie");
        endGameStage.setScene(endGameScene);
        endGameStage.show();
    }
}

