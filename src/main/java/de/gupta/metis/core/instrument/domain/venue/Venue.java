package de.gupta.metis.core.instrument.domain.venue;

import de.gupta.commons.utility.string.StringSanitizationUtility;
import de.gupta.metis.core.instrument.domain.product.CountryCode;

import java.util.Locale;

public record Venue(String code, String name, String operatingMic, CountryCode country)
{
	public static final Venue NASDAQ = new Venue("NASDAQ", "Nasdaq Stock Market", "XNAS", CountryCode.US);
	public static final Venue NYSE = new Venue("NYSE", "New York Stock Exchange", "XNYS", CountryCode.US);
	public static final Venue NYSE_ARCA = new Venue("NYSE_ARCA", "NYSE Arca", "ARCX", CountryCode.US);

	public Venue
	{
		code = StringSanitizationUtility.requireNotBlankAnd(code, "Venue code may not be blank", String::trim);
		name = StringSanitizationUtility.requireNotBlankAnd(name, "Venue name may not be blank", String::trim);
		operatingMic = StringSanitizationUtility.requireNotBlankAnd(operatingMic, "Venue MIC may not be blank",
				s -> s.trim().toUpperCase(Locale.ROOT));
	}
}