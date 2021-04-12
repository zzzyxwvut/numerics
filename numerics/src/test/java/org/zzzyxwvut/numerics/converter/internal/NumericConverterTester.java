package org.zzzyxwvut.numerics.converter.internal;

import java.util.Set;

import org.zzzyxwvut.ate.Testable;
import org.zzzyxwvut.ate.Tester;
import org.zzzyxwvut.ate.configuration.Configurable;

import org.zzzyxwvut.numerics.converter.ArabicRomanConverterTests;

class NumericConverterTester
{
	static {
		final ClassLoader loader = NumericConverterTester.class
			.getClassLoader();
		loader.setClassAssertionStatus(
			"org.zzzyxwvut.numerics.converter.ArabicRomanConverterTests",
									true);
		loader.setPackageAssertionStatus(
			"org.zzzyxwvut.numerics.converter.internal", true);
	}

	private NumericConverterTester() { /* No instantiation. */ }

	public static void main(String... args)
	{
		Tester.newBuilder(Set.of(ArabicRomanConverterTests.class,
						ArabicNumeralTests.class))
			.configurable(Configurable.newBuilder()
				.executionPolicy(Testable.ExecutionPolicy
								.CONCURRENT)
				.build())
			.verifyAssertion()
			.build()
			.runAndReport();
	}
}
