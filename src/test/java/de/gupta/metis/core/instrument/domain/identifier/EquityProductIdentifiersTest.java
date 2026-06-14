package de.gupta.metis.core.instrument.domain.identifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EquityProductIdentifiers")
final class EquityProductIdentifiersTest
{
	@Nested
	@DisplayName("when empty")
	final class WhenEmpty
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsNoIdentifierForAnySchemeCases")
		@DisplayName("returns no identifier for any scheme")
		void returnsNoIdentifierForAnyScheme(final String as, final EquityProductIdentifierScheme scheme)
		{
			var identifiers = EquityProductIdentifiers.empty();

			assertThat(identifiers.find(scheme)).as(as).isEmpty();
		}

		private static Stream<Arguments> returnsNoIdentifierForAnySchemeCases()
		{
			return Stream.of(
					Arguments.of("ISIN lookup on empty identifiers", EquityProductIdentifierScheme.ISIN),
					Arguments.of("CUSIP lookup on empty identifiers", EquityProductIdentifierScheme.CUSIP)
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
		void returnsTheStoredIdentifierByScheme(final String as, final EquityProductIdentifierScheme scheme,
		                                        final String expectedValue)
		{
			var identifiers = EquityProductIdentifiers.of(Map.of(
					EquityProductIdentifierScheme.ISIN, new IdentifierValue("us0378331005"),
					EquityProductIdentifierScheme.CUSIP, new IdentifierValue("037833100")
			));

			assertThat(identifiers.find(scheme))
					.as(as)
					.hasValueSatisfying(v -> assertThat(v.value()).as("%s value", as).isEqualTo(expectedValue));
		}

		private static Stream<Arguments> returnsTheStoredIdentifierBySchemeCases()
		{
			return Stream.of(
					Arguments.of("ISIN lookup", EquityProductIdentifierScheme.ISIN, "US0378331005"),
					Arguments.of("CUSIP lookup", EquityProductIdentifierScheme.CUSIP, "037833100")
			);
		}
	}
}
