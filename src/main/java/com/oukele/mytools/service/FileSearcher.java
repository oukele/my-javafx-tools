package com.oukele.mytools.service;

/**
 * @author oukele
 */
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileSearcher {

    /**
     * 在指定目录中递归搜索包含关键字的文件，返回文件路径列表。
     *
     * @param directory 根目录
     * @param keyword   关键字，如 "你好"
     * @return 包含关键字的文件路径列表
     */
    public static List<String> searchFiles(File directory, String keyword) {
        List<String> matchedFiles = new ArrayList<>();
        try {
            Path startPath = directory.toPath();
            Files.walk(startPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            // 读取所有文本内容（简单实现，可能针对大文件需优化）
                            String content = Files.readString(path, StandardCharsets.UTF_8);
                            if (content.contains(keyword)) {
                                matchedFiles.add(path.toString());
                            }
                        } catch (IOException e) {
                            // 忽略无法读取的文件
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matchedFiles;
    }
}
