package cassanra;

import com.datastax.driver.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by hunterhug on 16-9-9.
 */


public class CassandraUtils {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    Cluster cluster;
    String database;
    String hosts[];
    Session cassandra;

    public Session getConnecttion() {
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setConsistencyLevel(ConsistencyLevel.QUORUM);
        cluster = Cluster.builder().addContactPoints(hosts).withQueryOptions(queryOptions).build();
        cassandra = cluster.connect(database);
        return cassandra;
    }

    public CassandraUtils(DbConfig cassandraConfig) {
        database = cassandraConfig.dataBase;
        hosts = cassandraConfig.host.split(",");
    }

    //没有用到
    public void destory() {
        try {
            cassandra.close();
            cluster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class DbConfig implements Serializable {
        public String host;
        public String dataBase;
    }

    public static void main(String[] a){
        DbConfig config = new DbConfig();
        config.dataBase="clic";
        config.host="192.168.11.74";
        CassandraUtils c=new CassandraUtils(config);
        c.getConnecttion();
        String tableName="info_mz_paign_spot";
        String sql="SELECT * from " + tableName + " where c_id=1  and s_id=1;";
        ResultSet r =c.cassandra.execute(sql);
        while(r.iterator().hasNext()){
            System.out.println(r.iterator().next().getLong(0));
        }
        c.destory();
    }
}

