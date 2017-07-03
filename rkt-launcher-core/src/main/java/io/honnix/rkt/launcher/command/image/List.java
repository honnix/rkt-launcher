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

import com.fasterxml.jackson.core.type.TypeReference;
import io.honnix.rkt.launcher.command.CommandWithoutArgs;
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.options.image.ListOptions;
import io.honnix.rkt.launcher.output.image.ListOutput;
import io.honnix.rkt.launcher.util.Json;
import io.norberg.automatter.AutoMatter;
import java.io.IOException;
import java.util.Optional;

@AutoMatter
public interface List extends CommandWithoutArgs<ListOptions, ListOutput> {

  @Override
  Optional<ListOptions> options();

  @Override
  default java.util.List<String> asList() {
    return Image.image(asList("list"));
  }

  @Override
  default ListOutput parse(final String output) {
    try {
      final java.util.List<io.honnix.rkt.launcher.model.Image> images =
          Json.deserializeSnakeCase(
              output,
              new TypeReference<java.util.List<io.honnix.rkt.launcher.model.Image>>() {
              });
      return ListOutput.builder()
          .images(images)
          .build();
    } catch (IOException e) {
      throw new RktUnexpectedOutputException("failed parsing JSON output", e);
    }
  }

  static ListBuilder builder() {
    return new ListBuilder();
  }
}
