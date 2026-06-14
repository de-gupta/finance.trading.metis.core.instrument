package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.EquityProduct;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

import java.util.Objects;

public final class EquityListing<U extends PriceQuotingUnit, V extends SizeQuotingUnit>
		implements ListedInstrument<EquityProduct, U, V>
{
	private final InstrumentId id;
	private final EquityProduct product;
	private final VenueSymbol venueSymbol;
	private final ListingStatus status;
	private final TradingTerms<U, V> tradingTerms;

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

	@Override
	public InstrumentId id()
	{
		return id;
	}

	@Override
	public EquityProduct product()
	{
		return product;
	}

	@Override
	public VenueSymbol venueSymbol()
	{
		return venueSymbol;
	}

	@Override
	public ListingStatus status()
	{
		return status;
	}

	@Override
	public TradingTerms<U, V> tradingTerms()
	{
		return tradingTerms;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, product, venueSymbol, status, tradingTerms);
	}

	@Override
	public boolean equals(final Object object)
	{
		return object == this || isEqualTo(object);
	}

	@Override
	public String toString()
	{
		return "EquityListing[" + "id=" + id + ", " + "product=" + product + ", " + "venueSymbol=" + venueSymbol + ", " + "status=" + status + ", " + "tradingTerms=" + tradingTerms + ']';
	}

	private boolean isEqualTo(final Object object)
	{
		return switch (object)
		{
			case EquityListing<?, ?> that -> Objects.equals(this.id, that.id()) && Objects.equals(this.product,
					that.product()) && Objects.equals(this.venueSymbol, that.venueSymbol()) && Objects.equals(
					this.status, that.status()) && Objects.equals(this.tradingTerms, that.tradingTerms());
			default -> false;
		};
	}

	private EquityListing(final InstrumentId id, final EquityProduct product, final VenueSymbol venueSymbol,
	                      final ListingStatus status, final TradingTerms<U, V> tradingTerms)
	{
		this.id = id;
		this.product = product;
		this.venueSymbol = venueSymbol;
		this.status = status;
		this.tradingTerms = tradingTerms;
	}
}