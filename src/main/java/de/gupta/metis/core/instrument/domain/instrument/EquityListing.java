package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.identifier.EquityListingIdentifier;
import de.gupta.metis.core.instrument.domain.identifier.EquityListingIdentifierScheme;
import de.gupta.metis.core.instrument.domain.identifier.EquityListingIdentifiers;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;
import de.gupta.metis.core.instrument.domain.product.EquityProduct;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

import java.util.LinkedHashSet;
import java.util.Set;

public record EquityListing<U extends PriceQuotingUnit, V extends SizeQuotingUnit>(InstrumentId id,
                                                                                   EquityProduct product,
                                                                                   VenueSymbol venueSymbol,
                                                                                   EquityListingIdentifiers identifiers,
                                                                                   ListingStatus status,
                                                                                   TradingTerms<U, V> tradingTerms,
                                                                                   boolean primaryListing)
		implements ListedInstrument<EquityProduct, U, V>
{
	public static <U extends PriceQuotingUnit, V extends SizeQuotingUnit> EquityListing<U, V> of(final InstrumentId id,
	                                                                                             final EquityProduct product,
	                                                                                             final VenueSymbol venueSymbol,
	                                                                                             final EquityListingIdentifiers identifiers,
	                                                                                             final ListingStatus status,
	                                                                                             final TradingTerms<U, V> tradingTerms,
	                                                                                             final boolean primaryListing)
	{
		return new EquityListing<>(id, product, venueSymbol,
				identifiers == null ? EquityListingIdentifiers.empty() : identifiers, status, tradingTerms,
				primaryListing);
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
		identifiers = identifiers == null ? EquityListingIdentifiers.empty() : identifiers;
	}

	public static final class Builder<U extends PriceQuotingUnit, V extends SizeQuotingUnit>
	{
		private final TradingTerms<U, V> tradingTerms;
		private final Set<EquityListingIdentifier> identifiers = new LinkedHashSet<>();
		private InstrumentId id;
		private EquityProduct product;
		private VenueSymbol venueSymbol;
		private ListingStatus status = ListingStatus.ACTIVE;
		private boolean primaryListing = true;

		public Builder<U, V> id(final InstrumentId id)
		{
			this.id = id;
			return this;
		}

		public Builder<U, V> product(final EquityProduct product)
		{
			this.product = product;
			return this;
		}

		public Builder<U, V> venueSymbol(final VenueSymbol venueSymbol)
		{
			this.venueSymbol = venueSymbol;
			return this;
		}

		public Builder<U, V> venueSymbol(final Venue venue, final String symbol)
		{
			return venueSymbol(VenueSymbol.of(venue, symbol));
		}

		public Builder<U, V> status(final ListingStatus status)
		{
			this.status = status;
			return this;
		}

		public Builder<U, V> primaryListing(final boolean primaryListing)
		{
			this.primaryListing = primaryListing;
			return this;
		}

		public Builder<U, V> identifiers(final EquityListingIdentifiers identifiers)
		{
			this.identifiers.clear();
			this.identifiers.addAll(identifiers.values());
			return this;
		}

		public Builder<U, V> compositeFigi(final String value)
		{
			return identifier(EquityListingIdentifierScheme.COMPOSITE_FIGI, value);
		}

		public Builder<U, V> shareClassFigi(final String value)
		{
			return identifier(EquityListingIdentifierScheme.SHARE_CLASS_FIGI, value);
		}

		public Builder<U, V> identifier(final EquityListingIdentifierScheme scheme, final String value)
		{
			return identifier(new EquityListingIdentifier(scheme, new IdentifierValue(value)));
		}

		public Builder<U, V> identifier(final EquityListingIdentifier identifier)
		{
			this.identifiers.removeIf(existing -> existing.scheme() == identifier.scheme());
			this.identifiers.add(identifier);
			return this;
		}

		public EquityListing<U, V> build()
		{
			return new EquityListing<>(id, product, venueSymbol, new EquityListingIdentifiers(Set.copyOf(identifiers)),
					status, tradingTerms, primaryListing);
		}

		private Builder(final TradingTerms<U, V> tradingTerms)
		{
			this.tradingTerms = tradingTerms;
		}
	}
}