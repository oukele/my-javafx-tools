package com.oukele.mytools.utils;

import com.oukele.mytools.model.FindResult;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * TableView 工具类
 *
 * @author oukele
 */
public class TableViewUtils {

    /**
     * 初始化 TableView 并添加按钮列
     *
     * @param tableView          TableView 对象
     * @param findPathColumn     文件路径列
     * @param actionColumn       操作列
     * @param buttonText         按钮文本
     * @param buttonClickHandler 按钮点击事件处理器
     */
    public static void setupTableView(TableView<FindResult> tableView, TableColumn<FindResult, String> findPathColumn, TableColumn<FindResult, Void> actionColumn, String buttonText, ButtonClickHandler buttonClickHandler) {
        // 绑定列和数据模型
        findPathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));

        // 设置按钮列
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button(buttonText);

            {
                // 绑定按钮点击事件
                button.setOnAction(event -> {
                    FindResult result = getTableView().getItems().get(getIndex());
                    buttonClickHandler.handle(result);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setAlignment(Pos.CENTER);
                    button.getStyleClass().add("btn-info");
                    setGraphic(button);
                }
            }
        });
    }

    /**
     * 按钮点击事件处理器接口
     */
    public interface ButtonClickHandler {
        void handle(FindResult result);
    }
}