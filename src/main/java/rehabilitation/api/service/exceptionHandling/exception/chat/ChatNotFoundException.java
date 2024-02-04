package rehabilitation.api.service.exceptionHandling.exception.chat;

public class ChatNotFoundException extends Exception{
    public ChatNotFoundException() {
        super("The chat din not found");
    }
}
