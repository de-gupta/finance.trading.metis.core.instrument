package de.gupta.metis.core.instrument.domain.terms;

import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.SizeTypeFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TradingTerms")
final class TradingTermsTest
{
	@Nested
	@DisplayName("when settlement currency does not match the price convention currency")
	final class WhenSettlementCurrencyDoesNotMatchThePriceConventionCurrency
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("rejectsConstructionWithAnIncompatibleInputExceptionCases")
		@DisplayName("rejects construction with an incompatible input exception")
		void rejectsConstructionWithAnIncompatibleInputException(final String as, final Currency settlementCurrency)
		{
			assertThatThrownBy(() -> TradingTerms.of(
					PriceQuotingConvention.currency(Currency.USD.INSTANCE),
					SizeQuotingConvention.units(0),
					SizeTypeFactory.of(100),
					settlementCurrency
			)).as(as).isInstanceOf(IncompatibleInputException.class);
		}

		private static Stream<Arguments> rejectsConstructionWithAnIncompatibleInputExceptionCases()
		{
			return Stream.of(
					Arguments.of("USD price convention with EUR settlement currency", Currency.EUR.INSTANCE),
					Arguments.of("USD price convention with JPY settlement currency", Currency.JPY.INSTANCE)
			);
		}
	}

	@Nested
	@DisplayName("when round lot is not positive")
	final class WhenRoundLotIsNotPositive
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("rejectsConstructionWithAnIllegalArgumentExceptionCases")
		@DisplayName("rejects construction with an illegal argument exception")
		void rejectsConstructionWithAnIllegalArgumentException(final String as, final long roundLot)
		{
			assertThatThrownBy(() -> TradingTerms.of(
					PriceQuotingConvention.currency(Currency.USD.INSTANCE),
					SizeQuotingConvention.units(0),
					SizeTypeFactory.of(roundLot),
					Currency.USD.INSTANCE
			)).as(as)
			  .isInstanceOf(IllegalArgumentException.class)
			  .hasMessage("Round lot must be positive");
		}

		private static Stream<Arguments> rejectsConstructionWithAnIllegalArgumentExceptionCases()
		{
			return Stream.of(
					Arguments.of("USD cash equity terms with zero round lot", 0L),
					Arguments.of("USD cash equity terms with negative round lot", -100L)
			);
		}
	}
}
