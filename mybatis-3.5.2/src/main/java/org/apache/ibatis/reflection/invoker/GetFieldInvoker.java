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
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.Field;

import org.apache.ibatis.reflection.Reflector;

/**
 * @author Clinton Begin
 */
public class GetFieldInvoker implements Invoker {
  private final Field field;

  public GetFieldInvoker(Field field) {
    this.field = field;
  }

  /**
   * 代理方法
   *
   * @param target 被代理的目标对象
   * @param args 方法参数
   * @return
   * @throws IllegalAccessException
   */
  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException {
    try {
      // 直接通过反射获取目标属性的值
      return field.get(target);
    } catch (IllegalAccessException e) { // 如果属性无法访问
      if (Reflector.canControlMemberAccessible()) {
        // 如果属性的访问性可以修改，则将属性的可访问性修改为可访问
        field.setAccessible(true);
        // 再次通过反射获取目标属性的值
        return field.get(target);
      } else {
        throw e;
      }
    }
  }

  /**
   * 获取属性的类型
   *
   * @return 返回属性的类型
   */
  @Override
  public Class<?> getType() {
    return field.getType();
  }
}
