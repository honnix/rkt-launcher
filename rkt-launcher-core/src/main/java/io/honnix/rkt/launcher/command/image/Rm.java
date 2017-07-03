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
import io.honnix.rkt.launcher.options.image.RmOptions;
import io.honnix.rkt.launcher.output.image.RmOutput;
import io.honnix.rkt.launcher.output.image.RmOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoMatter
public interface Rm extends CommandWithArgs<RmOptions, RmOutput> {

  @Override
  Optional<RmOptions> options();

  @Override
  default List<String> asList() {
    return Image.image(asList("rm"));
  }

  @Override
  default RmOutput parse(final String output) {
    final RmOutputBuilder rmOutputBuilder = RmOutput.builder();

    final Pattern removedPattern =
        Pattern.compile("successfully removed aci for image: \"(.+)\"");

    final Matcher removedMatcher = removedPattern.matcher(output);
    while (removedMatcher.find()) {
      rmOutputBuilder.removed().add(removedMatcher.group(1));
    }

    return rmOutputBuilder.build();
  }

  static RmBuilder builder() {
    return new RmBuilder();
  }
}
