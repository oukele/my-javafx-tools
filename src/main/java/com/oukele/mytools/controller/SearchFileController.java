package com.oukele.mytools.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.oukele.mytools.constant.SystemConstant;
import com.oukele.mytools.model.FindResult;
import com.oukele.mytools.service.FileSearcher;
import com.oukele.mytools.utils.TableViewUtils;
import com.oukele.mytools.utils.WinUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 搜索文件控制器
 *
 * @author oukele
 */
public class SearchFileController implements Initializable {

    @FXML
    public TextField dirPath;
    @FXML
    public TextField findContent;
    @FXML
    public Button selectDir;
    @FXML
    public Button search;
    @FXML
    public TableView<FindResult> tableView;
    @FXML
    public TableColumn<FindResult, String> findPathColumn;
    @FXML
    public TableColumn<FindResult, Void> actionColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            // 获取窗口
            Stage stage = (Stage) dirPath.getScene().getWindow();
            // 设置窗口不可拉伸
            stage.setResizable(false);

            // 加载配置文件
            Properties properties = new MainController().loadProperties();
            // 设置搜索文件路径
            String searchFilePathValue = properties.getProperty(SystemConstant.SEARCH_FILE_PATH_KEY);
            if (StrUtil.isNotEmpty(searchFilePathValue)) {
                dirPath.setText(searchFilePathValue);
            }

            // 使用工具类初始化 TableView
            TableViewUtils.setupTableView(tableView, findPathColumn, actionColumn, "打开", fundResult -> {
                if (!FileUtil.exist(fundResult.getFilePath())) {
                    WinUtils.commonAlert("检测到文件路径不存在！");
                    return;
                }
                // 获取文件夹路径
                File folder = new File(fundResult.getFilePath()).getParentFile();
                if (folder.exists() && folder.isDirectory()) {
                    try {
                        // 打开文件夹
                        Desktop.getDesktop().open(folder);
                    } catch (IOException e) {
                        e.printStackTrace();
                        WinUtils.commonAlert("打开文件夹失败：" + e.getMessage());
                    }
                } else {
                    WinUtils.commonAlert("文件夹路径无效！");
                }
            });
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
            tableView.getItems().clear();
            // 按钮禁用
            search.setDisable(true);
            selectDir.setDisable(true);
            // 开始处理
            new FileSearcher().searchFiles(
                    // 目录路径
                    dirPathText,
                    // 关键字
                    findContentText,
                    // onFound
                    filePath -> Platform.runLater(() -> {
                        tableView.getItems().add(new FindResult(filePath));
                    }),
                    // onComplete
                    () -> Platform.runLater(() -> {
                        WinUtils.commonInfo("搜索完成！");
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
            Platform.runLater(() -> dirPath.setText(selectedDirectory.getAbsolutePath()));
        }
    }

}
