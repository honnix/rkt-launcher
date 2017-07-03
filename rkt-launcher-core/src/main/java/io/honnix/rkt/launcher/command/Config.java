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

import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.options.Options;
import io.honnix.rkt.launcher.output.ConfigOutput;
import io.honnix.rkt.launcher.util.Json;
import io.norberg.automatter.AutoMatter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface Config extends CommandWithoutArgs<Options, ConfigOutput> {

  @Override
  default Optional<Options> options() {
    return Optional.of(Options.NULL);
  }

  @Override
  default List<String> asList() {
    return asList("config");
  }

  @Override
  default ConfigOutput parse(final String output) {
    // rkt returns "stage1-images" which does not agree with any other property
    // using annotation can fix it but will make serialization inconsistent again.
    final String modifiedOutput = output.replaceAll("\"stage1-images\"", "\"stage1Images\"");
    try {
      return ConfigOutput.builder()
          .config(Json.deserialize(modifiedOutput,
                                   io.honnix.rkt.launcher.model.config.Config.class))
          .build();
    } catch (IOException e) {
      throw new RktUnexpectedOutputException("failed parsing JSON output", e);
    }
  }

  Config COMMAND = builder().build();

  static ConfigBuilder builder() {
    return new ConfigBuilder();
  }
}
