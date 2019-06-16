package com.example.sujet_sequence_1.api;

import com.example.sujet_sequence_1.models.AuthenticateResponse;
import com.example.sujet_sequence_1.models.ProfilListeToDo;
import com.example.sujet_sequence_1.models.ListeToDo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface APIInterface {
    @POST("authenticate")
    Call<AuthenticateResponse> authenticate(@Query("user") String pseudo, @Query("password") String pass);

    @GET("lists")
    Call<ProfilListeToDo> importListeToDoAPI(@Header("hash") String hash);

    @POST("lists")
    Call<ProfilListeToDo> addListeToDoAPI(@Header("hash") String hash, @Query("label") String label);

    @GET("lists/{id}/items")
    Call<ListeToDo> importListeItemAPI(@Header("hash") String hash, @Path("id") int id);

    @POST("lists/{id}/items")
    Call<ProfilListeToDo> addItemToDoAPI(@Header("hash") String hash, @Path("id") int id, @Query("label") String label);

    @POST("lists/{id}/items")
    Call<ProfilListeToDo> addItemToDoAPI(@Header("hash") String hash, @Path("id") int id, @Query("label") String label, @Query("url") String url);

    @PUT("lists/{idList}/items/{idItem}")
    Call <ListeToDo> checkItemAPI(@Header("hash") String hash,@Path("idList") int listId, @Path("idItem") int itemId,@Query("check") int check);
}
