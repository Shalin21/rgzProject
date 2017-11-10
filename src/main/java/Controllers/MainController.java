package Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by admin on 09.11.17.
 */
public class MainController {
    @FXML
    private MenuItem newFileMenu;

    @FXML
    private MenuItem openFile;

    @FXML
    private MenuItem saveFile;

    @FXML
    private TextArea textArea;
    @FXML
    private Label fileName;
    @FXML
            private AnchorPane anchorPane;
    String filePath = new String();
    String oldText = new String();
    String newText = new String();
    public void initialize(){
        fileName.setText("");
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                newText = newValue;
            }
        });
        anchorPane.setOnDragOver(event -> {mouseDragOver(event);});


        anchorPane.setOnDragDropped(event -> {mouseDragDropped(event);});
    }
    @FXML
    void newFileMenu_Click(ActionEvent event) {
        if(fileName.getText().length()>0){
            if(oldText.equals(newText)){
                textArea.setText("");
                fileName.setText("");
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Новый файл");
                String s = "Вы хотите сохранить изменения в текущем файле?";
                alert.setContentText(s);

                Optional<ButtonType> result = alert.showAndWait();

                if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
                    write(filePath, newText);
                    textArea.setText("");
                    fileName.setText("");
                }
                else
                {
                    textArea.setText("");
                    fileName.setText("");
                }

            }
        }
    }

    @FXML
    void openFileMenu_Click(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showOpenDialog(fileName.getScene().getWindow());
        try {
            exists(file.getAbsolutePath());
            filePath = file.getAbsolutePath();
            textArea.setText(read(file.getAbsolutePath()));
            fileName.setText(file.getName());
            oldText = textArea.getText();
        }
        catch (FileNotFoundException ex){

        }



    }

    @FXML
    void saveFileMenu_Click(ActionEvent event) {
        if(fileName.getText().length()>0){
            write(filePath, textArea.getText());
            oldText=textArea.getText();
        }
        else {
            FileChooser fileChooser = new FileChooser();

            //Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File file = fileChooser.showOpenDialog(fileName.getScene().getWindow());
            write(file.getAbsolutePath(), textArea.getText());
        }
    }
    private static void exists(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists()){
            throw new FileNotFoundException(file.getName());
        }
    }
    public static String read(String fileName) throws FileNotFoundException {
        //Этот спец. объект для построения строки
        StringBuilder sb = new StringBuilder();

        exists(fileName);

        try {
            //Объект для чтения файла в буфер
            BufferedReader in = new BufferedReader(new FileReader( fileName));
            try {
                //В цикле построчно считываем файл
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                //Также не забываем закрыть файл
                in.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        //Возвращаем полученный текст с файла
        return sb.toString();
    }
    public static void write(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст у файл
                out.print(text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void mouseDragDropped(final DragEvent e) {
        final Dragboard db = e.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            // Only get the first file from the list
            final File file = db.getFiles().get(0);
            try {

                textArea.setText(read(file.getAbsolutePath()));
                filePath = file.getAbsolutePath();
                fileName.setText(file.getName());
            } catch (FileNotFoundException ex) {
            }
        }

        e.setDropCompleted(success);
        e.consume();
        }



    private  void mouseDragOver(final DragEvent e) {
        final Dragboard db = e.getDragboard();
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".txt");


        if (db.hasFiles()) {
            if (isAccepted) {

                e.acceptTransferModes(TransferMode.COPY);
            }
        } else {
            e.consume();
        }
    }
}
