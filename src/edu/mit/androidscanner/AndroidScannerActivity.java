package edu.mit.androidscanner;

import java.nio.IntBuffer;

import org.libdmtx.DMTXImage;
import org.libdmtx.DMTXTag;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AndroidScannerActivity extends Activity {
    private static final int REQ_CODE_PICK_IMAGE = 0;
    private static final int REQ_CODE_CAMERA = 1;

	static {
    	System.loadLibrary("dmtx");
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button cameraButton = (Button)findViewById(R.id.camerabutton);
        cameraButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
//				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				startActivityForResult(cameraIntent, 0);
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);    
			}
		});
    }
    
    private void decode(Bitmap img) {
    	System.out.println("DECODING");
    	int width = 800;
    	int height = 800;
    	System.out.println(img.getWidth());
    	System.out.println(img.getHeight());
    	Bitmap scaledBitmap = Bitmap.createScaledBitmap(img, (int) (img.getWidth() * 0.5), (int)(img.getHeight() * 0.5), true);
    	int midx = scaledBitmap.getWidth() / 2;
    	int midy = scaledBitmap.getHeight() / 2;
    	Bitmap cropImg = Bitmap.createBitmap(scaledBitmap, midx - width / 2, midy - height/2, width, height);
    	int size = cropImg.getHeight() * cropImg.getWidth();
    	IntBuffer buff = IntBuffer.allocate(size);
    	cropImg.copyPixelsToBuffer(buff);
    	DMTXImage dmtxImage = new DMTXImage(cropImg.getWidth(), cropImg.getHeight(), buff.array());
    	DMTXTag[] tags = dmtxImage.getTags(5, 10000);
    	System.out.println(tags.length);
    	StringBuilder sb = new StringBuilder();
    	sb.append(tags.length + " tags found\n");
    	for (DMTXTag tag: tags) {
    		sb.append(tag.id + "\n");
    		System.out.println(tag.id);
    	}
    	String text = sb.toString();
    	EditText textView = (EditText)findViewById(R.id.textView);
    	textView.setText(text);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	if (requestCode == REQ_CODE_PICK_IMAGE) {
	        if(resultCode == RESULT_OK){  
	            Uri selectedImage = intent.getData();
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};
	
	            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();
	
	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();
	
	
	            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
	            decode(yourSelectedImage);
	        }
    	} else if (requestCode == REQ_CODE_CAMERA) {
	    	Bitmap img = (Bitmap) intent.getExtras().get("data");
	    	decode(img);
    	}
    }
}

