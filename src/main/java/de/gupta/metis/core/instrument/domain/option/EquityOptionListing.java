package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.instrument.domain.instrument.AbstractListingBuilder;
import de.gupta.metis.core.instrument.domain.instrument.InstrumentId;
import de.gupta.metis.core.instrument.domain.instrument.ListingDetails;
import de.gupta.metis.core.instrument.domain.instrument.ListingStatus;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public record EquityOptionListing<U extends PriceQuotingUnit, V extends SizeQuotingUnit>(
		EquityOptionProduct<?> product,
		ListingDetails<OptionListingIdentifiers, U, V> listing
) implements OptionListing<EquityOptionProduct<?>, OptionListingIdentifiers, U, V>
{
	public static <U extends PriceQuotingUnit, V extends SizeQuotingUnit>
	EquityOptionListing<U, V> of(
			final InstrumentId id,
			final EquityOptionProduct<?> product,
			final VenueSymbol venueSymbol,
			final OptionListingIdentifiers identifiers,
			final ListingStatus status,
			final TradingTerms<U, V> tradingTerms,
			final boolean primaryListing
	)
	{
		return new EquityOptionListing<>(product,
				new ListingDetails<>(id, venueSymbol,
						identifiers == null ? OptionListingIdentifiers.empty() : identifiers,
						status, tradingTerms, primaryListing));
	}

	public static <U extends PriceQuotingUnit, V extends SizeQuotingUnit> Builder<U, V> builder(
			final TradingTerms<U, V> tradingTerms
	)
	{
		return new Builder<>(tradingTerms);
	}

	public static EquityOptionListing<CurrencyPriceUnit<Currency.USD>, SizeQuotingUnit.Contracts> usdListed(
			final InstrumentId id,
			final EquityOptionProduct<?> product,
			final VenueSymbol venueSymbol,
			final ListingStatus status
	)
	{
		return EquityOptionListings.usdListed(id, product, venueSymbol, status);
	}

	public EquityOptionListing
	{
		listing = new ListingDetails<>(listing.id(), listing.venueSymbol(),
				listing.identifiers() == null ? OptionListingIdentifiers.empty() : listing.identifiers(),
				listing.status(), listing.tradingTerms(), listing.primaryListing());
	}

	public static final class Builder<U extends PriceQuotingUnit, V extends SizeQuotingUnit>
			extends AbstractListingBuilder<EquityOptionProduct<?>, OptionListingIdentifierScheme, U, V, Builder<U, V>>
	{
		public Builder<U, V> identifiers(final OptionListingIdentifiers identifiers)
		{
			return super.identifiers(identifiers.values());
		}

		public Builder<U, V> opra(final String value)
		{
			return super.identifier(OptionListingIdentifierScheme.OPRA, value);
		}

		public Builder<U, V> compositeFigi(final String value)
		{
			return super.identifier(OptionListingIdentifierScheme.COMPOSITE_FIGI, value);
		}

		public Builder<U, V> exchangeFigi(final String value)
		{
			return super.identifier(OptionListingIdentifierScheme.EXCHANGE_FIGI, value);
		}

		public EquityOptionListing<U, V> build()
		{
			return new EquityOptionListing<>(this.product,
					super.listing(new OptionListingIdentifiers(this.identifiers)));
		}

		@Override
		protected Builder<U, V> self()
		{
			return this;
		}

		Builder(final TradingTerms<U, V> tradingTerms)
		{
			super(tradingTerms, OptionListingIdentifierScheme.class);
		}
	}
}