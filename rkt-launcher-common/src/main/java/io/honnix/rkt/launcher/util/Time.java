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

import com.fasterxml.jackson.databind.util.StdConverter;
import java.time.Duration;
import java.time.Instant;

public final class Time {

  public static class Nano2Instant extends StdConverter<Long, Instant> {

    private static final long NANOS_PER_SECOND = 1000_000_000L;

    @Override
    public Instant convert(final Long value) {
      final long seconds = value / NANOS_PER_SECOND;
      final long nanoseconds = value % NANOS_PER_SECOND;
      return Instant.ofEpochSecond(seconds, nanoseconds);
    }
  }

  public static class Instant2Nano extends StdConverter<Instant, Long> {

    private static final long NANOS_PER_SECOND = 1000_000_000L;

    @Override
    public Long convert(final Instant value) {
      return value.getEpochSecond() * NANOS_PER_SECOND + value.getNano();
    }
  }

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
