package com.oukele.mytools.controller;


import com.oukele.mytools.service.FileSearcher;
import com.oukele.mytools.utils.WinUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 主界面控制类
 *
 * @author oukele
 */
public class MainController {

//    @FXML
//    private ListView<String> listViewResults;
//
//    @FXML
//    private void handleChooseFolder(ActionEvent event) {
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        directoryChooser.setTitle("选择文件夹");
//        File selectedDirectory = directoryChooser.showDialog(new Stage());
//        if (selectedDirectory != null) {
//            // 清空旧结果
//            listViewResults.getItems().clear();
//            // 为避免阻塞 UI，启动一个新线程执行搜索
//            new Thread(() -> {
//                List<String> results = FileSearcher.searchFiles(selectedDirectory, "你好");
//                // 更新 UI 必须在 JavaFX 线程中
//                Platform.runLater(() -> listViewResults.getItems().addAll(results));
//            }).start();
//        }
//    }

    /**
     * 打开搜索内容页面
     *
     * @param event 事件
     */
    @FXML
    public void openSearchContentUiEvent(ActionEvent event) throws IOException {
        WinUtils.openDefaultSystemUi("search-content-view", "搜索内容");
    }

    /**
     * 打开telnet测试页面
     *
     * @param actionEvent 事件
     */
    @FXML
    public void openTelnetTestUiEvent(ActionEvent actionEvent) throws IOException {
        WinUtils.openDefaultSystemUi("telnet-test-view","Telnet 测试");
    }
}
