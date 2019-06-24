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

package com.io7m.jproperties.monad;

import com.io7m.jproperties.JProperties;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.io7m.jproperties.monad.JPropertyParseMonadType.Unit;

/**
 * Parser monad functions.
 *
 * @since 3.0.0
 */

public final class JPropertyParsing
{
  private JPropertyParsing()
  {

  }

  private static <T> JPropertyParseMonadType<T> parseFromText(
    final String key,
    final String text,
    final String format,
    final ParseOfTextType<T> parse)
  {
    try {
      return JPropertySuccess.<T>builder()
        .setResult(parse.apply(text))
        .build();
    } catch (final Exception e) {
      return JPropertyFailure.<T>builder()
        .setException(e)
        .addErrors(couldNotParseAs(key, text, format, e.getMessage()))
        .build();
    }
  }

  private static JPropertyError couldNotParseAs(
    final String key,
    final String text,
    final String format,
    final String message)
  {
    return JPropertyError.of(
      new StringBuilder(128)
        .append("Key '")
        .append(key)
        .append("' with value '")
        .append(text)
        .append("' could not be parsed as ")
        .append(format)
        .append(": ")
        .append(message)
        .toString());
  }

  private static <T> JPropertyParseMonadType<T> parseT(
    final ParseType<T> supplier)
  {
    try {
      final var result = supplier.apply();
      return JPropertySuccess.<T>builder()
        .setResult(result)
        .build();
    } catch (final Exception ex) {
      return JPropertyFailure.<T>builder()
        .setException(ex)
        .addErrors(JPropertyError.of(ex.getMessage()))
        .build();
    }
  }

  /**
   * Parse anything. The given {@code parser} function is used to parse the given text value, and
   * any exception raised will be converted into a parse failure result and a useful error message
   * constructed. The given {@code format} names the target format and should be worded similarly to
   * "a URI" or "an ISO time duration".
   *
   * @param properties The properties from which to extract values
   * @param key        The property key
   * @param format     The name of the format used for error messages
   * @param parser     The parse function
   * @param <T>        The type of returned values
   *
   * @return A parse result
   */

  public static <T> JPropertyParseMonadType<T> parseAny(
    final Properties properties,
    final String key,
    final String format,
    final ParseFunctionType<T> parser)
  {
    return parseString(properties, key)
      .flatMap(text -> parseFromText(key, text, format, s -> parser.parse(key, s)));
  }

  /**
   * Parse anything. The given {@code parser} function is used to parse the given text value, and
   * any exception raised will be converted into a parse failure result and a useful error message
   * constructed. The given {@code format} names the target format and should be worded similarly to
   * "a URI" or "an ISO time duration".
   *
   * @param properties The properties from which to extract values
   * @param key        The property key
   * @param format     The name of the format used for error messages
   * @param parser     The parse function
   * @param <T>        The type of returned values
   *
   * @return A parse result
   */

  public static <T> JPropertyParseMonadType<Optional<T>> parseAnyOptional(
    final Properties properties,
    final String key,
    final String format,
    final ParseFunctionType<T> parser)
  {
    return parseStringOptional(properties, key).flatMap(option -> {
      if (option.isPresent()) {
        return parseFromText(key, option.get(), format, ignored -> parser.parse(key, ignored))
          .flatMap(result -> successOf(Optional.of(result)));
      }
      return successOf(Optional.empty());
    });
  }

  /**
   * Parse anything. The given {@code parser} function is used to parse the given text value, and
   * any exception raised will be converted into a parse failure result and a useful error message
   * constructed. The given {@code format} names the target format and should be worded similarly to
   * "a URI" or "an ISO time duration".
   *
   * @param properties   The properties from which to extract values
   * @param key          The property key
   * @param format       The name of the format used for error messages
   * @param defaultValue The default value
   * @param parser       The parse function
   * @param <T>          The type of returned values
   *
   * @return A parse result
   */

  public static <T> JPropertyParseMonadType<T> parseAnyWithDefault(
    final Properties properties,
    final String key,
    final String format,
    final T defaultValue,
    final ParseFunctionType<T> parser)
  {
    return parseAnyOptional(properties, key, format, parser)
      .flatMap(option -> successOf(option.orElse(defaultValue)));
  }

