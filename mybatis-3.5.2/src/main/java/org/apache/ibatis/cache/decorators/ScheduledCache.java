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
package org.apache.ibatis.cache.decorators;

import org.apache.ibatis.cache.Cache;

/**
 * @author Clinton Begin
 */

/**
 * 定时清理装饰器
 *
 * ScheduledCache 提供的定时清理功能并非是实时的。也就是说，即使已经满足了清理时间间隔的要求，只要 getSize、
 * putObject、getObject、removeObject 这四个方法没有被调用，则 clearWhenStale方法也不会被触发，也就不会发生缓存清理操作。
 * 这种非实时的设计方式也是值得参考的，因为实时操作需要增加单独的计时线程，会消耗大量的资源;而这种非实时的方式节约了资源，
 * 但同时也不会造成太大的误差。
 */
public class ScheduledCache implements Cache {

  // 被装饰的对象
  private final Cache delegate;
  // 清理的时间间隔
  protected long clearInterval;
  // 上次清理的时刻
  protected long lastClear;

  public ScheduledCache(Cache delegate) {
    this.delegate = delegate;
    this.clearInterval = 60 * 60 * 1000; // 1 hour
    this.lastClear = System.currentTimeMillis();
  }

  public void setClearInterval(long clearInterval) {
    this.clearInterval = clearInterval;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    clearWhenStale();
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object object) {
    clearWhenStale();
    delegate.putObject(key, object);
  }

  @Override
  public Object getObject(Object key) {
    return clearWhenStale() ? null : delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    clearWhenStale();
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    lastClear = System.currentTimeMillis();
    delegate.clear();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override

  /**
   * 根据清理时间间隔设置清理缓存
   * @return 是否发生了缓存清理
   */  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  private boolean clearWhenStale() {
    if (System.currentTimeMillis() - lastClear > clearInterval) {
      clear();
      return true;
    }
    return false;
  }

}
