package de.gupta.metis.core.instrument.domain.symbol;

import de.gupta.metis.core.instrument.domain.venue.Venue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VenueSymbol")
final class VenueSymbolTest
{
	@Nested
	@DisplayName("when created from a venue and symbol string")
	final class WhenCreatedFromAVenueAndSymbolString
	{
		@Test
		@DisplayName("binds the venue and symbol together in its string representation")
		void bindsTheVenueAndSymbolTogetherInItsStringRepresentation()
		{
			var venueSymbol = VenueSymbol.of(Venue.NASDAQ, "msft");

			assertThat(venueSymbol.toString()).as("string representation of %s", venueSymbol).isEqualTo("NASDAQ:MSFT");
		}
	}
}