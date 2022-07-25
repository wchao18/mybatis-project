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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ibatis.cache.Cache;

/**
 * Lru (least recently used) cache decorator.
 *
 * @author Clinton Begin
 */

/**
 * LRU(Least Recently Used)即近期最少使用算法，该算法会在缓存数据数量达到设置的上限时将近期未使用的数据删除。
 * LruCache 装饰器便可以为缓存增加这些功能。
 */
public class LruCache implements Cache {

  // 被装饰对象
  private final Cache delegate;
  // 使用LinkedHashMap保存的缓存的键
  private Map<Object, Object> keyMap;
  // 最近最少使用的数据键
  private Object eldestKey;

  /**
   * LruCache构造方法
   * @param delegate  被装饰对象
   */
  public LruCache(Cache delegate) {
    this.delegate = delegate;
    setSize(1024);
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public int getSize() {
    return delegate.getSize();
  }

  /**
   * 设置缓存空间大小
   * @param size 缓存空间大小
   */
  public void setSize(final int size) {
    keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
      private static final long serialVersionUID = 4267176411845948333L;

      /**
       * 每次向LinkedHashMap放入数据时触发
       * @param eldest 最久未被访问的数据
       * @return 最久未被访问的元素是否应该删除
       */
      @Override
      protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
        boolean tooBig = size() > size;
        if (tooBig) {
          eldestKey = eldest.getKey();
        }
        return tooBig;
      }
    };
  }

  /**
   * 向缓存写入一条数据
   * @param key 数据的键
   * @param value 数据的值
   */
  @Override
  public void putObject(Object key, Object value) {
    // 真正的写入操作
    delegate.putObject(key, value);
    // 向keyMap中也放入键，并根据空间情况决定是否删除最久未被访问的数据
    cycleKeyList(key);
  }

  /**
   * 从缓存中读取一条数据
   * @param key 数据的键
   * @return 数据的值
   */
  @Override
  public Object getObject(Object key) {
    // 触及一下当前被访问的键，表明它被访问了
    keyMap.get(key); //touch
    // 真正的查询操作
    return delegate.getObject(key);
  }

  @Override
  public Object removeObject(Object key) {
    return delegate.removeObject(key);
  }

  @Override
  public void clear() {
    delegate.clear();
    keyMap.clear();
  }

  /**
   * 向keyMap中存入当前的键，并删除最久未被访问的数据
   * @param key 当前的键
   */
  private void cycleKeyList(Object key) {
    keyMap.put(key, key);
    if (eldestKey != null) {
      delegate.removeObject(eldestKey);
      eldestKey = null;
    }
  }

}
