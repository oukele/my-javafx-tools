package com.oukele.mytools.controller;

import cn.hutool.core.util.StrUtil;
import com.oukele.mytools.constant.SystemConstant;
import com.oukele.mytools.service.FileSearcher;
import com.oukele.mytools.utils.WinUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 搜索内容控制器
 *
 * @author oukele
 */
public class SearchContentController implements Initializable {

    @FXML
    public TextField dirPath;
    @FXML
    public TextField findContent;
    @FXML
    public TextArea resultContent;
    @FXML
    public Button selectDir;
    @FXML
    public Button search;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            // 获取窗口
            Stage stage = (Stage) dirPath.getScene().getWindow();
            // 设置窗口不可拉伸
            stage.setResizable(false);

            resultContent.setFont(new Font(16));
            // 加载配置文件
            Properties properties = new MainController().loadProperties();
            // 设置搜索内容路径
            String searchContentPathValue = properties.getProperty(SystemConstant.SEARCH_CONTENT_PATH_KEY);
            if (StrUtil.isNotEmpty(searchContentPathValue)) {
                dirPath.setText(searchContentPathValue);
            }
        });
    }

    /**
     * 搜索事件
     *
     * @param actionEvent
     */
    @FXML
    public void searchEvent(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            String dirPathText = dirPath.getText();
            if (StrUtil.isEmpty(dirPathText)) {
                WinUtils.commonAlert("请先选择文件夹！");
                return;
            }
            if (!new File(dirPathText).isDirectory()) {
                WinUtils.commonAlert("指定的路径不是目录：" + dirPathText);
                return;
            }
            String findContentText = findContent.getText();
            if (StrUtil.isEmpty(findContentText)) {
                WinUtils.commonAlert("请先输入搜索的内容！");
                return;
            }
            // 清空旧结果
            resultContent.clear();
            resultContent.appendText("开始搜索...\n");
            // 按钮禁用
            search.setDisable(true);
            selectDir.setDisable(true);
            // 开始处理
            new FileSearcher().searchFilesContent(
                    // 目录路径
                    dirPathText,
                    // 关键字
                    findContentText,
                    // onFound
                    filePath -> Platform.runLater(() -> resultContent.appendText("文件中包含 " + findContentText + " , " + filePath + "\n")),
                    // onComplete
                    () -> Platform.runLater(() -> {
                        resultContent.appendText("搜索完成！\n");
                        // 恢复按钮
                        search.setDisable(false);
                        selectDir.setDisable(false);
                    }),
                    // onError
                    errorMessage -> {
                    });
        });
    }

    /**
     * 选择目录事件
     *
     * @param actionEvent
     */
    @FXML
    public void selectDirEvent(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件夹");
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            // 清空旧结果
            dirPath.clear();

            // 为避免阻塞 UI，启动一个新线程执行搜索
            Platform.runLater(()-> dirPath.setText(selectedDirectory.getAbsolutePath()));
        }
    }

}
