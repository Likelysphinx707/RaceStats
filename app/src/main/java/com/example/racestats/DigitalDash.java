package com.example.racestats;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class DigitalDash extends AppCompatActivity {
    private static BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digital_dash);

        // Get the Bluetooth device address from the intent
        String deviceAddress = getIntent().getStringExtra("deviceAddress");
        BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        // Check Bluetooth connect permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions();
            }
        }

        // Create Bluetooth socket
        socket = createBluetoothSocket(selectedDevice);

        try {
            // Connect socket and execute OBD2 commands in a separate thread
            socket.connect();

            // Run OBD2 commands in new Thread
            Executors.newSingleThreadExecutor().execute(this::obd2CommandsToCall);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Create a Bluetooth socket for the selected device
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    requestPermissions();
                }
            }
            return device.createInsecureRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This function will check and see what PIDs are available for the selected ECU
     */
    private int[] avaliablePIDs() {
        return null;
    }


    /**
     * Class that will make calls to the obd2 scanner based off of the selected gauges of the user
     */
    private void obd2CommandsToCall() {
        long startTime = System.currentTimeMillis();

        try {
            // Initialize OBD2 communication
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

            // Batch multiple commands in a single request
            List<ObdCommand> commandsToRun = new ArrayList<>();
            commandsToRun.add(new EngineLoadCommand());
            commandsToRun.add(new EngineRPMCommand());
            // Add more commands to the list

            // Execute commands in a single request
            for (ObdCommand command : commandsToRun) {
                command.run(socket.getInputStream(), socket.getOutputStream());
                String result = command.getFormattedResult();
                Log.d("Command Result", result);
            }

            // ... Add more OBD2 commands here ...

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        double elapsedTimeSeconds = (endTime - startTime) / 1000.0;
        Log.d("Execution Time", "Total execution time: " + elapsedTimeSeconds + " seconds");
    }


    /**
     * Cleanup when the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Request Bluetooth permissions for scanning
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestPermissionsBlueToothScan(String[] strings, int i) {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // Access granted
            Log.d("Permissions", "Permission Already granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        }
    }

    /**
     * Request Bluetooth permissions for connection
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestPermissions() {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            // Access granted
            Log.d("Permissions", "Permission Already granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
    }
}
