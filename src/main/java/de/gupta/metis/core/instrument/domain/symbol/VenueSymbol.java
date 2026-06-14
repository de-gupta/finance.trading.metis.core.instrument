package de.gupta.metis.core.instrument.domain.symbol;

import de.gupta.metis.core.instrument.domain.venue.Venue;

public record VenueSymbol(Venue venue, Symbol symbol)
{
	public static VenueSymbol of(final Venue venue, final String symbol)
	{
		return new VenueSymbol(venue, new Symbol(symbol));
	}

	@Override
	public String toString()
	{
		return venue.code() + ":" + symbol.value();
	}
}