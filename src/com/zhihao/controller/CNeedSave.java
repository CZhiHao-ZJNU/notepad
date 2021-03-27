package com.zhihao.controller;

import com.zhihao.Utils.FxUtils;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.File;

public class CNeedSave {

    private Stage rootStage;
    private Stage stage;
    private File file, homePath;
    private final static int OPEN = 1;
    private final static int SAVE = 2;
    private Controller controller;

    public void init(Stage stage, Controller controller) {
        this.rootStage = controller.getStage();
        this.file = controller.getFile();
        this.stage = stage;
        this.homePath = controller.getHomePath();
        this.controller = controller;
    }


    @FXML
    private void save() {
        if (this.file == null) {
            this.file = FxUtils.getFilePath(this.homePath, "另存为", SAVE);
        }
        if (this.file == null) {
            return;
        }
        this.controller.save(this.file);
        this.rootStage.close();
    }

    @FXML
    private void noSave() {
        rootStage.close();
    }

    @FXML
    private void cancel() {
        stage.close();
    }
}
