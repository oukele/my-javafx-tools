package com.oukele.mytools.controller;

import cn.hutool.core.util.StrUtil;
import com.oukele.mytools.utils.WinUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * telnet 测试控制器
 *
 * @author oukele
 */
public class TelnetTestController implements Initializable {

    @FXML
    public AnchorPane root;
    @FXML
    public TextArea content;
    @FXML
    public Button startButton;
    @FXML
    public TextArea resultContent;

    /**
     * 初始化事件
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 确保 Scene 已经初始化
        Platform.runLater(() -> {
            // 获取窗口
            Stage stage = (Stage) root.getScene().getWindow();
            // 设置窗口不可拉伸
            stage.setResizable(false);
        });

    }

    /**
     * 启动测试事件
     *
     * @param actionEvent 事件
     */
    @FXML
    public void startTestEvent(ActionEvent actionEvent) {
        String contentText = content.getText();
        if (StrUtil.isEmpty(contentText)) {
            WinUtils.commonAlert("请先输入内容！");
        }

        // 按钮禁用
        startButton.setDisable(true);
        resultContent.clear();
        resultContent.setFont(new Font(16));
        resultContent.appendText("开始测试...\n");

        // 创建后台任务
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                String[] rows = contentText.split("\n");
                for (String infos : rows) {
                    if (StrUtil.isEmpty(infos)) {
                        continue;
                    }
                    String[] infoList = infos.split(" ");
                    if (infoList.length < 2) {
                        continue;
                    }
                    String ip = infoList[0];
                    String port = infoList[1];
                    if (StrUtil.isEmpty(ip) || StrUtil.isEmpty(port)) {
                        continue;
                    }
                    // 测试端口
                    boolean isOpen = testPort(ip, Integer.parseInt(port));
                    // 更新 UI
                    Platform.runLater(() -> {
                        if (isOpen) {
                            resultContent.appendText(ip + ":" + port + " 端口开放\n");
                        } else {
                            resultContent.appendText(ip + ":" + port + " 端口不可用\n");
                        }
                    });
                }
                return null;
            }

            @Override
            protected void succeeded() {
                // 任务完成后启用按钮
                startButton.setDisable(false);
            }

            @Override
            protected void failed() {
                // 任务失败后启用按钮
                startButton.setDisable(false);
            }
        };
        // 启动后台任务
        new Thread(task).start();
    }

    private boolean testPort(String ip, int port) {
        try (Socket socket = new Socket(ip, port)) {
            // 端口开放
            return true;
        } catch (IOException e) {
            // 端口不可用
            return false;
        }
    }

}
