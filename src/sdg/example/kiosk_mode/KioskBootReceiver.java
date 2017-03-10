package sdg.example.kiosk_mode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.lang.StringBuffer;

import static android.R.attr.text;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class KioskBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        BootState.setBootNote("Receiver entered");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            BootState.setBootNote("BOOT Receiver Called");
            Intent activityIntent = new Intent(context, KioskModeDemo.class);
            //Intent startActivityIntnent = new Intent(context, KioskModeDemo.class);
            try {
                activityIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
            } catch (Exception e)  {
                BootState.setBootNote("Boot Start Failure" + e);

            }
            //startActivity(activityIntent);
            //KioskModeDemo startup;
            //startup = new KioskModeDemo();
            //startup.setFullScreen(startup, true);
            //Toast.makeText(context, "Boot Receiver Called", Toast.LENGTH_LONG).show();
            //bootNotice.BootNote = "Hello From Boot Receiver";
            //BootState.setBootNote("Receiver Called");
        }

    }

}

