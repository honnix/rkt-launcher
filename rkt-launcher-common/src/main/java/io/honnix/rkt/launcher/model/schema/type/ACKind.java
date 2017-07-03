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
package io.honnix.rkt.launcher.model.schema.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum ACKind {
  IMAGE_MANIFEST("ImageManifest"),
  POD_MANIFEST("PodManifest");

  private final String kind;

  ACKind(final String kind) {
    this.kind = kind;
  }

  @JsonValue
  @Override
  public String toString() {
    return kind;
  }

  @JsonCreator
  public static ACKind fromString(final String kind) {
    return Arrays.stream(ACKind.values()).filter(x -> x.kind.equals(kind)).findFirst().orElse(null);
  }
}
