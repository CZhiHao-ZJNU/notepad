package com.zhihao.controller;

import com.zhihao.Utils.FxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;

public class CCreate {

    private Stage parentStage;
    private Stage stage;
    private File file, homePath;
    private TextArea inputTextArea;
    private Controller controller;
    private final static int SAVE = 2;

    public void init(Stage stage, Controller controller) {
        this.controller = controller;
        this.stage = stage;
        this.inputTextArea = controller.getInputTextArea();
        this.file = controller.getFile();
        this.parentStage = controller.getStage();
        this.homePath = controller.getHomePath();
    }


    @FXML
    private void save() {
        if (this.file == null) {
            this.file = FxUtils.getFilePath(this.homePath, "另存为", SAVE);
        }
        if (this.file == null) {
            return;
        }
        controller.save(this.file);
        clearBeforeCreateNew();
        this.stage.close();
    }

    @FXML
    private void noSave() {
        clearBeforeCreateNew();
        this.stage.close();
    }

    @FXML
    private void cancel() {
        this.stage.close();
    }

    private void clearBeforeCreateNew() {
        inputTextArea.setText("");
        parentStage.setTitle("无标题 - 记事本");
        controller.setFile(null);
        controller.setStartContent("");
        controller.clearUndoRedoStack();
    }
}
