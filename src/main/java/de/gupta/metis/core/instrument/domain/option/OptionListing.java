package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.instrument.domain.instrument.DetailedListing;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;
import de.gupta.metis.core.types.quoting.SizeQuotingUnit;

public sealed interface OptionListing<P extends OptionProduct<?>, I, U extends PriceQuotingUnit, V extends SizeQuotingUnit>
		extends DetailedListing<P, I, U, V> permits EquityOptionListing
{
}
