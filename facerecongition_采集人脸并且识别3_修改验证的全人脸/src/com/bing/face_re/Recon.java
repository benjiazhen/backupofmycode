package com.bing.face_re;

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

public class Recon extends Activity  {

	private static final String TAG="Face_Recognition";
	private CascadeClassifier mjavaClassifier;
	private Button faceButton;
	private Button addButton;
	private Button cameraButton;
	private Button shutButton;	

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	String name;
	private int k = 0;
	double confidence = 0;
	private List<FacePojo> faceList = new ArrayList<FacePojo>();  
	private List<FacePojo> faceList_new = new ArrayList<FacePojo>();  
	private int index;
	private int isture = 0;
	private double[] value = new double[100];
	private TextView textView = null;
	Intent intent = new Intent(); 
	Bundle bundle = new Bundle(); 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recon);
		preferences = getSharedPreferences("persondata",MODE_WORLD_READABLE);
		editor = preferences.edit();
		
		
		
		cameraButton = (Button)findViewById(R.id.recon_recon);
		textView = (TextView)this.findViewById(R.id.camera_textView);
		cameraButton.setOnClickListener(listener);
	}

	private OnClickListener listener= new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 int id = v.getId();  
		        switch(id){  
		            
		           
		            case R.id.recon_recon:
		            	 
		            	intent.setClass(Recon.this, CameraSurface.class);   //描述起点和目标   
		            	bundle.putInt("something", 3);     //装入数据   
		            	intent.putExtras(bundle);                                //把Bundle塞入Intent里面 
		            	startActivityForResult(intent,1); //开始切换	
		            	break;
		            default:
		        		break;
		      }  
		        	
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
        		 Recon.this.runOnUiThread(new Runnable() {
		            	public void run() {
	    					//show the image
	    					
	    					textView.setText("正在识别，请等待...... ");
	    				}
		            	});
        		 new Thread(faceRunnable).start();		                 
               }
    
        }
     }      
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
		LoadFaceData();		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {

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
	
	private String FACE=Environment.getExternalStorageDirectory()  
            + "/bing/face.jpg";

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
//   将采集的人脸与训练好的人脸比对，并返回一个索引值	
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
//	 Identification返回的索引值所指向的图片与摄像头采集的图片对比，得出相似度
	 public double CmpPic(String path) {  
        int l_bins = 20;  
        int hist_size[] = { l_bins };  
  
        float v_ranges[] = { 0, 100 };  
        float ranges[][] = { v_ranges };  
  
        IplImage Image1 = cvLoadImage(Environment.getExternalStorageDirectory()  
                + "/bing/face.jpg", CV_LOAD_IMAGE_GRAYSCALE);  
        IplImage Image2 = cvLoadImage(path, CV_LOAD_IMAGE_GRAYSCALE);  
        Log.i(TAG, "路径:"+path);
        IplImage imageArr1[] = { Image1 };  
        IplImage imageArr2[] = { Image2 };  
  
        CvHistogram Histogram1 = CvHistogram.create(1, hist_size,  
                CV_HIST_ARRAY, ranges, 1);  
        CvHistogram Histogram2 = CvHistogram.create(1, hist_size,  
                CV_HIST_ARRAY, ranges, 1);  
  
        cvCalcHist(imageArr1, Histogram1, 0, null);  
        cvCalcHist(imageArr2, Histogram2, 0, null);  
  
        cvNormalizeHist(Histogram1, 1.0);  
        cvNormalizeHist(Histogram2, 1.0);  
        double ccc0=cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);  
        double ccc1=cvCompareHist(Histogram1, Histogram2, 1);  
        double ccc2=cvCompareHist(Histogram1, Histogram2, 2);  
        double ccc3=cvCompareHist(Histogram1, Histogram2, 3);  
        Log.i(TAG, "检测结果:"+ccc0+"::"
        		+ccc1+"::"+ccc2+"::"+ccc3);
        double bing=((1-ccc1)+(1-ccc3)+ccc0+ccc2)/4;
        Log.i(TAG, "最终结果:"+bing);
        return cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);  
    }  
	

//人脸识别	
	Runnable faceRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub			
			value[0] = 0;
			Recon.this.runOnUiThread(new Runnable() {
				
				public void run() {					
					textView.setText("正在识别人脸，请等待。。。");
				}
			});
			DetectFace();			
			index = Identification();
			name = preferences.getString(""+index, null);
			Log.i("name",""+name);
			if(k == 0){
				Recon.this.runOnUiThread(new Runnable() {
					
					public void run() {						
							textView.setText("没有检测到人脸，请将人眼放在红色方框内");
								
					}
				});
			}
			if(k == 1){
				
						for(int i=1;new File(Environment.getExternalStorageDirectory()  
								+ "/FaceData/"+index+"-"+name+"_"+i+".jpg").exists();i++){
							Log.i("lujing", name);
							value[i] = CmpPic(Environment.getExternalStorageDirectory()  
								+ "/FaceData/"+index+"-"+name+"_"+i+".jpg")*100;
							if(value[i]>value[i-1])
							{
								confidence=value[i];
							}
							if(confidence>70)
							{
								isture=1;
							}
							Log.i(TAG, "相似度"+value[i]);						
					}
						Recon.this.runOnUiThread(new Runnable() {
				
					public void run() {
						if(isture == 1){
							textView.setText("识别结果： "+ name+" 相似度： "+confidence);
							isture = 0;
						}
						else{
							textView.setText("识别无此人");
						}		
					}
				});
			k = 0;
			}
			
			if(k == 2)
			{
				Recon.this.runOnUiThread(new Runnable() {
					
					public void run() {						
							textView.setText("没有人脸信息，请先添加人脸");								
					}
				});
			}
		  }		
	};
	/**
	 * 加载本地图片
	 */
	 public void LoadFaceData() { 
		    File[] files = new File(Environment.getExternalStorageDirectory()  
		            + "/FaceData").listFiles();
			File f;
			int id;
			int idtwo;
			String name;
			faceList.clear();
			for (int i = 0; i < files.length; i++) {
				f = files[i];
				if (!f.canRead()) {
					return;
				}
				if (f.isFile()) {
					id = Integer.parseInt(f.getName().split("\\-")[0]);
					idtwo = Integer.parseInt(f.getName().substring(f.getName().lastIndexOf("_")+1,
				    		  f.getName().indexOf(".")));
					name = f.getName().substring(f.getName().indexOf("-")+1,
				    		  f.getName().lastIndexOf("_"));
					faceList.add(new FacePojo(id, name, Environment
						.getExternalStorageDirectory()
						+ "/FaceData/"
							+ f.getName() ,idtwo));
				}
				
			}
	    }  
	  
}