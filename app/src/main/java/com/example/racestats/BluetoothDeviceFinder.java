package com.example.racestats;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

// Imports for OBD2 classes
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.S)
public class BluetoothDeviceFinder extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> devicesArrayAdapter;
    private ArrayList<String> devicesList;

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.S)
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                String deviceInfo = deviceName + "\n" + deviceAddress;
                devicesList.add(deviceInfo);
                devicesArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paired_blue_tooth_list);

        requestPermissions();

        ListView devicesListView = findViewById(R.id.devicesListView);
        devicesList = new ArrayList<>();
        devicesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devicesList);
        devicesListView.setAdapter(devicesArrayAdapter);

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshBluetoothDevices();
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d("bluetooth error", "Device Does Not Support Bluetooth");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Error in permission check here", Toast.LENGTH_SHORT).show();
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        Log.d("Test", "onCreate: Test 106");
        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();
            String deviceInfo = deviceName + "\n" + deviceAddress;
            devicesList.add(deviceInfo);
        }
        devicesArrayAdapter.notifyDataSetChanged();

        Log.d("Test", "Added Device to list 115");

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

        Log.d("Test", "Added Device to list 120");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsBlueToothScan(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
            Log.d("Test", "Added Device to list 129 needs permission");
            return;
        }

        Log.d("Test", "Added Device to list 132");
        bluetoothAdapter.startDiscovery();
        Log.d("Line 142","test");
        devicesListView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("Line 145","Click detected");
            String deviceInfo = devicesArrayAdapter.getItem(position);
            String[] deviceInfoArray = deviceInfo.split("\n");
            String deviceAddress = deviceInfoArray[1];

            BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
            Log.d("selectedDevice BluetoothDevice", String.valueOf(selectedDevice));
            System.out.println("test sys out");

            // Connect to the selected device
            BluetoothSocket socket = null;
            try {
                System.out.println("test sys out");
                Log.d("Trying socket connection", "searching");
                socket = selectedDevice.createInsecureRfcommSocketToServiceRecord(selectedDevice.getUuids()[0].getUuid());
                socket.connect();

                // Request coolant temperature
                try {
                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

                    // Create a command to retrieve the coolant temperature
                    String coolantTempCmd = AvailableCommandNames.ENGINE_COOLANT_TEMP.getValue();

                    System.out.println("system out : " + coolantTempCmd);
                    Log.d("ran through temp", "temp found maybe check below");
                    Log.d("Coolant Temp: ", coolantTempCmd);
                } catch (Exception e) {
                    // handle errors
                    e.printStackTrace();
                    Log.e("Error", e.toString());
                }

                // Now you have the coolant temperature in Celsius
//                Log.d("Coolant Temperature", coolantTempCelsius + " °C");

                // Close the socket when done
                socket.close();
            } catch (IOException e) {
                Log.d("Bluetooth connection error", "Failed to connect and establish a connection with the OBD2 Scanner");
                e.printStackTrace();
            }
        });
    }

    private void requestPermissionsBlueToothScan(String[] strings, int i) {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // Access granted
            Log.d("Permissions", "Permission Already granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
    }

    private void requestPermissions() {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            // Access granted
            Log.d("Permissions", "Permission Already granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Access granted
                Log.d("Permissions", "Permission Granted to BluetoothConnect");
            } else {
                Log.d("Permissions", "Denied Permission to BluetoothConnect");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    private void refreshBluetoothDevices() {
        devicesList.clear();
        devicesArrayAdapter.notifyDataSetChanged();
        startBluetoothDiscovery();
    }

    private void startBluetoothDiscovery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Error Permissions", "229 Permissions error");
            return;
        }
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
    }

}
