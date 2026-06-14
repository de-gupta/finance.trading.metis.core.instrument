package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.Product;
import de.gupta.metis.core.instrument.domain.symbol.Symbol;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public interface ListedInstrument<P extends Product, U extends PriceQuotingUnit, V extends SizeQuotingUnit>
		extends Instrument<P>
{
	VenueSymbol venueSymbol();

	default Venue venue()
	{
		return venueSymbol().venue();
	}

	default Symbol symbol()
	{
		return venueSymbol().symbol();
	}

	TradingTerms<U, V> tradingTerms();

	ListingStatus status();
}