package de.gupta.metis.core.instrument.domain.terms;

import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.SizeTypeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TradingTerms")
final class TradingTermsTest
{
	@Nested
	@DisplayName("when settlement currency does not match the price convention currency")
	final class WhenSettlementCurrencyDoesNotMatchThePriceConventionCurrency
	{
		@Test
		@DisplayName("rejects construction with an incompatible input exception")
		void rejectsConstructionWithAnIncompatibleInputException()
		{
			assertThatThrownBy(() -> TradingTerms.of(
					PriceQuotingConvention.currency(Currency.USD.INSTANCE),
					SizeQuotingConvention.units(0),
					SizeTypeFactory.of(100),
					Currency.EUR.INSTANCE
			)).as("USD price convention with EUR settlement currency")
			  .isInstanceOf(IncompatibleInputException.class);
		}
	}
}