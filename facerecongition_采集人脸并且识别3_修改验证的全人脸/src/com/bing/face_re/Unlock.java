package com.bing.face_re;



import static com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.NORM_L2;
import static com.googlecode.javacv.cpp.opencv_core.cvNorm;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvMat;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COMP_CORREL;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HIST_ARRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_GRAY2BGR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCompareHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvNormalizeHist;

import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer;

import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1; 
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvReshape;
import static com.googlecode.javacv.cpp.opencv_core.cvReshapeND;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Environment;


import android.util.Log;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.SurfaceHolder;

import com.bing.face_re.FacePojo;
import com.bing.face_re.R;


import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;

import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;

import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;
/*
 *        蓝牙接收线程类
 * 
 * */
class bluetoothMsgThread extends Thread {
	private DataInputStream mmInStream;           //in数据流
	private Handler msgHandler;                   //Handler
	public bluetoothMsgThread(DataInputStream mmInStream,Handler msgHandler) {   //构造函数，获得mmInStream和msgHandler对象
		this.mmInStream = mmInStream;
		this.msgHandler = msgHandler;
	}
	
	public void run() {
		byte[] InBuffer = new byte[64];           //创建 缓冲区      
		while (!Thread.interrupted()) {                             
		  try {
		    mmInStream.readFully(InBuffer, 0, 8); //读取蓝牙数据流
		    Message msg = new Message();          //定义一个消息,并填充数据
		    msg.what = 0x1234;
		    msg.obj = InBuffer;
		    msgHandler.sendMessage(msg);          //通过handler发送消息
		  }catch(IOException e) {
			  e.printStackTrace();
		  }
		}
	}
}

public class Unlock extends Activity {
	private BluetoothAdapter mBluetoothAdapter;   //蓝牙适配器
	private BluetoothDevice device;               //蓝牙设备
	private BluetoothSocket clientSocket;         //socket 通讯用
	private BluetoothServerSocket btserver;       //未用上
	private String address;                       //蓝牙设备地址
	private OutputStream mmOutStream;             //out数据流
	private DataInputStream mmInStream;           //in数据流
	private bluetoothMsgThread blue_tooth_msg_thread;
	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  //蓝牙连接用 UUID 标识
	
	private Intent intent = new Intent(); 
	private Bundle bundle = new Bundle(); 
	private TextView textView = null;
	
	
	private Vibrator vib;                         //手机系统震动 对象  
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private String name;
	private int k = 0;
	private double confidence = 1;
	private int index;
	private int isture = 0;
	private double[] value = new double[100];
	private String FACE=Environment.getExternalStorageDirectory()  
            + "/bing/face.jpg";
	private static final String TAG="Face_Recognition";
	private CascadeClassifier mjavaClassifier;
	
	/*
	 *   显示从蓝牙设备接收到的数据
	 * */
	public void show_result(byte[] buffer,int count)
	{
		StringBuffer msg = new StringBuffer();                                //创建缓冲区
		TextView tvInfo = (TextView)findViewById(R.id.textViewReceiveInfo);   //创建 文本显示对象
		tvInfo.setText("");                                                   //清空对象内容 
		for (int i = 0; i < count; i++)                                       //循环 加入 数据，16进制 格式  
		  msg.append(String.format("0x%x ", buffer[i]));
				
		tvInfo.setText(msg);		                                           //显示到界面上
	}
	
	/*
	 *   设置按钮的状态,根据入参设置一批 按钮的状态
	 * 
	 * */
	public void set_btn_status(boolean status)
	{			
		Button jdqonBtn = (Button)findViewById(R.id.jdqonBtn);
		jdqonBtn.setEnabled(status);
		Button jdqoffBtn = (Button)findViewById(R.id.jdqoffBtn);
		jdqoffBtn.setEnabled(status);
		Button recon = (Button)findViewById(R.id.recon_recon);
		recon.setEnabled(status);
		
	}
	
