package de.gupta.metis.core.instrument.domain.terms;

import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;
import de.gupta.metis.core.types.size.SizeType;

public record TradingTerms<U extends PriceQuotingUnit, V extends SizeQuotingUnit>(
		PriceQuotingConvention<U> priceConvention,
		SizeQuotingConvention<V> sizeConvention,
		SizeType roundLot,
		Currency settlementCurrency
)
{
}