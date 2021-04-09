package com.zhihao.controller;

import com.zhihao.Utils.FxUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主界面的Controller，应用于notepad.fxml文件所对应的界面
 */
public class Controller {

    private File file, homePath;
    private Stage stage;
    private boolean needSave = false;
    private final static String stageTitle = "%s - 记事本";
    public static final String noneTitle = "无标题 - 记事本";
    private final static int OPEN = 1;
    private final static int SAVE = 2;
    private Stack<String> undoStack;
    private Stack<String> redoStack;
    private String startContent = "";
    private final KeyCombination keyCombCtrZ = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    private final KeyCombination keyCombCtrY = new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN);
    private final KeyCombination keyCombCtrH = new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN);

    @FXML
    private RadioMenuItem wordWrap;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private Label row_column_label;
    @FXML
    private AnchorPane rootLayout;
    @FXML
    private MenuItem Undo;
    @FXML
    private MenuItem Redo;
    @FXML
    private MenuItem Cut;
    @FXML
    private MenuItem Copy;
    @FXML
    private MenuItem Paste;
    @FXML
    private MenuItem Delete;
    @FXML
    private MenuItem FindAndReplace;

    public void init(Stage primaryStage) {
        this.homePath = FxUtils.getHomePath();
        this.stage = primaryStage;

        //完成作业用，实际可能会面临栈占用内存过大的情况。
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                exit();
            }
        });
        inputTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!stage.getTitle().matches("^\\*.+")) {
                    stage.setTitle("*"+stage.getTitle());
                }
                needSave = true;
                if (undoStack.size() >0 && !newValue.equals(undoStack.peek())) {
                    undoStack.push(newValue);
                }else if (undoStack.empty()) {
                    undoStack.push(newValue);
                }
                setUndoRedoAvailable();
            }
        });
        inputTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (keyCombCtrZ.match(event) || keyCombCtrY.match(event) || keyCombCtrH.match(event)) {
                    event.consume();
                }
                if (keyCombCtrZ.match(event)) {
                    Undo();
                    String content = inputTextArea.getText();
                    inputTextArea.positionCaret(content.length());
                } else if (keyCombCtrY.match(event)) {
                    Redo();
                    String content = inputTextArea.getText();
                    inputTextArea.positionCaret(content.length());
                } else if (keyCombCtrH.match(event)) {
                    findAndReplace();
                }
            }
        });
    }

    @FXML
    private void createNew() {
        if (!isNeedSave()) {
            if (this.file != null) {
                this.inputTextArea.setText("");
                this.stage.setTitle(noneTitle);
                this.setFile(null);
                this.clearUndoRedoStack();
                this.clearUndoRedoStack();
            } else {
                return;
            }
        }
        URL location = getClass().getResource("../fxml/create.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        Stage createStage = FxUtils.createNewStage(loader, this.stage,
                "记事本", 485, 230);
        CCreate create = loader.getController();
        create.init(createStage, this);
        createStage.show();
    }

    @FXML
    private void open(){
        File openFile = FxUtils.getFilePath(this.homePath, "打开", OPEN);
        StringBuilder sb = new StringBuilder();
        try (FileReader fr = new FileReader(openFile)) {
            int ch;
            this.file = openFile;
            while ((ch = fr.read()) != -1) {
                sb.append((char)ch);
            }
            inputTextArea.setText(sb.toString());
            this.stage.setTitle(String.format(stageTitle,this.file.getName()));
            this.startContent = sb.toString();
            this.clearUndoRedoStack();
            this.setUndoRedoAvailable();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            return;
        }
        inputTextArea.positionCaret(0);
    }

    @FXML
    private void save() {
        if (this.file == null) {
            this.file = FxUtils.getFilePath(this.homePath, "另存为", SAVE);
        }
        save(this.file);
    }

    @FXML
    private void saveAs() {
        this.file = FxUtils.getFilePath(this.homePath, "另存为", SAVE);
        save(this.file);
    }

    @FXML
    private void exit() {
        if (isNeedSave()) {
            URL location = getClass().getResource("../fxml/needsave.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Stage needSaveStage = FxUtils.createNewStage(fxmlLoader, this.stage,
                    "记事本", 485, 230);
            CNeedSave needSave_controller = fxmlLoader.getController();
            needSave_controller.init(needSaveStage, this);
            needSaveStage.show();
        }
        else {
            stage.close();
        }
    }

    @FXML
    private void Undo() {
        if (!undoStack.empty()) {
            String top = undoStack.pop();
            redoStack.push(top);
            if (undoStack.empty()) {
                inputTextArea.setText(startContent);
            } else {
                inputTextArea.setText(undoStack.peek());
            }
        }
    }

    @FXML
    private void Redo() {
        if (!redoStack.empty()) {
            String top = redoStack.pop();
            undoStack.push(top);
            inputTextArea.setText(top);
        }
    }

    @FXML
    private void Cut() {
        String selectedContent = inputTextArea.getSelectedText();
        if (selectedContent != null && !"".equals(selectedContent)) {
            inputTextArea.cut();
        }
        selectedContent = inputTextArea.getSelectedText();
        setIsAvailable(selectedContent);
    }

    @FXML
    private void Copy() {
        String selectedContent = inputTextArea.getSelectedText();
        if (selectedContent != null && !"".equals(selectedContent)) {
            inputTextArea.copy();
        }
        selectedContent = inputTextArea.getSelectedText();
        setIsAvailable(selectedContent);
    }

    @FXML
    private void Paste() {
        inputTextArea.paste();
        String selectedContent = inputTextArea.getSelectedText();
        setIsAvailable(selectedContent);
    }

    @FXML
    private void Delete() {
        String selectedContent = inputTextArea.getSelectedText();
        if (selectedContent != null && !"".equals(selectedContent)) {
            inputTextArea.replaceSelection("");
        }
        selectedContent = inputTextArea.getSelectedText();
        setIsAvailable(selectedContent);
    }

    @FXML
    private void setUsable() {
        String selectedContent = inputTextArea.getSelectedText();
        setIsAvailable(selectedContent);
    }

    @FXML
    private void selectAll() {
        this.inputTextArea.selectAll();
    }

    @FXML
    private void findAndReplace() {
        URL location = getClass().getResource("../fxml/FindAndReplace.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Stage FAR = FxUtils.createNewStage(fxmlLoader, this.stage,
                "替换", 530, 260);
        CFindAndReplace cfar = fxmlLoader.getController();
        cfar.init(this.inputTextArea, FAR);
        FAR.show();
    }

    @FXML
    private void wordWrap(){
        boolean isWrapText = inputTextArea.isWrapText();
        inputTextArea.setWrapText(!isWrapText);
    }

    @FXML
    private void truncation() {
       if(inputTextArea.isWrapText()) {

       }
    }

    @FXML
    private void setTextFont() {

        URL location = getClass().getResource("../fxml/fontwindow.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        Stage fontSetter = FxUtils.createNewStage(loader, this.stage,
                "字体", 630, 550);

        CFont fontController = loader.getController();
        fontController.init(fontSetter, this.inputTextArea);
        fontSetter.show();
    }

    @FXML
    private void setFontColor() {
        URL location = getClass().getResource("../fxml/ChangeColor.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        loader.setBuilderFactory(new JavaFXBuilderFactory());

        Stage colorSetter = FxUtils.createNewStage(loader, this.stage,
                "颜色", 530, 350);
        CChangeColor changeColor = loader.getController();
        changeColor.init(this.inputTextArea, colorSetter);
        colorSetter.show();
    }

    @FXML
    private void viewHelp() {
        try {
            URL location = new URL("https://cn.bing.com/search?q=%E8%8E%B7%E5%8F%96%E6%9C%89%E5%85%B3+windows+10+%E4%B8%AD%E7%9A%84%E8%AE%B0%E4%BA%8B%E6%9C%AC%E7%9A%84%E5%B8%AE%E5%8A%A9&filters=guid:%224466414-zh-hans-dia%22%20lang:%22zh-hans%22&form=T00032&ocid=HelpPane-BingIA");
            Desktop.getDesktop().browse(location.toURI());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void aboutNotePad() {
        URL location = getClass().getResource("../fxml/aboutNotepad.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        Stage about = FxUtils.createNewStage(loader, this.stage,
                "About Notepad...", 479, 310);
        CAboutNotepad aboutController = loader.getController();
        aboutController.init(about);
        about.show();
    }

    @FXML
    private void refreshPosition() {
        int position = inputTextArea.getCaretPosition();
        String content = inputTextArea.getText(0, position);
        Pattern p = Pattern.compile("\n");
        int row = 0, column = 0;
        Matcher m = p.matcher(content);
        while (m.find()) {
            row++;
        }
        int lastIndex = content.lastIndexOf("\n");
        if (lastIndex < 0) {
            column = position + 1;
        } else {
            column = position - lastIndex;
        }
        String labelContent = "第 %d 行，第 %d 列";
        row_column_label.setText(String.format(labelContent, row+1, column));

        content = inputTextArea.getSelectedText();
        setIsAvailable(content);
    }

    /**
     * 依据具体的文件路径进行保存操作
     * @param file
     */
    public void save(File file){
        try (FileOutputStream fop = new FileOutputStream(file)) {
            String content = inputTextArea.getText();
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes); fop.flush();
            stage.setTitle(String.format(stageTitle,file.getName()));
            this.needSave = false;
            if (stage.getTitle().matches("^\\*.+")) {
                stage.setTitle(stage.getTitle().substring(1));
            }
            this.startContent = inputTextArea.getText();
            this.clearUndoRedoStack();
        } catch (NullPointerException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setIsAvailable(String str) {
        if (str == null || "".equals(str)) {
            setItemDisable();
        } else if (str != null && !"".equals(str)) {
            setItemUsable();
        }
    }

    private void setUndoRedoAvailable() {
        if (!undoStack.empty()) {
            Undo.setDisable(false);
        } else {
            Undo.setDisable(true);
        }
        if (!redoStack.empty()) {
            Redo.setDisable(false);
        } else {
            Redo.setDisable(true);
        }
    }

    private void setItemUsable() {
        Cut.setDisable(false);
        Copy.setDisable(false);
        Delete.setDisable(false);
    }

    private void setItemDisable() {
        Cut.setDisable(true);
        Copy.setDisable(true);
        Delete.setDisable(true);
    }

    public void clearUndoRedoStack() {
        if (undoStack != null && !undoStack.empty()) {
            this.undoStack.clear();
        }
        if (redoStack != null && !redoStack.empty() ) {
            this.redoStack.clear();
        }
    }

    public boolean isNeedSave() {
        return stage.getTitle().matches("^\\*.+");
    }

    public File getHomePath() {
        return homePath;
    }

    public Stage getStage() {
        return stage;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public TextArea getInputTextArea() {
        return inputTextArea;
    }

    public String getStartContent() {
        return startContent;
    }

    public void setStartContent(String startContent) {
        this.startContent = startContent;
    }
}
