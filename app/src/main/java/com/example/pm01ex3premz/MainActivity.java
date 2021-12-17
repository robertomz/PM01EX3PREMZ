package com.example.pm01ex3premz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner tiempo;
    EditText txtDesc, txtCantidad, txtPeriodo;
    ImageView imageView;
    Button btnAdd, btnList;

    public static SQLiteHelper sqLiteHelper;
    final int REQUEST_CODE_GALLERY = 999;

    NotificationManagerCompat notificationManagerCompat;
    Notification notification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* INICIO COGIDO SPINNER */
        tiempo = findViewById(R.id.txtTiempo);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tiempos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiempo.setAdapter(adapter);
        tiempo.setOnItemSelectedListener(this);
        /* FIN COGIDO SPINNER */

        txtDesc = findViewById(R.id.txtDesc);
        txtCantidad = findViewById(R.id.txtCantidad);
        txtPeriodo = findViewById(R.id.txtPeriodo);
        imageView = findViewById(R.id.imageView);
        btnAdd = findViewById(R.id.btnAdd);
        btnList = findViewById(R.id.btnList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Medicamentos", "Medicamentos", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        sqLiteHelper = new SQLiteHelper(this, "MedicamentosDB.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS MEDICAMENTOS (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "descp TEXT, cantidad TEXT, tiempo TEXT, periodo TEXT, image BLOB)");


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    sqLiteHelper.insertData(
                            txtDesc.getText().toString().trim(),
                            txtCantidad.getText().toString().trim(),
                            tiempo.getSelectedItem().toString().trim(),
                            txtPeriodo.getText().toString().trim(),
                            imageViewToByte(imageView)
                    );

                    if (tiempo.getSelectedItem().toString().equals("Diaria")) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "Medicamentos")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("MEDICAMENTO: " + txtDesc.getText().toString().toUpperCase())
                                .setContentText("NO OLVIDES TOMAR TU MEDICAMENTO DIARIAMENTE");

                        notification = builder.build();
                        notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                    }
                    else {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "Medicamentos")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("MEDICAMENTO: " + txtDesc.getText().toString().toUpperCase())
                                .setContentText("NO OLVIDES TOMAR TU MEDICAMENTO CADA " + txtPeriodo.getText().toString() + " HORAS");

                        notification = builder.build();
                        notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                    }

                    Toast.makeText(getApplicationContext(), "Añadido", Toast.LENGTH_SHORT).show();

                    Notify();
                    Clean();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityMedList.class);
                startActivity(intent);
            }
        });
    }

    private void Clean() {
        txtPeriodo.setText("");
        txtDesc.setText("");
        txtCantidad.setText("");
        imageView.setImageResource(R.mipmap.ic_launcher);
    }

    private void Notify() {
        notificationManagerCompat.notify(1, notification);
    }



    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "No tienes acceso a los archivos", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (tiempo.getSelectedItem().equals("Horas")) {
            txtPeriodo.setEnabled(true);
        }
        else if (tiempo.getSelectedItem().equals("Diaria")) {
            txtPeriodo.setEnabled(false);
            txtPeriodo.setText("");
        }
        else {
            txtPeriodo.setEnabled(false);
            txtPeriodo.setText("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}