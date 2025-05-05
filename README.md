<h1 align="center" style="font-weight: normal;"><b>MC-Server-Test</b></h1>
<p align="center">Test Minecraft production servers inside your CI/CD pipeline.</p>
<p align="center">
MC-Server-Test | <a href="https://github.com/3arthqu4ke/headlessmc">HMC</a> | <a href="https://github.com/3arthqu4ke/hmc-specifics">HMC-Specifics</a> | <a href="https://github.com/3arthqu4ke/hmc-optimizations">HMC-Optimizations</a>
</p>

<div align="center">

[![CodeFactor](https://www.codefactor.io/repository/github/headlesshq/mc-runtime-test/badge/main)](https://www.codefactor.io/repository/github/headlesshq/mc-runtime-test/overview/main)
[![GitHub All Releases](https://img.shields.io/github/downloads/headlesshq/mc-server-test/total.svg)](https://github.com/headlesshq/mc-server-test/releases)
![GitHub License](https://img.shields.io/github/license/headlesshq/mc-server-test)
![GitHub Last Commit](https://img.shields.io/github/last-commit/headlesshq/mc-server-test)

</div>

> [!NOTE]  
> This is **not an official Minecraft product**. It is **not approved by or associated with Mojang or Microsoft**.

---

MC-Server-Test enables you to run a Minecraft production server within your CI/CD pipelines, 
simplifying the testing of runtime bugs in Minecraft server mods/plugins.
Manual testing for different Minecraft versions and modloaders can be time-consuming,
especially for bugs that do not occur in the usual dev environments, like mixin or remapping issues.
This project helps streamline that process by automating the server launch and basic test execution.

For a client equivalent see [mc-runtime-test](https://github.com/headlesshq/mc-runtime-test).

MC-Server-Test supports basically all versions of Minecraft 
and the server types paper, fabric, forge, neoforge and vanilla.

By default it will wait for the server to start,
and then sends the stop command as soon as the world has loaded.
This way you can see if the server launches with your plugin.
You can also specify your own command interactions with the server
to test via our [json schema](./test.json).

## Quickstart Example
Below is a basic workflow example to run the Minecraft server using MC-Server-Test.

<pre lang="yml">
name: Run Minecraft Server

on:
workflow_dispatch:

env:
java_version: 21

jobs:
run:
runs-on: ubuntu-latest
steps:
- name: Install Java
uses: actions/setup-java@v4
with:
java-version: ${{ env.java_version }}
distribution: "temurin"

      - name: [Example] Build mod
        run: ./gradlew build

      - name: [Example] Stage mod for test client
        run: |
          mkdir -p run/mods  # or run/plugins on paper
          cp build/libs/&lt;your-mod&gt;.jar run/mods

      - name: Run MC test client
        uses: headlesshq/mc-server-test@0.1.0 <!-- x-release-please-version -->
        with:
          mc: 1.21.5
          modloader: fabric
          java: ${{ env.java_version }}
</pre>

More examples (replace `uses: ./` with `uses: headlesshq/mc-server-test ...`):
- [Paper Workflow Example](.github/workflows/test-plugin.yml)
- [Matrix Workflow Testing Multiple Versions](.github/workflows/run-matrix.yml)

---

## Inputs
The following table summarizes the available inputs for customization:

| Input                 | Description                            | Required | Example                            |
|-----------------------|----------------------------------------|----------|------------------------------------|
| `mc`                  | Minecraft version to run               | Yes      | `1.21.5`                           |
| `modloader`           | Modloader to install                   | Yes      | `paper`, `fabric`, `neoforge`,     |
| `java`                | Java version to use                    | Yes      | `8`, `16`, `17`, `21`              |
| `headlessmc-command`  | Command-line arguments for HeadlessMC  |          | `--jvm "-Djava.awt.headless=true"` |
| `fabric-api`          | Fabric API version to download or none |          | `0.97.0`, `none`                   |
| `fabric-gametest-api` | Fabric GameTest API version or none    |          | `1.3.5+85d85a934f`, `none`         |
| `download-hmc`        | Download HeadlessMC                    |          | `true`, `false`                    |
| `hmc-version`         | HeadlessMC version                     |          | `2.5.1`, `1.5.0`                   |
| `cache-mc`            | Cache `.minecraft`                     |          | `true`, `false`                    |
| `test-file`           | Path to HeadlessMc test file           |          | `./test.json`                      |
---

## Running Your Own Tests
> [!NOTE]  
> GameTest support is a TODO, but you can run the test command via the json.

With `test-file` you can specify a json file for HeadlessMc that
defines a test to execute. 
Here is a simple example to test the paper plugin in the [test](/test) directory:
```json
{
  "name": "Server with Plugin Test",
  "steps": [
    {
      "type": "ENDS_WITH",
      "message": "For help, type \"help\""
    },
    {
      "type": "SEND",
      "message": "mc-server-test-command"
    },
    {
      "type": "ENDS_WITH",
      "message": "Hello from mc-server-test!"
    },
    {
      "type": "SEND",
      "message": "stop",
      "timeout": 120
    }
  ]
}
```
It first checks for the server to be loaded by checking for log messages
ending with `For help, type \"help\"`.
This message is output by basically all Minecraft server versions.
Afterwards, it sends the command of the plugin to the server.
It then expects the server to respond with the output `Hello from mc-server-test!`.
Lastly, the server is stopped.
