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
package io.honnix.rkt.launcher.command;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.options.Options;
import io.honnix.rkt.launcher.output.Output;
import java.util.List;

public interface CommandWithoutArgs<T extends Options, S extends Output> extends Command<T, S> {

  default List<String> asList(final String command) {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    builder.add(command);
    options().ifPresent(v -> builder.addAll(v.asList()));
    return builder.build();
  }
}
