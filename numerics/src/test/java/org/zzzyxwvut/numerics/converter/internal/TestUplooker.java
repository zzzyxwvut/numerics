package org.zzzyxwvut.numerics.converter.internal;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodHandles;

import org.zzzyxwvut.ate.support.AccessibleUplooker;

/** An {@code AccessibleUplooker}. */
public class TestUplooker extends AccessibleUplooker
{
	/* See https://bugs.openjdk.java.net/browse/JDK-8228624. */
	@Override
	public Lookup exportableLookup()
	{
		return MethodHandles.lookup()
			.dropLookupMode(Lookup.PROTECTED);
	}
}
