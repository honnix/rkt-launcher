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

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.LOWER_CAMEL_CASE;
import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.norberg.automatter.jackson.AutoMatterModule;
import java.io.IOException;

public final class Json {

  private Json() {
  }

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
      .setPropertyNamingStrategy(LOWER_CAMEL_CASE)
      .registerModule(new JavaTimeModule())
      .registerModule(new Jdk8Module())
      .registerModule(new AutoMatterModule());

  private static final ObjectMapper OBJECT_MAPPER_SNAKE_CASE = new ObjectMapper()
      .setPropertyNamingStrategy(SNAKE_CASE)
      .registerModule(new JavaTimeModule())
      .registerModule(new Jdk8Module())
      .registerModule(new AutoMatterModule());

  public static <T> T deserialize(final String json, final Class<T> clazz) throws IOException {
    return OBJECT_MAPPER.readValue(json, clazz);
  }

  public static <T> T deserialize(final byte[] bytes, final Class<T> clazz) throws IOException {
    return OBJECT_MAPPER.readValue(bytes, clazz);
  }

  public static <T> T deserialize(final String json, final TypeReference valueTypeRef)
      throws IOException {
    return OBJECT_MAPPER.readValue(json, valueTypeRef);
  }

  public static <T> T deserializeSnakeCase(final String json, final TypeReference valueTypeRef)
      throws IOException {
    return OBJECT_MAPPER_SNAKE_CASE.readValue(json, valueTypeRef);
  }

  public static byte[] serialize(final Object value) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsBytes(value);
  }
}
