package de.chasenet.dice

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

sealed class Command {
    data class RollCommand(val count: Int, val sides: Int, val modifier: Int) : Command()
    object NegativeDiceCommand : Command()
}

object CommandParser : Grammar<Command>() {
    val COMMAND_TOKEN by literalToken("/")
    val PLUS by literalToken("+")
    val MINUS by literalToken("-")
    val NEGATIVE_DICE_TOKEN by literalToken("d-")
    val DICE_TOKEN by literalToken("d")
    val ROLL_TOKEN by literalToken("roll")

    val NUMBER_TOKEN by regexToken("\\d+")
    val IDENTITY_TOKEN by regexToken("\\w+")
    val WHITESPACE = regexToken("\\s+", ignore = true)

    val NUMBER_EXPRESSION by NUMBER_TOKEN.use { text.toInt() }

    val POSITIVE_NUMBER by (optional(PLUS) and NUMBER_EXPRESSION).map { it.t2 }
    val NEGATIVE_NUMBER by skip(MINUS) and NUMBER_EXPRESSION.map { it * -1 }

    val MODIFIER_EXPRESSION by (POSITIVE_NUMBER or NEGATIVE_NUMBER).map { it }

    val ROLL_COMMAND by (
            skip(COMMAND_TOKEN)
                    and skip(ROLL_TOKEN)
                    and skip(WHITESPACE)
                    and optional(NUMBER_EXPRESSION)
                    and skip(DICE_TOKEN)
                    and NUMBER_EXPRESSION
                    and optional(MODIFIER_EXPRESSION)
            ).map { (count, sides, modifier) ->
            Command.RollCommand(count ?: 1, sides, modifier ?: 0)
        }

    val NEGATIVE_DICE_COMMAND by skip(COMMAND_TOKEN) and skip(NEGATIVE_DICE_TOKEN) and NUMBER_EXPRESSION.map { Command.NegativeDiceCommand }
    val DICE_COMMAND by skip(COMMAND_TOKEN) and skip(DICE_TOKEN) and (NUMBER_EXPRESSION and optional(MODIFIER_EXPRESSION)).map { (sides, modifier) ->
        Command.RollCommand(
            1,
            sides,
            modifier ?: 0
        )
    }

    override val rootParser: Parser<Command>
        get() = ROLL_COMMAND or DICE_COMMAND or NEGATIVE_DICE_COMMAND

}