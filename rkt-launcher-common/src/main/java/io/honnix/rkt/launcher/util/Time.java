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
package io.honnix.rkt.launcher.util;

import java.time.Duration;

public final class Time {

  private Time() {
  }

  public static String durationToString(final Duration duration) {
    final long seconds = duration.getSeconds();
    if (seconds < 0) {
      throw new IllegalArgumentException("negative duration");
    }

    return String.format("%dh%dm%ds",
                         seconds / 3600,
                         (seconds % 3600) / 60,
                         seconds % 60);
  }
}
