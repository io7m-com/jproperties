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
import com.io7m.junreachable.UnreachableCodeException;
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
  public void testUnreachable()
    throws Exception
  {
    final var cons =
      JProperties.class.getDeclaredConstructor();
    cons.setAccessible(true);

    try {
      cons.newInstance();
      Assert.fail();
    } catch (final Exception e) {
      Assert.assertEquals(UnreachableCodeException.class, e.getCause().getClass());
    }
  }

  @Test
  public void testGetBoolean()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final var properties = new Properties();
    properties.put("key", "true");
    Assert.assertTrue(true == (JProperties.getBoolean(properties, "key")));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetBooleanBadType()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    Assert.assertTrue(false == (JProperties.getBoolean(properties, "key")));
  }

  @Test
  public void testGetBooleanFalse()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final var properties = new Properties();
    properties.put("key", "false");
    Assert.assertTrue(false == (JProperties.getBoolean(properties, "key")));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void
  testGetBooleanMissing()
    throws Exception
  {
    JProperties.getBoolean(new Properties(), "key");
  }

  @Test
  public void testGetInteger()
    throws Exception
  {
    final var properties = new Properties();
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
    final var properties = new Properties();
    properties.put("key", "Z");
    Assert.assertEquals(
      BigInteger.valueOf(23),
      JProperties.getBigInteger(properties, "key"));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void
  testGetIntegerMissing()
    throws Exception
  {
    JProperties.getBigInteger(new Properties(), "key");
  }

  @Test
  public void testGetOptionalBoolean()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "true");
    Assert.assertTrue(true == (JProperties.getBooleanWithDefault(
      properties,
      "key",
      false)));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetOptionalBooleanBadType()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    JProperties.getBooleanWithDefault(properties, "key", false);
  }

  @Test
  public void testGetOptionalBooleanFalse()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "false");
    Assert.assertTrue(false == (JProperties.getBooleanWithDefault(
      properties,
      "key",
      true)));
  }

  @Test
  public void testGetOptionalBooleanMissing()
    throws Exception
  {
    Assert.assertTrue(true == (JProperties.getBooleanWithDefault(
      new Properties(),
      "key",
      true)));
  }

  @Test
  public void testGetOptionalInteger()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23");
    Assert.assertEquals(
      BigInteger.valueOf(23),
      JProperties.getBigIntegerWithDefault(
        properties,
        "key",
        BigInteger.valueOf(47)));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetOptionalIntegerBadType()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    JProperties.getBigIntegerWithDefault(
      properties,
      "key",
      BigInteger.valueOf(47));
  }

  @Test
  public void
  testGetOptionalIntegerMissing()
    throws Exception
  {
    Assert.assertEquals(
      BigInteger.valueOf(47),
      JProperties.getBigIntegerWithDefault(
        new Properties(),
        "key",
        BigInteger.valueOf(47)));
  }


  @Test
  public void testGetOptionalReal()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23.0");
    Assert.assertEquals(
      new BigDecimal("23.0"),
      JProperties.getBigDecimalWithDefault(
        properties,
        "key",
        BigDecimal.valueOf(47)));
  }


  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetOptionalRealBadType()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    JProperties.getBigDecimalWithDefault(
      properties,
      "key",
      BigDecimal.valueOf(23));
  }

  @Test
  public void
  testGetOptionalRealMissing()
    throws Exception
  {
    Assert.assertEquals(
      BigDecimal.valueOf(23.0),
      JProperties.getBigDecimalWithDefault(
        new Properties(),
        "key",
        BigDecimal.valueOf(23.0)));
  }

  @Test
  public void testGetOptionalString()
    throws JPropertyNonexistent
  {
    final var properties = new Properties();
    properties.put("key", "old_value");
    Assert.assertEquals(
      "old_value",
      JProperties.getStringWithDefault(properties, "key", "value"));
  }

  @Test
  public void testGetOptionalStringMissing()
    throws JPropertyNonexistent
  {
    Assert.assertEquals(
      "value",
      JProperties.getStringWithDefault(new Properties(), "key", "value"));
  }

  @Test
  public void testGetReal()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23.0");
    Assert.assertEquals(
      BigDecimal.valueOf(23.0),
      JProperties.getBigDecimal(properties, "key"));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void
  testGetRealBadType()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    Assert.assertEquals(
      BigDecimal.valueOf(23.0),
      JProperties.getBigDecimal(properties, "key"));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void
  testGetRealMissing()
    throws Exception
  {
    JProperties.getBigDecimal(new Properties(), "key");
  }

  @Test
  public void testGetString()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "value");
    Assert
      .assertTrue("value".equals(JProperties.getString(properties, "key")));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void
  testGetStringMissing()
    throws Exception
  {
    JProperties.getString(new Properties(), "key");
  }

  @Test
  public void testLoad()
    throws Exception
  {
    final var temp = File.createTempFile("JPropertiesTest", ".properties");
    final var stream = new PrintWriter(new FileOutputStream(temp));
    stream.write("integer = 23");
    stream.flush();
    stream.close();

    final var properties = JProperties.fromFile(temp);
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

  @Test
  public void testGetInt()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23");
    Assert.assertEquals(
      23,
      JProperties.getInteger(properties, "key"));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetIntOutOfRange()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "232323232323232323");
    JProperties.getInteger(properties, "key");
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetIntBadType()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    Assert.assertEquals(
      23,
      JProperties.getInteger(properties, "key"));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void testGetIntMissing()
    throws Exception
  {
    JProperties.getInteger(new Properties(), "key");
  }

  @Test
  public void testGetLong()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23");
    Assert.assertEquals(
      23,
      JProperties.getLong(properties, "key"));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetLongOutOfRange()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "232323232323232323232323232323232323232323");
    JProperties.getLong(properties, "key");
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetLongBadType()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    Assert.assertEquals(
      23,
      JProperties.getLong(properties, "key"));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void testGetLongMissing()
    throws Exception
  {
    JProperties.getLong(new Properties(), "key");
  }


  @Test
  public void testGetDouble()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23.0");
    Assert.assertEquals(
      23.0,
      JProperties.getDouble(properties, "key"),
      0.0);
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetDoubleBadType()
    throws JPropertyNonexistent,
    JPropertyIncorrectType
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    Assert.assertEquals(
      23.0,
      JProperties.getDouble(properties, "key"));
  }

  @Test(expected = JPropertyNonexistent.class)
  public void testGetDoubleMissing()
    throws Exception
  {
    JProperties.getDouble(new Properties(), "key");
  }

  @Test
  public void testGetOptionalInt()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23");
    Assert.assertEquals(
      23,
      JProperties.getIntegerWithDefault(
        properties,
        "key",
        Integer.valueOf(47)));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetOptionalIntBadType()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    JProperties.getIntegerWithDefault(
      properties,
      "key",
      47);
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetOptionalIntOutOfRange()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "2323232323232323232323232323232323");
    JProperties.getIntegerWithDefault(
      properties,
      "key",
      47);
  }

  @Test
  public void testGetOptionalIntMissing()
    throws Exception
  {
    Assert.assertEquals(
      47,
      JProperties.getIntegerWithDefault(
        new Properties(),
        "key",
        Integer.valueOf(47)));
  }

  @Test
  public void testGetOptionalLong()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23");
    Assert.assertEquals(
      23L,
      JProperties.getLongWithDefault(
        properties,
        "key",
        Long.valueOf(47)));
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetOptionalLongBadType()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    JProperties.getLongWithDefault(
      properties,
      "key",
      47);
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetOptionalLongOutOfRange()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "232323232323232323232323232323232323232323");
    JProperties.getLongWithDefault(
      properties,
      "key",
      47);
  }

  @Test
  public void testGetOptionalLongMissing()
    throws Exception
  {
    Assert.assertEquals(
      47,
      JProperties.getLongWithDefault(
        new Properties(),
        "key",
        Long.valueOf(47)));
  }

  @Test
  public void testGetOptionalDouble()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "23");
    Assert.assertEquals(
      23L,
      JProperties.getDoubleWithDefault(
        properties,
        "key",
        Double.valueOf(47.0)),
      0.0);
  }

  @Test(expected = JPropertyIncorrectType.class)
  public void testGetOptionalDoubleBadType()
    throws Exception
  {
    final var properties = new Properties();
    properties.put("key", "Z");
    JProperties.getDoubleWithDefault(
      properties,
      "key",
      47);
  }

  @Test
  public void testGetOptionalDoubleMissing()
    throws Exception
  {
    Assert.assertEquals(
      47.0,
      JProperties.getDoubleWithDefault(
        new Properties(),
        "key",
        Double.valueOf(47.0)),
      0.0);
  }
}
