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

import io.norberg.automatter.AutoMatter;
import java.util.List;
import java.util.Optional;

@AutoMatter
public interface App {

  List<String> exec();
  
  Optional<List<EventHandler>> eventHandlers();
  
  String user();
  
  String group();
  
  Optional<List<Integer>> supplementaryGIDs();
  
  Optional<String> workingDirectory();
  
  Optional<List<Environment>> environment();
  
  Optional<List<MountPoint>> mountPoints();
  
  Optional<List<Port>> ports();
  
  Optional<List<Isolator>> isolators();
}
