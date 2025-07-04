<h1 align="center">MasteryLauncher</h1>

<img src="https://github.com/MasteryLauncherTeam/MasteryLauncher/blob/v3_openjdk/app_MasteryLauncher/src/main/assets/MasteryLauncher.png" align="left" width="130" height="150" alt="MasteryLauncher logo">

[![Android CI](https://github.com/MasteryLauncherTeam/MasteryLauncher/workflows/Android%20CI/badge.svg)](https://github.com/MasteryLauncherTeam/MasteryLauncher/actions)
[![GitHub commit activity](https://img.shields.io/github/commit-activity/m/MasteryLauncherTeam/MasteryLauncher)](https://github.com/MasteryLauncherTeam/MasteryLauncher/actions)
[![Crowdin](https://badges.crowdin.net/MasteryLauncher/localized.svg)](https://crowdin.com/project/MasteryLauncher)
[![Discord](https://img.shields.io/discord/724163890803638273.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.com/invite/aenk3EUvER)
[![Twitter Follow](https://img.shields.io/twitter/follow/plaunchteam?color=blue&style=flat-square)](https://twitter.com/PLaunchTeam)

*From [Boardwalk](https://github.com/zhuowei/Boardwalk)'s ashes here comes MasteryLauncher!*

MasteryLauncher is a launcher that allows you to play Minecraft: Java Edition on your Android and [iOS](https://github.com/MasteryLauncherTeam/MasteryLauncher_iOS) devices.

For more details, check out our [wiki](https://MasteryLauncher.app/)!

## Important Notes

* We do not have an official TikTok account. No one from the dev team makes TikTok videos.
* The official Twitter for MasteryLauncher is [@PLaunchTeam](https://twitter.com/PLaunchTeam). Any others (most notably @MasteryLauncher) are fake.

## Table of Contents

* [Introduction](#introduction)
* [Getting MasteryLauncher](#getting-MasteryLauncher)
* [Building](#building)
    * [Quick Build (Recommended)](#quick-build-recommended)
    * [Detailed Build](#detailed-build)
* [Current Status](#current-status)
* [Known Issues](#known-issues)
* [FAQ](#faq)
* [Contributing](#contributing)
* [Support](#support)
* [License](#license)
* [Credits & Dependencies](#credits--dependencies)
* [Roadmap](#roadmap)

## Introduction

* MasteryLauncher is a Minecraft: Java Edition launcher for Android and iOS based on [Boardwalk](https://github.com/zhuowei/Boardwalk)
* This launcher can launch almost all available Minecraft versions ranging from rd-132211 to 1.21 snapshots (including Combat Test versions)
* Modding via Forge and Fabric are also supported.
* This repository contains source code for Android. For iOS/iPadOS, check out [MasteryLauncher_iOS](https://github.com/MasteryLauncherTeam/MasteryLauncher_iOS).

## Getting MasteryLauncher

You can get MasteryLauncher via three methods:

1. **Releases:** Download the prebuilt app from our [stable releases](https://github.com/MasteryLauncherTeam/MasteryLauncher/releases) or [automatic builds](https://github.com/MasteryLauncherTeam/MasteryLauncher/actions).
2. **Google Play:** Get it from Google Play by clicking on this badge: [![Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.redemastery.oldapi.pojav)
3. **Build from Source:** Follow the [building instructions](#building) below.

## Building

### Quick Build (Recommended)

The easiest way to build MasteryLauncher is to use the pre-built JREs provided by our CI.

1. Clone the repository: `git clone https://github.com/MasteryLauncherTeam/MasteryLauncher.git`
2. Build the launcher: `./gradlew :app_MasteryLauncher:assembleDebug` (Use `gradlew.bat` on Windows)

The built APK will be located in `app_MasteryLauncher/build/outputs/apk/debug/`.

### Detailed Build

If you need more control over the build process, follow these steps:

1. **Java Runtime Environment (JRE):** Download the `jre8-pojav` artifact from our [CI auto builds](https://github.com/MasteryLauncherTeam/android-openjdk-build-multiarch/actions).  This package contains pre-built JREs for all supported architectures.  If you need to build the JRE yourself, follow the instructions in the [android-openjdk-build-multiarch](https://github.com/MasteryLauncherTeam/android-openjdk-build-multiarch) repository.

2. **LWJGL:** The build instructions for the custom LWJGL are available over the [LWJGL repository](https://github.com/MasteryLauncherTeam/lwjgl3).

3. **Language List:** Because languages are auto-added by Crowdin, you need to run the language list generator before building. In the project directory, run:
   * Linux/macOS:
     ```bash
     chmod +x scripts/languagelist_updater.sh
     bash scripts/languagelist_updater.sh
     ```
   * Windows:
     ```batch
     scripts\languagelist_updater.bat
     ```

4. **Build GLFW stub:** `./gradlew :jre_lwjgl3glfw:build`

5. **Build the launcher:** `./gradlew :app_MasteryLauncher:assembleDebug` (Replace `gradlew` with `gradlew.bat` on Windows).

## Current Status

* [x] OpenJDK 8 Mobile port: ARM32, ARM64, x86, x86_64
* [x] OpenJDK 17 Mobile port: ARM32, ARM64, x86, x86_64
* [x] OpenJDK 21 Mobile port: ARM32, ARM64, x86, x86_64
* [x] Headless mod installer
* [x] Mod installer with GUI
* [x] OpenGL in OpenJDK environment
* [x] OpenAL (works on most devices)
* [x] Support for Minecraft 1.12.2 and below
* [x] Support for Minecraft 1.13 and above
* [x] Support for Minecraft 1.17 (22w13a) and above
* [x] Game surface zooming
* [x] New input pipe rewritten to native code
* [x] Rewritten entire controls system
* [ ] More to come!

## Known Issues

See our [issue tracker](https://github.com/MasteryLauncherTeam/MasteryLauncher/issues) for a list of known issues and their current status.

## FAQ

See our [wiki](https://MasteryLauncherteam.github.io/) for more information.

## Contributing

Contributions are welcome! We welcome any type of contribution, not only code. For example, you can help improve the [wiki](https://MasteryLauncherteam.github.io/), contribute to the [translations](https://crowdin.com/project/MasteryLauncher), or submit bug reports and feature requests.

Any code change should be submitted as a pull request. The description should explain what the code does and give steps to execute it.

## Support

For support, please join our [Discord server](https://discord.com/invite/aenk3EUvER).

## License

MasteryLauncher is licensed under [GNU LGPLv3](https://github.com/MasteryLauncherTeam/MasteryLauncher/blob/v3_openjdk/LICENSE).

## Credits & Dependencies

* [Boardwalk](https://github.com/zhuowei/Boardwalk) (JVM Launcher): Unknown License/[Apache License 2.0](https://github.com/zhuowei/Boardwalk/blob/master/LICENSE) or GNU GPLv2.
* Android Support Libraries: [Apache License 2.0](https://android.googlesource.com/platform/prebuilts/maven_repo/android/+/master/NOTICE.txt).
* [GL4ES](https://github.com/MasteryLauncherTeam/gl4es): [MIT License](https://github.com/ptitSeb/gl4es/blob/master/LICENSE).
* [OpenJDK](https://github.com/MasteryLauncherTeam/openjdk-multiarch-jdk8u): [GNU GPLv2 License](https://openjdk.java.net/legal/gplv2+ce.html).
* [LWJGL3](https://github.com/MasteryLauncherTeam/lwjgl3): [BSD-3 License](https://github.com/LWJGL/lwjgl3/blob/master/LICENSE.md).
* [LWJGLX](https://github.com/MasteryLauncherTeam/lwjglx) (LWJGL2 API compatibility layer for LWJGL3): unknown license.
* [Mesa 3D Graphics Library](https://gitlab.freedesktop.org/mesa/mesa): [MIT License](https://docs.mesa3d.org/license.html).
* [pro-grade](https://github.com/pro-grade/pro-grade) (Java sandboxing security manager): [Apache License 2.0](https://github.com/pro-grade/pro-grade/blob/master/LICENSE.txt).
* [bhook](https://github.com/bytedance/bhook) (Used for exit code trapping): [MIT license](https://github.com/bytedance/bhook/blob/main/LICENSE).
* [libepoxy](https://github.com/anholt/libepoxy): [MIT License](https://github.com/anholt/libepoxy/blob/master/COPYING).
* [virglrenderer](https://github.com/MasteryLauncherTeam/virglrenderer): [MIT License](https://gitlab.freedesktop.org/virgl/virglrenderer/-/blob/master/COPYING).
* Thanks to [MCHeads](https://mc-heads.net) for providing Minecraft avatars.

## Roadmap

We are currently focusing on:

* Exploring new rendering technologies.

Future plans include:

* Improving stability and performance.
* Enhancing the mod installation experience.

We welcome community feedback and suggestions for our roadmap.  Please feel free to open a feature request in our [issue tracker](https://github.com/MasteryLauncherTeam/MasteryLauncher/issues).
