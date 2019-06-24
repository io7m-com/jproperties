/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
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

package com.io7m.jproperties;

import com.io7m.junreachable.UnreachableCodeException;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Type-safe interface to {@link java.util.Properties}.
 */

public final class JProperties
{
  private JProperties()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Load properties from the given file.
   *
   * @param file The file.
   *
   * @return The loaded properties.
   *
   * @throws IOException On I/O errors.
   */

  public static Properties fromFile(
    final File file)
    throws IOException
  {
    Objects.requireNonNull(file, "File");

    try (var stream = Files.newInputStream(file.toPath())) {
      final var p = new Properties();
      p.load(stream);
      return p;
    }
  }

  /**
   * <p> Returns the real value associated with {@code key} in the
   * properties referenced by {@code properties}. </p>
   *
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as an real.
   *
   * @throws JPropertyNonexistent   If the key does not exist in the given properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot be parsed as a
   *                                real.
   */

  public static BigDecimal getBigDecimal(
    final Properties properties,
    final String key)
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final var text = getString(properties, key);
    return parseReal(key, text);
  }

  /**
   * <p> Returns the real value associated with {@code key} in the
   * properties referenced by {@code properties} if it exists, otherwise returns the given default
   * value. </p>
   *
   * @param other      The default value
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as an real.
   *
   * @throws JPropertyIncorrectType If the value associated with the key cannot be parsed as a
   *                                real.
   * @since 3.0.0
   */

  public static BigDecimal getBigDecimalWithDefault(
    final Properties properties,
    final String key,
    final BigDecimal other)
    throws JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");
    Objects.requireNonNull(other, "Default");

    final var text = properties.getProperty(key);
    if (text == null) {
      return other;
    }
    return parseReal(key, text);
  }

  /**
   * <p> Returns the integer value associated with {@code key} in the
   * properties referenced by {@code properties}. </p>
   *
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as an integer.
   *
   * @throws JPropertyNonexistent   If the key does not exist in the given properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot be parsed as an
   *                                integer.
   */

  public static BigInteger getBigInteger(
    final Properties properties,
    final String key)
    throws JPropertyNonexistent, JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final var text = getString(properties, key);
    return parseInteger(key, text);
  }

  /**
   * <p> Returns the integer value associated with {@code key} in the
   * properties referenced by {@code properties} if it exists, otherwise returns {@code other}.
   * </p>
   *
   * @param other      The default value
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as an integer.
   *
   * @throws JPropertyIncorrectType If the value associated with the key cannot be parsed as an
   *                                integer.
   * @since 3.0.0
   */

  public static BigInteger getBigIntegerWithDefault(
    final Properties properties,
    final String key,
    final BigInteger other)
    throws JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");
    Objects.requireNonNull(other, "Default");

    final var text = properties.getProperty(key);
    if (text == null) {
      return other;
    }
    return parseInteger(key, text);
  }

  /**
   * <p> Returns the boolean value associated with {@code key} in the
   * properties referenced by {@code properties}. </p> <p> A boolean value is syntactically the
   * strings "true" or "false", case insensitive. </p>
   *
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as a boolean.
   *
   * @throws JPropertyNonexistent   If the key does not exist in the given properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot be parsed as a
   *                                boolean.
   */

  public static boolean getBoolean(
    final Properties properties,
    final String key)
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final var text = getString(properties, key);
    return parseBoolean(key, text);
  }

  /**
   * <p> Returns the boolean value associated with {@code key} in the
   * properties referenced by {@code properties} if it exists, otherwise returns {@code other}.
   * </p>
   * <p> A boolean value is syntactically the strings "true" or "false", case insensitive. </p>
   *
   * @param other      The default value
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as a boolean.
   *
   * @throws JPropertyIncorrectType If the value associated with the key cannot be parsed as a
   *                                boolean.
   * @since 3.0.0
   */

  public static boolean getBooleanWithDefault(
    final Properties properties,
    final String key,
    final boolean other)
    throws
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final var text = properties.getProperty(key);
    if (text == null) {
      return other;
    }
    return parseBoolean(key, text);
  }

  /**
   * <p> Returns the string value associated with {@code key} in the
   * properties referenced by {@code props} </p>
   *
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the given key
   *
   * @throws JPropertyNonexistent If the given key is not present in the given properties.
   */

  public static String getString(
    final Properties properties,
    final String key)
    throws JPropertyNonexistent
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final var value = properties.getProperty(key);
    if (value == null) {
      throw notFound(key);
    }
    return value;
  }

  /**
   * <p> Returns the string value associated with {@code key} in the
   * properties referenced by {@code props} </p>
   *
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the given key
   *
   * @since 3.0.0
   */

  public static Optional<String> getStringOptional(
    final Properties properties,
    final String key)
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final var value = properties.getProperty(key);
    if (value == null) {
      return Optional.empty();
    }
    return Optional.of(value);
  }

  /**
   * <p> Returns the string value associated with {@code key} in the
   * properties referenced by {@code props} if it exists, otherwise returns {@code other}. </p>
   *
   * @param other      The default value
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the given key
   *
   * @since 3.0.0
   */

  public static String getStringWithDefault(
    final Properties properties,
    final String key,
    final String other)
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");
    Objects.requireNonNull(other, "Default value");

    final var value = properties.getProperty(key);
    if (value == null) {
      return other;
    }
    return value;
  }

  private static JPropertyIncorrectType incorrectType(
    final Exception cause,
    final String key,
    final String value,
    final String type)
  {
    final var message = new StringBuilder();
    message.append("Value for key ");
    message.append(key);
    message.append(" (");
    message.append(value);
    message.append(") cannot be parsed as type ");
    message.append(type);
    final var s = message.toString();
    assert s != null;
    return new JPropertyIncorrectType(s, cause);
  }

  private static JPropertyNonexistent notFound(
    final String key)
  {
    final var message = new StringBuilder();
    message.append("Key not found in properties: ");
    message.append(key);
    final var s = message.toString();
    assert s != null;
    return new JPropertyNonexistent(s);
  }

  private static boolean parseBoolean(
    final String key,
    final String text)
    throws JPropertyIncorrectType
  {
    if ("true".equalsIgnoreCase(text)) {
      return true;
    }
    if ("false".equalsIgnoreCase(text)) {
      return false;
    }
    throw incorrectType(null, key, text, "Boolean");
  }

  private static BigInteger parseInteger(
    final String key,
    final String text)
    throws JPropertyIncorrectType
  {
    try {
      return new BigInteger(text);
    } catch (final NumberFormatException e) {
      throw incorrectType(e, key, text, "Integer");
    }
  }

  private static BigDecimal parseReal(
    final String key,
    final String text)
    throws JPropertyIncorrectType
  {
    try {
      return new BigDecimal(text);
    } catch (final NumberFormatException e) {
      throw incorrectType(e, key, text, "Real");
    }
  }
}
