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
package io.honnix.rkt.launcher.remote.http;

import com.google.common.collect.ImmutableMap;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import okio.ByteString;

public class Client implements com.spotify.apollo.Client {

  private OkHttpClient okHttpClient;

  public Client(final OkHttpClient okHttpClient) {
    this.okHttpClient = Objects.requireNonNull(okHttpClient);
  }

  @Override
  public CompletionStage<Response<ByteString>> send(final Request apolloRequest) {
    final MediaType contentType =
        MediaType.parse(com.google.common.net.MediaType.JSON_UTF_8.toString());
    final RequestBody requestBody =
        apolloRequest.payload().map(p -> RequestBody.create(contentType, p)).orElse(null);

    final com.squareup.okhttp.Request.Builder requestBuilder =
        new com.squareup.okhttp.Request.Builder();
    final com.squareup.okhttp.Request request = requestBuilder
        .url(apolloRequest.uri())
        .headers(Headers.of(ImmutableMap.copyOf(apolloRequest.headerEntries())))
        .method(apolloRequest.method(), requestBody)
        .build();

    apolloRequest.ttl()
        .ifPresent(ttl -> okHttpClient.setReadTimeout(ttl.toMillis(), TimeUnit.MILLISECONDS));

    final CompletableFuture<Response<ByteString>> future = new CompletableFuture<>();
    okHttpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(final com.squareup.okhttp.Request request, IOException e) {
        future.completeExceptionally(e);
      }

      @Override
      public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
        future.complete(
            Response
                .of(Status.createForCode(response.code())
                        .withReasonPhrase(response.message()),
                    ByteString.of(response.body().bytes()))
                .withHeaders(multimap2Map(response.headers().toMultimap())));
      }

      private Map<String, String> multimap2Map(final Map<String, List<String>> multimap) {
        // TODO: can we do better?
        return multimap.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
      }
    });

    return future;
  }
}
