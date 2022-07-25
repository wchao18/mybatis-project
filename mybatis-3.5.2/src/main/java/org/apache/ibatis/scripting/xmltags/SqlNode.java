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

/**
 * @author Clinton Begin
 */

/**
 * MyBatis 有一个重要的优点是支持动态节点。可数据库本身并不认识这些节点，
 * 因此MyBatis 会先对这些节点进行处理后再交给数据库执行。这些节点在 MyBatis 中被定义为SqlNode
 */
public interface SqlNode {

  /**
   * 完成该节点自身的解析
   * @param context 上下文环境，该节点自身的解析结果将合并到该上下文环境中
   * @return 解析是否成功
   */
  boolean apply(DynamicContext context);
}
