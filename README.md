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
- `VenueSymbol` binds venue and symbol together so ticker is never treated as global identity.
- `TradingTerms` attaches quoting and settlement meaning to raw price/size types from `metis.core.types`.

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

## Richer case: explicit product metadata

Use the builder when identifiers or share class matter.

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
```

## Richer case: explicit listing metadata

Use the builder when listing identifiers or non-default listing settings matter.

```java
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

## When to use product identifiers vs listing identifiers

- Put identifiers on `EquityProduct` when they describe the security/product itself.
  Example: ISIN, CUSIP.
- Put identifiers on `EquityListing` when they describe the listed trading line.
  Example: composite FIGI, share-class FIGI.

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

## Modeling rules

- Do not treat ticker alone as identity.
- Do not put venue-specific facts on `EquityProduct`.
- Do not put product/security facts on `EquityListing`.
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

- If the task is “model a normal listed US stock”, use `EquityProducts.commonStock(...)` and
  `EquityListings.nasdaq(...)`.
- If the task includes `ISIN`, `CUSIP`, share class, or listing FIGIs, switch to the builders.
- If you are adding a new instrument family later, preserve the same split:
  product/security definition first, tradable instrument/listing second.