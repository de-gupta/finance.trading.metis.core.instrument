package de.gupta.metis.core.instrument.domain.equity;

import de.gupta.commons.utility.map.enumMap.ImmutableEnumMap;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record EquityListingIdentifiers(ImmutableEnumMap<EquityListingIdentifierScheme, IdentifierValue> values)
{
	public static EquityListingIdentifiers empty()
	{
		return new EquityListingIdentifiers(ImmutableEnumMap.empty(EquityListingIdentifierScheme.class));
	}

	public static EquityListingIdentifiers of(final Map<EquityListingIdentifierScheme, IdentifierValue> values)
	{
		return new EquityListingIdentifiers(ImmutableEnumMap.of(EquityListingIdentifierScheme.class, values));
	}

	public EquityListingIdentifiers
	{
		Objects.requireNonNull(values, "values may not be null");
	}

	public Optional<IdentifierValue> find(final EquityListingIdentifierScheme scheme)
	{
		return values.find(scheme);
	}
}
