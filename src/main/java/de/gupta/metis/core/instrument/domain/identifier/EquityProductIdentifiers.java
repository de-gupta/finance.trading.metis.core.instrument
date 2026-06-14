package de.gupta.metis.core.instrument.domain.identifier;

import de.gupta.commons.utility.map.enumMap.ImmutableEnumMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record EquityProductIdentifiers(ImmutableEnumMap<EquityProductIdentifierScheme, IdentifierValue> values)
{
	public static EquityProductIdentifiers empty()
	{
		return new EquityProductIdentifiers(ImmutableEnumMap.empty(EquityProductIdentifierScheme.class));
	}

	public static EquityProductIdentifiers of(final Map<EquityProductIdentifierScheme, IdentifierValue> values)
	{
		return new EquityProductIdentifiers(ImmutableEnumMap.of(EquityProductIdentifierScheme.class, values));
	}

	public EquityProductIdentifiers
	{
		Objects.requireNonNull(values, "values may not be null");
	}

	public Optional<IdentifierValue> find(final EquityProductIdentifierScheme scheme)
	{
		return values.find(scheme);
	}
}