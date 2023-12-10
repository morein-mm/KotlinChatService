fun main() {

    ChatService.createChat(1, 2, "Hello, User2")
    ChatService.createChat(1, 3, "Hello, User3")
    ChatService.createChat(2, 3, "Hello in 2-3 chat")
    ChatService.sendMessage(1, Message(authorId = 1, text = "How are you?"))
    ChatService.sendMessage(1, Message(authorId = 2, text = "I'm fine. And you?"))
    ChatService.sendMessage(1, Message(authorId = 2, text = "What are you planning to do today?"))
    ChatService.sendMessage(1, Message(authorId = 1, text = "I want to go to the cinema."))
    ChatService.sendMessage(1, Message(authorId = 2, text = "And play football"))

}

data class Chat (
    val id: Int,
    val user1Id: Int,
    val user2Id: Int,
    var nextMessageId: Int = 2,
    var messages: MutableList<Message>
) {
    fun printChat() {
        println("ЧАТ ID = " + this.id)
        println("Пользователь 1 id = " + this.user1Id)
        println("Пользователь 2 id = " + this.user2Id)
        println("id следующего сообщения = " + this.nextMessageId)
        println("Сообщения:")
        for (message in this.messages) {
            message.printMessage()
        }
        println("___________________")
    }
}

data class Message (
    val id: Int = 0,
    val authorId: Int,
    val text: String,
    var read: Boolean = false
) {
    fun printMessage() {
        println("ID = " + this.id)
        println("Автор = " + this.authorId)
        println("Текст = " + this.text)
        println("Прочитано = " + this.read)
    }
}

object ChatService {
    private var chats = mutableListOf<Chat>()
    private var nextChatId = 1


    fun createChat(user1Id: Int, user2Id: Int, messageText: String) : Chat {
        chats += Chat(nextChatId, user1Id, user2Id, messages  = mutableListOf(Message(1, user1Id, messageText)))
        nextChatId ++
        return chats.last()
    }

    fun printAllChats() {
        for (chat in chats) {
            chat.printChat()
        }
    }

    fun deleteChat(chatId: Int) : Boolean {
        if (!chats.removeIf { chat: Chat -> chat.id == chatId }) throw ChatNotFoundException("Chat not found by id")
        return true
    }

//    fun sendMessage(chatId: Int, message: Message) : Message {
//        val chat = getChatById(chatId)
//        chat.messages += message.copy(id = chat.nextMessageId)
//        chat.nextMessageId ++
//        return chat.messages.last()
//    }

    fun sendMessage(companionUserId: Int, message: Message) : Message {
        var chat = getChats(message.authorId).intersect(getChats(companionUserId)).singleOrNull()
        if (chat != null) {
            chat.messages += message.copy(id = chat.nextMessageId)
            chat.nextMessageId ++
            return chat.messages.last()
        }
        return createChat(message.authorId, companionUserId, message.text).messages.last()
    }

    fun getChatById(chatID: Int) : Chat {
        val lst = chats.filter { chat: Chat -> chat.id == chatID }
        if (lst.size != 1) throw ChatNotFoundException("Chat not found by id")
        return lst[0]
    }

    fun deleteMessage(chatId: Int, messageId: Int) : Boolean {
        val chat = getChatById(chatId)
        if (!chat.messages.removeIf { message: Message -> message.id == messageId }) throw MessageNotFoundException("Message not found by id")
        if (chat.messages.isEmpty()) {
            deleteChat(chatId)
        }
        return true
    }

    fun getChats(userId:Int) : List<Chat> = chats.filter { chat: Chat -> chat.user1Id == userId } + chats.filter { chat: Chat -> chat.user2Id == userId }

    fun getUnreadChatsCount(userId: Int) : Int {
        //непрочитанное сообщение - если message.authorID != текущий userID и message.read = false
        return getChats(userId).filter { chat: Chat -> return@filter getUnreadMessagesFromChat(chat, userId).count() > 0 }.count()
    }

    fun getNotMyMessagesFromChat(chat:Chat, userId: Int) = chat.messages.filter { message -> message.authorId != userId }

    fun getUnreadMessagesFromChat(chat:Chat, userId: Int) = getNotMyMessagesFromChat(chat, userId).filter { message -> !message.read }


    fun getChatsLastMessages(userId:Int) : List<String> {
        var lastMessages = mutableListOf<String>()
        for (chat in getChats(userId)) {
            lastMessages += chat.messages.last().text
        }
        return lastMessages
    }

    fun getMessagesFromChat(currentUserId: Int, companionUserId: Int, numberOfMessages: Int): List<Message> =
        getChats(currentUserId).intersect(getChats(companionUserId)) // Находим пересечение чатов между двумя пользователями
            .singleOrNull() // Возвращает единственный чат, если он есть, иначе null
            ?.messages // Получаем список сообщений из найденного чата
            .orEmpty() // В случае, если чата нет, возвращает пустой список
// .asReversed().take(numberOfMessages) // Альтернатива: переворачиваем список и берем первые 'numberOfMessages' сообщений
            .takeLast(numberOfMessages) // Берем последние 'numberOfMessages' сообщений из непустого списка
            .onEach { if (it.authorId == companionUserId) it.read = true } // Устанавливает сообщения как прочитанные, если автор - companionUserId

//    fun getMessagesFromChat(currentUserId: Int, companionUserId: Int, numberOfMessages: Int) : List<Message>  {
//        var messagesList = listOf<Message>()
//        val chat = getChats(currentUserId).intersect(getChats(companionUserId).toSet())
//        if (chat.count() == 1) {
//            val toIndex = chat.elementAt(0).messages.size
//            var fromIndex = chat.elementAt(0).messages.size - numberOfMessages
//            if (fromIndex < 0) {fromIndex = 0}
//            messagesList = chat.elementAt(0).messages.subList(fromIndex, toIndex)
//            for (message in messagesList) {
//                if (message.authorId == companionUserId) {message.read = true}
//            }
//        }
//        return messagesList
//    }

    fun clear() {
        chats = mutableListOf<Chat>()
        nextChatId = 1
    }

}

class ChatNotFoundException(message: String) : RuntimeException(message)

class MessageNotFoundException(message: String) : RuntimeException(message)