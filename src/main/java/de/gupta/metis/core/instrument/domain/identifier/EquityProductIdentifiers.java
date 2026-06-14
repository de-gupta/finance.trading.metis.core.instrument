package de.gupta.metis.core.instrument.domain.identifier;

import java.util.Objects;
import java.util.Set;

public record EquityProductIdentifiers(Set<EquityProductIdentifier> values)
{
	public static EquityProductIdentifiers empty()
	{
		return new EquityProductIdentifiers(Set.of());
	}

	public static EquityProductIdentifiers of(final EquityProductIdentifier... values)
	{
		return new EquityProductIdentifiers(Set.of(values));
	}

	public EquityProductIdentifiers
	{
		Objects.requireNonNull(values, "values may not be null");
		values = Set.copyOf(values);

		if (values.stream().map(EquityProductIdentifier::scheme).distinct().count() != values.size())
			throw new IllegalArgumentException("Duplicate equity product identifier schemes are not allowed");
	}
}
