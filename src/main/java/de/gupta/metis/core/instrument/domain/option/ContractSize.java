package de.gupta.metis.core.instrument.domain.option;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.exception.ExceptionHelper;
import de.gupta.metis.core.types.number.TradingNumber;
import de.gupta.metis.core.types.size.SizeType;
import de.gupta.metis.core.types.size.SizeTypeFactory;

public record ContractSize(SizeType underlyingUnits)
{
	public static ContractSize of(final long underlyingUnits)
	{
		return new ContractSize(SizeTypeFactory.of(underlyingUnits));
	}

	public static ContractSize standardEquityOption()
	{
		return of(100);
	}

	public ContractSize
	{
		Unfolding.beckon(underlyingUnits)
		         .metamorphose(SizeType::value)
		         .discern(TradingNumber::isPositive,
						 ExceptionHelper.iaeFrom("Contract size must be positive"));
	}
}
