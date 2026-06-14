package de.gupta.metis.core.instrument.domain.product;

import de.gupta.commons.utility.string.StringSanitizationUtility;
import de.gupta.metis.core.instrument.domain.identifier.EquityProductIdentifier;
import de.gupta.metis.core.instrument.domain.identifier.EquityProductIdentifierScheme;
import de.gupta.metis.core.instrument.domain.identifier.EquityProductIdentifiers;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record EquityProduct(ProductId id, String issuerName, Optional<ShareClass> shareClass,
                            EquitySecurityType securityType, CountryCode incorporationCountry,
                            EquityProductIdentifiers identifiers) implements Product
{
	public static EquityProduct commonStock(final ProductId id, final String issuerName)
	{
		return EquityProducts.commonStock(id, issuerName);
	}

	public static EquityProduct commonStock(final ProductId id, final String issuerName,
	                                        final CountryCode incorporationCountry)
	{
		return EquityProducts.commonStock(id, issuerName, incorporationCountry);
	}

	public static EquityProduct commonStock(final ProductId id, final String issuerName, final ShareClass shareClass,
	                                        final CountryCode incorporationCountry)
	{
		return EquityProducts.commonStock(id, issuerName, shareClass, incorporationCountry);
	}

	public static Builder builder()
	{
		return new Builder();
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

	public static final class Builder
	{
		private final Set<EquityProductIdentifier> identifiers = new LinkedHashSet<>();
		private ProductId id;
		private String issuerName;
		private Optional<ShareClass> shareClass = Optional.empty();
		private EquitySecurityType securityType = EquitySecurityType.COMMON_STOCK;
		private CountryCode incorporationCountry = CountryCode.US;

		public Builder id(final ProductId id)
		{
			this.id = id;
			return this;
		}

		public Builder issuerName(final String issuerName)
		{
			this.issuerName = issuerName;
			return this;
		}

		public Builder shareClass(final ShareClass shareClass)
		{
			this.shareClass = Optional.ofNullable(shareClass);
			return this;
		}

		public Builder noShareClass()
		{
			this.shareClass = Optional.empty();
			return this;
		}

		public Builder securityType(final EquitySecurityType securityType)
		{
			this.securityType = securityType;
			return this;
		}

		public Builder incorporationCountry(final CountryCode incorporationCountry)
		{
			this.incorporationCountry = incorporationCountry;
			return this;
		}

		public Builder identifiers(final EquityProductIdentifiers identifiers)
		{
			this.identifiers.clear();
			this.identifiers.addAll(identifiers.values());
			return this;
		}

		public Builder isin(final String value)
		{
			return identifier(EquityProductIdentifierScheme.ISIN, value);
		}

		public Builder cusip(final String value)
		{
			return identifier(EquityProductIdentifierScheme.CUSIP, value);
		}

		public Builder identifier(final EquityProductIdentifierScheme scheme, final String value)
		{
			return identifier(new EquityProductIdentifier(scheme, new IdentifierValue(value)));
		}

		public Builder identifier(final EquityProductIdentifier identifier)
		{
			this.identifiers.removeIf(existing -> existing.scheme() == identifier.scheme());
			this.identifiers.add(identifier);
			return this;
		}

		public EquityProduct build()
		{
			return new EquityProduct(id, issuerName, shareClass, securityType, incorporationCountry,
					new EquityProductIdentifiers(Set.copyOf(identifiers)));
		}
	}
}