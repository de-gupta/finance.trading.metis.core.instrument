package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.Product;
import de.gupta.metis.core.instrument.domain.symbol.Symbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public sealed interface ListedInstrument<P extends Product, U extends PriceQuotingUnit, V extends SizeQuotingUnit>
		extends Instrument<P> permits EquityListing
{
	P product();

	Venue venue();

	Symbol symbol();

	TradingTerms<U, V> tradingTerms();

	ListingStatus status();
}