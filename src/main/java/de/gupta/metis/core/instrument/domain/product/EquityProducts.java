package de.gupta.metis.core.instrument.domain.product;

import de.gupta.metis.core.instrument.domain.identifier.EquityProductIdentifiers;

import java.util.Optional;

public final class EquityProducts
{
	public static EquityProduct commonStock(final ProductId id, final String issuerName)
	{
		return commonStock(id, issuerName, CountryCode.US);
	}

	public static EquityProduct commonStock(
			final ProductId id,
			final String issuerName,
			final CountryCode incorporationCountry
	)
	{
		return new EquityProduct(id, issuerName, Optional.empty(), EquitySecurityType.COMMON_STOCK,
				incorporationCountry, EquityProductIdentifiers.empty());
	}

	public static EquityProduct commonStock(
			final ProductId id,
			final String issuerName,
			final ShareClass shareClass,
			final CountryCode incorporationCountry
	)
	{
		return new EquityProduct(id, issuerName, Optional.of(shareClass), EquitySecurityType.COMMON_STOCK,
				incorporationCountry, EquityProductIdentifiers.empty());
	}

	private EquityProducts()
	{
	}
}