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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

// Imports for OBD2 classes
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.pressure.FuelPressureCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.FuelType;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.commands.control.VinCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.exceptions.NoDataException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        }

        Log.d("Test", "Added Device to list 132");
        bluetoothAdapter.startDiscovery();
        Log.d("Line 142", "test");
        devicesListView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("Line 145", "Click detected");
            String deviceInfo = devicesArrayAdapter.getItem(position);
            String[] deviceInfoArray = deviceInfo.split("\n");
            String deviceAddress = deviceInfoArray[1];

            BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
            Log.d("selectedDevice BluetoothDevice", String.valueOf(selectedDevice));

            // Connect to the selected device
            BluetoothSocket socket = null;
            long startTime = System.currentTimeMillis();
            try {
                Log.d("Trying socket connection", "searching");
                socket = selectedDevice.createInsecureRfcommSocketToServiceRecord(selectedDevice.getUuids()[0].getUuid());
                socket.connect();

                // Request ECU Data
                try {
                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

                    String coolantTemp = null;
                    try {
                        // EngineCoolantTemperatureCommand
                        EngineCoolantTemperatureCommand coolantTempCmd = new EngineCoolantTemperatureCommand();
                        coolantTempCmd.run(socket.getInputStream(), socket.getOutputStream());
                        coolantTemp = coolantTempCmd.getFormattedResult();
                        System.out.println("Coolant Temperature: " + coolantTemp);
                    } catch (NoDataException e) {
                        System.err.println("Error: No data available for EngineCoolantTemperatureCommand");
                    } catch (Exception e) {
                        System.err.println("Error while fetching EngineCoolantTemperatureCommand: " + e.getMessage());
                    }

                    String vin = null;
                    try {
                        // VinCommand
                        VinCommand getVinCmd = new VinCommand();
                        getVinCmd.run(socket.getInputStream(), socket.getOutputStream());
                        vin = getVinCmd.getFormattedResult();
                        System.out.println("Vin: " + vin);
                    } catch (NoDataException e) {
                        System.err.println("Error: No data available for VinCommand");
                    } catch (Exception e) {
                        System.err.println("Error while fetching VinCommand: " + e.getMessage());
                    }

                    try {
                        // ThrottlePositionCommand
                        ThrottlePositionCommand throttlePositionCmd = new ThrottlePositionCommand();
                        throttlePositionCmd.run(socket.getInputStream(), socket.getOutputStream());
                        String throttlePosition = throttlePositionCmd.getFormattedResult();
                        System.out.println("Throttle Position: " + throttlePosition);
                    } catch (NoDataException e) {
                        System.err.println("Error: No data available for ThrottlePositionCommand");
                    } catch (Exception e) {
                        System.err.println("Error while fetching ThrottlePositionCommand: " + e.getMessage());
                    }

                    try {
                        // LoadCommand
                        LoadCommand loadCmd = new LoadCommand();
                        loadCmd.run(socket.getInputStream(), socket.getOutputStream());
                        String load = loadCmd.getFormattedResult();
                        System.out.println("Load: " + load);
                    } catch (NoDataException e) {
                        System.err.println("Error: No data available for LoadCommand");
                    } catch (Exception e) {
                        System.err.println("Error while fetching LoadCommand: " + e.getMessage());
                    }

                    try {
                        // RPMCommand
                        RPMCommand rpmCmd = new RPMCommand();
                        rpmCmd.run(socket.getInputStream(), socket.getOutputStream());
                        String rpm = rpmCmd.getFormattedResult();
                        System.out.println("RPM: " + rpm);
                    } catch (NoDataException e) {
                        System.err.println("Error: No data available for RPMCommand");
                    } catch (Exception e) {
                        System.err.println("Error while fetching RPMCommand: " + e.getMessage());
                    }

                    try {
                        // FuelPRessureCommand
                        FuelPressureCommand rpmCmd = new FuelPressureCommand();
                        rpmCmd.run(socket.getInputStream(), socket.getOutputStream());
                        String rpm = rpmCmd.getFormattedResult();
                        System.out.println("RPM: " + rpm);
                    } catch (NoDataException e) {
                        System.err.println("Error: No data available for RPMCommand");
                    } catch (Exception e) {
                        System.err.println("Error while fetching RPMCommand: " + e.getMessage());
                    }


                    // Construct and send a custom OBD command
                    String customPid = "01 0C"; // Replace with your custom PID
                    String command = customPid + "\r\n"; // ELM327 command format
                    byte[] commandBytes = command.getBytes();

                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(commandBytes);
                    outputStream.flush();

                    // Read and parse the response
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead = inputStream.read(buffer);
                    String response = new String(buffer, 0, bytesRead);

                    // Parse the response to extract the oil pressure value
                    String oilPressure = parseOilPressureResponse(response);
                    System.out.println("Oil Pressure: " + oilPressure);


                    // Don't work on the Z
//                    AbsoluteLoadCommand
//                    OilTempCommand
//                    RuntimeCommand
//                    ModuleVoltageCommand
//                    FindFuelTypeCommand

                    TextView vinTextView = findViewById(R.id.vin);
                    vinTextView.setText("VIN: " + vin);
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e("Error", e.toString());
                }
            } catch (IOException e) {
//                Log.d("Bluetooth connection error", "Failed to connect and establish a connection with the OBD2 Scanner");
                e.printStackTrace();
            }
            // Done calculating time it took to run OBD2 calls
            long endTime = System.currentTimeMillis();
            double elapsedTimeSeconds = (endTime - startTime) / 1000.0;
            System.out.println("Total execution time to call OBD2 Data: " + elapsedTimeSeconds + " seconds");

            // Find disconnect UI button
            Button disconnectButton = findViewById(R.id.disconnectButton);
            BluetoothSocket finalSocket = socket;

            /**
             * On click listener that will deal with disconnecting the device from the OBD2 Scanner
             */
            disconnectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalSocket != null) {
                        try {
                            finalSocket.close();
                            Log.d("Disconnect", "Socket closed");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("Disconnect Error", e.toString());
                        }
                    }
                }
            });
        });

    }

    private String parseOilPressureResponse(String response) {
        // Assuming the response contains the oil pressure value in the form "PID:VALUE"
        String[] parts = response.split(":");
        if (parts.length >= 2) {
            return parts[1].trim(); // Extract the value part
        }
        return "N/A"; // Return "Not Available" if parsing fails
    }

    /**
     * @param strings
     * @param i
     */
    private void requestPermissionsBlueToothScan(String[] strings, int i) {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // Access granted
            Log.d("Permissions", "Permission Already granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
    }

    /**
     *
     */
    private void requestPermissions() {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            // Access granted
            Log.d("Permissions", "Permission Already granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
    }

    /**
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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


    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    /**
     *
     */
    private void refreshBluetoothDevices() {
        devicesList.clear();
        devicesArrayAdapter.notifyDataSetChanged();
        startBluetoothDiscovery();
    }

    /**
     *
     */
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
