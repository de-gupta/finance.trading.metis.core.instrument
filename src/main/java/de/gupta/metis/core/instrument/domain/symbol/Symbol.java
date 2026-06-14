package de.gupta.metis.core.instrument.domain.symbol;

import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.util.Locale;

public record Symbol(String value)
{
	public Symbol
	{
		value = StringSanitizationUtility.requireNotBlankAnd(value, "Symbol may not be blank",
				s -> s.trim().toUpperCase(Locale.ROOT));
	}
}