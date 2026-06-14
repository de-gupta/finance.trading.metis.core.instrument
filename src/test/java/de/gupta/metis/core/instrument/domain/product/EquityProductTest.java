package de.gupta.metis.core.instrument.domain.product;

import de.gupta.metis.core.instrument.domain.identifier.EquityProductIdentifierScheme;
import de.gupta.metis.core.instrument.domain.identifier.EquityProductIdentifiers;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("EquityProduct")
final class EquityProductTest
{
	@Nested
	@DisplayName("when creating a common stock through the happy-path factory")
	final class WhenCreatingACommonStockThroughTheHappyPathFactory
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("usesCommonStockDefaultsForAnEquityProductCases")
		@DisplayName("uses common stock defaults for an equity product")
		void usesCommonStockDefaultsForAnEquityProduct(final String as, final ProductId productId,
		                                               final String issuerName,
		                                               final CountryCode incorporationCountry)
		{
			var product = EquityProducts.commonStock(productId, issuerName, incorporationCountry);

			assertSoftly(softly ->
			{
				softly.assertThat(product.issuerName()).as("%s issuer name", as).isEqualTo(issuerName);
				softly.assertThat(product.securityType()).as("%s security type", as)
				      .isEqualTo(EquitySecurityType.COMMON_STOCK);
				softly.assertThat(product.incorporationCountry()).as("%s incorporation country", as)
				      .isEqualTo(incorporationCountry);
				softly.assertThat(product.shareClass()).as("%s share class", as).isEmpty();
				softly.assertThat(product.identifiers().values().values()).as("%s product identifiers", as).isEmpty();
			});
		}

