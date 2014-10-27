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
import static com.googlecode.javacv.cpp.opencv_core.cvNorm;
import static com.googlecode.javacv.cpp.opencv_core.NORM_L2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class MainActivity extends Activity  {

	private static final String TAG="Face_Recognition";
	private CascadeClassifier mjavaClassifier;
	private Button faceButton;
	private Button addButton;
	private Button cameraButton;
	private Button shutButton;
	private Button personButton;
	private Button delButton;	

	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	String name;
	private int k = 0;
	double confidence = 1;
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
		setContentView(R.layout.activity_main);
		preferences = getSharedPreferences("persondata",MODE_WORLD_READABLE);
		editor = preferences.edit();
		faceButton=(Button)findViewById(R.id.main_recon);
		personButton=(Button)findViewById(R.id.main_person);
		addButton=(Button)findViewById(R.id.main_modkey);
		cameraButton = (Button)findViewById(R.id.main_addperson);
		shutButton = (Button)findViewById(R.id.main_exit);
		delButton=(Button)findViewById(R.id.main_del);
		faceButton.setOnClickListener(listener);
		personButton.setOnClickListener(listener);
		addButton.setOnClickListener(listener);
		cameraButton.setOnClickListener(listener);
		shutButton.setOnClickListener(listener);
		delButton.setOnClickListener(listener);
		textView = (TextView)this.findViewById(R.id.camera_textView);
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;//消费掉后退键 
        }
        return super.onKeyDown(keyCode, event);
    }        
	private OnClickListener listener= new OnClickListener()
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 int id = v.getId();  
		        switch(id){  
		            
		            case R.id.main_modkey: 	 
		            	  intent.setClass(MainActivity.this,ModkeyActivity.class);  
		            	  startActivity(intent);
		            	  MainActivity.this.finish();
		                  break;
		            case R.id.main_addperson:  
		            	intent.setClass(MainActivity.this, CameraSurface.class);   //描述起点和目标   		                        
		            	bundle.putInt("something", 2);     //装入数据   
		            	intent.putExtras(bundle);                                //把Bundle塞入Intent里面 
		            	startActivityForResult(intent,2);  //开始切换		     
		    			break;
		            case R.id.main_exit:
		            	intent.setClass(MainActivity.this,StartActivity.class);   //描述起点和目标   		                        		       	 
		            	startActivity(intent); 		            	 
		            	MainActivity.this.finish();      
		    			break;		    
		            case R.id.main_recon:
		            	 
		            	intent.setClass(MainActivity.this, CameraSurface.class);   //描述起点和目标   
		            	bundle.putInt("something", 1);     //装入数据   
		            	intent.putExtras(bundle);                                //把Bundle塞入Intent里面 
		            	startActivityForResult(intent,1); //开始切换	
		            	break;
		            case R.id.main_person:
		            	 
		            	intent.setClass(MainActivity.this,Person.class);  
		            	startActivity(intent); 		           
		            	break;	
		            case R.id.main_del:		            	 
		            	new Thread(delallperson).start();	
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
        		 MainActivity.this.runOnUiThread(new Runnable() {
		            	public void run() {
	    					//show the image
	    					
	    					textView.setText("正在识别，请等待...... ");
	    				}
		            	});
        		 new Thread(faceRunnable).start();		                 
               }
        	 if(requestCode==2){
        		 int request = data.getIntExtra("result", 0);
        		 Log.i("tag", "result2"+request);
        		 MainActivity.this.runOnUiThread(new Runnable() {
		            	public void run() {
	    					//show the image
	    					
	    					textView.setText("正在添加人脸，请等待...... ");
	    				}
		            	});
        		 new Thread(addface).start();		                 
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
				if (mjavaClassifier!=null) {
//				Toast.makeText(MainActivity.this, "加载成功"+mjavaClassifier.toString(), Toast.LENGTH_LONG).show();
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
			if(k == 0)
			{
				MainActivity.this.runOnUiThread(new Runnable() {
				
					public void run() {
					
						textView.setText("没有检测到人脸，请将人眼放在红色方框内");
					}
				});
        	
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
	        Log.i(TAG, "m:"+k);	
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
	
//向训练样本中添加人脸	
	Runnable addface=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			AddPerson();
			MainActivity.this.runOnUiThread(new Runnable() {
				
				public void run() {
					//show the image					
					textView.setText("人脸添加成功 ");
				}
			});
		}
	};
	
//人脸识别	
	Runnable faceRunnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub			
			value[0] = 1;
			MainActivity.this.runOnUiThread(new Runnable() {
				
				public void run() {					
					textView.setText("正在识别人脸，请等待。。。");
				}
			});
			DetectFace();			
			index = Identification();
			name = preferences.getString(""+index, null);
			Log.i("name",""+name);
			if(k == 0){
				MainActivity.this.runOnUiThread(new Runnable() {
					
					public void run() {						
							textView.setText("没有检测到人脸，请将人眼放在红色方框内");
								
					}
				});
			}
			if(k == 1){
				
						for(int i=1;new File(Environment.getExternalStorageDirectory()  
								+ "/FaceData/"+index+"-"+name+"_"+i+".jpg").exists();i++){
							Log.i("lujing", name);
//							value[i] = CmpPic(Environment.getExternalStorageDirectory()  
//								+ "/FaceData/"+index+"-"+name+"_"+i+".jpg")*100;
							value[i] = getSimilarity(Environment.getExternalStorageDirectory()  
									+ "/FaceData/"+index+"-"+name+"_"+i+".jpg");
							if(value[i]<value[i-1])
							{
								confidence=value[i];
							}
							if(confidence<0.4)
							{
								isture=1;
							}
							Log.i(TAG, "相似度"+value[i]);						
					}
				MainActivity.this.runOnUiThread(new Runnable() {
				
					public void run() {
						if(isture == 1){
							textView.setText("识别结果： "+ name+" 相似度： "+confidence);
							isture = 0;
						}
						else{
							textView.setText("识别无此人"+confidence);
						}		
					}
				});
			k = 0;
			}
			
			if(k == 2)
			{
					MainActivity.this.runOnUiThread(new Runnable() {
					
					public void run() {						
							textView.setText("没有人脸信息，请先添加人脸");								
					}
				});
			}
		  }		
	};

