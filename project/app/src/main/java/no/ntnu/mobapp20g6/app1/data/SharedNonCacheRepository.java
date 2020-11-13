package no.ntnu.mobapp20g6.app1.data;

/**
 * Requests information regarding Group and Location.
 * Does not hold caches of this information.
 * Is a singleton.
 */
public class SharedNonCacheRepository {

    private static volatile SharedNonCacheRepository instance;

    private GroupDataSource groupDataSource;
    private LocationDataSource locationDataSource;

    private SharedNonCacheRepository(GroupDataSource groupDataSource, LocationDataSource locationDataSource) {
        this.groupDataSource = groupDataSource;
        this.locationDataSource = locationDataSource;
    }

    public static SharedNonCacheRepository getInstance(GroupDataSource groupDataSource, LocationDataSource locationDataSource) {
        if (instance == null) {
            instance = new SharedNonCacheRepository(groupDataSource, locationDataSource);
        }
        return instance;
    }


}
