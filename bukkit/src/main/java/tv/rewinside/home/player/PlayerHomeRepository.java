package tv.rewinside.home.player;

import com.mongodb.WriteResult;

import java.util.*;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateResults;
import org.mongodb.morphia.query.UpdateOperations;
import tv.rewinside.home.utils.Tuple;

public class PlayerHomeRepository {

    private Datastore datastore;

    public PlayerHomeRepository(Datastore datastore) {
        this.datastore = datastore;
    }

    public PlayerHome find(UUID homeUUID) {
        return datastore.find(PlayerHome.class, "_id", homeUUID).get();
    }

    public List<PlayerHome> findAll() {
        List<PlayerHome> homes = new ArrayList<>();
        Iterator<PlayerHome> homeIterator = datastore.find(PlayerHome.class).iterator();
        while(homeIterator.hasNext()) {
            homes.add(homeIterator.next());
        }
        return homes;
    }

    public List<PlayerHome> findAllMember(UUID ownerUUID) {
        List<PlayerHome> homes = new ArrayList<>();
        Iterator<PlayerHome> homeIterator = datastore.find(PlayerHome.class, "ownerUUID", ownerUUID).iterator();
        while(homeIterator.hasNext()) {
            homes.add(homeIterator.next());
        }
        Iterator<PlayerHome> homeIterator2 = datastore.find(PlayerHome.class).field("members").equal(ownerUUID).iterator();
        while(homeIterator2.hasNext()) {
            homes.add(homeIterator2.next());
        }
        return homes;
    }

    public List<PlayerHome> findAllOwned(UUID ownerUUID) {
        List<PlayerHome> homes = new ArrayList<>();
        Iterator<PlayerHome> homeIterator = datastore.find(PlayerHome.class, "ownerUUID", ownerUUID).iterator();
        while(homeIterator.hasNext()) {
            homes.add(homeIterator.next());
        }
        return homes;
    }

    public void create(PlayerHome playerHome) {
        datastore.save(playerHome);
    }

    public UpdateResults update(UUID homeUUID, Tuple<String, Object> tuple) {
        UpdateOperations<PlayerHome> ops = datastore.createUpdateOperations(PlayerHome.class)
                .set(tuple.x, tuple.y);
        return datastore.update(datastore.createQuery(PlayerHome.class).field("_id").equal(homeUUID), ops);
    }

    public WriteResult delete(PlayerHome playerHome) {
        return datastore.delete(playerHome);
    }
}
