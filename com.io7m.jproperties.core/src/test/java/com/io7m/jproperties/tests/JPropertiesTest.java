/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
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

import com.io7m.jproperties.JProperties;
import com.io7m.jproperties.JPropertyIncorrectType;
import com.io7m.jproperties.JPropertyNonexistent;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Properties;

/**
 * Tests to ensure the property interface is working correctly.
 */

public final class JPropertiesTest
{
  @Test
  public void testGetBoolean()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final Properties properties = new Properties();
    properties.put("key", "true");
    Assert.assertTrue(true == (JProperties.getBoolean(properties, "key")));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetBooleanBadType()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final Properties properties = new Properties();
    properties.put("key", "Z");
    Assert.assertTrue(false == (JProperties.getBoolean(properties, "key")));
  }

  @Test
  public void testGetBooleanFalse()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final Properties properties = new Properties();
    properties.put("key", "false");
    Assert.assertTrue(false == (JProperties.getBoolean(properties, "key")));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void
  testGetBooleanMissing()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    JProperties.getBoolean(new Properties(), "key");
  }

  @Test
  public void testGetInteger()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final Properties properties = new Properties();
    properties.put("key", "23");
    Assert.assertEquals(
      BigInteger.valueOf(23),
      JProperties.getBigInteger(properties, "key"));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetIntegerBadType()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final Properties properties = new Properties();
    properties.put("key", "Z");
    Assert.assertEquals(
      BigInteger.valueOf(23),
      JProperties.getBigInteger(properties, "key"));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void
  testGetIntegerMissing()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    JProperties.getBigInteger(new Properties(), "key");
  }

  @Test
  public void testGetOptionalBoolean()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "true");
    Assert.assertTrue(true == (JProperties.getBooleanOptional(
      properties,
      "key",
      false)));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetOptionalBooleanBadType()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "Z");
    JProperties.getBooleanOptional(properties, "key", false);
  }

  @Test
  public void testGetOptionalBooleanFalse()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "false");
    Assert.assertTrue(false == (JProperties.getBooleanOptional(
      properties,
      "key",
      true)));
  }

  @Test
  public void testGetOptionalBooleanMissing()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    Assert.assertTrue(true == (JProperties.getBooleanOptional(
      new Properties(),
      "key",
      true)));
  }

  @SuppressWarnings("null")
  @Test
  public void testGetOptionalInteger()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "23");
    Assert.assertEquals(
      BigInteger.valueOf(23),
      JProperties.getBigIntegerOptional(
        properties,
        "key",
        BigInteger.valueOf(47)));
  }

  @SuppressWarnings("null")
  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetOptionalIntegerBadType()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "Z");
    JProperties.getBigIntegerOptional(
      properties,
      "key",
      BigInteger.valueOf(47));
  }

  @SuppressWarnings("null")
  @Test
  public void
  testGetOptionalIntegerMissing()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    Assert.assertEquals(
      BigInteger.valueOf(47),
      JProperties.getBigIntegerOptional(
        new Properties(),
        "key",
        BigInteger.valueOf(47)));
  }

  @SuppressWarnings("null")
  @Test
  public void testGetOptionalReal()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "23.0");
    Assert.assertEquals(
      new BigDecimal("23.0"),
      JProperties.getBigDecimalOptional(
        properties,
        "key",
        BigDecimal.valueOf(47)));
  }

  @SuppressWarnings("null")
  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetOptionalRealBadType()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "Z");
    JProperties.getBigDecimalOptional(
      properties,
      "key",
      BigDecimal.valueOf(23));
  }

  @SuppressWarnings("null")
  @Test
  public void
  testGetOptionalRealMissing()
    throws JPropertyIncorrectType,
    JPropertyNonexistent
  {
    Assert.assertEquals(
      BigDecimal.valueOf(23.0),
      JProperties.getBigDecimalOptional(
        new Properties(),
        "key",
        BigDecimal.valueOf(23.0)));
  }

  @Test
  public void testGetOptionalString()
    throws JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "old_value");
    Assert.assertEquals(
      "old_value",
      JProperties.getStringOptional(properties, "key", "value"));
  }

  @Test
  public void testGetOptionalStringMissing()
    throws JPropertyNonexistent
  {
    Assert.assertEquals(
      "value",
      JProperties.getStringOptional(new Properties(), "key", "value"));
  }

  @Test
  public void testGetReal()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final Properties properties = new Properties();
    properties.put("key", "23.0");
    Assert.assertEquals(
      BigDecimal.valueOf(23.0),
      JProperties.getBigDecimal(properties, "key"));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetRealBadType()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final Properties properties = new Properties();
    properties.put("key", "Z");
    Assert.assertEquals(
      BigDecimal.valueOf(23.0),
      JProperties.getBigDecimal(properties, "key"));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void
  testGetRealMissing()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    JProperties.getBigDecimal(new Properties(), "key");
  }

  @Test
  public void testGetString()
    throws JPropertyNonexistent
  {
    final Properties properties = new Properties();
    properties.put("key", "value");
    Assert
      .assertTrue("value".equals(JProperties.getString(properties, "key")));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void
  testGetStringMissing()
    throws JPropertyNonexistent
  {
    JProperties.getString(new Properties(), "key");
  }

  @Test
  public void testLoad()
    throws IOException,
    JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final File temp = File.createTempFile("JPropertiesTest", ".properties");
    final PrintWriter stream = new PrintWriter(new FileOutputStream(temp));
    stream.write("integer = 23");
    stream.flush();
    stream.close();

    final Properties properties = JProperties.fromFile(temp);
    Assert.assertEquals(
      BigInteger.valueOf(23),
      JProperties.getBigInteger(properties, "integer"));
  }

  @Test(expected = IOException.class)
  public void testLoadNonexistent()
    throws IOException
  {
    JProperties.fromFile(new File("nonexistent"));
  }
}
