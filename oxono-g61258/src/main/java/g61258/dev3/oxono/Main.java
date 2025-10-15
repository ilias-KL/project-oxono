package g61258.dev3.oxono;



import g61258.dev3.oxono.controller.GameController;
import g61258.dev3.oxono.model.Board;
import g61258.dev3.oxono.model.Game;
import g61258.dev3.oxono.view.GameStartView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialisation de la vue
        GameStartView view = new GameStartView();

        // Initialisation du contrôleur
        GameController controller = new GameController(view);
        controller.setPrimaryStage(primaryStage);

        view.setController(controller);

        // Configuration de la scène
        Scene scene = new Scene(view, 300, 200);
        primaryStage.setTitle("Oxono");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
