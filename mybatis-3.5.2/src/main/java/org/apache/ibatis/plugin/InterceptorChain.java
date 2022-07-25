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
package org.apache.ibatis.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Clinton Begin
 */
public class InterceptorChain {

  private final List<Interceptor> interceptors = new ArrayList<>();

  /**
   * MyBatis 中一共只有四个类的对象可以被拦截器替 换，它们分别是ParameterHandler、ResultSetHandler、StatementHan dler 和 Executor。而且替换只能发生在固定的地方，我们称其为拦截 点
   *
   * 向所有拦截器链提供目标对象，由拦截器链给出替换目标对象的对象
   * @param target 目标对象，是Mybatis中支持拦截的几个类（ParameterHandler、ResultSetHandler、StatementHandler、Executor） 的实例
   * @return 用来替换目标对象的对象
   */
  public Object pluginAll(Object target) {
    // 依次交给每个拦截器完成目标对象的替换工作
    for (Interceptor interceptor : interceptors) {
      target = interceptor.plugin(target);
    }
    return target;
  }

  public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }

  public List<Interceptor> getInterceptors() {
    return Collections.unmodifiableList(interceptors);
  }

}
