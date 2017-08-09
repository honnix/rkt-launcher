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
package io.honnix.rkt.launcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import io.honnix.rkt.launcher.command.Config;
import io.honnix.rkt.launcher.command.Fetch;
import io.honnix.rkt.launcher.command.Prepare;
import io.honnix.rkt.launcher.command.Rm;
import io.honnix.rkt.launcher.command.Run;
import io.honnix.rkt.launcher.command.RunPrepared;
import io.honnix.rkt.launcher.command.Status;
import io.honnix.rkt.launcher.command.Stop;
import io.honnix.rkt.launcher.command.Trust;
import io.honnix.rkt.launcher.command.Version;
import io.honnix.rkt.launcher.command.image.CatManifest;
import io.honnix.rkt.launcher.command.image.Export;
import io.honnix.rkt.launcher.command.image.Extract;
import io.honnix.rkt.launcher.command.image.Gc;
import io.honnix.rkt.launcher.command.image.List;
import io.honnix.rkt.launcher.model.Network;
import io.honnix.rkt.launcher.model.PullPolicy;
import io.honnix.rkt.launcher.model.schema.type.ACKind;
import io.honnix.rkt.launcher.options.ExportOptions;
import io.honnix.rkt.launcher.options.FetchOptions;
import io.honnix.rkt.launcher.options.GcOptions;
import io.honnix.rkt.launcher.options.GlobalOptions;
import io.honnix.rkt.launcher.options.ListOptions;
import io.honnix.rkt.launcher.options.PerImageOptions;
import io.honnix.rkt.launcher.options.PrepareOptions;
import io.honnix.rkt.launcher.options.RunOptions;
import io.honnix.rkt.launcher.options.RunPreparedOptions;
import io.honnix.rkt.launcher.options.StatusOptions;
import io.honnix.rkt.launcher.options.StopOptions;
import io.honnix.rkt.launcher.options.TrustOptions;
import io.honnix.rkt.launcher.options.image.ExtractOptions;
import io.honnix.rkt.launcher.options.image.RmOptions;
import io.honnix.rkt.launcher.output.ConfigOutput;
import io.honnix.rkt.launcher.output.FetchOutput;
import io.honnix.rkt.launcher.output.ListOutput;
import io.honnix.rkt.launcher.output.Output;
import io.honnix.rkt.launcher.output.PrepareOutput;
import io.honnix.rkt.launcher.output.RunOutput;
import io.honnix.rkt.launcher.output.StatusOutput;
import io.honnix.rkt.launcher.output.StopOutput;
import io.honnix.rkt.launcher.output.TrustOutput;
import io.honnix.rkt.launcher.output.VersionOutput;
import io.honnix.rkt.launcher.output.image.GcOutput;
import io.honnix.rkt.launcher.output.image.RmOutput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessOutput;
import org.zeroturnaround.exec.ProcessResult;

@RunWith(MockitoJUnitRunner.class)
public class SystemTest {

  private RktLauncher rktLauncher;

  @Mock
  private ProcessExecutor processExecutor;

  @Before
  public void setUp() {
    final GlobalOptions globalOptions = GlobalOptions.builder()
        .insecureOptions(ImmutableList.of(GlobalOptions.InsecureOption.IMAGE))
        .build();
    final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
        .rkt("rkt")
        .globalOptions(globalOptions)
        .build();
    rktLauncher = new RktLauncher(rktLauncherConfig, () -> processExecutor);
  }

