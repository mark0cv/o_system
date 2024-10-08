package gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import system.ShellCommands;

import java.io.File;

public class Controller {
    public static TextArea ta;

    private boolean awaitingConfirmation = false;
    private String directory;

    @FXML
    TextField textField;
    @FXML
    TextArea textArea=new TextArea();

    static String input;
    ShellCommands sh=new ShellCommands();


    private void handleCommand(TextField textField1,TextArea textArea1)
    {
        if (!textField1.getText().startsWith("rmdir") && !textField1.getText().startsWith("rd"))
        {   String text = sh.getCommand(textField1.getText());
            textArea1.appendText(">>"+textField1.getText()+"\n");
            textField1.clear();


            ta.appendText(text + "\n");
        }else{
            String text = textField1.getText().substring(6);

            File dir=new File(ShellCommands.getCurrentDir()+'\\'+text);
            if(dir.exists()) {
                ta.appendText(">>are you sure you want to remove " + text + "[Y/N]\n");
                textField1.clear();
                awaitingConfirmation = true;
                directory = text;
            }else {
                ta.appendText("directory does not exist.\n");
                textField1.clear();
            }


        }

    }

    private void handleConfirmation(TextField textField1,TextArea textArea1)
    {
        if(textField1.getText().equalsIgnoreCase("y"))
        {
            ta.appendText(">>y\n");
            textField1.clear();
            String s = sh.getCommand("rmdir " + directory);
            ta.appendText(">>"+s+"\n");
        }else if(textField1.getText().equalsIgnoreCase("n"))
        {
            ta.appendText(">>y\n");
            textField1.clear();
            ta.appendText(">>canceling deleting file\n");
        }else{
            textField1.clear();
            ta.appendText(">>incorect input!\n");
        }

        directory = "";
        awaitingConfirmation = false;
    }

    @FXML
    public void initialize() {
        ta=textArea;

        textField.setOnAction(e -> {
            if (!awaitingConfirmation) {
                handleCommand(textField, textArea);
            } else {
                handleConfirmation(textField, textArea);
            }
        });



    }

    public static void updateTa(String s)
    {
        ta.appendText(s+"\n");
    }

    public static void clear(){
        ta.clear();
    }
}
