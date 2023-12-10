import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class ChatServiceTest {

    @Before
    fun clearBeforeTest() {
        ChatService.clear()
    }

    @Test
    fun createChat() {
        val chat = ChatService.createChat(1, 2, "Hello, User2")
        assertEquals(1, chat.id)
    }

    @Test
    fun deleteChatSuccess() {
        val chat = ChatService.createChat(1, 2, "Hello, User2")
        val result = ChatService.deleteChat(chat.id)
        assertTrue(result)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChatChatNotFoundException() {
        ChatService.deleteChat(1)
    }

    @Test
    fun sendMessageToExistingChat() {
        val chat = ChatService.createChat(1, 2, "Hello, User2")
        val message = ChatService.sendMessage(chat.id, Message(authorId = 1, text = "How are you?"))
        assertEquals(2, message.id)
    }

    @Test
    fun sendMessageToNewChat() {
        val message = ChatService.sendMessage(2, Message(authorId = 1, text = "How are you?"))
        assertEquals(1, message.id)
    }


    @Test
    fun getChatByIdSuccess() {
        val chat = ChatService.createChat(1, 2, "Hello, User2")
        val getChat = ChatService.getChatById(chat.id)
        assertEquals(chat.id, getChat.id)
    }

    @Test(expected = ChatNotFoundException::class)
    fun getChatByIdChatNotFoundException() {
        ChatService.getChatById(10)
    }

    @Test
    fun deleteMessageSuccess() {
        val chat = ChatService.createChat(1, 2, "Hello, User2")
        ChatService.sendMessage(chat.id, Message(authorId = 1, text = "How are you?"))
        val result = ChatService.deleteMessage(1, 2)
        assertTrue(result)
    }

    @Test(expected = MessageNotFoundException::class)
    fun deleteMessageMessageNotFoundException() {
        val chat = ChatService.createChat(1, 2, "Hello, User2")
        ChatService.deleteMessage(chat.id, 2)
    }

    @Test
    fun getChats() {
        var chat = ChatService.createChat(1, 2, "Hello, User2")
        ChatService.createChat(3, 2, "Hello, User1")
        var result = ChatService.getChats(1)
        assertEquals(chat, result.get(0))
    }

    @Test
    fun getUnreadChatsCount() {
        ChatService.createChat(1, 2, "Hello, User2")
        ChatService.createChat(2, 1, "Hello, User1")
        var result = ChatService.getUnreadChatsCount(1)
        assertEquals(1, result)
    }

    @Test
    fun getChatsLastMessages() {
        val lstMessage = listOf("Hello, User2", "Hello, User1")
        ChatService.createChat(1, 2, lstMessage.get(0))
        ChatService.createChat(2, 1, lstMessage.get(1))
        val lstResult = ChatService.getChatsLastMessages(1)
        assertEquals(lstMessage, lstResult)
    }

    @Test
    fun getMessagesFromChat() {
        val messageText = "Hello, User2"
        ChatService.createChat(1, 2, messageText)
        val lstResult = ChatService.getMessagesFromChat(1, 2, 1)
        assertEquals(messageText, lstResult.get(0).text)

    }
}