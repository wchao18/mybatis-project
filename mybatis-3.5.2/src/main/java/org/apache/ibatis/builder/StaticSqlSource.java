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
package org.apache.ibatis.builder;

import java.util.List;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * StaticSqlSource是 SqlSource的四个子类之一，它内部包含的 SQL语句中已经不存在“${}”和“#{}”这两种符号，而只有“?”
 *
 * StaticSqlSource:静态语句。语句中可能含有“?”，可以直 接提交给数据库执行。
 *
 * @author Clinton Begin
 */
public class StaticSqlSource implements SqlSource {

  // 经过解析后，不存在${} 和 #{} 这两种符号，只剩下？符号的SQL语句
  private final String sql;
  // SQL语句对应的参数列表
  private final List<ParameterMapping> parameterMappings;
  // 配置信息
  private final Configuration configuration;

  public StaticSqlSource(Configuration configuration, String sql) {
    this(configuration, sql, null);
  }

  public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
    this.sql = sql;
    this.parameterMappings = parameterMappings;
    this.configuration = configuration;
  }

  /**
   * 组建一个BoundSql对象
   * @param parameterObject 参数对象
   * @return 组建的BoundSql对象
   */
  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    return new BoundSql(configuration, sql, parameterMappings, parameterObject);
  }

}
