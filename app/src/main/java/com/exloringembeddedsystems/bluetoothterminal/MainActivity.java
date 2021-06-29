 package com.exloringembeddedsystems.bluetoothterminal;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG_MA";

    Button buttonSendMessage;
    Button buttonBTConnect;
    Button buttonShare;
    Button buttonMemory1;
    Button buttonMemory2;
    Button buttonMemory3;
    Button buttonMemory4;
    Button buttonMemory5;
    Button buttonMemory6;
    Button buttonMemory7;
    Button buttonMemory8;
    Button buttonMemory9;

    Button buttonPopUpSave;
    Button buttonPopUpCancel;


    TextView tvReceivedMessage;

    EditText editTextSentMessage;
    EditText editTextPopUpLabel;
    EditText editTextPopUpData;

    Spinner spinnerBTPairedDevices;

    LinearLayout linearLayoutPopupSaveData;
    PopupWindow popupWindowSaveData;


    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket BTSocket = null;
    BluetoothAdapter BTAdaptor = null;
    Set<BluetoothDevice> BTPairedDevices = null;
    boolean bBTConnected = false;
    BluetoothDevice BTDevice = null;
    classBTInitDataCommunication cBTInitSendReceive =null;

    static public final int BT_CON_STATUS_NOT_CONNECTED     =0;
    static public final int BT_CON_STATUS_CONNECTING        =1;
    static public final int BT_CON_STATUS_CONNECTED         =2;
    static public final int BT_CON_STATUS_FAILED            =3;
    static public final int BT_CON_STATUS_CONNECTiON_LOST   =4;
    static public int iBTConnectionStatus = BT_CON_STATUS_NOT_CONNECTED;

    static final int BT_STATE_LISTENING            =1;
    static final int BT_STATE_CONNECTING           =2;
    static final int BT_STATE_CONNECTED            =3;
    static final int BT_STATE_CONNECTION_FAILED    =4;
    static final int BT_STATE_MESSAGE_RECEIVED     =5;

    String sM1Index="",sM1Data="";
    String sM2Index="",sM2Data="";
    String sM3Index="",sM3Data="";
    String sM4Index="",sM4Data="";
    String sM5Index="",sM5Data="";
    String sM6Index="",sM6Data="";
    String sM7Index="",sM7Data="";
    String sM8Index="",sM8Data="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate-Start");

        tvReceivedMessage = findViewById(R.id.idMATextViewReceivedMessage);
        tvReceivedMessage.setMovementMethod(new ScrollingMovementMethod());


        editTextSentMessage = findViewById(R.id.idMAEditTextSendMessage);

        spinnerBTPairedDevices = findViewById(R.id.idMASpinnerBTPairedDevices);

        buttonSendMessage = findViewById(R.id.idMAButtonSendData);
        buttonBTConnect = findViewById(R.id.idMAButtonConnect);
        buttonShare = findViewById(R.id.idMAButtonShare);
        buttonMemory1 = findViewById(R.id.idMAButtonStoreData1);
        buttonMemory2 = findViewById(R.id.idMAButtonStoreData2);
        buttonMemory3 = findViewById(R.id.idMAButtonStoreData3);
        buttonMemory4 = findViewById(R.id.idMAButtonStoreData4);
        buttonMemory5 = findViewById(R.id.idMAButtonStoreData5);
        buttonMemory6 = findViewById(R.id.idMAButtonStoreData6);
        buttonMemory7 = findViewById(R.id.idMAButtonStoreData7);
        buttonMemory8 = findViewById(R.id.idMAButtonStoreData8);
        buttonMemory9 = findViewById(R.id.idMAButtonStoreData9);

        tvReceivedMessage.setText("App Loaded");

        buttonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Send Button clicked");
                String sMessage = editTextSentMessage.getText().toString();
                tvReceivedMessage.append("\n->"+sMessage);

               sendMessage(sMessage);

            }
        });


        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonShare");


                Log.d(TAG, "sharing  : " + tvReceivedMessage.getText().toString());
                Intent intentShare = new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_SUBJECT,"Share BTTerminal message");
                intentShare.putExtra(Intent.EXTRA_TEXT,tvReceivedMessage.getText().toString());
                startActivity(Intent.createChooser(intentShare, "Sharing BT Terminal"));


            }
        });
        buttonMemory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonMemory1");
                sendMessage(sM1Data);
            }
        });
        buttonMemory1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "Button Long Press buttonMemory1");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.layoutstoredata,null);
                buttonPopUpCancel = customView.findViewById(R.id.idSDButtonCancel);
                buttonPopUpSave = customView.findViewById(R.id.idSDButtonSave);
                editTextPopUpLabel = customView.findViewById(R.id.idSDEditTextLabel);
                editTextPopUpData = customView.findViewById(R.id.idSDEditTextData);
                editTextPopUpData.setText(sM1Data);
                editTextPopUpLabel.setText(sM1Index);
                linearLayoutPopupSaveData = customView.findViewById(R.id.idLLPopupStoreData);
                popupWindowSaveData = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindowSaveData.setFocusable(true);
                popupWindowSaveData.update();
                //display the popup window
                popupWindowSaveData.showAtLocation(linearLayoutPopupSaveData, Gravity.CENTER, 0, 0);
                buttonPopUpCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowSaveData.dismiss();
                    }
                });
                buttonPopUpSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(editTextPopUpData.getText().length()<1 || editTextPopUpLabel.getText().length()<1)
                        {
                            Toast.makeText(getApplicationContext(), "Please enter both label and Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        storeData("1",editTextPopUpLabel.getText().toString(),editTextPopUpData.getText().toString());
                        popupWindowSaveData.dismiss();
                        readAllData();
                    }
                });
                return false;
            }
        });

        buttonMemory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonMemory2");
                sendMessage(sM2Data);
            }
        });
        buttonMemory2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "Button Long Press buttonMemory2");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.layoutstoredata,null);
                buttonPopUpCancel = customView.findViewById(R.id.idSDButtonCancel);
                buttonPopUpSave = customView.findViewById(R.id.idSDButtonSave);
                editTextPopUpLabel = customView.findViewById(R.id.idSDEditTextLabel);
                editTextPopUpData = customView.findViewById(R.id.idSDEditTextData);
                editTextPopUpData.setText(sM2Data);
                editTextPopUpLabel.setText(sM2Index);
                linearLayoutPopupSaveData = customView.findViewById(R.id.idLLPopupStoreData);
                popupWindowSaveData = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindowSaveData.setFocusable(true);
                popupWindowSaveData.update();
                //display the popup window
                popupWindowSaveData.showAtLocation(linearLayoutPopupSaveData, Gravity.CENTER, 0, 0);
                buttonPopUpCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowSaveData.dismiss();
                    }
                });
                buttonPopUpSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(editTextPopUpData.getText().length()<1 || editTextPopUpLabel.getText().length()<1)
                        {
                            Toast.makeText(getApplicationContext(), "Please enter both label and Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        storeData("2",editTextPopUpLabel.getText().toString(),editTextPopUpData.getText().toString());
                        popupWindowSaveData.dismiss();
                        readAllData();
                    }
                });

                return false;
            }
        });




        buttonMemory3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonMemory3");
                sendMessage(sM3Data);
            }
        });
        buttonMemory3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "Button Long Press buttonMemory2");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.layoutstoredata,null);
                buttonPopUpCancel = customView.findViewById(R.id.idSDButtonCancel);
                buttonPopUpSave = customView.findViewById(R.id.idSDButtonSave);
                editTextPopUpLabel = customView.findViewById(R.id.idSDEditTextLabel);
                editTextPopUpData = customView.findViewById(R.id.idSDEditTextData);
                editTextPopUpData.setText(sM3Data);
                editTextPopUpLabel.setText(sM3Index);
                linearLayoutPopupSaveData = customView.findViewById(R.id.idLLPopupStoreData);
                popupWindowSaveData = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindowSaveData.setFocusable(true);
                popupWindowSaveData.update();
                //display the popup window
                popupWindowSaveData.showAtLocation(linearLayoutPopupSaveData, Gravity.CENTER, 0, 0);
                buttonPopUpCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowSaveData.dismiss();
                    }
                });
                buttonPopUpSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(editTextPopUpData.getText().length()<1 || editTextPopUpLabel.getText().length()<1)
                        {
                            Toast.makeText(getApplicationContext(), "Please enter both label and Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        storeData("3",editTextPopUpLabel.getText().toString(),editTextPopUpData.getText().toString());
                        popupWindowSaveData.dismiss();
                        readAllData();
                    }
                });

                return false;
            }
        });




        buttonMemory4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonMemory4");
                sendMessage(sM4Data);
            }
        });
        buttonMemory4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "Button Long Press buttonMemory4");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.layoutstoredata,null);
                buttonPopUpCancel = customView.findViewById(R.id.idSDButtonCancel);
                buttonPopUpSave = customView.findViewById(R.id.idSDButtonSave);
                editTextPopUpLabel = customView.findViewById(R.id.idSDEditTextLabel);
                editTextPopUpData = customView.findViewById(R.id.idSDEditTextData);
                editTextPopUpData.setText(sM4Data);
                editTextPopUpLabel.setText(sM4Index);
                linearLayoutPopupSaveData = customView.findViewById(R.id.idLLPopupStoreData);
                popupWindowSaveData = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindowSaveData.setFocusable(true);
                popupWindowSaveData.update();
                //display the popup window
                popupWindowSaveData.showAtLocation(linearLayoutPopupSaveData, Gravity.CENTER, 0, 0);
                buttonPopUpCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowSaveData.dismiss();
                    }
                });
                buttonPopUpSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(editTextPopUpData.getText().length()<1 || editTextPopUpLabel.getText().length()<1)
                        {
                            Toast.makeText(getApplicationContext(), "Please enter both label and Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        storeData("4",editTextPopUpLabel.getText().toString(),editTextPopUpData.getText().toString());
                        popupWindowSaveData.dismiss();
                        readAllData();
                    }
                });

                return false;
            }
        });


        buttonMemory5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonMemory5");
                sendMessage(sM5Data);
            }
        });
        buttonMemory5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "Button Long Press buttonMemory5");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.layoutstoredata,null);
                buttonPopUpCancel = customView.findViewById(R.id.idSDButtonCancel);
                buttonPopUpSave = customView.findViewById(R.id.idSDButtonSave);
                editTextPopUpLabel = customView.findViewById(R.id.idSDEditTextLabel);
                editTextPopUpData = customView.findViewById(R.id.idSDEditTextData);
                editTextPopUpData.setText(sM5Data);
                editTextPopUpLabel.setText(sM5Index);
                linearLayoutPopupSaveData = customView.findViewById(R.id.idLLPopupStoreData);
                popupWindowSaveData = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindowSaveData.setFocusable(true);
                popupWindowSaveData.update();
                //display the popup window
                popupWindowSaveData.showAtLocation(linearLayoutPopupSaveData, Gravity.CENTER, 0, 0);
                buttonPopUpCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowSaveData.dismiss();
                    }
                });
                buttonPopUpSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(editTextPopUpData.getText().length()<1 || editTextPopUpLabel.getText().length()<1)
                        {
                            Toast.makeText(getApplicationContext(), "Please enter both label and Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        storeData("5",editTextPopUpLabel.getText().toString(),editTextPopUpData.getText().toString());
                        popupWindowSaveData.dismiss();
                        readAllData();
                    }
                });

                return false;
            }
        });


        buttonMemory6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonMemory6");
                sendMessage(sM6Data);
            }
        });
        buttonMemory6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "Button Long Press buttonMemory6");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.layoutstoredata,null);
                buttonPopUpCancel = customView.findViewById(R.id.idSDButtonCancel);
                buttonPopUpSave = customView.findViewById(R.id.idSDButtonSave);
                editTextPopUpLabel = customView.findViewById(R.id.idSDEditTextLabel);
                editTextPopUpData = customView.findViewById(R.id.idSDEditTextData);
                editTextPopUpData.setText(sM6Data);
                editTextPopUpLabel.setText(sM6Index);
                linearLayoutPopupSaveData = customView.findViewById(R.id.idLLPopupStoreData);
                popupWindowSaveData = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindowSaveData.setFocusable(true);
                popupWindowSaveData.update();
                //display the popup window
                popupWindowSaveData.showAtLocation(linearLayoutPopupSaveData, Gravity.CENTER, 0, 0);
                buttonPopUpCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowSaveData.dismiss();
                    }
                });
                buttonPopUpSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(editTextPopUpData.getText().length()<1 || editTextPopUpLabel.getText().length()<1)
                        {
                            Toast.makeText(getApplicationContext(), "Please enter both label and Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        storeData("6",editTextPopUpLabel.getText().toString(),editTextPopUpData.getText().toString());
                        popupWindowSaveData.dismiss();
                        readAllData();
                    }
                });

                return false;
            }
        });



        buttonMemory7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonMemory7");
                sendMessage(sM7Data);
            }
        });
        buttonMemory7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "Button Long Press buttonMemory7");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.layoutstoredata,null);
                buttonPopUpCancel = customView.findViewById(R.id.idSDButtonCancel);
                buttonPopUpSave = customView.findViewById(R.id.idSDButtonSave);
                editTextPopUpLabel = customView.findViewById(R.id.idSDEditTextLabel);
                editTextPopUpData = customView.findViewById(R.id.idSDEditTextData);
                editTextPopUpData.setText(sM7Data);
                editTextPopUpLabel.setText(sM7Index);
                linearLayoutPopupSaveData = customView.findViewById(R.id.idLLPopupStoreData);
                popupWindowSaveData = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindowSaveData.setFocusable(true);
                popupWindowSaveData.update();
                //display the popup window
                popupWindowSaveData.showAtLocation(linearLayoutPopupSaveData, Gravity.CENTER, 0, 0);
                buttonPopUpCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowSaveData.dismiss();
                    }
                });
                buttonPopUpSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(editTextPopUpData.getText().length()<1 || editTextPopUpLabel.getText().length()<1)
                        {
                            Toast.makeText(getApplicationContext(), "Please enter both label and Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        storeData("7",editTextPopUpLabel.getText().toString(),editTextPopUpData.getText().toString());
                        popupWindowSaveData.dismiss();
                        readAllData();
                    }
                });

                return false;
            }
        });


        buttonMemory8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonMemory8");
                sendMessage(sM8Data);
            }
        });
        buttonMemory8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Log.d(TAG, "Button Long Press buttonMemory8");
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.layoutstoredata,null);
                buttonPopUpCancel = customView.findViewById(R.id.idSDButtonCancel);
                buttonPopUpSave = customView.findViewById(R.id.idSDButtonSave);
                editTextPopUpLabel = customView.findViewById(R.id.idSDEditTextLabel);
                editTextPopUpData = customView.findViewById(R.id.idSDEditTextData);
                editTextPopUpData.setText(sM8Data);
                editTextPopUpLabel.setText(sM8Index);
                linearLayoutPopupSaveData = customView.findViewById(R.id.idLLPopupStoreData);
                popupWindowSaveData = new PopupWindow(customView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                popupWindowSaveData.setFocusable(true);
                popupWindowSaveData.update();
                //display the popup window
                popupWindowSaveData.showAtLocation(linearLayoutPopupSaveData, Gravity.CENTER, 0, 0);
                buttonPopUpCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindowSaveData.dismiss();
                    }
                });
                buttonPopUpSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(editTextPopUpData.getText().length()<1 || editTextPopUpLabel.getText().length()<1)
                        {
                            Toast.makeText(getApplicationContext(), "Please enter both label and Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        storeData("8",editTextPopUpLabel.getText().toString(),editTextPopUpData.getText().toString());
                        popupWindowSaveData.dismiss();
                        readAllData();
                    }
                });

                return false;
            }
        });



        buttonMemory9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click Clear screen");
                tvReceivedMessage.setText("");
            }
        });


        buttonBTConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button Click buttonBTConnect");

                if(bBTConnected==false) {
                    if (spinnerBTPairedDevices.getSelectedItemPosition() == 0) {
                        Log.d(TAG, "Please select BT device");
                        Toast.makeText(getApplicationContext(), "Please select Bluetooth Device", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String sSelectedDevice = spinnerBTPairedDevices.getSelectedItem().toString();
                    Log.d(TAG, "Selected device = " + sSelectedDevice);

                    for (BluetoothDevice BTDev : BTPairedDevices) {
                        if (sSelectedDevice.equals(BTDev.getName())) {
                            BTDevice = BTDev;
                            Log.d(TAG, "Selected device UUID = " + BTDevice.getAddress());

                            cBluetoothConnect cBTConnect = new cBluetoothConnect(BTDevice);
                            cBTConnect.start();

//
//                            try {
//                                Log.d(TAG, "Creating socket, my uuid " + MY_UUID);
//                                BTSocket = BTDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                                Log.d(TAG, "Connecting to device");
//                                BTSocket.connect();
//                                Log.d(TAG, "Connected");
//                                buttonBTConnect.setText("Disconnect");
//                                bBTConnected = true;
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                Log.e(TAG, "Exception = " + e.getMessage());
//                                bBTConnected = false;
//                            }


                        }
                    }
                }
                else {
                    Log.d(TAG, "Disconnecting BTConnection");
                    if(BTSocket!=null && BTSocket.isConnected())
                    {
                        try {
                            BTSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "BTDisconnect Exp " + e.getMessage());
                        }
                    }
                    buttonBTConnect.setText("Connect");
                    bBTConnected = false;

                }



            }
        });
    }


    void storeData(String sButtonNumber,String sIndex,String sValue)
    {
        Log.d(TAG, "storeData : " + sButtonNumber + ", Index : " + sIndex+ ", Value : " + sValue);
        try {
            SharedPreferences spSavedBluetoothDevice = getSharedPreferences("TERMINAL_STORED_DATA", this.MODE_PRIVATE);
            SharedPreferences.Editor editor = spSavedBluetoothDevice.edit();
            editor.putString("M"+sButtonNumber+"_INDEX",sIndex);
            editor.putString("M"+sButtonNumber+"_DATA",sValue);
            editor.commit();
        }
        catch (Exception exp)
        {
        }
    }

    void readAllData()
    {
        Log.d(TAG, "readAllData " );
        try {
            SharedPreferences spSavedBluetoothDevice = getSharedPreferences("TERMINAL_STORED_DATA", this.MODE_PRIVATE);
            sM1Index = spSavedBluetoothDevice.getString("M1_INDEX", null);
            if(sM1Index==null)
            {
                Log.d(TAG, "storing Default Data " );
                storeData("1","S1","M1 Data");
                storeData("2","S2","M2 Data");
                storeData("3","S3","M3 Data");
                storeData("4","S4","M4 Data");
                storeData("5","S5","M5 Data");
                storeData("6","S6","M6 Data");
                storeData("7","S7","M7 Data");
                storeData("8","S8","M8 Data");
            }
            sM1Index = spSavedBluetoothDevice.getString("M1_INDEX", null);sM1Data = spSavedBluetoothDevice.getString("M1_DATA", null);
            sM2Index = spSavedBluetoothDevice.getString("M2_INDEX", null);sM2Data = spSavedBluetoothDevice.getString("M2_DATA", null);
            sM3Index = spSavedBluetoothDevice.getString("M3_INDEX", null);sM3Data = spSavedBluetoothDevice.getString("M3_DATA", null);
            sM4Index = spSavedBluetoothDevice.getString("M4_INDEX", null);sM4Data = spSavedBluetoothDevice.getString("M4_DATA", null);
            sM5Index = spSavedBluetoothDevice.getString("M5_INDEX", null);sM5Data = spSavedBluetoothDevice.getString("M5_DATA", null);
            sM6Index = spSavedBluetoothDevice.getString("M6_INDEX", null);sM6Data = spSavedBluetoothDevice.getString("M6_DATA", null);
            sM7Index = spSavedBluetoothDevice.getString("M7_INDEX", null);sM7Data = spSavedBluetoothDevice.getString("M7_DATA", null);
            sM8Index = spSavedBluetoothDevice.getString("M8_INDEX", null);sM8Data = spSavedBluetoothDevice.getString("M8_DATA", null);

            buttonMemory1.setText(sM1Index);
            buttonMemory2.setText(sM2Index);
            buttonMemory3.setText(sM3Index);
            buttonMemory4.setText(sM4Index);
            buttonMemory5.setText(sM5Index);
            buttonMemory6.setText(sM6Index);
            buttonMemory7.setText(sM7Index);
            buttonMemory8.setText(sM8Index);

        }
        catch (Exception exp)
        {

        }
    }



    public class cBluetoothConnect extends Thread
    {
        private BluetoothDevice device;


        public cBluetoothConnect (BluetoothDevice BTDevice)
        {
            Log.i(TAG, "classBTConnect-start");

            device = BTDevice;
            try{
                BTSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (Exception exp)
            {
                Log.e(TAG, "classBTConnect-exp" + exp.getMessage());
            }
        }

        public void run()
        {
            try {
                BTSocket.connect();
                Message message=Message.obtain();
                message.what=BT_STATE_CONNECTED;
                handler.sendMessage(message);


            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=BT_STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }


    public class classBTInitDataCommunication extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private  InputStream inputStream =null;
        private  OutputStream outputStream=null;

        public classBTInitDataCommunication (BluetoothSocket socket)
        {
            Log.i(TAG, "classBTInitDataCommunication-start");

            bluetoothSocket=socket;


            try {
                inputStream=bluetoothSocket.getInputStream();
                outputStream=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "classBTInitDataCommunication-start, exp " + e.getMessage());
            }


        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (BTSocket.isConnected())
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(BT_STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "BT disconnect from decide end, exp " + e.getMessage());
                    iBTConnectionStatus=BT_CON_STATUS_CONNECTiON_LOST;
                    try {
                        //disconnect bluetooth
                        Log.d(TAG, "Disconnecting BTConnection");
                        if(BTSocket!=null && BTSocket.isConnected())
                        {

                                BTSocket.close();
                        }
                        buttonBTConnect.setText("Connect");
                        bBTConnected = false;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    Handler handler =new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case BT_STATE_LISTENING:
                    Log.d(TAG, "BT_STATE_LISTENING");
                    break;
                case BT_STATE_CONNECTING:
                    iBTConnectionStatus = BT_CON_STATUS_CONNECTING;
                    buttonBTConnect.setText("Connecting..");
                    Log.d(TAG, "BT_STATE_CONNECTING");
                    break;
                case BT_STATE_CONNECTED:

                    iBTConnectionStatus = BT_CON_STATUS_CONNECTED;

                    Log.d(TAG, "BT_CON_STATUS_CONNECTED");
                    buttonBTConnect.setText("Disconnect");

                    cBTInitSendReceive = new classBTInitDataCommunication(BTSocket);
                    cBTInitSendReceive.start();

                    bBTConnected = true;
                    break;
                case BT_STATE_CONNECTION_FAILED:
                    iBTConnectionStatus = BT_CON_STATUS_FAILED;
                    Log.d(TAG, "BT_STATE_CONNECTION_FAILED");
                    bBTConnected = false;
                    break;

                case BT_STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    Log.d(TAG, "Message receive ( " + tempMsg.length() + " )  data : " + tempMsg);

                    tvReceivedMessage.append(tempMsg);


                    break;

            }
            return true;
        }
    });


    public void sendMessage(String sMessage)
    {
        if( BTSocket!= null && iBTConnectionStatus==BT_CON_STATUS_CONNECTED)
        {
            if(BTSocket.isConnected() )
            {
                try {
                    cBTInitSendReceive.write(sMessage.getBytes());
                    tvReceivedMessage.append("\r\n-> " + sMessage);
                }
                catch (Exception exp)
                {

                }
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Please connect to bluetooth", Toast.LENGTH_SHORT).show();
            tvReceivedMessage.append("\r\n Not connected to bluetooth");
        }

    }

    void getBTPairedDevices()
    {
        Log.d(TAG, "getBTPairedDevices - start ");
        BTAdaptor = BluetoothAdapter.getDefaultAdapter();
        if(BTAdaptor == null)
        {
            Log.e(TAG, "getBTPairedDevices , BTAdaptor null ");
            editTextSentMessage.setText("\nNo Bluetooth Device in the phone");
            return;

        }
        else if(!BTAdaptor.isEnabled())
        {
            Log.e(TAG, "getBTPairedDevices , BT not enabled");
            editTextSentMessage.setText("\nPlease turn ON Bluetooth");
            return;
        }

        BTPairedDevices = BTAdaptor.getBondedDevices();
        Log.d(TAG, "getBTPairedDevices , Paired devices count = " + BTPairedDevices.size());

        for (BluetoothDevice BTDev : BTPairedDevices)
        {
            Log.d(TAG, BTDev.getName() + ", " + BTDev.getAddress());
        }


    }

    void populateSpinnerWithBTPairedDevices()
    {
        ArrayList<String> alPairedDevices = new ArrayList<>();
        alPairedDevices.add("Select");
        for (BluetoothDevice BTDev : BTPairedDevices)
        {
            alPairedDevices.add(BTDev.getName());
        }
        final ArrayAdapter<String> aaPairedDevices = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,alPairedDevices);
        aaPairedDevices.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerBTPairedDevices.setAdapter(aaPairedDevices);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume-Resume");

        getBTPairedDevices();
        populateSpinnerWithBTPairedDevices();
        readAllData();


    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause-Start");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy-Start");
    }
}