package DO;

import io.atomix.catalyst.serializer.CatalystSerializable;

import java.util.concurrent.locks.ReentrantLock;

public abstract class Obj implements CatalystSerializable{
    private ReentrantLock lock;
    private int id;

    public Obj(){
        lock = new ReentrantLock();
    }

    public void lock(){ lock.lock(); }

    public void unlock(){ lock.unlock(); }

    public int getIdRes() { return id; }

    public void setIdRes(int id){ this.id = id; }
}
