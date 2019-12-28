/**
 * 
 */
package com.example.h3;



import com.example.nn.R;


import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import util.ConfigCt;

/**
 * @author byc
 *
 */
public class FloatingWindowPic {
	public static String TAG = "byc001";//���Ա�ʶ��
	private Context context;
	//-------------------------------------------------------------------------
	//���帡�����ڲ���
	private LinearLayout mFloatLayout;
	private WindowManager.LayoutParams wmParams;
    //���������������ò��ֲ����Ķ���
	private WindowManager mWindowManager;
	
	public boolean bShow=false;//�Ƿ���ʾ
	//��������
	private int i=0;
	//��ʱ����
	private int j=0;
	//��ʾʱ�䣺
	public int c=10;
	//��ɫ��Դ���ϣ�
	int[] resids=new int[]{
			R.drawable.p0,R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,
			R.drawable.p5,R.drawable.p6,R.drawable.p7,R.drawable.p8,R.drawable.p9
	};
	//��ɫͼƬ��Դ���ϣ�
	int[] residsRed=new int[]{
			R.drawable.m0,R.drawable.m1,R.drawable.m2,R.drawable.m3,R.drawable.m4,
			R.drawable.m5,R.drawable.m6,R.drawable.m7,R.drawable.m8,R.drawable.m9
	};
	//��ʾ��Դ���
	public int mShowPicType=0;//Ĭ����ʾ��ɫͼƬ����
	public static final int SHOW_PIC_GREEN=0;//��ɫͼƬ����
	public static final int SHOW_PIC_RED=1;//��ɫͼƬ����
	//������Ϣ������
	public String mSendMsg="";
	//bTreadRun:��ֹ�̱߳�����
	private boolean bTreadRun=true;
	private static FloatingWindowPic current;
	
	public FloatingWindowPic(Context context,int resource) {
		this.context = context;
		TAG=ConfigCt.TAG;

		createFloatViewPic(resource);
	}
    public static synchronized FloatingWindowPic getFloatingWindowPic(Context context,int resource) {
        if(current == null) {
            current = new FloatingWindowPic(context,resource);
        }
        return current;
        
    }
    public void ShowFloatingWindow(){
    	if(!bShow){
    		
    		try{
       		 mWindowManager.addView(mFloatLayout, wmParams);
       		bShow=true;
       		}catch (Exception e) {
       			e.printStackTrace();
       		}
    	}
    }
    
    public void RemoveFloatingWindowPic(){
		if(mFloatLayout != null)
		{
			if(bShow)mWindowManager.removeView(mFloatLayout);
			bShow=false;
		}
    }
    public void SetFloatViewPara(int x,int y,int w,int h){
        // ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
    	if(wmParams==null)return;
        wmParams.x = x;
        wmParams.y = y;
        // �����������ڳ�������
        wmParams.width = w;
        wmParams.height =h;
        //�����������ڳ�������  
        //wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }
    private void createFloatViewPic(int resource)
 	{
 		wmParams = new WindowManager.LayoutParams();
 		//��ȡWindowManagerImpl.CompatModeWrapper
 		mWindowManager = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
 		//����window type
 		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
 			wmParams.type = LayoutParams.TYPE_TOAST; 
 		else
 			wmParams.type = LayoutParams.TYPE_PHONE; 
 		//����ͼƬ��ʽ��Ч��Ϊ����͸��
         wmParams.format = PixelFormat.RGBA_8888; 
         //���ø������ڲ��ɾ۽���ʵ�ֲ���������������������ɼ����ڵĲ�����
         wmParams.flags = 
           LayoutParams.FLAG_NOT_TOUCH_MODAL |
           LayoutParams.FLAG_NOT_FOCUSABLE	|
           LayoutParams.FLAG_NOT_TOUCHABLE
           ;
         
         //������������ʾ��ͣ��λ��Ϊ����ö�
         wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
         //wmParams.gravity = Gravity.CENTER | Gravity.CENTER; 
         
         // ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
         wmParams.x = 0;
         wmParams.y = 0;

         // �����������ڳ�������
         //wmParams.width = w;
         //wmParams.height =h;
         
         //�����������ڳ�������  
         wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
         wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
         
         LayoutInflater inflater = LayoutInflater.from(context);
         //��ȡ����������ͼ���ڲ���
         //mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_bigpic, null);
         mFloatLayout = (LinearLayout) inflater.inflate(resource, null);
         //����mFloatLayout
         //mWindowManager.addView(mFloatLayout, wmParams);
         
         //Log.i(TAG, "mFloatLayout-->left" + mFloatLayout.getLeft());
         //Log.i(TAG, "mFloatLayout-->right" + mFloatLayout.getRight());
         //Log.i(TAG, "mFloatLayout-->top" + mFloatLayout.getTop());
         //Log.i(TAG, "mFloatLayout-->bottom" + mFloatLayout.getBottom());      
         
         
         mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
 				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
 				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        
 	}

    //�л�ͼƬ��
    private void switchPic(int i){

    	ImageView iv=(ImageView)mFloatLayout.findViewById(R.id.ImageView01);
    	switch(mShowPicType){
    	case SHOW_PIC_GREEN:
    		iv.setImageResource(resids[i]);
    		break;
    	case SHOW_PIC_RED:
    		iv.setImageResource(residsRed[i]);
    		break;
    	}
    }


    class PicThread extends Thread { 

    	 public PicThread() { 

    	 }
    	 @Override  
         public void run() {  

             
             while(bTreadRun){
                 //������Ϣ  
                 Message msg = new Message();  
                 msg.what = 0x21;
                 Bundle bundle = new Bundle();
                 bundle.clear(); 
            	 bundle.putString("msg", "01");  
            	 msg.setData(bundle);  //
            	 //������Ϣ �޸�UI�߳��е����  
            	 HandlerPic.sendMessage(msg); 
            	 try{
            	 Thread.sleep(100);
                 } catch (InterruptedException e) {
            		 e.printStackTrace();
            	 }

            	 //Log.i(TAG, i);
             }

             
    	 }
    }//class SockThread extends Thread { 
    public void StartSwitchPics(){
    	i=0;
    	j=0;
    	bTreadRun=true;
    	new PicThread().start();
    	return ;
    }
    public void StopSwitchPics(){
    	i=0;
    	j=0;
    	bTreadRun=false;
    	return ;
    }
    //������Ϣ��
    public Handler HandlerPic = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            if (msg.what == 0x21) {  
            	//Log.i(TAG, "handleMessage----------->"+i);
            	switchPic(i);
            	i=i+1;
            	if(i>9)i=0;
            	//׼���رմ��ڣ�
           	 	j=j+1;
           	 	if(j>=c){
           	 		StopSwitchPics();
           	 		RemoveFloatingWindowPic();
           	        //Intent intent = new Intent(Config.ACTION_PUT_PWD_DELAY);
           	        Intent intent = new Intent(mSendMsg);
           	        context.sendBroadcast(intent);
           	 	}//if(j>=c){
            }  
        }  
  
    };  
	
}