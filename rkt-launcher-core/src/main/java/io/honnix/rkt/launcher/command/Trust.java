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
import io.honnix.rkt.launcher.options.TrustOptions;
import io.honnix.rkt.launcher.output.TrustOutput;
import io.honnix.rkt.launcher.output.TrustOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoMatter
public interface Trust extends CommandWithArgs<TrustOptions, TrustOutput> {

  @Override
  Optional<TrustOptions> options();

  @Override
  default List<String> asList() {
    return asList("trust");
  }

  @Override
  default TrustOutput parse(final String output) {
    final TrustOutputBuilder trustOutputBuilder = TrustOutput.builder();

    final Pattern prefixPattern = Pattern.compile("pubkey: prefix: \"(.*)\"");
    final Pattern keyPattern = Pattern.compile("key: \"(.+)\"");
    final Pattern localtionPattern = Pattern.compile("Added .* at \"(.+)\"");

    final Matcher prefixMatcher = prefixPattern.matcher(output);
    if (prefixMatcher.find()) {
      trustOutputBuilder.prefix(prefixMatcher.group(1));
    } else {
      throw new RktUnexpectedOutputException("no prefix found");
    }

    final Matcher keyMatcher = keyPattern.matcher(output);
    if (keyMatcher.find()) {
      trustOutputBuilder.key(keyMatcher.group(1));
    } else {
      throw new RktUnexpectedOutputException("no key found");
    }

    final Matcher locationMatcher = localtionPattern.matcher(output);
    if (locationMatcher.find()) {
      trustOutputBuilder.location(locationMatcher.group(1));
    } else {
      throw new RktUnexpectedOutputException("no location found");
    }

    return trustOutputBuilder.build();
  }

  static TrustBuilder builder() {
    return new TrustBuilder();
  }
}
