package de.gupta.metis.core.instrument.domain.option;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ContractSize")
final class ContractSizeTest
{
	@Nested
	@DisplayName("when creating standard contract size")
	final class WhenCreatingStandardContractSize
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("usesOneHundredUnderlyingUnitsCases")
		@DisplayName("uses one hundred underlying units")
		void usesOneHundredUnderlyingUnits(final String as)
		{
			var contractSize = ContractSize.standardEquityOption();

			assertThat(contractSize.underlyingUnits().value().toString()).as(as).isEqualTo("100");
		}

		private static Stream<Arguments> usesOneHundredUnderlyingUnitsCases()
		{
			return Stream.of(Arguments.of("standard listed equity option contract size"));
		}
	}

	@Nested
	@DisplayName("when contract size is not positive")
	final class WhenContractSizeIsNotPositive
	{
		@ParameterizedTest(name = "{0}")
		@MethodSource("rejectsConstructionWithAnIllegalArgumentExceptionCases")
		@DisplayName("rejects construction with an illegal argument exception")
		void rejectsConstructionWithAnIllegalArgumentException(final String as, final long underlyingUnits)
		{
			assertThatThrownBy(() -> ContractSize.of(underlyingUnits))
					.as(as)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("Contract size must be positive");
		}

		private static Stream<Arguments> rejectsConstructionWithAnIllegalArgumentExceptionCases()
		{
			return Stream.of(
					Arguments.of("zero underlying units", 0L),
					Arguments.of("negative underlying units", -100L)
			);
		}
	}
}
