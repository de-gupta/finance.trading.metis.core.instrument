package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.EquityProduct;
import de.gupta.metis.core.instrument.domain.symbol.Symbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public final class EquityListing<P extends EquityProduct, U extends PriceQuotingUnit, V extends SizeQuotingUnit>
		implements ListedInstrument<P, U, V>
{
	private final InstrumentId id;
	private final P product;
	private final Venue venue;
	private final Symbol symbol;
	private final ListingStatus status;
	private final TradingTerms<U, V> tradingTerms;

	@Override
	public InstrumentId id()
	{
		return id;
	}

	@Override
	public P product()
	{
		return product;
	}

	@Override
	public Venue venue()
	{
		return venue;
	}

	@Override
	public Symbol symbol()
	{
		return symbol;
	}

	@Override
	public TradingTerms<U, V> tradingTerms()
	{
		return tradingTerms;
	}

	@Override
	public ListingStatus status()
	{
		return status;
	}

	private EquityListing(final InstrumentId id, final P product, final Venue venue, final Symbol symbol,
	                      final ListingStatus status,
	                      final TradingTerms<U, V> tradingTerms)
	{
		this.id = id;
		this.product = product;
		this.venue = venue;
		this.symbol = symbol;
		this.status = status;
		this.tradingTerms = tradingTerms;
	}
}