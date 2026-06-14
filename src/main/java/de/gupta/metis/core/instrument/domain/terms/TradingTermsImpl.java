package de.gupta.metis.core.instrument.domain.terms;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.quoting.*;
import de.gupta.metis.core.types.size.SizeType;

import java.util.Objects;

public final class TradingTermsImpl<U extends PriceQuotingUnit, V extends SizeQuotingUnit> implements TradingTerms<U, V>
{
	private final PriceQuotingConvention<U> priceConvention;
	private final SizeQuotingConvention<V> sizeConvention;
	private final SizeType roundLot;
	private final Currency settlementCurrency;

	public static <U extends PriceQuotingUnit, V extends SizeQuotingUnit> TradingTermsImpl<U, V> of(
			final PriceQuotingConvention<U> priceConvention,
			final SizeQuotingConvention<V> sizeConvention,
			final SizeType roundLot,
			final Currency settlementCurrency
	)
	{
//		return
		Unfolding.beckon(priceConvention)
		         .metamorphose(PriceQuotingConvention::unit)
		         .evolve(p -> p instanceof CurrencyPriceUnit<?>, p -> ((CurrencyPriceUnit<?>) p).currency())
		         .discern(c -> c.equals(settlementCurrency),
				         IncompatibleInputException.from("Settlement currency does not match price quoting convention"))
//		                .infuse(new TradingTermsImpl<>(priceConvention, sizeConvention, roundLot, settlementCurrency));
		;

		return new TradingTermsImpl<>(priceConvention, sizeConvention, roundLot, settlementCurrency);
	}

	@Override
	public PriceQuotingConvention<U> priceConvention()
	{
		return priceConvention;
	}

	@Override
	public SizeQuotingConvention<V> sizeConvention()
	{
		return sizeConvention;
	}

	@Override
	public SizeType roundLot()
	{
		return roundLot;
	}

	@Override
	public Currency settlementCurrency()
	{
		return settlementCurrency;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(priceConvention, sizeConvention, roundLot, settlementCurrency);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj == this) || isEqualTo(obj);
	}

	@Override
	public String toString()
	{
		return "TradingTerms[" +
				"priceConvention=" + priceConvention + ", " +
				"sizeConvention=" + sizeConvention + ", " +
				"roundLot=" + roundLot + ", " +
				"settlementCurrency=" + settlementCurrency + ']';
	}

	private boolean isEqualTo(final Object object)
	{
		return switch (object)
		{
			case TradingTerms<?, ?> that -> Objects.equals(this.priceConvention, that.priceConvention()) &&
					Objects.equals(this.sizeConvention, that.sizeConvention()) &&
					Objects.equals(this.roundLot, that.roundLot()) &&
					Objects.equals(this.settlementCurrency, that.settlementCurrency());
			default -> false;
		};

	}

	private TradingTermsImpl(
			PriceQuotingConvention<U> priceConvention,
			SizeQuotingConvention<V> sizeConvention,
			SizeType roundLot,
			Currency settlementCurrency
	)
	{
		this.priceConvention = priceConvention;
		this.sizeConvention = sizeConvention;
		this.roundLot = roundLot;
		this.settlementCurrency = settlementCurrency;
	}
}