package de.gupta.metis.core.instrument.domain.identifier;

import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.util.Locale;

public record IdentifierValue(String value)
{
	public IdentifierValue
	{
		value = StringSanitizationUtility.requireNotBlankAnd(value, "Identifier value may not be blank",
				v -> v.trim().toUpperCase(Locale.ROOT));
	}
}
