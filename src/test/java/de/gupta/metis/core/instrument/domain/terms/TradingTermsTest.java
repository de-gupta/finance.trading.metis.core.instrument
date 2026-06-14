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
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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

	@Nested
	@DisplayName("when creating listed option terms")
	final class WhenCreatingListedOptionTerms
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("usesContractSizingAndSingleContractRoundLotCases")
		@DisplayName("uses contract sizing and single-contract round lot")
		void usesContractSizingAndSingleContractRoundLot(final String as, final Currency settlementCurrency)
		{
			var tradingTerms = TradingTerms.listedOption(settlementCurrency);

			assertSoftly(softly ->
			{
				softly.assertThat(tradingTerms.settlementCurrency()).as("%s settlement currency", as)
				      .isEqualTo(settlementCurrency);
				softly.assertThat(tradingTerms.priceConvention()).as("%s price convention", as)
				      .isEqualTo(PriceQuotingConvention.currency(settlementCurrency));
				softly.assertThat(tradingTerms.sizeConvention()).as("%s size convention", as)
				      .isEqualTo(SizeQuotingConvention.contracts(0));
				softly.assertThat(tradingTerms.roundLot().value().toString()).as("%s round lot", as).isEqualTo("1");
			});
		}

		private static Stream<Arguments> usesContractSizingAndSingleContractRoundLotCases()
		{
			return Stream.of(
					Arguments.of("USD listed option terms", Currency.USD.INSTANCE),
					Arguments.of("EUR listed option terms", Currency.EUR.INSTANCE)
			);
		}
	}
}