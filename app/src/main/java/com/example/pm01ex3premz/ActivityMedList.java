package com.example.pm01ex3premz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class ActivityMedList extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner tiempo;

    GridView gridView;
    ArrayList<Medicamentos> list;
    MedicamentosListAdapter adapter = null;

    final int REQUEST_CODE_GALLERY = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_list);

        gridView = (GridView) findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new MedicamentosListAdapter(this, R.layout.medicamentos_items, list);
        gridView.setAdapter(adapter);

        //get data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM MEDICAMENTOS");
        list.clear();

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String desc = cursor.getString(1);
            String cantidad = cursor.getString(2);
            String tiempo = cursor.getString(3);
            String periodo = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            list.add(new Medicamentos(id, desc, cantidad, tiempo, periodo, image));
        }

        adapter.notifyDataSetChanged();

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence[] items = {"Editar","Eliminar"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(ActivityMedList.this);

                dialog.setTitle("Eliga una acción");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM MEDICAMENTOS");

                            ArrayList<Integer> arrID = new ArrayList<Integer>();

                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }

                            showDialogUpdate(ActivityMedList.this, arrID.get(position));
                        }
                        else {
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM MEDICAMENTOS");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });

                dialog.show();
                return true;
            }
        });
    }

    ImageView imageView;
    EditText txtPeriodo;

    //DIALOGO PARA EDITAR
    private void showDialogUpdate(Activity activity, int position) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.activity_update);
        dialog.setTitle("Editar");

        /* INICIO COGIDO SPINNER */
        tiempo = dialog.findViewById(R.id.txtTiempoEdit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tiempos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tiempo.setAdapter(adapter);
        tiempo.setOnItemSelectedListener(this);
        /* FIN COGIDO SPINNER */

        imageView = dialog.findViewById(R.id.imageViewEdit);
        EditText txtDescp = dialog.findViewById(R.id.txtDescEdit);
        EditText txtCantidad = dialog.findViewById(R.id.txtCantidadEdit);
        txtPeriodo = dialog.findViewById(R.id.txtPeriodoEdit);
        Button btnEdit = dialog.findViewById(R.id.btnEdit);

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        ActivityMedList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });


        //BOTON PARA LLAMAR FUNCION DE EDITAR
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.sqLiteHelper.updateData(
                            txtDescp.getText().toString().trim(),
                            txtCantidad.getText().toString().trim(),
                            tiempo.getSelectedItem().toString().trim(),
                            txtPeriodo.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageView),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Editado",Toast.LENGTH_SHORT).show();
                }
                catch (Exception error) {
                    Log.e("Error Actualizar", error.getMessage());
                }
                updateMedList();
            }
        });
    }


    //DIALOGO PARA ELIMINAR
    private void showDialogDelete(final int idFood){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(ActivityMedList.this);

        dialogDelete.setTitle("Alerta!!");
        dialogDelete.setMessage("Está seguro que quiere eliminar este dato?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelper.deleteData(idFood);
                    Toast.makeText(getApplicationContext(), "Eliminado",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateMedList();
            }
        });

        dialogDelete.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    //ACTUALIZAR LISTA DE MEDICAMENTOS LUEGO DE EDITAR/ELIMINAR
    private void updateMedList(){
        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM MEDICAMENTOS");
        list.clear();

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String desc = cursor.getString(1);
            String cantidad = cursor.getString(2);
            String tiempo = cursor.getString(3);
            String periodo = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            list.add(new Medicamentos(id, desc, cantidad, tiempo, periodo, image));
        }

        adapter.notifyDataSetChanged();
    }


    //PERMISOS, SPINNER CODE, ETC
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
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

        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
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
}