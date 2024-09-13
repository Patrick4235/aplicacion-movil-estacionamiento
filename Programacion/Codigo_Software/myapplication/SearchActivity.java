package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.AgentName;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import com.google.rpc.context.AttributeContext;

public class SearchActivity extends AppCompatActivity {

    TextView consulta;
    TextView respuesta;
    boolean isMicronofoActivado = false;



    //Dialogflow
    AgentName agentName = AgentName.of("geoproject-424022");
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        Button botonVolver = (Button) findViewById(R.id.btnVolverID);

        consulta = (TextView) findViewById(R.id.txtConsultaID);

        ImageButton microfono = (ImageButton) findViewById(R.id.btnMicrofonoID);

        respuesta = (TextView) findViewById(R.id.txtMensajeID);


        try {
            InputStream stream = this.getResources().openRawResource(R.raw.credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            consulta.setText("projectId : " + projectId);
        } catch (Exception e) {
            consulta.setText("setUpBot: " + e.getMessage());
        }


        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SearchActivity.this.finish();
            }
        });

        microfono.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent
                                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                                Locale.getDefault());
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Cual es tu consulta");

                try {
                    startActivityForResult(intent, 100);
                }
                catch (Exception e) {
                    Toast.makeText(SearchActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> resultado = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            consulta.setText(resultado.get(0));

            // Crear una consulta (texto del usuario)
            String queryText = resultado.get(0);
            TextInput textInput = TextInput.newBuilder().setText(queryText).setLanguageCode("es").build();
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            // Enviar la consulta y obtener la respuesta
            DetectIntentResponse response = sessionsClient.detectIntent(sessionName, queryInput);
            QueryResult queryResult = response.getQueryResult();

            respuesta.setText(queryResult.getFulfillmentText());

            String intentName = queryResult.getIntent().getDisplayName();

            solicitud(intentName);
            // Mostrar la respuesta
            //respuesta.setText("Query Text: " + queryResult.getQueryText() + "\n"+
              //      "Detected Intent: " + queryResult.getIntent().getDisplayName()+"\n"+

                //    "Response: " + queryResult.getFulfillmentText());
        }

    }

    public void solicitud(String intentName){
        if(intentName.equals("FindParking")){
            Intent pantallaMain = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(pantallaMain);
        }else if(intentName.equals("Registro")){
            Intent pantallaRegistro = new Intent(SearchActivity.this, RegisterActivity.class);
            startActivity(pantallaRegistro);
        }else if(intentName.equals("InicioSesion")){
            Intent pantallaInicioSesion = new Intent(SearchActivity.this, LoginActivity.class);
            startActivity(pantallaInicioSesion);
        }
    }
}