		private static Stream<Arguments> usesCommonStockDefaultsForAnEquityProductCases()
		{
			return Stream.of(
					Arguments.of("US common stock", new ProductId("product:aapl"), "Apple Inc.", CountryCode.US),
					Arguments.of("non-US common stock", new ProductId("product:sap"), "SAP SE", new CountryCode("DE"))
			);
		}
	}

	@Nested
	@DisplayName("when building a product explicitly")
	final class WhenBuildingAProductExplicitly
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("capturesShareClassAndIdentifiersBySchemeCases")
		@DisplayName("captures share class and identifiers by scheme")
		void capturesShareClassAndIdentifiersByScheme(final String as, final ProductId productId,
		                                              final String issuerName, final ShareClass shareClass,
		                                              final String isin, final String cusip)
		{
			var product = EquityProduct.builder()
			                           .id(productId)
			                           .issuerName(issuerName)
			                           .shareClass(shareClass)
			                           .securityType(EquitySecurityType.COMMON_STOCK)
			                           .incorporationCountry(CountryCode.US)
			                           .isin(isin)
			                           .cusip(cusip)
			                           .build();

			assertSoftly(softly ->
			{
				softly.assertThat(product.shareClass()).as("%s share class", as).hasValue(shareClass);
				softly.assertThat(product.identifiers().find(EquityProductIdentifierScheme.ISIN))
				      .as("%s ISIN", as)
				      .hasValueSatisfying(
							  v -> softly.assertThat(v.value()).as("%s ISIN value", as).isEqualTo(isin.toUpperCase()));
				softly.assertThat(product.identifiers().find(EquityProductIdentifierScheme.CUSIP))
				      .as("%s CUSIP", as)
				      .hasValueSatisfying(v -> softly.assertThat(v.value()).as("%s CUSIP value", as)
				                                     .isEqualTo(cusip.toUpperCase()));
			});
		}

		private static Stream<Arguments> capturesShareClassAndIdentifiersBySchemeCases()
		{
			return Stream.of(
					Arguments.of("Alphabet class A", new ProductId("product:googl"), "Alphabet Inc.",
							ShareClass.CLASS_A,
							"us02079k3059", "02079k305"),
					Arguments.of("Liberty class C", new ProductId("product:fwonc"), "Formula One Group",
							ShareClass.CLASS_C,
							"us5312297550", "531229755")
			);
		}
	}

	@Nested
	@DisplayName("when using stock factories and defaults")
	final class WhenUsingStockFactoriesAndDefaults
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("usesTheProductShortcutFactoryCases")
		@DisplayName("uses the product shortcut factory")
		void usesTheProductShortcutFactory(final String as, final ProductId productId, final String issuerName,
		                                   final CountryCode incorporationCountry)
		{
			var product = EquityProduct.commonStock(productId, issuerName, incorporationCountry);

			assertSoftly(softly ->
			{
				softly.assertThat(product.id()).as("%s id", as).isEqualTo(productId);
				softly.assertThat(product.issuerName()).as("%s issuer name", as).isEqualTo(issuerName);
				softly.assertThat(product.incorporationCountry()).as("%s incorporation country", as)
				      .isEqualTo(incorporationCountry);
				softly.assertThat(product.shareClass()).as("%s share class", as).isEmpty();
				softly.assertThat(product.identifiers().values().values()).as("%s identifiers", as).isEmpty();
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("capturesShareClassThroughFactoryCases")
		@DisplayName("captures share class through the stock factory")
		void capturesShareClassThroughTheStockFactory(final String as, final ProductId productId,
		                                              final String issuerName, final ShareClass shareClass,
		                                              final CountryCode incorporationCountry)
		{
			var product = EquityProduct.commonStock(productId, issuerName, shareClass, incorporationCountry);

			assertSoftly(softly ->
			{
				softly.assertThat(product.securityType()).as("%s security type", as)
				      .isEqualTo(EquitySecurityType.COMMON_STOCK);
				softly.assertThat(product.shareClass()).as("%s share class", as).hasValue(shareClass);
				softly.assertThat(product.incorporationCountry()).as("%s incorporation country", as)
				      .isEqualTo(incorporationCountry);
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("defaultsMissingIdentifiersToEmptyCases")
		@DisplayName("defaults missing identifiers to empty")
		void defaultsMissingIdentifiersToEmpty(final String as, final ProductId productId, final String issuerName,
		                                       final Optional<ShareClass> shareClass)
		{
			var product = new EquityProduct(productId, issuerName, shareClass, EquitySecurityType.COMMON_STOCK,
					CountryCode.US, null);

			assertSoftly(softly ->
			{
				softly.assertThat(product.id()).as("%s id", as).isEqualTo(productId);
				softly.assertThat(product.identifiers()).as("%s identifiers wrapper", as)
				      .isEqualTo(EquityProductIdentifiers.empty());
				softly.assertThat(product.identifiers().values().values()).as("%s identifier values", as).isEmpty();
			});
		}

		@ParameterizedTest(name = "{0}")
		@MethodSource("replacesBuilderStateCases")
		@DisplayName("replaces builder state with explicit identifier sets")
		void replacesBuilderStateWithExplicitIdentifierSets(final String as, final ProductId productId,
		                                                    final String issuerName,
		                                                    final EquityProductIdentifiers identifiers,
		                                                    final String expectedIsin,
		                                                    final String expectedCusip)
		{
			var product = EquityProduct.builder()
			                           .id(productId)
			                           .issuerName(issuerName)
			                           .shareClass(ShareClass.CLASS_A)
			                           .noShareClass()
			                           .identifiers(identifiers)
			                           .build();

			assertSoftly(softly ->
			{
				softly.assertThat(product.shareClass()).as("%s share class", as).isEmpty();
				softly.assertThat(product.identifiers().find(EquityProductIdentifierScheme.ISIN))
				      .as("%s ISIN", as)
				      .hasValueSatisfying(
							  v -> softly.assertThat(v.value()).as("%s ISIN value", as).isEqualTo(expectedIsin));
				softly.assertThat(product.identifiers().find(EquityProductIdentifierScheme.CUSIP))
				      .as("%s CUSIP", as)
				      .hasValueSatisfying(
							  v -> softly.assertThat(v.value()).as("%s CUSIP value", as).isEqualTo(expectedCusip));
			});
		}

		private static Stream<Arguments> usesTheProductShortcutFactoryCases()
		{
			return Stream.of(
					Arguments.of("Apple with explicit US incorporation", new ProductId("product:aapl"), "Apple Inc.",
							CountryCode.US),
					Arguments.of("SAP with explicit German incorporation", new ProductId("product:sap"), "SAP SE",
							new CountryCode("DE"))
			);
		}

		private static Stream<Arguments> capturesShareClassThroughFactoryCases()
		{
			return Stream.of(
					Arguments.of("Alphabet class A factory", new ProductId("product:googl"), "Alphabet Inc.",
							ShareClass.CLASS_A, CountryCode.US),
					Arguments.of("Liberty class C factory", new ProductId("product:fwonc"), "Formula One Group",
							ShareClass.CLASS_C, CountryCode.US)
			);
		}

		private static Stream<Arguments> defaultsMissingIdentifiersToEmptyCases()
		{
			return Stream.of(
					Arguments.of("Product without share class identifiers", new ProductId("product:aapl"), "Apple Inc.",
							Optional.empty()),
					Arguments.of("Product with share class but no identifiers", new ProductId("product:brk.b"),
							"Berkshire Hathaway Inc.", Optional.of(ShareClass.CLASS_B))
			);
		}

		private static Stream<Arguments> replacesBuilderStateCases()
		{
			return Stream.of(
					Arguments.of(
							"Apple identifiers replace prior builder state",
							new ProductId("product:aapl"),
							"Apple Inc.",
							EquityProductIdentifiers.of(Map.of(
									EquityProductIdentifierScheme.ISIN, new IdentifierValue("us0378331005"),
									EquityProductIdentifierScheme.CUSIP, new IdentifierValue("037833100")
							)),
							"US0378331005",
							"037833100"
					),
					Arguments.of(
							"Microsoft identifiers replace prior builder state",
							new ProductId("product:msft"),
							"Microsoft Corporation",
							EquityProductIdentifiers.of(Map.of(
									EquityProductIdentifierScheme.ISIN, new IdentifierValue("us5949181045"),
									EquityProductIdentifierScheme.CUSIP, new IdentifierValue("594918104")
							)),
							"US5949181045",
							"594918104"
					)
			);
		}
	}
}