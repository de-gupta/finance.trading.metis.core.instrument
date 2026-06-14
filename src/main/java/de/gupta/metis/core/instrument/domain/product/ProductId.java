package de.gupta.metis.core.instrument.domain.product;

import de.gupta.commons.utility.string.StringSanitizationUtility;

public record ProductId(String value)
{
	public ProductId
	{
		value = StringSanitizationUtility.requireNotBlankAnd(value, "Product id may not be blank", String::trim);
	}
}