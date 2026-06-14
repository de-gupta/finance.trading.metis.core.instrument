package de.gupta.metis.core.instrument.domain.instrument;

import de.gupta.metis.core.instrument.domain.product.EquityProduct;
import de.gupta.metis.core.instrument.domain.product.ProductId;
import de.gupta.metis.core.instrument.domain.symbol.Symbol;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.exception.IncompatibleInputException;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import de.gupta.metis.core.types.size.SizeTypeFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquityListingTest
{
	@Test
	void nasdaqListingUsesVenueScopedSymbolAndDefaultCashEquityTerms()
	{
		final var product = EquityProduct.commonStock(new ProductId("product:aapl"), "Apple Inc.");
		final var listing = EquityListing.nasdaq(
				new InstrumentId("instrument:xnas:aapl"),
				product,
				"aapl",
				ListingStatus.ACTIVE
		);

		assertThat(listing.product()).isEqualTo(product);
		assertThat(listing.venue()).isEqualTo(Venue.NASDAQ);
		assertThat(listing.symbol()).isEqualTo(new Symbol("AAPL"));
		assertThat(listing.venueSymbol()).isEqualTo(VenueSymbol.of(Venue.NASDAQ, "AAPL"));
		assertThat(listing.tradingTerms().settlementCurrency()).isEqualTo(Currency.USD.INSTANCE);
		assertThat(listing.tradingTerms().roundLot().value().toString()).isEqualTo("100");
		assertThat(listing.tradingTerms().sizeConvention()).isEqualTo(SizeQuotingConvention.units(0));
		assertThat(listing.tradingTerms().priceConvention()).isEqualTo(
				PriceQuotingConvention.currency(Currency.USD.INSTANCE));
	}

	@Test
	void venueSymbolKeepsVenueAndSymbolBoundTogether()
	{
		assertThat(VenueSymbol.of(Venue.NASDAQ, "msft").toString()).isEqualTo("NASDAQ:MSFT");
	}

	@Test
	void tradingTermsRejectMismatchedSettlementCurrency()
	{
		assertThatThrownBy(() -> TradingTerms.of(
				PriceQuotingConvention.currency(Currency.USD.INSTANCE),
				SizeQuotingConvention.units(0),
				SizeTypeFactory.of(100),
				Currency.EUR.INSTANCE
		)).isInstanceOf(IncompatibleInputException.class);
	}
}