package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.EquityProduct;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public record EquityListing<U extends PriceQuotingUnit, V extends SizeQuotingUnit>(InstrumentId id,
                                                                                   EquityProduct product,
                                                                                   VenueSymbol venueSymbol,
                                                                                   ListingStatus status,
                                                                                   TradingTerms<U, V> tradingTerms)
		implements ListedInstrument<EquityProduct, U, V>
{
	public static <U extends PriceQuotingUnit, V extends SizeQuotingUnit> EquityListing<U, V> of(final InstrumentId id,
	                                                                                             final EquityProduct product,
	                                                                                             final VenueSymbol venueSymbol,
	                                                                                             final ListingStatus status,
	                                                                                             final TradingTerms<U, V> tradingTerms)
	{
		return new EquityListing<>(id, product, venueSymbol, status, tradingTerms);
	}

	public static EquityListing<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Units> nasdaq(final InstrumentId id,
	                                                                                           final EquityProduct product,
	                                                                                           final String symbol,
	                                                                                           final ListingStatus status)
	{
		return EquityListing.of(id, product, VenueSymbol.of(Venue.NASDAQ, symbol), status,
				TradingTerms.cashEquity(Currency.USD.INSTANCE));
	}
}