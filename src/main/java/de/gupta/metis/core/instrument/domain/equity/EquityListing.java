package de.gupta.metis.core.instrument.domain.equity;

import de.gupta.metis.core.instrument.domain.instrument.*;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public record EquityListing<U extends PriceQuotingUnit, V extends SizeQuotingUnit>(
		EquityProduct product,
		ListingDetails<EquityListingIdentifiers, U, V> listing
) implements DetailedListing<EquityProduct, EquityListingIdentifiers, U, V>
{
	public static <U extends PriceQuotingUnit, V extends SizeQuotingUnit> EquityListing<U, V> of(final InstrumentId id,
	                                                                                             final EquityProduct product,
	                                                                                             final VenueSymbol venueSymbol,
	                                                                                             final EquityListingIdentifiers identifiers,
	                                                                                             final ListingStatus status,
	                                                                                             final TradingTerms<U, V> tradingTerms,
	                                                                                             final boolean primaryListing)
	{
		return new EquityListing<>(product,
				new ListingDetails<>(id, venueSymbol,
						identifiers == null ? EquityListingIdentifiers.empty() : identifiers,
						status, tradingTerms, primaryListing));
	}

	public static <U extends PriceQuotingUnit, V extends SizeQuotingUnit> Builder<U, V> builder(
			final TradingTerms<U, V> tradingTerms
	)
	{
		return new Builder<>(tradingTerms);
	}

	public static EquityListing<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Units> nasdaq(final InstrumentId id,
	                                                                                           final EquityProduct product,
	                                                                                           final String symbol,
	                                                                                           final ListingStatus status)
	{
		return EquityListings.nasdaq(id, product, symbol, status);
	}

	public EquityListing
	{
		listing = new ListingDetails<>(listing.id(), listing.venueSymbol(),
				listing.identifiers() == null ? EquityListingIdentifiers.empty() : listing.identifiers(),
				listing.status(), listing.tradingTerms(), listing.primaryListing());
	}

	public static final class Builder<U extends PriceQuotingUnit, V extends SizeQuotingUnit>
			extends AbstractListingBuilder<EquityProduct, EquityListingIdentifierScheme, U, V, Builder<U, V>>
	{
		public Builder<U, V> identifiers(final EquityListingIdentifiers identifiers)
		{
			return super.identifiers(identifiers.values());
		}

		public Builder<U, V> compositeFigi(final String value)
		{
			return super.identifier(EquityListingIdentifierScheme.COMPOSITE_FIGI, value);
		}

		public Builder<U, V> shareClassFigi(final String value)
		{
			return super.identifier(EquityListingIdentifierScheme.SHARE_CLASS_FIGI, value);
		}

		public EquityListing<U, V> build()
		{
			return new EquityListing<>(this.product, super.listing(new EquityListingIdentifiers(this.identifiers)));
		}

		@Override
		protected Builder<U, V> self()
		{
			return this;
		}

		private Builder(final TradingTerms<U, V> tradingTerms)
		{
			super(tradingTerms, EquityListingIdentifierScheme.class);
		}
	}
}