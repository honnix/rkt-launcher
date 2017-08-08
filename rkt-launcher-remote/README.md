# rkt-launcher-remote

A client library talking to `rkt-launcher-service` remotely.

## Usage

### Setup

```xml
<dependency>
  <groupId>io.honnix</groupId>
  <artifactId>rkt-launcher-remote</artifactId>
  <version>${rkt-launcher-version}</version>
</dependency>
```

### To run a pod remotely as daemon

```
final Service rktRemoteService = Services.usingName("rkt-remote")
        .withEnvVarPrefix("RKT_REMOTE")
        .withModule(ApolloEnvironmentModule.create())
        .withModule(HttpClientModule.create())
        .build();
try (Service.Instance instance = rktRemoteService.start()) {
    final Client client = ApolloEnvironmentModule.environment(instance).environment().client();
    final RktLauncherRemote rktLauncherRemote = RktLauncherRemote.builder()
        .scheme(HTTP)
        .host("rkt.example.com")
        .port(80)
        .client(client)
        .build();
    final RunOptions options = RunOptions.builder()
        .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
        .build();
    rktLauncherRemote.run(options, true);
    final CompletionStage<RunOutput> output = rktCommandRemote.run(options, true);
    ...
} catch (Exception e) {
    ...
}
```

Or instead of using a full-featured Apollo Client, a light-weighted client
is provided by this module.

```
import io.honnix.rkt.launcher.remote.http.Client;

...
final Client client = new Client(new OkHttpClient());
final RktLauncherRemote rktLauncherRemote = RktLauncherRemote.builder()
    .scheme(HTTP)
    .host("rkt.example.com")
    .port(80)
    .client(client)
    .build();
final RunOptions options = RunOptions.builder()
    .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
    .build();
rktLauncherRemote.run(options, true);
final CompletionStage<RunOutput> output = rktCommandRemote.run(options, true);
...
```

### More examples

[SystemTest.java](src/test/java/io/honnix/rkt/launcher/remote/SystemTest.java)
is usually a good place to find more examples of how to use this library.
