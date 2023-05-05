package org.zzzyxwvut.numerics.converter.internal;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import org.zzzyxwvut.numerics.converter.ArabicRomanConverter;

/** An {@code ArabicRomanConverter} implementation. */
public final class DefaultArabicRomanConverter implements ArabicRomanConverter
{
	private static final short[] ARABICS = new short[] {
				1000,
				900, 500, 400, 100,
				90, 50, 40, 10,
				9, 5, 4, 1
	};

	/* ROMANS [0-88] are exactly twice as many as ARABICS [1-1000]. */
	private static final byte[] ROMANS = new byte[] {
				'M', 0,
				'C', 'M', 'D', 0, 'C', 'D', 'C', 0,
				'X', 'C', 'L', 0, 'X', 'L', 'X', 0,
				'I', 'X', 'V', 0, 'I', 'V', 'I', 0
	};

	private static final Function<byte[], String> CHARSET_ENCODER;

	static {
		final Charset charset = Charset.defaultCharset();
		CHARSET_ENCODER = (UTF_8.equals(charset)
						|| ISO_8859_1.equals(charset)
						|| US_ASCII.equals(charset))
			? asciiValue -> new String(asciiValue, US_ASCII)
			: Function.<Function<Charset, Function<byte[], String>>>
								identity()
				.apply(defaultCharset -> asciiValue ->
								new String(
					new String(asciiValue, US_ASCII)
						.getBytes(defaultCharset),
					defaultCharset))
				.apply(charset);
	}

	private static final Converter<Short, Arabic<Short>, String, Roman>
				TO_ROMAN_CONVERTER = arabicNumeral -> {
		/* The longest value is MMMDCCCLXXXVIII (3888). */
		final byte[] result = new byte[15];
		short value = arabicNumeral.get();
		int offset = 0;
		int cursor = 0;

		while (value > 0) {
			/*
			 * ARABICS and ROMANS form a one-to-one mapping (where
			 * an Arabic element corresponds to a pair of Roman
			 * elements) and thus are accessed with an only cursor
			 * and without explicit prior checking of the bounds:
			 * an integer greater than zero is either unity or it
			 * is the sum of its unities.
			 */
			final short a = ARABICS[cursor];	/* 0, 1, 2, 3... */
			final byte r0 = ROMANS[cursor << 1];	/* 0, 2, 4, 6... */
			final byte r1 = ROMANS[(cursor++ << 1) + 1];

			if (value < a) {
				continue;
			} else if (r1 == 0) {
				while (value >= a) {
					result[offset++] = r0;	/* I, II, III. */
					value -= a;
				}
			} else {
				result[offset++] = r0;
				result[offset++] = r1;		/* IV. */
				value -= a;
			}
		}

		return new Roman(CHARSET_ENCODER
					.apply(Arrays.copyOfRange(result,
								0,
								offset)));
	};

	private static final Converter<String, Roman, Short, Arabic<Short>>
				TO_ARABIC_CONVERTER = romanNumeral -> {
		final byte[] value = romanNumeral.get().getBytes(US_ASCII);
		final int valueLength = value.length;
		short result = 0;
		int offset = 0;
		int cursor = 0;

		while (offset < valueLength) {
			/*
			 * ARABICS and ROMANS form a one-to-one mapping (where
			 * an Arabic element corresponds to a pair of Roman
			 * elements) and thus are accessed with an only cursor
			 * and without explicit prior checking of the bounds:
			 * a regular expression filter elsewhere only accepts
			 * values that are composed of the ASCII-compatible
			 * bytes exclusively drawn from ROMANS.
			 */
			final short a = ARABICS[cursor];	/* 0, 1, 2, 3... */
			final byte r0 = ROMANS[cursor << 1];	/* 0, 2, 4, 6... */
			final byte r1 = ROMANS[(cursor++ << 1) + 1];

			if (r1 == 0) {
				while (offset < valueLength
						&& r0 == value[offset]) {
					result += a;		/* I, II, III. */
					++offset;
				}
			} else if ((1 + offset) < valueLength
						&& r0 == value[offset]
						&& r1 == value[offset + 1]) {
				result += a;			/* IV. */
				offset += 2;
			}
		}

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
