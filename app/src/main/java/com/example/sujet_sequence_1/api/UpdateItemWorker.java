package com.example.sujet_sequence_1.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sujet_sequence_1.GetPreferenceException;
import com.example.sujet_sequence_1.api.APIClient;
import com.example.sujet_sequence_1.api.APIInterface;
import com.example.sujet_sequence_1.models.ListeToDo;
import com.example.sujet_sequence_1.models.ProfilListeToDo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateItemWorker extends Worker {
    private Integer idItem;
    private Integer idList;
    private Integer check;

    public UpdateItemWorker(@NonNull Context context,
                            @NonNull WorkerParameters params) {
        super(context, params);
        Log.i("PMR", "Construit Work sending");
    }

    @NonNull
    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.
        check = getInputData().getInt("check", -1);
        idList = getInputData().getInt("idList", -1);
        idItem = getInputData().getInt("idItem", -1);


        Log.i("PMR", idItem.toString() + " liste id : " + idList.toString());

        try {
            appelAPIUpdateItem();
        } catch (GetPreferenceException e) {
            e.printStackTrace();
        }
        Log.i("PMR", "WIP sending");
        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }

    public String recupPreference(String pref) throws GetPreferenceException {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String storedString = settings.getString(pref, "");
        if (!"".equals(storedString)) {
            return storedString;
        } else {
            throw new GetPreferenceException();
        }
    }

    /**
     * Appel l'API pour mettre à jour un item à la liste affichée. Récupère aussi les nouvelles données et préviens l'adaptateur.
     *
     * @throws GetPreferenceException : si elle ne peut pas récupérer le hash ou l'url
     */
    private void appelAPIUpdateItem() throws GetPreferenceException {
        final String baseUrl = recupPreference("baseUrl");
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
        final String hash = recupPreference("hash");
        Call<ListeToDo> call = api.checkItemAPI(hash, idList, idItem,check);
        call.enqueue(new Callback<ListeToDo>() {
            @Override
            public void onResponse(Call<ListeToDo> call, Response<ListeToDo> response) {
                if (response.isSuccessful()) {
                    Log.i("PMR","Item checked");
                } else {
                    Log.i("PMR","Error checking Item");
                }
            }

            @Override
            public void onFailure(Call<ListeToDo> call, Throwable t) {
                Log.i("PMR", "Erreur lors du (dé)cochage d'un item");
            }
        });

    }
}
