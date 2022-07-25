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
package org.apache.ibatis.reflection.wrapper;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * 对象或者集合的包装器
 *
 * @author Clinton Begin
 */
public interface ObjectWrapper {

  // get 获得被包装对象某个属性的值
  Object get(PropertyTokenizer prop);

  // set 设置被包装对象某个属性值
  void set(PropertyTokenizer prop, Object value);

  // 查找对应的属性名称
  String findProperty(String name, boolean useCamelCaseMapping);

  // 获得所有属性的getter方法名称列表
  String[] getGetterNames();

  // 获得所有属性的setter方法名称列表
  String[] getSetterNames();

  // 获得指定属性的setter方法类型
  Class<?> getSetterType(String name);

  // 获得指定属性的getter方法类型
  Class<?> getGetterType(String name);

  // 查看指定属性是否有setter方法
  boolean hasSetter(String name);

  // 查看指定属性是否有getter方法
  boolean hasGetter(String name);

  // 实例化某个属性的值
  MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

  // 判断是否是集合
  boolean isCollection();

  // 添加元素
  void add(Object element);

  // 添加全部元素
  <E> void addAll(List<E> element);

}
