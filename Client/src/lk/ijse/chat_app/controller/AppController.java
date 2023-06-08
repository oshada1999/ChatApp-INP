package lk.ijse.chat_app.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.chat_app.service.Client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class AppController {
    public AnchorPane context;
    public AnchorPane ap_bkImage;
    public ScrollPane sp_main;
    public VBox vbox_message;
    public TextField txt_message;
    public FontAwesomeIconView btn_sent;
    public FontAwesomeIconView btn_sent_V;
    private Client client;
    private String clientUserName;
    File file;
    String path = "";
    public static boolean isImageChoose = false;

    public void initialize(){
        this.clientUserName=MainFormController.nameList.get(MainFormController.nameList.size()-1);

        try {
            System.out.println("name :"+MainFormController.nameList.get(MainFormController.nameList.size()-1));

            this.client=new Client(new Socket("localhost",4000),clientUserName);
            System.out.println("Client connected to group!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        vbox_message.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double)newValue);
            }
        });

        client.receiveMessage(vbox_message);

    }

    public void setClientUserName(String clientUserName){
        this.clientUserName=clientUserName;
    }
    public void txt_messageOnAction(MouseEvent actionEvent) {
        btn_sentOnAction(actionEvent);
    }

    public void btn_sentOnAction(MouseEvent actionEvent) {
        String messageToSent=txt_message.getText();

        /** ensure to text field not empty*/
        if(!messageToSent.isEmpty()){

            HBox hBox=new HBox();
            hBox.setAlignment(Pos.CENTER_RIGHT);
            hBox.setPadding(new Insets(5,5,5,10));

            Text text=new Text(messageToSent);
            TextFlow textFlow=new TextFlow(text);

            textFlow.setStyle("-fx-color: rgb(239,242,255);"+
                    "-fx-background-color: #024959;" +
                    "-fx-background-radius: 20px;");

            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.color(0.934,0.945,0.996));

            hBox.getChildren().add(textFlow);
            vbox_message.getChildren().add(hBox);

            client.sentMessage(clientUserName+": "+messageToSent);
            txt_message.clear();
        }
    }

    public static void receiveMessage(String receivedMessage,VBox vbox){
        HBox hBox=new HBox();
        hBox.setPadding(new Insets(5,5,5,10));
        Text text=new Text(receivedMessage);
        TextFlow textFlow=new TextFlow(text);

        if(receivedMessage.startsWith("*")){
            hBox.setAlignment(Pos.CENTER);
            textFlow.setStyle("-fx-background-color: rgb(243,172,157);" +
                    "-fx-background-radius: 20px;");
        }else {
            hBox.setAlignment(Pos.CENTER_LEFT);
            textFlow.setStyle("-fx-background-color: rgb(233,233,235);" +
                    "-fx-background-radius: 20px;");

        }

        textFlow.setPadding(new Insets(5,10,5,10));
        //text.setFill(Color.color(0.934,0.945,0.996));

        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hBox);
            }
        });
    }

    public void btn_sent_VOnAction(MouseEvent actionEvent) {

    }

    public void btnImageChooserOnAction(MouseEvent mouseEvent) {
// get the file selected
        FileChooser chooser = new FileChooser();
        Stage stage = new Stage();
        file = chooser.showOpenDialog(stage);

        if (file != null) {
//            dataOutputStream.writeUTF(file.getPath());
            path = file.getPath();
            System.out.println("selected");
            System.out.println(file.getPath());

            isImageChoose = true;
        }
        client.sentImage(path);

    }
}
