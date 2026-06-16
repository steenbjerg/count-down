# Count Down - JavaFX Application

A JavaFX application built with Gradle, using Liberica JDK for native compilation with GraalVM.

## BellSoft Liberica Setup
Download Liberica from https://bell-sw.com/pages/downloads/

setup up java with the script setup-java.sh (and source it) or manually:

or this for jdk 25:

```{script}
export JAVA_HOME=/opt/bellsoft/liberica-vm-full-25.0.3-openjdk25
export PATH=/opt/bellsoft/liberica-vm-full-25.0.3-openjdk25/bin:$PATH
```

In order to compile native you must install the following packages:
```{script}
sudo apt install zlib1g-dev
```

## Graalvm Gradle plugin for Native Client
see https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html

Run this to collect information about classes accessed via reflections and resources needed.  

Do this for creating the necessary metadata files:

```{script}
./gradlew -Pagent=standard run
./gradlew metadataCopy
```

Run this for doing the actual native compilation:

```{script}
./gradlew nativeCompile
```

or commit an the github actions will do it for you.
