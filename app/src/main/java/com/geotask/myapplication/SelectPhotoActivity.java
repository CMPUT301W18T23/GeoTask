package com.geotask.myapplication;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.geotask.myapplication.Adapters.PhotoAdapter;
import com.geotask.myapplication.Adapters.ViewPhotoAdapter;
import com.linchaolong.android.imagepicker.ImagePicker;
import com.linchaolong.android.imagepicker.cropper.CropImage;
import com.linchaolong.android.imagepicker.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * this is the photo selector acticity
 *
 * references by: https://blog.csdn.net/qq_26841579/article/details/72438318
 */

public class SelectPhotoActivity extends AppCompatActivity {
    private GridView gridView;
    private PhotoAdapter adapter;
    private ViewPhotoAdapter adapter2;
    private List<byte[]> list;
    ImagePicker imagePicker ;
    private Button saveButton;
    private Button cancelButton;


    /**
     * initial the variables
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);
        gridView = (GridView) findViewById(R.id.grid_view);
        list = new ArrayList<>();
        Intent intent = this.getIntent();
        final String typecase = intent.getStringExtra("type");
        System.out.println(typecase);
        int size = Integer.parseInt(intent.getExtras().get(getString(R.string.PHOTO_LIST_SIZE)).toString());
        for (int i = 0; i < size; i++) {
            list.add(intent.getByteArrayExtra("list"+i));
        }


        /**
         * if the typecase is add, then this is the layout for requester edit
         */
        if(typecase.equals("add")){
            System.out.println(typecase);
            adapter = new PhotoAdapter(this, list);
            initView(adapter);
            cancelButton = (Button)findViewById(R.id.Cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
             @Override
                public void onClick(View view) {
                 finish();
                }
            });



            saveButton = (Button) findViewById(R.id.SavePhoto);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent tempIntent = new Intent();
                    tempIntent.putExtra(getString(R.string.PHOTO_LIST_SIZE), list.size());
                    setResult(1,tempIntent);
                    for (int i = 0; i < list.size(); i++) {
                        tempIntent.putExtra("list"+i, list.get(i));
                        setResult(1,tempIntent);
                    }
                    finish();
                }
            });
        }
        /**
         * if the typecase is view, this is the layout for provider view
         */
        else if(typecase.equals("view")){
            adapter2 = new ViewPhotoAdapter(this, list);

            gridView.setAdapter(adapter2);
            gridView.setOnItemClickListener(new AdapterView
                    .OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View
                        view, int position, long id) {
                    if(position != parent.getChildCount()-1){
                        Intent intent = new Intent(SelectPhotoActivity.this, ViewPhotoActivity.class);
                        intent.putExtra("ID",list.get(position));
                        startActivity(intent);}


                }
            });

            saveButton = findViewById(R.id.SavePhoto);
            saveButton.setVisibility(View.GONE);
            cancelButton = findViewById(R.id.Cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }

    }

    /**
     * init the photo adapter
     * and then open the select photo function
     * @param adapter
     */
    private void initView(PhotoAdapter adapter) {
        imagePicker = new ImagePicker();
        imagePicker.setTitle("Select Photo");
        imagePicker.setCropImage(true);

        //adapter = new PhotoAdapter(this, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView
                .OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View
                    view, int position, long id) {
                if (position==parent.getChildCount()-1){
                    if (position==10){
                    }else{
                        openPhoto();
                    }
                }else{
                    Intent intent = new Intent(SelectPhotoActivity.this, ViewPhotoActivity.class);
                    intent.putExtra("ID",list.get(position));
                    startActivity(intent);

                }
            }
        });
    }

    /**
     * open the photo selecter and check the photo size is less than 64kb
     */
    public void openPhoto() {
        imagePicker.startChooser(this, new ImagePicker.Callback() {
            @Override
            public void onPickImage(Uri imageUri) {
            }
            @Override
            public void onCropImage(Uri imageUri) {
                if (list.size()>=10){
                    Toast.makeText(SelectPhotoActivity.this,"No more than 10 photos",Toast.LENGTH_LONG).show();
                }else{
                    Uri img = imageUri;
                    try {
                        InputStream iStream = getContentResolver().openInputStream(imageUri);
                        byte[] inputData = getBytes(iStream);
                        if(inputData.length<72990){
                            list.add(inputData);
                        }else{
                            Toast.makeText(SelectPhotoActivity.this,"Too large photo",Toast.LENGTH_SHORT).show();
                        }

                    }catch(FileNotFoundException e){e.printStackTrace();}
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void cropConfig(CropImage.ActivityBuilder
                                           builder) {
                builder
                        .setMultiTouchEnabled(false)
                        .setGuidelines(CropImageView.Guidelines.OFF);
            }
            @Override
            public void onPermissionDenied(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
            }
        });
    }

    /**
     * get the result of photo selecter
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int
            resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.onActivityResult(SelectPhotoActivity.this,requestCode, resultCode, data);

    }


    /**
     * change the photo format into byte[]
     * @param inputStream
     * @return
     */
    public byte[] getBytes(InputStream inputStream){
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        try {while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }}catch (IOException ex){ex.printStackTrace();}

        return byteBuffer.toByteArray();
    }





}
