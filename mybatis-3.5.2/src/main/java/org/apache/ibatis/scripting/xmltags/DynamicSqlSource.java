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
package org.apache.ibatis.scripting.xmltags;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * @author Clinton Begin
 */

/**
 * DynamicSqlSource:动态 SQL语句。所谓动态 SQL语句是指含 有动态 SQL节点(如if节点)或者含有“${}”占位符的语句。
 */
public class DynamicSqlSource implements SqlSource {

  private final Configuration configuration;
  private final SqlNode rootSqlNode;

  public DynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
    this.configuration = configuration;
    this.rootSqlNode = rootSqlNode;
  }

  /**
   * 获取一个BoundSql对象
   *
   * DynamicSqlSource和 RawSqlSource都会转 化为 StaticSqlSource，然后才能给出一个 BoundSql对象。
   *
   * @param parameterObject 参数对象
   * @return BoundSql对象
   */
  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    // 创建DynamicSqlSource的辅助类，用来记录DynamicSqlSource解析出来的SQL片段信息和参数信息
    DynamicContext context = new DynamicContext(configuration, parameterObject);
    // 这里会从根节点开始，对节点逐层调用apply方法，经过这一步后，动态节点"${}"都被替换，这样 DynamicSqlSource便不再是动态的，而是静态的。
    rootSqlNode.apply(context);
    // 处理占位符，汇总参数信息
    SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
    Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
    // 使用SqlSourceBuilder处理"#{}"，将其转化为"？"，最终生成了StaticSqlSource对象
    SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());
    BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
    // 把context.getBindings()的参数放到boundSql的metaParameters中进行保存
    context.getBindings().forEach(boundSql::setAdditionalParameter);
    return boundSql;
  }

}
