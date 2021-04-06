package org.zzzyxwvut.numerics.converter.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.zzzyxwvut.numerics.converter.ArabicRomanConverter;

/** An {@code ArabicRomanConverter} implementation. */
public final class DefaultArabicRomanConverter implements ArabicRomanConverter
{
	private static final List<Short> ARABICS = List.of(
			(short) 1000,
			(short) 900, (short) 500, (short) 400, (short) 100,
			(short) 90, (short) 50, (short) 40, (short) 10,
			(short) 9, (short) 5, (short) 4, (short) 1);
	private static final List<String> ROMANS = List.of(
			"M",
			"CM", "D", "CD", "C",
			"XC", "L", "XL", "X",
			"IX", "V", "IV", "I");
	private static final Converter<Short, Arabic<Short>, String, Roman>
				TO_ROMAN_CONVERTER = arabicNumeral -> {
		/* The longest value is MMMDCCCLXXXVIII (3888). */
		final StringBuilder result = new StringBuilder(15);
		final Iterator<String> romans = ROMANS.iterator();
		final Iterator<Short> arabics = ARABICS.iterator();
		short value = arabicNumeral.get();

		do {
			final String roman = romans.next();
			final short arabic = arabics.next();

			while (value >= arabic) {
				result.append(roman);
				value -= arabic;
			} /* Beware of Tweedledum (Puzzle#9)! */
		} while (value > 0);

		return new Roman(result.toString());
	};
	private static final Converter<String, Roman, Short, Arabic<Short>>
				TO_ARABIC_CONVERTER = romanNumeral -> {
		final String value = romanNumeral.get();
		final int valueLength = value.length();
		final Iterator<String> romans = ROMANS.iterator();
		final Iterator<Short> arabics = ARABICS.iterator();
		short result = (short) 0;
		int offset = 0;

		do {
			final String roman = romans.next();
			final Short arabic = arabics.next();

			while (value.startsWith(roman, offset)) {
				result += arabic;
				offset += roman.length();
			}
		} while (offset < valueLength);

		return new Arabic<>(result);
	};

	/** Constructs a new {@code DefaultArabicRomanConverter} object. */
	public DefaultArabicRomanConverter() { }

	/** @throws IndexOutOfBoundsException {@inheritDoc} */
	@Override
	public Roman convert(Arabic<Short> numeral)
	{
		Objects.requireNonNull(numeral, "numeral");

		if (numeral.get() < 1 || numeral.get() > 3_999)
			throw new IndexOutOfBoundsException(
					"out of range: [1-3999]");

		return TO_ROMAN_CONVERTER.convert(numeral);
	}

	@Override
	public Arabic<Short> convert(Roman numeral)
	{
		Objects.requireNonNull(numeral, "numeral");
		return TO_ARABIC_CONVERTER.convert(numeral);
	}
}
