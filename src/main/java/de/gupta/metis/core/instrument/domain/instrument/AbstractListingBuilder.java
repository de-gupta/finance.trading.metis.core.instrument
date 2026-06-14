package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.commons.utility.map.enumMap.ImmutableEnumMap;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;
import de.gupta.metis.core.instrument.domain.product.Product;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public abstract class AbstractListingBuilder<P extends Product, S extends Enum<S>, U extends PriceQuotingUnit,
		V extends SizeQuotingUnit, B extends AbstractListingBuilder<P, S, U, V, B>>
{
	protected final TradingTerms<U, V> tradingTerms;
	protected final Class<S> identifierSchemeType;
	protected InstrumentId id;
	protected P product;
	protected VenueSymbol venueSymbol;
	protected ListingStatus status = ListingStatus.ACTIVE;
	protected boolean primaryListing = true;
	protected ImmutableEnumMap<S, IdentifierValue> identifiers;

	public final B id(final InstrumentId id)
	{
		this.id = id;
		return self();
	}

	public final B product(final P product)
	{
		this.product = product;
		return self();
	}

	public final B venueSymbol(final VenueSymbol venueSymbol)
	{
		this.venueSymbol = venueSymbol;
		return self();
	}

	public final B venueSymbol(final Venue venue, final String symbol)
	{
		return venueSymbol(VenueSymbol.of(venue, symbol));
	}

	public final B status(final ListingStatus status)
	{
		this.status = status;
		return self();
	}

	public final B primaryListing(final boolean primaryListing)
	{
		this.primaryListing = primaryListing;
		return self();
	}

	protected final B identifiers(final ImmutableEnumMap<S, IdentifierValue> identifiers)
	{
		this.identifiers = identifiers;
		return self();
	}

	protected final B identifier(final S scheme, final String value)
	{
		this.identifiers = this.identifiers.with(scheme, new IdentifierValue(value));
		return self();
	}

	protected final <I> ListingDetails<I, U, V> listing(final I identifiers)
	{
		return new ListingDetails<>(id, venueSymbol, identifiers, status, tradingTerms, primaryListing);
	}

	protected abstract B self();

	protected AbstractListingBuilder(final TradingTerms<U, V> tradingTerms, final Class<S> identifierSchemeType)
	{
		this.tradingTerms = tradingTerms;
		this.identifierSchemeType = identifierSchemeType;
		this.identifiers = ImmutableEnumMap.empty(identifierSchemeType);
	}
}