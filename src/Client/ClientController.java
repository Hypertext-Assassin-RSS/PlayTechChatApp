package Client;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.Socket;

public class ClientController {
    public TextField txtUser;
    @FXML
    private TextField ua;

    @FXML
    private Label status;

    @FXML
    private JFXButton CU;

    @FXML
    private JFXButton clear;

    @FXML
    private JFXButton disconnect;

    @FXML
    private JFXButton connect;

    @FXML
    private TextField field;

    @FXML
    private JFXButton send;

    @FXML
    private ScrollPane jScrollPane1;

    @FXML
    private TextArea area;

    Boolean c = false;
    Socket s;
    BufferedReader r;
    PrintWriter w;
    static int j = -1;

    String username = "";

    File file = null;

    private double xOffset = 0;
    private double yOffset = 0;

    public void login(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Client.fxml"));
        Stage stage = new Stage();

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });


        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.UNDECORATED);
        username = txtUser.getText();
        System.out.println("User :"+username);
        stage.show();
    }

    public void thread_listening() {
        Thread msgReader = new Thread(new msgReader());
        msgReader.start();
    }

    public void sendAction(ActionEvent actionEvent) {
        try {
            w.println(ua.getText() + ":" + field.getText() + ":chat");
            field.setText("");
            w.flush();
        } catch (Exception e) {
            area.appendText("\nMESSAGE NOT SENT.\n");
        }
    }

    public void connectToServer(ActionEvent actionEvent) {
        if (c == false) {
            ua.setEditable(false);
            try {
                s = new Socket("localhost", 8000);
                InputStreamReader in = new InputStreamReader(s.getInputStream());
                r = new BufferedReader(in);
                w = new PrintWriter(s.getOutputStream());
                w.println(ua.getText() + ":has connected.:connect");
                System.out.println(ua.getText());
                status.setText("CONNECTED");
                w.flush();
                c = true;
            } catch (Exception e) {
                area.appendText("\nAN ERROR OCCURS WHILE CONNECTING.\n");
                ua.setEditable(true);
            }
            thread_listening();
        } else
            area.appendText("\nYOU ARE ALREADY CONNECTED.\n");
    }

    public void disconnectFromServer(ActionEvent actionEvent) {
        try {
            w.println(ua.getText() + ":has disconnected.:disconnect");
            w.flush();
            area.appendText("DISCONNECTED.\n");
            ua.setEditable(true);
            ua.setText("");
            status.setText("DISCONNECTED");
            c = false;
        } catch (Exception e) {
            area.appendText("\nAN ERROR OCCURS WHILE DISCONNECTING.\n");
        }
    }

    @FXML
    void close(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();


        ActionEvent actionEvent = new ActionEvent();
        disconnectFromServer(actionEvent);
    }


    @FXML
    void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    public void openFileChooser(MouseEvent mouseEvent) {
        //option = "file";
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            field.appendText(file.getAbsolutePath() + ", selected");
        }
    }


    public class msgReader implements Runnable {

        @Override
        public void run() {
            String[] i;
            String stream;

            try {
                while ((stream = r.readLine()) != null) {
                    i = stream.split(":");
                    if (!stream.equals("CU")) {
                        switch (i[2]) {
                            case "connect":
                                status.setText("CONNECTED");
                                area.appendText(i[0] + ": " + i[1] + "\n");
                                break;
                            case "disconnect":
                                area.appendText(i[0] + ": " + i[1] + "\n");
                                area.appendText(i[0] + " IS NOW OFFLINE.\n");
                                break;
                            case "chat":
                                area.appendText(i[0] + ":" + i[1] + "\n");
                                break;
                            default:
                                break;
                        }
                    } else
                        status.setText("CHANGE USERNAME(Clear User)");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
