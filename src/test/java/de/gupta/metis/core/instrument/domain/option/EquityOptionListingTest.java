package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.instrument.domain.equity.EquityProducts;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;
import de.gupta.metis.core.instrument.domain.instrument.InstrumentId;
import de.gupta.metis.core.instrument.domain.instrument.ListingDetails;
import de.gupta.metis.core.instrument.domain.instrument.ListingStatus;
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

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("EquityOptionListing#usdListed")
final class EquityOptionListingTest
{
	private static final Venue CBOE = new Venue("CBOE", "Chicago Board Options Exchange", "XCBO",
			de.gupta.metis.core.instrument.domain.product.CountryCode.US);

	private static EquityOptionProduct<?> standardAaplCall()
	{
		return EquityOptionProducts.standardContract(
				new ProductId("product:aapl-20260116-c-250"),
				EquityProducts.commonStock(new ProductId("product:aapl"), "Apple Inc."),
				OptionRight.CALL,
				LocalDate.of(2026, 1, 16),
				StrikePrice.of(250L, Currency.USD.INSTANCE),
				OptionExerciseStyle.AMERICAN
		);
	}

	private static EquityOptionProduct<?> standardMsftPut()
	{
		return EquityOptionProducts.standardContract(
				new ProductId("product:msft-20260320-p-400"),
				EquityProducts.commonStock(new ProductId("product:msft"), "Microsoft Corporation"),
				OptionRight.PUT,
				LocalDate.of(2026, 3, 20),
				StrikePrice.of(400L, Currency.USD.INSTANCE),
				OptionExerciseStyle.EUROPEAN
		);
	}

