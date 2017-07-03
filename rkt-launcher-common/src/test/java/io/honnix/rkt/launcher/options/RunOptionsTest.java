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
package io.honnix.rkt.launcher.options;

import static io.honnix.rkt.launcher.model.Capability.CAP_CHOWN;
import static io.honnix.rkt.launcher.model.Capability.CAP_DAC_OVERRIDE;
import static io.honnix.rkt.launcher.model.Capability.CAP_DAC_READ_SEARCH;
import static io.honnix.rkt.launcher.model.Capability.CAP_FOWNER;
import static io.honnix.rkt.launcher.model.PullPolicy.NEW;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.model.schema.type.Annotation;
import io.honnix.rkt.launcher.model.schema.type.Environment;
import io.honnix.rkt.launcher.model.schema.type.ExposedPort;
import io.honnix.rkt.launcher.model.schema.type.Label;
import io.honnix.rkt.launcher.model.schema.type.Mount;
import io.honnix.rkt.launcher.model.Network;
import io.honnix.rkt.launcher.model.schema.type.Volume;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;

public class RunOptionsTest {

  private PerImageOptions buildImageOptions() throws MalformedURLException {
    return PerImageOptions.builder()
        .image("image")
        .capsRemove(ImmutableList.of(
            CAP_CHOWN,
            CAP_DAC_OVERRIDE))
        .capsRetain(ImmutableList.of(
            CAP_DAC_READ_SEARCH,
            CAP_FOWNER))
        .cpu("500m")
        .cpuShares(2048)
        .environment(ImmutableList.of(
            Environment.builder()
                .name("foo")
                .value("foo")
                .build(),
            Environment.builder()
                .name("bar")
                .value("bar")
                .build()))
        .exec("exec")
        .group("group")
        .inheritEnv(true)
        .memory("50M")
        .name("foobar")
        .noOverlay(true)
        .oomScoreAdj(-999)
        .port(ImmutableList.of(
            ExposedPort.builder()
                .name("port1")
                .hostPort(2000)
                .build(),
            ExposedPort.builder()
                .name("port2")
                .hostPort(3000)
                .build()))
        .privateUsers(true)
        .pullPolicy(NEW)
        .readonlyRootfs(true)
        .seccomp(ImmutableList.of(
            PerImageOptions.Seccomp.builder()
                .mode(PerImageOptions.Seccomp.Mode.REMOVE)
                .errno("ENOTSUP")
                .syscalls("socket")
                .build(),
            PerImageOptions.Seccomp.builder()
                .mode(PerImageOptions.Seccomp.Mode.RETAIN)
                .errno("ENOSYS")
                .syscalls("mount", "umount")
                .build()))
        .signature("signature")
        .stage1FromDir("dir")
        .stage1Hash("hash")
        .stage1Name("foo")
        .stage1Path("path")
        .stage1Url(new URL("http://example.com"))
        .supplementaryGIDs(ImmutableList.of(200, 300))
        .user("user")
        .userAnnotation(ImmutableList.of(
            Annotation.builder()
                .name("foo")
                .value("bar")
                .build()))
        .userLabel(ImmutableList.of(
            Label.builder()
                .name("foo")
                .value("bar")
                .build()))
        .workingDir("dir")
        .build();
  }

