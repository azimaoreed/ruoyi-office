package cn.iocoder.yudao.module.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 * <p>
 * 如遇启动问题，请查阅项目部署文档。
 * 如遇启动问题，请查阅项目部署文档。
 * 如遇启动问题，请查阅项目部署文档。
 *
  */
@SpringBootApplication(exclude = {
        org.springframework.ai.vectorstore.qdrant.autoconfigure.QdrantVectorStoreAutoConfiguration.class,
        org.springframework.ai.vectorstore.milvus.autoconfigure.MilvusVectorStoreAutoConfiguration.class,
}) // 解决 application-${profile}.yaml 配置文件下，通过 spring.autoconfigure.exclude 无法排除的问题
public class AiServerApplication {

    public static void main(String[] args) {
        // 如遇启动问题，请查阅项目部署文档。
        // 如遇启动问题，请查阅项目部署文档。
        // 如遇启动问题，请查阅项目部署文档。

        SpringApplication.run(AiServerApplication.class, args);

        // 如遇启动问题，请查阅项目部署文档。
        // 如遇启动问题，请查阅项目部署文档。
        // 如遇启动问题，请查阅项目部署文档。
    }

}
