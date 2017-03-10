/*
 * Kiosk Mode (aka Screen Pinning, aka Task Locking) demo for Android 5+
 *
 * Copyright 2015, SDG Systems, LLC
 */

package sdg.example.kiosk_mode;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import static java.lang.System.out;
import static sdg.example.kiosk_mode.BootState.*;
import static sdg.example.kiosk_mode.BootState.BootNote;
import static sdg.example.kiosk_mode.R.id.button2;
import static sdg.example.kiosk_mode.R.id.webview1;


public class KioskModeDemo extends Activity {
    private final static String TAG = "KioskModeDemo";
    private Button button;
    private boolean inKioskMode = false;
    private DevicePolicyManager dpm;
    private ComponentName deviceAdmin;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void setKioskMode(boolean on) {
        String item;
        item = "org.mozilla.fennec_jim";
        //showToast("Kiosk State " + String.valueOf(on));
        try {
            if (on == false) {
                if (dpm.isLockTaskPermitted(this.getPackageName())) {
                    startLockTask();
                    setFullScreen(this, true);
                    inKioskMode = true;
                    button.setText("Exit Kiosk Mode");
                    dpm.setLockTaskPackages(deviceAdmin, new String[]{item, this.getPackageName()});
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    View button4 = findViewById(R.id.button4);
                    button4.setVisibility(View.GONE);
                    IsExteranlStorageWritable();
                } else {
                    showToast("Kiosk Mode not permitted");
                }
            } else {
                stopLockTask();
                setFullScreen(this, true);
                inKioskMode = false;
                button.setText("Enter Kiosk Mode");
                View button4 = findViewById(R.id.button4);
                button4.setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        }
    }

    /*
    To become the device owner(deviceAdmin) use the adb shell:
      adb shell dpm set-device-owner sdg.example.kiosk_mode/.AdminReceiver
    Replace sdg.example.kiosk_mode with the package name
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        button = (Button) findViewById(R.id.button1);

        deviceAdmin = new ComponentName(this, AdminReceiver.class);
        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        setFullScreen(this, true);
        //BootState bootMsg = new BootState.getBootNote;

        if (getBootNote().contentEquals("null") ){
            setBootNote("Not yet Set");
        }

        Toast.makeText(this, getBootNote(), Toast.LENGTH_LONG).show();
        if (!dpm.isAdminActive(deviceAdmin)) {
            showToast("This app is not a device admin!");
        }
        if (dpm.isDeviceOwnerApp(getPackageName())) {
            dpm.setLockTaskPackages(deviceAdmin,
                    new String[]{getPackageName()});
        } else {
            showToast("This app is not the device owner!");
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        setKioskMode(false);
        View button3 = findViewById(R.id.button3);
        button3.setVisibility(View.GONE);
        View button4 = findViewById(R.id.button4);
        button4.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {

        super.onResume();
        setFullScreen(this, true);
    }

    //public void toggleKioskMode(View view) {setKioskMode(!inKioskMode);}
    public void toggleKioskMode(View view) {
        setKioskMode(inKioskMode);
    }

    public void restoreLauncher(View view) {
        dpm.clearPackagePersistentPreferredActivities(deviceAdmin,
                this.getPackageName());
        showToast("Home activity: " + Common.getHomeActivity(this));
        View button2 = findViewById(R.id.button2);
        button2.setVisibility(View.VISIBLE);
        View button3 = findViewById(R.id.button3);
        button3.setVisibility(View.GONE);
    }

    public void setLauncher(View view) {

        Common.becomeHomeActivity(this);
        View button2 = findViewById(R.id.button2);
        View button3 = findViewById(R.id.button3);
        button2.setVisibility(View.GONE);
        button3.setVisibility(View.VISIBLE);
    }


    public void jtjReply(View view) {
        Intent browser = new Intent();
        browser = browser.setClassName("org.mozilla.firefox", "org.mozilla.gecko.BrowserApp");
        Uri webPage = Uri.parse("http://stlouis-shopper.com/EventKioskindex.html");
        browser = browser.setData(webPage);
        //showToast("JTJ Button reply");
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("org.mozilla.firefox");
        startActivity(browser);
    }

    // starts the fennec_jim kiosk browser
    public void jtjReply2(View view) {
        try {
            stopLockTask();
            Intent browser = new Intent();
            browser = browser.setClassName("org.mozilla.fennec_jim", "org.mozilla.gecko.BrowserApp");
            //Uri webPage = Uri.parse("http://stlouis-shopper.com/EventKioskindex.html");
            Uri webPage = Uri.parse("file:///android_asset/dragNspin.html");
            browser = browser.setData(webPage);
            //showToast("Fennec jim");
            // Kill old browser so that on restart it loads the uri specified above
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses("org.mozilla.fennec_jim");
            startActivity(browser);

        } catch(Exception e) {
            showToast("fennec start faild with: " + e);
        }
    }

    public void showWebView(View view) {
        //showToast("WebView");
        //return;
        WebView kioskWebView = (WebView) findViewById(webview1);
        WebSettings websettings = kioskWebView.getSettings();
        websettings.setJavaScriptEnabled(true);
        kioskWebView.setVisibility(View.VISIBLE);
        kioskWebView.addJavascriptInterface(new WebPageInterface(this), "android");
        //kioskWebView.loadUrl("http://stlouis-shopper.com");
        //kioskWebView.loadUrl("file:///storage/sdcard0/dragNspin.html");
        kioskWebView.loadUrl("file:///storage/sdcard0/jtjsoftware/index.html");
    }

    public void setFullScreen(KioskModeDemo activity, boolean fullscreen) {
        // Hide/show the system notification bar
        Window window = activity.getWindow();
        if (window.getAttributes().FLAG_FULLSCREEN == 1) {
            showToast("Already in fullscreen mode");
            return;}
        int newVis;
        if (Build.VERSION.SDK_INT >= 16) {
            //int newVis;
            if (fullscreen) {
                newVis = View.SYSTEM_UI_FLAG_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    newVis |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                    //} else {
                    //newVis |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }

            } else {
                newVis = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            }
            window.getDecorView().setSystemUiVisibility(newVis);
        } else {
            //window.setFlags(fullscreen ?
            //WindowManager.LayoutParams.FLAG_FULLSCREEN : 0,
            //WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }
    }


    // Check if exteranl storage is read/write
    //public boolean IsExteranlStorageWritable() {
    public void IsExteranlStorageWritable() throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        File storageLocation = new File(sdcard, "/jtjsoftware/index.html");
        String writable = Environment.getExternalStorageState(storageLocation);
        String newDir = Environment.getExternalStorageDirectory() + File.separator+"testDir";
        File newFolder = new File(newDir);
        File newFile = new File(newFolder, "Test.txt");
         if (Environment.MEDIA_MOUNTED.equals(writable)) {
             showToast("External storage " + sdcard + " Mounted");

             if (newFolder.exists()) {
                 showToast(newFolder + " has already been created");
             } else {
                 newFolder.mkdir();
             }

             try {
                  newFile.createNewFile();
                 //showToast(newFile + " was created successfully");
             } catch(IOException e) {
                  showToast("File creation failed: " + e);
             }

         } else {
             showToast(sdcard + "No Location: " + writable);
         }

        try {
        FileOutputStream file = new FileOutputStream(newFile,true);
            Date date = new Date(System.currentTimeMillis());

        //try (OutputStreamWriter out = new OutputStreamWriter(openFileOutput("/storage/sdcard0/testDir/Test.txt", Context.MODE_APPEND))) {
            String fileLine = "NEW Line as we enter kiosk mode: date= " + date.toString() + "\n";
            file.write(fileLine.getBytes());
            file.close();
        } catch(IOException e) {
            showToast("Append to file FAILED: " + e);
        }

         //return true;
    }


    public class WebPageInterface {

        Context myContext;

        WebPageInterface(Context c) {
            myContext = c;
        }

        @JavascriptInterface
          public void formContent(String name, String email) {

            Date date = new Date(System.currentTimeMillis());
            String newDir = Environment.getExternalStorageDirectory() + File.separator+"testDir";
            File newFolder = new File(newDir);
            File newFile = new File(newFolder, "Test.txt");

            try {
                FileOutputStream file = new FileOutputStream(newFile,true);
                String fileLine = date.toString() + "," + name + "," + email + "\n";
                file.write(fileLine.getBytes());
                file.close();
            } catch(IOException e) {
                showToast("Append to file FAILED: " + e);
            }


          }

        //webpage survey form with 26 fields
        @JavascriptInterface
        public void surveyLong(){


        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("KioskModeDemo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
        setFullScreen(this, true);

    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}