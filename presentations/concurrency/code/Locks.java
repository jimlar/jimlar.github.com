import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Unlocked {
    private List<String> state = new ArrayList<String>();

    public void add(String data) {
        state.add(data);                
    }

    public String getFirst() {
        return state.get(0);
    }
}

class SafeLocks {
    private List<String> state = new ArrayList<String>();

    public synchronized void add(String data) {
        state.add(data);                
    }

    public synchronized String getFirst() {
        return state.get(0);
    }
}

class FineGrainedLocks {
    private List<String> state = new ArrayList<String>();

    public void add(String data) {
        synchronized (state) {
            state.add(data);
        }
    }
    
    public String getFirst() {
        synchronized (state) {
            return state.get(0);
        }
    }
}

class ReadWriteLocks {
    private List<String> state = new ArrayList<String>();
    private ReadWriteLock stateLock = new ReentrantReadWriteLock();
    
    public void add(String data) {
        try {
            stateLock.writeLock().lock();
            state.add(data);
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    public String getFirst() {
        try {
            stateLock.readLock().lock();
            return state.get(0);
        } finally {
            stateLock.readLock().unlock();
        }
    }
}

class SafeReadWriteLocks {
    private List<String> state = new ArrayList<String>();
    private ReadWriteLock stateLock = new ReentrantReadWriteLock();

    public void add(String data) {
        try {
            stateLock.writeLock().tryLock(1, TimeUnit.SECONDS);
            state.add(data);
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not get write lock");
        } finally {
            stateLock.writeLock().unlock();
        }
    }

    public String getFirst() {
        try {
            stateLock.readLock().tryLock(1, TimeUnit.SECONDS);
            return state.get(0);
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not get read lock");
        } finally {
            stateLock.readLock().unlock();
        }
    }
}


class Coordination {
    public void transfer(SafeReadWriteLocks from, SafeReadWriteLocks to) {
        from.stateLock.readLock().lock();
        to.stateLock.writeLock().lock();
        to.add(from.getFirst());
        from.stateLock.readLock().unlock();
        to.stateLock.writeLock().unlock();

        /* Beware of deadlocks!! */
    }
}

