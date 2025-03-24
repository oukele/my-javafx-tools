package com.oukele.mytools.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.oukele.mytools.constant.SystemConstant;
import com.oukele.mytools.utils.WinUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 主界面控制类
 *
 * @author oukele
 */
public class MainController implements Initializable {
    @FXML
    public TextField searchContentPath;
    @FXML
    public TextField searchFilePath;
    @FXML
    public TextArea telnetContent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            // 加载配置文件
            Properties properties = loadProperties();
            // 设置搜索内容路径
            String searchContentPathValue = properties.getProperty(SystemConstant.SEARCH_CONTENT_PATH_KEY);
            if (StrUtil.isNotEmpty(searchContentPathValue)) {
                searchContentPath.setText(searchContentPathValue);
            }
            // 设置搜索文件路径
            String searchFilePathValue = properties.getProperty(SystemConstant.SEARCH_FILE_PATH_KEY);
            if (StrUtil.isNotEmpty(searchFilePathValue)) {
                searchFilePath.setText(searchFilePathValue);
            }
            // 设置Telnet内容
            String telnetContentValue = properties.getProperty(SystemConstant.TELNET_CONTENT_KEY);
            if (StrUtil.isNotEmpty(telnetContentValue)) {
                telnetContent.setText(telnetContentValue);
            }
        });
    }

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
        WinUtils.openDefaultSystemUi("telnet-test-view", "Telnet 测试");
    }

    /**
     * 打开搜索文件页面
     *
     * @param actionEvent 事件
     */
    @FXML
    public void openSearchFileUiEvent(ActionEvent actionEvent) throws IOException {
        WinUtils.openDefaultSystemUi("search-file-view", "搜索文件");
    }

    /**
     * 保存搜索内容路径
     *
     * @param actionEvent 事件
     */
    @FXML
    public void searchContentPathEvent(ActionEvent actionEvent) {
        String contentPathText = searchContentPath.getText();
        if (StrUtil.isEmpty(contentPathText)) {
            WinUtils.commonAlert("请先输入需要搜索的文件夹路径！");
            return;
        }
        if (!FileUtil.isDirectory(contentPathText)) {
            WinUtils.commonAlert("指定的路径不是目录：" + contentPathText);
            return;
        }
        // 保存路径到本地配置文件
        Properties properties = loadProperties();
        properties.setProperty(SystemConstant.SEARCH_CONTENT_PATH_KEY, contentPathText);
        saveProperties(properties);
        WinUtils.commonInfo("保存成功！");
    }

    /**
     * 保存搜索文件路径
     *
     * @param actionEvent 事件
     */
    @FXML
    public void searchFilePathEvent(ActionEvent actionEvent) {
        String filePathText = searchFilePath.getText();
        if (StrUtil.isEmpty(filePathText)) {
            WinUtils.commonAlert("请先输入需要搜索的文件夹路径！");
            return;
        }
        if (!FileUtil.isDirectory(filePathText)) {
            WinUtils.commonAlert("指定的路径不是目录：" + filePathText);
            return;
        }
        // 保存路径到本地配置文件
        Properties properties = loadProperties();
        properties.setProperty(SystemConstant.SEARCH_FILE_PATH_KEY, filePathText);
        saveProperties(properties);
        WinUtils.commonInfo("保存成功！");
    }

    /**
     * 保存telnet测试内容
     *
     * @param actionEvent 事件
     */
    @FXML
    public void telnetContentEvent(ActionEvent actionEvent) {
        String telnetContentText = telnetContent.getText();
        if (StrUtil.isEmpty(telnetContentText)) {
            WinUtils.commonAlert("请先输入内容！");
            return;
        }
        // 保存内容到本地配置文件
        Properties properties = loadProperties();
        properties.setProperty(SystemConstant.TELNET_CONTENT_KEY, telnetContentText);
        saveProperties(properties);
        WinUtils.commonInfo("保存成功！");
    }

    /**
     * 获取配置文件对象
     *
     * @return 配置文件对象
     */
    public static File getConfigFileObj() {
        return new File("config.properties");
    }

    /**
     * 加载配置文件
     *
     * @return Properties对象
     */
    public Properties loadProperties() {
        Properties properties = new Properties();
        File configFile = getConfigFileObj();
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
                WinUtils.commonAlert("加载配置文件失败：" + e.getMessage());
            }
        }
        return properties;
    }

    /**
     * 保存配置文件
     *
     * @param properties Properties对象
     */
    private void saveProperties(Properties properties) {
        try (OutputStream output = new FileOutputStream(getConfigFileObj())) {
            properties.store(output, "");
        } catch (IOException e) {
            e.printStackTrace();
            WinUtils.commonAlert("保存配置文件失败：" + e.getMessage());
        }
    }


}
