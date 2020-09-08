package ru.spliterash.musicbox.fx;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@SuppressWarnings("unused")
public class FxStart extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane pane = new StackPane();
        primaryStage.setWidth(800);
        primaryStage.setHeight(500);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Привет))");
        VBox box = new VBox();
        Label label = new Label("It minecraft plugin");
        box.getChildren().add(label);
        Label uTitle = new Label("not application");
        uTitle.setStyle("-fx-font-size: 25px");
        box.getChildren().add(uTitle);
        box.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-size: 40px");
        TextFlow flow = new TextFlow();
        flow.setStyle("-fx-font-size: 15px;-fx-padding: 15px;");
        Text line = new Text("");
        flow.getChildren().add(line);
        box.getChildren().add(flow);
        //Заполнитель
        {
            Region region = new Region();
            VBox.setVgrow(region, Priority.ALWAYS);
            box.getChildren().add(region);
        }
        //Линк на меня
        {
            Label info = new Label("Plugin author");

            {
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                hBox.setStyle("-fx-padding: 15px;");
                hBox.getChildren().add(info);
                box.getChildren().add(hBox);
            }
            info.setAlignment(Pos.CENTER_RIGHT);
            info.setUnderline(true);
            info.setStyle("-fx-cursor: hand;-fx-fill: #bdbdbd");
            info.setOnMouseClicked(e -> {
                try {
                    Desktop.getDesktop().browse(new URL("https://spliterash.ru").toURI());
                } catch (IOException | URISyntaxException ioException) {
                    ioException.printStackTrace();
                }
            });
//            box.getChildren().add(info);
        }
        pane.getChildren().add(box);

        primaryStage.show();
    }
}
