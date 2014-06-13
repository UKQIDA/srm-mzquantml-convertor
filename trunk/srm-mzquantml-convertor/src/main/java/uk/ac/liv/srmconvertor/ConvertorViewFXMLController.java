
package uk.ac.liv.srmconvertor;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ConvertorViewFXMLController implements Initializable {

    @FXML
    private TextField fileTextField;
    @FXML
    private Button selectButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button convertButton;
    @FXML
    private Text message;

    private Stage stage;
    private static File currentDirectory;
    private static File inputFile;
    private static File outputFile;

    public ConvertorViewFXMLController() {
        this.progressBar = new ProgressBar();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        fileTextField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                File f = new File(newValue);
                if (f.isFile()) {
                    currentDirectory = f.getParentFile();
                }
            }

        });
    }

    @FXML
    public void selectButtonActionPerformed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Skyline CSV file");
        if (currentDirectory == null) {
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home")));
        }
        else {
            fileChooser.setInitialDirectory(currentDirectory);
        }

        //... Applying file extension filters ...//
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV (Comma delimited)(*.csv)", "*.csv"));

        inputFile = fileChooser.showOpenDialog(stage);
        fileTextField.setText(inputFile.getAbsolutePath());
        currentDirectory = inputFile.getParentFile();

    }

    @FXML
    public void convertButtonActionPerformed(ActionEvent event) {
        if (fileTextField.getText().isEmpty()) {
            message.textProperty().unbind();
            message.setText("No input file! Select one before converting.");
        }
        else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save mzq file");
            if (currentDirectory == null) {
                fileChooser.setInitialDirectory(
                        new File(System.getProperty("user.home")));
            }
            else {
                fileChooser.setInitialDirectory(currentDirectory);
            }

            //... Applying file extension filters ...//
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("MzQuantML (*.mzq)", "*.mzq"));

            outputFile = fileChooser.showSaveDialog(stage);
            currentDirectory = outputFile.getParentFile();

            MzqCreateTask mzqTask = new MzqCreateTask(fileTextField.getText(), outputFile);
            progressBar.progressProperty().unbind();
            progressBar.progressProperty().bind(mzqTask.progressProperty());

            message.textProperty().unbind();
            message.textProperty().bind(mzqTask.messageProperty());

            Thread mzqTh = new Thread(mzqTask);
            mzqTh.setDaemon(true);
            mzqTh.start();
        }

    }

    void setStageAndSetupListeners(Stage stage) {
        this.stage = stage;
    }

}
