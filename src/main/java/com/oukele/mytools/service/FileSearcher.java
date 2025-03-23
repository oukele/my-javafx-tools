package com.oukele.mytools.service;

/**
 * @author oukele
 */


import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

public class FileSearcher {

    /**
     * 异步搜索包含关键字的文件。
     *
     * @param dirPath    目录路径
     * @param keyword    关键字
     * @param onFound    匹配到文件时的回调函数
     * @param onComplete 搜索完成时的回调函数
     * @param onError    发生错误时的回调函数
     */
    public void searchFilesContent(String dirPath, String keyword, Consumer<String> onFound, Runnable onComplete, Consumer<String> onError) {
        File directory = new File(dirPath);
        new Thread(() -> {
            try {
                searchFolder(directory, keyword, "content", onFound);
                onComplete.run(); // 搜索完成
            } catch (Exception e) {
                onError.accept("遍历目录时发生错误：" + e.getMessage());
            }
        }).start();
    }

    /**
     * 异步搜索包含关键字的文件名。
     *
     * @param dirPath    目录路径
     * @param keyword    关键字
     * @param onFound    匹配到文件时的回调函数
     * @param onComplete 搜索完成时的回调函数
     * @param onError    发生错误时的回调函数
     */
    public void searchFiles(String dirPath, String keyword, Consumer<String> onFound, Runnable onComplete, Consumer<String> onError) {
        File directory = new File(dirPath);
        new Thread(() -> {
            try {
                searchFolder(directory, keyword, "file", onFound);
                onComplete.run(); // 搜索完成
            } catch (Exception e) {
                onError.accept("遍历目录时发生错误：" + e.getMessage());
            }
        }).start();
    }

    public void searchFolder(File folder, String keyword, String type, Consumer<String> onFound) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是文件夹，则递归遍历
                searchFolder(file, keyword, type, onFound);
            } else {
                // 如果是文件，则检查文件内容中是否包含关键字
                if ("content".equals(type)) {
                    // 如果是文件，则检查文件内容中是否包含关键字
                    if (containsKeyword(file, keyword)) {
                        onFound.accept(file.getPath());
                    }
                    // 找文件名
                } else if ("file".equals(type)) {
                    String name = file.getName();
                    if (name.toLowerCase().contains(keyword.toLowerCase())) {
                        onFound.accept(file.getPath());
                    }
                }
            }
        }
    }

    /**
     * 检查给定的文件中是否包含指定的关键字。
     * 检查前先自动检测文件的编码，然后以该编码读取文件内容。
     *
     * @param file    要检查的文件
     * @param keyword 关键字
     * @return 如果文件中包含关键字返回 true，否则返回 false
     */
    public boolean containsKeyword(File file, String keyword) {
        // 先检测文件编码
        String encoding = detectFileEncoding(file);
        if (encoding == null) {
            // 如果无法检测，则使用默认编码 UTF-8
            encoding = StandardCharsets.UTF_8.name();
//            System.out.println("无法检测文件编码，使用默认编码 UTF-8: " + file.getAbsolutePath());
        } else {
//            System.out.println("检测到文件编码 " + encoding + " : " + file.getAbsolutePath());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), encoding))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(keyword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("读取文件出错: " + file.getAbsolutePath());
        }
        return false;
    }

    /**
     * 使用 juniversalchardet 检测给定文件的编码
     *
     * @param file 要检测的文件
     * @return 检测到的编码名称，如果检测失败则返回 null
     */
    public static String detectFileEncoding(File file) {
        byte[] buf = new byte[4096];
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int nread;
            // 这里可以增加一个条件判断，如样本数据超过一定大小时退出循环
            while ((nread = fis.read(buf)) > 0) {
                baos.write(buf, 0, nread);
                // 例如：若已读取超过 8KB，则退出
                if (baos.size() > 8192) {
                    break;
                }
            }

            // 累积到的样本数据
            byte[] sampleBytes = baos.toByteArray();

            // 使用 ICU4J 进行编码检测
            CharsetDetector detector = new CharsetDetector();
            detector.setText(sampleBytes);
            CharsetMatch match = detector.detect();
            return match != null ? match.getName() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
