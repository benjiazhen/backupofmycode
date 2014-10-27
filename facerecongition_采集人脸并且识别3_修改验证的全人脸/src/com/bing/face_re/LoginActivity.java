package com.bing.face_re;

import java.io.File;

import android.app.Activity;  
import android.content.Intent;  
import android.content.SharedPreferences;  
import android.content.SharedPreferences.Editor;  

import android.os.Bundle;  
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;  
import android.view.View.OnClickListener;  

import android.widget.Button;  
import android.widget.EditText;
import android.widget.Toast;
import com.bing.face_re.R;
public class LoginActivity extends Activity{
	   
    private Button loginButton; 
    private Button cancelButton;  
    private EditText password;    
    private SharedPreferences preferences;
	private Editor editor;
	private String key;
	protected int k = 0 ; 
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
        setContentView(R.layout.adminlog); 
        password = (EditText) findViewById(R.id.key);       
        loginButton = (Button)findViewById(R.id.admin_log);
     
        loginButton.setOnClickListener(new OnClickListener() {         
            @Override           	   
            public void onClick(View v) {  
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
          	   
            	 if(password.getText().toString().equals(key))  
                 {  
                     Toast.makeText(LoginActivity.this,"登录成功", Toast.LENGTH_SHORT).show();  
                    
                     //跳转界面  
                     Intent intent = new Intent(LoginActivity.this,MainActivity.class);  
                     LoginActivity.this.startActivity(intent);  
                     LoginActivity.this.finish();        
                       
                 }else{  
                       
                     Toast.makeText(LoginActivity.this,"用户名或密码错误，请重新登录", Toast.LENGTH_LONG).show();  
                 }                                             	   
            } 
         });  
        cancelButton = (Button)findViewById(R.id.admin_cancel);        
        cancelButton.setOnClickListener(new OnClickListener() {         
            @Override           	   
            public void onClick(View v) {  
            	
                     Intent intent = new Intent(LoginActivity.this,StartActivity.class);  
                     LoginActivity.this.startActivity(intent);  
                     LoginActivity.this.finish();                                                               
             }  
         });  
            }  
        
  
    }  
	   
	


