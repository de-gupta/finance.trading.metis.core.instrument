package de.gupta.metis.core.instrument.domain.option;

import de.gupta.commons.utility.map.enumMap.ImmutableEnumMap;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record OptionListingIdentifiers(ImmutableEnumMap<OptionListingIdentifierScheme, IdentifierValue> values)
{
	public static OptionListingIdentifiers empty()
	{
		return new OptionListingIdentifiers(ImmutableEnumMap.empty(OptionListingIdentifierScheme.class));
	}

	public static OptionListingIdentifiers of(final Map<OptionListingIdentifierScheme, IdentifierValue> values)
	{
		return new OptionListingIdentifiers(ImmutableEnumMap.of(OptionListingIdentifierScheme.class, values));
	}

	public OptionListingIdentifiers
	{
		Objects.requireNonNull(values, "values may not be null");
	}

	public Optional<IdentifierValue> find(final OptionListingIdentifierScheme scheme)
	{
		return values.find(scheme);
	}
}
