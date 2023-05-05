package org.zzzyxwvut.numerics.converter;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.zzzyxwvut.numerics.converter.ArabicRomanConverter.Arabic;
import org.zzzyxwvut.numerics.converter.ArabicRomanConverter.Roman;

import org.zzzyxwvut.ate.Testable;

public class ArabicRomanConverterTests implements Testable
{
	private static final ArabicRomanConverter CONVERTER =
					ArabicRomanConverter.instance();

	public void testObjectOrderAndEquality()
	{
		final Arabic<BigDecimal> a1_0 = new Arabic<>(
						new BigDecimal("1.0"));
		final Arabic<BigDecimal> a1_00 = new Arabic<>(
						new BigDecimal("1.00"));
		assert !a1_0.equals(a1_00) && a1_0.compareTo(a1_00) == 0
				: "See the java.math.BigDecimal API Note.";
	}

	private static <T> void assertEquals(T expected, T obtained)
	{
		assert expected.equals(obtained) : String.format("%s != %s%n",
							expected.toString(),
							obtained.toString());
	}

	public void testRomanOrderWithPowersOfTwo()
	{
		final List<String> expected = List.of("MMXLVIII", "MXXIV",
				"DXII", "CCLVI", "CXXVIII", "LXIV", "XXXII",
				"XVI", "VIII", "IV", "II", "I");
		final List<String> obtained = Stream.of("IV", "XXXII",
				"CCLVI", "XVI", "I", "DXII", "MMXLVIII",
				"CXXVIII", "MXXIV", "VIII", "LXIV", "II")
			.sorted(Function.<Function<Function<String, Arabic<Short>>,
					Function<Map<String, Arabic<Short>>,
							Comparator<String>>>>
								identity()
				.apply(remapper -> cache -> (left, right) ->
					/*
					 * Arrange the elements in reverse order
					 * to match the _expected_ elements.
					 */
									cache
					.computeIfAbsent(right, remapper)
						.compareTo(cache
							.computeIfAbsent(
								left,
								remapper)))
				.apply(value -> CONVERTER.convert(
							new Roman(value)))
				.apply(new HashMap<>(32)))
			.collect(Collectors.toUnmodifiableList());
		assertEquals(expected, obtained);
	}

	public void testToRomanNumeralWith0()
	{
		try {
			CONVERTER.convert(new Arabic<>((short) 0));
		} catch (final IndexOutOfBoundsException expected) {
			return;
		}

		throw new AssertionError();
	}

	public void testToRomanNumeralWith3888()
	{
		final Roman obtained = CONVERTER.convert(
					new Arabic<>((short) 3888));
		final Roman expected = new Roman("MMMDCCCLXXXVIII");
		assertEquals(expected, obtained);
	}

	public void testToRomanNumeralWith3999AndDefaultCharsetCompatibility()
	{
		final Charset defaultCharset = Charset.defaultCharset();
		final Roman obtained = CONVERTER.convert(
					new Arabic<>((short) 3999));
		final Roman expected = new Roman(new String(
					"MMMCMXCIX".getBytes(defaultCharset),
					defaultCharset));
		assertEquals(expected, obtained);
	}

	public void testToArabicNumeralWithEmptyString()
	{
		try {
			CONVERTER.convert(new Roman(""));
		} catch (final IndexOutOfBoundsException expected) {
			return;
		}

		throw new AssertionError();
	}

	public void testToArabicNumeralWithMMMDCCCLXXXVIII()
	{
		final Arabic<Short> obtained = CONVERTER.convert(
					new Roman("MMMDCCCLXXXVIII"));
		final Arabic<Short> expected = new Arabic<>((short) 3888);
		assertEquals(expected, obtained);
	}

	public void testToArabicNumeralWithUTF16MMMCCCXXXIII()
	{
		final Arabic<Short> obtained = CONVERTER.convert(
							new Roman(new String(
					"MMMCCCXXXIII".getBytes(UTF_16),
					UTF_16)));
		final Arabic<Short> expected = new Arabic<>((short) 3333);
		assertEquals(expected, obtained);
	}

	public void testToArabicNumeralWithUTF16BEMCCCXIII()
	{
		final Arabic<Short> obtained = CONVERTER.convert(
							new Roman(new String(
					"MCCCXIII".getBytes(UTF_16BE),
					UTF_16BE)));
		final Arabic<Short> expected = new Arabic<>((short) 1313);
		assertEquals(expected, obtained);
	}

	public void testToArabicNumeralWithUTF16LEMMMCXXXI()
	{
		final Arabic<Short> obtained = CONVERTER.convert(
							new Roman(new String(
					"MMMCXXXI".getBytes(UTF_16LE),
					UTF_16LE)));
		final Arabic<Short> expected = new Arabic<>((short) 3131);
		assertEquals(expected, obtained);
	}
}
