package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.Product;

public interface Instrument<P extends Product>
{
	InstrumentId id();

	P product();
}