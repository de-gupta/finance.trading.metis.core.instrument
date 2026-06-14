package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.instrument.domain.instrument.InstrumentId;
import de.gupta.metis.core.instrument.domain.instrument.ListingDetails;
import de.gupta.metis.core.instrument.domain.instrument.ListingStatus;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public final class EquityOptionListings
{
	public static EquityOptionListing<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Contracts> usdListed(
			final InstrumentId id,
			final EquityOptionProduct<?> product,
			final VenueSymbol venueSymbol,
			final ListingStatus status
	)
	{
		return new EquityOptionListing<>(
				product,
				new ListingDetails<>(
						id,
						venueSymbol,
						OptionListingIdentifiers.empty(),
						status,
						TradingTerms.listedOption(Currency.USD.INSTANCE),
						true
				)
		);
	}

	public static EquityOptionListing.Builder<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Contracts> usdBuilder()
	{
		return new EquityOptionListing.Builder<>(
				TradingTerms.listedOption(Currency.USD.INSTANCE));
	}

	public static EquityOptionListing.Builder<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Contracts> usdBuilder(
			final Venue venue,
			final String symbol
	)
	{
		return usdBuilder().venueSymbol(venue, symbol);
	}

	private EquityOptionListings()
	{
	}
}