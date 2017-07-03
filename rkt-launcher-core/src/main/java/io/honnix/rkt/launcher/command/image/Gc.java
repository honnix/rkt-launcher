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

import io.honnix.rkt.launcher.command.CommandWithoutArgs;
import io.honnix.rkt.launcher.options.image.GcOptions;
import io.honnix.rkt.launcher.output.image.GcOutput;
import io.honnix.rkt.launcher.output.image.GcOutputBuilder;
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
    return Image.image(asList("gc"));
  }

  @Override
  default GcOutput parse(final String output) {
    final GcOutputBuilder gcOutputBuilder = GcOutput.builder();

    final Pattern removedTreestorePattern = Pattern.compile("removed treestore \"(.+)\"");
    final Pattern removedImagePattern =
        Pattern.compile("successfully removed aci for image: \"(.+)\"");

    final Matcher removedTreestoreMatcher = removedTreestorePattern.matcher(output);
    while (removedTreestoreMatcher.find()) {
      gcOutputBuilder.removedTreestores().add(removedTreestoreMatcher.group(1));
    }

    final Matcher removedImageMatcher = removedImagePattern.matcher(output);
    while (removedImageMatcher.find()) {
      gcOutputBuilder.removedImages().add(removedImageMatcher.group(1));
    }

    return gcOutputBuilder.build();
  }

  static GcBuilder builder() {
    return new GcBuilder();
  }
}