  @Test
  public void shouldBuildCorrectList() throws MalformedURLException {
    final RunOptions runOptions = RunOptions.builder()
        .volume(ImmutableList.of(
            Volume.EmptyVolume.builder()
                .name("foo")
                .mode("0755")
                .gid(100)
                .uid(100)
                .build(),
            Volume.HostVolume.builder()
                .name("bar")
                .readOnly(true)
                .source("source")
                .recursive(true)
                .build()))
        .mount(ImmutableList.of(
            Mount.builder()
                .volume("foo")
                .path("/foo")
                .build(),
            Mount.builder()
                .volume("bar")
                .path("/bar")
                .build()))
        .podManifest("pod_manifest")
        .setEnv(ImmutableList.of(
            Environment.builder()
                .name("foo")
                .value("foo")
                .build(),
            Environment.builder()
                .name("bar")
                .value("bar")
                .build()))
        .setEnvFile("foo")
        .uuidFileSave("foo")
        .dns(ImmutableList.of("8.8.8.8", "8.8.8.4"))
        .dnsDomain(ImmutableList.of("foo.com", "bar.com"))
        .dnsOpt(ImmutableList.of("opt1", "opt2"))
        .dnsSearch(ImmutableList.of("foo.com", "bar.com"))
        .hostname("foobar")
        .hostsEntry(ImmutableList.of("127.0.0.1 localhost"))
        .mdsRegister(true)
        .net(ImmutableList.of(
            Network.NONE,
            Network.builder()
                .name("foo")
                .args(ImmutableList.of(
                    Network.Argument.builder()
                        .name("IP")
                        .value("192.168.1.1")
                        .build(),
                    Network.Argument.builder()
                        .name("MASK")
                        .value("255.255.255.0")
                        .build()))
                .build()))
        .addImagesOption(buildImageOptions())
        .addImagesOption(buildImageOptions())
        .build();
    final ImmutableList<String> expected = ImmutableList.of(
        "--volume=foo,kind=empty,mode=0755,gid=100,uid=100",
        "--volume=bar,kind=host,readOnly=true,source=source,recursive=true",
        "--mount=volume=foo,target=/foo",
        "--mount=volume=bar,target=/bar",
        "--pod-manifest=pod_manifest",
        "--set-env=foo=foo",
        "--set-env=bar=bar",
        "--set-env-file=foo",
        "--uuid-file-save=foo",
        "--dns=8.8.8.8",
        "--dns=8.8.8.4",
        "--dns-domain=foo.com",
        "--dns-domain=bar.com",
        "--dns-opt=opt1",
        "--dns-opt=opt2",
        "--dns-search=foo.com",
        "--dns-search=bar.com",
        "--hostname=foobar",
        "--hosts-entry=127.0.0.1 localhost",
        "--mds-register=true",
        "--net=none",
        "--net=foo:IP=192.168.1.1;MASK=255.255.255.0",
        "image",
        "--caps-remove=CAP_CHOWN,CAP_DAC_OVERRIDE",
        "--caps-retain=CAP_DAC_READ_SEARCH,CAP_FOWNER",
        "--cpu=500m",
        "--cpu-shares=2048",
        "--environment=foo=foo",
        "--environment=bar=bar",
        "--exec=exec",
        "--group=group",
        "--inherit-env=true",
        "--memory=50M",
        "--name=foobar",
        "--no-overlay=true",
        "--oom-score-adj=-999",
        "--port=port1:2000",
        "--port=port2:3000",
        "--private-users=true",
        "--pull-policy=new",
        "--readonly-rootfs=true",
        "--seccomp=mode=remove,errno=ENOTSUP,socket",
        "--seccomp=mode=retain,errno=ENOSYS,mount,umount",
        "--signature=signature",
        "--stage1-from-dir=dir",
        "--stage1-hash=hash",
        "--stage1-name=foo",
        "--stage1-path=path",
        "--stage1-url=http://example.com",
        "--supplementary-gids=200,300",
        "--user=user",
        "--user-annotation=foo=bar",
        "--user-label=foo=bar",
        "--working-dir=dir",
        "---",
        "image",
        "--caps-remove=CAP_CHOWN,CAP_DAC_OVERRIDE",
        "--caps-retain=CAP_DAC_READ_SEARCH,CAP_FOWNER",
        "--cpu=500m",
        "--cpu-shares=2048",
        "--environment=foo=foo",
        "--environment=bar=bar",
        "--exec=exec",
        "--group=group",
        "--inherit-env=true",
        "--memory=50M",
        "--name=foobar",
        "--no-overlay=true",
        "--oom-score-adj=-999",
        "--port=port1:2000",
        "--port=port2:3000",
        "--private-users=true",
        "--pull-policy=new",
        "--readonly-rootfs=true",
        "--seccomp=mode=remove,errno=ENOTSUP,socket",
        "--seccomp=mode=retain,errno=ENOSYS,mount,umount",
        "--signature=signature",
        "--stage1-from-dir=dir",
        "--stage1-hash=hash",
        "--stage1-name=foo",
        "--stage1-path=path",
        "--stage1-url=http://example.com",
        "--supplementary-gids=200,300",
        "--user=user",
        "--user-annotation=foo=bar",
        "--user-label=foo=bar",
        "--working-dir=dir",
        "---"
    );
    assertEquals(expected, runOptions.asList());
  }
}
