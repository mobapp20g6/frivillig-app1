package no.ntnu.mobapp20g6.app1.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author TrymV
 */
public class Picture {

    @Expose
    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }
}
