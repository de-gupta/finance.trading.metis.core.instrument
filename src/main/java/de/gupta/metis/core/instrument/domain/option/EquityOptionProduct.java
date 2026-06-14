package de.gupta.metis.core.instrument.domain.option;

import de.gupta.commons.utility.map.enumMap.ImmutableEnumMap;
import de.gupta.metis.core.instrument.domain.equity.EquityProduct;
import de.gupta.metis.core.instrument.domain.identifier.IdentifierValue;
import de.gupta.metis.core.instrument.domain.product.ProductId;
import de.gupta.metis.core.types.quoting.PriceQuotingUnit;

import java.time.LocalDate;

public record EquityOptionProduct<U extends PriceQuotingUnit>(
		ProductId id,
		EquityProduct underlying,
		OptionRight right,
		LocalDate expiryDate,
		StrikePrice<U> strikePrice,
		OptionExerciseStyle exerciseStyle,
		OptionSettlementStyle settlementStyle,
		ContractSize contractSize,
		OptionProductIdentifiers identifiers
) implements OptionProduct<EquityProduct>
{
	public static <U extends de.gupta.metis.core.types.quoting.PriceQuotingUnit> EquityOptionProduct<U> standardContract(
			final ProductId id,
			final EquityProduct underlying,
			final OptionRight right,
			final LocalDate expiryDate,
			final StrikePrice<U> strikePrice,
			final OptionExerciseStyle exerciseStyle
	)
	{
		return EquityOptionProducts.standardContract(id, underlying, right, expiryDate, strikePrice, exerciseStyle);
	}

	public static <U extends de.gupta.metis.core.types.quoting.PriceQuotingUnit> Builder<U> builder()
	{
		return new Builder<>();
	}

	public EquityOptionProduct
	{
		identifiers = identifiers == null ? OptionProductIdentifiers.empty() : identifiers;
	}

	public static final class Builder<U extends de.gupta.metis.core.types.quoting.PriceQuotingUnit>
	{
		private ProductId id;
		private EquityProduct underlying;
		private OptionRight right;
		private LocalDate expiryDate;
		private StrikePrice<U> strikePrice;
		private OptionExerciseStyle exerciseStyle = OptionExerciseStyle.AMERICAN;
		private OptionSettlementStyle settlementStyle = OptionSettlementStyle.PHYSICAL_DELIVERY;
		private ContractSize contractSize = ContractSize.standardEquityOption();
		private ImmutableEnumMap<OptionProductIdentifierScheme, IdentifierValue> identifiers =
				ImmutableEnumMap.empty(OptionProductIdentifierScheme.class);

		public Builder<U> id(final ProductId id)
		{
			this.id = id;
			return this;
		}

		public Builder<U> underlying(final EquityProduct underlying)
		{
			this.underlying = underlying;
			return this;
		}

		public Builder<U> right(final OptionRight right)
		{
			this.right = right;
			return this;
		}

		public Builder<U> expiryDate(final LocalDate expiryDate)
		{
			this.expiryDate = expiryDate;
			return this;
		}

		public Builder<U> strikePrice(final StrikePrice<U> strikePrice)
		{
			this.strikePrice = strikePrice;
			return this;
		}

		public Builder<U> exerciseStyle(final OptionExerciseStyle exerciseStyle)
		{
			this.exerciseStyle = exerciseStyle;
			return this;
		}

		public Builder<U> settlementStyle(final OptionSettlementStyle settlementStyle)
		{
			this.settlementStyle = settlementStyle;
			return this;
		}

		public Builder<U> contractSize(final ContractSize contractSize)
		{
			this.contractSize = contractSize;
			return this;
		}

		public Builder<U> identifiers(final OptionProductIdentifiers identifiers)
		{
			this.identifiers = identifiers.values();
			return this;
		}

		public Builder<U> osi(final String value)
		{
			return identifier(OptionProductIdentifierScheme.OSI, value);
		}

		public Builder<U> isin(final String value)
		{
			return identifier(OptionProductIdentifierScheme.ISIN, value);
		}

		public Builder<U> occSeriesKey(final String value)
		{
			return identifier(OptionProductIdentifierScheme.OCC_SERIES_KEY, value);
		}

		public Builder<U> identifier(final OptionProductIdentifierScheme scheme, final String value)
		{
			this.identifiers = this.identifiers.with(scheme, new IdentifierValue(value));
			return this;
		}

		public EquityOptionProduct<U> build()
		{
			return new EquityOptionProduct<>(id, underlying, right, expiryDate, strikePrice, exerciseStyle,
					settlementStyle, contractSize, new OptionProductIdentifiers(identifiers));
		}
	}
}
