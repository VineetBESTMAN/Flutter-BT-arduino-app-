//Change 'bluetoothcar' to your project's name
package com.example.bluetoothcar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothSocket btSocket = null;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    OutputStream outStr = null;
    BluetoothDevice hc05 = null;
    static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final int REQUEST_ENABLE_BT = 1;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cnct = (Button) findViewById(R.id.connect);
        Button front = (Button) findViewById(R.id.front);
        Button back = (Button) findViewById(R.id.back);
        Button left = (Button) findViewById(R.id.left);
        Button right = (Button) findViewById(R.id.right);
        TextView address = (TextView) findViewById(R.id.address);
        TextView txt = (TextView) findViewById(R.id.text);

        if (btAdapter == null) {
            System.out.println("Bluetooth not supported");
        } else {
            if (!btAdapter.isEnabled()) {
                // Bluetooth is not enabled, request user permission to enable it.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        cnct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!connected) {

                    boolean allowConnect = true;

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    try {
                        hc05 = btAdapter.getRemoteDevice(address.getText().toString());
                    } catch (Exception e){
                        allowConnect = false;
                        txt.setText("Connection Failed!\n\nThe HC-05 BT Module Address may be incorrect. Enter the correct Address and tap on 'CONNECT'");
                    }

                    int i = 0;

                    if (allowConnect){

                        do {

                            try {
                                btSocket = hc05.createRfcommSocketToServiceRecord(uuid);
                                btSocket.connect();
                                txt.setText("Connected Successfully to:\n" + hc05.getName());
                                address.setText("");
                                cnct.setText("Disconnect");
                                connected = true;
                            }
                            catch (Exception e) {
                                allowConnect = false;
                                txt.setText("Connection Failed!\n\nThe HC-05 BT Module Address may be incorrect. Enter the correct Address and tap on 'CONNECT' ");
                                break;
                            }
                            
                            i++;

                        } while(!btSocket.isConnected() && i < 10);

                    }

                }

                else {
                    try {
                        btSocket.close();
                        cnct.setText("Connect");
                        connected = false;
                        txt.setText("Disconnected");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });

        front.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sendData(1);
                    txt.setText("Front");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    sendData(0);
                    txt.setText("Stop");
                }
                return false;
            }
        });

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sendData(2);
                    txt.setText("Back");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    sendData(0);
                    txt.setText("Stop");
                }
                return false;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sendData(3);
                    txt.setText("Left");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    sendData(0);
                    txt.setText("Stop");
                }
                return false;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sendData(4);
                    txt.setText("Right");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    sendData(0);
                    txt.setText("Stop");
                }
                return false;
            }
        });

    }

    public void sendData(int data){

        try {
            outStr = btSocket.getOutputStream();
            outStr.write(data);
        } 
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                //txt.setText("Bluetooth is now enabled");
            } else {
                // User declined to enable Bluetooth, handle this case.
            }
        }
    }

}
