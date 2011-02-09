package jcip;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memorizer<A, V> implements Computable<A, V> {

    public Memorizer(Computable<A, V> c) {
        this.c = c;
    }

    private final Computable<A, V> c;

    private final ConcurrentHashMap<A, Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();;

    @Override
    public V compute(final A arg) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(arg);
            if (f == null) {
                Callable<V> eval = new Callable<V>() {
                    @Override
                    public V call() throws Exception {
                        return c.compute(arg);
                    }
                };
                FutureTask<V> ft = new FutureTask<V>(eval);
                f = cache.putIfAbsent(arg, ft);
                if (f == null) {
                    ft.run();
                    f = ft;
                }
            }
            try {
                return f.get();
            } catch (ExecutionException e) {
                throw new InterruptedException(e.getMessage());
            } catch (CancellationException e) {
                cache.remove(arg, f);
            }
        }
    }

}
