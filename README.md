# metis.core.instrument

Instrument reference-data model for Metis. This module sits above `metis.core.types`: the types library gives raw,
algebraically honest `PriceType` / `SizeType` / `MoneyType`; this module gives them instrument meaning.

## Intended reader

This README is for agents and programmers constructing instrument models against this library.

## Design in one paragraph

- `Product` is the economic/security definition.
- `Instrument` is the exact tradable thing.
- For stocks, `EquityProduct` is the share/security definition and `EquityListing` is the venue-specific tradable
  listing.
- For options, `EquityOptionProduct` is the contract definition and `EquityOptionListing` is the venue-specific
  tradable option line.
- `VenueSymbol` binds venue and symbol together so ticker is never treated as global identity.
- `TradingTerms` attaches quoting and settlement meaning to raw price/size types from `metis.core.types`.
- Derivatives extend `DerivativeProduct`, and options extend `OptionProduct`, so later derivative families can reuse
  the same architectural seam.
- Shared listing state now lives in `ListingDetails`, and family-specific listing builders extend
  `AbstractListingBuilder`.

## Core stock model

### `EquityProduct`

Represents the stock/security itself.

Fields:

- `ProductId id`
- `String issuerName`
- `Optional<ShareClass> shareClass`
- `EquitySecurityType securityType`
- `CountryCode incorporationCountry`
- `EquityProductIdentifiers identifiers`

Interpretation:

- `"Apple Inc. common stock"` is a product.
- `shareClass` is issuer-specific classification like `CLASS_A`, `CLASS_B`.
- `securityType` is broader, e.g. `COMMON_STOCK`, `PREFERRED_STOCK`, `ADR`.

### `EquityListing`

Represents the exact tradable listed instrument.

Fields:

- `InstrumentId id`
- `EquityProduct product`
- `VenueSymbol venueSymbol`
- `EquityListingIdentifiers identifiers`
- `ListingStatus status`
- `TradingTerms<?, ?> tradingTerms`
- `boolean primaryListing`

Interpretation:

- Apple on Nasdaq and Apple on another exchange are different `EquityListing` values.
- They may still point to the same `EquityProduct`.

## Core option model

### `EquityOptionProduct`

Represents the option contract itself.

Fields:

- `ProductId id`
- `EquityProduct underlying`
- `OptionRight right`
- `LocalDate expiryDate`
- `StrikePrice<?> strikePrice`
- `OptionExerciseStyle exerciseStyle`
- `OptionSettlementStyle settlementStyle`
- `ContractSize contractSize`
- `OptionProductIdentifiers identifiers`

Interpretation:

- This is the economic contract definition.
- It points to the underlying equity security, not to a specific stock listing.
- `contractSize` is the number of underlying units represented by one contract.
- `strikePrice` carries both the raw `PriceType` and its quoting convention.

### `EquityOptionListing`

Represents the exact tradable listed option line.

Fields:

- `InstrumentId id`
- `EquityOptionProduct<?> product`
- `VenueSymbol venueSymbol`
- `OptionListingIdentifiers identifiers`
- `ListingStatus status`
- `TradingTerms<?, ?> tradingTerms`
- `boolean primaryListing`

Interpretation:

- The option contract and the option listing are different concepts, just like stock product vs stock listing.
- The listing carries venue identity and premium quoting terms.

## Happy path: create a normal US common stock listing

Use the utility factories.

```java
var product = EquityProducts.commonStock(
		new ProductId("product:aapl"),
		"Apple Inc."
);

var listing = EquityListings.nasdaq(
		new InstrumentId("instrument:xnas:aapl"),
		product,
		"AAPL",
		ListingStatus.ACTIVE
);
```

What this gives you:

- product security type = `COMMON_STOCK`
- no share class
- incorporation country = `US`
- no product/listing identifiers yet
- venue = `NASDAQ`
- trading terms = USD cash equity
- size convention = whole units/shares
- round lot = `100`
- `primaryListing = true`

## Richer case: explicit stock metadata

Use the builders when identifiers or non-default stock metadata matter.

```java
var product = EquityProduct.builder()
                           .id(new ProductId("product:goog-class-a"))
                           .issuerName("Alphabet Inc.")
                           .shareClass(ShareClass.CLASS_A)
                           .securityType(EquitySecurityType.COMMON_STOCK)
                           .incorporationCountry(CountryCode.US)
                           .isin("US02079K3059")
                           .cusip("02079K305")
                           .build();

var listing = EquityListing.builder(TradingTerms.cashEquity(Currency.USD.INSTANCE))
                           .id(new InstrumentId("instrument:xnas:goog"))
                           .product(product)
                           .venueSymbol(Venue.NASDAQ, "GOOG")
                           .status(ListingStatus.ACTIVE)
                           .primaryListing(true)
                           .compositeFigi("BBG000BWXBC2")
                           .shareClassFigi("BBG001S5PQL7")
                           .build();
```

