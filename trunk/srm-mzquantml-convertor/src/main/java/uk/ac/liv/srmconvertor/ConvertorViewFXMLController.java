
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
import org.controlsfx.dialog.Dialogs;

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
    @FXML
    private TextField firstNameTF;
    @FXML
    private TextField lastNameTF;
    @FXML
    private TextField orgTF;

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
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        
        message.textProperty().unbind();
        message.setText("");
        
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
        if (inputFile != null) {
            fileTextField.setText(inputFile.getAbsolutePath());
            currentDirectory = inputFile.getParentFile();
        }

    }

    @FXML
    public void convertButtonActionPerformed(ActionEvent event) {
        if (fileTextField.getText().isEmpty()) {
            message.textProperty().unbind();
            message.setText("No input file! Select one before converting.");
        }
        // check contat details
        else if (!hasContactDetail()) {
            Dialogs.create()
                    .title("Contact details")
                    .message("Please input complete contact details!")
                    .showError();
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
            if (outputFile != null) {

                Parameter param = new Parameter();
                //set contact details
                param.setFirstName(firstNameTF.getText());
                param.setLastName(lastNameTF.getText());
                param.setOrg(orgTF.getText());

                currentDirectory = outputFile.getParentFile();

                MzqCreateTask mzqTask = new MzqCreateTask(fileTextField.getText(), outputFile, param);
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(mzqTask.progressProperty());

                message.textProperty().unbind();
                message.textProperty().bind(mzqTask.messageProperty());

                Thread mzqTh = new Thread(mzqTask);
                mzqTh.setDaemon(true);
                mzqTh.start();
            }
        }

    }

    void setStageAndSetupListeners(Stage stage) {
        this.stage = stage;
    }

    /**
     * Check if contact details have all input properly.
     * It will return false is any of the text filed is empty.
     *
     * @return boolean
     */
    private boolean hasContactDetail() {

        if (firstNameTF.getText().isEmpty() || lastNameTF.getText().isEmpty() || orgTF.getText().isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

}
