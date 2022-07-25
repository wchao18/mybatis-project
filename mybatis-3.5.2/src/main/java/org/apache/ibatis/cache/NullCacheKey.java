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
package org.apache.ibatis.cache;

/**
 * @author Clinton Begin
 */

/**
 * 在缓存查询中，如果发现某个 CacheKey信息不全，则会返回 Null CacheKey对象，类似于返回一个 null值。但是 NullCacheKey毕竟是 C acheKey的子类，在接下来的处理中不会引发空指针异常。这种设计方 式也非常值得我们借鉴。
 */
public final class NullCacheKey extends CacheKey {

  private static final long serialVersionUID = 3704229911977019465L;

  public NullCacheKey() {
    super();
  }

  @Override
  public void update(Object object) {
    throw new CacheException("Not allowed to update a NullCacheKey instance.");
  }

  @Override
  public void updateAll(Object[] objects) {
    throw new CacheException("Not allowed to update a NullCacheKey instance.");
  }
}
