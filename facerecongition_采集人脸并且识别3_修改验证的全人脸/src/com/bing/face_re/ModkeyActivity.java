package com.bing.face_re;

import java.io.File;

import android.app.Activity;  
import android.content.Intent;  
import android.content.SharedPreferences;  
import android.content.SharedPreferences.Editor;  

import android.os.Bundle;  
import android.util.Log;
import android.view.View;  
import android.view.View.OnClickListener;  

import android.widget.Button;  
import android.widget.EditText;
import android.widget.Toast;
import com.bing.face_re.R;
public class ModkeyActivity extends Activity {
	private Button OkButton; 
	private Button NoButton; 
    private EditText oldpassword;
    private EditText newpassword;
    private EditText con_newpassword;      
    private SharedPreferences preferences;
	private Editor editor;
	private String key; 
	private String oldkey;
	private String newkey;
	private String connewkey;
	protected int k = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.mod_keyvalue); 
        oldpassword = (EditText) findViewById(R.id.old_key);
        newpassword = (EditText) findViewById(R.id.new_key);
        con_newpassword = (EditText) findViewById(R.id.connew_key);
        OkButton = (Button)findViewById(R.id.mod_keyok);
        NoButton = (Button)findViewById(R.id.mod_keyno);
        OkButton.setOnClickListener(listener);
        NoButton.setOnClickListener(listener);      
	    
	}
	   
	private OnClickListener listener= new OnClickListener()
    	{

    		
			@Override
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
				File f = new File("/data/data/com.bing.face_re/shared_prefs/keydata.xml");
    		    if(f.exists()){
    		    	k = 1;
    		    }else{
    		    	k = 2;
    			}	
				preferences = getSharedPreferences("keydata",MODE_WORLD_READABLE);
				editor = preferences.edit();
				if(k == 1){				
					key = preferences.getString("key", null);
				}else{
					key = "admin";
				}	
    			oldkey = oldpassword.getText().toString();
    			newkey = newpassword.getText().toString();
    			connewkey = con_newpassword.getText().toString();
    			 int id = v.getId();  
    		        switch(id){  
    		            
    		            case R.id.mod_keyok: 	 
    		            	 if(oldkey.equals(key)&&newkey.equals(connewkey))
    		            	 {
    		            		 editor.putString("key", newkey);
    		         			 editor.commit();
    		         			 Log.i("newkey",newkey);
    		            		 Toast.makeText(ModkeyActivity.this,"密码修改成功", Toast.LENGTH_SHORT).show();
    		            		 Intent intent = new Intent(ModkeyActivity.this,MainActivity.class);  
    		            		 ModkeyActivity.this.startActivity(intent);  
    		            		 ModkeyActivity.this.finish();
    		            	 } 
    		            	 else{
    		            		 if(!oldkey.equals(key))
    		            		 {
    		            			 Toast.makeText(ModkeyActivity.this,"密码错误", Toast.LENGTH_SHORT).show(); 
    		            		 }else
    		            		 {
    		            			 Toast.makeText(ModkeyActivity.this,"新密码输入不一致", Toast.LENGTH_LONG).show(); 
    		            		 }
    		            		 
    		            	 }                
    		                 break;
    		            case R.id.mod_keyno:  
    		            	   Intent intent = new Intent(ModkeyActivity.this,MainActivity.class);  
    		            	   ModkeyActivity.this.startActivity(intent);  
    		            	   ModkeyActivity.this.finish(); 
    		    			break;
    		         
    		            default:
    		        		break;
    		      }  
    		        	
    		}
    		
    	};
        
  
    }  
