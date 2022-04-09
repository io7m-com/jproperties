/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.io7m.immutables.styles.ImmutablesStyleType;
import com.io7m.junreachable.UnreachableCodeException;
import org.immutables.value.Value;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.io7m.jproperties.monad.JPropertyParseMonadType.Kind.SUCCESS;

/**
 * A parser monad.
 *
 * @param <A> The type of returned values
 */

public interface JPropertyParseMonadType<A>
{
  /**
   * @return The kind of result
   */

  Kind kind();

  /**
   * @return The accumulated warnings
   */

  List<JPropertyWarning> warnings();

  /**
   * @return The accumulated errors
   */

  List<JPropertyError> errors();

  /**
   * Monadic bind for parse values.
   *
   * @param f   A function to apply to the current value
   * @param <B> The type of result values
   *
   * @return A monad
   */

  <B> JPropertyParseMonadType<B> flatMap(
    Function<A, JPropertyParseMonadType<B>> f);

  /**
   * Monadic bind for parse values.
   *
   * @param f   A function to apply to the current value
   * @param <B> The type of result values
   *
   * @return A monad
   */

  default <B> JPropertyParseMonadType<B> andThen(
    final Supplier<JPropertyParseMonadType<B>> f)
  {
    Objects.requireNonNull(f, "f");
    return this.flatMap(ignored -> f.get());
  }

  /**
   * Functor map for parse values.
   *
   * @param f   A function to apply to the current value
   * @param <B> The type of result values
   *
   * @return A monad
   */

  <B> JPropertyParseMonadType<B> map(
    Function<A, B> f);

  /**
   * The kind of result.
   */

  enum Kind
  {
    /**
     * A success result.
     */

    SUCCESS,

    /**
     * A failure result.
     */

    FAILURE
  }

  /**
   * Single-valued unit type.
   */

  enum Unit
  {

    /**
     * The unit value.
     */

    UNIT
  }

  /**
   * The type of parse warnings.
   */

  @ImmutablesStyleType
  @Value.Immutable
  interface JPropertyWarningType
  {
    /**
     * @return The warning message
     */

    @Value.Parameter
    String message();
  }

  /**
   * The type of parse errors.
   */

  @ImmutablesStyleType
  @Value.Immutable
  interface JPropertyErrorType
  {
    /**
     * @return The error message
     */

    @Value.Parameter
    String message();
  }

  /**
   * A result indicating success.
   *
   * @param <A> The type of result values
   */

  @ImmutablesStyleType
  @Value.Immutable
  interface JPropertySuccessType<A> extends JPropertyParseMonadType<A>
  {
    @Override
    default Kind kind()
    {
      return SUCCESS;
    }

    /**
     * @return The result of parsing
     */

    A result();

    @Override
    default <B> JPropertyParseMonadType<B> flatMap(
      final Function<A, JPropertyParseMonadType<B>> f)
    {
      Objects.requireNonNull(f, "f");
      final var next = f.apply(this.result());
      switch (next.kind()) {
        case SUCCESS: {
          final var nextSuccess = (JPropertySuccess<B>) next;
          return JPropertySuccess.<B>builder()
            .addAllWarnings(this.warnings())
            .addAllWarnings(next.warnings())
            .addAllErrors(this.errors())
            .addAllErrors(next.errors())
            .setResult(nextSuccess.result())
            .build();
        }
        case FAILURE: {
          final var nextFailure = (JPropertyFailure<B>) next;
          return JPropertyFailure.<B>builder()
            .addAllWarnings(this.warnings())
            .addAllWarnings(next.warnings())
            .addAllErrors(this.errors())
            .addAllErrors(next.errors())
            .setException(nextFailure.exception())
            .build();
        }
      }

      throw new UnreachableCodeException();
    }

    @Override
    default <B> JPropertyParseMonadType<B> map(final Function<A, B> f)
    {
      return JPropertySuccess.<B>builder()
        .addAllWarnings(this.warnings())
        .addAllErrors(this.errors())
        .setResult(f.apply(this.result()))
        .build();
    }
  }

  /**
   * A result indicating failure.
   *
   * @param <A> The type of result values
   */

  @ImmutablesStyleType
  @Value.Immutable
  interface JPropertyFailureType<A> extends JPropertyParseMonadType<A>
  {
    @Override
    default Kind kind()
    {
      return Kind.FAILURE;
    }

    /**
     * @return The exception raised during parsing
     */

    Exception exception();

    @SuppressWarnings("unchecked")
    @Override
    default <B> JPropertyParseMonadType<B> flatMap(
      final Function<A, JPropertyParseMonadType<B>> f)
    {
      Objects.requireNonNull(f, "f");
      return (JPropertyParseMonadType<B>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    default <B> JPropertyParseMonadType<B> map(final Function<A, B> f)
    {
      Objects.requireNonNull(f, "f");
      return (JPropertyParseMonadType<B>) this;
    }
  }

}
