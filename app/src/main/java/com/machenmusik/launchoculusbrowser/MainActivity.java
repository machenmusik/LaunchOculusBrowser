package com.machenmusik.launchoculusbrowser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Log information on the received intent.
        Intent i = getIntent();
        Log.d(this.getPackageName(), "getAction() " + i.getAction());
        Log.d(this.getPackageName(), "getType() " + i.getType());
        Log.d(this.getPackageName(), "getDataString() " + i.getDataString());
        Log.d(this.getPackageName(), "getStringExtra(EXTRA_TEXT) " + i.getStringExtra(Intent.EXTRA_TEXT));

        // Form the intent to launch the Oculus Browser.
        Intent o = new Intent();
        o.setAction(Intent.ACTION_MAIN);
        o.setClassName("com.oculus.vrshell", "com.oculus.vrshell.MainActivity");
        o.setData(Uri.parse("apk://com.oculus.browser"));

        // Prepare to sift through any provided text for URI to open.
        Pattern uriPattern = android.util.Patterns.WEB_URL;
        String text = null;
        String uri = null;

        if (Intent.ACTION_MAIN.equals(i.getAction())) {
            // When started from home or app menu, no URI or text is passed in the intent.
            // Check to see whether there is anything on the clipboard.
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
                // Coerce clip to text string.
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                text = item.coerceToText(getApplicationContext()).toString();
            }
        }
        else
        if (Intent.ACTION_VIEW.equals(i.getAction())) {
            // Coerce URI to be viewed to text string.
            text = i.getDataString();
        }
        else
        if (Intent.ACTION_SEND.equals(i.getAction())) {
            // Collect the sent text string.
            text = i.getStringExtra(Intent.EXTRA_TEXT);
        }

        // Match the first URI in the text string, if any.
        if (text != null) {
            Matcher uriMatcher = uriPattern.matcher(text);
            if (uriMatcher.find()) {
                uri = uriMatcher.group();
            }
        }

        // Add the URI to the browser intent, if any.
        if (uri != null) {
            Log.i(this.getPackageName(), "uri " + uri);
            o.putExtra("uri", uri);
        }

        // If the browser intent resolves to an activity, start it.
        if (o.resolveActivity(getPackageManager()) != null) {
            startActivity(o);
        }

        // Finished!
        finishAffinity();
    }
}