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
package io.honnix.rkt.launcher.options;

import com.google.common.collect.ImmutableList;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface StopOptions extends Options {

  Optional<Boolean> force();

  Optional<String> uuidFile();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    force().ifPresent(v -> builder.add(
        join("--force", v)));
    uuidFile().ifPresent(v -> builder.add(
        join("--uuid-file", v)));
    return builder.build();
  }

  static StopOptionsBuilder builder() {
    return new StopOptionsBuilder();
  }
}
