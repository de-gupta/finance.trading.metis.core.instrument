package de.gupta.metis.core.instrument.domain.option;

import de.gupta.commons.utility.map.enumMap.ImmutableEnumMap;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record OptionProductIdentifiers(ImmutableEnumMap<OptionProductIdentifierScheme, IdentifierValue> values)
{
	public static OptionProductIdentifiers empty()
	{
		return new OptionProductIdentifiers(ImmutableEnumMap.empty(OptionProductIdentifierScheme.class));
	}

	public static OptionProductIdentifiers of(final Map<OptionProductIdentifierScheme, IdentifierValue> values)
	{
		return new OptionProductIdentifiers(ImmutableEnumMap.of(OptionProductIdentifierScheme.class, values));
	}

	public OptionProductIdentifiers
	{
		Objects.requireNonNull(values, "values may not be null");
	}

	public Optional<IdentifierValue> find(final OptionProductIdentifierScheme scheme)
	{
		return values.find(scheme);
	}
}
