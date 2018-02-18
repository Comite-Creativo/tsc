package com.comsab.tscsample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.tscdll.TSCActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;


public class MainActivity extends Activity {

	TSCActivity TscDll = new TSCActivity();

    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();
    private LinearLayout view;
    static final int REQUEST_ENABLE_BT = 1;
    private String mac;
    BluetoothAdapter bluetoothAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

        //getting SDcard root path
        root = new File(Environment.getExternalStorageDirectory().toString()+"/odk/instances");
        getfile(root);

        for (int i = 0; i < fileList.size(); i++) {
            TextView textView = new TextView(this);
            textView.setText(fileList.get(i).getName());
            textView.setPadding(5, 5, 5, 5);

            System.out.println(fileList.get(i).getName());

            view.addView(textView);
        }

        //setting up the bluethooth adapter and check the status
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        CheckBlueToothState();

        final Button button = findViewById(R.id.button_id);


        test = (Button) findViewById(R.id.button1);

        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "Bluetooth no está habilitado!", Toast.LENGTH_LONG).show();
                TscDll.openport(mac);
                TscDll.setup(70, 110, 4, 4, 0, 0, 0);
                TscDll.clearbuffer();
                TscDll.sendcommand("SET TEAR ON\n");
                TscDll.sendcommand("SET COUNTER @1 1\n");
                TscDll.sendcommand("@1 = \"0001\"\n");
                TscDll.sendcommand("TEXT 100,300,\"3\",0,1,1,@1\n");
                TscDll.barcode(100, 100, "128", 100, 1, 0, 3, 3, "123456789");
                TscDll.printerfont(100, 250, "3", 0, 1, 1, "987654321");
                TscDll.printlabel(2, 1);

                TscDll.closeport(700);
            }

        });

	}

    //method to get the list of XML files
    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    getfile(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".xml"))
                    {
                        fileList.add(listFile[i]);
                    }
                }
            }
        }
        return fileList;
    }


    //method to check the status of the bluetooth
    private void CheckBlueToothState(){
        if (bluetoothAdapter == null){
            Toast.makeText(this, "Bluetooth no soportado", Toast.LENGTH_LONG).show();
        }else{
            if (bluetoothAdapter.isEnabled()){
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        mac=device.getAddress();
                        Toast.makeText(this, deviceHardwareAddress, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this, "no hay dispositivos", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "Bluetooth no está habilitado!", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            CheckBlueToothState();
        }
    }
}
