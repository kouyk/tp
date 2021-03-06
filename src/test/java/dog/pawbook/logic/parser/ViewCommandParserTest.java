//@@ ZhangAnli
package dog.pawbook.logic.parser;

import static dog.pawbook.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static dog.pawbook.commons.core.Messages.MESSAGE_INVALID_ID_GENERAL;
import static dog.pawbook.logic.commands.CommandTestUtil.EMPTY_STRING;
import static dog.pawbook.logic.commands.CommandTestUtil.INVALID_NEGATIVE_ID_STRING;
import static dog.pawbook.logic.commands.CommandTestUtil.INVALID_OUT_OF_BOUNDS_ID_STRING;
import static dog.pawbook.logic.commands.CommandTestUtil.INVALID_UNKNOWN_ID_STRING;
import static dog.pawbook.logic.commands.CommandTestUtil.VALID_ENTITY_ID;
import static dog.pawbook.logic.commands.CommandTestUtil.WHITESPACE_STRING;
import static dog.pawbook.logic.parser.CommandParserTestUtil.assertParseFailure;
import static dog.pawbook.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import dog.pawbook.logic.commands.ViewCommand;

public class ViewCommandParserTest {

    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_emptyArguments_failure() {
        // empty arguments exception thrown
        assertParseFailure(parser, EMPTY_STRING, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewCommand.MESSAGE_USAGE));

        // white spaces exception thrown
        assertParseFailure(parser, " ", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ViewCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidId_failure() {
        // negative ID
        assertParseFailure(parser, INVALID_NEGATIVE_ID_STRING, MESSAGE_INVALID_ID_GENERAL);

        // unknown ID
        assertParseFailure(parser, INVALID_UNKNOWN_ID_STRING, MESSAGE_INVALID_ID_GENERAL);

        // integer overflow
        assertParseFailure(parser, INVALID_OUT_OF_BOUNDS_ID_STRING, MESSAGE_INVALID_ID_GENERAL);

    }

    @Test
    public void parse_validId_success() {

        ViewCommand expectedCommand = new ViewCommand(Integer.parseInt(VALID_ENTITY_ID));

        // no whitespaces trailing and before
        assertParseSuccess(parser, VALID_ENTITY_ID, expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser,
                String.format(WHITESPACE_STRING + VALID_ENTITY_ID + WHITESPACE_STRING), expectedCommand);
    }
}
