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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.BuilderException;

/**
 * @author Clinton Begin
 */

/**
 * MyBatis并没有将 OGNL工具直接暴露给各个 SQL节点使用，而是对 OGNL工具进行了进一步的易用性封装，得到了 ExpressionEvaluator 类，即表达式求值器
 */
public class ExpressionEvaluator {

  /**
   * 对结果为true、false形式的表达式求值
   *
   * 该方法能够对结果为 true、false形式的 表达式进行求值。例如，“<if test="name!=null">”节点中的 tr ue、false判断便可以直接调用该方法完成
   *
   * @param expression 表达式
   * @param parameterObject 参数对象
   * @return 求值结果
   */
  public boolean evaluateBoolean(String expression, Object parameterObject) {
    // 获取表达式的值
    Object value = OgnlCache.getValue(expression, parameterObject);
    if (value instanceof Boolean) { // 如果确实是Boolean形式的结果
      return (Boolean) value;
    }
    if (value instanceof Number) { // 如果是数值形式的结果
      return new BigDecimal(String.valueOf(value)).compareTo(BigDecimal.ZERO) != 0;
    }
    return value != null;
  }

  /**
   * 对结果为迭代形式的表达式进行求值
   *
   * 该方法 能对结果为迭代形式的表达式进行求值。这样，“<foreach item="id" collection="array" open="(" separator="，"close=")">#{id} </foreach>”节点中的迭代判断便可以直接调用该方法完成。
   * @param expression 表达式
   * @param parameterObject 参数对象
   * @return 求值结果
   */
  public Iterable<?> evaluateIterable(String expression, Object parameterObject) {
    // 获取表达式结果
    Object value = OgnlCache.getValue(expression, parameterObject);
    if (value == null) {
      throw new BuilderException("The expression '" + expression + "' evaluated to a null value.");
    }
    if (value instanceof Iterable) { // 如果结果是Iterable
      return (Iterable<?>) value;
    }
    if (value.getClass().isArray()) { // 如果结果是Array
      // the array may be primitive, so Arrays.asList() may throw
      // a ClassCastException (issue 209).  Do the work manually
      // Curse primitives! :) (JGB)

      // 得到的Array可能是原始的，因此调用Arrays.asList可能会抛出ClassCastException。因此要手工转为ArrayList
      int size = Array.getLength(value);
      List<Object> answer = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        Object o = Array.get(value, i);
        answer.add(o);
      }
      return answer;
    }
    if (value instanceof Map) { // 如果结果是Map
      return ((Map) value).entrySet();
    }
    throw new BuilderException("Error evaluating expression '" + expression + "'.  Return value (" + value + ") was not iterable.");
  }

}
