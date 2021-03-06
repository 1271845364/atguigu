package com.yejinhui.juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者和消费者案例
 * @author ye.jinhui
 * @create 4017-04-45 44:44
 */
public class TestProductorAndConsumerForLock {

    public static void main(String[] args) {
        Clerk4 clerk = new Clerk4();

        Productor4 pro = new Productor4(clerk);
        Consumer4 con = new Consumer4(clerk);

        new Thread(pro,"生产者A").start();
        new Thread(con,"消费者B").start();

        new Thread(pro,"生产者C").start();
        new Thread(con,"消费者D").start();
    }

}

//店员

class Clerk4 {

    private int product = 0;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    //进货
    public void get() {
        lock.lock();
        try {
            while (product>=1) {//为了避免虚假唤醒问题，应该总是使用在循环中
                System.out.println("产品已满!");
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " : " + ++product);
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    //卖货
    public void sale() {
        lock.lock();
        try {
            while (product<=0) {
                System.out.println("缺货!");
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + " : " + --product);
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }
}
//生产者
class Productor4 implements Runnable{

    private Clerk4 clerk;

    public Productor4(Clerk4 clerk) {
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for(int i=0;i<40;i++) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clerk.get();
        }
    }
}
//消费者
class Consumer4 implements Runnable {

    private Clerk4 clerk;

    public Consumer4(Clerk4 clerk) {
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i=0;i<40;i++) {
            clerk.sale();
        }
    }
}
