package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OptionListingIdentifiers")
final class OptionListingIdentifiersTest
{
	@Nested
	@DisplayName("when empty")
	final class WhenEmpty
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsNoIdentifierForAnySchemeCases")
		@DisplayName("returns no identifier for any scheme")
		void returnsNoIdentifierForAnyScheme(final String as, final OptionListingIdentifierScheme scheme)
		{
			var identifiers = OptionListingIdentifiers.empty();

			assertThat(identifiers.find(scheme)).as(as).isEmpty();
		}

		private static Stream<Arguments> returnsNoIdentifierForAnySchemeCases()
		{
			return Stream.of(
					Arguments.of("OPRA lookup on empty identifiers", OptionListingIdentifierScheme.OPRA),
					Arguments.of("Composite FIGI lookup on empty identifiers",
							OptionListingIdentifierScheme.COMPOSITE_FIGI)
			);
		}
	}

	@Nested
	@DisplayName("when created from a scheme-value map")
	final class WhenCreatedFromASchemeValueMap
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsTheStoredIdentifierBySchemeCases")
		@DisplayName("returns the stored identifier by scheme")
		void returnsTheStoredIdentifierByScheme(final String as, final OptionListingIdentifierScheme scheme,
		                                        final String expectedValue)
		{
			var identifiers = OptionListingIdentifiers.of(Map.of(
					OptionListingIdentifierScheme.OPRA, new IdentifierValue("aapl  260116c00250000"),
					OptionListingIdentifierScheme.EXCHANGE_FIGI, new IdentifierValue("bbg00optionx01")
			));

			assertThat(identifiers.find(scheme))
					.as(as)
					.hasValueSatisfying(v -> assertThat(v.value()).as("%s value", as).isEqualTo(expectedValue));
		}

		private static Stream<Arguments> returnsTheStoredIdentifierBySchemeCases()
		{
			return Stream.of(
					Arguments.of("OPRA lookup", OptionListingIdentifierScheme.OPRA, "AAPL  260116C00250000"),
					Arguments.of("Exchange FIGI lookup", OptionListingIdentifierScheme.EXCHANGE_FIGI, "BBG00OPTIONX01")
			);
		}
	}
}