package tv.rewinside.home.log;

import org.mongodb.morphia.Datastore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class HomeLogRepository {

    private Datastore datastore;

    public HomeLogRepository(Datastore datastore) {
        this.datastore = datastore;
    }

    public List<HomeLog> findAllByOldOwner(UUID oldOwner) {
        List<HomeLog> logs = new ArrayList<>();
        Iterator<HomeLog> logIterator = datastore.find(HomeLog.class, "oldOwner", oldOwner).iterator();
        while(logIterator.hasNext()) {
            logs.add(logIterator.next());
        }
        return logs;
    }

    public List<HomeLog> findAll() {
        List<HomeLog> logs = new ArrayList<>();
        Iterator<HomeLog> logIterator = datastore.find(HomeLog.class).iterator();
        while(logIterator.hasNext()) {
            logs.add(logIterator.next());
        }
        return logs;
    }

    public void create(HomeLog homeLog) {
        datastore.save(homeLog);
    }
}
