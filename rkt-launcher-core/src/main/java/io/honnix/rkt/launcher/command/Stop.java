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

import io.honnix.rkt.launcher.options.StopOptions;
import io.honnix.rkt.launcher.output.StopOutput;
import io.honnix.rkt.launcher.output.StopOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@AutoMatter
public interface Stop extends CommandWithOptionalArgs<StopOptions, StopOutput> {

  @Override
  Optional<StopOptions> options();

  @Override
  default List<String> asList() {
    return asList("stop");
  }

  @Override
  default StopOutput parse(final String output) {
    final StopOutputBuilder stopOutputBuilder = StopOutput.builder();

    final Pattern stoppedPattern = Pattern.compile("\"(.+)\"");

    final Matcher stoppedMatcher = stoppedPattern.matcher(output);
    while (stoppedMatcher.find()) {
      stopOutputBuilder.stopped().add(stoppedMatcher.group(1));
    }

    return stopOutputBuilder.build();
  }

  static StopBuilder builder() {
    return new StopBuilder();
  }
}
