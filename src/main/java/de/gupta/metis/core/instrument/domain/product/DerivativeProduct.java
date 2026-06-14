package de.gupta.metis.core.instrument.domain.product;

public interface DerivativeProduct<U extends Product> extends Product
{
	U underlying();
}
