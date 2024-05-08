package eventbus;

class Shanks {
    public void onEventMessage(String message) {
        System.out.println("路飞的引路人红发香克斯收到消息：" + message);
    }
}

class Dragon {
    public void onEventMessage(String message) {
        System.out.println("路飞的老爸革命军龙收到消息：" + message);
    }
}

class Sheep {
    public void onEventPrint(Integer num) {
        System.out.println("Sheep收到消息，金额+" + num);
    }
}

class Mat {
    public void onEventPrint(Integer num) {
        System.out.println("Mat收到消息，金额+" + num);
    }
}

public class Test {
    public static void main(String[] args) {
        EventBus eb = EventBus.getInstance();
        Dragon dragon = new Dragon();
        Shanks shanks = new Shanks();
        Sheep sheep = new Sheep();
        Mat mat = new Mat();
        eb.register(dragon);
        eb.register(shanks);
        eb.register(sheep);
        eb.register(mat);
        eb.post("蒙奇D路飞成为新世界的新的四皇之一，赏金30亿贝里！");
        eb.unregister(dragon);
        eb.post("蒙奇D路飞成为新世界的新的四皇之一，赏金30亿贝里！");
        System.out.println("==================================");
        eb.post(10000);
    }
}
