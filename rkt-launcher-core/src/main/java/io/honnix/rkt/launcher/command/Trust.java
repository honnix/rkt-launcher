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

import com.google.common.collect.Lists;
import io.honnix.rkt.launcher.exception.RktUnexpectedOutputException;
import io.honnix.rkt.launcher.model.TrustedPubkey;
import io.honnix.rkt.launcher.options.TrustOptions;
import io.honnix.rkt.launcher.output.TrustOutput;
import io.honnix.rkt.launcher.output.TrustOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoMatter
public interface Trust extends CommandWithOptionalArgs<TrustOptions, TrustOutput> {

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
    final Pattern locationPattern = Pattern.compile("Added .* at \"(.+)\"");

    final List<String> prefixes = Lists.newArrayList();
    final List<String> keys = Lists.newArrayList();
    final List<String> locations = Lists.newArrayList();

    final Matcher prefixMatcher = prefixPattern.matcher(output);
    while (prefixMatcher.find()) {
      prefixes.add(prefixMatcher.group(1));
    }

    final Matcher keyMatcher = keyPattern.matcher(output);
    while (keyMatcher.find()) {
      keys.add(keyMatcher.group(1));
    }

    final Matcher locationMatcher = locationPattern.matcher(output);
    while (locationMatcher.find()) {
      locations.add(locationMatcher.group(1));
    }
    
    if (prefixes.size() == keys.size() && prefixes.size() == locations.size()) {
      for (int i = 0; i < prefixes.size(); ++i) {
        final TrustedPubkey trustedPubkey = TrustedPubkey.builder()
            .prefix(prefixes.get(i))
            .key(keys.get(i))
            .location(locations.get(i))
            .build();
        trustOutputBuilder.addTrustedPubkey(trustedPubkey);
      }
    } else {
      throw new RktUnexpectedOutputException("missing value(s) in output");
    }

    return trustOutputBuilder.build();
  }

  static TrustBuilder builder() {
    return new TrustBuilder();
  }
}
