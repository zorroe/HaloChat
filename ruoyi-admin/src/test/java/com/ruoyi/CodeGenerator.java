package com.ruoyi;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.Types;
import java.util.Collections;


@Slf4j
public class CodeGenerator {


    @Test
    public void generator() {
        FastAutoGenerator.create("jdbc:mysql://192.168.1.101:13306/chat?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8", "chat", "y8NAiM7m7chP43bb")
                .globalConfig(builder -> {
                    builder.author("zorroe") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("E://"); // 指定输出目录
                })
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.SMALLINT) {
                                // 自定义类型转换
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                .packageConfig(builder ->
                        builder.parent("com.baomidou.mybatisplus.samples.generator") // 设置父包名
                                .moduleName("system") // 设置父包模块名
                                .pathInfo(Collections.singletonMap(OutputFile.xml, "D://")) // 设置mapperXml生成路径
                )
                .strategyConfig(builder -> {
                            builder.entityBuilder().enableLombok();
                            builder.addInclude("sys_user");
                        }
                )
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
