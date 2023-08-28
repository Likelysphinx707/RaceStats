package com.example.racestats;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

// Import our Gauge view class
import com.example.racestats.DraggableGaugeView;

// Imports for OBD2 classes
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class DigitalDash extends AppCompatActivity {
    private static BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // Declare member variables here
    private DraggableGaugeView rpmGauge;
    private DraggableGaugeView coolantTempGauge;
    private Button refreshbutton;

    private ImageButton hamburgerButton;
    private LinearLayout popoutMenu;
    private ImageButton xButton;
    private boolean isMenuOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digital_dash);

        // Initialize gauges and assets for UI
        rpmGauge = findViewById(R.id.rpmGauge);
        coolantTempGauge = findViewById(R.id.coolantTempGauge);
        refreshbutton = findViewById(R.id.refreshbutton);

        // Get the Bluetooth device address from the intent
        String deviceAddress = getIntent().getStringExtra("deviceAddress");

        hamburgerButton = findViewById(R.id.hamburgerButton);
        popoutMenu = findViewById(R.id.popoutMenu);
        xButton = findViewById(R.id.x_button);

        hamburgerButton.setOnClickListener(view -> {
            if (!isMenuOpen) {
                openMenu();
                hamburgerButton.setVisibility(View.GONE);
                xButton.setVisibility(View.VISIBLE);
            }
        });

        xButton.setOnClickListener(view -> {
            if (isMenuOpen) {
                closeMenu();
                xButton.setVisibility(View.GONE);
                hamburgerButton.setVisibility(View.VISIBLE);
            }
        });


        if (deviceAddress == null) {
            // Show a popup indicating that no Bluetooth device is connected
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("No Bluetooth Device Detected");
//            builder.setMessage("There is no Bluetooth device connected. Please connect a device and try again.");
//            builder.setPositiveButton("OK", (dialog, which) -> finish());
//            builder.show();

            // add dev test buttons here generate random values
            rpmGauge.setText("Engine RPM: " + generateRandomNumber(4));
            coolantTempGauge.setText("Coolant Temperature C°: " + 2);
        } else {
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

            // add refresh button here for testing
            refreshbutton.setOnClickListener(view -> {
                // Call the obd2CommandsToCall() function here
                obd2CommandsToCall();
                Log.d("Updated command", "OBD2 commands called refreshed");
            });
        }
    }

    public void onGaugeOptionClick(View view) {
        switch (view.getId()) {
            case R.id.gaugeOptionRPM:
                toggleGaugeVisibility(rpmGauge);
                break;
            case R.id.gaugeOptionCoolant:
                toggleGaugeVisibility(coolantTempGauge);
                break;
            // Add cases for more gauge options
        }
    }

    private void toggleGaugeVisibility(DraggableGaugeView gaugeView) {
        int newVisibility = gaugeView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        gaugeView.setVisibility(newVisibility);
    }


    private void openMenu() {
        isMenuOpen = true;
        popoutMenu.setVisibility(View.VISIBLE);

        ObjectAnimator rotation = ObjectAnimator.ofFloat(hamburgerButton, "rotation", 0f, 45f);
        rotation.setDuration(300);
        rotation.start();
    }

    private void closeMenu() {
        isMenuOpen = false;
        popoutMenu.setVisibility(View.INVISIBLE);

        ObjectAnimator rotation = ObjectAnimator.ofFloat(hamburgerButton, "rotation", 45f, 0f);
        rotation.setDuration(300);
        rotation.start();
    }

    /**
     * Random number generator for testing purposes
     *
     * @param numberOfDigits
     * @return
     */
    public static int generateRandomNumber(int numberOfDigits) {
        if (numberOfDigits <= 0) {
            throw new IllegalArgumentException("Number of digits must be a positive integer.");
        }

        Random random = new Random();
        int minBound = (int) Math.pow(10, numberOfDigits - 1);
        int maxBound = (int) Math.pow(10, numberOfDigits) - 1;

        return random.nextInt(maxBound - minBound + 1) + minBound;
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
            commandsToRun.add(new EngineCoolantTemperatureCommand());
            commandsToRun.add(new RPMCommand());

            String coolantTempResult = null;
            String rpmResult = null;

            // Execute commands in a single request
            //  adding a counter so we know how what request we are on
            int counter = 0;

            for (ObdCommand command : commandsToRun) {
                command.run(socket.getInputStream(), socket.getOutputStream());
                String result = command.getFormattedResult();
                if (counter == 0) {
                    // set coolant temp test
                    coolantTempResult = result;
                } else if (counter == 1) {
                    rpmResult = result;
                }
                counter++;

            }

            // This will actually set our values in the UI need to test first.
            // Get results
            Log.d("Coolant Temp", coolantTempResult);
            Log.d("RPM", rpmResult);

            // Update the values in the DraggableGaugeView instances
            rpmGauge.setText("Engine RPM: " + rpmResult);
            coolantTempGauge.setText("Coolant Temperature C°: " + coolantTempResult);


            // ... Add more OBD2 commands here ...

        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

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
