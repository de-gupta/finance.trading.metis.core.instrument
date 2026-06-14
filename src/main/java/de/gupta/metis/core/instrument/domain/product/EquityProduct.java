package de.gupta.metis.core.instrument.domain.product;

import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.util.Objects;

public record EquityProduct(ProductId id, String issuerName, ShareClass shareClass) implements Product
{
	public static EquityProduct commonStock(final ProductId id, final String issuerName)
	{
		return new EquityProduct(id, issuerName, ShareClass.COMMON);
	}

	public EquityProduct
	{
		Objects.requireNonNull(id, "id must not be null");
		Objects.requireNonNull(shareClass, "Share class may not be null");
		issuerName =
				StringSanitizationUtility.requireNotBlankAnd(issuerName, "Issuer name may not be blank", String::trim);
	}
}