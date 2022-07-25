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
package org.apache.ibatis.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class ParamNameResolver {

  private static final String GENERIC_NAME_PREFIX = "param";

  /**
   * <p>
   * The key is the index and the value is the name of the parameter.<br />
   * The name is obtained from {@link Param} if specified. When {@link Param} is not specified,
   * the parameter index is used. Note that this index could be different from the actual index
   * when the method has special parameters (i.e. {@link RowBounds} or {@link ResultHandler}).
   * </p>
   * <ul>
   * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
   * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
   * <li>aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
   * </ul>
   * <p>
   * 方法输入参数的参数次序表。键为参数次序，值为参数名称或者参数@Param注解的值
   *
   * 参数顺序->参数名称
   */
  private final SortedMap<Integer, String> names;

  // 该方法输入参数中是否含有@Param注解
  private boolean hasParamAnnotation;

  /**
   * 参数名解析器的构造方法
   *
   * @param config 配置信息
   * @param method 要被分析的方法
   */
  public ParamNameResolver(Configuration config, Method method) {
    // 获取方法参数类型列表
    final Class<?>[] paramTypes = method.getParameterTypes();
    // 准备存取所有的参数的注解，是二维数组
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    // 循环处理各个参数
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      if (isSpecialParameter(paramTypes[paramIndex])) {
        // skip special parameters
        // 跳过特别参数
        continue;
      }
      // 参数名称
      String name = null;
      for (Annotation annotation : paramAnnotations[paramIndex]) {
        // 找出参数的注解
        if (annotation instanceof Param) {
          // 如果注解是 Param
          hasParamAnnotation = true;
          // 那就以Param中的值作为参数名
          name = ((Param) annotation).value();
          break;
        }
      }
      if (name == null) {
        // 否则，保留参数的原名称
        // @Param was not specified.
        if (config.isUseActualParamName()) {
          name = getActualParamName(method, paramIndex);
        }
        if (name == null) {
          // use the parameter index as the name ("0", "1", ...)
          // gcode issue #71
          // 参数名称取不到，则按照参数的index命名
          name = String.valueOf(map.size());
        }
      }
      // 参数顺序->参数名称
      map.put(paramIndex, name);
    }
    names = Collections.unmodifiableSortedMap(map);
  }

  private String getActualParamName(Method method, int paramIndex) {
    return ParamNameUtil.getParamNames(method).get(paramIndex);
  }

  private static boolean isSpecialParameter(Class<?> clazz) {
    return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
  }

  /**
   * 给出被解析的方法中的参数名称列表
   * <p>
   * Returns parameter names referenced by SQL providers.
   */
  public String[] getNames() {
    return names.values().toArray(new String[0]);
  }

  /**
   * <p>
   * A single non-special parameter is returned without a name.
   * Multiple parameters are named using the naming rule.
   * In addition to the default names, this method also adds the generic names (param1, param2,
   * ...).
   * </p>
   * <p>
   * 将被解析的方法中的参数名称列表与传入的`Object[] args`进行对应，返回对应关系。
   * <p>
   * 如果只有一个参数，直接返回参数
   * 如果有多个参数，则进行与之前解析出的参数名称进行对应，返回对应关系
   *
   * 参数名称->参数值
   * paramx->参数值
   */
  public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    if (args == null || paramCount == 0) {
      return null;
    } else if (!hasParamAnnotation && paramCount == 1) {
      // 如果只有一个参数，直接返回参数
      return args[names.firstKey()];
    } else {
      final Map<String, Object> param = new ParamMap<>();
      int i = 0;
      for (Map.Entry<Integer, String> entry : names.entrySet()) {
        // 首先按照类注释中提供的key,存入一遍   【参数的@Param名称 或者 参数排序：实参值】
        // 注意，key和value交换了位置
        param.put(entry.getValue(), args[entry.getKey()]);

        // 再按照param1, param2, ...的命名方式存入一遍
        // add generic param names (param1, param2, ...)
        final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
        // ensure not to overwrite parameter named with @Param
        if (!names.containsValue(genericParamName)) {
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }
}
