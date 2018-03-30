package com.is416.smujio.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.is416.smujio.EventActivity;
import com.is416.smujio.JioActivity;
import com.is416.smujio.LandingActivity;
import com.is416.smujio.R;
import com.is416.smujio.model.Event;
import com.is416.smujio.util.ActivityManager;
import com.is416.smujio.util.General;
import com.is416.smujio.util.SharedPreferenceManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EventSocketService extends Service {
    private Context mContext;
    private long account_id;
    private OkHttpClient client;
    private WebSocket ws;

    public EventSocketService() {
        super();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = this;
        try {
            this.account_id = intent.getLongExtra(General.ACCOUNTID, -1);
        }catch (NullPointerException ne){
            if (!SharedPreferenceManager.get(General.PERSIST, mContext).equals(SharedPreferenceManager.nullable)){
                this.account_id = Long.parseLong(SharedPreferenceManager.get(General.ACCOUNTID,mContext));
            }else {
                stopSelf();
            }
        }
        this.client = new OkHttpClient();
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void init(){
        Request request = new Request.Builder().url("ws://www.card-digi.com:8080/Jio/eventSocket").build();
        EventWebSocketListener listener = new EventWebSocketListener();
        ws = client.newWebSocket(request, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent it = new Intent();
        it.putExtra(General.ACCOUNTID, this.account_id);
        it.setClass(mContext, EventSocketService.class);
        mContext.startService(it);
    }

    private void pushNotification(String title ,String content){
        try {
            Intent it = new Intent(mContext, LandingActivity.class);
            PendingIntent pit = PendingIntent.getActivity(mContext, 0, it, 0);
            NotificationManager mNManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Bitmap largeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
            Notification.Builder mBuilder = new Notification.Builder(this);
            mBuilder.setContentTitle(title)
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.logo)
                    .setLargeIcon(largeBitmap)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(content))
                    .setAutoCancel(true)
                    .setContentIntent(pit);
            Notification notify1 = mBuilder.build();
            mNManager.notify(1, notify1);
        }catch (Exception e){

        }
    }

    private final class EventWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            //System.out.println("Connected");
            webSocket.send(General.SOCKETREGISTER + ":" + account_id);
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            String[] params = text.split(":");
            switch (params[0]){
                case General.SOCKETUPDATE:
                    updateEvent();
                    break;
                case General.SOCKETCLOSE:
                    closeEvent();
                    break;
                case General.SOCKETADDNEWEVENT:
                    addEvent(Long.parseLong(params[1]));
                    break;
                case General.SOCKETREMOVE:
                    removeEvent(Long.parseLong(params[1]));
                    break;
            }
        }


        public void updateEvent(){
            if (!ActivityManager.isRunning() || !General.isForeground){
                pushNotification("Event Update", "Updates!!!!!");
            }else {
                ((EventActivity)ActivityManager.getAc(EventActivity.name)).getMyHandler().post(() -> ((EventActivity)ActivityManager.getAc(EventActivity.name)).updateEvent());
            }
        }

        public void closeEvent(){
            pushNotification("Event closed", "The Owner Has Closed This Event.");
            if (ActivityManager.isRunning() && General.isForeground){
                (ActivityManager.getAc(EventActivity.name)).finish();
            }
        }

        private void addEvent(long id){
            if (ActivityManager.isRunning()){
                if (((JioActivity)ActivityManager.getAc(JioActivity.name)).isInit){
                    if (((JioActivity)ActivityManager.getAc(JioActivity.name)).isOnTop){
                        General.UIHandler.post(() -> ((JioActivity)ActivityManager.getAc(JioActivity.name)).updateOneEvent(id));
                    }else{
                        JioActivity.pendingActivity.add(id);
                        if (JioActivity.pendingActivity.size() > 1){
                            pushNotification("New event","New events around you");
                        }
                    }
                }
            }
        }

        private void removeEvent(long id){
            if (ActivityManager.isRunning()){
                if (((JioActivity)ActivityManager.getAc(JioActivity.name)).isInit){
                    if (((JioActivity)ActivityManager.getAc(JioActivity.name)).isOnTop){
                        General.UIHandler.post(() -> ((JioActivity)ActivityManager.getAc(JioActivity.name)).removeOneEvent(id));
                    }else{
                        JioActivity.pendingRemoveActivity.add(id);
                    }
                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            System.out.println(bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            System.out.println("closing");
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            System.out.println("Error : " + t.getMessage());
        }
    }
}
