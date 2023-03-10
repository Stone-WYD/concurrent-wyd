package wyd.test.thread.cas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TestAccount2 {
    public static void main(String[] args) {
        DecimalAccount.demo(new DecimalAccountCas(new BigDecimal("10000")));
    }
}

class DecimalAccountCas implements DecimalAccount{

    private AtomicReference<BigDecimal> balance;

    public DecimalAccountCas(BigDecimal balance) {
        this.balance = new AtomicReference<>(balance);
    }

    @Override
    public BigDecimal getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        while (true){
            BigDecimal pre = balance.get();
            BigDecimal next = pre.subtract(amount);
            if (balance.compareAndSet(pre, next)) {
                break;
            }
        }


    }
}

interface DecimalAccount{

    BigDecimal getBalance();

    void withdraw(BigDecimal amount);

    static void demo(DecimalAccount account){
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(()->{
                account.withdraw(new BigDecimal("10"));
            }));
        }
        long start = System.nanoTime();
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance() + " cost: " +
                (end - start)/1000_000 + " ms"
        );
    }
}