  /**
   * Parse a {@code BigInteger}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<BigInteger> parseBigInteger(
    final Properties properties,
    final String key)
  {
    return parseAny(properties, key, "a BigInteger", (k, value) -> new BigInteger(value));
  }

  /**
   * Parse a {@code BigInteger} returning the default value if the key does not exist.
   *
   * @param properties   The properties
   * @param key          The key
   * @param defaultValue The default value
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<BigInteger> parseBigIntegerWithDefault(
    final Properties properties,
    final String key,
    final BigInteger defaultValue)
  {
    return parseAnyWithDefault(
      properties,
      key,
      "a BigInteger",
      defaultValue,
      (k, value) -> new BigInteger(value));
  }

  /**
   * Parse a {@code Boolean}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<Boolean> parseBoolean(
    final Properties properties,
    final String key)
  {
    return parseT(() -> Boolean.valueOf(
      JProperties.getBoolean(properties, key)));
  }

  /**
   * Parse a {@code Boolean} returning the default value if the key does not exist.
   *
   * @param properties   The properties
   * @param key          The key
   * @param defaultValue The default value
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<Boolean> parseBooleanWithDefault(
    final Properties properties,
    final String key,
    final boolean defaultValue)
  {
    return parseAnyWithDefault(
      properties,
      key,
      "a Boolean",
      Boolean.valueOf(defaultValue),
      (k, value) -> Boolean.valueOf(value));
  }

  /**
   * Parse a {@code String}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<String> parseString(
    final Properties properties,
    final String key)
  {
    return parseT(() -> JProperties.getString(properties, key));
  }

  /**
   * Parse a {@code String}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<Optional<String>> parseStringOptional(
    final Properties properties,
    final String key)
  {
    return parseT(() -> JProperties.getStringOptional(properties, key));
  }

  /**
   * Parse a {@code String} returning the default value if the key does not exist.
   *
   * @param properties   The properties
   * @param key          The key
   * @param defaultValue The default value
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<String> parseStringWithDefault(
    final Properties properties,
    final String key,
    final String defaultValue)
  {
    return parseT(() -> JProperties.getStringWithDefault(properties, key, defaultValue));
  }

  /**
   * Parse a {@code BigDecimal}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<BigDecimal> parseBigDecimal(
    final Properties properties,
    final String key)
  {
    return parseAny(properties, key, "a BigDecimal", (k, value) -> new BigDecimal(value));
  }

  /**
   * Parse a {@code BigDecimal} returning the default value if the key does not exist.
   *
   * @param properties   The properties
   * @param key          The key
   * @param defaultValue The default value
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<BigDecimal> parseBigDecimalWithDefault(
    final Properties properties,
    final String key,
    final BigDecimal defaultValue)
  {
    return parseAnyWithDefault(
      properties,
      key,
      "a BigDecimal",
      defaultValue,
      (k, value) -> new BigDecimal(value));
  }

  /**
   * Parse a {@code URI}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<URI> parseURI(
    final Properties properties,
    final String key)
  {
    return parseAny(properties, key, "a URI", (k, value) -> new URI(value));
  }

  /**
   * Parse a {@code URI} returning the default value if the key does not exist.
   *
   * @param properties   The properties
   * @param key          The key
   * @param defaultValue The default value
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<URI> parseURIWithDefault(
    final Properties properties,
    final String key,
    final URI defaultValue)
  {
    return parseAnyWithDefault(
      properties,
      key,
      "a URI",
      defaultValue,
      (k, value) -> new URI(value));
  }

  /**
   * Parse a {@code UUID}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<UUID> parseUUID(
    final Properties properties,
    final String key)
  {
    return parseAny(properties, key, "a UUID", (k, value) -> UUID.fromString(value));
  }

  /**
   * Parse a {@code UUID} returning the default value if the key does not exist.
   *
   * @param properties   The properties
   * @param key          The key
   * @param defaultValue The default value
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<UUID> parseUUIDWithDefault(
    final Properties properties,
    final String key,
    final UUID defaultValue)
  {
    return parseAnyWithDefault(
      properties,
      key,
      "a UUID",
      defaultValue,
      (k, value) -> UUID.fromString(value));
  }

  /**
   * Parse a {@code Duration}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<Duration> parseDuration(
    final Properties properties,
    final String key)
  {
    return parseAny(properties, key, "an ISO duration", (k, value) -> Duration.parse(value));
  }

  /**
   * Parse a {@code Duration} returning the default value if the key does not exist.
   *
   * @param properties   The properties
   * @param key          The key
   * @param defaultValue The default value
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<Duration> parseDurationWithDefault(
    final Properties properties,
    final String key,
    final Duration defaultValue)
  {
    return parseAnyWithDefault(
      properties,
      key,
      "an ISO duration",
      defaultValue,
      (k, value) -> Duration.parse(value));
  }

  /**
   * Parse a {@code OffsetDateTime}.
   *
   * @param properties The properties
   * @param key        The key
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<OffsetDateTime> parseOffsetDateTime(
    final Properties properties,
    final String key)
  {
    return parseAny(
      properties,
      key,
      "an ISO duration",
      (k, value) -> OffsetDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value)));

  }

  /**
   * Parse a {@code OffsetDateTime} returning the default value if the key does not exist.
   *
   * @param properties   The properties
   * @param key          The key
   * @param defaultValue The default value
   *
   * @return A parse result
   */

