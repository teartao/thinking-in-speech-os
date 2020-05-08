package org.neotao.ch16.my.prodandconsu;


public class SyncDemo {
    class CubbyHole {
        private int seq;
        private boolean able = false;

        public synchronized int get() {
            while (!able) {
                try {
                    wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            able = false;
            notify();
            return seq;
        }

        public synchronized void put(int value) {
            while (able) {
                try {
                    wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            seq = value;
            able = true;
            notify();
        }
    }

    class Producer extends Thread {
        private final CubbyHole cubbyHole;
        private final int number;

        public Producer(CubbyHole c, int number) {
            cubbyHole = c;
            this.number = number;
        }

        public void run() {
            for (int i = 0; i < 10; i++) {
                cubbyHole.put(i);
                System.out.println("Productor" + number + " put " + i);
                try {
                    sleep((int) (Math.random() * 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Consumer extends Thread {
        private CubbyHole cubbyHole;
        private int number;

        public Consumer(CubbyHole cubbyHole, int number) {
            super();
            this.cubbyHole = cubbyHole;
            this.number = number;
        }

        public void run() {
            int value = 0;
            for (int i = 0; i < 10; i++) {
                value = cubbyHole.get();
                System.out.println("Customer" + number + " get " + value);
            }
        }
    }

    public static void main(String[] args) {
        CubbyHole cubbyHole = new SyncDemo().new CubbyHole();
        Producer p1 = new SyncDemo().new Producer(cubbyHole, 1);
        Consumer c1 = new SyncDemo().new Consumer(cubbyHole, 1);
        p1.start();
        c1.start();
    }
}