/**
 * 
 */
package com.example.h3.job;

import java.util.Timer;
import java.util.TimerTask;

import util.BackgroundMusic;
import com.example.h3.Config;
import com.example.h3.MainActivity;
import accessibility.QiangHongBaoService;
import ad.VersionParam;
import notification.IStatusBarNotification;
import notification.NotifyHelper;
import notification.QHBNotificationService;
import accessibility.AccessibilityHelper;
import accessibility.BaseAccessibilityJob;
import floatwindow.FloatingWindowPic;
import util.SpeechUtil;
import com.example.nn.R;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * @author byc
 *
 */
public class WechatAccessibilityJob extends BaseAccessibilityJob  {
	
	private static WechatAccessibilityJob current;
	//-------------------------------拆包延时---------------------------------------------
	private int mWechatOpenDelayTime=0;//拆包延时
	private FloatingWindowPic fwp;
	private BackgroundMusic mBackgroundMusic;
	private SpeechUtil speaker ;
	public int mLuckyMoneyType=0;//红包类别：已拆过，福利包，有雷包；
	//工作类：处理红包来了界面和红包详细信息界面：
	private LuckyMoneyReceiveJob mReceiveJob;
	private LuckyMoneyDetailJob mDetailJob;
	private LuckyMoney mLuckyMoney;
	
	public WechatAccessibilityJob(){
		super(new String[]{Config.WECHAT_PACKAGENAME});
	}

