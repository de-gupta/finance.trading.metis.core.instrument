package de.gupta.metis.core.instrument.domain.symbol;

import de.gupta.metis.core.instrument.domain.venue.Venue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VenueSymbol")
final class VenueSymbolTest
{
	@Nested
	@DisplayName("when created from a venue and symbol string")
	final class WhenCreatedFromAVenueAndSymbolString
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("bindsTheVenueAndSymbolTogetherInItsStringRepresentationCases")
		@DisplayName("binds the venue and symbol together in its string representation")
		void bindsTheVenueAndSymbolTogetherInItsStringRepresentation(final String as, final Venue venue,
		                                                             final String input,
		                                                             final String expectedRepresentation)
		{
			var venueSymbol = VenueSymbol.of(venue, input);

			assertThat(venueSymbol.toString()).as(as).isEqualTo(expectedRepresentation);
		}

		private static Stream<Arguments> bindsTheVenueAndSymbolTogetherInItsStringRepresentationCases()
		{
			return Stream.of(
					Arguments.of("NASDAQ with lowercase symbol", Venue.NASDAQ, "msft", "NASDAQ:MSFT"),
					Arguments.of("NYSE with uppercase symbol", Venue.NYSE, "AAPL", "NYSE:AAPL"),
					Arguments.of("NYSE Arca with mixed-case symbol", Venue.NYSE_ARCA, "Spy", "NYSE_ARCA:SPY")
			);
		}
	}
}