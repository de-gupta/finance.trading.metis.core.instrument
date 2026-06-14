package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.EquityProduct;
import de.gupta.metis.core.instrument.domain.product.ProductId;
import de.gupta.metis.core.instrument.domain.symbol.Symbol;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EquityListing#nasdaq")
final class EquityListingTest
{
	@Nested
	@DisplayName("when creating a NASDAQ listing")
	final class WhenCreatingANasdaqListing
	{
		@Test
		@DisplayName("uses venue-scoped symbol and default cash equity terms")
		void usesVenueScopedSymbolAndDefaultCashEquityTerms()
		{
			var product = EquityProduct.commonStock(new ProductId("product:aapl"), "Apple Inc.");

			var listing = EquityListing.nasdaq(
					new InstrumentId("instrument:xnas:aapl"),
					product,
					"aapl",
					ListingStatus.ACTIVE
			);

			assertThat(listing).as("nasdaq listing for %s", product).satisfies(l ->
			{
				assertThat(l.product()).as("product").isEqualTo(product);
				assertThat(l.venue()).as("venue").isEqualTo(Venue.NASDAQ);
				assertThat(l.symbol()).as("symbol").isEqualTo(new Symbol("AAPL"));
				assertThat(l.venueSymbol()).as("venue symbol").isEqualTo(VenueSymbol.of(Venue.NASDAQ, "AAPL"));
				assertThat(l.tradingTerms().settlementCurrency()).as("settlement currency")
				                                                 .isEqualTo(Currency.USD.INSTANCE);
				assertThat(l.tradingTerms().roundLot().value().toString()).as("round lot").isEqualTo("100");
				assertThat(l.tradingTerms().sizeConvention()).as("size convention")
				                                             .isEqualTo(SizeQuotingConvention.units(0));
				assertThat(l.tradingTerms().priceConvention()).as("price convention")
				                                              .isEqualTo(PriceQuotingConvention.currency(
																	  Currency.USD.INSTANCE));
			});
		}
	}
}