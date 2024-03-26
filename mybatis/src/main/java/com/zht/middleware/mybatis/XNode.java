package com.zht.middleware.mybatis;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class XNode {
    // 是
    private String namespace;
    private String id;
    private String parameterType;
    private String resultType;
    private String sql;
    private Map<Integer, String> parameter;
}
