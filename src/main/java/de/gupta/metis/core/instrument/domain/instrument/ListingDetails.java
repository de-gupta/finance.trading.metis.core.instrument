package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public record ListingDetails<I, U extends PriceQuotingUnit, V extends SizeQuotingUnit>(
		InstrumentId id,
		VenueSymbol venueSymbol,
		I identifiers,
		ListingStatus status,
		TradingTerms<U, V> tradingTerms,
		boolean primaryListing
)
{
}