Runnable delallperson=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			 DeleteFiles(Environment.getExternalStorageDirectory()  
			            + "/FaceNew");	
			 DeleteFiles(Environment.getExternalStorageDirectory()  
			            + "/bing");	
			 DeleteFiles(Environment.getExternalStorageDirectory()  
			            + "/FaceData");	
			editor.clear();
			editor.commit();
			MainActivity.this.runOnUiThread(new Runnable() {
				
				public void run() {
					//show the image 
					
					textView.setText("删除成功 ");
				}
			});
		}
	};
//读取摄像头所采集的信息	
	 public void LoadFaceDataNew() { 
		 Log.i("111", "msgnew");
		 File file = new File(Environment.getExternalStorageDirectory()  
		            + "/FaceNew");
		  //判断文件夹否存,存则创建文件夹
		  if (!file.exists()) {
		   file.mkdir();
		  }
		 File[] files = new File(Environment.getExternalStorageDirectory()  
		            + "/FaceNew").listFiles();
			File f;
			int id;
			int idtwo;
			String name;
			faceList_new.clear();
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
					faceList_new.add(new FacePojo(id, name, Environment
						.getExternalStorageDirectory()
						+ "/FaceNew/"
							+ f.getName(), idtwo));
				}
			}
	    }  
	/**
	 * 加载本地图片
	 */
	 public void LoadFaceData() { 
		 
		   File file = new File(Environment.getExternalStorageDirectory()  
		            + "/FaceData");
		   //判断文件夹否存,存则创建文件夹
		   if (!file.exists()) {
		    file.mkdir();
		   }
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
	
	 public void AddPerson()
	 {		
		 LoadFaceDataNew();
		 File file = new File(Environment.getExternalStorageDirectory()  
		            + "/FaceData");
		   //判断文件夹否存,存则创建文件夹
		   if (!file.exists()) {
		    file.mkdir();
		   }
		 int picnum=faceList_new.size();
			
		/************遍历加载 ***********/
		  for (int i = 0; i < picnum; i++) {
			  	Log.i(TAG, "开始检测");
				Mat image1 = Highgui.imread(faceList_new.get(i).getPath());
				Mat mat1= new Mat(); 
				Size size1 = new Size(480, 640);  
		        Imgproc.resize(image1, mat1, size1);  
		        Highgui.imwrite(faceList_new.get(i).getPath(), mat1);
				CvMat SrImage = cvLoadImageM(faceList_new.get(i).getPath(), 0);
				cvEqualizeHist(SrImage,SrImage); 
				cvSaveImage(faceList_new.get(i).getPath(),SrImage);
				Log.i(TAG, "直方图均衡完毕");
				
				Mat image = Highgui.imread(faceList_new.get(i).getPath());  
			    MatOfRect faceDetections = new MatOfRect();  
			    mjavaClassifier.detectMultiScale(image, faceDetections);  
			    for (Rect rect : faceDetections.toArray()) {  
			            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x  
			                    + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));  

			            Mat sub = image.submat(rect);  
			            Mat mat = new Mat();  
			            Size size = new Size(100, 100);  
			            Imgproc.resize(sub, mat, size);  
			            boolean life= Highgui.imwrite(Environment.getExternalStorageDirectory()
			            		+"/FaceNew/"+faceList_new.get(i).getId()+"-"+faceList_new.get(i).getName()+"_"
			            		+faceList_new.get(i).getIdtwo()
			            		+".jpg", mat);
			            boolean life1= Highgui.imwrite(Environment.getExternalStorageDirectory()
			            		+"/FaceData/"+faceList_new.get(i).getId()+"-"+faceList_new.get(i).getName()+"_"
			            		+faceList_new.get(i).getIdtwo()
			            		+".jpg", mat);
			            Log.i(TAG, "mat:"+mat.toString());
			            Log.i(TAG, "保存:"+life);
			      } 
		  	}
		  	MatVector images = new MatVector(faceList_new.size());

		    int[] labels = new int[faceList_new.size()];
		   
		    for (int i = 0; i < faceList_new.size(); i++) {
				IplImage img = cvLoadImage(faceList_new.get(i).getPath(),
						CV_LOAD_IMAGE_GRAYSCALE);
				images.put(i, img);;
				labels[i] = faceList_new.get(i).getId();
			 	Log.i(TAG,"数量"+faceList_new.get(i).getIdtwo());
			}

			FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
			File f = new File(Environment.getExternalStorageDirectory()
					+ "/bing/faceset.xml");
			if(f.exists()){
			faceRecognizer.load(Environment.getExternalStorageDirectory()
						+ "/bing/faceset.xml");
		    faceRecognizer.update(images, labels);}
			else
			{
				faceRecognizer.train(images, labels);
			}
		    Log.i(TAG,"开始保存");
		    faceRecognizer.save(Environment.getExternalStorageDirectory()
						+ "/bing/faceset.xml");
		    DeleteFiles(Environment.getExternalStorageDirectory()  
		            + "/FaceNew");		 
	 		Log.i(TAG, "保存成功");  
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
	 public void DeleteFiles(String path)
	 {
		 	File[] files = new File(path).listFiles();
			File f;
			for (int i = 0; i < files.length; i++) {
				f = files[i];
				if (!f.canRead()) {
					return;
				}
				if (f.isFile()) {
					f.delete();
				}
			} 
	 }
	
}