
package uk.ac.liv.srmconvertor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage stage;
    private GridPane root;
    private ConvertorViewFXMLController controller;

    @Override
    public void start(Stage stage) {

        try {
            this.stage = stage;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ConvertorView.fxml"));

            root = (GridPane) loader.load();
            controller = loader.getController();
            //controller.setStageAndSetupListeners(stage);

            Scene scene = new Scene(root, 600, 300);
            //scene.getStylesheets().add("styles/convertorview.css");

            this.stage.setResizable(false);
            this.stage.setTitle("SRM mzQuantML Convertor (version 1.0)");
            this.stage.setScene(scene);
            this.stage.show();
        }
        catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
