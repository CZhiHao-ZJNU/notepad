package com.zhihao.controller;

import com.zhihao.Utils.FxUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CFont {

    private Stage stage;
    private String labelStyle;

    private final static String fontString = "-fx-font-family: %s";
    private final static String styleString = "-fx-font-style: %s";
    private final static String sizeString = "-fx-font-size: %s";

    @FXML
    private ListView fontChooser;
    @FXML
    private ListView styleChooser;
    @FXML
    private ListView sizeChooser;
    @FXML
    private Label demo;
    @FXML
    private TextArea inputTextArea;

    ObservableList<String> fontList = FXCollections.observableArrayList();
    ObservableList<String> fontStyleList = FXCollections.observableArrayList();
    ObservableList<String> fontSizeList = FXCollections.observableArrayList();
    Map<String, String> chineseCharMap = new HashMap<>();

    public void init(Stage stage, TextArea area) {
        this.stage = stage;
        this.inputTextArea = area;
        File font_file = new File("src\\com\\zhihao\\controller\\font.txt");
        File fontStyle_file = new File("src\\com\\zhihao\\controller\\FontStyle.txt");
        File fontWeight_file = new File("src\\com\\zhihao\\controller\\FontSize.txt");
        readToList(fontList, font_file);
        readToList(fontStyleList, fontStyle_file);
        readToList(fontSizeList, fontWeight_file);
        readChineseCharToMap();
        fontChooser.setItems(fontList);
        styleChooser.setItems(fontStyleList);
        sizeChooser.setItems(fontSizeList);

        fontChooser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                labelStyle = demo.getStyle();
                labelStyle = changeFont(labelStyle, (String)newValue);
                demo.setStyle(labelStyle);
            }
        });

        styleChooser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                labelStyle = demo.getStyle();
                labelStyle = FxUtils.changeStyle(labelStyle, (String)newValue, styleString, "-fx-font-style");
                demo.setStyle(labelStyle);
            }
        });

        sizeChooser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                labelStyle = demo.getStyle();
                labelStyle = FxUtils.changeStyle(labelStyle, (String)newValue, sizeString, "-fx-font-size");
                demo.setStyle(labelStyle);
            }
        });

    }

    @FXML
    private void confirm() {
        labelStyle = demo.getStyle();
        String oldStyle = inputTextArea.getStyle();

        String newStyle = changeStyle(oldStyle, labelStyle);
        inputTextArea.setStyle(newStyle);
        this.stage.close();

    }

    @FXML
    private void cancel() {
        this.stage.close();
    }

    private void readToList(ObservableList<String> list, File file) {
        try(Scanner in = new Scanner(file)) {
            while (in.hasNext()) {
                String str = in.nextLine();
                list.add(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readChineseCharToMap() {
        File fontMap = new File("src\\com\\zhihao\\controller\\FontMap.txt");
        try(Scanner in = new Scanner(fontMap)) {
            while (in.hasNext()) {
                String str = in.nextLine();
                String[] strings = str.split(" ",2);
                chineseCharMap.put(strings[0], strings[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String changeFont(String oldStyle, String font) {
        if (chineseCharMap.keySet().contains(font)) {
            font = chineseCharMap.get(font);
        }
        font = String.format(fontString, font);
        StringTokenizer st = new StringTokenizer(oldStyle, ";");
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            if (str.indexOf("-fx-font-family") == -1) {
                sb.append(str+";");
            }
        }
        sb.append(font+";");
        return sb.toString();
    }

    private String changeStyle(String oldStyle, String newStyle) {

        String[] newStyles = newStyle.split(";");
        Set<String> newStylesSet = new HashSet<String>();

        for (String style : newStyles) {
            newStylesSet.add(style.split(":")[0].trim());
        }

        StringTokenizer st = new StringTokenizer(oldStyle, ";");
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            if (!newStylesSet.contains(str.split(":")[0].trim())) {
                sb.append(str+";");
            }
        }
        sb.append(newStyle+";");
        return sb.toString();

    }
}
