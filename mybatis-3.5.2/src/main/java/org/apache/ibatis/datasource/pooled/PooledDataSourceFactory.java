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
package org.apache.ibatis.datasource.pooled;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

/**
 * @author Clinton Begin
 */

/**
 *        <environment id="sit">
 *             <transactionManager type="JDBC"/>
 *             <dataSource type="POOLED">
 *                 <property name="driver" value="com.mysql.jdbc.Driver"/>
 *                 <property name="url" value="jdbc:mysql://127.0.0.1:3306/yeecode"/>
 *                 <property name="username" value="yeecode"/>
 *                 <property name="password" value="yeecode_passward"/>
 *                 <property name="defaultTransactionlsoltionLevel" value="1"/>
 *                 <!-- 最大活动连接数 -->
 *                 <property name="poolMaximumActiveConnections" value="15" />
 *                 <!-- 最大空闲连接数 -->
 *                 <property name="poolMaximumIdleConnections" value="5" />
 *                 <!-- 省略了一些其他属性 -->
 *             </dataSource>
 *         </environment>
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

  public PooledDataSourceFactory() {
    this.dataSource = new PooledDataSource();
  }

}
