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
package org.apache.ibatis.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 用来处理泛型
 * <p>
 * References a generic type.
 *
 * @param <T> the referenced type
 * @author Simone Tripodi
 * @since 3.1.0
 */

/**
 * 43个类型处理器可以处理不同 Java类型的数据，而这些类型处理 器都是 TypeHandler接口的子类，因此可以都作为 TypeHandler来使 用。
 * 那会不会遇到一个问题，当 MyBatis取到某一个 TypeHandler 时，却不知道它到底是用来处理哪一种 Java类型的处理器?
 * 为了解决这一问题，MyBatis 定义了一个 TypeReference 类。它 能够判断出一个TypeHandler用来处理的目标类型。
 * 而它判断的方法也 很简单:取出 TypeHandler实现类中的泛型参数 T的类型，这个值的类型也便是该 TypeHandler能处理的目标类型。
 * @param <T>
 */
public abstract class TypeReference<T> {

  // 泛型类中的实际类型
  private final Type rawType;

  protected TypeReference() {
    rawType = getSuperclassTypeParameter(getClass());
  }

  /**
   * 解析出当前TypeHandler实现类能够处理的目标类型，取出 TypeHandler实现类中的泛型参数 T的类型，这个值的类型也便是该 TypeHandler能处理的目标类型
   *
   * @param clazz TypeHandler实现类
   * @return 该TypeHandler实现类能够处理的目标类型
   */
  Type getSuperclassTypeParameter(Class<?> clazz) {
    // 获取clazz类的带有泛型的直接父类
    Type genericSuperclass = clazz.getGenericSuperclass();
    if (genericSuperclass instanceof Class) {
      // 进入这里说明genericSuperclass是class的实例
      // try to climb up the hierarchy until meet something useful
      if (TypeReference.class != genericSuperclass) { // genericSuperclass不是TypeReference类本身
        // 说明没有解析到足够上层，将clazz类的父类作为入参递归调用
        return getSuperclassTypeParameter(clazz.getSuperclass());
      }

      // 说明clazz实现了TypeReference类，但是却没有使用泛型
      throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
        + "Remove the extension or add a type parameter to it.");
    }

    // 运行到这里说明genericSuperclass是泛型类。获取泛型的第一个参数，即T
    Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    // TODO remove this when Reflector is fixed to return Types
    if (rawType instanceof ParameterizedType) { // 如果是参数化类型
      // 获取参数化类型的实际类型
      rawType = ((ParameterizedType) rawType).getRawType();
    }

    return rawType;
  }

  public final Type getRawType() {
    return rawType;
  }

  @Override
  public String toString() {
    return rawType.toString();
  }

}