    @Override
    public void onCreateJob(QiangHongBaoService service) {
        super.onCreateJob(service);
        EventStart();
        mReceiveJob=LuckyMoneyReceiveJob.getLuckyMoneyReceiveJob(context);
        mDetailJob=LuckyMoneyDetailJob.getLuckyMoneyDetailJob(context);
        //mLauncherJob=LuckyMoneyLauncherJob.getLuckyMoneyLauncherJob(context);
        mLuckyMoney=LuckyMoney.getLuckyMoney(context);
        speaker=SpeechUtil.getSpeechUtil(context);
        mBackgroundMusic=BackgroundMusic.getInstance(context);
        
        
		fwp=FloatingWindowPic.getFloatingWindowPic(context,R.layout.float_click_delay_show);
		int w=Config.screenWidth-200;
		int h=Config.screenHeight-200;
		fwp.SetFloatViewPara(100, 200,w,h);
		//接收广播消息
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ACTION_CLICK_LUCKY_MONEY);
        context.registerReceiver(ClickLuckyMoneyReceiver, filter);
 
    
    }

    @Override
    public void onStopJob() {
    	super.onStopJob();
    	//mVolumeChange.onDestroy();
    }
    public static synchronized WechatAccessibilityJob getJob() {
        if(current == null) {
            current = new WechatAccessibilityJob();
        }
        return current;
    }
	/*
	 * (刷新处理流程)
	 * @see accessbility.AccessbilityJob#onWorking()
	 */
	@Override
	public void onWorking(){
		//Log.i(TAG2, "onWorking");
		//installApp.onWorking();
	}
    //----------------------------------------------------------------------------------------
    @Override
    public void onReceiveJob(AccessibilityEvent event) {
    	super.onReceiveJob(event);
    	if(!mIsEventWorking)return;
    	if(!mIsTargetPackageName)return;
    	//++++++++++++++++++++++++++++++++++++窗体改变+++++++++++++++++++++++++++++++++++++++++++++++++
		if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			AccessibilityNodeInfo rootNode=event.getSource();
			if(rootNode==null)return;
			//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)service.closeClick();
			//-------------------------拆红包界面----------------------------------------------------
			if(mCurrentUI.indexOf(VersionParam.WINDOW_LUCKYMONEY_RECEIVEUI)!=-1){
				if(!mReceiveJob.UnpackLuckyMoney(rootNode)){
					speaker.speak(LuckyMoneyReceiveJob.WECHAT_LUCKY_END);
					AccessibilityHelper.performBack(service);
					Config.JobState=Config.STATE_NONE;
				}
			}
			//-------------------------显示详细信息界面----------------------------------------------------
			if(mCurrentUI.equals(VersionParam.WINDOW_LUCKYMONEY_DETAILUI)){
				if(mLuckyMoneyType==Config.TYPE_LUCKYMONEY_THUNDER){
					UnpackLuckyMoneyShow(rootNode) ;
					mLuckyMoneyType=Config.TYPE_LUCKYMONEY_NONE;
					AccessibilityHelper.performBack(service);
				}
				Config.JobState=Config.STATE_NONE;
			}
		}//if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) 
		//++++++++++++++++++++++++++++++++++++内容改变+++++++++++++++++++++++++++++++++++++++++++++++++
		if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
			if(!Config.bAuto)return;
			//-------------------------拆红包界面----------------------------------------------------
			if(Config.JobState==Config.STATE_CLICK_LUCKYMONEY){
				if(mCurrentUI.indexOf(VersionParam.WINDOW_LUCKYMONEY_RECEIVEUI)!=-1){
					AccessibilityNodeInfo rootNode=event.getSource();
					if(rootNode==null)return;
					rootNode=AccessibilityHelper.getRootNode(rootNode);
					if(!mReceiveJob.UnpackLuckyMoney(rootNode)){
				   		speaker.speak(LuckyMoneyReceiveJob.WECHAT_LUCKY_END);
				   		Config.JobState=Config.STATE_NONE;
					}
				}
			}else{//if(Config.JobState==Config.STATE_CLICK_LUCKYMONEY)return;
				if(mCurrentUI.equals(Config.WINDOW_LUCKYMONEY_LAUNCHER_UI)){
					//----------------------------------------------------------------------------------
					AccessibilityNodeInfo rootNode=event.getSource();
					if(rootNode==null)return;
					rootNode=AccessibilityHelper.getRootNode(rootNode);
					if(clickLuckyMoney(rootNode))Config.JobState=Config.STATE_CLICK_LUCKYMONEY;			
				}//if(mCurrentUI.equals(Config.WINDOW_LUCKYMONEY_LAUNCHER_UI)){
			}
		}//if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) 
		
    }
    //红包来了，点击：
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean clickLuckyMoney(AccessibilityNodeInfo rootNode){

    	if(mCurrentUI.equals(Config.WINDOW_LUCKYMONEY_LAUNCHER_UI)){
    		/*
    		mLuckyMoney.LuckyMoneyNode=mLuckyMoney.getLastLuckyMoneyNode(rootNode);//取最后一个红包；
    		if(Config.wv>=1380){
    			mLuckyMoney.RobbedLuckyMoneyInfoNode=mLuckyMoney.getLastReceivedLuckyMoneyInfoNode(rootNode);
    			if(mLuckyMoney.isNewLuckyMoney(mLuckyMoney.LuckyMoneyNode, mLuckyMoney.RobbedLuckyMoneyInfoNode)){
    				//进入抢包状态：
        			Config.JobState=Config.STATE_CLICK_LUCKYMONEY;
        			//ClickLuckyMoneyDelay();//点包延时显示；
        			return true;
    			}
    		}
    		*/
    		mLuckyMoney.LuckyMoneyNode=AccessibilityHelper.findNodeInfosById(rootNode, VersionParam.WIDGET_ID_LUCKYMONEY_SAY, -1);
    		
    		if(mLuckyMoney.LuckyMoneyNode!=null){
    			AccessibilityNodeInfo friend=AccessibilityHelper.findNodeInfosByFriendNext(mLuckyMoney.LuckyMoneyNode);
    			if(friend==null){
    				Config.JobState=Config.STATE_CLICK_LUCKYMONEY;
    				mLuckyMoneyType=Config.TYPE_LUCKYMONEY_THUNDER;
    				ClickLuckyMoneyDelay();//点包延时显示；
    				Log.i(TAG, "friend:null");
    				return true;
    			}else{
    				if(friend.getText()!=null&&!friend.getText().toString().equals("已被领完")){
    					Config.JobState=Config.STATE_CLICK_LUCKYMONEY;
    					mLuckyMoneyType=Config.TYPE_LUCKYMONEY_THUNDER;
        				ClickLuckyMoneyDelay();//点包延时显示；
        				Log.i(TAG, "friend:"+friend.getText());
        				return true;
    				}
    			}
    			Log.i(TAG, "friend:"+friend.getText());
    		}
    	}
        return false;
    }
    //点包延时：
    private void ClickLuckyMoneyDelay() {

    	speaker.speak("正在获取牛牛：");
    	mBackgroundMusic.playBackgroundMusic( "ml.wav", true);
		fwp.ShowFloatingWindow(); 
    	fwp.c=Config.getConfig(context).getWechatOpenDelayTime();
    	fwp.StartSwitchPics();

    }

	//拆红包
	private boolean UnpackLuckyMoney(AccessibilityNodeInfo rootNode) {
		if (rootNode == null) return false;
		speaker.speak("正在为您分析：");
		mLuckyMoneyType=Config.TYPE_LUCKYMONEY_THUNDER;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
			Rect outBounds=new Rect();
			rootNode.getBoundsInScreen(outBounds);
			Point position=new Point();
			position.x=outBounds.right/2;
			position.y=outBounds.bottom*3/5;
			QiangHongBaoService.getQiangHongBaoService().startClick(position);
 		return true;
		}
        mLuckyMoneyType=mReceiveJob.IsLuckyMoneyReceive(rootNode);
        if(mLuckyMoneyType==Config.TYPE_LUCKYMONEY_THUNDER){
        	UnpackLuckyMoneyDelay();//拆包延时显示；
        	mReceiveJob.OpenLuckyMoney(rootNode);//拆包
        	return true;
        }
        if(mLuckyMoneyType==Config.TYPE_LUCKYMONEY_NONE){
        	//speeker.speak("福利包不拆！");
        	Config.JobState=Config.STATE_NONE;
        }
        return false;
        
    }
    //拆包延时：
    private void UnpackLuckyMoneyDelay() {
    	
    	//float f=0;
    	mWechatOpenDelayTime=Config.getConfig(context).getWechatOpenDelayTime();
    	//for(int i=0;i<mWechatOpenDelayTime;i++){
    		//handler.postDelayed(run, 10);
    	//}
    	float f=(float) (Math.random()*10000);
    	String s=String.valueOf(f);
    	Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
	    try{
	    	  Thread.sleep(10*mWechatOpenDelayTime);
	    }catch(Exception e){
	    } 
	    if(mWechatOpenDelayTime>0){
	    	f=(float) (Math.random()*10000);
	    	s=String.valueOf(f);
	    	Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
	    }

    }

    //拆包信息展示：
    private void UnpackLuckyMoneyShow(AccessibilityNodeInfo rootNode) {

        mDetailJob.LuckyMoneyDetailShow(rootNode);
        mLuckyMoneyType=Config.TYPE_LUCKYMONEY_NONE;
    }
	private BroadcastReceiver ClickLuckyMoneyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            //Log.d(TAG, "receive-->" + action);
            if(Config.ACTION_CLICK_LUCKY_MONEY.equals(action)) {
            	//点击红包：
            	mBackgroundMusic.stopBackgroundMusic();
            	if(!mCurrentUI.equals(Config.WINDOW_LUCKYMONEY_LAUNCHER_UI))return;
            	if(Config.JobState!=Config.STATE_CLICK_LUCKYMONEY)return;
            	//点击红包：
            	if(mLuckyMoney.ClickLuckyMoney(mLuckyMoney.LuckyMoneyNode))
            		Config.JobState=Config.STATE_CLICK_LUCKYMONEY;
            	else
            		Config.JobState=Config.TYPE_LUCKYMONEY_NONE;
            }
        }
    };
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)

    public void onNotificationPosted(IStatusBarNotification sbn) {
        Notification nf = sbn.getNotification();
        String text = String.valueOf(sbn.getNotification().tickerText);
        notificationEvent(text, nf);
    }
    /** 通知栏事件*/
    private void notificationEvent(String ticker, Notification nf) {
        String text = ticker;
        int index = text.indexOf(":");
        if(index != -1) {
            text = text.substring(index + 1);
        }
        text = text.trim();
        //transferAccounts.notificationEvent(ticker, nf);
        //if(text.contains(TransferAccounts.WX_TRANSFER_ACCOUNTS_ORDER)) { //红包消息
        //    newHongBaoNotification(nf);
        //}
    }

    /**打开通知栏消息*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void newHongBaoNotification(Notification notification) {
    	//TransferAccounts.mWorking = true;
        //以下是精华，将微信的通知栏消息打开
        PendingIntent pendingIntent = notification.contentIntent;
        boolean lock = NotifyHelper.isLockScreen(getContext());

        if(!lock) {
            NotifyHelper.send(pendingIntent);
        } else {
            //NotifyHelper.showNotify(getContext(), String.valueOf(notification.tickerText), pendingIntent);
        }

        if(lock) {
           // NotifyHelper.playEffect(getContext(), getConfig());
        }
    }

}

