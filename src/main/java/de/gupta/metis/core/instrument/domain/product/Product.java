package de.gupta.metis.core.instrument.domain.product;

public sealed interface Product permits EquityProduct
{
	ProductId id();
}