package com.example.pm01ex3premz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicamentosListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Medicamentos> medicamentosList;

    public MedicamentosListAdapter(Context context, int layout, ArrayList<Medicamentos> medicamentosList) {
        this.context = context;
        this.layout = layout;
        this.medicamentosList = medicamentosList;
    }

    @Override
    public int getCount() {
        return medicamentosList.size();
    }

    @Override
    public Object getItem(int position) {
        return medicamentosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView Descripcion, Dosis;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);

            holder.Descripcion = row.findViewById(R.id.viewDes);
            holder.Dosis = row.findViewById(R.id.viewDosis);
            holder.imageView = row.findViewById(R.id.medicImage);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Medicamentos medicamentos = medicamentosList.get(position);

        holder.Descripcion.setText(medicamentos.getDesc());
        holder.Dosis.setText("Cantidad: " + medicamentos.getCantidad()
        + " | " + medicamentos.getTiempo() + " " + medicamentos.getPeriodo());

        byte[] medicImage = medicamentos.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(medicImage, 0, medicImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
