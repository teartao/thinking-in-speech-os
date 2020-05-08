package org.neotao.ch16.my.print01;

/**
 * synchronized写法
 */
public class SyncDemo01 {
    private volatile boolean flag = true;

    private synchronized void sub() {
        while (!flag) {
            try {
                this.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 2; i++) {
            System.out.println("sub run " + i);
        }
        flag = false;
        this.notify();
    }

    private synchronized void main() {
        while (flag) {
            try {
                this.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 2; i++) {
            System.out.println("main run " + i);
        }
        flag = true;
        this.notify();
    }

    public static void main(String[] args) {
        SyncDemo01 syncDemo = new SyncDemo01();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                syncDemo.sub();
            }
        }).start();
        for (int i = 0; i < 10; i++) {
            syncDemo.main();
        }

    }
}
