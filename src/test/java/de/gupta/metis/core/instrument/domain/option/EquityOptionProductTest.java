package de.gupta.metis.core.instrument.domain.option;

import de.gupta.metis.core.instrument.domain.equity.EquityProduct;
import de.gupta.metis.core.instrument.domain.equity.EquityProducts;
import de.gupta.metis.core.instrument.domain.equity.EquitySecurityType;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;
import de.gupta.metis.core.instrument.domain.product.ProductId;
import de.gupta.metis.core.types.currency.Currency;
import de.gupta.metis.core.types.quoting.CurrencyPriceUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("EquityOptionProduct")
final class EquityOptionProductTest
{
	@Nested
	@DisplayName("when creating a standard contract through the happy-path factory")
	final class WhenCreatingAStandardContractThroughTheHappyPathFactory
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("usesStandardOptionDefaultsCases")
		@DisplayName("uses standard option defaults")
		void usesStandardOptionDefaults(final String as, final ProductId productId, final EquityProduct underlying,
		                                final OptionRight right, final LocalDate expiryDate, final long strikeValue,
		                                final OptionExerciseStyle exerciseStyle)
		{
			var product = EquityOptionProducts.standardContract(productId, underlying, right, expiryDate,
					StrikePrice.of(strikeValue, Currency.USD.INSTANCE), exerciseStyle);

			assertSoftly(softly ->
			{
				softly.assertThat(product.id()).as("%s id", as).isEqualTo(productId);
				softly.assertThat(product.underlying()).as("%s underlying", as).isEqualTo(underlying);
				softly.assertThat(product.right()).as("%s right", as).isEqualTo(right);
				softly.assertThat(product.expiryDate()).as("%s expiry", as).isEqualTo(expiryDate);
				softly.assertThat(product.exerciseStyle()).as("%s exercise style", as).isEqualTo(exerciseStyle);
				softly.assertThat(product.settlementStyle()).as("%s settlement style", as)
				      .isEqualTo(OptionSettlementStyle.PHYSICAL_DELIVERY);
				softly.assertThat(product.contractSize().underlyingUnits().value().toString())
				      .as("%s contract size", as)
				      .isEqualTo("100");
				softly.assertThat(product.identifiers().values().values()).as("%s identifiers", as).isEmpty();
			});
		}

