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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.norberg.automatter.AutoMatter;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "kind")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Volume.HostVolume.class, name = "host"),
    @JsonSubTypes.Type(value = Volume.HostVolume.class, name = "HostVolumeBuilder$Value"),
    @JsonSubTypes.Type(value = Volume.EmptyVolume.class, name = "empty"),
    @JsonSubTypes.Type(value = Volume.EmptyVolume.class, name = "EmptyVolumeBuilder$Value")})
public interface Volume {

  String name();

  String kind();

  String toOption();

  @AutoMatter
  interface EmptyVolume extends Volume {

    @Override
    default String kind() {
      return "empty";
    }

    Optional<String> mode();

    Optional<Integer> uid();

    Optional<Integer> gid();

    default String toOption() {
      final StringBuilder sb = new StringBuilder(name());
      sb.append(",kind=").append(kind());
      mode().ifPresent(v -> sb.append(",mode=").append(v));
      gid().ifPresent(v -> sb.append(",gid=").append(v));
      uid().ifPresent(v -> sb.append(",uid=").append(v));
      return sb.toString();
    }

    static EmptyVolumeBuilder builder() {
      return new EmptyVolumeBuilder();
    }
  }

  @AutoMatter
  interface HostVolume extends Volume {

    @Override
    default String kind() {
      return "host";
    }

    Optional<Boolean> readOnly();

    Optional<String> source();

    Optional<Boolean> recursive();

    default String toOption() {
      final StringBuilder sb = new StringBuilder(name());
      sb.append(",kind=").append(kind());
      readOnly().ifPresent(v -> sb.append(",readOnly=").append(v));
      source().ifPresent(v -> sb.append(",source=").append(v));
      recursive().ifPresent(v -> sb.append(",recursive=").append(v));
      return sb.toString();
    }

    static HostVolumeBuilder builder() {
      return new HostVolumeBuilder();
    }
  }
}
