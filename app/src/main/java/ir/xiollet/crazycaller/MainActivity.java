package ir.xiollet.crazycaller;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button mAddButton;

    private EditText mEditText;

    private ListView mListView;

    private Context context;

    private int NextCallPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        mAddButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.edittext);
        mListView = (ListView) findViewById(R.id.listview);


        final ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, Utils.getPhoneNumbers(context));
        mListView.setAdapter(adapter);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                                Utils.removePhoneNumber(context, textView.getText().toString());
                                ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, Utils.getPhoneNumbers(context));
                                mListView.setAdapter(adapter);
//                                Snackbar.make(view, "Phone number removed.", Snackbar.LENGTH_LONG)
//                                        .setAction("Action", null).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("do you want delete this number?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return false;
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEditText.getText().toString())) {
                    Snackbar.make(view, "بابا شماره تلفن رو اول وارد کن :|", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mEditText.setError("Phone number");
                } else {
                    Utils.addPhoneNumber(context, mEditText.getText().toString());
                    mEditText.setText("");
                    ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, Utils.getPhoneNumbers(context));
                    mListView.setAdapter(adapter);
                    Snackbar.make(view, "Phone number added.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        // add PhoneStateListener
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        // add button listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextCall();
                Snackbar.make(view, "start calling...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void nextCall() {
        Adapter adapter = mListView.getAdapter();
        String nextPhone = (String) adapter.getItem(NextCallPosition % adapter.getCount());
        NextCallPosition++;
        if (TextUtils.isEmpty(nextPhone)) {
            nextCall();
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + nextPhone));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Snackbar.make(mEditText, "I have no call Permission :/", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
            context.startActivity(callIntent);
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");
                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");
                nextCall();
//
//                if (isPhoneCalling) {
//
//                    Log.i(LOG_TAG, "restart app");
//
//                    // restart app
////                    Intent i = getBaseContext().getPackageManager()
////                            .getLaunchIntentForPackage(
////                                    getBaseContext().getPackageName());
////                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                    startActivity(i);
//                    nextCall();
//                    isPhoneCalling = false;
//                }

            }
        }
    }
}
