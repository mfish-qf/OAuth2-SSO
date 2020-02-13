package com.qf.sso.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.qf.sso.core.exception.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author qiufeng
 * @date 2020/2/10 17:10
 */
@Slf4j
@Configuration
@MapperScan(basePackages = DataSourceConfig.PACKAGE, sqlSessionFactoryRef = "sysSqlSessionFactory")
public class DataSourceConfig {
    static final String PACKAGE = "com.qf.sso.core.dao";
    static final String MAPPER_LOCATION = "classpath:/mybatis-mappers/sys/*.xml";
    @Value("${sys.datasource.url}")
    private String url;

    @Value("${sys.datasource.username}")
    private String user;

    @Value("${sys.datasource.security}")
    private String security;

    @Value("${sys.datasource.driver-class-name}")
    private String driverClass;

    @Value("${spring.datasource.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.poolPreparedStatements}")
    private boolean poolPreparedStatements;

    @Value("${spring.datasource.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Value("${spring.datasource.filters}")
    private String filters;

    @Value("{spring.datasource.connectionProperties}")
    private String connectionProperties;

    @Bean(name = "sysDataSource")
    @ConfigurationProperties(prefix = "sys.datasource")
    @Primary
    public DataSource sysDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(security);

        //具体配置
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            dataSource.setFilters(filters);
        } catch (SQLException e) {
            log.error("错误:datasource设置filters异常!", e);
        }
        dataSource.setConnectionProperties(connectionProperties);
        return dataSource;
    }

    @Bean(name = "sysTransactionManager")
    @Primary
    public DataSourceTransactionManager sysTransactionManager() {
        return new DataSourceTransactionManager(sysDataSource());
    }

    @Bean(name = "sysSqlSessionFactory")
    @Primary
    public SqlSessionFactory sysSqlSessionFactory(@Qualifier("sysDataSource") DataSource sysDataSource) {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(sysDataSource);
        try {
            sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                    .getResources(DataSourceConfig.MAPPER_LOCATION));
            org.apache.ibatis.session.Configuration conf = new org.apache.ibatis.session.Configuration();
            conf.setMapUnderscoreToCamelCase(true);
            conf.setCallSettersOnNulls(true);
            conf.setDefaultEnumTypeHandler(org.apache.ibatis.type.EnumOrdinalTypeHandler.class);
            conf.setJdbcTypeForNull(JdbcType.NULL);
            sessionFactory.setConfiguration(conf);
            return sessionFactory.getObject();
        } catch (Exception ex) {
            throw new MyRuntimeException(ex);
        }
    }
}
