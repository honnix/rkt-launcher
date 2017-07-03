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

import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.output.GcOutput;
import io.honnix.rkt.launcher.output.GcOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoMatter
public interface Gc extends CommandWithoutArgs<GcOptions, GcOutput> {

  @Override
  Optional<GcOptions> options();

  @Override
  default List<String> asList() {
    return asList("gc");
  }

  @Override
  default GcOutput parse(final String output) {
    final GcOutputBuilder gcOutputBuilder = GcOutput.builder();

    final Pattern markedPattern = Pattern.compile("moving pod \"(.+)\" to garbage");
    final Pattern removedPattern = Pattern.compile("Garbage collecting pod \"(.+)\"");
    final Pattern unremovedPattern = Pattern.compile("pod \"(.+)\" not removed");

    final Matcher markedMatcher = markedPattern.matcher(output);
    while (markedMatcher.find()) {
      gcOutputBuilder.marked().add(markedMatcher.group(1));
    }

    final Matcher removedMatcher = removedPattern.matcher(output);
    while (removedMatcher.find()) {
      gcOutputBuilder.removed().add(removedMatcher.group(1));
    }

    final Matcher unremovedMatcher = unremovedPattern.matcher(output);
    while (unremovedMatcher.find()) {
      gcOutputBuilder.unremoved().add(unremovedMatcher.group(1));
    }

    return gcOutputBuilder.build();
  }

  static GcBuilder builder() {
    return new GcBuilder();
  }
}
