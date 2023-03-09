package org.zzzyxwvut.numerics.converter.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

import org.zzzyxwvut.ate.Status;
import org.zzzyxwvut.ate.Testable;
import org.zzzyxwvut.ate.configuration.Instantiable;

import org.zzzyxwvut.numerics.converter.ArabicRomanConverter.Arabic;
import org.zzzyxwvut.numerics.converter.ArabicRomanConverter.Roman;
import org.zzzyxwvut.numerics.converter.ArabicRomanConverter;

class RomanNumeralTests implements Testable
{
	private static final AtomicInteger NON_NUMERALS = new AtomicInteger(0);

	private final ArabicRomanConverter converter =
					new DefaultArabicRomanConverter();

	public void testRandomRomanNumeralConversion(Status status,
							String pendingValue)
	{
		final Arabic<Short> arabicNumeral;

		try {
			arabicNumeral = converter.convert(new Roman(
							pendingValue));
		} catch (final IndexOutOfBoundsException aleatory) {
			NON_NUMERALS.incrementAndGet();
			return;
		}

		status.printStream()
			.format("accepted: %s%n", pendingValue);
		final String romanValue = converter.convert(arabicNumeral)
			.get();
		assert pendingValue.equals(romanValue) :
					String.format("%s => %s%n",
						pendingValue, romanValue);
	}

	public static void tearDownAll(Status status)
	{
		final int nonNumerals = NON_NUMERALS.get();
		NON_NUMERALS.set(0);
		status.printStream()
			.format("%nREJECTS TOTAL: %s%n", nonNumerals);
	}

	public static Instantiable<RomanNumeralTests> instantiable()
	{
		return Instantiable.<RomanNumeralTests>newBuilder(Set.of(
						RomanNumeralTests::new))
			.arguments(arguments())
			.build();
	}

	private static Function<List<String>, IntFunction<String>> letterer()
	{
		return romans -> romans::get;
	}

	private static Function<IntFunction<String>,
				IntFunction<LongFunction<String>>> generator()
	{
		return letterer -> digitCount -> wordCount ->
							ThreadLocalRandom
			.current()
			.ints(wordCount, 0, digitCount)
			.mapToObj(letterer)
			.collect(Collectors.joining(""));
	}

	private static Map<String, Collection<List<?>>> arguments()
	{
		final List<String> romans = Arrays.asList("M", "D", "C",
								"L", "X",
								"V", "I");
		final int maxRomanLength = 15; /* MMMDCCCLXXXVIII */
		Collections.shuffle(romans);
		return Map.of("testRandomRomanNumeralConversion",
							ThreadLocalRandom
				.current()
				.ints(32L, 1, maxRomanLength + 1)
				.mapToObj(Function.<Function<LongFunction<String>,
						IntFunction<List<String>>>>
								identity()
					.apply(generator -> i -> List.of(
							generator.apply(i)))
					.apply(generator()
						.apply(letterer()
							.apply(romans))
						.apply(romans.size())))
				.collect(Collectors.toUnmodifiableSet()));
	}	/* Generate 32 words of 1-to-15 Roman digits. */
}
