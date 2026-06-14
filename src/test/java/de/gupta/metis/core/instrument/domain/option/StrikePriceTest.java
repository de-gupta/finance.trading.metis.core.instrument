package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.types.currency.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("StrikePrice")
final class StrikePriceTest
{
	@Nested
	@DisplayName("when creating a currency strike")
	final class WhenCreatingACurrencyStrike
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("capturesValueAndConventionCases")
		@DisplayName("captures value and convention")
		void capturesValueAndConvention(final String as, final long strikeValue, final Currency currency)
		{
			var strikePrice = StrikePrice.of(strikeValue, currency);

			assertSoftly(softly ->
			{
				softly.assertThat(strikePrice.value().value().toString()).as("%s strike value", as)
				      .isEqualTo(Long.toString(strikeValue));
				softly.assertThat(strikePrice.convention().unit().toString()).as("%s convention unit", as)
				      .contains(currency.toString());
			});
		}

		private static Stream<Arguments> capturesValueAndConventionCases()
		{
			return Stream.of(
					Arguments.of("USD equity option strike", 250L, Currency.USD.INSTANCE),
					Arguments.of("EUR equity option strike", 140L, Currency.EUR.INSTANCE)
			);
		}
	}

	@Nested
	@DisplayName("when strike is not positive")
	final class WhenStrikeIsNotPositive
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("rejectsConstructionWithAnIllegalArgumentExceptionCases")
		@DisplayName("rejects construction with an illegal argument exception")
		void rejectsConstructionWithAnIllegalArgumentException(final String as, final long strikeValue)
		{
			assertThatThrownBy(() -> StrikePrice.of(strikeValue, Currency.USD.INSTANCE))
					.as(as)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("Strike price must be positive");
		}

		private static Stream<Arguments> rejectsConstructionWithAnIllegalArgumentExceptionCases()
		{
			return Stream.of(
					Arguments.of("zero strike", 0L),
					Arguments.of("negative strike", -250L)
			);
		}
	}
}