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
package io.honnix.rkt.launcher.options.image;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.options.Options;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface ExtractOptions extends Options {

  Optional<Boolean> overwrite();
  
  Optional<Boolean> rootfsOnly();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    overwrite().ifPresent(v -> builder.add(
        join("--overwrite", v)));
    rootfsOnly().ifPresent(v -> builder.add(
        join("--rootfs-only", v)));
    return builder.build();
  }

  static ExtractOptionsBuilder builder() {
    return new ExtractOptionsBuilder();
  }
}
