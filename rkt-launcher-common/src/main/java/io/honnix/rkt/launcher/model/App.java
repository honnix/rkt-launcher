/*-
 * -\-\-
 * Spotify rkt-launcher
 * --
 * Copyright (C) 2017 Spotify AB
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
package io.honnix.rkt.launcher.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.honnix.rkt.launcher.util.Time;
import io.norberg.automatter.AutoMatter;
import java.time.Instant;

@AutoMatter
public interface App {

  String name();

  String state();

  @JsonDeserialize(converter = Time.Nano2Instant.class)
  @JsonSerialize(converter = Time.Instant2Nano.class)
  Instant createdAt();

  @JsonDeserialize(converter = Time.Nano2Instant.class)
  @JsonSerialize(converter = Time.Instant2Nano.class)
  Instant startedAt();
  
  String imageId();
}