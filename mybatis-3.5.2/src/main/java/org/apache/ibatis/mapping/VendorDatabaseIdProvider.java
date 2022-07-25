/**
 *    Copyright 2009-2022 the original author or authors.
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
package org.apache.ibatis.mapping;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * Vendor DatabaseId provider.
 *
 * It returns database product name as a databaseId.
 * If the user provides a properties it uses it to translate database product name
 * key="Microsoft SQL Server", value="ms" will return "ms".
 * It can return null, if no database product name or
 * a properties was specified and no translation was found.
 *
 * @author Eduardo Macarron
 */
public class VendorDatabaseIdProvider implements DatabaseIdProvider {

  private Properties properties;

  /**
   * getDatabaseId 方法用来给出当前传入的 DataSource 对象对应的 databaseId
   * @param dataSource
   * @return
   */
  @Override
  public String getDatabaseId(DataSource dataSource) {
    if (dataSource == null) {
      throw new NullPointerException("dataSource cannot be null");
    }
    try {
      return getDatabaseName(dataSource);
    } catch (Exception e) {
      LogHolder.log.error("Could not get a databaseId from dataSource", e);
    }
    return null;
  }

  /**
   *     <databaseIdProvider type="DB_VENDOR">
   *         <property name="MySQL" value="mysql" />
   *         <property name="SQL Server" value="sqlserver" />
   *     </databaseIdProvider>
   *
   * setProperties方法用来将MyBatis配置文件中设置在databaseIdProvider节点中的信息写入VendorDatabaseIdProvider对象中
   * 这些信息实际是数据库的别名信息
   * @param p
   */
  @Override
  public void setProperties(Properties p) {
    this.properties = p;
  }

  /**
   * 获取当前的数据源类型的别名，
   * getDatabaseName方法做了两个工作，首先是获取当前数据源的类 型，然后是将数据源类型映射为我们在 databaseIdProvider节点中设 置的别名。
   * 这样，在需要执行 SQL语句时，就可以根据数据库操作节点 中的 databaseId设置对 SQL语句进行筛选
   * @param dataSource 数据源
   * @return 数据源类型别名
   * @throws SQLException
   */
  private String getDatabaseName(DataSource dataSource) throws SQLException {
    // 获取当前连接的数据库名，即获取数据库类型
    String productName = getDatabaseProductName(dataSource);
    if (this.properties != null) {
      // 如果设置有properties值，则根据将获取的数据库名称作为模糊的key,映射为对应的value，即将数据源类型映射为我们在 databaseIdProvider节点中设 置的别名。
      for (Map.Entry<Object, Object> property : properties.entrySet()) {
        if (productName.contains((String) property.getKey())) {
          return (String) property.getValue();
        }
      }
      // 没有找到对应映射
      // no match, return null
      return null;
    }
    return productName;
  }

  // 从连接中获取当前数据库的产品名
  private String getDatabaseProductName(DataSource dataSource) throws SQLException {
    Connection con = null;
    try {
      con = dataSource.getConnection();
      DatabaseMetaData metaData = con.getMetaData();
      return metaData.getDatabaseProductName();
    } finally {
      if (con != null) {
        try {
          con.close();
        } catch (SQLException e) {
          // ignored
        }
      }
    }
  }

  private static class LogHolder {
    private static final Log log = LogFactory.getLog(VendorDatabaseIdProvider.class);
  }

}
