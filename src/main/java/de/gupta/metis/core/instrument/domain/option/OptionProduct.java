package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.instrument.domain.product.DerivativeProduct;
import de.gupta.metis.core.instrument.domain.product.Product;

public sealed interface OptionProduct<U extends Product> extends DerivativeProduct<U> permits EquityOptionProduct
{
	OptionRight right();

	StrikePrice<?> strikePrice();

	OptionExerciseStyle exerciseStyle();

	OptionSettlementStyle settlementStyle();

	ContractSize contractSize();
}
