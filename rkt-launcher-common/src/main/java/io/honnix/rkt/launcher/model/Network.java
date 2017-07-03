/*-
 * -\-\-
 * rkt-launcher
 * --
 * 
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package io.honnix.rkt.launcher.model;

import com.google.common.base.Joiner;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface Network {

  @AutoMatter
  interface Argument {

    String name();

    String value();

    default String toOption() {
      return name() + "=" + value();
    }

    static ArgumentBuilder builder() {
      return new ArgumentBuilder();
    }
  }

  String name();

  Optional<List<Argument>> args();

  default String toOption() {
    final StringBuilder sb = new StringBuilder(name());
    args().ifPresent(v -> sb.append(":")
        .append(Joiner.on(";").join(
            v.stream().map(Argument::toOption).iterator())));
    return sb.toString();
  }

  Network NONE = builder().name("none").build();

  Network DEFAULT = builder().name("default").build();

  static NetworkBuilder builder() {
    return new NetworkBuilder();
  }
}
