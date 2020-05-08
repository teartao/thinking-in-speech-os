package org.neotao.ch16.my.tickets;

public class TicketUtils {
    //模拟买票后限制间隔，不允许频繁买票
    public static void waitMoment() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
