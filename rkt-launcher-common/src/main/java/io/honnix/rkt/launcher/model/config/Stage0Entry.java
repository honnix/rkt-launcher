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
package io.honnix.rkt.launcher.model.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "rktKind",
    visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Stage0Entry.Auth.class, name = "auth"),
    @JsonSubTypes.Type(value = Stage0Entry.DockerAuth.class, name = "dockerAuth"),
    @JsonSubTypes.Type(value = Stage0Entry.Paths.class, name = "paths"),
    @JsonSubTypes.Type(value = Stage0Entry.Stage1.class, name = "stage1")})
public interface Stage0Entry {

  String rktVersion();

  String rktKind();

  @AutoMatter
  interface Auth extends Stage0Entry {

    List<String> domains();

    String type();

    Map<String, String> credentials();
  }

  @AutoMatter
  interface DockerAuth extends Stage0Entry {

    @AutoMatter
    interface BasicCredentials {

      String user();

      String password();
    }

    List<String> registries();

    BasicCredentials credentials();
  }

  @AutoMatter
  interface Paths extends Stage0Entry {

    String data();

    String stage1Images();
  }

  @AutoMatter
  interface Stage1 extends Stage0Entry {

    String name();

    String version();

    String location();
  }
}
