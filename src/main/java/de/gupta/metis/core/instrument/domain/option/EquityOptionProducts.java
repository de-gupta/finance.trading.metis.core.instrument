package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.instrument.domain.equity.EquityProduct;
import de.gupta.metis.core.instrument.domain.product.ProductId;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;

import java.time.LocalDate;

public final class EquityOptionProducts
{
	public static <U extends PriceQuotingUnit> EquityOptionProduct<U> standardContract(
			final ProductId id,
			final EquityProduct underlying,
			final OptionRight right,
			final LocalDate expiryDate,
			final StrikePrice<U> strikePrice,
			final OptionExerciseStyle exerciseStyle
	)
	{
		return new EquityOptionProduct<>(id, underlying, right, expiryDate, strikePrice, exerciseStyle,
				OptionSettlementStyle.PHYSICAL_DELIVERY, ContractSize.standardEquityOption(),
				OptionProductIdentifiers.empty());
	}

	private EquityOptionProducts()
	{
	}
}
