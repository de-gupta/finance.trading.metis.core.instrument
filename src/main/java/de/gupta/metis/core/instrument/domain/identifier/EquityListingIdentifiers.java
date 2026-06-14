package de.gupta.metis.core.instrument.domain.identifier;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record EquityListingIdentifiers(Set<EquityListingIdentifier> values)
{
	public static EquityListingIdentifiers empty()
	{
		return new EquityListingIdentifiers(Set.of());
	}

	public static EquityListingIdentifiers of(final EquityListingIdentifier... values)
	{
		return new EquityListingIdentifiers(Set.of(values));
	}

	public EquityListingIdentifiers
	{
		Objects.requireNonNull(values, "values may not be null");
		values = Set.copyOf(values);

		if (values.stream().map(EquityListingIdentifier::scheme).distinct().count() != values.size())
			throw new IllegalArgumentException("Duplicate equity listing identifier schemes are not allowed");
	}

	public Optional<IdentifierValue> find(final EquityListingIdentifierScheme scheme)
	{
		return values.stream()
		             .filter(i -> i.scheme() == scheme)
		             .map(EquityListingIdentifier::value)
		             .findFirst();
	}
}