package org.zzzyxwvut.numerics.converter.internal;

import java.util.function.Supplier;

/**
 * This interface declares conversion between suppliers.
 *
 * @param <A> the type of a supplier-furnished object to convert from
 * @param <T> the type of a supplier to convert from
 * @param <B> the type of a supplier-furnished object to convert to
 * @param <U> the type of a supplier to convert to
 */
@FunctionalInterface
interface Converter<A, T extends Supplier<A>, B, U extends Supplier<B>>
{
	/**
	 * Returns a supplier converted from the passed supplier.
	 *
	 * @param supplier a supplier
	 * @return a supplier converted from the passed supplier
	 */
	U convert(T supplier);
}