	@Nested
	@DisplayName("when creating a USD-listed option")
	final class WhenCreatingAUsdListedOption
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("usesVenueScopedSymbolAndDefaultListedOptionTermsCases")
		@DisplayName("uses venue-scoped symbol and default listed-option terms")
		void usesVenueScopedSymbolAndDefaultListedOptionTerms(final String as, final EquityOptionProduct<?> product,
		                                                      final InstrumentId instrumentId,
		                                                      final VenueSymbol venueSymbol,
		                                                      final ListingStatus status)
		{
			var listing = EquityOptionListings.usdListed(instrumentId, product, venueSymbol, status);

			assertSoftly(softly ->
			{
				softly.assertThat(listing.product()).as("%s product", as).isEqualTo(product);
				softly.assertThat(listing.venue()).as("%s venue", as).isEqualTo(venueSymbol.venue());
				softly.assertThat(listing.symbol()).as("%s symbol", as).isEqualTo(venueSymbol.symbol());
				softly.assertThat(listing.tradingTerms().settlementCurrency()).as("%s settlement currency", as)
				      .isEqualTo(Currency.USD.INSTANCE);
				softly.assertThat(listing.tradingTerms().roundLot().value().toString()).as("%s round lot", as)
				      .isEqualTo("1");
				softly.assertThat(listing.tradingTerms().sizeConvention()).as("%s size convention", as)
				      .isEqualTo(SizeQuotingConvention.contracts(0));
				softly.assertThat(listing.tradingTerms().priceConvention()).as("%s price convention", as)
				      .isEqualTo(PriceQuotingConvention.currency(Currency.USD.INSTANCE));
				softly.assertThat(listing.identifiers().values().values()).as("%s identifiers", as).isEmpty();
				softly.assertThat(listing.primaryListing()).as("%s primary listing", as).isTrue();
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("forwardsThroughTheRecordUsdFactoryCases")
		@DisplayName("forwards through the record USD factory")
		void forwardsThroughTheRecordUsdFactory(final String as, final EquityOptionProduct<?> product,
		                                        final InstrumentId instrumentId, final VenueSymbol venueSymbol,
		                                        final ListingStatus status)
		{
			var listing = EquityOptionListing.usdListed(instrumentId, product, venueSymbol, status);

			assertSoftly(softly ->
			{
				softly.assertThat(listing.id()).as("%s id", as).isEqualTo(instrumentId);
				softly.assertThat(listing.product()).as("%s product", as).isEqualTo(product);
				softly.assertThat(listing.venueSymbol()).as("%s venue symbol", as).isEqualTo(venueSymbol);
				softly.assertThat(listing.status()).as("%s status", as).isEqualTo(status);
				softly.assertThat(listing.tradingTerms()).as("%s trading terms", as)
				      .isEqualTo(TradingTerms.listedOption(Currency.USD.INSTANCE));
			});
		}

		private static Stream<Arguments> usesVenueScopedSymbolAndDefaultListedOptionTermsCases()
		{
			return Stream.of(
					Arguments.of(
							"AAPL active option listing",
							standardAaplCall(),
							new InstrumentId("instrument:xcbo:aapl-20260116-c-250"),
							VenueSymbol.of(CBOE, "AAPL  260116C00250000"),
							ListingStatus.ACTIVE
					),
					Arguments.of(
							"MSFT halted option listing",
							standardMsftPut(),
							new InstrumentId("instrument:xcbo:msft-20260320-p-400"),
							VenueSymbol.of(CBOE, "MSFT  260320P00400000"),
							ListingStatus.HALTED
					)
			);
		}

		private static Stream<Arguments> forwardsThroughTheRecordUsdFactoryCases()
		{
			return Stream.of(
					Arguments.of(
							"AAPL record USD factory call",
							standardAaplCall(),
							new InstrumentId("instrument:xcbo:aapl-20260116-c-250"),
							VenueSymbol.of(CBOE, "AAPL  260116C00250000"),
							ListingStatus.ACTIVE
					),
					Arguments.of(
							"MSFT record USD factory call",
							standardMsftPut(),
							new InstrumentId("instrument:xcbo:msft-20260320-p-400"),
							VenueSymbol.of(CBOE, "MSFT  260320P00400000"),
							ListingStatus.HALTED
					)
			);
		}
	}

	@Nested
	@DisplayName("when building an option listing explicitly")
	final class WhenBuildingAnOptionListingExplicitly
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("capturesExplicitIdentifierAndPrimaryListingMetadataCases")
		@DisplayName("captures explicit identifier and primary listing metadata")
		void capturesExplicitIdentifierAndPrimaryListingMetadata(final String as, final EquityOptionProduct<?> product,
		                                                         final InstrumentId instrumentId,
		                                                         final String inputSymbol,
		                                                         final ListingStatus status,
		                                                         final boolean primaryListing,
		                                                         final String opra)
		{
			var listing = EquityOptionListing.builder(TradingTerms.listedOption(Currency.USD.INSTANCE))
			                                 .id(instrumentId)
			                                 .product(product)
			                                 .venueSymbol(CBOE, inputSymbol)
			                                 .status(status)
			                                 .primaryListing(primaryListing)
			                                 .opra(opra)
			                                 .build();

			assertSoftly(softly ->
			{
				softly.assertThat(listing.venue()).as("%s venue", as).isEqualTo(CBOE);
				softly.assertThat(listing.symbol()).as("%s symbol", as)
				      .isEqualTo(new Symbol(inputSymbol.toUpperCase()));
				softly.assertThat(listing.status()).as("%s status", as).isEqualTo(status);
				softly.assertThat(listing.primaryListing()).as("%s primary listing", as).isEqualTo(primaryListing);
				softly.assertThat(listing.identifiers().find(OptionListingIdentifierScheme.OPRA))
				      .as("%s OPRA", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s OPRA value", as)
				                                     .isEqualTo(opra.toUpperCase()));
			});
		}

		private static Stream<Arguments> capturesExplicitIdentifierAndPrimaryListingMetadataCases()
		{
			return Stream.of(
					Arguments.of("AAPL active primary option listing", standardAaplCall(),
							new InstrumentId("instrument:xcbo:aapl-20260116-c-250"),
							"AAPL  260116C00250000", ListingStatus.ACTIVE, true, "aapl  260116c00250000"),
					Arguments.of("MSFT halted secondary option listing", standardMsftPut(),
							new InstrumentId("instrument:xcbo:msft-20260320-p-400"),
							"MSFT  260320P00400000", ListingStatus.HALTED, false, "msft  260320p00400000")
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
		void defaultsMissingIdentifiersThroughDirectConstruction(final String as, final EquityOptionProduct<?> product,
		                                                         final InstrumentId instrumentId,
		                                                         final VenueSymbol venueSymbol,
		                                                         final ListingStatus status,
		                                                         final boolean primaryListing)
		{
			var listing = new EquityOptionListing<>(product,
					new ListingDetails<>(instrumentId, venueSymbol, null, status,
							TradingTerms.listedOption(Currency.USD.INSTANCE), primaryListing));

			assertSoftly(softly ->
			{
				softly.assertThat(listing.identifiers()).as("%s identifiers wrapper", as)
				      .isEqualTo(OptionListingIdentifiers.empty());
				softly.assertThat(listing.identifiers().values().values()).as("%s identifier values", as).isEmpty();
				softly.assertThat(listing.primaryListing()).as("%s primary listing flag", as).isEqualTo(primaryListing);
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("usesTheGenericListingFactoryCases")
		@DisplayName("uses the generic listing factory")
		void usesTheGenericListingFactory(final String as, final EquityOptionProduct<?> product,
		                                  final InstrumentId instrumentId, final VenueSymbol venueSymbol,
		                                  final OptionListingIdentifiers identifiers, final ListingStatus status,
		                                  final boolean primaryListing, final String expectedExchangeFigi)
		{
			var listing = EquityOptionListing.of(instrumentId, product, venueSymbol, identifiers, status,
					TradingTerms.listedOption(Currency.USD.INSTANCE), primaryListing);

			assertSoftly(softly ->
			{
				softly.assertThat(listing.id()).as("%s id", as).isEqualTo(instrumentId);
				softly.assertThat(listing.venueSymbol()).as("%s venue symbol", as).isEqualTo(venueSymbol);
				softly.assertThat(listing.status()).as("%s status", as).isEqualTo(status);
				softly.assertThat(listing.primaryListing()).as("%s primary listing", as).isEqualTo(primaryListing);
				if (expectedExchangeFigi == null)
				{
					softly.assertThat(listing.identifiers().values().values()).as("%s identifiers", as).isEmpty();
				}
				else
				{
					softly.assertThat(listing.identifiers().find(OptionListingIdentifierScheme.EXCHANGE_FIGI))
					      .as("%s exchange FIGI", as)
					      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s identifier value", as)
					                                     .isEqualTo(expectedExchangeFigi));
				}
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("usesUsdBuilderShortcutsCases")
		@DisplayName("uses USD builder shortcuts")
		void usesUsdBuilderShortcuts(final String as, final EquityOptionProduct<?> product,
		                             final InstrumentId instrumentId,
		                             final String inputSymbol, final ListingStatus status, final boolean primaryListing,
		                             final String opra, final String exchangeFigi)
		{
			var listing = EquityOptionListings.usdBuilder(CBOE, inputSymbol)
			                                  .id(instrumentId)
			                                  .product(product)
			                                  .status(status)
			                                  .primaryListing(primaryListing)
			                                  .identifiers(OptionListingIdentifiers.of(Map.of(
													  OptionListingIdentifierScheme.OPRA, new IdentifierValue(opra)
											  )))
			                                  .exchangeFigi(exchangeFigi)
			                                  .build();

			assertSoftly(softly ->
			{
				softly.assertThat(listing.venue()).as("%s venue", as).isEqualTo(CBOE);
				softly.assertThat(listing.symbol()).as("%s symbol", as)
				      .isEqualTo(new Symbol(inputSymbol.toUpperCase()));
				softly.assertThat(listing.status()).as("%s status", as).isEqualTo(status);
				softly.assertThat(listing.primaryListing()).as("%s primary listing", as).isEqualTo(primaryListing);
				softly.assertThat(listing.identifiers().find(OptionListingIdentifierScheme.OPRA))
				      .as("%s OPRA", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s OPRA value", as)
				                                     .isEqualTo(opra.toUpperCase()));
				softly.assertThat(listing.identifiers().find(OptionListingIdentifierScheme.EXCHANGE_FIGI))
				      .as("%s exchange FIGI", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s exchange FIGI value", as)
				                                     .isEqualTo(exchangeFigi.toUpperCase()));
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("capturesCompositeFigiThroughBuilderShortcutCases")
		@DisplayName("captures composite FIGI through the builder shortcut")
		void capturesCompositeFigiThroughTheBuilderShortcut(final String as, final EquityOptionProduct<?> product,
		                                                    final InstrumentId instrumentId,
		                                                    final String inputSymbol,
		                                                    final ListingStatus status,
		                                                    final String compositeFigi)
		{
			var listing = EquityOptionListings.usdBuilder()
			                                  .id(instrumentId)
			                                  .product(product)
			                                  .venueSymbol(CBOE, inputSymbol)
			                                  .status(status)
			                                  .compositeFigi(compositeFigi)
			                                  .build();

			assertSoftly(softly ->
			{
				softly.assertThat(listing.venue()).as("%s venue", as).isEqualTo(CBOE);
				softly.assertThat(listing.symbol()).as("%s symbol", as)
				      .isEqualTo(new Symbol(inputSymbol.toUpperCase()));
				softly.assertThat(listing.status()).as("%s status", as).isEqualTo(status);
				softly.assertThat(listing.identifiers().find(OptionListingIdentifierScheme.COMPOSITE_FIGI))
				      .as("%s composite FIGI", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s composite FIGI value", as)
				                                     .isEqualTo(compositeFigi.toUpperCase()));
			});
		}

		private static Stream<Arguments> defaultsMissingIdentifiersThroughDirectConstructionCases()
		{
			return Stream.of(
					Arguments.of("AAPL option listing without explicit identifiers", standardAaplCall(),
							new InstrumentId("instrument:xcbo:aapl-20260116-c-250"),
							VenueSymbol.of(CBOE, "AAPL  260116C00250000"), ListingStatus.ACTIVE, true),
					Arguments.of("MSFT option listing without explicit identifiers", standardMsftPut(),
							new InstrumentId("instrument:xcbo:msft-20260320-p-400"),
							VenueSymbol.of(CBOE, "MSFT  260320P00400000"), ListingStatus.HALTED, false)
			);
		}

		private static Stream<Arguments> usesTheGenericListingFactoryCases()
		{
			return Stream.of(
					Arguments.of("Factory defaults null identifiers to empty", standardAaplCall(),
							new InstrumentId("instrument:xcbo:aapl-20260116-c-250"),
							VenueSymbol.of(CBOE, "AAPL  260116C00250000"), null, ListingStatus.ACTIVE, true, null),
					Arguments.of("Factory preserves explicit identifiers", standardMsftPut(),
							new InstrumentId("instrument:xcbo:msft-20260320-p-400"),
							VenueSymbol.of(CBOE, "MSFT  260320P00400000"),
							OptionListingIdentifiers.of(Map.of(
									OptionListingIdentifierScheme.EXCHANGE_FIGI, new IdentifierValue("bbg00optionx02")
							)), ListingStatus.HALTED, false, "BBG00OPTIONX02")
			);
		}

		private static Stream<Arguments> usesUsdBuilderShortcutsCases()
		{
			return Stream.of(
					Arguments.of("AAPL USD builder shortcut listing", standardAaplCall(),
							new InstrumentId("instrument:xcbo:aapl-20260116-c-250"), "AAPL  260116C00250000",
							ListingStatus.ACTIVE, true, "aapl  260116c00250000", "bbg00optionx01"),
					Arguments.of("MSFT USD builder shortcut listing", standardMsftPut(),
							new InstrumentId("instrument:xcbo:msft-20260320-p-400"), "MSFT  260320P00400000",
							ListingStatus.HALTED, false, "msft  260320p00400000", "bbg00optionx02")
			);
		}

		private static Stream<Arguments> capturesCompositeFigiThroughBuilderShortcutCases()
		{
			return Stream.of(
					Arguments.of("AAPL composite FIGI builder shortcut", standardAaplCall(),
							new InstrumentId("instrument:xcbo:aapl-20260116-c-250"), "AAPL  260116C00250000",
							ListingStatus.ACTIVE, "bbg00optionc01"),
					Arguments.of("MSFT composite FIGI builder shortcut", standardMsftPut(),
							new InstrumentId("instrument:xcbo:msft-20260320-p-400"), "MSFT  260320P00400000",
							ListingStatus.HALTED, "bbg00optionc02")
			);
		}
	}
}