package com.zhihao.Utils;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * 封装一些零散的工具方法
 */
public class FxUtils {

    private final static int OPEN = 1;
    private final static int SAVE = 2;

    /**
     * 根据AnchorPane获取根Stage
     * @param rootLayout
     * @return
     */
    public static Stage getStageByAnchorPane(AnchorPane rootLayout) {
        return (Stage)rootLayout.getScene().getWindow();
    }

    /**
     * 返回本机电脑桌面的路径
     * @return
     */
    public static File getHomePath() {
        return FileSystemView.getFileSystemView().getHomeDirectory();
    }

    /**
     * 调用FileChooser，获取文件路径用于 打开文件 或 另存为文件 的操作
     * type表示操作，1表示打开，2表示保存
     * @param homePath
     * @param title
     * @return
     */
    public static File getFilePath(File homePath, String title, int type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(homePath);
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("文本文档(*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("所有文件 (*.*)","*.*")
        );
        if (type == OPEN) {
            return fileChooser.showOpenDialog(null);
        } else {
            return fileChooser.showSaveDialog(null);
        }
    }

    public static String changeStyle(String oldStyle, String style, String pattern, String styleKind) {
        style = String.format(pattern, style);
        StringTokenizer st = new StringTokenizer(oldStyle, ";");
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            if (str.indexOf(styleKind) == -1) {
                sb.append(str+";");
            }
        }
        sb.append(style+";");
        return sb.toString();
    }

    public static String getColorByColorStyle(String colorStyle) {
        String[] styles = colorStyle.split(":");
        if (styles.length == 2) {
            if (styles[1].indexOf(";") != -1) {
                styles[1] = styles[1].trim();
                return styles[1].substring(0, styles[1].length()-1);
            }
        }
        return "";
    }

    public static String getColorByStyles(String style) {
        String[] styles = style.split(";");
        if (style.indexOf("-fx-text-fill:") != -1 && styles.length == 1) {
            return style;
        }
        for (String s : styles) {
            if (s.indexOf("-fx-text-fill:") != -1) {
                return s;
            }
        }
        return "";
    }

    public static Stage createNewStage(FXMLLoader loader, Stage parent, String title, double width, double height) {

        loader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.initOwner(parent);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(new Scene(pane, width, height));

        return stage;
    }
}
