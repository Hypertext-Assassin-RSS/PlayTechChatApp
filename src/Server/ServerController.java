package Server;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerController {


    static ArrayList c_os;
    static ArrayList<String> users;
    static Socket cs;

    @FXML
    private JFXButton start;

    @FXML
    private JFXButton o_user;

    @FXML
    private JFXButton clear;

    @FXML
    private ScrollPane jScrollPane1;

    @FXML
    private TextArea area;

    public void startAction(ActionEvent actionEvent) {
      Thread starter =   new Thread(new server_listening());
      starter.start();
      area.appendText("SERVER STARTED LISTENING...\n");
    }

    public void getUsers(ActionEvent actionEvent) {
        area.appendText("\nONLINE USERS:\n");
        int i=1;
        for(String ou:users)
        {
            area.appendText(i+". "+ou+"\n");
            i++;
        }
        if(i==1)
            area.appendText("CURRENTLY NO ONE IS ONLINE.\n");
    }

    public void clearConsole(ActionEvent actionEvent) {
        area.setText("");
    }


    public class server_listening implements Runnable{
        @Override
        public void run()
        {
            c_os=new ArrayList();
            users=new ArrayList();

            try
            {
                ServerSocket ss=new ServerSocket(8000);
                while(true)
                {
                    cs=ss.accept();
                    PrintWriter w=new PrintWriter(cs.getOutputStream());
                    c_os.add(w);
                    Thread l=new Thread(new client_handler(cs,w));
                    l.start();
                    area.appendText("\nGOT A CONNECTION.\n");
                }
            }
            catch(IOException e)
            {
                area.appendText("\nAN ERROR OCCURS WHILE ESTABLISHING A CONNECTION.\n");
            }
        }
    }

    public class client_handler implements Runnable{
        BufferedReader r;
        PrintWriter client;

        public client_handler(Socket cs,PrintWriter w)
        {
            client=w;
            try
            {
                InputStreamReader in=new InputStreamReader(cs.getInputStream());
                r= new BufferedReader(in);
            }
            catch(Exception e)
            {
                area.appendText("\nAN ERROR OCCURS WHILE READING FROM INPUTSTREAM.\n");
            }
        }

        @Override
        public void run() {
            int c=0;
            String msg;
            String[] i;

            try
            {
                while((msg=r.readLine())!=null)
                {
                    i=msg.split(":");
                    area.appendText("\nRECEIVED:"+msg+"\n");

                    switch (i[2]) {
                        case "connect":
                            for(String u:users)
                            {
                                if(i[0].equals(u))
                                    c=1;
                                else
                                    c=0;
                            }
                            if(c==0) {
                                tell_everyone(msg);
                                users.add(i[0]);
                                area.appendText("SERVER HAS ADDED "+i[0]+" TO THE CHAT USERS LIST.\n");
                            }
                            else
                                tell_everyone("CU");
                            break;
                        case "disconnect":
                            tell_everyone(msg);
                            users.remove(i[0]);
                            c_os.remove(client);
                            area.appendText("SERVER HAS REMOVED "+i[0]+" FROM THE CHAT USERS LIST.\n");
                            break;
                        case "chat":
                            tell_everyone(msg);
                            break;
                        default:
                            break;
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println(e.getLocalizedMessage());
                area.appendText("LOST CONNECTION.\n");
                c_os.remove(client);
            }
        }
    }

    public void tell_everyone(String msg){
        Iterator it=c_os.iterator();
        area.appendText("SENDING: "+msg+"\n");

        while(it.hasNext())
        {
            try
            {
                PrintWriter w=(PrintWriter) it.next();
                w.println(msg);
                w.flush();
            }
            catch(Exception e)
            {
                area.appendText("\nAN ERROR OCCURS WHILE TELLING EVERYONE.\n");
            }
        }
    }
}
