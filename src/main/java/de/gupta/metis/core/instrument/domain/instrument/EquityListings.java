package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.identifier.EquityListingIdentifiers;
import de.gupta.metis.core.instrument.domain.product.EquityProduct;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public final class EquityListings
{
	public static EquityListing<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Units> nasdaq(
			final InstrumentId id,
			final EquityProduct product,
			final String symbol,
			final ListingStatus status
	)
	{
		return new EquityListing<>(
				id,
				product,
				VenueSymbol.of(Venue.NASDAQ, symbol),
				EquityListingIdentifiers.empty(),
				status,
				TradingTerms.cashEquity(Currency.USD.INSTANCE),
				true
		);
	}

	public static EquityListing.Builder<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Units> nasdaqBuilder()
	{
		return EquityListing.builder(TradingTerms.cashEquity(Currency.USD.INSTANCE));
	}

	public static EquityListing.Builder<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Units> nasdaqBuilder(
			final String symbol
	)
	{
		return nasdaqBuilder().venueSymbol(Venue.NASDAQ, symbol);
	}

	private EquityListings()
	{
	}
}