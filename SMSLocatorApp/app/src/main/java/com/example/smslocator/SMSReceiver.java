package com.example.smslocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu : pdus) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
            String msgBody = message.getMessageBody();
            String sender = message.getOriginatingAddress();

            if (msgBody.trim().equalsIgnoreCase("LOCATE")) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    String response = "Location: https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
                    SmsManager.getDefault().sendTextMessage(sender, null, response, null, null);
                } else {
                    SmsManager.getDefault().sendTextMessage(sender, null, "Unable to fetch location.", null, null);
                }
            }
        }
    }
}