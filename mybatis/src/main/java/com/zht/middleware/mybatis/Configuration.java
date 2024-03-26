package com.zht.middleware.mybatis;

import lombok.Setter;

import java.sql.Connection;
import java.util.Map;


@Setter
public class Configuration {
    protected Connection connection;
    protected Map<String, String> dataSource;
    protected Map<String, XNode> mapperElement;
}
