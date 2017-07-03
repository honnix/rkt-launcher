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
package io.honnix.rkt.launcher.command.image;

import io.honnix.rkt.launcher.command.CommandWithArgs;
import io.honnix.rkt.launcher.options.image.ExportOptions;
import io.honnix.rkt.launcher.output.Output;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface Export extends CommandWithArgs<ExportOptions, Output> {

  @Override
  Optional<ExportOptions> options();

  @Override
  default List<String> asList() {
    if (args().size() != 2) {
      throw new IllegalArgumentException("two arguments required");
    }
    return Image.image(asList("export"));
  }

  @Override
  default Output parse(final String output) {
    return Output.NULL;
  }

  static ExportBuilder builder() {
    return new ExportBuilder();
  }
}
