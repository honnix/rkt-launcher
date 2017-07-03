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

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.options.Options;
import io.honnix.rkt.launcher.output.VersionOutput;
import io.honnix.rkt.launcher.output.VersionOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoMatter
public interface Version extends CommandWithoutArgs<Options, VersionOutput> {

  @Override
  default Optional<Options> options() {
    return Optional.of(Options.NULL);
  }

  @Override
  default List<String> asList() {
    return asList("version");
  }

  @Override
  default VersionOutput parse(final String output) {
    final VersionOutputBuilder versionOutputBuilder = VersionOutput.builder();

    final Pattern rktVersionPattern = Pattern.compile("rkt Version: (.+)");
    final Pattern appcVersionPattern = Pattern.compile("appc Version: (.+)");
    final Pattern goVersionPattern = Pattern.compile("Go Version: (.+)");
    final Pattern goOSArchPattern = Pattern.compile("Go OS/Arch: (.+)");
    final Pattern featuresPattern = Pattern.compile("Features: (.+)");

    final Matcher rktVersionMatch = rktVersionPattern.matcher(output);
    if (rktVersionMatch.find()) {
      versionOutputBuilder.rktVersion(rktVersionMatch.group(1));
    } else {
      throw new RktUnexpectedOutputException("no rkt version found");
    }

    final Matcher appcVersionMatch = appcVersionPattern.matcher(output);
    if (appcVersionMatch.find()) {
      versionOutputBuilder.appcVersion(appcVersionMatch.group(1));
    } else {
      throw new RktUnexpectedOutputException("no appc version found");
    }

    final Matcher goVersionMatch = goVersionPattern.matcher(output);
    if (goVersionMatch.find()) {
      versionOutputBuilder.goVersion(goVersionMatch.group(1));
    } else {
      throw new RktUnexpectedOutputException("no Go version found");
    }

    final Matcher goOSArchMatcher = goOSArchPattern.matcher(output);
    if (goOSArchMatcher.find()) {
      versionOutputBuilder.goOSArch(goOSArchMatcher.group(1));
    } else {
      throw new RktUnexpectedOutputException("no Go OS/Arch found");
    }

    final Matcher featuresMatcher = featuresPattern.matcher(output);
    if (featuresMatcher.find()) {
      versionOutputBuilder.features(ImmutableList.copyOf(featuresMatcher.group(1).split(" ")));
    } else {
      throw new RktUnexpectedOutputException("no features found");
    }

    return versionOutputBuilder.build();
  }

  Version COMMAND = builder().build();

  static VersionBuilder builder() {
    return new VersionBuilder();
  }
}
