package com.bing.face_re;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

public class CameraSurface extends Activity implements OnClickListener,
SurfaceHolder.Callback, Camera.PictureCallback, PreviewCallback {
	SurfaceView cameraView;
	SurfaceHolder surfaceHolder;
	Camera camera;
	private EditText num;
	private EditText name;
	private String numget;
	private String nameget;
	
	private Button CamButton;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	int i=1;
	private int info = 0;
	Intent intent = new Intent();
	Bundle bundle = new Bundle(); 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera);
		preferences = getSharedPreferences("persondata",MODE_WORLD_READABLE);
		editor = preferences.edit();
		intent = this.getIntent();        //获取已有的intent对象   
		bundle = intent.getExtras();    //获取intent里面的bundle对象   
		info = bundle.getInt("something");    //获取Bundle里面的字符串 
		
		if(info == 1){
			
		DrawCaptureRect mDraw = new DrawCaptureRect(CameraSurface.this, 200,450,400,120,getResources().getColor(R.drawable.lightred));
		addContentView(mDraw, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		}
		if(info == 2)
		{
			DrawCaptureRect mDraw = new DrawCaptureRect(CameraSurface.this, 200,300,400,120,getResources().getColor(R.drawable.lightred));
			addContentView(mDraw, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			TableLayout loginForm = (TableLayout)getLayoutInflater()
					.inflate( R.layout.iputname, null);
			
			name = (EditText)loginForm.findViewById(R.id.iput_editTextname);
			num =  (EditText)loginForm.findViewById(R.id.iput_editTextID);
				new AlertDialog.Builder(CameraSurface.this)
					// 设置对话框的图标
					
					// 设置对话框的标题
					.setTitle("请输入ID号和姓名")
					// 设置对话框显示的View对象
					.setView(loginForm)
					// 为对话框设置一个“确定”按钮
					.setPositiveButton("确定" , new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog,
								int which)
						{
							// 此处可执行登录处理				
							numget = num.getText().toString();
							nameget = name.getText().toString();
							Log.i("tag", numget);
							Log.i("tag", nameget);
							editor.putString(num.getText().toString(), name.getText().toString());							
							editor.commit();
							
						}
						
						
					})
					// 为对话框设置一个“取消”按钮
					.setNegativeButton("取消", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog,
								int which)
						{
							// 取消登录。		 
							CameraSurface.this.finish();
						}
						
					})
					// 创建、并显示对话框
					.create()
					.show();	
		}
		if(info == 3){
			DrawCaptureRect mDraw = new DrawCaptureRect(CameraSurface.this, 200,450,400,120,getResources().getColor(R.drawable.lightred));
			addContentView(mDraw, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			}
		//在一个activity上面添加额外的content
//		 addContentView(mDraw, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		CamButton=(Button)findViewById(R.id.camera_ok);
		
		CamButton.setOnClickListener(listener);
		cameraView = (SurfaceView) this.findViewById(R.id.camera);
		surfaceHolder = cameraView.getHolder();
//		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		cameraView.setFocusable(true);
		cameraView.setFocusableInTouchMode(true);
		cameraView.setClickable(true);
	    

//		cameraView.setOnClickListener(this);
		
	
	}
	 class DrawCaptureRect extends View
	    {
	     private int mcolorfill;
	     private int mleft, mtop, mwidth, mheight;
	     public DrawCaptureRect(Context context,int left, int top, int width, int height, int colorfill) {
	      super(context);
	      // TODO Auto-generated constructor stub
	      this.mcolorfill = colorfill;
	      this.mleft = left;
	      this.mtop = top;
	      this.mwidth = width;
	      this.mheight = height;
	  }
	  @Override
	  protected void onDraw(Canvas canvas) {
	   // TODO Auto-generated method stub
	   Paint mpaint = new Paint();
	   mpaint.setColor(mcolorfill);
	   mpaint.setStyle(Paint.Style.FILL);
	   mpaint.setStrokeWidth(1.0f);
	   canvas.drawLine(mleft, mtop, mleft+mwidth, mtop, mpaint);
	   canvas.drawLine(mleft+mwidth, mtop, mleft+mwidth, mtop+mheight, mpaint);
	   canvas.drawLine(mleft, mtop, mleft, mtop+mheight, mpaint);
	   canvas.drawLine(mleft, mtop+mheight, mleft+mwidth, mtop+mheight, mpaint);
//	   canvas.drawLine(mleft, mtop+mheight/3, mleft+mwidth, mtop+mheight/3, mpaint);
	   super.onDraw(canvas); 
	  }
	 
	    }
	 private OnClickListener listener= new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 int id = v.getId();  
			        switch(id){  
			            case R.id.camera_ok:
			            	
//			            	Log.i("tag", num.getText().toString());
			            	Log.i("tag", "2222");
			            	camera.takePicture(null, null, CameraSurface.this);		                 
			                break;  
			            default:
			            	 break;	
			                   }  
				
			}
			
		};
	public void onClick(View v) {
		camera.takePicture(null, null, this);
		
	}

	public void onPictureTaken(byte[] data, Camera camera) {
		Uri imageFileUri = getContentResolver().insert(
				Media.EXTERNAL_CONTENT_URI, new ContentValues());
		try {
			if(info == 2){
//			editor.putString(num.getText().toString(), name.getText().toString());
//			editor.commit();
//			String path = Environment.getExternalStorageDirectory()
//					+ "/FaceNew/"+num.getText()+"-"+name.getText()+"_"+i+".jpg";
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/FaceNew");
				  //判断文件夹否存,存则创建文件夹
			if (!file.exists()) {
				   file.mkdir();
		    }
			String path = Environment.getExternalStorageDirectory()
					+ "/FaceNew/"+numget+"-"+nameget+"_"+i+".jpg";
			// data2file(data, path);
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Matrix matrix = new Matrix();
			// 设置图像的旋转角度
			matrix.setRotate(270);
			// 旋转图像，并生成新的Bitmap对像
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			writePhoto(bitmap, bitmap.getWidth(), bitmap.getHeight(),path);
			i++;
			if(i==2){
				DrawCaptureRect	mDraw2 = new DrawCaptureRect(CameraSurface.this, 200,450,400,120,getResources().getColor(R.drawable.blue));
				addContentView(mDraw2, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
				Log.i("222", ""+i);
			}
			if(i==3){
				DrawCaptureRect    mDraw3 = new DrawCaptureRect(CameraSurface.this, 200,600,400,120,getResources().getColor(R.drawable.green));
				addContentView(mDraw3, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				Log.i("333", ""+i);
			}
			if(i == 4){
				intent = intent.setClass(CameraSurface.this, MainActivity.class);     
				bundle.putInt("result", 2);   
				intent.putExtras(bundle);       
				CameraSurface.this.setResult(2, intent);   //RESULT_OK是返回状态码  
				CameraSurface.this.finish();	
			}
				
			Thread PicTaken = new Thread(new PicTaken());
			PicTaken.start();
			}
			if(info == 1)
			{	
				
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/bing");
					  //判断文件夹否存,存则创建文件夹
				if (!file.exists()) {
					   file.mkdir();
			    }
				String path = Environment.getExternalStorageDirectory()
						+ "/bing/face.jpg";
				// data2file(data, path);
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				Matrix matrix = new Matrix();
				// 设置图像的旋转角度
				matrix.setRotate(270);
				// 旋转图像，并生成新的Bitmap对像
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
				writePhoto(bitmap, bitmap.getWidth(), bitmap.getHeight(),path);   
				intent = intent.setClass(CameraSurface.this, MainActivity.class);     
				bundle.putInt("result", 1);   
				intent.putExtras(bundle);       
				CameraSurface.this.setResult(2, intent);   //RESULT_OK是返回状态码  
				CameraSurface.this.finish();
				
			}
			if(info == 3)
			{   Log.i("tag", "进入3");

				File file = new File(Environment.getExternalStorageDirectory()
						+ "/bing");
				  //判断文件夹否存,存则创建文件夹
				if (!file.exists()) {
					file.mkdir();
				}
				String path = Environment.getExternalStorageDirectory()
						+ "/bing/face.jpg";
				// data2file(data, path);
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				Matrix matrix = new Matrix();
				// 设置图像的旋转角度
				matrix.setRotate(270);
				// 旋转图像，并生成新的Bitmap对像
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
				writePhoto(bitmap, bitmap.getWidth(), bitmap.getHeight(),path);   
				intent = intent.setClass(CameraSurface.this, Unlock.class);     
				bundle.putInt("result", 1);   
				intent.putExtras(bundle);       
				CameraSurface.this.setResult(2, intent);   //RESULT_OK是返回状态码  
				CameraSurface.this.finish();
				
			}
		} catch (Exception e) {
		}
		camera.startPreview();
	}
	class PicTaken implements Runnable {
		public void run() {
			Message message = new Message();
			if(i<=3){
			message.what = 0;
			updateUI.sendMessage(message);
			}
		}
	}
	private Handler updateUI = new Handler() {
		@Override

		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case 0: 
				break;
			case 1:
					intent.setClass(getApplicationContext(), MainActivity.class);
					startActivity(intent);
				}
			}
		};
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		
		camera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(1); 
		try {
			
			camera.setPreviewDisplay(holder);
			Camera.Parameters parameters = camera.getParameters();
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "portrait");

				// For Android Version 2.2 and above
				camera.setDisplayOrientation(90);

				// For Android Version 2.0 and above
				parameters.setRotation(90);
			}

			// Effects are for Android Version 2.0 and higher
			List<String> colorEffects = parameters.getSupportedColorEffects();
			Iterator<String> cei = colorEffects.iterator();
			while (cei.hasNext()) {
				String currentEffect = cei.next();
				if (currentEffect.equals(Camera.Parameters.EFFECT_SOLARIZE)) {
					parameters
							.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
					break;
				}
			}
			// End Effects for Android Version 2.0 and higher

			camera.setParameters(parameters);
		} catch (IOException exception) {
			camera.release();
		}
	}
	public void writePhoto(Bitmap bmp, int width, int height, String path) {
		File file = new File(path);
		try {
			Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, width, height);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			if (bm.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
				bos.flush();
				bos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
	}

	@Override
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub
		 Canvas canvas = surfaceHolder.lockCanvas();  
	        canvas.drawColor(Color.BLACK);  
	        Paint p = new Paint();  
	        p.setAntiAlias(true);  
	        p.setColor(Color.RED);  
	        p.setStyle(Style.STROKE);  
	        //canvas.drawPoint(100.0f, 100.0f, p);  
	       // canvas.drawLine(0,110, 500, 110, p);  
	        canvas.drawCircle(110, 110, 10.0f, p);  
	        surfaceHolder.unlockCanvasAndPost(canvas);
		
	}

}
