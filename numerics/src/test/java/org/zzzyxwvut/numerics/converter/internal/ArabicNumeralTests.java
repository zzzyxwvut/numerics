package org.zzzyxwvut.numerics.converter.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.zzzyxwvut.ate.Testable;
import org.zzzyxwvut.ate.configuration.Instantiable;

import org.zzzyxwvut.numerics.converter.ArabicRomanConverter.Arabic;
import org.zzzyxwvut.numerics.converter.ArabicRomanConverter.Roman;
import org.zzzyxwvut.numerics.converter.ArabicRomanConverter;

class ArabicNumeralTests implements Testable
{
	private final ArabicRomanConverter converter =
					new DefaultArabicRomanConverter();

	public void testArabicNumeralConversion(Arabic<Short> arabicNumeral)
	{
		final Roman romanNumeral = converter.convert(arabicNumeral);
		final Arabic<Short> pendingNumeral = converter.convert(
								romanNumeral);
		assert arabicNumeral.equals(pendingNumeral) : pendingNumeral;
	}

	public static Instantiable<ArabicNumeralTests> instantiable()
	{
		return Instantiable.<ArabicNumeralTests>newBuilder(Set.of(
						ArabicNumeralTests::new))
			.arguments(Map.of("testArabicNumeralConversion",
						IntStream.range(1, 4000)
				.mapToObj(value -> List.of(new Arabic<>(
							(short) value)))
				.collect(Collectors.toUnmodifiableSet())))
			.build();
	}
}
