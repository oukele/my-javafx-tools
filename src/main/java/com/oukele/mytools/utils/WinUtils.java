package com.oukele.mytools.utils;

import cn.hutool.core.util.StrUtil;
import com.oukele.mytools.MyApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

/**
 * 窗口操作工具类
 *
 * @author oukele
 */
public class WinUtils {


    /**
     * 创建默认的场景
     *
     * @param fxmlName fxml文件路径（默认从 view 中读取）
     * @return Scene
     */
    public static Scene createDefaultScene(String fxmlName) throws IOException {
        ThrowUtils.throwIf(StrUtil.isEmpty(fxmlName), "请输入正确的fxml名称");
        // fxmlName 不包含 .fxml 的后缀
        if (!fxmlName.endsWith(".fxml")) {
            fxmlName = fxmlName + ".fxml";
        }
        FXMLLoader loader = new FXMLLoader(MyApplication.class.getResource("view/" + fxmlName));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        // 可额外添加 BootstrapFX 样式，如果 FXML 中未加载
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        return scene;
    }

    /**
     * 打开默认的系统窗口
     *
     * @param fxmlName fxml文件路径（默认从 view 中读取）
     * @throws IOException
     */
    public static void openDefaultSystemUi(String fxmlName) throws IOException {
        // 创建一个 Stage
        Stage stage = new Stage();
        stage.setScene(createDefaultScene(fxmlName));
        stage.show();
    }

    /**
     * 打开默认的系统窗口
     *
     * @param fxmlName fxml文件路径（默认从 view 中读取）
     * @param title    窗口标题
     */
    public static void openDefaultSystemUi(String fxmlName, String title) throws IOException {
        // 创建一个 Stage
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(createDefaultScene(fxmlName));
        stage.show();

    }

    /**
     * 通用的提示对话框
     *
     * @param msg 提示信息
     */
    public static void commonAlert(String msg) {
        // 如果内容为空，弹出提示对话框
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("系统提示");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


}
