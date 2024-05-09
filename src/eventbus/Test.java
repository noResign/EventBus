package eventbus;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

class Shanks {

    public void onEvent(MessageEvent message) {
        System.out.println("Shanks收到消息：" + message.message);
    }
}
class Dragon {
    public void onEvent(MessageEvent message) {
        System.out.println("Dragon收到消息：" + message.message);
    }
    public void onEvent(NumberEvent numberEvent) {
        System.out.println("Dragon收到消息，金额+" + numberEvent.number);
    }
}
class Sheep {
    public void onEvent(NumberEvent numberEvent) {
        System.out.println("Sheep收到消息，金额+" + numberEvent.number);
    }
}
class Mat {
    public void onEvent(NumberEvent numberEvent) {
        System.out.println("Mat收到消息，金额+" + numberEvent.number);
    }
}
class SubscriberCreateFactory {
    private static final Random random = new Random();
    private SubscriberCreateFactory(){}
    private static class SingletonInstance {
        private static final SubscriberCreateFactory INSTANCE = new SubscriberCreateFactory();
    }
    public static SubscriberCreateFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }
    public Object generateSubscriber() {
        int randomNumber = random.nextInt(3);
        switch (randomNumber) {
            case 0:
                return new Dragon();
            case 1:
                return new Shanks();
            case 2:
                return new Sheep();
            default:
                return new Mat();
        }
    }
}

class EventCreateFactory {
    private static final Random random = new Random();
    private EventCreateFactory(){}
    private static class SingletonInstance {
        private static final EventCreateFactory INSTANCE = new EventCreateFactory();
    }
    public static EventCreateFactory getInstance() {
        return SingletonInstance.INSTANCE;
    }
    public Object generateEvent() {
        int randomNumber = random.nextInt(2);
        switch (randomNumber) {
            case 0:
                return new MessageEvent("蒙奇D路飞成为新世界的新的四皇之一，赏金30亿贝里！");
            case 1:
                return new NumberEvent(10000);
            default:
                return null;
        }
    }
}
public class Test {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final SubscriberCreateFactory subscriberCreateFactory = SubscriberCreateFactory.getInstance();
    private static final EventCreateFactory eventCreateFactory = EventCreateFactory.getInstance();
    public static void main(String[] args) {
        EventBus eb = EventBus.getInstance();
        CountDownLatch latch = new CountDownLatch(10); // 用于等待订阅者注册任务完成

        for (int i = 0; i < 10; ++i) {
            executorService.submit(() -> {
                Object subscriber = subscriberCreateFactory.generateSubscriber();
                eb.register(subscriber);
                System.out.println(subscriber.getClass().getSimpleName());
                latch.countDown(); // 计数减一
            });
        }
        try {
            latch.await(); // 主线程等待latch为0时，即所有订阅者注册任务完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        eb.printSubscriptionByEventType();
        for (int i = 0; i < 4; ++i) {
            executorService.submit(() -> {
                Object event = eventCreateFactory.generateEvent();
                System.out.println(event.getClass().getSimpleName());
                eb.post(event);
            });
        }
        executorService.shutdown();
    }
}
