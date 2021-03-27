package com.zhihao.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;

public class CAboutNotepad {

    private Stage stage;
    private Image image;

    @FXML
    private ImageView logo;


    public void init(Stage stage) {
        this.stage = stage;
        try (FileInputStream fin = new FileInputStream(new File("logo.png"))) {
            image = new Image(fin);
            logo.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void confirm() {
        this.stage.close();
    }
}
