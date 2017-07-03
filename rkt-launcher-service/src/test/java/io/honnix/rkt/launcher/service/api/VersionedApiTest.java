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

package io.honnix.rkt.launcher.service.api;

import static io.honnix.rkt.launcher.service.api.ApiVersionTestUtils.ALL_VERSIONS;
import static io.honnix.rkt.launcher.service.api.ApiVersionTestUtils.is;
import static io.honnix.rkt.launcher.service.api.ApiVersionTestUtils.isAtLeast;
import static io.honnix.rkt.launcher.service.api.ApiVersionTestUtils.isAtMost;
import static org.junit.Assume.assumeThat;

import com.spotify.apollo.Environment;
import com.spotify.apollo.Response;
import com.spotify.apollo.test.ServiceHelper;
import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import okio.ByteString;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class VersionedApiTest {

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> versions() {
    return Stream.of(ALL_VERSIONS)
        .map(v -> new Object[]{v})
        .collect(Collectors.toList());
  }

  @Rule
  public ServiceHelper serviceHelper;

  private final String basePath;

  private final Api.Version version;

  VersionedApiTest(final String basePath, final Api.Version version, final String serviceName) {
    this.basePath = basePath;
    this.version = version;
    this.serviceHelper = ServiceHelper.create(this::init, serviceName);
  }

  protected abstract void init(final Environment environment);

  void sinceVersion(final Api.Version version) {
    assumeThat(this.version, isAtLeast(version));
  }

  protected void tillVersion(final Api.Version version) {
    assumeThat(this.version, isAtMost(version));
  }

  protected void isVersion(final Api.Version version) {
    assumeThat(this.version, is(version));
  }

  String path(final String path) {
    return version.prefix() + basePath + path;
  }

  Response<ByteString> awaitResponse(
      final CompletionStage<Response<ByteString>> completionStage)
      throws InterruptedException, ExecutionException, TimeoutException {
    return completionStage.toCompletableFuture().get(5, TimeUnit.SECONDS);
  }
}