		private static Stream<Arguments> usesStandardOptionDefaultsCases()
		{
			return Stream.of(
					Arguments.of("AAPL American call", new ProductId("product:aapl-20260116-c-250"),
							EquityProducts.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							OptionRight.CALL, LocalDate.of(2026, 1, 16), 250L, OptionExerciseStyle.AMERICAN),
					Arguments.of("SAP Bermudan put", new ProductId("product:sap-20260320-p-140"),
							EquityProducts.commonStock(new ProductId("product:sap"), "SAP SE",
									new de.gupta.metis.core.instrument.domain.product.CountryCode("DE")),
							OptionRight.PUT, LocalDate.of(2026, 3, 20), 140L, OptionExerciseStyle.BERMUDAN)
			);
		}
	}

	@Nested
	@DisplayName("when building an option product explicitly")
	final class WhenBuildingAnOptionProductExplicitly
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("capturesExplicitContractTermsAndIdentifiersCases")
		@DisplayName("captures explicit contract terms and identifiers")
		void capturesExplicitContractTermsAndIdentifiers(final String as, final ProductId productId,
		                                                 final EquityProduct underlying, final OptionRight right,
		                                                 final LocalDate expiryDate, final long strikeValue,
		                                                 final OptionExerciseStyle exerciseStyle,
		                                                 final OptionSettlementStyle settlementStyle,
		                                                 final long contractSize, final String osi,
		                                                 final String occSeriesKey)
		{
			var product = EquityOptionProduct.<CurrencyPriceUnit<Currency.USD>>builder()
			                                 .id(productId)
			                                 .underlying(underlying)
			                                 .right(right)
			                                 .expiryDate(expiryDate)
			                                 .strikePrice(StrikePrice.of(strikeValue, Currency.USD.INSTANCE))
			                                 .exerciseStyle(exerciseStyle)
			                                 .settlementStyle(settlementStyle)
			                                 .contractSize(ContractSize.of(contractSize))
			                                 .osi(osi)
			                                 .occSeriesKey(occSeriesKey)
			                                 .build();

			assertSoftly(softly ->
			{
				softly.assertThat(product.underlying().securityType()).as("%s underlying security type", as)
				      .isEqualTo(EquitySecurityType.COMMON_STOCK);
				softly.assertThat(product.right()).as("%s right", as).isEqualTo(right);
				softly.assertThat(product.exerciseStyle()).as("%s exercise style", as).isEqualTo(exerciseStyle);
				softly.assertThat(product.settlementStyle()).as("%s settlement style", as).isEqualTo(settlementStyle);
				softly.assertThat(product.contractSize().underlyingUnits().value().toString())
				      .as("%s contract size", as)
				      .isEqualTo(Long.toString(contractSize));
				softly.assertThat(product.identifiers().find(OptionProductIdentifierScheme.OSI))
				      .as("%s OSI", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s OSI value", as)
				                                     .isEqualTo(osi.toUpperCase()));
				softly.assertThat(product.identifiers().find(OptionProductIdentifierScheme.OCC_SERIES_KEY))
				      .as("%s OCC series key", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s OCC series key value", as)
				                                     .isEqualTo(occSeriesKey.toUpperCase()));
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("defaultsMissingIdentifiersToEmptyCases")
		@DisplayName("defaults missing identifiers to empty")
		void defaultsMissingIdentifiersToEmpty(final String as, final ProductId productId,
		                                       final EquityProduct underlying,
		                                       final OptionRight right, final LocalDate expiryDate,
		                                       final long strikeValue)
		{
			var product = new EquityOptionProduct<>(productId, underlying, right, expiryDate,
					StrikePrice.of(strikeValue, Currency.USD.INSTANCE), OptionExerciseStyle.AMERICAN,
					OptionSettlementStyle.PHYSICAL_DELIVERY, ContractSize.standardEquityOption(), null);

			assertSoftly(softly ->
			{
				softly.assertThat(product.id()).as("%s id", as).isEqualTo(productId);
				softly.assertThat(product.identifiers()).as("%s identifiers wrapper", as)
				      .isEqualTo(OptionProductIdentifiers.empty());
				softly.assertThat(product.identifiers().values().values()).as("%s identifier values", as).isEmpty();
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("replacesBuilderStateCases")
		@DisplayName("replaces builder state with explicit identifier sets")
		void replacesBuilderStateWithExplicitIdentifierSets(final String as, final ProductId productId,
		                                                    final EquityProduct underlying,
		                                                    final OptionProductIdentifiers identifiers,
		                                                    final String expectedOsi,
		                                                    final String expectedIsin)
		{
			var product = EquityOptionProduct.<CurrencyPriceUnit<Currency.USD>>builder()
			                                 .id(productId)
			                                 .underlying(underlying)
			                                 .right(OptionRight.CALL)
			                                 .expiryDate(LocalDate.of(2026, 1, 16))
			                                 .strikePrice(StrikePrice.of(250L, Currency.USD.INSTANCE))
			                                 .osi("stale")
			                                 .identifiers(identifiers)
			                                 .build();

			assertSoftly(softly ->
			{
				softly.assertThat(product.identifiers().find(OptionProductIdentifierScheme.OSI))
				      .as("%s OSI", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s OSI value", as)
				                                     .isEqualTo(expectedOsi));
				softly.assertThat(product.identifiers().find(OptionProductIdentifierScheme.ISIN))
				      .as("%s ISIN", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s ISIN value", as)
				                                     .isEqualTo(expectedIsin));
			});
		}

		private static Stream<Arguments> capturesExplicitContractTermsAndIdentifiersCases()
		{
			return Stream.of(
					Arguments.of("AAPL cash-settled European put", new ProductId("product:aapl-20260320-p-220"),
							EquityProducts.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							OptionRight.PUT, LocalDate.of(2026, 3, 20), 220L, OptionExerciseStyle.EUROPEAN,
							OptionSettlementStyle.CASH_SETTLED, 100L, "aapl  260320p00220000", "aapl260320p220"),
					Arguments.of("MSFT physical American call", new ProductId("product:msft-20260116-c-500"),
							EquityProducts.commonStock(new ProductId("product:msft"), "Microsoft Corporation"),
							OptionRight.CALL, LocalDate.of(2026, 1, 16), 500L, OptionExerciseStyle.AMERICAN,
							OptionSettlementStyle.PHYSICAL_DELIVERY, 100L, "msft  260116c00500000", "msft260116c500")
			);
		}

		private static Stream<Arguments> defaultsMissingIdentifiersToEmptyCases()
		{
			return Stream.of(
					Arguments.of("AAPL option without identifiers", new ProductId("product:aapl-20260116-c-250"),
							EquityProducts.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							OptionRight.CALL, LocalDate.of(2026, 1, 16), 250L),
					Arguments.of("GOOGL option without identifiers", new ProductId("product:googl-20260320-p-150"),
							EquityProducts.commonStock(new ProductId("product:googl"), "Alphabet Inc."),
							OptionRight.PUT, LocalDate.of(2026, 3, 20), 150L)
			);
		}

		private static Stream<Arguments> replacesBuilderStateCases()
		{
			return Stream.of(
					Arguments.of(
							"AAPL identifiers replace prior builder state",
							new ProductId("product:aapl-20260116-c-250"),
							EquityProducts.commonStock(new ProductId("product:aapl"), "Apple Inc."),
							OptionProductIdentifiers.of(Map.of(
									OptionProductIdentifierScheme.OSI, new IdentifierValue("aapl  260116c00250000"),
									OptionProductIdentifierScheme.ISIN, new IdentifierValue("us0378331005opt")
							)),
							"AAPL  260116C00250000",
							"US0378331005OPT"
					),
					Arguments.of(
							"MSFT identifiers replace prior builder state",
							new ProductId("product:msft-20260116-c-500"),
							EquityProducts.commonStock(new ProductId("product:msft"), "Microsoft Corporation"),
							OptionProductIdentifiers.of(Map.of(
									OptionProductIdentifierScheme.OSI, new IdentifierValue("msft  260116c00500000"),
									OptionProductIdentifierScheme.ISIN, new IdentifierValue("us5949181045opt")
							)),
							"MSFT  260116C00500000",
							"US5949181045OPT"
					)
			);
		}
	}
}