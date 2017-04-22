package appcmpt.example.com.fast4;

/**
 * Created by lalu on 4/12/2017.
 */



/**
 * Created by lalu on 4/7/2017.
 */
public class DataDriverId {
    private static DataDriverId  dataObject = null;

    private DataDriverId () {
        // left blank intentionally
    }

    public static DataDriverId  getInstance() {
        if (dataObject == null)
            dataObject = new DataDriverId ();
        return dataObject;
    }
    private String distributor_id="";;

    public String getDistributor_id() {
        return distributor_id;
    }

    public void setDistributor_id(String distributor_id) {

        this.distributor_id = distributor_id;
    }
}

