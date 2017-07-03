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
package io.honnix.rkt.launcher.remote;

import com.spotify.apollo.Client;
import io.honnix.rkt.launcher.remote.command.RktCommandRemote;
import io.honnix.rkt.launcher.remote.command.RktImageCommandRemote;
import java.net.URI;

public interface RktLauncherRemote extends RktCommandRemote {

  class RktLauncherRemoteBuilder {

    public enum Scheme {
      HTTP,
      HTTPS
    }

    private Scheme scheme = Scheme.HTTP;

    private String host = "localhost";

    private int port = 80;

    private Client client;

    public RktLauncherRemoteBuilder scheme(final Scheme scheme) {
      this.scheme = scheme;
      return this;
    }

    public RktLauncherRemoteBuilder host(final String host) {
      this.host = host;
      return this;
    }

    public RktLauncherRemoteBuilder port(final int port) {
      this.port = port;
      return this;
    }

    public RktLauncherRemoteBuilder client(final Client client) {
      this.client = client;
      return this;
    }

    public RktLauncherRemoteImpl build() {
      return new RktLauncherRemoteImpl(
          URI.create(scheme.name().toLowerCase() + "://" + host + ":" + port),
          client);
    }
  }

  static RktLauncherRemoteBuilder builder() {
    return new RktLauncherRemoteBuilder();
  }

  RktImageCommandRemote image();
}