  @Test
  public void shouldRunImageCatMinifest() throws Exception {
    //language=JSON
    final String json = "{\n"
                        + "  \"acKind\": \"ImageManifest\",\n"
                        + "  \"acVersion\": \"0.8.10\",\n"
                        + "  \"name\": \"coreos.com/rkt/stage1-coreos\",\n"
                        + "  \"labels\": [\n"
                        + "    {\n"
                        + "      \"name\": \"version\",\n"
                        + "      \"value\": \"1.25.0\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"arch\",\n"
                        + "      \"value\": \"amd64\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"os\",\n"
                        + "      \"value\": \"linux\"\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"annotations\": [\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/run\",\n"
                        + "      \"value\": \"/init\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/enter\",\n"
                        + "      \"value\": \"/enter\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/gc\",\n"
                        + "      \"value\": \"/gc\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/stop\",\n"
                        + "      \"value\": \"/stop\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/app/add\",\n"
                        + "      \"value\": \"/app_add\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/app/rm\",\n"
                        + "      \"value\": \"/app_rm\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/app/start\",\n"
                        + "      \"value\": \"/app_start\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/app/stop\",\n"
                        + "      \"value\": \"/app_stop\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/attach\",\n"
                        + "      \"value\": \"/attach\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"name\": \"coreos.com/rkt/stage1/interface-version\",\n"
                        + "      \"value\": \"5\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(json.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "image", "cat-manifest",
                         "sha512-887890e697d9")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.image.CatManifest catManifest =
        CatManifest.builder()
            .addArg("sha512-887890e697d9")
            .build();
    final io.honnix.rkt.launcher.output.image.CatManifestOutput catManifestOutput =
        rktLauncher.run(catManifest);
    assertEquals(ACKind.IMAGE_MANIFEST, catManifestOutput.imageManifest().acKind());
  }

  @Test
  public void shouldRunImageExport() throws Exception {
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(new byte[]{}));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "image", "export", "--overwrite=true",
                         "sha512-887890e697d9", "output.aci")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.image.Export export =
        Export.builder()
            .options(io.honnix.rkt.launcher.options.image.ExportOptions.builder()
                         .overwrite(true)
                         .build())
            .addArg("sha512-887890e697d9")
            .addArg("output.aci")
            .build();
    final Output exportOutput = rktLauncher.run(export);
    assertSame(Output.NULL, exportOutput);
  }

  @Test
  public void shouldRunImageExtract() throws Exception {
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(new byte[]{}));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "image", "extract", "--overwrite=true",
                         "sha512-887890e697d9", "/data")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Extract extract =
        Extract.builder()
            .options(ExtractOptions.builder()
                         .overwrite(true)
                         .build())
            .addArg("sha512-887890e697d9")
            .addArg("/data")
            .build();
    final Output extractOutput = rktLauncher.run(extract);
    assertSame(Output.NULL, extractOutput);
  }

  @Test
  public void shouldRunImageGc() throws Exception {
    final String
        output =
        "gc: removed treestore \"deps-sha512-219204dd54481154aec8f6eafc0f2064d973c8a2c0537eab827b7414f0a36248\"\n"
        + "gc: removed treestore \"deps-sha512-3f2a1ad0e9739d977278f0019b6d7d9024a10a2b1166f6c9fdc98f77a357856d\"\n"
        + "gc: successfully removed aci for image: \"sha512-e39d4089a224718c41e6bef4c1ac692a6c1832c8c69cf28123e1f205a9355444\"\n"
        + "gc: successfully removed aci for image: \"sha512-0648aa44a37a8200147d41d1a9eff0757d0ac113a22411f27e4e03cbd1e84d0d\"\n"
        + "gc: 2 image(s) successfully removed";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "image", "gc")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.image.Gc gc =
        Gc.builder()
            .options(io.honnix.rkt.launcher.options.image.GcOptions.builder().build())
            .build();
    final GcOutput gcOutput = rktLauncher.run(gc);
    assertEquals(ImmutableList
                     .of("deps-sha512-219204dd54481154aec8f6eafc0f2064d973c8a2c0537eab827b7414f0a36248",
                         "deps-sha512-3f2a1ad0e9739d977278f0019b6d7d9024a10a2b1166f6c9fdc98f77a357856d"),
                 gcOutput.removedTreestores());
    assertEquals(ImmutableList
                     .of("sha512-e39d4089a224718c41e6bef4c1ac692a6c1832c8c69cf28123e1f205a9355444",
                         "sha512-0648aa44a37a8200147d41d1a9eff0757d0ac113a22411f27e4e03cbd1e84d0d"),
                 gcOutput.removedImages());
  }

  @Test
  public void shouldRunImageList() throws Exception {
    //language=JSON
    final String json = "[\n"
                        + "  {\n"
                        + "    \"id\": \"sha512-887890e697d9a0229eff22436def3c436cb4b18f72ac274c8c05427b39539307\",\n"
                        + "    \"name\": \"coreos.com/rkt/stage1-coreos:1.25.0\",\n"
                        + "    \"import_time\": 1491859470078086852,\n"
                        + "    \"last_used_time\": 1497150284380714857,\n"
                        + "    \"size\": 234530175\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"id\": \"sha512-572c5b51abf596a30c2c9df4798b5da3baa4525ed2b413f33a84ced36a99e9ee\",\n"
                        + "    \"name\": \"registry-1.docker.io/library/nginx:latest\",\n"
                        + "    \"import_time\": 1491859491703293806,\n"
                        + "    \"last_used_time\": 1491859492487748062,\n"
                        + "    \"size\": 374494953\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"id\": \"sha512-938efe6e0cba2f0d56f2675244026d442c668fb18bcb18c2ee778c2ddf7c32cf\",\n"
                        + "    \"name\": \"coreos.com/etcd:v2.2.5\",\n"
                        + "    \"import_time\": 1491860749840089680,\n"
                        + "    \"last_used_time\": 1491860749963552618,\n"
                        + "    \"size\": 29588992\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"id\": \"sha512-9946ffed69f415f644d4b337d3d6f4b228e4527286897345198e11f20cb4b5f7\",\n"
                        + "    \"name\": \"registry.example.com/trusty:0.26\",\n"
                        + "    \"import_time\": 1494618447524974892,\n"
                        + "    \"last_used_time\": 1494618478192533276,\n"
                        + "    \"size\": 771228160\n"
                        + "  }\n"
                        + "]";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(json.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "image", "list", "--format=json")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.image.List list =
        List.builder()
            .options(io.honnix.rkt.launcher.options.image.ListOptions.builder().build())
            .build();
    final io.honnix.rkt.launcher.output.image.ListOutput listOutput = rktLauncher.run(list);
    assertEquals(4, listOutput.images().size());
  }

  @Test
  public void shouldRunImageRm() throws Exception {
    final String
        output =
        "successfully removed aci for image: \"sha512-e39d4089a224718c41e6bef4c1ac692a6c1832c8c69cf28123e1f205a9355444\"\n"
        + "successfully removed aci for image: \"sha512-0648aa44a37a8200147d41d1a9eff0757d0ac113a22411f27e4e03cbd1e84d0d\"\n"
        + "rm: 2 image(s) successfully removed";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "image", "rm",
                         "sha512-e39d4089a224", "sha512-0648aa44a37a")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.image.Rm
        rm =
        io.honnix.rkt.launcher.command.image.Rm.builder()
            .options(RmOptions.builder().build())
            .args(ImmutableList.of("sha512-e39d4089a224", "sha512-0648aa44a37a"))
            .build();
    final RmOutput rmOutput = rktLauncher.run(rm);
    assertEquals(2, rmOutput.removed().size());
  }

  @Test
  public void shouldRunCatMinifest() throws Exception {
    //language=JSON
    final String json = "{\n"
                        + "  \"acVersion\": \"1.25.0\",\n"
                        + "  \"acKind\": \"PodManifest\",\n"
                        + "  \"apps\": [],\n"
                        + "  \"volumes\": [],\n"
                        + "  \"isolators\": [],\n"
                        + "  \"annotations\": [],\n"
                        + "  \"ports\": []\n"
                        + "}\n";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(json.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "cat-manifest", "1d284607")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.CatManifest
        catManifest = io.honnix.rkt.launcher.command.CatManifest.builder()
        .args(ImmutableList.of("1d284607"))
        .build();
    final io.honnix.rkt.launcher.output.CatManifestOutput
        catManifestOutput = rktLauncher.run(catManifest);
    assertEquals(ACKind.POD_MANIFEST, catManifestOutput.podManifest().acKind());
  }

  @Test
  public void shouldRunConfig() throws Exception {
    //language=JSON
    final String json = "{\n"
                        + "  \"stage0\": [\n"
                        + "    {\n"
                        + "      \"rktVersion\": \"v1\",\n"
                        + "      \"rktKind\": \"paths\",\n"
                        + "      \"data\": \"/var/lib/rkt\",\n"
                        + "      \"stage1-images\": \"/usr/lib/rkt\"\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"rktVersion\": \"v1\",\n"
                        + "      \"rktKind\": \"stage1\",\n"
                        + "      \"name\": \"coreos.com/rkt/stage1-coreos\",\n"
                        + "      \"version\": \"0.15.0+git\",\n"
                        + "      \"location\": \"/usr/libexec/rkt/stage1-coreos.aci\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(json.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "config")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Config config = Config.COMMAND;
    final ConfigOutput configOutput = rktLauncher.run(config);
    assertEquals("paths", configOutput.config().stage0().get(0).rktKind());
  }

  @Test
  public void shouldRunExport() throws Exception {
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(new byte[]{}));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "export", "--app=nginx", "1d284607",
                         "output.aci")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.Export export = io.honnix.rkt.launcher.command.Export.builder()
        .options(ExportOptions.builder()
                     .app("nginx")
                     .build())
        .addArg("1d284607")
        .addArg("output.aci")
        .build();
    final Output exportOutput = rktLauncher.run(export);
    assertSame(Output.NULL, exportOutput);
  }

  @Test
  public void shouldRunFetch() throws Exception {
    final String output =
        "image: keys already exist for prefix \"coreos.com/etcd\", not fetching again\n"
        + "Downloading signature: [=======================================] 819 B/819 B\n"
        + "Downloading ACI: [=============================================] 3.7 MB/3.7 MB\n"
        + "image: signature verified:\n"
        + "  CoreOS ACI Builder <release@coreos.com>\n"
        + "sha512-fa1cb92dc276b0f9bedf87981e61ecde93cc16432d2441f23aa006a42bb873df";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "fetch", "--full=true",
                         "--pull-policy=new", "coreos.com/etcd:v2.0.0")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Fetch fetch = Fetch.builder()
        .options(FetchOptions.builder()
                     .full(true)
                     .pullPolicy(PullPolicy.NEW)
                     .build())
        .addArg("coreos.com/etcd:v2.0.0")
        .build();
    final FetchOutput fetchOutput = rktLauncher.run(fetch);
    assertEquals("sha512-fa1cb92dc276b0f9bedf87981e61ecde93cc16432d2441f23aa006a42bb873df",
                 fetchOutput.hash());
  }

  @Test
  public void shouldRunGc() throws Exception {
    final String output = "gc: moving pod \"6806ba33-0bfa-4389-84ba-3abba3dba97b\" to garbage\n"
                          + "gc: moving pod \"7806ba33-0bfa-4389-84ba-3abba3dba97b\" to garbage\n"
                          + "gc: pod \"6806ba33-0bfa-4389-84ba-3abba3dba97b\" not removed: still within grace period (5m10s)\n"
                          + "gc: pod \"7806ba33-0bfa-4389-84ba-3abba3dba97b\" not removed: still within grace period (5m10s)";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "gc", "--mark-only=true")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.Gc gc = io.honnix.rkt.launcher.command.Gc.builder()
        .options(GcOptions.builder()
                     .markOnly(true)
                     .build())
        .build();
    final io.honnix.rkt.launcher.output.GcOutput gcOutput = rktLauncher.run(gc);
    assertEquals(2, gcOutput.marked().size());
  }

  @Test
  public void shouldRunList() throws Exception {
    //language=JSON
    final String json = "[\n"
                        + "  {\n"
                        + "    \"name\": \"1e4bb8f2-ea9c-4c5d-8c20-66ef94a2c74b\",\n"
                        + "    \"state\": \"running\",\n"
                        + "    \"networks\": [\n"
                        + "      {\n"
                        + "        \"netName\": \"default\",\n"
                        + "        \"netConf\": \"net/99-default.conf\",\n"
                        + "        \"pluginPath\": \"stage1/rootfs/usr/lib/rkt/plugins/net/ptp\",\n"
                        + "        \"ifName\": \"eth0\",\n"
                        + "        \"ip\": \"172.16.28.4\",\n"
                        + "        \"args\": \"\",\n"
                        + "        \"mask\": \"255.255.255.0\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"app_names\": [\n"
                        + "      \"nginx\"\n"
                        + "    ],\n"
                        + "    \"started_at\": 1497280167\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"name\": \"9c1c7e0b-8927-45e1-a1de-5cd34f04a246\",\n"
                        + "    \"state\": \"running\",\n"
                        + "    \"networks\": [\n"
                        + "      {\n"
                        + "        \"netName\": \"default\",\n"
                        + "        \"netConf\": \"net/99-default.conf\",\n"
                        + "        \"pluginPath\": \"stage1/rootfs/usr/lib/rkt/plugins/net/ptp\",\n"
                        + "        \"ifName\": \"eth0\",\n"
                        + "        \"ip\": \"172.16.28.2\",\n"
                        + "        \"args\": \"\",\n"
                        + "        \"mask\": \"255.255.255.0\"\n"
                        + "      }\n"
                        + "    ],\n"
                        + "    \"app_names\": [\n"
                        + "      \"nginx\"\n"
                        + "    ],\n"
                        + "    \"started_at\": 1497112016\n"
                        + "  }\n"
                        + "]";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(json.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "list", "--format=json")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final io.honnix.rkt.launcher.command.List list = io.honnix.rkt.launcher.command.List.builder()
        .options(ListOptions.builder().build())
        .build();
    final ListOutput listOutput = rktLauncher.run(list);
    assertEquals(2, listOutput.pods().size());
  }

  @Test
  public void shouldRunPrepare() throws Exception {
    final String output = "6c8158de-e3f0-46af-9100-88591c17d302";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "prepare",
                         "--quiet=true",
                         "docker://nginx",
                         "---",
                         "docker://mysql",
                         "---")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Prepare prepare = Prepare.builder()
        .options(PrepareOptions.builder()
                     .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
                     .addImagesOption(PerImageOptions.builder().image("docker://mysql").build())
                     .build())
        .build();
    final PrepareOutput prepareOutput = rktLauncher.run(prepare);
    assertEquals("6c8158de-e3f0-46af-9100-88591c17d302", prepareOutput.prepared());
  }

  @Test
  public void shouldRunRm() throws Exception {
    final String output = "\"1e4bb8f2\"\n"
                          + "\"9c1c7e0b\"";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "rm", "1e4bb8f2", "9c1c7e0b")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Rm rm = Rm.builder()
        .options(io.honnix.rkt.launcher.options.RmOptions.builder().build())
        .args(ImmutableList.of("1e4bb8f2", "9c1c7e0b"))
        .build();
    final io.honnix.rkt.launcher.output.RmOutput rmOutput = rktLauncher.run(rm);
    assertEquals(2, rmOutput.removed().size());
  }

  @Test
  public void shouldRunRun() throws Exception {
    final String output = "Running as unit run-r69ece8681fbc4e7787e639a78b141599.service.";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "run", "docker://nginx", "---")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Run run = Run.builder()
        .options(RunOptions.builder()
                     .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
                     .build())
        .build();
    final RunOutput runOutput = rktLauncher.run(run);
    assertEquals("NA", runOutput.service());
  }

  @Test
  public void shouldRunRunAsDaemon() throws Exception {
    final String output = "Running as unit run-r69ece8681fbc4e7787e639a78b141599.service.";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("systemd-run", "--slice=machine", "rkt", "--insecure-options=image", "run",
                         "docker://nginx", "---")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Run run = Run.builder()
        .options(RunOptions.builder()
                     .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
                     .build())
        .daemonize(true)
        .build();
    final RunOutput runOutput = rktLauncher.run(run);
    assertEquals("run-r69ece8681fbc4e7787e639a78b141599.service", runOutput.service());
  }

  @Test
  public void shouldRunRunPrepared() throws Exception {
    final String output = "Running as unit run-r69ece8681fbc4e7787e639a78b141599.service.";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "run-prepared", "--net=default",
                         "2a98c7c0")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final RunPrepared runPrepared = RunPrepared.builder()
        .options(RunPreparedOptions.builder()
                     .net(ImmutableList.of(Network.DEFAULT))
                     .build())
        .addArg("2a98c7c0")
        .build();
    final RunOutput runOutput = rktLauncher.run(runPrepared);
    assertEquals("NA", runOutput.service());
  }

  @Test
  public void shouldRunRunPreparedAsDaemon() throws Exception {
    final String output = "Running as unit run-r69ece8681fbc4e7787e639a78b141599.service.";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("systemd-run", "--slice=machine", "rkt", "--insecure-options=image",
                         "run-prepared", "--net=default", "2a98c7c0")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final RunPrepared runPrepared = RunPrepared.builder()
        .options(RunPreparedOptions.builder()
                     .net(ImmutableList.of(Network.DEFAULT))
                     .build())
        .addArg("2a98c7c0")
        .daemonize(true)
        .build();
    final RunOutput runOutput = rktLauncher.run(runPrepared);
    assertEquals("run-r69ece8681fbc4e7787e639a78b141599.service", runOutput.service());
  }

  @Test
  public void shouldRunStatus() throws Exception {
    //language=JSON
    final String json = "{\n"
                        + "  \"name\": \"1e4bb8f2-ea9c-4c5d-8c20-66ef94a2c74b\",\n"
                        + "  \"state\": \"running\",\n"
                        + "  \"networks\": [\n"
                        + "    {\n"
                        + "      \"netName\": \"default\",\n"
                        + "      \"netConf\": \"net/99-default.conf\",\n"
                        + "      \"pluginPath\": \"stage1/rootfs/usr/lib/rkt/plugins/net/ptp\",\n"
                        + "      \"ifName\": \"eth0\",\n"
                        + "      \"ip\": \"172.16.28.4\",\n"
                        + "      \"args\": \"\",\n"
                        + "      \"mask\": \"255.255.255.0\"\n"
                        + "    }\n"
                        + "  ],\n"
                        + "  \"app_names\": [\n"
                        + "    \"nginx\"\n"
                        + "  ],\n"
                        + "  \"started_at\": 1497280167\n"
                        + "}";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(json.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "status", "--format=json", "1e4bb8f2")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Status status = Status.builder()
        .options(StatusOptions.builder().build())
        .addArg("1e4bb8f2")
        .build();
    final StatusOutput statusOutput = rktLauncher.run(status);
    assertEquals("1e4bb8f2-ea9c-4c5d-8c20-66ef94a2c74b", statusOutput.status().name());
  }

  @Test
  public void shouldRunStop() throws Exception {
    final String output = "\"1e4bb8f2\"\n"
                          + "\"9c1c7e0b\"";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "stop", "--force=true",
                         "1e4bb8f2", "9c1c7e0b")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Stop stop = Stop.builder()
        .options(StopOptions.builder()
                     .force(true)
                     .build())
        .args(ImmutableList.of("1e4bb8f2", "9c1c7e0b"))
        .build();
    final StopOutput stopOutput = rktLauncher.run(stop);
    assertEquals(2, stopOutput.stopped().size());
  }

  @Test
  public void shouldRunTrust() throws Exception {
    final String output = "pubkey: prefix: \"\"\n"
                          + "key: \"test.key\"\n"
                          + "gpg key fingerprint is: 3367 1532 095F 91C5 CB26  7B35 882B FD6B 87CF C5E9\n"
                          + "    Subkey fingerprint: 8009 438D 951F 67A7 7F06  8BD3 E75D F9C2 35EC 0676\n"
                          + "    Subkey fingerprint: 818B 5C43 34EB E280 A3CB  D285 3810 BDA9 2FCB 61B2\n"
                          + "\ta (a) <a@a.com>\n"
                          + "Trusting \"test.key\" for prefix \"\" without fingerprint review.\n"
                          + "Added root key at \"/etc/rkt/trustedkeys/root.d/33671532095f91c5cb267b35882bfd6b87cfc5e9\"";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "trust", "--root=true",
                         "--skip-fingerprint-review=true", "test.key")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Trust trust = Trust.builder()
        .options(TrustOptions.builder()
                     .root(true)
                     .build())
        .args(ImmutableList.of("test.key"))
        .build();
    final TrustOutput trustOutput = rktLauncher.run(trust);
    assertEquals(1, trustOutput.trustedPubkeys().size());
    assertEquals("test.key", trustOutput.trustedPubkeys().get(0).key());
    assertEquals("/etc/rkt/trustedkeys/root.d/33671532095f91c5cb267b35882bfd6b87cfc5e9",
                 trustOutput.trustedPubkeys().get(0).location());
    assertEquals("", trustOutput.trustedPubkeys().get(0).prefix());
  }

  @Test
  public void shouldRunVersion() throws Exception {
    final String output = "rkt Version: 1.25.0\n"
                          + "appc Version: 0.8.10\n"
                          + "Go Version: go1.7.4\n"
                          + "Go OS/Arch: linux/amd64\n"
                          + "Features: -TPM +SDJOURNAL";
    final ProcessResult processResult = new ProcessResult(0, new ProcessOutput(output.getBytes()));
    when(processExecutor.command(
        ImmutableList.of("rkt", "--insecure-options=image", "version")))
        .thenReturn(processExecutor);
    when(processExecutor.executeNoTimeout()).thenReturn(processResult);
    final Version version = Version.builder().build();
    final VersionOutput versionOutput = rktLauncher.run(version);
    assertEquals("1.25.0", versionOutput.rktVersion());
    assertEquals("0.8.10", versionOutput.appcVersion());
    assertEquals("go1.7.4", versionOutput.goVersion());
    assertEquals("linux/amd64", versionOutput.goOSArch());
    assertEquals(ImmutableList.of("-TPM", "+SDJOURNAL"), versionOutput.features());
  }
}
