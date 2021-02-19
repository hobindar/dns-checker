package ca.hobin.xhsddsthsfthjsfgjrsyjfyssfytj.dnschecker;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private static final String PREF_NAME = "default";
    private static final String PREF_CURRENT = "lost_dns";
    private static final String PREF_LONGEST = "lost_dnsn";
    private static final long REPEAT_INTERVAL = TimeUnit.SECONDS.toMillis(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.main_text);

        MainViewModel.getViewModel().getFailed().observe(this, hasFailed -> {
            textView.setText(String.format("%s", hasFailed));
        });

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        textView.setText(String.format("%s", preferences.getInt(PREF_LONGEST, 0)));

        scheduleNext(this);
    }

    private static void scheduleNext(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class),
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + REPEAT_INTERVAL, pendingIntent);
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("Darren", "Whoa ho ho");
            scheduleNext(context);
            new Thread(() -> {
                Log.e("Darren", "Thread running");
                SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

                int currentChain = preferences.getInt(PREF_CURRENT, 0);
                try {
                    InetAddress.getByName("www.google.ca");
                    currentChain = 0;
                } catch (UnknownHostException e) {
                    currentChain += 1;
                }

                int longestChain = preferences.getInt(PREF_LONGEST, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(PREF_CURRENT, currentChain);
                if (currentChain > longestChain) {
                    editor.putInt(PREF_LONGEST, currentChain);
                    MainViewModel.getViewModel().setFailed(currentChain);
                }
                editor.commit();
            }).start();
        }
    }

}