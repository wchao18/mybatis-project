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
package org.apache.ibatis.parsing;

import java.util.Properties;

/**
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */

/**
 * 属性解析器，内部使用 GenericTokenParser、TokenHandler完成属性解析功能
 */
public class PropertyParser {

  private static final String KEY_PREFIX = "org.apache.ibatis.parsing.PropertyParser.";
  /**
   * The special property key that indicate whether enable a default value on placeholder.
   * <p>
   *   The default value is {@code false} (indicate disable a default value on placeholder)
   *   If you specify the {@code true}, you can specify key and default value on placeholder (e.g. {@code ${db.username:postgres}}).
   * </p>
   * @since 3.4.2
   */
  public static final String KEY_ENABLE_DEFAULT_VALUE = KEY_PREFIX + "enable-default-value";

  /**
   * The special property key that specify a separator for key and default value on placeholder.
   * <p>
   *   The default separator is {@code ":"}.
   * </p>
   * @since 3.4.2
   */
  public static final String KEY_DEFAULT_VALUE_SEPARATOR = KEY_PREFIX + "default-value-separator";

  private static final String ENABLE_DEFAULT_VALUE = "false";
  private static final String DEFAULT_VALUE_SEPARATOR = ":";

  private PropertyParser() {
    // Prevent Instantiation
  }

  /**
   * 进行字符串中属性变量的替换，这样一来，只要在 XML文件中使用“${”和“}”包围一个变量 名，则该变量名就会被替换成 properties节点中对应的值
   * @param string 输入的字符串，可能包含属性变量
   * @param variables 属性映射信息
   * @return 经过属性变量替换的字符串
   */
  public static String parse(String string, Properties variables) {
    // 创建负责字符串替换的类
    VariableTokenHandler handler = new VariableTokenHandler(variables);
    // 创建通用占位符解析器
    GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
    // 开展解析，即替换占位符中的值
    return parser.parse(string);
  }

  private static class VariableTokenHandler implements TokenHandler {
    // 输入的属性变量，是HashTable的子类
    private final Properties variables;
    // 是否启用默认值
    private final boolean enableDefaultValue;
    // 如果启用默认值，则表示键和默认值之间的分隔符
    private final String defaultValueSeparator;

    private VariableTokenHandler(Properties variables) {
      this.variables = variables;
      this.enableDefaultValue = Boolean.parseBoolean(getPropertyValue(KEY_ENABLE_DEFAULT_VALUE, ENABLE_DEFAULT_VALUE));
      this.defaultValueSeparator = getPropertyValue(KEY_DEFAULT_VALUE_SEPARATOR, DEFAULT_VALUE_SEPARATOR);
    }

    private String getPropertyValue(String key, String defaultValue) {
      return (variables == null) ? defaultValue : variables.getProperty(key, defaultValue);
    }

    /**
     * handleToken 方法中传入输入参数后， 该方法会以输入参数为键尝试从 variables属性中寻找对应的值返回。
     * 在这个由键寻值的过程中还可以支持默认值
     *
     * 根据一个字符串，给出另外一个字符串。多用在字符串替换等处
     * 具体实现中，会议content作为键，从variables中找出并返回对应的值
     * 由键寻值的过程中支持设置默认值，如果启用默认值，则content形如"key:defaultValue"
     * 如果没有启用默认值，则content形如"key"
     * @param content 输入的字符串
     * @return 输出的字符串
     */
    @Override
    public String handleToken(String content) {
      if (variables != null) { // variables不为null
        String key = content;
        if (enableDefaultValue) { // 如果启用默认值，则设置默认值
          // 找出键与默认值分隔符的位置
          final int separatorIndex = content.indexOf(defaultValueSeparator);
          String defaultValue = null;
          if (separatorIndex >= 0) {
            // 分隔符以前是键
            key = content.substring(0, separatorIndex);
            // 分隔符以后是默认值
            defaultValue = content.substring(separatorIndex + defaultValueSeparator.length());
          }
          if (defaultValue != null) {
            return variables.getProperty(key, defaultValue);
          }
        }
        if (variables.containsKey(key)) {
          // 尝试寻找非默认的值
          return variables.getProperty(key);
        }
      }
      // 如果variables为null,不发生任何替换，直接原样返回
      return "${" + content + "}";
    }
  }

}
