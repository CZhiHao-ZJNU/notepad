package com.zhihao.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CFindAndReplace {

    private TextArea inputTextArea;
    private Stage stage;
    private int fromIndex = 0;
    private String cannotFindContext = "找不到\"%s\"";
    private String oldFindText = null;

    @FXML
    private TextField findField;
    @FXML
    private TextField replaceField;
    @FXML
    private Button btn_findNext;
    @FXML
    private Button btn_replace;
    @FXML
    private Button btn_replaceAll;
    @FXML
    private Button btn_cancel;

    public void init(TextArea inputTextArea, Stage stage) {
        this.inputTextArea = inputTextArea;
        this.stage = stage;
    }

    @FXML
    private void findNext() {
        String findText = findField.getText();
        int status = find();
        if (status == -1) {
            cannotFind(findText);
        }
    }

    @FXML
    private void replace() {
        String contextReplaceTo = replaceField.getText();
        String findText = findField.getText();
        String selectContext = inputTextArea.getSelectedText();

        if (selectContext != null && !"".equals(selectContext)) {
            inputTextArea.replaceSelection(contextReplaceTo);
        }
        int status = find();
        if (status == -1) {
            cannotFind(findText);
        }
    }

    @FXML
    private void replaceAll() {
        String contextReplaceTo = replaceField.getText();
        fromIndex = 0;
        while ( find() != -1) {
            inputTextArea.replaceSelection(contextReplaceTo);
        }
    }

    @FXML
    private void cancel() {
        this.stage.close();
    }

    @FXML
    private void setButtonAvailable() {
        String str_find = findField.getText();
        String str_replace = replaceField.getText();
        if (str_find == null || "".equals(str_find)) {
            btn_findNext.setDisable(true);
            btn_replace.setDisable(true);
            btn_replaceAll.setDisable(true);
        } else if ("".equals(str_replace)) {
            btn_findNext.setDisable(false);
            btn_replace.setDisable(true);
            btn_replaceAll.setDisable(true);
        } else {
            btn_findNext.setDisable(false);
            btn_replace.setDisable(false);
            btn_replaceAll.setDisable(false);
        }
    }

    private int find() {
        String content = inputTextArea.getText();
        String newFindText = findField.getText();
        if (oldFindText == null || !oldFindText.equals(newFindText)) {
            fromIndex = 0;
            oldFindText = newFindText;
        }
        int start = content.indexOf(newFindText, fromIndex);
        if (start == -1) {
            return -1;
        } else {
            int end = start + newFindText.length();
            inputTextArea.selectRange(start, end);
            fromIndex = end+1;
            return 1;
        }
    }

    private void cannotFind(String findText) {
        Alert alert_cannotFind = new Alert(Alert.AlertType.WARNING);
        alert_cannotFind.setTitle("记事本");
        alert_cannotFind.initOwner(this.stage);
        alert_cannotFind.setContentText(String.format(cannotFindContext, findText));
        alert_cannotFind.show();
    }
}
