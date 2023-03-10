package wyd.test.thread.cas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestAccount {

    public static void main(String[] args) {
        AccountUnsafe accountUnsafe = new AccountUnsafe(10000);
        Account.demo(accountUnsafe);
        AccountCas accountCas = new AccountCas(10000);
        Account.demo(accountCas);
    }
}

class AccountUnsafe implements Account {

    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        synchronized (this) {
            return this.balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        synchronized (this) {
            this.balance -= amount;
        }
    }
}

class AccountCas implements Account{

    private AtomicInteger balance;

    public AccountCas(Integer balance){
        this.balance = new AtomicInteger(balance);
    }
    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        while(true){
            int prev = balance.get();
            int next = prev - amount;
            if (balance.compareAndSet(prev, next)) {
                break;
            }
        }
        /*balance.getAndAdd(-1 * amount);*/
    }
}

interface Account{

    // 获取余额
    Integer getBalance();

    // 取款
    void withdraw(Integer amount);

    // 测试方法
    static void demo(Account account){
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(()->{
                account.withdraw(10);
            }));
        }
        long start = System.nanoTime();
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance() + " cost: " +
                        (end - start)/1000_000 + " ms"
                );
    }
}


