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
import io.honnix.rkt.launcher.model.Pod;
import io.honnix.rkt.launcher.options.StatusOptions;
import io.honnix.rkt.launcher.output.StatusOutput;
import io.honnix.rkt.launcher.util.Json;
import io.norberg.automatter.AutoMatter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface Status extends CommandWithArgs<StatusOptions, StatusOutput> {

  @Override
  Optional<StatusOptions> options();

  @Override
  default List<String> asList() {
    return asList("status");
  }

  @Override
  default StatusOutput parse(final String output) {
    // Fix in a hard way without bothering annotation which will make serialization
    // inconsistent.
    final String modifiedOutput = output
        .replaceAll("\"app_names\"", "\"appNames\"")
        .replaceAll("\"started_at\"", "\"startedAt\"")
        .replaceAll("\"user_annotations\"", "\"userAnnotations\"")
        .replaceAll("\"user_labels\"", "\"userLabels\"");
    try {
      return StatusOutput.builder()
          .status(Json.deserialize(modifiedOutput, Pod.class))
          .build();
    } catch (IOException e) {
      throw new RktUnexpectedOutputException("failed parsing JSON output", e);
    }
  }

  static StatusBuilder builder() {
    return new StatusBuilder();
  }
}
