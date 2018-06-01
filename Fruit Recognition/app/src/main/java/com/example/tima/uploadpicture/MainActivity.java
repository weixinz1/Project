package com.example.tima.uploadpicture;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.Bundle;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends Activity {

    public static final int REQUEST_CAPTURE = 1;
    ImageView uploadPhoto;
    Button capture, upload,connect;
    private static final String SERVER_ADDRESS = "https://tinadbx.000webhostapp.com/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        uploadPhoto = (ImageView) findViewById(R.id.imageView);
        capture = (Button) findViewById(R.id.Bcapture);
        upload = (Button) findViewById(R.id.Bupload);
        connect=(Button) findViewById(R.id.Connect);


        if(!hasCamera())
        {
            capture.setEnabled(false);
        }

        capture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, REQUEST_CAPTURE);
            }
        });
        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bitmap image = ((BitmapDrawable) uploadPhoto.getDrawable()).getBitmap();
                new UploadImage(image).execute();
            }
        });
        connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Thread myThread=new Thread(new MyServerThread());
                myThread.start();
            }
        });
    }

    public boolean hasCamera()
    {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            uploadPhoto.setImageBitmap(photo);
        }
    }

    private class UploadImage extends AsyncTask<Void, Void, Void>
    {

          Bitmap image;
        public UploadImage(Bitmap image)
        {
            this.image = image;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("image", encodeImage));

            HttpParams httpRequestParams = getHttpRequestParams();
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "SavePicture.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
        }
    }


    private HttpParams getHttpRequestParams()
    {
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 30);
        return httpRequestParams;
    }

    class MyServerThread implements Runnable
    {
        Socket s;
        InputStreamReader in;
        BufferedReader reader;
        Handler h=new Handler();

        String message;
        @Override
        public void run()
        {
            try
            {
                s=new Socket("192.168.1.102",3002);
                    in=new InputStreamReader(s.getInputStream());
                    reader=new BufferedReader(in);
                    message=reader.readLine();

                    h.post(new Runnable(){
                        @Override
                        public void run(){
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }
                    });
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}
