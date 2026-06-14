package de.gupta.metis.core.instrument.domain.product;

import de.gupta.commons.utility.string.StringSanitizationUtility;
import de.gupta.metis.core.instrument.domain.identifier.EquityProductIdentifiers;

import java.util.Objects;
import java.util.Optional;

public record EquityProduct(ProductId id, String issuerName, Optional<ShareClass> shareClass,
                            EquitySecurityType securityType, CountryCode incorporationCountry,
                            EquityProductIdentifiers identifiers) implements Product
{
	public static EquityProduct commonStock(final ProductId id, final String issuerName)
	{
		return commonStock(id, issuerName, CountryCode.US);
	}

	public static EquityProduct commonStock(final ProductId id, final String issuerName,
	                                        final CountryCode incorporationCountry)
	{
		return new EquityProduct(id, issuerName, Optional.empty(), EquitySecurityType.COMMON_STOCK,
				incorporationCountry, EquityProductIdentifiers.empty());
	}

	public static EquityProduct commonStock(final ProductId id, final String issuerName, final ShareClass shareClass,
	                                        final CountryCode incorporationCountry)
	{
		return new EquityProduct(id, issuerName, Optional.of(shareClass), EquitySecurityType.COMMON_STOCK,
				incorporationCountry, EquityProductIdentifiers.empty());
	}

	public EquityProduct
	{
		Objects.requireNonNull(id, "id must not be null");
		Objects.requireNonNull(securityType, "Security type may not be null");
		Objects.requireNonNull(incorporationCountry, "Incorporation country may not be null");
		identifiers = identifiers == null ? EquityProductIdentifiers.empty() : identifiers;
		issuerName =
				StringSanitizationUtility.requireNotBlankAnd(issuerName, "Issuer name may not be blank", String::trim);
	}
}