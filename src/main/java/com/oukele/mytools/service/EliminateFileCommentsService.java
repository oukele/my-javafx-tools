package com.oukele.mytools.service;

import cn.hutool.core.util.ObjUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 去除文件注释逻辑
 *
 * @author oukele
 */
public class EliminateFileCommentsService {

    private static final String TEMP_FILE_PREFIX = "Oukele";

    private final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    // 原子计数器
    private final AtomicInteger processedCount = new AtomicInteger(0);
    // 操作文件的原子计数器
    private final AtomicInteger operationFileCount = new AtomicInteger(0);
    private final AtomicInteger operationFailFileCount = new AtomicInteger(0);

    public void eliminateFileComments(String dirPath,
                                      Consumer<Integer> totalCountFileProcessed,
                                      Consumer<Integer> handlerSuccessFileProcessed,
                                      Consumer<Integer> handlerFailFileProcessed,
                                      Runnable onComplete,
                                      Consumer<String> onError) {
        Path dir = Path.of(dirPath);
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            // 创建线程池
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            try (Stream<Path> paths = Files.walk(dir)) {
                paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".lua")).forEach(p -> executor.execute(() -> {
                    handler(p, handlerSuccessFileProcessed, handlerFailFileProcessed, onError);
                    // 计数+1
                    totalCountFileProcessed.accept(processedCount.incrementAndGet());
                }));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            executor.shutdown();
            // 等待线程池中的任务执行完成
            try {
                executor.awaitTermination(2, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            onComplete.run();
            // 输出统计结果
            System.out.printf("处理完成！耗时 %.2f 秒%n", (System.currentTimeMillis() - startTime) / 1000.0);
        }).start();
    }

    /**
     * 去除文件注释
     *
     * @param source 文件路径
     */
    public void handler(Path source, Consumer<Integer> handlerSuccessFileProcessed, Consumer<Integer> handlerFailFileProcessed, Consumer<String> onError) {
        if (ObjUtil.isNull(source)) {
            return;
        }

        // 生成副本文件
        String tempFilePath = source.toString().substring(0, source.toString().lastIndexOf(".lua")) + "_" + TEMP_FILE_PREFIX + "Clean.lua";
        Path dest = Path.of(tempFilePath);
        try {
            Files.createFile(dest);
        } catch (IOException e) {
            onError.accept(source.toString());
            handlerFailFileProcessed.accept(operationFailFileCount.incrementAndGet());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // 处理逻辑
        try (BufferedReader reader = Files.newBufferedReader(source); BufferedWriter writer = Files.newBufferedWriter(dest)) {
            String line;
            boolean isHaveComment = false;
            while ((line = reader.readLine()) != null) {
                // 跳过纯注释行
                if (line.trim().startsWith("--")) {
                    isHaveComment = true;
                    continue;
                }

                // 处理行内注释
                boolean inString = false;
                StringBuilder cleanedLine = new StringBuilder();

                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);

                    // 检查字符串边界（简单实现，不考虑转义）
                    if (c == '"' || c == '\'') {
                        inString = !inString;
                    }

                    // 发现注释标记且不在字符串中
                    if (!inString && i + 1 < line.length() && c == '-' && line.charAt(i + 1) == '-') {
                        break;
                    }

                    cleanedLine.append(c);
                }

                // 写入非空行
                String result = cleanedLine.toString();
                if (!result.isEmpty()) {
                    writer.write(result);
                    writer.newLine(); // 保持原有换行
                }
                if (!result.equals(line)) {
                    isHaveComment = true;
                }
            }
            if (isHaveComment) {
                // 原子替换原文件（操作系统保证完整性）
                Files.move(dest, source, StandardCopyOption.REPLACE_EXISTING);
                // 计数加1
                handlerSuccessFileProcessed.accept(operationFileCount.incrementAndGet());
            }
        } catch (IOException e) {
            e.printStackTrace();
            onError.accept(source.toString());
            handlerFailFileProcessed.accept(operationFailFileCount.incrementAndGet());
            throw new RuntimeException(e);
        } finally {
            try {
                if (dest.toFile().exists()) {
                    Files.delete(dest);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
