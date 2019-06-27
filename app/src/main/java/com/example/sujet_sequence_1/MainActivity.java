package com.example.sujet_sequence_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sujet_sequence_1.api.APIClient;
import com.example.sujet_sequence_1.api.APIInterface;
import com.example.sujet_sequence_1.models.AuthenticateResponse;
import com.example.sujet_sequence_1.settings.DecryptException;

import java.security.GeneralSecurityException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.sujet_sequence_1.settings.KeyStoreHelper.createKeys;
import static com.example.sujet_sequence_1.settings.KeyStoreHelper.decrypt;
import static com.example.sujet_sequence_1.settings.KeyStoreHelper.encrypt;

/**
 * Main Activity
 * The User can login in this activity.
 * This is the entry point of the application.
 * If a username and password are stored in the preferences,
 * its log in automatically.
 * When the user is logged in this activity is removed from the stack as it is not necessary anymore.
 */
public class MainActivity extends ParentActivity implements View.OnClickListener {

    private Button button = null; // Boutton pour valider l'entré du pseudo
    private EditText textViewEdtPseudo = null; //Champ texte où l'utilisateur entre le pseudo
    private EditText passwordEdtPass = null; //Champ texte où l'utilisateur entre le mot de passe
    private String KEYSTORE_KEY_ALIAS = "keyPasswordToDoApp"; //Alias pour le stockage de la clé du mot de passe


    /**
     * onCreate est une fonction liée au cycle de vie de l'activité.
     * Elle est appellée lors de la création de l'activité.
     * <p>
     * Tente une authentification implicite de l'utilisateur à l'aide des données stockées dans les préférences
     * Met en place le layout de l'activité et lie les variables d'nistances aux éléments du layout
     * Ajoute le sous-titre à l'actionBar
     *
     * @param savedInstanceState données utilisées si la réinitialisée après avoir été arrêtée
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (verifReseau()) {
            try {
                implicitAuthentication();
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        } else {
            offlineAuthentication();
        }
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        textViewEdtPseudo = findViewById(R.id.textViewEdtPseudo);
        passwordEdtPass = findViewById(R.id.textViewEdtPass);
        button.setOnClickListener(this);
        actionBar.setSubtitle(R.string.BySubtitle);
    }

    /**
     * On charge les préférences lors du démarrage de l'activité.
     * On affiche le contenue de la préférence pseudo (ie. le dernier pseudo rentré) dans le champs texte.
     * Si il n'y a pas de connexion internet, le boutton de connexion est désactivé et une alerte
     * sous forme de toast est envoyée à l'utilisateur.
     */
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        textViewEdtPseudo.setText(settings.getString("pseudo", ""));
        passwordEdtPass.setText("");
        boolean internetAccess = verifReseau();
        button.setEnabled(internetAccess);
        if (!internetAccess) {
            alerter(getResources().getString(R.string.noInternet));
        }
        try {
            createKeys(this, KEYSTORE_KEY_ALIAS);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * On définit le onclick pour l'application qui ici s'applique uniquement au bouton ok.
     * on récupère les préférences et le contenue des champs textes pseudo et password.
     * Si ceux-ci ne sont pas vide, on essaye d'authentifié l'utilisateur auprès de  l'API puis
     * passer à l'activité suivante avec la fonction autentication()
     * Si on veux plus tard ajouter un autre comportement en cas de click, il faura créer un switch
     * en fonction de la vue sur laquelle le click a eu lieu.
     *
     * @param v : vue sur laquelle on a cliqué.
     */
    @Override
    public void onClick(View v) {

        final String pseudo = textViewEdtPseudo.getText().toString();
        String pass = passwordEdtPass.getText().toString();
        if ("".equals(pseudo)) {
            alerter(getResources().getString(R.string.promptforpseudo));
            return;
        }
        if ("".equals(pass)) {
            alerter(getResources().getString(R.string.promptforpass));
            System.out.println("toto");
            return;
        }
        if (!verifReseau()) {
            alerter(getResources().getString(R.string.changeProfilNoInternet));
        } else {
            String baseUrl;
            try {
                baseUrl = recupPreference("baseUrl");
            } catch (GetPreferenceException e) {
                //Si on ne trouve pas d'url dans les préférences on utilise la valeur par défaut de l'url de l'API
                String defaultApiUrl = getResources().getString(R.string.defaultApiUrl);
                updatePreference("baseUrl", defaultApiUrl);
                baseUrl = defaultApiUrl;
            }
            authentication(pseudo, pass, baseUrl);
        }
    }

    /**
     * Création de l'intent et démarrage de l'activiter ChoixListActivity.
     */
    private void goToChoixListActivity() {
        Intent toChoixListActivity;
        toChoixListActivity = new Intent(MainActivity.this, ChoixListActivity.class);
        startActivity(toChoixListActivity);
    }

    /**
     * Stockage de toutes les préférences concernant l'utilisateur.
     *
     * @param pseudo            : pseudo de l'utilisateur
     * @param encryptedPassword : mot de passe chiffré à enregistrer dans les préférences.
     * @param hash              : hash de l'utilisateur utilisé pour faire des requêtes à l'api.
     */
    private void updateAllUserPreferences(String pseudo, String encryptedPassword, String hash) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences((MainActivity.this));
        SharedPreferences.Editor editor = settings.edit();
        // on a enlever le editor.clear() pour pouvoir garder l'url et ne réécrire que les valeurs nécessaires
        editor.putString("hash", hash);
        editor.putString("pseudo", pseudo);
        editor.putString("encryptedPassword", encryptedPassword);
        editor.apply();
    }

