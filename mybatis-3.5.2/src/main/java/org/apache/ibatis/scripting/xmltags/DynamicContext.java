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

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * @author Clinton Begin
 */

/**
 * 在进行 SQL节点树的解析时，需要不断保存已经解析完成 的 SQL片段;
 * 另一方面，在进行SQL节点树的解析时也需要一些参数和 环境信息作为解析的依据。
 * 以上这两个功能是由动态上下文 DynamicContext提供的
 */
public class DynamicContext {

  public static final String PARAMETER_OBJECT_KEY = "_parameter";
  public static final String DATABASE_ID_KEY = "_databaseId";

  static {
    OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
  }

  // 上下文环境
  private final ContextMap bindings;
  // 用于拼装SQL语句片段
  private final StringJoiner sqlBuilder = new StringJoiner(" ");
  // 解析时的唯一编号，防止解析混乱
  private int uniqueNumber = 0;

  /**
   * DynamicContext构造方法
   * @param configuration 配置信息
   * @param parameterObject 用户传入的查询参数对象
   */
  public DynamicContext(Configuration configuration, Object parameterObject) {
    if (parameterObject != null && !(parameterObject instanceof Map)) {
      // 获取参数对象的元对象
      MetaObject metaObject = configuration.newMetaObject(parameterObject);
      // 判断参数对象本身是否有对应的类型处理器
      boolean existsTypeHandler = configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass());
      // 方法上下文信息
      bindings = new ContextMap(metaObject, existsTypeHandler);
    } else {
      // 上线文信息为空
      bindings = new ContextMap(null, false);
    }
    // 把参数对象放入上下文信息
    bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
    // 把数据库id放入上下文信息
    bindings.put(DATABASE_ID_KEY, configuration.getDatabaseId());
  }

  public Map<String, Object> getBindings() {
    return bindings;
  }

  public void bind(String name, Object value) {
    bindings.put(name, value);
  }

  public void appendSql(String sql) {
    sqlBuilder.add(sql);
  }

  public String getSql() {
    return sqlBuilder.toString().trim();
  }

  public int getUniqueNumber() {
    return uniqueNumber++;
  }

  /**
   * DynamicContext中还有一个 ContextMap，它是 HashMap的子类。 在进行数据查询时，
   * DynamicContext会先从 HashMap中查询，如果查询 失败则会从参数对象的属性中查询。
   * 正是基于这一点，我们可以在编写 SQL 语句时直接引用参数对象的属性。
   *
   * 通过如下源码，我们就知道为什么在书写映射文件 时既能够直接引用实参，又能直接引用实参的属性。
   */
  static class ContextMap extends HashMap<String, Object> {
    private static final long serialVersionUID = 2977601501966151582L;
    private final MetaObject parameterMetaObject;
    private final boolean fallbackParameterObject;

    public ContextMap(MetaObject parameterMetaObject, boolean fallbackParameterObject) {
      this.parameterMetaObject = parameterMetaObject;
      this.fallbackParameterObject = fallbackParameterObject;
    }

    /**
     * 根据索引键，会尝试从HashMap中寻找，失败后会再尝试从parameterMetaObject中寻找
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(Object key) {
      String strKey = (String) key;
      // 如果Map中包含对应的键，直接返回
      if (super.containsKey(strKey)) {
        return super.get(strKey);
      }

      // 如果HashMap中不含有对应的键，则尝试从参数对象的原对象中获取
      if (parameterMetaObject == null) {
        return null;
      }

      if (fallbackParameterObject && !parameterMetaObject.hasGetter(strKey)) {
        return parameterMetaObject.getOriginalObject();
      } else {
        // issue #61 do not modify the context when reading
        return parameterMetaObject.getValue(strKey);
      }
    }
  }

  static class ContextAccessor implements PropertyAccessor {

    @Override
    public Object getProperty(Map context, Object target, Object name) {
      Map map = (Map) target;

      Object result = map.get(name);
      if (map.containsKey(name) || result != null) {
        return result;
      }

      Object parameterObject = map.get(PARAMETER_OBJECT_KEY);
      if (parameterObject instanceof Map) {
        return ((Map)parameterObject).get(name);
      }

      return null;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) {
      Map<Object, Object> map = (Map<Object, Object>) target;
      map.put(name, value);
    }

    @Override
    public String getSourceAccessor(OgnlContext arg0, Object arg1, Object arg2) {
      return null;
    }

    @Override
    public String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2) {
      return null;
    }
  }
}
