package bitcoin.com.bicoinprice;

import android.app.Application;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by Srinath on 27/10/17.
 */

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .schemaVersion(2) // Must be bumped when the schema changes
//                .migration(new RealmMigration() {
//                    @Override
//                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
//
//                    }
//                }) // Migration to run
//                .build();

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

    }
}
