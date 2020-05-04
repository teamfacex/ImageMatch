package app.com.imagecheck.ServiceApi;


import app.com.imagecheck.ImagesUpload;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("match_faces")
    Call<ImagesUpload> uploadImages(@Header("user_id") String user_id,
                                    @Part MultipartBody.Part imageFile1,
                                    @Part MultipartBody.Part imageFile2);

}
