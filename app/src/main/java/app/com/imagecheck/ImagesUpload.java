package app.com.imagecheck;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ImagesUpload {
    @SerializedName("confidence")
    @Expose
    private String confidence;

    @SerializedName("error")
    @Expose
    private String msg;

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}