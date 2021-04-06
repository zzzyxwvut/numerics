package org.zzzyxwvut.numerics.converter.internal;

import java.util.function.Supplier;

/**
 * This interface declares conversion between some types.
 *
 * @param <A> the type of an unwrapped object to convert from
 * @param <T> the type of an object to convert from
 * @param <B> the type of an unwrapped object to convert to
 * @param <U> the type of an object to convert to
 */
interface Converter<A, T extends Supplier<A>, B, U extends Supplier<B>>
{
	/**
	 * Returns an object converted from the passed value.
	 *
	 * @param value a value
	 * @return an object converted from the passed value
	 */
	U convert(T value);
}
