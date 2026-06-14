package de.gupta.metis.core.instrument.domain.identifier;

import java.util.Objects;

public record EquityListingIdentifier(EquityListingIdentifierScheme scheme, IdentifierValue value)
{
	public EquityListingIdentifier
	{
		Objects.requireNonNull(scheme, "scheme may not be null");
		Objects.requireNonNull(value, "value may not be null");
	}
}