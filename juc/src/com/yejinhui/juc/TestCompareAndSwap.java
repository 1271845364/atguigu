package com.yejinhui.juc;

/**
 * 模拟CAS算法
 * @author ye.jinhui
 * @create 2017-02-25 16:50
 */
public class TestCompareAndSwap {

    public static void main(String[] args) throws InterruptedException {
        final CompareAndSwap cas = new CompareAndSwap();
        for(int i=0;i<10;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int expectedValue = cas.get();
                    boolean b = cas.compareAndSet(expectedValue,(int)(Math.random()*101));
                    System.out.println(b);
                }
            }).start();
        }
    }
}

class CompareAndSwap {
    private int value;

    //获取内存值
    public synchronized int get() {
        return value;
    }

    //比较
    public synchronized int compareAndSwap(int expectedValue,int newValue) {
        //读取内存值
        int oldValue  = value;

        if(oldValue == expectedValue) {
            this.value = newValue;
        }
        return oldValue;
    }

    //设置
    public synchronized boolean compareAndSet(int expectedValue,int newValue) {
        return expectedValue == compareAndSwap(expectedValue,newValue);
    }
}
