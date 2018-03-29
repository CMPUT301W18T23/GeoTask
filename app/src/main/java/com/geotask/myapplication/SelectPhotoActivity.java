package com.geotask.myapplication;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.geotask.myapplication.Adapters.PhotoAdapter;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;


import java.util.ArrayList;
import java.util.List;

public class SelectPhotoActivity extends AppCompatActivity {
    private GridView gridView;
    private PhotoAdapter adapter;
    private List<String> list;
    ImagePicker imagePicker ;

    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        gridView = (GridView) findViewById(R.id.grid_view);
        initView();
    }
    private void initView() {
        imagePicker = new ImagePicker();
        imagePicker.setTitle("Select Photo");
        imagePicker.setCropImage(true);
        list = new ArrayList<>();
        adapter = new PhotoAdapter(this, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View
                    view, int position, long id) {
                if (position==parent.getChildCount()-1){
                    if (position==6){
                    }else{
                        opnePhoto();
                    }
                }else{
                    Intent intent = new Intent(SelectPhotoActivity.this, ViewPhotoActivity.class);
                    intent.putExtra("ID",list.get(position));
                    startActivity(intent);

                }
            }
        });
    }
    public void opnePhoto() {
        imagePicker.startChooser(this, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
            }
            @Override
            public void onCropImage(Uri imageUri) {
                if (list.size()>=6){
                    Toast.makeText(SelectPhotoActivity.this,"No more than 6 photos",Toast.LENGTH_LONG).show();
                }else{
                    list.add(String.valueOf(imageUri));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder
                                           builder) {
                builder
                        .setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.OFF)
                        .setCropShape(CropImageView.CropShape
                                .RECTANGLE)
                        .setRequestedSize(1920, 1080)
                        .setAspectRatio(16, 9);
            }
            @Override
            public void onPermissionDenied(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int
            resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(SelectPhotoActivity.this,requestCode, resultCode, data);

    }
}
