package com.alibaba.dataops.server.test.domain.data.service;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dataops.server.domain.data.api.enums.DbTypeEnum;
import com.alibaba.dataops.server.domain.data.api.param.datasource.DataSourceCloseParam;
import com.alibaba.dataops.server.domain.data.api.param.datasource.DataSourceCreateParam;
import com.alibaba.dataops.server.domain.data.api.service.DataSourceDataService;
import com.alibaba.dataops.server.domain.data.core.util.DataCenterUtils;
import com.alibaba.dataops.server.test.domain.data.service.dialect.DialectProperties;
import com.alibaba.dataops.server.test.domain.data.utils.TestUtils;
import com.alibaba.dataops.server.tools.base.wrapper.result.ActionResult;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据源测试
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class DataSourceDataServiceTest {
    @Resource
    private DataSourceDataService dataSourceDataService;
    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    @Order(1)
    public void createAndClose() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            DbTypeEnum dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();

            // 创建
            DataSourceCreateParam dataSourceCreateParam = new DataSourceCreateParam();
            dataSourceCreateParam.setDataSourceId(dataSourceId);
            dataSourceCreateParam.setDbType(dbTypeEnum.getCode());
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUsername(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            ActionResult actionResult = dataSourceDataService.create(dataSourceCreateParam);
            Assertions.assertTrue(actionResult.success(), "创建数据库连接池失败");
            Assertions.assertTrue(DataCenterUtils.DATA_SOURCE_CACHE.containsKey(dataSourceId), "创建数据库连接池失败");

            // 关闭
            DataSourceCloseParam dataSourceCloseParam = new DataSourceCloseParam();
            dataSourceCloseParam.setDataSourceId(dataSourceId);
            actionResult = dataSourceDataService.close(dataSourceCloseParam);
            Assertions.assertTrue(actionResult.success(), "关闭数据库连接池失败");

            Assertions.assertFalse(DataCenterUtils.DATA_SOURCE_CACHE.containsKey(dataSourceId), "关闭连接池失败");
        }
    }

}