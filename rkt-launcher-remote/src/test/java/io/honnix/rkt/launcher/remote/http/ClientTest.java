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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spotify.apollo.Status;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import okio.ByteString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientTest {

  static class RequestMatcher implements ArgumentMatcher<Request> {

    private Request left;

    RequestMatcher(Request left) {
      this.left = left;
    }

    @Override
    public boolean matches(Request right) {
      if (!right.method().equals(left.method())) {
        return false;
      }
      if (!right.headers().toMultimap().equals(left.headers().toMultimap())) {
        return false;
      }
      if (right.body() == null && left.body() == null) {
        return true;
      } else if (right.body() != null && left.body() != null) {
        try {
          return right.body().contentLength() == left.body().contentLength();
        } catch (IOException e) {
          return false;
        }
      } else {
        return false;
      }
    }
  }

  private static final String URI = "http://localhost:12345";

  private static MediaType CONTENT_TYPE =
      MediaType.parse(com.google.common.net.MediaType.JSON_UTF_8.toString());

  private Client client;

  @Mock
  private OkHttpClient okHttpClient;

  @Mock
  private Call call;

  @Before
  public void setUp() {
    client = new Client(okHttpClient);
  }

  @Test
  public void shouldSendGetRequest() throws ExecutionException, InterruptedException {
    final Request request = new com.squareup.okhttp.Request.Builder()
        .url(URI)
        .method("GET", null)
        .build();
    when(okHttpClient.newCall(argThat(new RequestMatcher(request)))).thenReturn(call);
    doAnswer(invocation -> {
      final Callback callback = invocation.getArgument(0);
      callback.onResponse(new Response.Builder()
                              .request(request)
                              .protocol(Protocol.HTTP_1_1)
                              .code(Status.OK.code())
                              .message("OK")
                              .body(ResponseBody.create(CONTENT_TYPE, "{}"))
                              .header("foo", "bar")
                              .build());
      return Void.TYPE;
    }).when(call).enqueue(isA(Callback.class));
    final com.spotify.apollo.Response<ByteString> response =
        client.send(com.spotify.apollo.Request.forUri(URI, "GET")).toCompletableFuture().get();
    verify(okHttpClient, never()).setReadTimeout(anyLong(), any());

    assertEquals(Optional.of(ByteString.of("{}".getBytes())), response.payload());
    assertEquals(Optional.of("bar"), response.header("foo"));
  }

  @Test
  public void shouldSendPostRequest() throws ExecutionException, InterruptedException {
    final Request request = new com.squareup.okhttp.Request.Builder()
        .url(URI)
        .method("POST", RequestBody.create(CONTENT_TYPE, "{}"))
        .build();
    when(okHttpClient.newCall(argThat(new RequestMatcher(request)))).thenReturn(call);
    doAnswer(invocation -> {
      final Callback callback = invocation.getArgument(0);
      callback.onResponse(new Response.Builder()
                              .request(request)
                              .protocol(Protocol.HTTP_1_1)
                              .code(Status.OK.code())
                              .message("OK")
                              .body(ResponseBody.create(CONTENT_TYPE, "{}"))
                              .header("foo", "bar")
                              .build());
      return Void.TYPE;
    }).when(call).enqueue(isA(Callback.class));
    final com.spotify.apollo.Response<ByteString> response =
        client.send(com.spotify.apollo.Request
                        .forUri(URI, "POST")
                        .withPayload(ByteString.of("{}".getBytes())))
            .toCompletableFuture().get();
    verify(okHttpClient, never()).setReadTimeout(anyLong(), any());

    assertEquals(Optional.of(ByteString.of("{}".getBytes())), response.payload());
    assertEquals(Optional.of("bar"), response.header("foo"));
  }

  @Test
  public void shouldSendGetRequestAndReceiveException() {
    final Request request = new com.squareup.okhttp.Request.Builder()
        .url(URI)
        .method("GET", null)
        .build();
    when(okHttpClient.newCall(argThat(new RequestMatcher(request)))).thenReturn(call);
    doAnswer(invocation -> {
      final Callback callback = invocation.getArgument(0);
      callback.onFailure(request, new IOException());
      return Void.TYPE;
    }).when(call).enqueue(isA(Callback.class));
    final CompletionStage<com.spotify.apollo.Response<ByteString>> response =
        client.send(com.spotify.apollo.Request.forUri(URI, "GET").withTtl(Duration.ofMillis(100)));
    verify(okHttpClient).setReadTimeout(100, TimeUnit.MILLISECONDS);
    assertTrue(response.toCompletableFuture().isCompletedExceptionally());
  }
}
