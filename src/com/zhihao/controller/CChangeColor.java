package com.zhihao.controller;

import com.zhihao.Utils.FxUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CChangeColor {

    private Stage stage;
    private TextArea inputTextArea;
    private final static String colorString = "-fx-text-fill: %s";
    @FXML
    private Label currentColor;
    @FXML
    private Label modifiedColor;
    @FXML
    private ColorPicker colorPicker;

    @FXML
    private void confirm() {
        String colorStyle = "#" +colorPicker.getValue().toString().substring(2);
        String newStyle = FxUtils.changeStyle(inputTextArea.getStyle(), colorStyle,
                colorString, "-fx-text-fill");
        inputTextArea.setStyle(newStyle);
        this.stage.close();
    }

    @FXML
    private void cancel() {
        stage.close();
    }

    public void init(TextArea inputTextArea, Stage stage) {
        this.inputTextArea = inputTextArea;
        this.stage = stage;
        String styles = this.inputTextArea.getStyle();

        //获取关于字体颜色的单条style信息
        String colorStyle = FxUtils.getColorByStyles(styles);

        //获取颜色style中的色号信息
        String color = FxUtils.getColorByColorStyle(colorStyle);

        //设置选色板的默认颜色与输入框一致
        if (!"".equals(color)) {
            colorPicker.setValue(Color.web(color));
        } else {
            colorPicker.setValue(Color.BLACK);
        }
        currentColor.setStyle(colorStyle);
        modifiedColor.setStyle(colorStyle);

        //设置监听器，当选色板选中的颜色有改动时，调整modifiedColor标签的字体颜色
        colorPicker.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                String newColor = newValue.toString().substring(2);
                String colorStyle = "#" + newColor;
                //String newStyle = FxUtils.changeColor(modifiedColor.getStyle(), colorStyle);
                String newStyle = FxUtils.changeStyle(modifiedColor.getStyle(), colorStyle,
                        colorString, "-fx-text-fill");
                modifiedColor.setStyle(newStyle);
            }
        });
    }

}
