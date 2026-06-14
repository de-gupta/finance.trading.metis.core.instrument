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

@DisplayName("OptionProductIdentifiers")
final class OptionProductIdentifiersTest
{
	@Nested
	@DisplayName("when empty")
	final class WhenEmpty
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsNoIdentifierForAnySchemeCases")
		@DisplayName("returns no identifier for any scheme")
		void returnsNoIdentifierForAnyScheme(final String as, final OptionProductIdentifierScheme scheme)
		{
			var identifiers = OptionProductIdentifiers.empty();

			assertThat(identifiers.find(scheme)).as(as).isEmpty();
		}

		private static Stream<Arguments> returnsNoIdentifierForAnySchemeCases()
		{
			return Stream.of(
					Arguments.of("OSI lookup on empty identifiers", OptionProductIdentifierScheme.OSI),
					Arguments.of("OCC series key lookup on empty identifiers",
							OptionProductIdentifierScheme.OCC_SERIES_KEY)
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
		void returnsTheStoredIdentifierByScheme(final String as, final OptionProductIdentifierScheme scheme,
		                                        final String expectedValue)
		{
			var identifiers = OptionProductIdentifiers.of(Map.of(
					OptionProductIdentifierScheme.OSI, new IdentifierValue("aapl  260116c00250000"),
					OptionProductIdentifierScheme.OCC_SERIES_KEY, new IdentifierValue("AAPL260116C250")
			));

			assertThat(identifiers.find(scheme))
					.as(as)
					.hasValueSatisfying(v -> assertThat(v.value()).as("%s value", as).isEqualTo(expectedValue));
		}

		private static Stream<Arguments> returnsTheStoredIdentifierBySchemeCases()
		{
			return Stream.of(
					Arguments.of("OSI lookup", OptionProductIdentifierScheme.OSI, "AAPL  260116C00250000"),
					Arguments.of("OCC series key lookup", OptionProductIdentifierScheme.OCC_SERIES_KEY,
							"AAPL260116C250")
			);
		}
	}
}