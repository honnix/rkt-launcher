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
package io.honnix.rkt.launcher.options.image;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.options.Options;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface ListOptions extends Options {

  enum Order {
    ASC,
    DESC
  }

  enum Field {
    ID("id"),
    NAME("name"),
    SIZE("size"),
    IMPORT_TIME("importtime"),
    LAST_USED_TINE("lastused");

    private final String fieldName;

    Field(final String fieldName) {
      this.fieldName = fieldName;
    }

    @JsonValue
    @Override
    public String toString() {
      return fieldName;
    }
  }

  Optional<Order> order();

  Optional<List<Field>> sort();

  @Override
  default List<String> asList() {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    order().ifPresent(v -> builder.add(
        join("--order", v.name().toLowerCase())));
    sort().ifPresent(v -> builder.add(
        join("--sort", Joiner.on(",").join(v))));
    builder.add(
        join("--format", "json"));
    return builder.build();
  }

  static ListOptionsBuilder builder() {
    return new ListOptionsBuilder();
  }
}
