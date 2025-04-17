package com.oukele.mytools.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.oukele.mytools.model.FindResult;
import com.oukele.mytools.service.EliminateFileCommentsService;
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
import java.util.ResourceBundle;

/**
 * 去除文件注释控制器
 *
 * @author oukele
 */
public class EliminateFileCommentsController implements Initializable {

    @FXML
    public TextField dirPath;
    @FXML
    public Button selectDir;
    @FXML
    public Button start;
    @FXML
    public TableView<FindResult> tableView;
    @FXML
    public TableColumn<FindResult, String> findPathColumn;
    @FXML
    public TableColumn<FindResult, Void> actionColumn;

    @FXML
    public TextField fileCount;
    @FXML
    public TextField successCount;
    @FXML
    public TextField failCount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            // 获取窗口
            Stage stage = (Stage) dirPath.getScene().getWindow();
            // 设置窗口不可拉伸
            stage.setResizable(false);

            // 使用工具类初始化 TableView
            TableViewUtils.setupTableView(tableView, findPathColumn, actionColumn, "打开", this::handleButtonClick);
        });
    }

    private void handleButtonClick(FindResult result) {
        if (!FileUtil.exist(result.getFilePath())) {
            WinUtils.commonAlert("检测到文件路径不存在！");
            return;
        }
        // 获取文件夹路径
        File folder = new File(result.getFilePath()).getParentFile();
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
    }

    /**
     * 开始事件
     *
     * @param actionEvent
     */
    @FXML
    public void startEvent(ActionEvent actionEvent) {
        String dirPathText = dirPath.getText();
        if (StrUtil.isEmpty(dirPathText)) {
            WinUtils.commonAlert("请先选择文件夹！");
            return;
        }
        if (!new File(dirPathText).isDirectory()) {
            WinUtils.commonAlert("指定的路径不是目录：" + dirPathText);
            return;
        }
        // 清空旧结果
        tableView.getItems().clear();
        // 按钮禁用
        start.setDisable(true);
        selectDir.setDisable(true);

        new EliminateFileCommentsService().eliminateFileComments(
                // 目录路径
                dirPathText,
                // 总文件数
                totalCount -> Platform.runLater(() -> fileCount.setText(String.valueOf(totalCount))),
                // 处理成功文件数
                handlerSuccessFileProcessed -> Platform.runLater(() -> successCount.setText(String.valueOf(handlerSuccessFileProcessed))),
                // 处理失败文件数
                handlerFailFileProcessed -> Platform.runLater(() -> failCount.setText(String.valueOf(handlerFailFileProcessed))),
                // onComplete
                () -> Platform.runLater(() -> {
                    WinUtils.commonInfo("处理完成！");
                    // 恢复按钮
                    start.setDisable(false);
                    selectDir.setDisable(false);
                }),
                // 处理失败文件
                handlerFailFileProcessed -> Platform.runLater(() -> tableView.getItems().add(new FindResult(handlerFailFileProcessed)))
        );
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
