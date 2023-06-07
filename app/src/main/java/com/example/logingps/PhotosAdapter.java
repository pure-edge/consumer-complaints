package com.example.logingps;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PhotosAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Uri> photoURIs = new ArrayList<>();

    public PhotosAdapter(Context c){
        context = c;
    }

    public void addItem(String uri) {
        photoURIs.add(Uri.parse(uri));
        notifyDataSetChanged();
    }

    public void addItem(Uri uri) {
        photoURIs.add(uri);
        notifyDataSetChanged();
    }

    public List<Uri> getCapturedPhotoUris() {
        return photoURIs;
    }

    @Override
    public int getCount() {
        return photoURIs.size();
    }

    @Override
    public Object getItem(int position) {
        return photoURIs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null){
            convertView = inflater.inflate(R.layout.gridview_item, null);
        }

        ImageView imageView = convertView.findViewById(R.id.photo_image_view);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoURIs.remove(position);
                notifyDataSetChanged();
            }
        });

        Glide.with(context).asBitmap().load(photoURIs.get(position)).into(imageView);

        return convertView;
    }
}
