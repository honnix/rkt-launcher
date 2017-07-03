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
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.output.FetchOutput;
import io.honnix.rkt.launcher.output.FetchOutputBuilder;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoMatter
public interface Fetch extends CommandWithArgs<FetchOptions, FetchOutput> {

  @Override
  Optional<FetchOptions> options();

  @Override
  default List<String> asList() {
    return asList("fetch");
  }

  @Override
  default FetchOutput parse(final String output) {
    final FetchOutputBuilder fetchOutputBuilder = FetchOutput.builder();

    final Pattern signaturePattern = Pattern.compile("image: signature verified:\\s+(.+)");
    final Pattern hashPattern = Pattern.compile("sha512-.+");

    final Matcher signatureMatcher = signaturePattern.matcher(output);
    if (signatureMatcher.find()) {
      fetchOutputBuilder.signature(signatureMatcher.group(1));
    }

    final Matcher hashMatcher = hashPattern.matcher(output);
    if (hashMatcher.find()) {
      fetchOutputBuilder.hash(hashMatcher.group());
    } else {
      throw new RktUnexpectedOutputException("no hash found");
    }

    return fetchOutputBuilder.build();
  }

  static FetchBuilder builder() {
    return new FetchBuilder();
  }
}
