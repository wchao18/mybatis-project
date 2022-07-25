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
package org.apache.ibatis.scripting.xmltags;

import java.util.regex.Pattern;

import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.parsing.TokenHandler;
import org.apache.ibatis.scripting.ScriptingException;
import org.apache.ibatis.type.SimpleTypeRegistry;

/**
 * @author Clinton Begin
 */

/**
 * TextSqlNode对象本身就很纯粹不需要解析，其实并不是。Te xtSqlNode对象的解析是必要的，因为它能够替换掉其中的“${}”占位 符
 *
 * extSqlNode 对象有两个内部 类:BindingTokenParser类和 DynamicCheckerTokenParser类
 */
public class TextSqlNode implements SqlNode {
  private final String text;
  private final Pattern injectionFilter;

  public TextSqlNode(String text) {
    this(text, null);
  }

  public TextSqlNode(String text, Pattern injectionFilter) {
    this.text = text;
    this.injectionFilter = injectionFilter;
  }

  /**
   * 判断当前节点是不是动态的
   * 对于 TextSqlNode对象而言，如果内 部含有“${}”占位符，那它就是动态的，否则就不是动态的。
   * @return 节点是否为动态
   */
  public boolean isDynamic() {
    // 占位符处理器，该处理器并不会处理占位符，而是判断是不是含有占位符
    DynamicCheckerTokenParser checker = new DynamicCheckerTokenParser();
    GenericTokenParser parser = createParser(checker);
    // 使用占位符处理器。如果节点内容中含有占位符，则DynamicCheckerTokenParser对象的isDynamic属性将会被置为true
    parser.parse(text);
    return checker.isDynamic();
  }

  /**
   * 完成该节点自身的解析
   *
   * TextSqlNode 类对应了字符串节点，字符串节点的应用非常广泛， 在 if 节点、foreach节点中也包含了字符串节点。例如，如下代码中 的 SQL片段就包含了字符串节点。
   *     <select id="selectUser">
   *       SELECT * FROM `user` WHERE `id` = #{id}
   *     </select>
   *
   * @param context 上下文环境，该节点自身的解析结果将合并到该上下文环境中
   * @return
   */
  @Override
  public boolean apply(DynamicContext context) {
    // 创建通用占位符解析器
    GenericTokenParser parser = createParser(new BindingTokenParser(context, injectionFilter));
    // 替换掉其中的 ${} 占位符
    context.appendSql(parser.parse(text));
    return true;
  }

  /**
   * 创建一个通用的占位符解析器，用来解析${}占位符
   * @param handler 用来处理${}占位符的专用处理器
   * @return 占位符解析器
   */
  private GenericTokenParser createParser(TokenHandler handler) {
    return new GenericTokenParser("${", "}", handler);
  }

  /**
   * BindingTokenParser:该对象的 handleToken方法会取出占位 符中的变量，
   * 然后使用该变量作为键去上下文环境中寻找对应的值。之后，会用找到的值替换占位符。因此，该对象可以完成占位符的替换工作
   */
  private static class BindingTokenParser implements TokenHandler {

    private DynamicContext context;
    private Pattern injectionFilter;

    public BindingTokenParser(DynamicContext context, Pattern injectionFilter) {
      this.context = context;
      this.injectionFilter = injectionFilter;
    }

    @Override
    public String handleToken(String content) {
      Object parameter = context.getBindings().get("_parameter");
      if (parameter == null) {
        context.getBindings().put("value", null);
      } else if (SimpleTypeRegistry.isSimpleType(parameter.getClass())) {
        context.getBindings().put("value", parameter);
      }
      Object value = OgnlCache.getValue(content, context.getBindings());
      String srtValue = value == null ? "" : String.valueOf(value); // issue #274 return "" instead of "null"
      checkInjection(srtValue);
      return srtValue;
    }

    private void checkInjection(String value) {
      if (injectionFilter != null && !injectionFilter.matcher(value).matches()) {
        throw new ScriptingException("Invalid input. Please conform to regex" + injectionFilter.pattern());
      }
    }
  }

  /**
   * DynamicCheckerTokenParser:该对象的 handleToken 方法会 置位成员属性isDynamic。因此该对象可以记录自身是否遇到过占位 符。
   */
  private static class DynamicCheckerTokenParser implements TokenHandler {

    private boolean isDynamic;

    public DynamicCheckerTokenParser() {
      // Prevent Synthetic Access
    }

    public boolean isDynamic() {
      return isDynamic;
    }

    @Override
    public String handleToken(String content) {
      this.isDynamic = true;
      return null;
    }
  }

}
