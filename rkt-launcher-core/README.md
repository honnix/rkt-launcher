# rkt-launcher-core

Interact with [rkt] in a type-safe way.

## Description

rkt-launcher-core is designed to interact with `rkt` in a type-safe
way.

`rkt` provides an API service in a read-only manner, which means one can
read status of images, pods, etc., but will not be able to fetch images,
run pods, etc.

This library acts as a thin wrapper/helper to fork `rkt` process,
passing in command options and arguments provided by the user, and
interpreting output from `rkt` process.

This library tries to make command options as type-safe as possible by
utilizing proper Java types, e.g. String, Integer, enum, and customized
classes. Some `rkt` commands returns JSON for which POJOs are 
defined to deserialize smoothly; other commands may return arbitrary 
strings for which regex is used to interpret in a best effort way.

## Usage

### Setup

NOT yet in Maven central.

```xml
<dependency>
  <groupId>io.honnix</groupId>
  <artifactId>rkt-launcher-core</artifactId>
  <version>${rkt-launcher-version}</version>
</dependency>
```

### Prerequisite

`rkt` requires root privilege to for non read-only operations, such as
running a pod. Application using this library will need to be run by
root in order to interact with `rkt` for those operations.

### To run a pod

```java
final GlobalOptions globalOptions = GlobalOptions.builder()
    .insecureOptions(ImmutableList.of(IMAGE))
    .build();
final RktLauncherConfig rktLauncherConfig = RktLauncherConfig.builder()
    .rkt("rkt")
    .globalOptions(globalOptions)
    .build();
final RktLauncher rktLauncher = new RktLauncher(rktLauncherConfig);
final Run run = Run.builder()
    .options(RunOptions.builder()
                 .addImagesOption(PerImageOptions.builder().image("docker://nginx").build())
                 .build())
    .daemonize(true) // if false current thread will block until the pod finishes
    .build();
final RunOutput runOutput = rktLauncher.run(run);
assertEquals("run-r69ece8681fbc4e7787e639a78b141599.service", runOutput.service());
```

`rkt` itself doesn't provide any daemon capability
(read [here][using-rkt-with-systemd] for details).
This library uses `systemd-run` and `--slice=machine` by default to
launch pod as daemon. To change the default configuration,
`RktLauncherConfig.daemon()` can be used to do customization.

### More examples

[SystemTest.java](src/test/java/io/honnix/rkt/launcher/SystemTest.java)
is usually a good place to find more examples of how to use this library.

[rkt]: https://coreos.com/rkt/
[using-rkt-with-systemd]: https://rocket.readthedocs.io/en/latest/Documentation/using-rkt-with-systemd/
