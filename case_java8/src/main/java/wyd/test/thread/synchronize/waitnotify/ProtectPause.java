package wyd.test.thread.synchronize.waitnotify;

import java.util.List;

public class ProtectPause {


}

class GuardedObject{
    private Object response;

    // 获取结果
    public Object get() throws InterruptedException {
        synchronized(this){
            while (response == null) {
                this.wait();
            }
        }
        return response;
    }

    // 存放结果
    public void complete(Object response){
        synchronized (this){
            this.response = response;
            this.notifyAll();
        }
    }

}
