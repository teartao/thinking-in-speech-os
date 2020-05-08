package org.neotao.ch16.my.print.sample01;

/**
 * synchronized 写法
 */
public class SynchronizedMain {
    private volatile int flag = 0;
    //count只有printNum执行完(一组打印)会++，因此无需volatile
    private int count = 1;

    public synchronized void printNum() {
        //全局flag执行完后修改为下一步执行的flag数值，多线程环境将根据flag判断并执行下一步个线程的逻辑
        while (flag != 0) {
            try {
                //不满足条件则等待，由其它线程唤醒后判断flag满足条件再打印
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.print(2 * count - 1 + "_");
        System.out.print(2 * count);
        flag = 1;
        count++;
        //唤醒其它打印线程(本质应该是只唤醒下一个执行的线程，如这里flag=1，
        // 则应该唤醒printUpperCase对应的线程，类似责任链模式)
        this.notifyAll();
    }

    public synchronized void printUpperCase() {
        while (flag != 1) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //printNum先执行，count从1开始打印后++，因此count=2，相比于A的ASCII码 A+2 偏移了2位，因此需要-2
        System.out.print((char) ('A' + count - 2));
        flag = 2;
        this.notifyAll();
    }

    public synchronized void printLowerCase() {
        while (flag != 2) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //-2 原因同上
        System.out.print((char) ('a' + count - 2) + " ");
        flag = 0;
        this.notifyAll();
    }


    public static void main(String[] args) {
        SynchronizedMain synchronizedMain = new SynchronizedMain();
        /*
         * 第一种写法：通过一层for循环控制三个线程执行次数(因为它们都是26倍数)
         */
        for (int i = 0; i < 26; i++) {
            new Thread(() -> {
                synchronizedMain.printNum();
            }).start();
            new Thread(() -> {
                synchronizedMain.printUpperCase();
            }).start();
            new Thread(() -> {
                synchronizedMain.printLowerCase();
            }).start();
        }

        /*
         * 第二种写法：如果执行次数没有紧密联系，则通过多个for循环各自控制执行次数
         * 两种写法2选1,同时放开将导致程序执行次数出错
         */
        // new Thread(() -> {
        //     for (int i = 0; i < 26; i++) {
        //         printSample01.printNum();
        //     }
        // }).start();
        //
        // new Thread(() -> {
        //     for (int i = 0; i < 26; i++) {
        //         printSample01.printLowerCase();
        //     }
        // }).start();
        //
        // new Thread(() -> {
        //     for (int i = 0; i < 26; i++) {
        //         printSample01.printUpperCase();
        //     }
        // }).start();

    }
}
