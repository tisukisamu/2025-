package com.example.backend.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class TestDataInserter {
    
    private static final String DB_URL = "jdbc:mysql://47.97.108.222:3306/gp13?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "kimi";
    private static final String DB_PASSWORD = "tR!9dB2x@7QwLpZ$mF8s";
    
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            System.out.println("正在连接数据库...");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                System.out.println("数据库连接成功！");
                
                String sqlFile = "src/main/resources/test_data.sql";
                System.out.println("正在读取SQL文件: " + sqlFile);
                
                StringBuilder sqlBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sqlBuilder.append(line).append("\n");
                    }
                }
                
                String[] sqlStatements = sqlBuilder.toString().split(";");
                
                int successCount = 0;
                int failCount = 0;
                
                try (Statement stmt = conn.createStatement()) {
                    for (String sql : sqlStatements) {
                        sql = sql.trim();
                        if (sql.isEmpty() || sql.startsWith("--") || sql.startsWith("SELECT")) {
                            if (sql.startsWith("SELECT")) {
                                try {
                                    ResultSet rs = stmt.executeQuery(sql);
                                    System.out.println("\n执行查询: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                                    int colCount = rs.getMetaData().getColumnCount();
                                    while (rs.next()) {
                                        StringBuilder row = new StringBuilder("  ");
                                        for (int i = 1; i <= colCount; i++) {
                                            if (i > 1) row.append(" | ");
                                            String value = rs.getString(i);
                                            row.append(value != null ? value : "NULL");
                                        }
                                        System.out.println(row);
                                    }
                                } catch (Exception e) {
                                    // 忽略查询错误
                                }
                            }
                            continue;
                        }
                        
                        try {
                            stmt.execute(sql);
                            successCount++;
                            if (sql.contains("INSERT")) {
                                System.out.println("✓ 执行成功: " + sql.substring(0, Math.min(60, sql.length())) + "...");
                            }
                        } catch (Exception e) {
                            failCount++;
                            if (!e.getMessage().contains("Duplicate")) {
                                System.out.println("✗ 执行失败: " + e.getMessage());
                            }
                        }
                    }
                }
                
                System.out.println("\n========================================");
                System.out.println("测试数据插入完成！");
                System.out.println("成功: " + successCount + " 条");
                System.out.println("失败(含重复): " + failCount + " 条");
                System.out.println("========================================");
                
                verifyData(conn);
            }
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verifyData(Connection conn) throws Exception {
        System.out.println("\n验证数据:");
        try (Statement stmt = conn.createStatement()) {
            String[] tables = {"users", "service_package", "pet_info", "appointment", "service_process", "memorial_album", "message"};
            for (String table : tables) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
                if (rs.next()) {
                    System.out.println("  " + table + ": " + rs.getInt(1) + " 条记录");
                }
            }
        }
    }
}
