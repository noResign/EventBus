package eventbus;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
    private static final String TAG = EventBus.class.getSimpleName();
    // 事件-订阅者列表
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
    // 订阅者-事件列表
    private final Map<Object, List<Class<?>>> eventTypeBySubscriber;
    private EventBus() {
        eventTypeBySubscriber = new HashMap<>();
        subscriptionsByEventType = new HashMap<>();
    }
    public void printSubscriptionByEventType() {
        if (subscriptionsByEventType == null) {
            System.out.println("subscriptionsByEventType is null");
            return;
        }
        for (Class<?> key : subscriptionsByEventType.keySet()) {
            System.out.println("EventType: " + key.getSimpleName());
            for (Subscription subscription : subscriptionsByEventType.get(key)) {
                System.out.println("  " + subscription.subscriber.getClass().getSimpleName());
            }
        }
    }
    private static class Holder {
        static EventBus instance = new EventBus();
    }
    public static EventBus getInstance() {
        return Holder.instance;
    }
    public void register(Object subscriber) {
        Class<?> subscriberClass = subscriber.getClass();
        List<SubscriberMethod> subscriberMethods = new ArrayList<>();
        Method[] methods = subscriberClass.getDeclaredMethods();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) continue;
            if (method.getName().startsWith("onEvent")) {
                subscriberMethods.add(new SubscriberMethod(method, parameterTypes[0]));
            }
        }
        synchronized (this) {
            for (SubscriberMethod method : subscriberMethods) {
                subscribe(subscriber, method);
            }
        }

    }
    private void subscribe(Object subscriber, SubscriberMethod method) {
//        Logger.i(subscriber.getClass().getSimpleName() + ":" + method.method.getName());
        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(method.eventType);
        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList<>();
        }
        subscriptions.add(new Subscription(subscriber, method));
        subscriptionsByEventType.put(method.eventType, subscriptions);
        List<Class<?>> eventTypes = eventTypeBySubscriber.get(subscriber);
        if (eventTypes == null) {
            eventTypes = new CopyOnWriteArrayList<>();
        }
        eventTypes.add(method.eventType);
        eventTypeBySubscriber.put(subscriber, eventTypes);
    }
    public void unregister(Object subscriber) {
        List<Class<?>> eventTypes = eventTypeBySubscriber.get(subscriber);
        if (eventTypes != null)  {
            for (Class<?> eventType : eventTypes) {
                unsubscribe(subscriber, eventType);
            }
        }
    }
    private void unsubscribe(Object subscriber, Class<?> eventType) {
        List<Subscription> subscriptionList = subscriptionsByEventType.get(eventType);
        if (subscriptionList == null) {
            return;
        }
        int size = subscriptionList.size();
        for (int i = 0; i < size; ++i) {
            if (subscriptionList.get(i).subscriber == subscriber) {
                subscriptionList.remove(i);
                --i;
                --size;
            }
        }
    }
    public void post(Object event) {
        List<Subscription> subscriptionList = subscriptionsByEventType.get(event.getClass());
        if (subscriptionList == null) {
//            Logger.i("not found subscriber");
            return;
        }
        for (Subscription subscription : subscriptionList) {
            subscription.notifySubscriber(event);
        }
    }
}
