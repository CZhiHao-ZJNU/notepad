package com.zhihao.main;

import com.zhihao.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        /**
         * 介似静态滴初始化方法
         * 但是貌似不能获取Controller对象，因此后面换一种写法
         */
        // Parent root = FXMLLoader.load(getClass().getResource("../fxml/notepad.fxml"));

        URL location = getClass().getResource("../fxml/notepad.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("无标题 - 记事本");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.getIcons().add(new Image("file:logo.png"));

        Controller controller = fxmlLoader.getController();
        controller.init(primaryStage);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
