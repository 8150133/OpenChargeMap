package pt.ipp.estg.openchargesmap.API;



import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenChargeMapAPI {

    public static final String url = "https://api.openchargemap.io/v2/";

    @GET("poi")

    Call<PostsList> postsCatalog();

}
