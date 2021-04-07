package org.zzzyxwvut.numerics.converter;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
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
			.map(Roman::new)
			.sorted(Comparator.reverseOrder())
			.map(Roman::get)
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
}
