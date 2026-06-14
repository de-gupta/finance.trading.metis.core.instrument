package de.gupta.metis.core.instrument.domain.venue;

import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.util.Locale;

public record Venue(String code, String name, String operatingMic)
{
	public static final Venue NASDAQ = new Venue("NASDAQ", "Nasdaq Stock Market", "XNAS");

	public Venue
	{
		code = StringSanitizationUtility.requireNotBlankAnd(code, "Venue code may not be blank", String::trim);
		name = StringSanitizationUtility.requireNotBlankAnd(name, "Venue name may not be blank", String::trim);
		operatingMic = StringSanitizationUtility.requireNotBlankAnd(operatingMic, "Venue MIC may not be blank",
				s -> s.trim().toUpperCase(Locale.ROOT));
	}
}