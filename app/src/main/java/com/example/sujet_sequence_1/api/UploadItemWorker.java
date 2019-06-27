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
import com.example.sujet_sequence_1.models.ProfilListeToDo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadItemWorker extends Worker {
    private String newItemName;
    private String url;
    private Integer id;

    public UploadItemWorker(@NonNull Context context,
                            @NonNull WorkerParameters params) {
        super(context, params);
        Log.i("PMR", "Construit Work sending");
    }

    @NonNull
    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.
        newItemName = getInputData().getString("newListName");
        id = getInputData().getInt("id", -1);
        url = getInputData().getString("url");

        Log.i("PMR", newItemName + " liste id : " + id.toString());

        Log.i("PMR", "appelAPIAddAListeToDo(newListName)");
        try {
            appelAPIAddItem();
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
     * Appel l'API pour ajouter un item à la liste affichée. Récupère aussi les nouvelles données et préviens l'adaptateur.
     *
     * @throws GetPreferenceException : si elle ne peut pas récupérer le hash ou l'url
     */
    private void appelAPIAddItem() throws GetPreferenceException {
        final String baseUrl = recupPreference("baseUrl");
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
        final String hash = recupPreference("hash");
        Call<ProfilListeToDo> call;
        if ("".equals(url)) {
            call = api.addItemToDoAPI(hash, id, newItemName);
        } else {
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                url = "http://" + url;
            }
            call = api.addItemToDoAPI(hash, id, newItemName, url);
        }
        call.enqueue(new Callback<ProfilListeToDo>() {
            @Override
            public void onResponse(Call<ProfilListeToDo> call, Response<ProfilListeToDo> response) {
                if (response.isSuccessful()) {
                    //En cas de succès, on récupère les listes avec l'API
                    Log.i("PMR", "Liste ajoutées avec succès");
                } else {
                    Log.i("PMR", "Erreur lors de l'ajout d'un item");
                }
            }

            @Override
            public void onFailure(Call<ProfilListeToDo> call, Throwable t) {
                Log.i("PMR", "Erreur lors de l'ajout d'un item");
            }
        });
    }
}
