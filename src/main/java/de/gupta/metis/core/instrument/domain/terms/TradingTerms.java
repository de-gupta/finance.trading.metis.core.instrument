package de.gupta.metis.core.instrument.domain.terms;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.quoting.*;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;

public record TradingTerms<U extends PriceQuotingUnit, V extends SizeQuotingUnit>(
		PriceQuotingConvention<U> priceConvention, SizeQuotingConvention<V> sizeConvention, SizeType roundLot,
		Currency settlementCurrency)
{
	public static <U extends PriceQuotingUnit, V extends SizeQuotingUnit> TradingTerms<U, V> of(
			final PriceQuotingConvention<U> priceConvention, final SizeQuotingConvention<V> sizeConvention,
			final SizeType roundLot, final Currency settlementCurrency)
	{
		return new TradingTerms<>(priceConvention, sizeConvention, roundLot, settlementCurrency);
	}

	public static <C extends Currency> TradingTerms<CurrencyPriceUnit<C>, SizeQuotingUnit.Units> cashEquity(
			final C settlementCurrency, final SizeType roundLot)
	{
		return TradingTerms.of(PriceQuotingConvention.currency(settlementCurrency), SizeQuotingConvention.units(0),
				roundLot, settlementCurrency);
	}

	public static <C extends Currency> TradingTerms<CurrencyPriceUnit<C>, SizeQuotingUnit.Units> cashEquity(
			final C settlementCurrency)
	{
		return cashEquity(settlementCurrency, SizeTypeFactory.of(100));
	}

	public TradingTerms
	{
		Unfolding.beckon(roundLot)
		         .metamorphose(SizeType::value)
		         .discern(TradingNumber::isPositive, ExceptionHelper.iaeFrom("Round lot must be positive"));

		Unfolding.beckon(priceConvention.unit())
		         .evolve(u -> u instanceof CurrencyPriceUnit<?>, u -> (CurrencyPriceUnit<?>) u)
		         .discern(u -> u.currency().equals(settlementCurrency),
						 IncompatibleInputException.from("Settlement currency must match the currency price " +
								 "convention"));
	}
}