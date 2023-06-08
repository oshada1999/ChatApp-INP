package lk.ijse.chat_app.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;

public class MainFormController {
    public TextField txt_userName;
    public JFXButton btn_start;
    private double x,y;

    public static ArrayList<String> nameList = new ArrayList<>();

    public void txt_userNameOnAction(ActionEvent actionEvent) {
        btn_startOnAction(actionEvent);
    }

    public void btn_startOnAction(ActionEvent actionEvent) {
        if (txt_userName.getText().equals("")) {
            new Alert(Alert.AlertType.ERROR, "Enter Your Name").show();
            txt_userName.requestFocus();
        } else {
            nameList.add(txt_userName.getText());

            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("../view/App.fxml"));
            Parent load = null;
            try {
                load = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Stage stage = new Stage();
            stage.setTitle("Group Chat");
            stage.setScene(new Scene(load));
            stage.centerOnScreen();
            //stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            load.setOnMousePressed(event -> {
                x=event.getSceneX();
                y=event.getSceneY();
            });

            load.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
            });
            stage.show();
            txt_userName.clear();
        }
    }

}
