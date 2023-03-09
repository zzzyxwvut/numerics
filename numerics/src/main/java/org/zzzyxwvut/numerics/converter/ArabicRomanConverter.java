package org.zzzyxwvut.numerics.converter;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.zzzyxwvut.numerics.converter.internal.DefaultArabicRomanConverter;

/** This interface declares conversion between Arabic and Roman numerals. */
public interface ArabicRomanConverter
{
	/**
	 * Returns the Roman numeral equivalent of the passed Arabic numeral.
	 *
	 * @param numeral an Arabic numeral
	 * @return the Roman numeral equivalent of the passed Arabic numeral
	 * @throws IndexOutOfBoundsException if the passed numeral is out of
	 *	range [1-3999]
	 */
	Roman convert(Arabic<Short> numeral);

	/**
	 * Returns the Arabic numeral equivalent of the passed Roman numeral.
	 *
	 * @param numeral a Roman numeral
	 * @return the Arabic numeral equivalent of the passed Roman numeral
	 */
	Arabic<Short> convert(Roman numeral);

	/**
	 * Obtains an instance of {@code ArabicRomanConverter}.
	 *
	 * @return an instance of {@code ArabicRomanConverter}
	 */
	static ArabicRomanConverter instance()
	{
		return new DefaultArabicRomanConverter();
	}

	/** This {@code Supplier} represents a Roman numeral. */
	final class Roman implements Supplier<String>, Comparable<Roman>
	{
		/* Note that an empty string shall be matched as well. */
		private static final Pattern FROM_1_TO_3999 = Pattern.compile(
								"M{0,3}?"
					+ "(?:(?:C[MD])??|D??C{0,3}?)"
					+ "(?:(?:X[CL])??|L??X{0,3}?)"
					+ "(?:(?:I[XV])??|V??I{0,3}?)");
		private static final ArabicRomanConverter CONVERTER =
					ArabicRomanConverter.instance();

		private final String value;

		/**
		 * Constructs a new {@code Roman} numeral.
		 *
		 * @param value a string representation of a Roman numeral
		 * @throws IndexOutOfBoundsException if the passed value is out
		 *	of range [I-MMMCMXCIX] or the form of the passed value
		 *	does not conform to "an arbitrary subtractive-additive
		 *	notation rule"
		 */
		public Roman(String value)
		{
			Objects.requireNonNull(value, "value");

			/* The longest value is MMMDCCCLXXXVIII (3888). */
			if (value.isBlank()
					|| value.length() > 15
					|| !FROM_1_TO_3999.matcher(value)
								.matches())
				throw new IndexOutOfBoundsException(
						"out of range: [I-MMMCMXCIX]");

			this.value = value;
		}

		@Override
		public String get()		{ return value; }

		@Override
		public int compareTo(Roman that)
		{
			Objects.requireNonNull(that, "that");
			return CONVERTER.convert(this)
				.compareTo(CONVERTER.convert(that));
		}

		@Override
		public boolean equals(Object that)
		{
			return ((this == that)
				|| ((that instanceof Roman r)
				&& value.equals(r.get())));
		}

		@Override
		public int hashCode()		{ return value.hashCode(); }

		@Override
		public String toString()	{ return value; }
	}

	/**
	 * This {@code Supplier} represents an Arabic numeral.
	 *
	 * @param <T> a numeral type
	 */
	final class Arabic<T extends Number & Comparable<? super T>>
				implements Supplier<T>, Comparable<Arabic<T>>
	{
		private final T value;

		/**
		 * Constructs a new {@code Arabic} numeral.
		 *
		 * @param value a numeral representation of an Arabic numeral
		 */
		public Arabic(T value)
		{
			this.value = Objects.requireNonNull(value, "value");
		}

		@Override
		public T get()			{ return value; }

		@Override
		public int compareTo(Arabic<T> that)
		{
			Objects.requireNonNull(that, "that");
			return get().compareTo(that.get());
		}

		@Override
		public boolean equals(Object that)
		{
			return ((this == that)
				|| ((that instanceof Arabic<?> a)
				&& value.equals(a.get())));
		}

		@Override
		public int hashCode()		{ return value.hashCode(); }

		@Override
		public String toString()	{ return value.toString(); }
	}
}