	protected void onDestroy() {
		super.onDestroy();		
		try {
		  if (mmOutStream != null)           
		    mmOutStream.close();             //关闭 out 数据流
		  if (mmInStream != null)
			  mmInStream.close();            //关闭 in 数据流
		  if (clientSocket != null)
		    clientSocket.close();            //关闭socket
		  blue_tooth_msg_thread.interrupt();
//		  Toast.makeText(getApplicationContext(), "蓝牙测试应用程序退出", Toast.LENGTH_LONG).show();  //提示信息
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {

		private File mCascadeFile;

		@Override
		public void onManagerConnected(int status) {
			// TODO Auto-generated method stub
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				try {
                    // load cascade file from application resources
                    InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                    mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
                    FileOutputStream os = new FileOutputStream(mCascadeFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();
				mjavaClassifier=new CascadeClassifier(mCascadeFile.getAbsolutePath());
				if (mjavaClassifier!=null) {
//					Toast.makeText(unlock.this, "加载成功"+mjavaClassifier.toString(), Toast.LENGTH_LONG).show();
				}
				cascadeDir.delete();
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			super.onManagerConnected(status);
		}
		
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;//消费掉后退键 
        }
        return super.onKeyDown(keyCode, event);
    }        
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.recon);
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);	
		textView = (TextView)this.findViewById(R.id.camera_textView);
		set_btn_status(false);                                          //蓝呀设备未连接，设置 屏幕 一些 按钮 不能操作
		vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);   //获取手机震动对象
		preferences = getSharedPreferences("persondata",MODE_WORLD_READABLE);
		editor = preferences.edit();
		Button searchDeviceBtn = (Button)findViewById(R.id.searchDeviceBtn);	//创建 搜索按键对象 并 监听 click事件			
		searchDeviceBtn.setOnClickListener(new View.OnClickListener() { 						
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
		       	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();       //获取 蓝牙 适配器     
		       	if (mBluetoothAdapter == null) {                                //手机无蓝牙功能，提示并退出
		       		Toast.makeText(getApplicationContext(), "bluetooth is no available",Toast.LENGTH_LONG).show();
		       		finish();
		       		return;
		       	}
		       	
		       	mBluetoothAdapter.enable();  		       	//打开手机 蓝牙 功能
		        try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	if (!mBluetoothAdapter.isEnabled()) {                         
		    		mBluetoothAdapter.enable();
		       	}
		       	if (!mBluetoothAdapter.isEnabled()) {                           //手机未打开蓝牙功能，提示并退出
		       		Toast.makeText(getApplicationContext(), "蓝牙功能未打开，请重新搜索",Toast.LENGTH_LONG).show();
//		       		finish();
		       		return;
		       	}
		       	
		       	
		        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();    //获取 已经配对的蓝牙设备列表
		       	if (pairedDevices.size() < 1) {                                               //无配对蓝牙设备,则退出
		       		Toast.makeText(getApplicationContext(), "没有找到已经配对的蓝牙设备,请配对后再操作",Toast.LENGTH_LONG).show();
		       		finish();
		       		return;
		       	}
		       	
		       	Spinner spinner = (Spinner)findViewById(R.id.spinner1);       //获取 下拉框控件 对象
		       	List<String>list = new ArrayList<String>();                   //创建列表，用于保存蓝牙设备地址
		       	for (BluetoothDevice device:pairedDevices) {
		       		//myArrayAdapter.add(device.getName() + " " + device.getAddress());
		       	//	list.add(device.getName() + " " + device.getAddress());
		       		list.add(device.getAddress());                            //将蓝牙地址进入到列表
		       	}
		       	//创建数组适配器
		       	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,list);		       	
		       	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	//设置 下来显示方式	       	
		       	spinner.setAdapter(adapter);                                  //将适配器中数据给下拉框对象  
		       	
		       	
		       	Button connectBtn = (Button)findViewById(R.id.connectBtn);    //创建 连接按钮对象
		       	connectBtn.setEnabled(true);                                  //允许连接对象按钮操作  
			}
		}
		);
		
		Button connectBtn = (Button)findViewById(R.id.connectBtn);	       //创建 连接按钮 对象，设置监听器
		connectBtn.setEnabled(false);                                      //不允许 连接对象 按钮操作 
		connectBtn.setOnClickListener(new View.OnClickListener() { 						
			@Override
			public void onClick(View arg0) {
				Spinner spinner = (Spinner)findViewById(R.id.spinner1);    //获取 下拉框对象    
				address = spinner.getSelectedItem().toString();            //从下拉框中选择项目，并获得它的地址
				try {
				  device = mBluetoothAdapter.getRemoteDevice(address);     //根据蓝牙设备的地址 连接 单片机蓝牙 设备
				  clientSocket = device.createRfcommSocketToServiceRecord(uuid);   //根据uuid创建 socket
			 	  clientSocket.connect();			 	                           //手机socket连接远端蓝牙设备			 	  
			 	  Log.i("连接状态", "sucess0"); 
			 	  mmOutStream = clientSocket.getOutputStream();	                   //从socket获得 数据流对象，实现读写操作
			 	  mmInStream  = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));                     
			 	  Toast.makeText(getApplicationContext(), "蓝牙设备连接成功，可以操作了", Toast.LENGTH_SHORT).show();
			 	 Log.i("连接状态", "sucess1"); 
			 	 vib.vibrate(100);  
			 	 Log.i("连接状态", "sucess2"); //手机震动,时长100毫秒
			 	 set_btn_status(true);			 	 			 	   			 	//允许 按钮操作 （开LED灯等按钮）
			 	 Log.i("连接状态", "sucess"); 			 	  
			 	  //定义 多线程对象，并执行线程，用于接收蓝牙数据
			 
			 	  blue_tooth_msg_thread = new bluetoothMsgThread(mmInStream,bluetoothMessageHandle);
  		 	      blue_tooth_msg_thread.start();

				}catch (Exception e) {
					set_btn_status(false);										    //不允许 按钮操作 （开LED灯等按钮）
					Log.i("连接状态", "failed"); 	
					Toast.makeText(getApplicationContext(), "蓝牙设备连接失败!", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});	
		
		Button jdqonBtn = (Button)findViewById(R.id.jdqonBtn);				         //创建 开继电器灯按钮 对象，设置监听器
		jdqonBtn.setOnClickListener(new View.OnClickListener() { 						
			@Override
			public void onClick(View arg0) {
				 byte[] InBuffer = new byte[64];                                     //输入缓存
			 	 byte buffer[] = "c".getBytes();                                     //创建字符数组c,只有一个字符，当然也可以自己定义协议
			 	 try {
			 	   mmOutStream.write(buffer);                                        //数据流发送数组，发送给单片机蓝牙设备 
			 	   //mmInStream.readFully(InBuffer, 0, 8);                             //读取 外部蓝牙设备发送回来的数据			 	  
			 	   //show_result(InBuffer,8);                                          //显示到界面上
			 	   vib.vibrate(100);                                                 //手机震动,时长100毫秒
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Button jdqoffBtn = (Button)findViewById(R.id.jdqoffBtn);				      //创建 关继电器灯按钮 对象，设置监听器
		jdqoffBtn.setOnClickListener(new View.OnClickListener() { 						
			@Override
			public void onClick(View arg0) {
				 byte[] InBuffer = new byte[64];                                      //输入缓存
			 	 byte buffer[] = "d".getBytes();                                      //创建字符数组d,只有一个字符，当然也可以自己定义协议  
			 	 try { 
			 	  mmOutStream.write(buffer);	                                      //数据流发送数组，发送给单片机蓝牙设备
			 	  //mmInStream.readFully(InBuffer, 0, 8);                               //读取 外部蓝牙设备发送回来的数据
			 	  //show_result(InBuffer,8);                                            //显示到界面上
			 	  vib.vibrate(100);                                                   //手机震动,时长100毫秒
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Button recon = (Button)findViewById(R.id.recon_recon);				      //进行人脸识别
		recon.setOnClickListener(new View.OnClickListener() { 						
			@Override
			public void onClick(View arg0) {
				intent.setClass(Unlock.this, CameraSurface.class);   //描述起点和目标   
            	bundle.putInt("something", 3);     //装入数据   
            	intent.putExtras(bundle);                                //把Bundle塞入Intent里面 
            	startActivityForResult(intent,1); //开始切换	                                             //手机震动,时长100毫秒				
				}
			
		});
		Button logout = (Button)findViewById(R.id.recon_logout);				      //进行人脸识别
		logout.setOnClickListener(new View.OnClickListener() { 						
			@Override
			public void onClick(View arg0) {
				intent.setClass(Unlock.this,StartActivity.class);   //描述起点和目标   		                        		       	 
            	startActivity(intent); 		            	 
            	Unlock.this.finish();   
				}
			
		});
	}
	
	Handler bluetoothMessageHandle = new Handler() {            //蓝牙消息 handler 对象
		public void handleMessage(Message msg) {
		  if (msg.what == 0x1234) {                             //如果消息是 0x1234,则是从 线程中 传输过来的数据  			 
			                   
		  }
		}
	};

Runnable faceRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub			
			value[0] = 1;
			Unlock.this.runOnUiThread(new Runnable() {
				
				public void run() {					
					textView.setText("正在识别人脸，请等待。。。");
				}
			});
			DetectFace();			
			index = Identification();
			name = preferences.getString(""+index, null);
			Log.i("name",""+name);
			if(k == 0){
				Unlock.this.runOnUiThread(new Runnable() {
					
					public void run() {						
							textView.setText("没有检测到人脸，请将人眼放在红色方框内");
								
					}
				});
			}
			if(k == 1){
				
						for(int i=1;new File(Environment.getExternalStorageDirectory()  
								+ "/FaceData/"+index+"-"+name+"_"+i+".jpg").exists();i++){
							Log.i("lujing", name);
							value[i] = getSimilarity(Environment.getExternalStorageDirectory()  
								+ "/FaceData/"+index+"-"+name+"_"+i+".jpg");
							if(value[i]<value[i-1])
							{
								confidence=value[i];
							}
							if(confidence<0.40)
							{
								isture=1;
							}
							Log.i(TAG, "相似度"+value[i]);						
					}												
				    Unlock.this.runOnUiThread(new Runnable() {
				
					public void run() {
						if(isture == 1){
							 textView.setText("识别结果： "+ name+" 相似度： "+confidence);									
					
						}
						else{
							textView.setText("识别结果无此人"+confidence);
						}		
					}
				   });
				    if(isture == 1){																	
				    	    
				 	      byte buffer[] = "c".getBytes();                                     //创建字符数组c,只有一个字符，当然也可以自己定义协议
				 	      try {
				 	      mmOutStream.write(buffer);                                      //数据流发送数组，发送给单片机蓝牙设备 				 	   						 	 				 	 
				 	      Thread.sleep(5000);
				 	      isture = 0;  
				 	      byte buffer1[] = "d".getBytes();  
				 	      mmOutStream.write(buffer1);   
					      }catch (Exception e) {
						  e.printStackTrace();
					      }
				 	 }
			 k = 0;
			}
			
			if(k == 2)
			{
				Unlock.this.runOnUiThread(new Runnable() {
					
					public void run() {						
							textView.setText("没有人脸信息，请先添加人脸");								
					}
				});
			}
			 isture = 0;
		  }
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
    
        
        if(resultCode==2){
        	 if(requestCode==1){
        		 int request = data.getIntExtra("result", 0);
        		 Log.i("tag", "result"+request);
        		 new Thread(faceRunnable).start();		                  
               }
    
        }
     }      
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//检测摄像头采集的人脸并保存	
	public void DetectFace() {  
			Log.i(TAG, "开始检测");
			
			Mat image1 = Highgui.imread(FACE);
			Mat mat1= new Mat(); 
			Size size1 = new Size(480, 640);  
			Imgproc.resize(image1, mat1, size1);  
			Highgui.imwrite(FACE, mat1);
			CvMat SrImage = cvLoadImageM(
					Environment.getExternalStorageDirectory()
							+ "/bing/face.jpg", 0);
			cvEqualizeHist(SrImage,SrImage); 
			cvSaveImage(Environment.getExternalStorageDirectory()  
					+ "/bing/face.jpg",SrImage);
			Log.i(TAG, "直方图均衡完毕");
			Mat image = Highgui.imread(FACE);
			MatOfRect faceDetections = new MatOfRect();  
			mjavaClassifier.detectMultiScale(image, faceDetections);  
			Log.i(TAG, "检测完毕");
			for (Rect rect : faceDetections.toArray()) {  
				Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x  
						+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));  
           
				Mat sub = image.submat(rect);  
				Mat mat = new Mat();  
				Size size = new Size(100, 100);  
				Imgproc.resize(sub, mat, size);  
				boolean num= Highgui.imwrite(FACE, mat);
            	Log.i(TAG, "mat:"+mat.toString());
            	Log.i(TAG, "保存:"+num);
            	k = 1;
			}  
      
	}
//  将采集的人脸与训练好的人脸比对，并返回一个索引值	
	 public int Identification() {  
		 int m= 0;
		    FaceRecognizer fr = createLBPHFaceRecognizer();

			Log.i(TAG, "开始读取:");
			File f = new File(Environment.getExternalStorageDirectory()
					+ "/bing/faceset.xml");
			if(f.exists()){
			fr.load(Environment.getExternalStorageDirectory()
						+ "/bing/faceset.xml");
		    }else{
			 k = 2;
			 }		
			 CvArr testImage = cvLoadImage(
					Environment.getExternalStorageDirectory()
							+ "/bing/face.jpg", CV_LOAD_IMAGE_GRAYSCALE);
	         Log.i(TAG, "开始识别:");
	         Log.i(TAG, "开始识别:"+k);	        
	         if(k == 1){
            m=fr.predict(testImage);
            }
	         Log.i(TAG, "m:"+m);	 
	          return m;
	      	  
	    }  
	 public double getSimilarity(String path) {
			// Calculate the L2 relative error between the 2 images.
			CvMat DsImage = cvLoadImageM(path, 0); 
			CvMat SrImage= cvLoadImageM(Environment.getExternalStorageDirectory()
					+ "/bing/face.jpg", 0);
			double errorL2 = cvNorm(SrImage, DsImage, NORM_L2);
			// Scale the value since L2 is summed across all pixels.
			
			double similarity = errorL2 / (double)(SrImage.rows() * SrImage.cols());
			return similarity;
			}
}

