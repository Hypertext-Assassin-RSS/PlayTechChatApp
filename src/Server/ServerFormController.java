package Server;

import Login.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController {
    public TextField txtServerMessage;
    public TextArea txtServerPane;
    Socket socket =null;


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


    public void initialize() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket= new ServerSocket(5000);
                    System.out.println("Server started!.................");
                    socket = serverSocket.accept();
                    System.out.println("Client Connected!................");
                txtServerPane.appendText("\n "+"Client Connected...!"+"\n");
                while (!socket.isClosed()){
                    //read received msg
                    receiveMsg = new DataInputStream(socket.getInputStream());
                    message = receiveMsg.readUTF();
                    txtServerPane.appendText("\nClient:" + message.trim() + "\n"); //add received msg to text area
                    System.out.println(message);
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        sendMsg  = new DataOutputStream(socket.getOutputStream());
        replay = txtServerMessage.getText();
        txtServerPane.appendText("\n\t\t\t\t\t\t\t\tMe :" + replay.trim()); //add replay to text area
        sendMsg.writeUTF(replay);
        txtServerMessage.setText("");
    }
}
