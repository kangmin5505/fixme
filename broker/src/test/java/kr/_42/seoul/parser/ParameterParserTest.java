package kr._42.seoul.parser;

import kr._42.seoul.enums.Command;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParameterParserTest {

    private static final ParameterParser parameterParser = new ParameterParser();

    @Test
    @DisplayName("buy | sell 명령어 성공")
    void parseBuySuccessTest() {
        String[] buyArgs = this.getValidArgs("buy");
        parameterParser.parse(buyArgs);

        assertThat(parameterParser.isValid()).isEqualTo(true);
        assertThat(parameterParser.getCommand()).isEqualTo(Command.BUY);

        String[] sellArgs = this.getValidArgs("sell");
        parameterParser.parse(sellArgs);

        assertThat(parameterParser.isValid()).isEqualTo(true);
        assertThat(parameterParser.getCommand()).isEqualTo(Command.SELL);
    }


    @Test
    @DisplayName("buy | sell 명령어 실패")
    void parseBuyFailTest() {
        String[] invalidTypeBuyArgs = this.getTypeInvalidArgs("buy");
        parameterParser.parse(invalidTypeBuyArgs);

        assertThat(parameterParser.isValid()).isEqualTo(false);

        String[] invalidLengthBuyArgs = this.getLengthInvalidArgs("buy");
        parameterParser.parse(invalidLengthBuyArgs);

        assertThat(parameterParser.isValid()).isEqualTo(false);

        String[] invalidTypeSellArgs = this.getTypeInvalidArgs("sell");
        parameterParser.parse(invalidTypeSellArgs);

        assertThat(parameterParser.isValid()).isEqualTo(false);

        String[] invalidLengthSellArgs = this.getLengthInvalidArgs("sell");
        parameterParser.parse(invalidLengthSellArgs);

        assertThat(parameterParser.isValid()).isEqualTo(false);
    }

    @Test
    @DisplayName("정의되지 않은 command")
    void invalidCommandTest() {
        String command = "invalid";

        String[] invalidCommandArgs = new String[] {command};
        parameterParser.parse(invalidCommandArgs);

        assertThat(parameterParser.isValid()).isEqualTo(false);
    }


    String[] getLengthInvalidArgs(String command) {
        String instrument = "instrument";
        String market = "market";
        int price = 500;

        return new String[] {command, instrument, market, String.valueOf(price)};
    }

    String[] getTypeInvalidArgs(String command) {
        String instrument = "instrument";
        String quantity = "fail";
        String market = "market";
        int price = 500;

        return new String[] {command, instrument, quantity, market, String.valueOf(price)};
    }

    String[] getValidArgs(String command) {
        String instrument = "instrument";
        int quantity = 100;
        String market = "market";
        int price = 500;

        return new String[] {command, instrument, String.valueOf(quantity), market, String.valueOf(price)};
    }

}