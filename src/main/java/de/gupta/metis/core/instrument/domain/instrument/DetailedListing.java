package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.Product;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public interface DetailedListing<P extends Product, I, U extends PriceQuotingUnit, V extends SizeQuotingUnit>
		extends ListedInstrument<P, U, V>
{
	ListingDetails<I, U, V> listing();

	default I identifiers()
	{
		return listing().identifiers();
	}

	default boolean primaryListing()
	{
		return listing().primaryListing();
	}

	@Override
	default InstrumentId id()
	{
		return listing().id();
	}

	@Override
	default VenueSymbol venueSymbol()
	{
		return listing().venueSymbol();
	}

	@Override
	default TradingTerms<U, V> tradingTerms()
	{
		return listing().tradingTerms();
	}

	@Override
	default ListingStatus status()
	{
		return listing().status();
	}
}