    /**
     * Stockage d'une préférence pref
     *
     * @param pref  : clé de la préférence à stocker
     * @param value : valeur de la préférence.
     */
    private void updatePreference(String pref, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences((MainActivity.this));
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(pref, value);
        editor.apply();
    }


    /**
     * Connexion de l'utilisateur et enregistrement des paramètres de connexion
     *
     * @param pseudo  : pseudo de l'utilisateur
     * @param pass    : mot de passe de l'utilisateur
     * @param baseUrl : url de l'API
     */
    private void authentication(String pseudo, final String pass, String baseUrl) {
        APIInterface api = APIClient.createService(APIInterface.class, baseUrl);
        Call<AuthenticateResponse> call = api.authenticate(pseudo, pass);
        call.enqueue(new Callback<AuthenticateResponse>() {
            @Override
            public void onResponse(Call<AuthenticateResponse> call, Response<AuthenticateResponse> response) {
                if (response.isSuccessful()) {
                    AuthenticateResponse authenticateResponse = response.body();
                    String pseudo = call.request().url().queryParameter("user");
                    updateAllUserPreferences(pseudo, encrypt(KEYSTORE_KEY_ALIAS, pass), authenticateResponse.getHash());
                    goToChoixListActivity();
                } else {
                    alerter(getResources().getString(R.string.wrongLogin));
                }
            }

            @Override
            public void onFailure(Call<AuthenticateResponse> call, Throwable t) {
                alerter(getResources().getString(R.string.timeout));
                Log.i(CAT, "authentication failure");
            }
        });
    }

    /**
     * Tente de connecter l'utilisateur à l'aide des informations stockées dans les préférences.
     *
     * @throws AuthenticationException : quand la connexion implicite échoue (préférences incomplètes, érronées
     */
    private void implicitAuthentication() throws AuthenticationException {

        try {
            // Récupération des informations sotckées dans les préférences + déchiffrage du mot de passe
            final String pseudo = recupPreference("pseudo");
            final String encryptedPassword = recupPreference("encryptedPassword");
            final String password;
            if ("".equals(encryptedPassword) || "".equals(pseudo)) {

                throw new AuthenticationException();
            }
            try {
                password = decrypt(KEYSTORE_KEY_ALIAS, encryptedPassword);
            } catch (DecryptException e) {
                e.printStackTrace();
                throw new AuthenticationException();
            }
            //Authentification auprès de l'API à l'aide des informations précédentes.
            APIInterface api = APIClient.createService(APIInterface.class, recupPreference("baseUrl"));
            Call<AuthenticateResponse> call = api.authenticate(pseudo, password);
            call.enqueue(new Callback<AuthenticateResponse>() {
                @Override
                public void onResponse(Call<AuthenticateResponse> call, Response<AuthenticateResponse> response) {
                    if (response.isSuccessful()) {
                        AuthenticateResponse authenticateResponse = response.body();
                        updatePreference("hash", authenticateResponse.getHash());
                        goToChoixListActivity();
                    } else {
                        alerter("failure on implicit login");
                        onFailure(call, new AuthenticationException());
                    }
                }

                @Override
                public void onFailure(Call<AuthenticateResponse> call, Throwable t) {
                    alerter(getResources().getString(R.string.timeout));
                    Log.i(CAT, "implicite authentication failure");
                }
            });

        } catch (GetPreferenceException e) {
            throw new AuthenticationException();
        }
    }

    private void offlineAuthentication() {
        try {
            if (!"".equals(recupPreference ("pseudo"))){
                goToChoixListActivity();
            }
        } catch (GetPreferenceException e) {
            alerter(getResources().getString(R.string.please_connect));
            e.printStackTrace();
        }
    }
}

