package de.gupta.metis.core.instrument.domain.product;

import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.util.Locale;

public record CountryCode(String value)
{
	public static final CountryCode US = new CountryCode("US");

	public CountryCode
	{
		value = StringSanitizationUtility.requireNotBlankAnd(value, "Country code may not be blank",
				v -> v.trim().toUpperCase(Locale.ROOT));
	}
}