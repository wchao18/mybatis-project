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
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * @author Clinton Begin
 */

/**
 * 日志装饰器，
 * 日志装饰器可以为缓存增加日志统计的功能，而需要统计的数据主 要是缓存命中率。
 * 所谓缓存命中率是指在多次访问缓存的过程中，能够在缓存中查询到数据的比率，
 * 日志装饰器的实现非常简单，即在缓存查询时记录查询的总次数与命中次数
 */
public class LoggingCache implements Cache {

  private final Log log;
  private final Cache delegate;
  protected int requests = 0;
  protected int hits = 0;

  public LoggingCache(Cache delegate) {
    this.delegate = delegate;
    this.log = LogFactory.getLog(getId());
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  @Override
  public void putObject(Object key, Object object) {
    delegate.putObject(key, object);
  }

  /**
   * 从缓存中读取一条信息
   * @param key 数据的键
   * @return 信息的值
   */
  @Override
  public Object getObject(Object key) {
    // 请求缓存次数+1
    requests++;
    final Object value = delegate.getObject(key);
    if (value != null) { // 命中缓存
      // 命中缓存次数+1
      hits++;
    }
    if (log.isDebugEnabled()) {
      log.debug("Cache Hit Ratio [" + getId() + "]: " + getHitRatio());
    }
    return value;
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
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

  /**
   * 获取缓存命中率
   * @return
   */
  private double getHitRatio() {
    return (double) hits / (double) requests;
  }

}
