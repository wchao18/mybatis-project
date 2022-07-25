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
package org.apache.ibatis.logging;

/**
 * @author Clinton Begin
 */
public interface Log {

  // 判断打印Debug级别日志功能是否开启
  boolean isDebugEnabled();

  // 判断打印Trace级别日志功能是否开启
  boolean isTraceEnabled();

  // 打印error级别日志
  void error(String s, Throwable e);

  // 打印error级别日志
  void error(String s);

  // 打印debug级别日志
  void debug(String s);

  // 打印trace级别日志
  void trace(String s);

  // 打印warn级别日志
  void warn(String s);

}
