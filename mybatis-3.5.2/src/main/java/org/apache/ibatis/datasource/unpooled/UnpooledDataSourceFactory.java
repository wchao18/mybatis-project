/**
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.datasource.unpooled;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * @author Clinton Begin
 */

/**
 * UnpooledDataSourceFactory 是非池化的数据源工厂，UnpooledDataSourceFactory 直接在自身的构造方 法中创建了数据源对象，并保存在了自身的成员变量中
 * 非池化数据源是最简单的数据源，它只需要在每次请求连接时打开 连接，在每次连接结束时关闭连接即可
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {

  private static final String DRIVER_PROPERTY_PREFIX = "driver.";
  private static final int DRIVER_PROPERTY_PREFIX_LENGTH = DRIVER_PROPERTY_PREFIX.length();

  protected DataSource dataSource;

  /**
   * UnpooledDataSourceFactory 的构造方法，包含创建数据源的操作
   */
  public UnpooledDataSourceFactory() {
    this.dataSource = new UnpooledDataSource();
  }

  /**
   * 为数据源设置配置信息
   *
   * setProperties 方法负责为工厂 中的数据源设置属性
   *
   * 给数据源设置的属性分为两类:以“driver.”开头的属性是设置给数据源内包含的 DriverManager对象的;
   * 其他的属性是设置给数据源本身的。
   *
   * @param properties 配置信息
   */
  @Override
  public void setProperties(Properties properties) {
    // 驱动的属性
    Properties driverProperties = new Properties();
    // 生成一个包含DataSource对象的元对象
    MetaObject metaDataSource = SystemMetaObject.forObject(dataSource);
    // 设置属性
    for (Object key : properties.keySet()) {
      String propertyName = (String) key;
      if (propertyName.startsWith(DRIVER_PROPERTY_PREFIX)) { // 取出以"driver."开头的配置信息
        // 记录以"driver."开头的配置信息
        String value = properties.getProperty(propertyName);
        driverProperties.setProperty(propertyName.substring(DRIVER_PROPERTY_PREFIX_LENGTH), value);
      } else if (metaDataSource.hasSetter(propertyName)) {
        // 通过反射为DataSource设置其他的属性
        String value = (String) properties.get(propertyName);
        Object convertedValue = convertValue(metaDataSource, propertyName, value);
        metaDataSource.setValue(propertyName, convertedValue);
      } else {
        throw new DataSourceException("Unknown DataSource property: " + propertyName);
      }
    }
    if (driverProperties.size() > 0) {
      // 将以"driver."开头的配置信息放入DataSource的driverProperties属性中
      metaDataSource.setValue("driverProperties", driverProperties);
    }
  }

  /**
   * 获取数据源
   * @return
   */
  @Override
  public DataSource getDataSource() {
    return dataSource;
  }

  private Object convertValue(MetaObject metaDataSource, String propertyName, String value) {
    Object convertedValue = value;
    Class<?> targetType = metaDataSource.getSetterType(propertyName);
    if (targetType == Integer.class || targetType == int.class) {
      convertedValue = Integer.valueOf(value);
    } else if (targetType == Long.class || targetType == long.class) {
      convertedValue = Long.valueOf(value);
    } else if (targetType == Boolean.class || targetType == boolean.class) {
      convertedValue = Boolean.valueOf(value);
    }
    return convertedValue;
  }

}
