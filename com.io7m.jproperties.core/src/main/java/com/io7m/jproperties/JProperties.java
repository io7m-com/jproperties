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
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
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

    final FileInputStream stream = new FileInputStream(file);
    try {
      final Properties p = new Properties();
      p.load(stream);
      return p;
    } finally {
      stream.close();
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
   * @throws JPropertyNonexistent   If the key does not exist in the given
   *                                properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot
   *                                be parsed as a real.
   */

  public static BigDecimal getBigDecimal(
    final Properties properties,
    final String key)
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final String text = JProperties.getString(properties, key);
    return JProperties.parseReal(key, text);
  }

  /**
   * <p> Returns the real value associated with {@code key} in the
   * properties referenced by {@code properties} if it exists, otherwise
   * returns the given default value. </p>
   *
   * @param other      The default value
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as an real.
   *
   * @throws JPropertyNonexistent   If the key does not exist in the given
   *                                properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot
   *                                be parsed as a real.
   */

  public static BigDecimal getBigDecimalOptional(
    final Properties properties,
    final String key,
    final BigDecimal other)
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");
    Objects.requireNonNull(other, "Default");

    final String text = properties.getProperty(key);
    if (text == null) {
      return other;
    }
    return JProperties.parseReal(key, text);
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
   * @throws JPropertyNonexistent   If the key does not exist in the given
   *                                properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot
   *                                be parsed as an integer.
   */

  public static BigInteger getBigInteger(
    final Properties properties,
    final String key)
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final String text = JProperties.getString(properties, key);
    return JProperties.parseInteger(key, text);
  }

  /**
   * <p> Returns the integer value associated with {@code key} in the
   * properties referenced by {@code properties} if it exists, otherwise
   * returns {@code other}. </p>
   *
   * @param other      The default value
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as an integer.
   *
   * @throws JPropertyNonexistent   If the key does not exist in the given
   *                                properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot
   *                                be parsed as an integer.
   */

  public static BigInteger getBigIntegerOptional(
    final Properties properties,
    final String key,
    final BigInteger other)
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");
    Objects.requireNonNull(other, "Default");

    final String text = properties.getProperty(key);
    if (text == null) {
      return other;
    }
    return JProperties.parseInteger(key, text);
  }

  /**
   * <p> Returns the boolean value associated with {@code key} in the
   * properties referenced by {@code properties}. </p> <p> A boolean value
   * is syntactically the strings "true" or "false", case insensitive. </p>
   *
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as a boolean.
   *
   * @throws JPropertyNonexistent   If the key does not exist in the given
   *                                properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot
   *                                be parsed as a boolean.
   */

  public static boolean getBoolean(
    final Properties properties,
    final String key)
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final String text = JProperties.getString(properties, key);
    return JProperties.parseBoolean(key, text);
  }

  /**
   * <p> Returns the boolean value associated with {@code key} in the
   * properties referenced by {@code properties} if it exists, otherwise
   * returns {@code other}. </p> <p> A boolean value is syntactically the
   * strings "true" or "false", case insensitive. </p>
   *
   * @param other      The default value
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the key, parsed as a boolean.
   *
   * @throws JPropertyNonexistent   If the key does not exist in the given
   *                                properties.
   * @throws JPropertyIncorrectType If the value associated with the key cannot
   *                                be parsed as a boolean.
   */

  public static boolean getBooleanOptional(
    final Properties properties,
    final String key,
    final boolean other)
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final String text = properties.getProperty(key);
    if (text == null) {
      return other;
    }
    return JProperties.parseBoolean(key, text);
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
   * @throws JPropertyNonexistent If the given key is not present in the given
   *                              properties.
   */

  public static String getString(
    final Properties properties,
    final String key)
    throws JPropertyNonexistent
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");

    final String value = properties.getProperty(key);
    if (value == null) {
      throw JProperties.notFound(key);
    }
    return value;
  }

  /**
   * <p> Returns the string value associated with {@code key} in the
   * properties referenced by {@code props} if it exists, otherwise returns
   * {@code other}. </p>
   *
   * @param other      The default value
   * @param properties The loaded properties.
   * @param key        The requested key.
   *
   * @return The value associated with the given key
   *
   * @throws JPropertyNonexistent If the given key is not present in the given
   *                              properties.
   */

  public static String getStringOptional(
    final Properties properties,
    final String key,
    final String other)
    throws JPropertyNonexistent
  {
    Objects.requireNonNull(properties, "Properties");
    Objects.requireNonNull(key, "Key");
    Objects.requireNonNull(other, "Default value");

    final String value = properties.getProperty(key);
    if (value == null) {
      return other;
    }
    return value;
  }

  private static JPropertyIncorrectType incorrectType(
    final String key,
    final String value,
    final String type)
  {
    final StringBuilder message = new StringBuilder();
    message.append("Value for key ");
    message.append(key);
    message.append(" (");
    message.append(value);
    message.append(") cannot be parsed as type ");
    message.append(type);
    final String s = message.toString();
    assert s != null;
    return new JPropertyIncorrectType(s);
  }

  private static JPropertyNonexistent notFound(
    final String key)
  {
    final StringBuilder message = new StringBuilder();
    message.append("Key not found in properties: ");
    message.append(key);
    final String s = message.toString();
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
    throw JProperties.incorrectType(key, text, "Boolean");
  }

  private static BigInteger parseInteger(
    final String key,
    final String text)
    throws JPropertyIncorrectType
  {
    try {
      return new BigInteger(text);
    } catch (final NumberFormatException e) {
      throw JProperties.incorrectType(key, text, "Integer");
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
      throw JProperties.incorrectType(key, text, "Real");
    }
  }
}
