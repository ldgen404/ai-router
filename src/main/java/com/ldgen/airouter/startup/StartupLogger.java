package com.ldgen.airouter.startup;

import cn.hutool.core.date.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@Slf4j
public class StartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    private static final long START_TIME = SystemClock.now();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            Environment env = event.getApplicationContext().getEnvironment();

            String protocol =
                    env.getProperty("server.ssl.key-store") != null ? "https" : "http";

            String port = env.getProperty("server.port", "8080");

            String docPath =
                    env.getProperty("ldgen.doc-path", "api/doc.html#/home");

            String docUrl =
                    protocol + "://localhost:" + port + "/" + docPath;

            long cost = (SystemClock.now() - START_TIME) / 1000;

            log.info("""
                            
                             * 🔥 项目已就绪！
                             * ----------------------------------------------------------
                             * 📌 环境信息
                             * • 应用：{}
                             * • 环境：{}
                             * • 耗时：{} s
                             * ----------------------------------------------------------
                             * 🌐 访问入口
                             * • Local    → {}://localhost:{}
                             * • External → {}://{}:{}
                             * • Doc      → {}
                            
                            """,
                    env.getProperty("spring.application.name", "aicodmaster"),
                    String.join(",", env.getActiveProfiles()),
                    cost,
                    protocol,
                    port,
                    protocol,
                    InetAddress.getLocalHost().getHostAddress(),
                    port,
                    docUrl
            );

        } catch (Exception e) {
            log.warn("打印启动信息失败", e);
        }
    }
}

/**
 *
 * 🔥 项目已就绪！
 * <p>
 * 📌 环境信息
 * • 应用：{}
 * • 环境：{}
 * • 耗时：{} s
 * <p>
 * 🌐 访问入口
 * • Local    → {}://localhost:{}
 * • External → {}://{}:{}
 * • Doc      → {}
 */

/**
 * ----------------------------------------------------------
 *                             Application '{}' 启动成功!
 *                             环境:      {}
 *                             耗时:      {} s
 *                             Local:    {}://localhost:{}
 *                             External: {}://{}:{}
 *                             项目接口地址：{}
 * ----------------------------------------------------------
 */