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
package io.honnix.rkt.launcher.remote.command;

import static com.spotify.apollo.StatusType.Family.SUCCESSFUL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.futures.CompletableFutures;
import io.honnix.rkt.launcher.options.Options;
import io.honnix.rkt.launcher.output.Output;
import io.honnix.rkt.launcher.remote.exception.RktLauncherRemoteException;
import io.honnix.rkt.launcher.remote.exception.RktLauncherRemoteHttpException;
import io.honnix.rkt.launcher.util.Json;
import com.squareup.okhttp.HttpUrl.Builder;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import okio.ByteString;

class RktCommandHelper {

  private static final String RKT_LAUNCHER_SERVICE_VERSION = "v0";

  private static final String DEFAULT_HTTP_METHOD = "POST";

  private static final String RKT_LAUNCHER_SERVICE_ROOT = "rkt";

  private RktCommandHelper() {
  }

  static String uri(final URI apiHost,
                    final String... segments) {
    return builder(apiHost, segments).build().toString();
  }

  static String uri(final URI apiHost,
                    final Map<String, List<String>> parameters,
                    final String... segments) {
    final Builder builder = builder(apiHost, segments);
    for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
      for (String value : entry.getValue()) {
        builder.addQueryParameter(entry.getKey(), value);
      }
    }
    return builder.build().toString();
  }

  static List<String> merge(final String str, final String... strs) {
    return ImmutableList.<String>builder().add(str).add(strs).build();
  }

  private static Builder builder(final URI apiHost,
                                 final String... segments) {
    final Builder builder = new Builder()
        .scheme(apiHost.getScheme())
        .host(apiHost.getHost())
        .port(apiHost.getPort())
        .addPathSegment("api")
        .addPathSegment(RKT_LAUNCHER_SERVICE_VERSION)
        .addPathSegment(RKT_LAUNCHER_SERVICE_ROOT);
    for (String segment : segments) {
      builder.addPathSegment(segment);
    }
    return builder;
  }

  static <T extends Options, S extends Output> CompletionStage<S> sendRequest(
      final Client client,
      final String uri,
      final T options,
      final Class<S> cls) throws RktLauncherRemoteException {
    final ByteString payload;
    try {
      payload = ByteString.of(Json.serialize(options));
    } catch (JsonProcessingException e) {
      return CompletableFutures.exceptionallyCompletedFuture(
          new RktLauncherRemoteException("failed to serialize payload", e));
    }
    return sendRequest(client, Request.forUri(uri, DEFAULT_HTTP_METHOD).withPayload(payload), cls);
  }

  static <S extends Output> CompletionStage<S> sendRequest(
      final Client client,
      final String uri,
      final Class<S> cls) throws RktLauncherRemoteException {
    return sendRequest(client, Request.forUri(uri, DEFAULT_HTTP_METHOD), cls);
  }

  private static <S extends Output> CompletionStage<S> sendRequest(final Client client,
                                                                   final Request request,
                                                                   final Class<S> cls) {
    return client.send(request).thenApply(response -> {
      if (response.status().family() == SUCCESSFUL) {
        return response.payload().map(responsePayload -> {
          try {
            return Json.deserialize(responsePayload.toByteArray(), cls);
          } catch (IOException e) {
            throw new RktLauncherRemoteException("failed to deserialize response", e);
          }
        }).orElseThrow(() -> new RktLauncherRemoteException("unexpected empty payload"));
      } else {
        throw new RktLauncherRemoteHttpException(response.status().reasonPhrase(),
                                                 response.status().code());
      }
    });
  }
}
