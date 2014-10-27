package com.bing.face_re;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bing.face_re.R;

public class StartActivity extends Activity{
	private Button ManageButton; 
	private Button ReconButton;
	private Button LogoutButton;
	private Intent intent = new Intent(); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.start); 
      
        ManageButton = (Button)findViewById(R.id.mode_Man);
        ReconButton = (Button)findViewById(R.id.mode_recon);
        LogoutButton = (Button)findViewById(R.id.start_logout);
        ManageButton.setOnClickListener(listener);
        ReconButton.setOnClickListener(listener);
        LogoutButton.setOnClickListener(listener);
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
		            
		            case R.id.mode_Man: 	 
		            	intent.setClass(StartActivity.this,LoginActivity.class);   //描述起点和目标   		                        		        		 
		            	StartActivity.this.startActivity(intent); 		            	 
		            	StartActivity.this.finish();        	   
		                break;
		                 
		            case R.id.mode_recon:  
		            	intent.setClass(StartActivity.this,Unlock.class);   //描述起点和目标   		            	                                
		            	StartActivity.this.startActivity(intent); //开始切换	 
		            	StartActivity.this.finish();
		    			break;
		            case R.id.start_logout:  
		            	StartActivity.this.finish();      
		    			break;
		            default:
		        		break;
		      }  
		        	
		}
		
	};
    
}