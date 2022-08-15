package Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClientFormController {
    public TextField txtClientMessage;
    public TextArea txtClientPane;
    Socket socket = null;

    static DataInputStream receiveMsg;

    static DataOutputStream sendMsg;

    String message = "";

    String replay = "";

    String option = "text";

    File file = null;

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


    @FXML
    void openFileChooser(MouseEvent event) {
        option = "file";
        FileChooser fileChooser = new FileChooser();
       file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            txtClientMessage.appendText(file.getAbsolutePath() + " selected");
        }
    }

    public void sendOnAction(ActionEvent actionEvent) throws IOException, InterruptedException {
        sendMsg  = new DataOutputStream(socket.getOutputStream());
        switch (option){
            case "text":
                replay = txtClientMessage.getText();
                txtClientPane.appendText("\n\t\t\t\t\t\t\t\tMe :" + replay.trim()); //add replay to text area
                sendMsg.writeUTF(replay);
                txtClientMessage.setText("");
                break;
            case "file":
                BufferedImage bufferedImage = ImageIO.read(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"jpg",byteArrayOutputStream);
                byte[] bytes = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                sendMsg.write(bytes);
                sendMsg.write(byteArrayOutputStream.toByteArray());
                Thread.sleep(120000);
                break;
        }


    }
}
