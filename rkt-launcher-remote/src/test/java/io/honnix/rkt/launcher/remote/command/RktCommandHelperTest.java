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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;

import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.apollo.Client;
import com.spotify.apollo.Request;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import io.honnix.rkt.launcher.model.config.ConfigBuilder;
import io.honnix.rkt.launcher.model.config.PathsBuilder;
import io.honnix.rkt.launcher.model.config.Stage1Builder;
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.output.ConfigOutput;
import io.honnix.rkt.launcher.output.GcOutput;
import io.honnix.rkt.launcher.remote.exception.RktLauncherRemoteException;
import io.honnix.rkt.launcher.remote.exception.RktLauncherRemoteHttpException;
import io.honnix.rkt.launcher.util.Json;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import okio.ByteString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(Json.class)
@RunWith(PowerMockRunner.class)
public class RktCommandHelperTest {

  @Mock
  private Client client;

  @Test
  public void shouldBuildCorrectURI() {
    assertEquals("http://localhost:8080/api/v0/rkt/cat-manifest",
                 RktCommandHelper
                     .uri(URI.create("http://localhost:8080"), "cat-manifest"));
  }

  @Test
  public void shouldBuildCorrectImageURI() {
    assertEquals("http://localhost:8080/api/v0/rkt/image/cat-manifest",
                 RktCommandHelper
                     .uri(URI.create("http://localhost:8080"), "image",
                          "cat-manifest"));
  }

  @Test
  public void shouldBuildCorrectURIWithQueryParameters() {
    assertEquals("http://localhost:8080/api/v0/rkt/fetch?image=image1&image=image2",
                 RktCommandHelper
                     .uri(URI.create("http://localhost:8080"),
                          ImmutableMap.of("image", ImmutableList.of("image1", "image2")),
                          "fetch"));
  }
  
  @Test
  public void shouldMerge() {
    Assert.assertEquals(ImmutableList.of("id1", "id2", "id3"), RktCommandHelper
        .merge("id1", "id2", "id3"));
  }

  @Test
  public void shouldMergeNullArray() {
    Assert.assertEquals(ImmutableList.of("id1"), RktCommandHelper.merge("id1"));
  }

  @Test
  public void shouldSendRequestWithPayload() throws Exception {
    final GcOutput gcOutput = GcOutput.builder()
        .unremoved("id1")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(gcOutput)));
    when(client.send(any(Request.class)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final GcOutput response =
        RktCommandHelper.sendRequest(client,
                                     "http://localhost:8080/rkt/gc",
                                     GcOptions
                                         .builder()
                                         .markOnly(true)
                                         .build(),
                                     GcOutput.class)
            .toCompletableFuture().get();
    assertEquals(gcOutput, response);
  }

  @Test
  public void shouldSendRequestWithoutPayload() throws Exception {
    final ConfigOutput configOutput = ConfigOutput.builder()
        .config(new ConfigBuilder()
                    .stage0(new PathsBuilder()
                                .rktVersion("v1")
                                .rktKind("paths")
                                .data("/var/lib/rkt")
                                .stage1Images("/usr/lib/rkt")
                                .build(),
                            new Stage1Builder()
                                .rktVersion("v1")
                                .rktKind("stage1")
                                .name("coreos.com/rkt/stage1-coreos")
                                .version("0.15.0+git")
                                .location("/usr/libexec/rkt/stage1-coreos.aci")
                                .build())
                    .build())
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(configOutput)));
    when(client.send(any(Request.class)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    final ConfigOutput response =
        RktCommandHelper.sendRequest(client,
                                     "http://localhost:8080/rkt/config",
                                     ConfigOutput.class)
            .toCompletableFuture().get();
    assertEquals(configOutput, response);
  }

  @Test
  public void shouldThrowIfEmptyResponsePayload() throws Exception {
    when(client.send(any(Request.class)))
        .thenReturn(CompletableFuture.completedFuture(Response.ok()));
    try {
      RktCommandHelper.sendRequest(client,
                                   "http://localhost:8080/rkt/gc",
                                   GcOptions
                                       .builder()
                                       .markOnly(true)
                                       .build(),
                                   GcOutput.class)
          .toCompletableFuture().get();
      fail();
    } catch (ExecutionException e) {
      assertSame(RktLauncherRemoteException.class, e.getCause().getClass());
    }
  }

  @Test
  public void shouldThrowWhenSerializationFailed() throws Exception {
    mockStatic(Json.class);
    doThrow(new IgnoredPropertyException(null, "", null, null, "", null))
        .when(Json.class);
    Json.serialize(any());
    try {
      RktCommandHelper.sendRequest(client,
                                   "http://localhost:8080/rkt/gd",
                                   GcOptions
                                       .builder()
                                       .markOnly(true)
                                       .build(),
                                   GcOutput.class)
          .toCompletableFuture().get();
      fail();
    } catch (ExecutionException e) {
      assertSame(RktLauncherRemoteException.class, e.getCause().getClass());
    }
  }

  @Test
  public void shouldThrowWhenDeserializationFailed() throws Exception {
    final GcOutput gcOutput = GcOutput.builder()
        .unremoved("id1")
        .build();
    final Response<ByteString> responsePayload =
        Response.forPayload(ByteString.of(Json.serialize(gcOutput)));
    when(client.send(any(Request.class)))
        .thenReturn(CompletableFuture.completedFuture(responsePayload));
    spy(Json.class);
    doThrow(new IgnoredPropertyException(null, "", null, null, "", null))
        .when(Json.class);
    Json.deserialize(any(byte[].class), any());
    try {
      RktCommandHelper.sendRequest(client,
                                   "http://localhost:8080/rkt/gc",
                                   GcOptions
                                       .builder()
                                       .markOnly(true)
                                       .build(),
                                   GcOutput.class)
          .toCompletableFuture().get();
      fail();
    } catch (ExecutionException e) {
      assertSame(RktLauncherRemoteException.class, e.getCause().getClass());
    }
  }

  @Test
  public void shouldThrowIfNotOK() throws Exception {
    when(client.send(any(Request.class)))
        .thenReturn(CompletableFuture.completedFuture(Response.forStatus(Status.BAD_REQUEST)));
    try {
      RktCommandHelper.sendRequest(client,
                                   "http://localhost:8080/rkt/gc",
                                   GcOptions
                                       .builder()
                                       .markOnly(true)
                                       .build(),
                                   GcOutput.class)
          .toCompletableFuture().get();
      fail();
    } catch (ExecutionException e) {
      assertSame(RktLauncherRemoteHttpException.class, e.getCause().getClass());
      assertEquals(400, ((RktLauncherRemoteHttpException) e.getCause()).getCode());
    }
  }

  @Test
  public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
                                                InvocationTargetException, InstantiationException {
    Constructor<RktCommandHelper> constructor = RktCommandHelper.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
