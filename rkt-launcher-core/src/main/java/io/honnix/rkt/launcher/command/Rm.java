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

import io.honnix.rkt.launcher.options.RmOptions;
import io.honnix.rkt.launcher.output.RmOutput;
import io.honnix.rkt.launcher.output.RmOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoMatter
public interface Rm extends CommandWithOptionalArgs<RmOptions, RmOutput> {

  @Override
  Optional<RmOptions> options();

  @Override
  default List<String> asList() {
    return asList("rm");
  }

  @Override
  default RmOutput parse(final String output) {
    final RmOutputBuilder rmOutputBuilder = RmOutput.builder();

    final Pattern removedPattern = Pattern.compile("\"(.+)\"");

    final Matcher removedMatchert = removedPattern.matcher(output);
    while (removedMatchert.find()) {
      rmOutputBuilder.removed().add(removedMatchert.group(1));
    }

    return rmOutputBuilder.build();
  }

  static RmBuilder builder() {
    return new RmBuilder();
  }
}
