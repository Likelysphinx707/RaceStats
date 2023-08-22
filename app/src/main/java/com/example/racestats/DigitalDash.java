package com.example.racestats;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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

public class DigitalDash extends AppCompatActivity {
    private static BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digital_dash);

        // Retrieve the passed device address from the intent
        String deviceAddress = getIntent().getStringExtra("deviceAddress");
        BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        try {
            // Create a BluetoothSocket using the selected device
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            }
            socket = selectedDevice.createInsecureRfcommSocketToServiceRecord(selectedDevice.getUuids()[0].getUuid());
            socket.connect();

            // need to make array of some sort that will collect all gauges user wants to use. Maybe make ids for each gauge to pass to the command call

            // will make calls to the OBD2 scanner to get values needed
            obd2CommandsToCall();

        } catch (IOException ex) {
            ex.printStackTrace();
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
    private static void obd2CommandsToCall() {
        long startTime = System.currentTimeMillis();
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
                // FuelPressureCommand
                FuelPressureCommand getFuelPressure = new FuelPressureCommand();
                getFuelPressure.run(socket.getInputStream(), socket.getOutputStream());
                String rpm = getFuelPressure.getFormattedResult();
                System.out.println("Fuel Pressure: " + getFuelPressure);
            } catch (NoDataException e) {
                System.err.println("Error: No data available for Fuel Pressure");
            } catch (Exception e) {
                System.err.println("Error while fetching Fuel Pressure: " + e.getMessage());
            }


            // Construct and send a custom OBD command
//                    String customPid = "01 0C"; // Replace with your custom PID
//                    String command = customPid + "\r\n"; // ELM327 command format
//                    byte[] commandBytes = command.getBytes();
//
//                    OutputStream outputStream = socket.getOutputStream();
//                    outputStream.write(commandBytes);
//                    outputStream.flush();
//
//                    // Read and parse the response
//                    InputStream inputStream = socket.getInputStream();
//                    byte[] buffer = new byte[1024];
//                    int bytesRead = inputStream.read(buffer);
//                    String response = new String(buffer, 0, bytesRead);
//
//                    // Parse the response to extract the oil pressure value
//                    String oilPressure = parseOilPressureResponse(response);
//                    System.out.println("Oil Pressure: " + oilPressure);


            // Don't work on the Z
//                    AbsoluteLoadCommand
//                    OilTempCommand
//                    RuntimeCommand
//                    ModuleVoltageCommand
//                    FindFuelTypeCommand
        } catch (Exception e) {
            e.printStackTrace();
//                    Log.e("Error", e.toString());

        } catch (IOException e) {
//        Log.d("Bluetooth connection error", "Failed to connect and establish a connection with the OBD2 Scanner");
            e.printStackTrace();
        }

        // Done calculating time it took to run OBD2 calls
        long endTime = System.currentTimeMillis();
        double elapsedTimeSeconds = (endTime - startTime) / 1000.0;
        System.out.println("Total execution time to call OBD2 Data: " + elapsedTimeSeconds + " seconds");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the Bluetooth socket when the activity is destroyed
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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


}
