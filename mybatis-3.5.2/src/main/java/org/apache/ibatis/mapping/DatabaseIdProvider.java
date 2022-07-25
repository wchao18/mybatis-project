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
package org.apache.ibatis.mapping;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * Should return an id to identify the type of this database.
 * That id can be used later on to build different queries for each database type
 * This mechanism enables supporting multiple vendors or versions
 *
 * @author Eduardo Macarron
 */

/**
 * 多数据支持的实现由 DatabaseIdProvider接口负责
 */
public interface DatabaseIdProvider {

  /**
   * setProperties方法用来将MyBatis配置文件中设置在databaseIdProvider节点中的信息写入VendorDatabaseIdProvider对象中。这些信息实际是数据库的别名信息
   * @param p
   */
  default void setProperties(Properties p) {
    // NOP
  }

  /**
   * getDatabaseId 方法用来给出当前传入的 DataSource 对象对应的 databaseId
   * @param dataSource
   * @return
   * @throws SQLException
   */
  String getDatabaseId(DataSource dataSource) throws SQLException;
}
