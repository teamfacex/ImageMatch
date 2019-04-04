package app.com.imagecheck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.PrecomputedText;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vlk.multimager.activities.GalleryActivity;
import com.vlk.multimager.activities.MultiCameraActivity;
import com.vlk.multimager.adapters.GalleryImagesAdapter;
import com.vlk.multimager.utils.Constants;
import com.vlk.multimager.utils.Image;
import com.vlk.multimager.utils.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import app.com.imagecheck.ProgressBar.Loader;
import app.com.imagecheck.ServiceApi.APIServiceFactory;
import app.com.imagecheck.ServiceApi.ApiService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView output_tv;

    Button add_images;

    RecyclerView recyclerView;

    public ApiService apiService;

    public Loader loader;

//    please add your userKey

    String userKey = "--YOUR KEY--";

    //    please add  your userId
    String userId = "--YOUR USER ID--";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output_tv = findViewById(R.id.outPut_tv);
        add_images = findViewById(R.id.add_image);
        recyclerView = findViewById(R.id.images);
        apiService = APIServiceFactory.getRetrofit().create(ApiService.class);
        loader = new Loader(MainActivity.this);


        onClickevents();
    }

    private void onClickevents() {

        add_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                Params params = new Params();
                params.setCaptureLimit(2);
                params.setPickerLimit(2);
                intent.putExtra(Constants.KEY_PARAMS, params);
                startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.TYPE_MULTI_CAPTURE:
                handleResponseIntent(intent);
                break;
            case Constants.TYPE_MULTI_PICKER:
                handleResponseIntent(intent);
                break;
        }
    }

    private void handleResponseIntent(Intent intent) {
        ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(getColumnCount(), GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(mLayoutManager);
        GalleryImagesAdapter imageAdapter = new GalleryImagesAdapter(this, imagesList, getColumnCount(), new Params());
        recyclerView.setAdapter(imageAdapter);

        uploadPhotos(imagesList);
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailDpWidth = getResources().getDimension(R.dimen.thumbnail_width) / displayMetrics.density;
        return (int) (dpWidth / thumbnailDpWidth);
    }


    private void uploadPhotos(ArrayList<Image> imagesList) {

        try {
            show_loader();

            for (Image file : imagesList) {
                Log.e("file", file.imagePath);
            }

            File file = new File(imagesList.get(0).imagePath);
            File file1 = new File(imagesList.get(1).imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("img_1", file.getName(), requestFile);
            RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file1);
            MultipartBody.Part body1 = MultipartBody.Part.createFormData("img_2", file.getName(), requestFile1);

//
            apiService.uploadImages(userKey, userId, body, body1).enqueue(new Callback<ImagesUpload>() {
                @Override
                public void onResponse(Call<ImagesUpload> call, Response<ImagesUpload> response) {

                    dismiss_loader();

                    Log.e("response", "--" + response.raw().code());

                    Log.e("response", new Gson().toJson(response.body()));

                    Log.e("res", response.body().toString());

                    if (response.raw().code() == 200) {

                        String jsonResponse = new Gson().toJson(response.body());
                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            if (jsonObject.toString().contains("confidence")) {
//
                                output_tv.setText("Output:" + response.body().getConfidence());
                            } else if (jsonObject.toString().contains("error")) {
                                output_tv.setText("Output:" + response.body().getMsg());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        JsonObject post = new JsonObject().get(response.body().toString();
//                        if (post.toString().contains("confidence")) {
//
//                            output_tv.setText("Output:" + response.body().getConfidence());
//                        } else if (post.toString().contains("error")) {
//                            output_tv.setText("Output:" + response.body().getMsg());
//                        }
                    }

                }

                @Override
                public void onFailure(Call<ImagesUpload> call, Throwable t) {


                    dismiss_loader();

                    Log.e("onFailure", t.getMessage());

                }
            });

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    public void show_loader() {
        if (loader != null) {
            loader.show();
        }
    }

    public void dismiss_loader() {
        if (loader != null) {
            loader.dismiss();
        }
    }
}
