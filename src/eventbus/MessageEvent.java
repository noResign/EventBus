package eventbus;
public class MessageEvent {
    public String message;
    public MessageEvent(String message) {
        this.message = message;
    }
}
class NumberEvent {
    public Integer number;
    public NumberEvent(Integer number) {
        this.number = number;
    }
}
