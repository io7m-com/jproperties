jproperties
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jproperties/com.io7m.jproperties.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jproperties%22)
[![Maven Central (snapshot)](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fcom%2Fio7m%2Fjproperties%2Fcom.io7m.jproperties%2Fmaven-metadata.xml&style=flat-square)](https://central.sonatype.com/repository/maven-snapshots/com/io7m/jproperties/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/jproperties.svg?style=flat-square)](https://codecov.io/gh/io7m-com/jproperties)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=e6c35c)

![com.io7m.jproperties](./src/site/resources/jproperties.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jproperties/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/jproperties/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jproperties/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/jproperties/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jproperties/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/jproperties/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jproperties/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/jproperties/actions?query=workflow%3Amain.windows.temurin.lts)|

## Repository Relocation

Development of this project has moved to an
[open-source but not open-contribution](https://sqlite.org/copyright.html#notopencontrib)
model.

Source code and commits will remain publicly available perpetually, but issues
and/or pull requests will be rejected and/or ignored. Additionally, this project
will now only be available via a read-only mirror at:

  https://codeberg.org/io7m-com/jproperties


## jproperties

Java functions to access values from `java.util.Properties` collections in
a typed manner.

## Features

* Functions to access `java.util.Properties` values as typed values.
* High coverage test suite.
* [OSGi-ready](https://www.osgi.org/)
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
* ISC license.

## Usage

The `JProperties` class contains numerous functions to extract typed values
from a `Properties` collection:

```
final var p = new Properties();
p.setProperty("int0", "23");
p.setProperty("b0", "true");

JProperties.getBoolean(p, "b0");       // ⇒ true
JProperties.getBigInteger(p, "int0");  // ⇒ new BigInteger(23)
```

If a property is not present, or has a value that cannot be parsed as a value
of the requested type, a `JPropertyException` is raised.

```
JProperties.getBigInteger(p, "nonexistent") // ⇒ JPropertyNonexistent
JProperties.getBigInteger(p, "b0");         // ⇒ JPropertyIncorrectType
```

Functions typically have an optional variant that returns an `Optional.empty()`
value on missing keys (but an exception will still be raised on type errors).

```
JProperties.getStringOptional(p, "nonexistent") // ⇒ Optional.empty()
JProperties.getStringOptional(p, "b0");         // ⇒ "true"
```

Functions typically also have a `default` variant that can return a given
default value if a property is not present (but an exception will still be
raised on type errors).

```
JProperties.getStringWithDefault(p, "nonexistent", "z") // ⇒ "z"
JProperties.getStringWithDefault(p, "b0", "z");         // ⇒ "true"
```

