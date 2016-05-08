package tacos.vegas.geonotes;

/**
 * Created by scottsasaki on 5/6/16.
 */
public class DataModel {
    String name;
    String timestamp;
    String note;
    String feature;
    double dataLat;
    double dataLng;

    public DataModel(String name, String timestamp, String version_number, String feature, double dataLat, double dataLng ) {
        this.name=name;
        this.timestamp=timestamp;
        this.note =version_number;
        this.feature=feature;
        this.dataLat=dataLat;
        this.dataLng=dataLng;

    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getNote() {
        return note;
    }

    public String getFeature() {
        return feature;
    }

    public double getDataLat() {
        return dataLat;
    }

    public double getDataLng() {
        return dataLng;
    }

}
