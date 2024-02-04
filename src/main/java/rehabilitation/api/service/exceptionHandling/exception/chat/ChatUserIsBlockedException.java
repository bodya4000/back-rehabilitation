package rehabilitation.api.service.exceptionHandling.exception.chat;

import rehabilitation.api.service.entity.mongo.ChatUser;

public class ChatUserIsBlockedException extends Exception{
    public ChatUserIsBlockedException(ChatUser user, ChatUser blockedUser) {
        super("The user " + blockedUser.getLogin() + " is blocked for the user " + user.getLogin());
    }
}
