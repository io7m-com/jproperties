/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jproperties.tests;

import com.io7m.jproperties.JPropertyIncorrectType;
import com.io7m.jproperties.monad.JPropertyFailure;
import com.io7m.jproperties.monad.JPropertyParseMonadType;
import com.io7m.jproperties.monad.JPropertyParsing;
import com.io7m.jproperties.monad.JPropertySuccess;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.io7m.jproperties.monad.JPropertyParseMonadType.*;

public final class JPropertiesMonadTest
{
  private static final Logger LOG = Logger.getLogger(JPropertiesMonadTest.class.getCanonicalName());

  @Test
  public void testFlatMap0()
  {
    final var properties = new Properties();
    properties.setProperty("int", "23");
    properties.setProperty("uri", "http://www.example.com");

    final JPropertyParseMonadType<List<Serializable>> results =
      JPropertyParsing.parseBigInteger(properties, "int")
        .flatMap(x_int -> JPropertyParsing.parseURI(properties, "uri")
          .flatMap(x_uri -> JPropertyParsing.successOf(List.of(x_int, x_uri))));

    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));

    final var success = (JPropertySuccess<List<Serializable>>) results;
    Assert.assertEquals(BigInteger.valueOf(23), success.result().get(0));
    Assert.assertEquals(URI.create("http://www.example.com"), success.result().get(1));
  }

  @Test
  public void testWarnings0()
  {
    final var properties = new Properties();
    properties.setProperty("int", "23");
    properties.setProperty("uri", "http://www.example.com");

    final JPropertyParseMonadType<List<Serializable>> results =
      JPropertyParsing.warn("Warning 0")
        .andThen(() -> JPropertyParsing.warn("x", "Warning 1"))
        .andThen(() -> JPropertyParsing.parseBigInteger(properties, "int")
          .flatMap(x_int -> JPropertyParsing.parseURI(properties, "uri")
            .flatMap(x_uri -> JPropertyParsing.successOf(List.of(x_int, x_uri)))));

    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<List<Serializable>>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals("Warning 0", actual.warnings().get(0).message());
    Assert.assertEquals("Key 'x': Warning 1", actual.warnings().get(1).message());
    Assert.assertEquals(BigInteger.valueOf(23), actual.result().get(0));
    Assert.assertEquals(URI.create("http://www.example.com"), actual.result().get(1));
  }

  @Test
  public void testURI()
  {
    final var properties = new Properties();
    properties.setProperty("uri", "http://www.example.com");

    final var results = JPropertyParsing.parseURI(properties, "uri");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<URI>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(URI.create("http://www.example.com"), actual.result());
  }

  @Test
  public void testBadURI()
  {
    final var properties = new Properties();
    properties.setProperty("uri", " not a uri");

    final var results = JPropertyParsing.parseURI(properties, "uri");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<URI>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(URISyntaxException.class, actual.exception().getClass());
  }

  @Test
  public void testURIWithDefault()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseURIWithDefault(
      properties,
      "uri",
      URI.create("http://www.example.com"));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<URI>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(URI.create("http://www.example.com"), actual.result());
  }

  @Test
  public void testURIWithDefaultPresent()
  {
    final var properties = new Properties();
    properties.setProperty("URI", "http://www.example.com");

    final var results = JPropertyParsing.parseURIWithDefault(
      properties,
      "URI",
      URI.create("http://www.example.com/xyz"));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<URI>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(URI.create("http://www.example.com"), actual.result());
  }

  @Test
  public void testDuration()
  {
    final var properties = new Properties();
    properties.setProperty("Duration", "PT15M");

    final var results = JPropertyParsing.parseDuration(properties, "Duration");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<Duration>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(Duration.parse("PT15M"), actual.result());
  }

  @Test
  public void testDurationWithDefault()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseDurationWithDefault(
      properties,
      "Duration",
      Duration.parse("PT10S"));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<Duration>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(Duration.parse("PT10S"), actual.result());
  }

  @Test
  public void testDurationWithDefaultPresent()
  {
    final var properties = new Properties();
    properties.setProperty("Duration", "PT10S");

    final var results = JPropertyParsing.parseDurationWithDefault(
      properties,
      "Duration",
      Duration.parse("PT15S"));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<Duration>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(Duration.parse("PT10S"), actual.result());
  }

  @Test
  public void testBadDuration()
  {
    final var properties = new Properties();
    properties.setProperty("Duration", " not a Duration");

    final var results = JPropertyParsing.parseDuration(properties, "Duration");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<Duration>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(DateTimeParseException.class, actual.exception().getClass());
  }

  @Test
  public void testOffsetDateTime()
  {
    final var properties = new Properties();
    properties.setProperty("OffsetDateTime", "2000-01-01T00:00:00+00:20");

    final var results = JPropertyParsing.parseOffsetDateTime(properties, "OffsetDateTime");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<OffsetDateTime>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00+00:20"), actual.result());
  }

  @Test
  public void testOffsetDateTimeWithDefault()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseOffsetDateTimeWithDefault(
      properties,
      "OffsetDateTime",
      OffsetDateTime.parse("2000-01-01T00:00:00+00:20"));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<OffsetDateTime>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00+00:20"), actual.result());
  }

  @Test
  public void testOffsetDateTimeWithDefaultPresent()
  {
    final var properties = new Properties();
    properties.setProperty("OffsetDateTime", "2000-01-01T00:00:00+00:20");

    final var results = JPropertyParsing.parseOffsetDateTimeWithDefault(
      properties,
      "OffsetDateTime",
      OffsetDateTime.parse("2000-01-01T00:00:00+00:25"));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<OffsetDateTime>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(OffsetDateTime.parse("2000-01-01T00:00:00+00:20"), actual.result());
  }

  @Test
  public void testBadOffsetDateTime()
  {
    final var properties = new Properties();
    properties.setProperty("OffsetDateTime", " not a OffsetDateTime");

    final var results = JPropertyParsing.parseOffsetDateTime(properties, "OffsetDateTime");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<OffsetDateTime>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(DateTimeParseException.class, actual.exception().getClass());
  }

  @Test
  public void testBoolean()
  {
    final var properties = new Properties();
    properties.setProperty("Boolean", "true");

    final var results = JPropertyParsing.parseBoolean(properties, "Boolean");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<Boolean>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(Boolean.valueOf("true"), actual.result());
  }

  @Test
  public void testBooleanWithDefault()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseBooleanWithDefault(properties, "Boolean", true);
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<Boolean>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(Boolean.valueOf("true"), actual.result());
  }

  @Test
  public void testBooleanWithDefaultPresent()
  {
    final var properties = new Properties();
    properties.setProperty("Boolean", "false");

    final var results = JPropertyParsing.parseBooleanWithDefault(
      properties,
      "Boolean",
      true);
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<Boolean>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(Boolean.valueOf(false), actual.result());
  }

  @Test
  public void testBadBoolean()
  {
    final var properties = new Properties();
    properties.setProperty("Boolean", " not a Boolean");

    final var results = JPropertyParsing.parseBoolean(properties, "Boolean");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<Boolean>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(JPropertyIncorrectType.class, actual.exception().getClass());
  }

  @Test
  public void testBigInteger()
  {
    final var properties = new Properties();
    properties.setProperty("BigInteger", "23");

    final var results = JPropertyParsing.parseBigInteger(properties, "BigInteger");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<BigInteger>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(BigInteger.valueOf(23), actual.result());
  }

  @Test
  public void testBigIntegerWithDefault()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseBigIntegerWithDefault(
      properties,
      "BigInteger",
      BigInteger.valueOf(23));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<BigInteger>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(BigInteger.valueOf(23), actual.result());
  }

  @Test
  public void testBigIntegerWithDefaultPresent()
  {
    final var properties = new Properties();
    properties.setProperty("BigInteger", "24");

    final var results = JPropertyParsing.parseBigIntegerWithDefault(
      properties,
      "BigInteger",
      BigInteger.valueOf(23));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<BigInteger>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(BigInteger.valueOf(24), actual.result());
  }

  @Test
  public void testBadBigInteger()
  {
    final var properties = new Properties();
    properties.setProperty("BigInteger", " not a BigInteger");

    final var results = JPropertyParsing.parseBigInteger(properties, "BigInteger");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<BigInteger>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(NumberFormatException.class, actual.exception().getClass());
  }

  @Test
  public void testBigDecimal()
  {
    final var properties = new Properties();
    properties.setProperty("BigDecimal", "23");

    final var results = JPropertyParsing.parseBigDecimal(properties, "BigDecimal");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<BigDecimal>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(BigDecimal.valueOf(23L), actual.result());
  }

  @Test
  public void testBigDecimalWithDefault()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseBigDecimalWithDefault(
      properties,
      "BigDecimal",
      BigDecimal.valueOf(23));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<BigDecimal>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(BigDecimal.valueOf(23), actual.result());
  }

  @Test
  public void testBigDecimalWithDefaultPresent()
  {
    final var properties = new Properties();
    properties.setProperty("BigDecimal", "24");

    final var results = JPropertyParsing.parseBigDecimalWithDefault(
      properties,
      "BigDecimal",
      BigDecimal.valueOf(23));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<BigDecimal>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(BigDecimal.valueOf(24), actual.result());
  }

  @Test
  public void testBadBigDecimal()
  {
    final var properties = new Properties();
    properties.setProperty("BigDecimal", " not a BigDecimal");

    final var results = JPropertyParsing.parseBigDecimal(properties, "BigDecimal");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<BigDecimal>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(NumberFormatException.class, actual.exception().getClass());
  }

  @Test
  public void testAny()
  {
    final var properties = new Properties();
    properties.setProperty("Any", "14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8");

    final var results = JPropertyParsing.parseAny(
      properties,
      "Any",
      "a UUID",
      (key, value) -> UUID.fromString(value));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<UUID>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(UUID.fromString("14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8"), actual.result());
  }

  @Test
  public void testAnyWithDefault()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseAnyWithDefault(
      properties,
      "Any",
      "a UUID",
      UUID.fromString("14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8"),
      (key, value) -> UUID.fromString(value));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<UUID>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(UUID.fromString("14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8"), actual.result());
  }

  @Test
  public void testAnyOptional()
  {
    final var properties = new Properties();
    properties.setProperty("Any", "14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8");

    final var results = JPropertyParsing.parseAnyOptional(
      properties,
      "Any",
      "a UUID",
      (key, value) -> UUID.fromString(value));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<Optional<UUID>>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(
      Optional.of(UUID.fromString("14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8")),
      actual.result());
  }

  @Test
  public void testAnyOptionalPresent()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseAnyOptional(
      properties,
      "Any",
      "a UUID",
      (key, value) -> UUID.fromString(value));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<Optional<UUID>>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(Optional.empty(), actual.result());
  }

  @Test
  public void testBadAny()
  {
    final var properties = new Properties();
    properties.setProperty("Any", " not a Any");

    final var results = JPropertyParsing.parseAny(
      properties,
      "Any",
      "a UUID",
      (key, value) -> UUID.fromString(value));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<UUID>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(IllegalArgumentException.class, actual.exception().getClass());
  }

  @Test
  public void testUUID()
  {
    final var properties = new Properties();
    properties.setProperty("UUID", "14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8");

    final var results = JPropertyParsing.parseUUID(properties, "UUID");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<UUID>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(UUID.fromString("14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8"), actual.result());
  }

  @Test
  public void testBadUUID()
  {
    final var properties = new Properties();
    properties.setProperty("UUID", " not a UUID");

    final var results = JPropertyParsing.parseUUID(properties, "UUID");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<UUID>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(IllegalArgumentException.class, actual.exception().getClass());
  }

  @Test
  public void testUUIDWithDefault()
  {
    final var properties = new Properties();

    final var results = JPropertyParsing.parseUUIDWithDefault(
      properties,
      "UUID",
      UUID.fromString("14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8"));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<UUID>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(UUID.fromString("14f303b0-7a5d-49b6-b7b2-d4bc678e5fd8"), actual.result());
  }

  @Test
  public void testUUIDWithDefaultPresent()
  {
    final var properties = new Properties();
    properties.setProperty("UUID", "206d3bac-83fb-47d5-8f51-52d18abd8e80");

    final var results = JPropertyParsing.parseUUIDWithDefault(
      properties,
      "UUID",
      UUID.fromString("92cbd183-ab1b-49ae-a423-8307550ab531"));
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<UUID>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(UUID.fromString("206d3bac-83fb-47d5-8f51-52d18abd8e80"), actual.result());
  }

  @Test
  public void testStringWithDefaultPresent()
  {
    final var properties = new Properties();
    properties.setProperty("String", "false");

    final var results = JPropertyParsing.parseStringWithDefault(
      properties,
      "String",
      "true");
    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<String>) results;
    actual.warnings().forEach(error -> LOG.log(Level.INFO, error.toString()));
    actual.errors().forEach(error -> LOG.log(Level.INFO, error.toString()));
    Assert.assertEquals(String.valueOf(false), actual.result());
  }

  @Test
  public void testAllOf0()
  {
    final var properties = new Properties();
    properties.setProperty("String", "false");

    final JPropertyParseMonadType<List<Serializable>> results =
      JPropertyParsing.allOf(
        JPropertyParsing.successOf(BigInteger.valueOf(23L)),
        JPropertyParsing.warn("warning!"),
        JPropertyParsing.successOf(BigInteger.valueOf(24L)),
        JPropertyParsing.successOf(BigInteger.valueOf(25L))
      );

    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertySuccess.class));
    final var actual = (JPropertySuccess<List<Serializable>>) results;

    Assert.assertEquals(1, actual.warnings().size());
    Assert.assertEquals("warning!", actual.warnings().get(0).message());

    final var result0 = (BigInteger) actual.result().get(0);
    final var result1 = (Unit) actual.result().get(1);
    final var result2 = (BigInteger) actual.result().get(2);
    final var result3 = (BigInteger) actual.result().get(3);

    Assert.assertEquals(BigInteger.valueOf(23L), result0);
    Assert.assertEquals(Unit.UNIT, result1);
    Assert.assertEquals(BigInteger.valueOf(24L), result2);
    Assert.assertEquals(BigInteger.valueOf(25L), result3);
  }

  @Test
  public void testAllOf1()
  {
    final var properties = new Properties();
    properties.setProperty("String", "false");

    final JPropertyParseMonadType<List<Serializable>> results =
      JPropertyParsing.allOf(
        JPropertyParsing.successOf(BigInteger.valueOf(23L)),
        JPropertyParsing.warn("warning!"),
        JPropertyParsing.fail(new Exception("Error 0")),
        JPropertyParsing.fail(new Exception("Error 1"))
      );

    Assert.assertThat(results, IsInstanceOf.instanceOf(JPropertyFailure.class));
    final var actual = (JPropertyFailure<List<Serializable>>) results;

    Assert.assertEquals(1, actual.warnings().size());
    Assert.assertEquals("warning!", actual.warnings().get(0).message());

    Assert.assertEquals(2, actual.errors().size());
    Assert.assertEquals("Error 0", actual.errors().get(0).message());
    Assert.assertEquals("Error 1", actual.errors().get(1).message());
  }
}
