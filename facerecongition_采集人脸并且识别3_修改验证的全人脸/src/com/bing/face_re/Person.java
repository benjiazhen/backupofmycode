package com.bing.face_re;

import static com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bing.face_re.R;
import com.bing.face_re.FacePojo;
import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;

public class Person extends Activity {
	
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	private Map<String, ?> map;
	List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
	private Object index;
	private Object name;
	private List<FacePojo> faceList = new ArrayList<FacePojo>();  
    // private List<String> data = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person);
        preferences = getSharedPreferences("persondata",MODE_WORLD_READABLE);
		editor = preferences.edit();
		map = preferences.getAll();		
		Iterator it = map.keySet().iterator();
		listItems.clear();
	       while (it.hasNext()) {  
	    	   Map<String, Object> listItem = new HashMap<String, Object>();
	           String key = it.next().toString();  
	           listItem.put("index",key);  
	           listItem.put("name",map.get(key));
	           listItems.add(listItem);
	       }  	       	         	      
        SimpleAdapter adapter = new SimpleAdapter(this,listItems,R.layout.simple_item,
                new String[]{"index","name"},
                new int[]{R.id.item_index,R.id.item_name});
        ListView list = (ListView) findViewById(R.id.person_list);
        list.setAdapter(adapter);
        list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
        	public boolean onItemLongClick(AdapterView<?> parent, View view,
        	int position, long id) {
				index = listItems.get(position).get("index");
				name = listItems.get(position).get("name");
        		Log.i("index", ""+index);
        		DeletePerson();
        		return false;
        	}
        	});                
    }
    private void DeletePerson() {
    	AlertDialog.Builder builder = new Builder(Person.this);

    	builder.setMessage("确定删除人脸");
    	builder.setTitle("提示");

    	builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

    	@Override
    	public void onClick(DialogInterface dialog, int which) {
    	//File构造参数list读取文件路径
    		editor.remove(""+index);
        	editor.commit();
    	
    	//通知adapter 更新
        	map = preferences.getAll();
      	    Iterator it = map.keySet().iterator();
    		listItems.clear();
    	    while (it.hasNext()) {  
    	    	   Map<String, Object> listItem = new HashMap<String, Object>();
    	           String key = it.next().toString();  
    	           listItem.put("index",key);  
    	           listItem.put("name",map.get(key));
    	           listItems.add(listItem);
    	       }  
 
            SimpleAdapter adapter1 = new SimpleAdapter(Person.this,listItems,R.layout.simple_item,
                    new String[]{"index","name"},
                    new int[]{R.id.item_index,R.id.item_name});
            ListView list = (ListView) findViewById(R.id.person_list);
            list.setAdapter(adapter1);
            DeleteFiles(Environment.getExternalStorageDirectory()  
					+ "/FaceNew");
            for(int i=1;new File(Environment.getExternalStorageDirectory()  
					+ "/FaceData/"+index+"-"+name+"_"+i+".jpg").exists();i++){
				Log.i("lujing", (String) name);
				File f = new File(Environment.getExternalStorageDirectory()  
						+ "/FaceData/"+index+"-"+name+"_"+i+".jpg");
				f.delete();				
										
		     }
            AddPerson();
    	}
    	});

    	builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

    	@Override
    	public void onClick(DialogInterface dialog, int which) {

    	}
    	});
    	builder.create().show();
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
	 public void AddPerson()
	 {		
		 LoadFaceData();
		 int picnum=faceList.size();
			
		 if(picnum == 0)
		 {	File f = new File(Environment.getExternalStorageDirectory()
					+ "/bing/faceset.xml");
		    f.delete();
		 }else{
		  	MatVector images = new MatVector(faceList.size());

		    int[] labels = new int[faceList.size()];
		   
		    for (int i = 0; i < faceList.size(); i++) {
				IplImage img = cvLoadImage(faceList.get(i).getPath(),
						CV_LOAD_IMAGE_GRAYSCALE);
				images.put(i, img);;
				labels[i] = faceList.get(i).getId();
			 	Log.i("TAG","数量"+faceList.get(i).getIdtwo());
			}

			FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
		
				faceRecognizer.train(images, labels);
			
		    Log.i("TAG","开始保存");
		    faceRecognizer.save(Environment.getExternalStorageDirectory()
						+ "/bing/faceset.xml");		 		   
	 		Log.i("TAG", "保存成功");  
	
		 }   	
	 
	 }
    }

    