## Happy path: create a standard listed equity option

Use the product factory plus the USD-listed option factory.

```java
var underlying = EquityProducts.commonStock(
		new ProductId("product:aapl"),
		"Apple Inc."
);

var optionProduct = EquityOptionProducts.standardContract(
		new ProductId("product:aapl-20260116-c-250"),
		underlying,
		OptionRight.CALL,
		LocalDate.of(2026, 1, 16),
		StrikePrice.of(250, Currency.USD.INSTANCE),
		OptionExerciseStyle.AMERICAN
);

var optionListing = EquityOptionListings.usdListed(
		new InstrumentId("instrument:xcbo:aapl-20260116-c-250"),
		optionProduct,
		VenueSymbol.of(new Venue("CBOE", "Chicago Board Options Exchange", "XCBO", CountryCode.US),
				"AAPL  260116C00250000"),
		ListingStatus.ACTIVE
);
```

What this gives you:

- settlement style = `PHYSICAL_DELIVERY`
- contract size = `100` underlying units
- no option product/listing identifiers yet
- premium quoted in settlement currency
- size quoted in contracts
- option round lot = `1`
- `primaryListing = true`

## Richer case: explicit option metadata

Use the builders when settlement style, contract size, or option identifiers matter.

```java
var optionProduct = EquityOptionProduct.builder()
                                       .id(new ProductId("product:googl-20260320-p-150"))
                                       .underlying(product)
                                       .right(OptionRight.PUT)
                                       .expiryDate(LocalDate.of(2026, 3, 20))
                                       .strikePrice(StrikePrice.of(150, Currency.USD.INSTANCE))
                                       .exerciseStyle(OptionExerciseStyle.EUROPEAN)
                                       .settlementStyle(OptionSettlementStyle.CASH_SETTLED)
                                       .contractSize(ContractSize.of(100))
                                       .osi("GOOGL  260320P00150000")
                                       .build();

var optionListing = EquityOptionListings.usdBuilder(new Venue("CBOE", "Chicago Board Options Exchange", "XCBO",
												CountryCode.US), "GOOGL  260320P00150000")
                                        .id(new InstrumentId("instrument:xcbo:googl-20260320-p-150"))
                                        .product(optionProduct)
                                        .status(ListingStatus.ACTIVE)
                                        .primaryListing(false)
                                        .opra("GOOGL  260320P00150000")
                                        .compositeFigi("BBG00OPTION123")
                                        .build();
```

## When to use product identifiers vs listing identifiers

- Put identifiers on `EquityProduct` when they describe the security/product itself.
  Example: ISIN, CUSIP.
- Put identifiers on `EquityListing` when they describe the listed trading line.
  Example: composite FIGI, share-class FIGI.
- Put identifiers on `EquityOptionProduct` when they describe the contract definition itself.
  Example: OSI, OCC series key.
- Put identifiers on `EquityOptionListing` when they describe the listed option line.
  Example: OPRA code, composite FIGI, exchange FIGI.

## Trading terms rule

`TradingTerms` is where raw numeric values become meaningful.

For stocks, prefer:

```java
TradingTerms.cashEquity(Currency.USD.INSTANCE)
```

This means:

- price quoted in USD money scale
- size quoted in whole units/shares
- round lot = `100`
- settlement currency = USD

For listed options, prefer:

```java
TradingTerms.listedOption(Currency.USD.INSTANCE)
```

This means:

- premium quoted in USD money scale
- size quoted in whole contracts
- round lot = `1`
- settlement currency = USD

## Modeling rules

- Do not treat ticker alone as identity.
- Do not put venue-specific facts on `EquityProduct`.
- Do not put product/security facts on `EquityListing`.
- Do not put venue-specific option facts on `EquityOptionProduct`.
- Do not put contract-definition facts on `EquityOptionListing`.
- Do not invent IDs inside this library unless your surrounding system has a canonical derivation rule.
- Prefer the utility factories for simple cases.
- Prefer builders when identifiers or non-default metadata are present.

## What this module does not do

- allocate unique IDs
- persist instruments
- model market data
- model corporate actions
- model valuation or pricing
- model order entry / execution

## Practical guidance for agents

- If the task is "model a normal listed US stock", use `EquityProducts.commonStock(...)` and
  `EquityListings.nasdaq(...)`.
- If the task includes `ISIN`, `CUSIP`, share class, or listing FIGIs, switch to the stock builders.
- If the task is "model a normal listed equity option", use `EquityOptionProducts.standardContract(...)` and
  `EquityOptionListings.usdListed(...)`.
- If the task includes OSI, OPRA, settlement style, contract size, or non-default option metadata, switch to the
  option builders.
- If you are adding a new instrument family later, preserve the same split:
  product/security definition first, tradable instrument/listing second.