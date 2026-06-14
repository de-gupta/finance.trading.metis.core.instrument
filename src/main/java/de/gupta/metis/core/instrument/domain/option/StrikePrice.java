package de.gupta.metis.core.instrument.domain.option;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.price.PriceType;
import de.gupta.metis.core.types.price.PriceTypeFactory;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;

public record StrikePrice<U extends PriceQuotingUnit>(PriceType value, PriceQuotingConvention<U> convention)
{
	public static <C extends Currency> StrikePrice<CurrencyPriceUnit<C>> of(final long value, final C currency)
	{
		return of(PriceTypeFactory.of(value), currency);
	}

	public static <C extends Currency> StrikePrice<CurrencyPriceUnit<C>> of(final PriceType value, final C currency)
	{
		return new StrikePrice<>(value, PriceQuotingConvention.currency(currency));
	}

	public StrikePrice
	{
		Unfolding.beckon(value)
		         .metamorphose(PriceType::value)
		         .discern(TradingNumber::isPositive,
						 ExceptionHelper.iaeFrom("Strike price must be positive"));
	}
}
