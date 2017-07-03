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

package io.honnix.rkt.launcher.service.api;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

class ApiVersionTestUtils {

  static Api.Version[] ALL_VERSIONS = Api.Version.values();

  static Matcher<Api.Version> isAtLeast(final Api.Version lowerBound) {
    return new TypeSafeMatcher<Api.Version>() {
      @Override
      protected boolean matchesSafely(final Api.Version item) {
        return item.ordinal() >= lowerBound.ordinal();
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("Version is at least");
        description.appendValue(lowerBound);
      }
    };
  }

  static Matcher<Api.Version> isAtMost(final Api.Version upperBound) {
    return new TypeSafeMatcher<Api.Version>() {
      @Override
      protected boolean matchesSafely(final Api.Version item) {
        return item.ordinal() <= upperBound.ordinal();
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("Version is at most");
        description.appendValue(upperBound);
      }
    };
  }

  static Matcher<Api.Version> is(final Api.Version version) {
    return new TypeSafeMatcher<Api.Version>() {
      @Override
      protected boolean matchesSafely(final Api.Version item) {
        return item.ordinal() == version.ordinal();
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText("Version can only be");
        description.appendValue(version);
      }
    };
  }
}
