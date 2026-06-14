package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.commons.utility.string.StringSanitizationUtility;

public record InstrumentId(String value)
{
	public InstrumentId
	{
		value = StringSanitizationUtility.requireNotBlankAnd(value, "Instrument id may not be blank", String::trim);
	}
}