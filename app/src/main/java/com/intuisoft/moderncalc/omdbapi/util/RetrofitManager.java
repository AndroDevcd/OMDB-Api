package com.intuisoft.moderncalc.omdbapi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.intuisoft.moderncalc.omdbapi.Omdb;
import com.intuisoft.moderncalc.omdbapi.data.SearchResponse;
import com.intuisoft.moderncalc.omdbapi.data.SearchResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class RetrofitManager {

    private static OmdbApiInterface omdbApiInterface;
    private static OkHttpClient okClient;
    public static Retrofit client;
    public static final String SERVER_BASE_URL = Omdb.BASE_URI;


    public static OmdbApiInterface getClient() {
        if (omdbApiInterface == null) {

            File httpCacheDirectory = new File(Omdb.getContext().getCacheDir(), "responses");
            Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
            okClient = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();

                            Request.Builder reqBuilder = request.newBuilder();
                            reqBuilder.addHeader("Content-Type", "application/json;charset=utf-8")
                                    .addHeader("Accept","application/json");

                            request = reqBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .cache(cache)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .build();

            //Create gson converter that handles long date format
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            builder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                        context) {
                    return src == null ? null : new JsonPrimitive(src.getTime());
                }
            });
            Gson gson = builder.create();

            client = new Retrofit.Builder()
                    .baseUrl(SERVER_BASE_URL)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())

                    .build();
            omdbApiInterface = client.create(OmdbApiInterface.class);
        }
        return omdbApiInterface;
    }

    public static void clearCache() {
        if (okClient != null && okClient.cache() != null ) {
            try {
                okClient.cache().evictAll();
            } catch(NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeFromCache(String urlString) {
        try {
            // If the app is started with notification and a call to clear cache is performed before the first getClient then then cache call fails.
            if (client == null) {
                getClient();
            }

            Iterator<String> it = okClient.cache().urls();

            while (it.hasNext()) {
                String next = it.next();

                if (next.contains(urlString)) {
                    it.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OmdbApiInterface {
        @GET(".")
        Call<SearchResponse> performSearch(@Query("s") String searchParams, @Query("apikey") String apiKey);

        @GET(".")
        Call<SearchResponse> performFilteredSearch(@Query("s") String searchParams, @Query("type") String filter, @Query("apikey") String apiKey);
    }
}
