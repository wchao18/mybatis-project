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
package org.apache.ibatis.cache.decorators;

import org.apache.ibatis.cache.Cache;

/**
 * @author Clinton Begin
 */

/**
 * 同步装饰器，
 *
 * 在使用 MyBatis的过程中，可能会出现多个线程同时访问一个缓存 的情况。例如，
 * 在代码19-11所示的映射文件中，如果多个线程同时调用 selectUsers方法，
 * 则这两个线程会同时访问 id为“com.github.ye ecode.mybatisdemo.dao.UserDao”的这个缓存
 *
 * ```xml
 * <mapper namespace="com.github.yeecode.mybatisdemo.dao.UserDao">
 *     <cache/>
 *     <select id="selectUsers" resultType="User">
 *         SELECT * FROM `user`;
 *     </select>
 * </mapper>
 * ```
 * 而缓存实现类 PerpetualCache 并没有增加任何保证多线程安全的措施，这会引发多线程安全问题。
 * MyBatis 将保证缓存多线程安全这项工作交给了 SynchronizedCac he 装饰器来完成。
 * SynchronizedCache 装饰器的实现非常简单，它直接在被包装对象的操作方法外围增加了synchronized关键字，
 * 将被包装对象的方法转变为了同步方法。
 */
public class SynchronizedCache implements Cache {

  private final Cache delegate;

  public SynchronizedCache(Cache delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public synchronized int getSize() {
    return delegate.getSize();
  }

  @Override
  public synchronized void putObject(Object key, Object object) {
    delegate.putObject(key, object);
  }

  @Override
  public synchronized Object getObject(Object key) {
    return delegate.getObject(key);
  }

  @Override
  public synchronized Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public synchronized void clear() {
    delegate.clear();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

}