  public static JPropertyParseMonadType<OffsetDateTime> parseOffsetDateTimeWithDefault(
    final Properties properties,
    final String key,
    final OffsetDateTime defaultValue)
  {
    return parseAnyWithDefault(
      properties,
      key,
      "an ISO duration",
      defaultValue,
      (k, value) -> OffsetDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value)));
  }

  /**
   * Trivially succeed.
   *
   * @param result The returned value
   * @param <T>    The type of returned values
   *
   * @return A parse result
   */

  public static <T> JPropertyParseMonadType<T> successOf(
    final T result)
  {
    Objects.requireNonNull(result, "result");
    return JPropertySuccess.<T>builder()
      .setResult(result)
      .build();
  }

  /**
   * Publish a warning.
   *
   * @param message The warning message
   *
   * @return A warning result
   */

  public static JPropertyParseMonadType<Unit> warn(
    final String message)
  {
    Objects.requireNonNull(message, "message");
    return JPropertySuccess.<Unit>builder()
      .addWarnings(JPropertyWarning.of(message))
      .setResult(Unit.UNIT)
      .build();
  }

  /**
   * Publish a warning.
   *
   * @param key     The key associated with the warning
   * @param message The warning message
   *
   * @return A warning result
   */

  public static JPropertyParseMonadType<Unit> warn(
    final String key,
    final String message)
  {
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(message, "message");
    return JPropertySuccess.<Unit>builder()
      .addWarnings(JPropertyWarning.of(
        new StringBuilder(128)
          .append("Key '")
          .append(key)
          .append("': ")
          .append(message)
          .toString()))
      .setResult(Unit.UNIT)
      .build();
  }

  /**
   * Publish an error.
   *
   * @param e The exception
   *
   * @return A failure result
   */

  public static JPropertyParseMonadType<Unit> fail(
    final Exception e)
  {
    return JPropertyFailure.<Unit>builder()
      .addErrors(JPropertyError.of(e.getMessage()))
      .setException(e)
      .build();
  }

  /**
   * Evaluate all of the given operations and return a list of the results (including the union of
   * all of the warnings) if all of them succeeded. Otherwise, return a failure result that
   * accumulates all of the warnings and errors.
   *
   * @param operations The list of parse operations
   * @param <T>        The common type of the results
   *
   * @return The result of evaluating all of the operations
   */

  @SuppressWarnings("unchecked")
  public static <T> JPropertyParseMonadType<List<T>> allOf(
    final List<JPropertyParseMonadType<? extends T>> operations)
  {
    Objects.requireNonNull(operations, "operations");

    final var successes =
      operations.stream()
        .filter(p -> p instanceof JPropertySuccess)
        .map(p -> (JPropertySuccess<? extends T>) p)
        .collect(Collectors.toList());

    final var failures =
      operations.stream()
        .filter(p -> p instanceof JPropertyFailure)
        .map(p -> (JPropertyFailure<? extends T>) p)
        .collect(Collectors.toList());

    if (failures.isEmpty()) {
      final var warnings =
        successes.stream()
          .flatMap(p -> p.warnings().stream())
          .collect(Collectors.toList());
      final var results =
        successes.stream()
          .map(JPropertySuccess::result)
          .map(p -> (T) p)
          .collect(Collectors.toList());

      return JPropertySuccess.<List<T>>builder()
        .setWarnings(warnings)
        .setResult(results)
        .build();
    }

    final var warnings =
      operations.stream()
        .flatMap(p -> p.warnings().stream())
        .collect(Collectors.toList());

    final var errors =
      operations.stream()
        .flatMap(p -> p.errors().stream())
        .collect(Collectors.toList());

    return JPropertyFailure.<List<T>>builder()
      .setWarnings(warnings)
      .setErrors(errors)
      .setException(new Exception("At least one operation failed!"))
      .build();
  }

  /**
   * Evaluate all of the given operations and return a list of the results (including the union of
   * all of the warnings) if all of them succeeded. Otherwise, return a failure result that
   * accumulates all of the warnings and errors.
   *
   * @param operations The list of parse operations
   * @param <T>        The common type of the results
   *
   * @return The result of evaluating all of the operations
   */

  public static <T> JPropertyParseMonadType<List<T>> allOf(
    final JPropertyParseMonadType<? extends T>... operations)
  {
    Objects.requireNonNull(operations, "operations");
    return allOf(List.of(operations));
  }

  private interface ParseType<T>
  {
    T apply()
      throws Exception;
  }

  private interface ParseOfTextType<T>
  {
    T apply(String text)
      throws Exception;
  }

  /**
   * A parse function.
   *
   * @param <T> The type of returned values.
   */

  public interface ParseFunctionType<T>
  {
    /**
     * Parse {@code value} associated with {@code key}, raising an exception if this cannot occur
     * for any reason.
     *
     * @param key   The name of the key
     * @param value The text of the value
     *
     * @return A parsed value
     *
     * @throws Exception On errors
     */

    T parse(
      String key,
      String value)
      throws Exception;
  }
}
