package de.chasenet.dice

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import kotlin.math.max
import kotlin.random.Random
import kotlin.random.nextInt

object CommandProcessor {
    private val random = Random(System.currentTimeMillis())

    fun process(command: Command, sender: AbsSender, message: Message) {
        when (command) {
            is Command.RollCommand -> processRollCommand(sender, message, command)
            is Command.NegativeDiceCommand -> sender.replyToMessage(message, "Sides count cannot be negative")
        }
    }

    private fun processRollCommand(sender: AbsSender, message: Message, rollCommand: Command.RollCommand) {
        val (count, sides, modifier) = rollCommand

        if (sides < 2) {
            sender.replyToMessage(message, "A die must have 2 or more sides")
        }
        val throws = IntArray(count) {
            random.nextInt(1..rollCommand.sides)
        }

        sender.replyToMessage(message, buildString {
            appendLine("${message.from.firstName} rolled:")

            append(throws.joinToString(" + ", "(", ") = "))
            if (modifier != 0) append("$modifier")
            appendLine()

            appendLine(throws.sum() + modifier)
        })
    }
}