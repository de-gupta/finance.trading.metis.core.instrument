package de.gupta.metis.core.instrument.domain.identifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EquityListingIdentifiers")
final class EquityListingIdentifiersTest
{
	@Nested
	@DisplayName("when empty")
	final class WhenEmpty
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("returnsNoIdentifierForAnySchemeCases")
		@DisplayName("returns no identifier for any scheme")
		void returnsNoIdentifierForAnyScheme(final String as, final EquityListingIdentifierScheme scheme)
		{
			var identifiers = EquityListingIdentifiers.empty();

			assertThat(identifiers.find(scheme)).as(as).isEmpty();
		}

		private static Stream<Arguments> returnsNoIdentifierForAnySchemeCases()
		{
			return Stream.of(
					Arguments.of("Composite FIGI lookup on empty identifiers",
							EquityListingIdentifierScheme.COMPOSITE_FIGI),
					Arguments.of("Share-class FIGI lookup on empty identifiers",
							EquityListingIdentifierScheme.SHARE_CLASS_FIGI)
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
		void returnsTheStoredIdentifierByScheme(final String as, final EquityListingIdentifierScheme scheme,
		                                        final String expectedValue)
		{
			var identifiers = EquityListingIdentifiers.of(Map.of(
					EquityListingIdentifierScheme.COMPOSITE_FIGI, new IdentifierValue("bbg000b9xry4"),
					EquityListingIdentifierScheme.SHARE_CLASS_FIGI, new IdentifierValue("bbg001s5n8v8")
			));

			assertThat(identifiers.find(scheme))
					.as(as)
					.hasValueSatisfying(v -> assertThat(v.value()).as("%s value", as).isEqualTo(expectedValue));
		}

		private static Stream<Arguments> returnsTheStoredIdentifierBySchemeCases()
		{
			return Stream.of(
					Arguments.of("Composite FIGI lookup", EquityListingIdentifierScheme.COMPOSITE_FIGI,
							"BBG000B9XRY4"),
					Arguments.of("Share-class FIGI lookup", EquityListingIdentifierScheme.SHARE_CLASS_FIGI,
							"BBG001S5N8V8")
			);
		}
	}
}
