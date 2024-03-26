package com.zht.middleware.mybatis;

import org.dom4j.Element;
import org.xml.sax.InputSource;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//正则表达式
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// 工厂需要核心配置，核心配置需要Configuration提供，这个类就是通过读取XML配置文件来生成Configuration，来提供给工厂生成Session
public class SqlSessionFactoryBuilder {

    public DefaultSqlSessionFactory build(Reader reader){
        SAXReader saxReader = new SAXReader();
        try{
            // 解析XML对象 ， 将输入流用InputSource包装后使用saxReader.read生成Document对象
            Document document = saxReader.read(new InputSource(reader));

            // 开始解析XML
            Configuration configuration = parseConfiguration(document.getRootElement());

            return new DefaultSqlSessionFactory(configuration);
        }catch (Exception e){
            System.out.println("出错");
            return null;
        }
    }

    private Configuration parseConfiguration(Element root){
        Configuration configuration = new Configuration();
        // 开始填充配置类的三个属性
        configuration.setDataSource(dataSource(root.selectNodes("//dataSource")));

        configuration.setConnection(connection(configuration.dataSource));

        configuration.setMapperElement(mapperElement(root.selectNodes("mappers")));

        return configuration;
    }

    private Map<String, String> dataSource(List<Element> list) {
        Map<String, String> dataSource = new HashMap<>(4);

        Element element = list.get(0);
        List content = element.elements();
        for (Object o : content) {
            Element e = (Element) o;
            String name = e.attributeValue("name");
            String value = e.attributeValue("value");
            dataSource.put(name, value);
        }
        return dataSource;
    }

    private Connection connection(Map<String, String> dataSource) {
        try {
            Class.forName(dataSource.get("driver"));
            return DriverManager.getConnection(dataSource.get("url"), dataSource.get("username"), dataSource.get("password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, XNode> mapperElement(List<Element> list) {
        Map<String, XNode> map = new HashMap<>();

        Element element = list.get(0);
        List content = element.elements();
        for (Object o : content) {
            Element e = (Element) o;
            String resource = e.attributeValue("resource");

            try {
                Reader reader = Resources.getResourceAsReader(resource);
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new InputSource(reader));
                Element root = document.getRootElement();
                //命名空间
                String namespace = root.attributeValue("namespace");

                // SELECT
                List<Element> selectNodes = root.selectNodes("select");
                for (Element node : selectNodes) {
                    String id = node.attributeValue("id");
                    String parameterType = node.attributeValue("parameterType");
                    String resultType = node.attributeValue("resultType");
                    String sql = node.getText();

                    // ? 匹配   SELECT * FROM users WHERE id = #{userId} AND name = #{userName} ---> SELECT * FROM users WHERE id = ? AND name = ?
                    Map<Integer, String> parameter = new HashMap<>();
                    Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                    Matcher matcher = pattern.matcher(sql);
                    for (int i = 1; matcher.find(); i++) {
                        String g1 = matcher.group(1);
                        String g2 = matcher.group(2);
                        parameter.put(i, g2);
                        sql = sql.replace(g1, "?");
                    }

                    XNode xNode = new XNode();
                    xNode.setNamespace(namespace);
                    xNode.setId(id);
                    xNode.setParameterType(parameterType);
                    xNode.setResultType(resultType);
                    xNode.setSql(sql);
                    xNode.setParameter(parameter);

                    map.put(namespace + "." + id, xNode);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return map;
    }


}
