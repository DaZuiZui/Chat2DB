package com.alibaba.dbhub.server.test.domain.data.service;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dbhub.server.domain.api.param.ConsoleConnectParam;
import com.alibaba.dbhub.server.domain.api.param.DataSourcePreConnectParam;
import com.alibaba.dbhub.server.domain.api.service.ConsoleService;
import com.alibaba.dbhub.server.domain.api.service.DataSourceService;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.param.console.ConsoleCloseParam;
import com.alibaba.dbhub.server.test.common.BaseTest;
import com.alibaba.dbhub.server.test.domain.data.service.dialect.DialectProperties;
import com.alibaba.dbhub.server.test.domain.data.utils.TestUtils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据源测试
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class ConsoleOperationsTest extends BaseTest {
    @Resource
    private DataSourceService dataSourceService;
    @Resource
    private ConsoleService consoleService;
    @Autowired
    private List<DialectProperties> dialectPropertiesList;

    @Test
    @Order(1)
    public void createAndClose() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            DbTypeEnum dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();
            dataSourceCreateParam.setType(dbTypeEnum.getCode());
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            dataSourceService.preConnect(dataSourceCreateParam);

            // 创建
            ConsoleConnectParam consoleCreateParam = new ConsoleConnectParam();
            consoleCreateParam.setDataSourceId(dataSourceId);
            consoleCreateParam.setConsoleId(consoleId);
            consoleCreateParam.setDatabaseName(dialectProperties.getDatabaseName());
            consoleService.createConsole(consoleCreateParam);

            // 关闭
            ConsoleCloseParam consoleCloseParam = new ConsoleCloseParam();
            consoleCloseParam.setDataSourceId(dataSourceId);
            consoleCloseParam.setConsoleId(consoleId);
            consoleService.closeConsole(consoleCloseParam);
            TestUtils.remove();
        }
    }

    @Test
    @Order(2)
    public void createAfterDataSourceClose() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            DbTypeEnum dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();
            dataSourceCreateParam.setType(dbTypeEnum.getCode());
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            dataSourceService.preConnect(dataSourceCreateParam);

            dataSourceService.close(dataSourceId);

            TestUtils.remove();
        }
    }

    @Test
    @Order(3)
    public void closeDataSourceAfterCreateConsole() {
        for (DialectProperties dialectProperties : dialectPropertiesList) {
            DbTypeEnum dbTypeEnum = dialectProperties.getDbType();
            Long dataSourceId = TestUtils.nextLong();
            Long consoleId = TestUtils.nextLong();
            TestUtils.buildContext(dialectProperties, dataSourceId, consoleId);

            DataSourcePreConnectParam dataSourceCreateParam = new DataSourcePreConnectParam();
            dataSourceCreateParam.setType(dbTypeEnum.getCode());
            dataSourceCreateParam.setUrl(dialectProperties.getUrl());
            dataSourceCreateParam.setUser(dialectProperties.getUsername());
            dataSourceCreateParam.setPassword(dialectProperties.getPassword());
            dataSourceService.preConnect(dataSourceCreateParam);

            // 创建控制台
            ConsoleConnectParam consoleCreateParam = new ConsoleConnectParam();
            consoleCreateParam.setDataSourceId(dataSourceId);
            consoleCreateParam.setConsoleId(consoleId);
            consoleCreateParam.setDatabaseName(dialectProperties.getDatabaseName());
            consoleService.createConsole(consoleCreateParam);

            dataSourceService.close(dataSourceId);
            TestUtils.remove();
        }
    }

}
