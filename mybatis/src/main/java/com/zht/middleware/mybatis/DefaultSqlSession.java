package com.zht.middleware.mybatis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSqlSession implements SqlSession {

    private Connection connection ;
    // 一个sql语句对应一个XNode
    private Map<String , XNode> mapperElement;

    public DefaultSqlSession(Connection connection, Map<String, XNode> mapperElement) {
        this.connection = connection;
        this.mapperElement = mapperElement;
    }

    @Override
    public <T> T selectOne(String statement) {
       try{
           XNode xNode = mapperElement.get(statement);
           // 执行sql
           PreparedStatement preparedStatement  = connection.prepareStatement(xNode.getSql());

           ResultSet resultSet = preparedStatement.executeQuery();
           List<T> object = resultSet2Obj(resultSet , Class.forName(xNode.getResultType()));

           return object.get(0);

       }catch (Exception e){}
       return null;
    }

    @Override
    public <T> T selctOne(String statement, Object parameters) {
        XNode xNode = mapperElement.get(statement);
        Map<Integer , String> parameterMap = xNode.getParameter();

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());

            buildParameter(preparedStatement, parameters ,parameterMap);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<T> objects = resultSet2Obj(resultSet ,Class.forName(xNode.getResultType()));
            return objects.get(0);
        }catch (Exception e){}
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement) {
        XNode xNode = mapperElement.get(statement);

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet2Obj(resultSet, Class.forName(xNode.getResultType()));
        }catch (Exception e){}
        return null;
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameters) {
        XNode xNode = mapperElement.get(statement);
        Map<Integer , String> parameterMap = xNode.getParameter();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(xNode.getSql());

            buildParameter(preparedStatement ,parameters ,parameterMap);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet2Obj(resultSet , Class.forName(xNode.getResultType()));
        }catch (Exception e){}
        return null;
    }

    @Override
    public void close() {
        if(connection == null)return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void buildParameter(PreparedStatement preparedStatement, Object parameter, Map<Integer, String> parameterMap) throws SQLException, IllegalAccessException {

        int size = parameterMap.size();
        // 单个参数
        if (parameter instanceof Long) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setLong(i, Long.parseLong(parameter.toString()));
            }
            return;
        }

        if (parameter instanceof Integer) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setInt(i, Integer.parseInt(parameter.toString()));
            }
            return;
        }

        if (parameter instanceof String) {
            for (int i = 1; i <= size; i++) {
                preparedStatement.setString(i, parameter.toString());
            }
            return;
        }

        Map<String, Object> fieldMap = new HashMap<>();

        // 对象参数
        Field[] declaredFields = parameter.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            field.setAccessible(true);
            Object obj = field.get(parameter);
            field.setAccessible(false);
            fieldMap.put(name, obj);
        }

        for (int i = 1; i <= size; i++) {
            String parameterDefine = parameterMap.get(i);
            Object obj = fieldMap.get(parameterDefine);

            if (obj instanceof Short) {
                preparedStatement.setShort(i, Short.parseShort(obj.toString()));
                continue;
            }

            if (obj instanceof Integer) {
                preparedStatement.setInt(i, Integer.parseInt(obj.toString()));
                continue;
            }

            if (obj instanceof Long) {
                preparedStatement.setLong(i, Long.parseLong(obj.toString()));
                continue;
            }

            if (obj instanceof String) {
                preparedStatement.setString(i, obj.toString());
                continue;
            }

            if (obj instanceof Date) {
                preparedStatement.setDate(i, (java.sql.Date) obj);
            }

        }

    }



    // 处理结果集，这里处理方式比以前复杂，因为以前是先画靶子后射箭，是直接从结果集点名拿字段，现在是不知道结果集有什么字段，所以更具有通用性
    private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        try {

            // 获取 ResultSet 的元数据，包括结果集中的列数、列名等信息。
            ResultSetMetaData metaData = resultSet.getMetaData();
            // 信息分配
            int columnCount = metaData.getColumnCount();
            // 每次遍历行值
            while (resultSet.next()) {

                T obj = (T) clazz.newInstance();

                // 遍历每一行
                for (int i = 1; i <= columnCount; i++) {
                    // 获取当前列的数据值
                    Object value = resultSet.getObject(i);

                    // 获取当前列的列名
                    String columnName = metaData.getColumnName(i);


                    // 构造当前列对应的 setter 方法名 比如：setName
                    String setMethod = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method method;

                    // 根据数据值的类型选择相应的 setter 方法
                    if (value instanceof Timestamp) {
                        method = clazz.getMethod(setMethod, java.util.Date.class);
                    } else {
                        method = clazz.getMethod(setMethod, value.getClass());
                    }
                    method.invoke(obj, value);
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
