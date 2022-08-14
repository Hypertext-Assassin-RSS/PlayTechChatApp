package Client;

import Login.LoginController;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientFormController {
    public TextField txtClientMessage;
    public TextArea txtClientPane;
    Socket socket = null;

    static DataInputStream receiveMsg;

    static DataOutputStream sendMsg;

    String message = "";

    String replay = "";


    @FXML
    void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @FXML
    void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }



    public void initialize() throws IOException {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 5000);
                //read received msg
                receiveMsg = new DataInputStream(socket.getInputStream());

                while (!socket.isClosed()){
                    message = receiveMsg.readUTF();
                    txtClientPane.appendText("\nServer: " + message.trim() + "\n"); //add received msg to text area
                    System.out.println(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException {
        sendMsg  = new DataOutputStream(socket.getOutputStream());
        replay = txtClientMessage.getText();
        txtClientPane.appendText("\n\t\t\t\t\t\t\t\tMe :" + replay.trim()); //add replay to text area
        sendMsg.writeUTF(replay);
        txtClientMessage.setText("");
    }
}
