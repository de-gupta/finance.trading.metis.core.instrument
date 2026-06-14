package de.gupta.metis.core.instrument.domain.equity;

import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;
import de.gupta.metis.core.instrument.domain.instrument.InstrumentId;
import de.gupta.metis.core.instrument.domain.instrument.ListingDetails;
import de.gupta.metis.core.instrument.domain.instrument.ListingStatus;
import de.gupta.metis.core.instrument.domain.product.CountryCode;
import de.gupta.metis.core.instrument.domain.product.ProductId;
import de.gupta.metis.core.instrument.domain.symbol.Symbol;
import de.gupta.metis.core.instrument.domain.symbol.VenueSymbol;
import de.gupta.metis.core.instrument.domain.terms.TradingTerms;
import de.gupta.metis.core.instrument.domain.venue.Venue;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.PriceQuotingConvention;
import de.gupta.metis.core.types.quoting.SizeQuotingConvention;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("EquityListing#nasdaq")
final class EquityListingTest
{
	@Nested
	@DisplayName("when creating a NASDAQ listing")
	final class WhenCreatingANasdaqListing
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("usesVenueScopedSymbolAndDefaultCashEquityTermsCases")
		@DisplayName("uses venue-scoped symbol and default cash equity terms")
		void usesVenueScopedSymbolAndDefaultCashEquityTerms(final String as, final EquityProduct product,
		                                                    final InstrumentId instrumentId, final String inputSymbol,
		                                                    final ListingStatus status)
		{
			var listing = EquityListing.nasdaq(instrumentId, product, inputSymbol, status);

			assertSoftly(softly ->
			{
				softly.assertThat(listing.product()).as("%s product", as).isEqualTo(product);
				softly.assertThat(listing.venue()).as("%s venue", as).isEqualTo(Venue.NASDAQ);
				softly.assertThat(listing.symbol()).as("%s symbol", as)
				      .isEqualTo(new Symbol(inputSymbol.toUpperCase()));
				softly.assertThat(listing.venueSymbol()).as("%s venue symbol", as)
				      .isEqualTo(VenueSymbol.of(Venue.NASDAQ, inputSymbol.toUpperCase()));
				softly.assertThat(listing.tradingTerms().settlementCurrency()).as("%s settlement currency", as)
				      .isEqualTo(Currency.USD.INSTANCE);
				softly.assertThat(listing.tradingTerms().roundLot().value().toString()).as("%s round lot", as)
				      .isEqualTo("100");
				softly.assertThat(listing.tradingTerms().sizeConvention()).as("%s size convention", as)
				      .isEqualTo(SizeQuotingConvention.units(0));
				softly.assertThat(listing.tradingTerms().priceConvention()).as("%s price convention", as)
				      .isEqualTo(PriceQuotingConvention.currency(Currency.USD.INSTANCE));
			});
		}

