package mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public class MongoTest {

    private static final String TEST_COLL = "t";
    private static final String TEST_DB = "repl2";
    private static final int THREAD_SIZE = 40;

    private static class MongoTask implements Runnable {

        private static Logger logger = Logger.getLogger(MongoTest.class);
        private final long start;
        private final long maxTime;
        private final DBCollection coll;

        public MongoTask(long start, final long maxtime, DBCollection coll) {
            this.start = start;
            this.maxTime = maxtime;
            this.coll = coll;
        }

        private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz"
                + "1234567890";
        private static final Random r = new Random();

        private static String getRandomString() {
            int length = r.nextInt(20);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; ++i) {
                sb.append(ALPHA.charAt(r.nextInt(ALPHA.length())));
            }
            return sb.toString();
        }

        @Override
        public void run() {
            long i = 0;
            outerloop: while (true) {
                String str = getRandomString();
                while (true) {
                    try {
                        long begin = System.currentTimeMillis();

                        BasicDBObject doc = new BasicDBObject();
                        doc.put("test", str);
                        doc.put("index", i);
                        coll.insert(doc);
                        ++i;

                        boolean find = coll.findOne(doc) != null;
                        long duration = System.currentTimeMillis() - begin;

                        logger.info("success! data count: " + i + "; time: "
                                + duration + "ms; find it: " + find);
                        if (System.currentTimeMillis() - start > maxTime)
                            break outerloop;

                        break;
                    } catch (MongoException ignore) {
                        logger.fatal(ignore.getMessage(), ignore);
                    }
                }
            }
        }

    }

    @Test
    public void testReplication() throws UnknownHostException,
            InterruptedException {

        List<ServerAddress> addrs = new ArrayList<ServerAddress>();
        // addrs.add(new ServerAddress("192.168.0.150"));
        // addrs.add(new ServerAddress("192.168.0.12"));
        addrs.add(new ServerAddress("192.168.0.101"));
        Mongo mongo = new Mongo(addrs);
        mongo.slaveOk();

        mongo.dropDatabase(TEST_DB);
        DB db = mongo.getDB(TEST_DB);
        DBCollection coll = db.getCollection(TEST_COLL);

        ExecutorService exec = Executors.newFixedThreadPool(THREAD_SIZE);

        long start = System.currentTimeMillis();
        final long duration = TimeUnit.MINUTES.toMillis(20);

        for (int i = 0; i < THREAD_SIZE; ++i) {
            exec.submit(new MongoTask(start, duration, coll));
        }
        exec.shutdown();
        exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }

}
