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

import com.google.common.collect.Sets;
import io.honnix.rkt.launcher.options.RmOptions;
import io.honnix.rkt.launcher.output.RmOutput;
import io.honnix.rkt.launcher.output.RmOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    // when removing a prepared pod, there is an additional line in output like:
    // moving expired prepared pod "290710d1-0547-4343-8823-e36a27f8b95c" to garbage
    final Set<String> removed = Sets.newHashSet();

    final Pattern removedPattern = Pattern.compile("\"(.+)\"");

    final Matcher removedMatcher = removedPattern.matcher(output);
    while (removedMatcher.find()) {
      removed.add(removedMatcher.group(1));
    }
    rmOutputBuilder.removed(removed.iterator());

    return rmOutputBuilder.build();
  }

  static RmBuilder builder() {
    return new RmBuilder();
  }
}
