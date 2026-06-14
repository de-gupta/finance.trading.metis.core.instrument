package de.gupta.metis.core.instrument.domain.identifier;

import java.util.Objects;

public record EquityProductIdentifier(EquityProductIdentifierScheme scheme, IdentifierValue value)
{
	public EquityProductIdentifier
	{
		Objects.requireNonNull(scheme, "scheme may not be null");
		Objects.requireNonNull(value, "value may not be null");
	}
}
