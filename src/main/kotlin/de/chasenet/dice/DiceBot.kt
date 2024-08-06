package de.chasenet.dice

import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

class DiceBot : TelegramLongPollingBot() {
    override fun getBotToken(): String = System.getenv("DICE_BOT_TOKEN")

    override fun getBotUsername(): String = "dice_topic_bot"

    override fun onUpdateReceived(update: Update) {
        update.message?.let { message ->
            if (message.isCommand) {
                when (val result = CommandParser.tryParseToEnd(message.text)) {
                    is Parsed -> CommandProcessor.process(result.value, this, message)
                    is ErrorResult -> replyToMessage(message, "Unknown command ${message.text}")
                }
            }
        }
    }
}

fun AbsSender.replyToMessage(message: Message, text: String) {
    execute(SendMessage.builder()
        .chatId(message.chatId)
        .replyToMessageId(message.messageId)
        .text(text).build()
    )
}

fun main() {
    TelegramBotsApi(DefaultBotSession::class.java).registerBot(DiceBot())
}