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

import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;

/**
 * MyBatis 的 resultMap 标签支持继承，resultMap 继承关系的解析由 ResultMapResolver 类来完成
 *
 * @author Eduardo Macarron
 */
public class ResultMapResolver {
  // 建造者辅助类
  private final MapperBuilderAssistant assistant;
  // ResultMap 的 id
  private final String id;
  // ResultMap 的 type属性，即目标对象类型
  private final Class<?> type;
  // ResultMap 的 extends属性，即继承属性
  private final String extend;
  // ResultMap 的 Discriminator 节点，即鉴别器
  private final Discriminator discriminator;
  // ResultMap 中的属性映射列表
  private final List<ResultMapping> resultMappings;
  // ResultMap 的autoMapping属性，即是否开启自动映射
  private final Boolean autoMapping;

  public ResultMapResolver(MapperBuilderAssistant assistant, String id, Class<?> type, String extend, Discriminator discriminator, List<ResultMapping> resultMappings, Boolean autoMapping) {
    this.assistant = assistant;
    this.id = id;
    this.type = type;
    this.extend = extend;
    this.discriminator = discriminator;
    this.resultMappings = resultMappings;
    this.autoMapping = autoMapping;
  }

  /**
   * 完成 ResultMap 的继承关系解析
   * @return
   */
  public ResultMap resolve() {
    return assistant.addResultMap(this.id, this.type, this.extend, this.discriminator, this.resultMappings, this.autoMapping);
  }

}