		private static Stream<Arguments> usesVenueScopedSymbolAndDefaultCashEquityTermsCases()
		{
			return Stream.of(
					Arguments.of(
							"AAPL active listing",
							EquityProduct.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							new InstrumentId("instrument:xnas:aapl"),
							"aapl",
							ListingStatus.ACTIVE
					),
					Arguments.of(
							"MSFT halted listing",
							EquityProduct.commonStock(new ProductId("product:msft"), "Microsoft Corporation"),
							new InstrumentId("instrument:xnas:msft"),
							"msft",
							ListingStatus.HALTED
					)
			);
		}
	}

	@Nested
	@DisplayName("when building a listing explicitly")
	final class WhenBuildingAListingExplicitly
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("capturesExplicitIdentifierAndPrimaryListingMetadataCases")
		@DisplayName("captures explicit identifier and primary listing metadata")
		void capturesExplicitIdentifierAndPrimaryListingMetadata(final String as, final EquityProduct product,
		                                                         final InstrumentId instrumentId,
		                                                         final String inputSymbol,
		                                                         final ListingStatus status,
		                                                         final boolean primaryListing,
		                                                         final String compositeFigi)
		{
			var listing = EquityListing.builder(TradingTerms.cashEquity(Currency.USD.INSTANCE))
			                           .id(instrumentId)
			                           .product(product)
			                           .venueSymbol(Venue.NASDAQ, inputSymbol)
			                           .status(status)
			                           .primaryListing(primaryListing)
			                           .compositeFigi(compositeFigi)
			                           .build();

			assertSoftly(softly ->
			{
				softly.assertThat(listing.venue()).as("%s venue", as).isEqualTo(Venue.NASDAQ);
				softly.assertThat(listing.symbol()).as("%s symbol", as)
				      .isEqualTo(new Symbol(inputSymbol.toUpperCase()));
				softly.assertThat(listing.status()).as("%s listing status", as).isEqualTo(status);
				softly.assertThat(listing.primaryListing()).as("%s primary listing flag", as).isEqualTo(primaryListing);
				softly.assertThat(listing.identifiers().find(EquityListingIdentifierScheme.COMPOSITE_FIGI))
				      .as("%s composite FIGI", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s identifier value", as)
				                                     .isEqualTo(compositeFigi.toUpperCase()));
			});
		}

		private static Stream<Arguments> capturesExplicitIdentifierAndPrimaryListingMetadataCases()
		{
			return Stream.of(
					Arguments.of(
							"Microsoft halted secondary listing",
							EquityProduct.commonStock(new ProductId("product:msft"), "Microsoft Corporation"),
							new InstrumentId("instrument:xnas:msft"),
							"msft",
							ListingStatus.HALTED,
							false,
							"bbg000bph459"
					),
					Arguments.of(
							"Apple active primary listing",
							EquityProduct.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							new InstrumentId("instrument:xnas:aapl"),
							"aapl",
							ListingStatus.ACTIVE,
							true,
							"bbg000b9xry4"
					)
			);
		}
	}

	@Nested
	@DisplayName("when using generic factories and builder shortcuts")
	final class WhenUsingGenericFactoriesAndBuilderShortcuts
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("defaultsMissingIdentifiersThroughDirectConstructionCases")
		@DisplayName("defaults missing identifiers through direct construction")
		void defaultsMissingIdentifiersThroughDirectConstruction(final String as, final EquityProduct product,
		                                                         final InstrumentId instrumentId,
		                                                         final VenueSymbol venueSymbol,
		                                                         final ListingStatus status,
		                                                         final boolean primaryListing)
		{
			var listing = new EquityListing<>(product,
					new ListingDetails<>(instrumentId, venueSymbol, null, status,
							TradingTerms.cashEquity(Currency.USD.INSTANCE), primaryListing));

			assertSoftly(softly ->
			{
				softly.assertThat(listing.identifiers()).as("%s identifiers wrapper", as)
				      .isEqualTo(EquityListingIdentifiers.empty());
				softly.assertThat(listing.identifiers().values().values()).as("%s identifier values", as).isEmpty();
				softly.assertThat(listing.primaryListing()).as("%s primary listing flag", as).isEqualTo(primaryListing);
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("usesTheGenericFactoryCases")
		@DisplayName("uses the generic listing factory")
		void usesTheGenericListingFactory(final String as, final EquityProduct product, final InstrumentId instrumentId,
		                                  final VenueSymbol venueSymbol, final EquityListingIdentifiers identifiers,
		                                  final ListingStatus status, final boolean primaryListing,
		                                  final String expectedShareClassFigi)
		{
			var listing = EquityListing.of(instrumentId, product, venueSymbol, identifiers, status,
					TradingTerms.cashEquity(Currency.USD.INSTANCE), primaryListing);

			assertSoftly(softly ->
			{
				softly.assertThat(listing.id()).as("%s id", as).isEqualTo(instrumentId);
				softly.assertThat(listing.venueSymbol()).as("%s venue symbol", as).isEqualTo(venueSymbol);
				softly.assertThat(listing.status()).as("%s status", as).isEqualTo(status);
				softly.assertThat(listing.primaryListing()).as("%s primary listing", as).isEqualTo(primaryListing);
				if (expectedShareClassFigi == null)
				{
					softly.assertThat(listing.identifiers().values().values()).as("%s identifiers", as).isEmpty();
				}
				else
				{
					softly.assertThat(listing.identifiers().find(EquityListingIdentifierScheme.SHARE_CLASS_FIGI))
					      .as("%s share-class FIGI", as)
					      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s identifier value", as)
					                                     .isEqualTo(expectedShareClassFigi));
				}
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("usesNasdaqBuilderShortcutsCases")
		@DisplayName("uses Nasdaq builder shortcuts")
		void usesNasdaqBuilderShortcuts(final String as, final EquityProduct product, final InstrumentId instrumentId,
		                                final String inputSymbol, final ListingStatus status,
		                                final boolean primaryListing, final String compositeFigi,
		                                final String shareClassFigi)
		{
			var listing = EquityListings.nasdaqBuilder(inputSymbol)
			                            .id(instrumentId)
			                            .product(product)
			                            .status(status)
			                            .primaryListing(primaryListing)
			                            .identifiers(EquityListingIdentifiers.of(Map.of(
												EquityListingIdentifierScheme.COMPOSITE_FIGI,
												new IdentifierValue(compositeFigi)
										)))
			                            .shareClassFigi(shareClassFigi)
			                            .build();

			assertSoftly(softly ->
			{
				softly.assertThat(listing.venue()).as("%s venue", as).isEqualTo(Venue.NASDAQ);
				softly.assertThat(listing.symbol()).as("%s symbol", as)
				      .isEqualTo(new Symbol(inputSymbol.toUpperCase()));
				softly.assertThat(listing.status()).as("%s status", as).isEqualTo(status);
				softly.assertThat(listing.primaryListing()).as("%s primary listing", as).isEqualTo(primaryListing);
				softly.assertThat(listing.identifiers().find(EquityListingIdentifierScheme.COMPOSITE_FIGI))
				      .as("%s composite FIGI", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s composite FIGI value", as)
				                                     .isEqualTo(compositeFigi.toUpperCase()));
				softly.assertThat(listing.identifiers().find(EquityListingIdentifierScheme.SHARE_CLASS_FIGI))
				      .as("%s share-class FIGI", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s share-class FIGI value", as)
				                                     .isEqualTo(shareClassFigi.toUpperCase()));
			});
		}

		private static Stream<Arguments> defaultsMissingIdentifiersThroughDirectConstructionCases()
		{
			return Stream.of(
					Arguments.of(
							"Apple listing without explicit identifiers",
							EquityProduct.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							new InstrumentId("instrument:xnas:aapl"),
							VenueSymbol.of(Venue.NASDAQ, "AAPL"),
							ListingStatus.ACTIVE,
							true
					),
					Arguments.of(
							"Microsoft halted listing without explicit identifiers",
							EquityProduct.commonStock(new ProductId("product:msft"), "Microsoft Corporation"),
							new InstrumentId("instrument:xnas:msft"),
							VenueSymbol.of(Venue.NASDAQ, "MSFT"),
							ListingStatus.HALTED,
							false
					)
			);
		}

		private static Stream<Arguments> usesTheGenericFactoryCases()
		{
			return Stream.of(
					Arguments.of(
							"Factory defaults null identifiers to empty",
							EquityProduct.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							new InstrumentId("instrument:xnas:aapl"),
							VenueSymbol.of(Venue.NASDAQ, "AAPL"),
							null,
							ListingStatus.ACTIVE,
							true,
							null
					),
					Arguments.of(
							"Factory preserves explicit identifiers",
							EquityProduct.commonStock(new ProductId("product:googl"), "Alphabet Inc."),
							new InstrumentId("instrument:xnas:googl"),
							VenueSymbol.of(Venue.NASDAQ, "GOOGL"),
							EquityListingIdentifiers.of(Map.of(
									EquityListingIdentifierScheme.SHARE_CLASS_FIGI, new IdentifierValue("bbg001s5v3c6")
							)),
							ListingStatus.ACTIVE,
							false,
							"BBG001S5V3C6"
					)
			);
		}

		private static Stream<Arguments> usesNasdaqBuilderShortcutsCases()
		{
			return Stream.of(
					Arguments.of(
							"Apple builder shortcut listing",
							EquityProduct.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							new InstrumentId("instrument:xnas:aapl"),
							"aapl",
							ListingStatus.ACTIVE,
							true,
							"bbg000b9xry4",
							"bbg001s5n8v8"
					),
					Arguments.of(
							"Alphabet builder shortcut listing",
							EquityProduct.commonStock(new ProductId("product:googl"), "Alphabet Inc.",
									ShareClass.CLASS_A,
									CountryCode.US),
							new InstrumentId("instrument:xnas:googl"),
							"googl",
							ListingStatus.HALTED,
							false,
							"bbg009s39jv6",
							"bbg001s5v3c6"
					)
			);
		}
	}
}
