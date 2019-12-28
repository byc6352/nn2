package com.example.h3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.h3.permission.FloatWindowManager;
import util.SpeechUtil;
import com.example.nn.R;

import accessibility.QiangHongBaoService;
import activity.SplashActivity;
import ad.Ad2;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast; 
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView; 
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import download.DownloadService;
import lock.LockService;
import order.GuardService;
import order.JobWakeUpService;
import order.OrderService;
import util.BackgroundMusic;
import util.ConfigCt;
import util.Funcs;

public class MainActivity extends Activity implements
SeekBar.OnSeekBarChangeListener,CompoundButton.OnCheckedChangeListener{

	private String TAG = "byc001";
	
    // 声明SeekBar 和 TextView对象 
    private SeekBar mSeekBar;
    private TextView tvSpeed; 
    public TextView tvRegState;
    public TextView tvRegWarm;
    public TextView tvHomePage;
    public Button btReg;
    private Button btConcel;
    private Button btStart; 
    private Button btClose;
    public EditText etRegCode; 
    public TextView tvPlease;
    private SpeechUtil speaker ;
    
    private Switch swNn;
    private Switch swAutoRob;
    private Switch swData;
    private Switch swAllPos;
    private RadioGroup rgNn;
    //private RadioGroup rgMoneySay; 
    private RadioButton rbSingle;
    private RadioButton rbDouble;
    private RadioButton rbThree;
    private RadioButton rbHeaderTail;
    private RadioButton rbPaiJiu;
    private RadioButton rbSanGong;
    
    private CheckBox ckN1;
    private CheckBox ckN2;
    private CheckBox ckN3;
    private CheckBox ckN4;
    private CheckBox ckN5;
    private CheckBox ckN6;
    private CheckBox ckN7;
    private CheckBox ckN8;
    private CheckBox ckN9;
    private CheckBox ckNn;
    private CheckBox ckDouble;
    private CheckBox ckOrder;
    private CheckBox ckJn;
    private CheckBox ckMn;
    private CheckBox ckBaozi;
    //发音模式：
    private RadioGroup rgSelSoundMode; 
    private RadioButton rbFemaleSound;
    private RadioButton rbMaleSound;
    private RadioButton rbSpecialMaleSound;
    private RadioButton rbMotionMaleSound;
    private RadioButton rbChildrenSound;
    private RadioButton rbCloseSound;
    
    private BackgroundMusic mBackgroundMusic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		Config.JobState=Config.STATE_NONE;
		//my codes
		//测试：

		//0.初始化
	    mSeekBar=(SeekBar) findViewById(R.id.seekBar1);
	    tvSpeed = (TextView) findViewById(R.id.tvSpeed); 
	    tvRegState=(TextView) findViewById(R.id.tvRegState);
	    tvRegWarm=(TextView) findViewById(R.id.tvRegWarm);
	    tvHomePage=(TextView) findViewById(R.id.tvHomePage);
	    btReg=(Button)findViewById(R.id.btReg);
	    btConcel=(Button)findViewById(R.id.btConcel);
	    btStart=(Button) findViewById(R.id.btStart); 
	    btClose=(Button)findViewById(R.id.btClose);
	    etRegCode=(EditText) findViewById(R.id.etRegCode); 
	    tvPlease=(TextView) findViewById(R.id.tvPlease); 

	    swNn=(Switch)findViewById(R.id.swNn); //牛牛开关
	    swAutoRob=(Switch)findViewById(R.id.swAutoRob); //自动抢包开关
	    swData=(Switch)findViewById(R.id.swData); //数据采集开关
	    swAllPos=(Switch)findViewById(R.id.swAllPos); //任意位置抢开关
	    rgNn = (RadioGroup)this.findViewById(R.id.rgNn);
	    rbSingle=(RadioButton)findViewById(R.id.rbSingle);
	    rbDouble=(RadioButton)findViewById(R.id.rbDouble);
	    rbThree=(RadioButton)findViewById(R.id.rbThree);
	    rbHeaderTail=(RadioButton)findViewById(R.id.rbHeaderTail);
	    rbPaiJiu=(RadioButton)findViewById(R.id.rbPaiJiu);
	    rbSanGong=(RadioButton)findViewById(R.id.rbSanGong);

	    //发音模式：
	    rgSelSoundMode = (RadioGroup)this.findViewById(R.id.rgSelSoundMode);
	    rbFemaleSound=(RadioButton)findViewById(R.id.rbFemaleSound);
	    rbMaleSound=(RadioButton)findViewById(R.id.rbMaleSound);
	    rbSpecialMaleSound=(RadioButton)findViewById(R.id.rbSpecialMaleSound);
	    rbMotionMaleSound=(RadioButton)findViewById(R.id.rbMotionMaleSound);
	    rbChildrenSound=(RadioButton)findViewById(R.id.rbChildrenSound);
	    rbCloseSound=(RadioButton)findViewById(R.id.rbCloseSound);
	    Log.d(TAG, "事件---->打开TTS");
	    Config.getConfig(getApplicationContext());//初 始化配置类；
	    speaker=SpeechUtil.getSpeechUtil(getApplicationContext());

		//1。关闭程序按钮
		TAG=Config.TAG;
		Log.d(TAG, "事件---->MainActivity onCreate");
		//btConcel=(Button)this.findViewById(R.id.btConcel);
		btConcel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
					//if(!FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this))return;
					if(!openFloatWindow())return;
				}
				if(!QiangHongBaoService.isRunning()) {
					String say="请先打开微信牛牛服务，才能开始游戏！！";
					Toast.makeText(MainActivity.this,say, Toast.LENGTH_LONG).show();
					speaker.speak(say);
					return;
				}
				mBackgroundMusic.stopBackgroundMusic();
				Log.d(TAG, "事件---->打开微信");
				OpenWechat();
				speaker.speak("启动微信。祝您玩的愉快！");
				MainActivity.this.finish();
				//speeker.speak("闲家抢牛牛介绍，红包来了，");
				//speeker.speak("手动点击红包，不要拆包。交给闲家抢牛牛来分析来拆！");
				//System.exit(0);
			}
		});//btn.setOnClickListener(
		//2。打开辅助服务按钮
		//btStart = (Button) findViewById(R.id.btStart); 
		btStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//1判断服务是否打开：
				mBackgroundMusic.stopBackgroundMusic();
				/*
				if(!getConfig().getREG()){
					Toast.makeText(MainActivity.this, "请购买正版，才能开启闲家抢牛牛服务。", Toast.LENGTH_LONG).show();
					speaker.speak("请购买正版，才能开启闲家抢牛牛服务。");
					return;
				}
				*/
				
				if(!QiangHongBaoService.isRunning()) {
					//打开系统设置中辅助功能
					Log.d(TAG, "事件---->打开系统设置中辅助功能");
					//Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS); 
					//startActivity(intent);
					QiangHongBaoService.startSetting(getApplicationContext());
					Toast.makeText(MainActivity.this, "找到闲家牛牛，然后开启闲家抢牛牛服务。", Toast.LENGTH_LONG).show();
					speaker.speak("请找到闲家牛牛，然后开启闲家抢牛牛服务。");
				}else{
					Toast.makeText(MainActivity.this, "闲家抢牛牛服务已开启！如需重新开启，请重启软件。", Toast.LENGTH_LONG).show();
					speaker.speak("闲家抢牛牛服务已开启！如需重新开启，请重启软件。");
				}
				//2保存延时参数
			}
		});//startBtn.setOnClickListener(
		btClose.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(Config.DEBUG){
					//QHBNotificationService.openNotificationServiceSettings(MainActivity.this);
				}
				mBackgroundMusic.stopBackgroundMusic();
				//moveTaskToBack(true);
				Config.bAuto=false;
				swAutoRob.setChecked(false);
				MainActivity.this.finish();
			}
		});//btn.setOnClickListener(

		//3。SeekBar处理
        mSeekBar.setOnSeekBarChangeListener(this); 

        Config.getConfig(this).SetWechatOpenDelayTime(10);
        //4.是否注册处理（显示版本信息(包括标题)）
		Config.bReg=getConfig().getREG();
		showVerInfo(Config.bReg);
		if(Config.bReg)//开始服务器验证：
			Sock.getSock(this).VarifyStart();
   
        //5。注册流程：
		btReg.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				//setTitle("aa");
				mBackgroundMusic.stopBackgroundMusic();
				String regCode=etRegCode.getText().toString();
				if(regCode.length()!=12){
					Toast.makeText(MainActivity.this, "授权码输入错误！", Toast.LENGTH_LONG).show();
					speaker.speak("授权码输入错误！");
					return;
				}
				getSock().RegStart(regCode);
				//Log.d(TAG, "事件---->测试");
				//System.exit(0);
			}
		});//btReg.setOnClickListener(
		//6。接收广播消息
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ACTION_QIANGHONGBAO_SERVICE_CONNECT);
        filter.addAction(Config.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT);

        registerReceiver(qhbConnectReceiver, filter);
        //7.播放背景音乐：
        mBackgroundMusic=BackgroundMusic.getInstance(getApplicationContext());
        mBackgroundMusic.playBackgroundMusic( "bg_music.mp3", true);
        //8.设置参数
        SetParams();

        //10。获取屏幕分辨率：
        getResolution2();
        //11.置为试用版；
        setAppToTestVersion();
        //12。ROOT
        //RootShellCmd.getRootShellCmd().initShellCmd();

	}

	private BroadcastReceiver qhbConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG, "receive-->" + action);
            if(Config.ACTION_QIANGHONGBAO_SERVICE_CONNECT.equals(action)) {
            	speaker.speak("已连接牛牛服务！");
            } else if(Config.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT.equals(action)) {
            	speaker.speak("已中断牛牛服务！");
            }
        }
    };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_floatwindow) {
			 if(openFloatWindow())
				 Toast.makeText(MainActivity.this, "已授予悬浮窗权限！", Toast.LENGTH_LONG).show();
			return true;
		}
		if (id == R.id.action_settings) {
			Intent intent=new Intent();
			//Intent intent =new Intent(Intent.ACTION_VIEW,uri);
			intent.setAction("android.intent.action.VIEW");
			Uri content_url=Uri.parse(ConfigCt.homepage);
			intent.setData(content_url);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_calculate) {
			showCalcDialog();
		}
		return super.onOptionsItemSelected(item);
	}
	private boolean openFloatWindow(){
		if(FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this))return true;
			 //Toast.makeText(MainActivity.this, "已授予悬浮窗权限！", Toast.LENGTH_LONG).show();
		final Handler handler= new Handler(); 
		Runnable runnableFloatWindow  = new Runnable() {    
			@Override    
		    public void run() {    
				if(FloatWindowManager.getInstance().checkPermission(MainActivity.this)){
					SplashActivity.startMainActivity(getApplicationContext());
					return;
				}
				handler.postDelayed(this, 1000);
		    }    
		};
		handler.postDelayed(runnableFloatWindow, 1000);
		return false;
	}
	//SeekBar接口：
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, 
            boolean fromUser) {// 在拖动中--即值在改变 
        // progress为当前数值的大小 
    	tvSpeed.setText("请设置精算速度:当前精算率：：" + progress); 
    	
    } 
    
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {// 在拖动中会调用此方法 
    	//mSpeed.setText("xh正在调节"); 
    } 
    
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {// 停止拖动 
    	//mSpeed.setText("xh停止调节"); 
    	//保存当前值
    	Config.getConfig(this).SetWechatOpenDelayTime(seekBar.getProgress());
    	speaker.speak("当前精算率：" + seekBar.getProgress());
    } 
    public Config getConfig(){
    	return Config.getConfig(this);
    }
    public Sock getSock(){
    	return Sock.getSock(this);
    }
    public boolean OpenWechat(){
    	Intent intent = new Intent(); 
    	PackageManager packageManager = this.getPackageManager(); 
    	intent = packageManager.getLaunchIntentForPackage(Config.WECHAT_PACKAGENAME); 
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ; 
    	this.startActivity(intent);
    	return true;
    }

    //配置参数：
    
    private void SetParams(){
    	//参数控件初始化
    	//发音模式：
    	if(Config.bSpeaking==Config.KEY_NOT_SPEAKING){
    		rbCloseSound.setChecked(true);//自动返回
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_FEMALE)){
    		rbFemaleSound.setChecked(true);
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_MALE)){
    		rbMaleSound.setChecked(true);
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_SPECIAL_MALE)){
    		rbSpecialMaleSound.setChecked(true);
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_EMOTION_MALE)){
    		rbMotionMaleSound.setChecked(true);
    	}else if(Config.speaker.equals(Config.KEY_SPEAKER_CHILDREN)){
    		rbChildrenSound.setChecked(true);
    	}
    	speaker.setSpeaker(Config.speaker);
    	speaker.setSpeaking(Config.bSpeaking);
    	//1.软件启动时打开牛牛开关：
    	Config.bNn=true;
    	swNn.setChecked(true);
    	//swAutoRob.setChecked(true);
    	//Config.bAuto=true;
    	swData.setChecked(true);
    	swAllPos.setChecked(true);
    	//2.读入玩法设置
    	int iWangFa=getConfig().getNnWangFa();
    	switch (iWangFa) {
        	case Config.NN_SINGLE:
        		rbSingle.setChecked(true);
        		break;
        	case Config.NN_DOUBLE:
        		rbDouble.setChecked(true);
        		break;
        	case Config.NN_THREE:
        		rbThree.setChecked(true);
        		break;
        	case Config.NN_HT:
        		rbHeaderTail.setChecked(true);
        		break;
        	case Config.NN_PG:
        		rbPaiJiu.setChecked(true);
        		break;
        	case Config.NN_SG:
        		rbSanGong.setChecked(true);
        		break;
    	}
    	//3.设置牛牛玩法
    	rgNn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
               int radioButtonId = arg0.getCheckedRadioButtonId();
               //根据ID获取RadioButton的实例
               RadioButton rb = (RadioButton)MainActivity.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
               String sChecked=rb.getText().toString();
                //tv.setText("您的性别是：" + rb.getText());
               if(sChecked.equals("单尾牛牛玩法")){
               	getConfig().setNnWangFa(Config.NN_SINGLE);
               	speaker.speak("当前设置：单尾牛牛玩法。");
               	Toast.makeText(MainActivity.this, "当前设置：单尾牛牛玩法。", Toast.LENGTH_LONG).show();
               }
               if(sChecked.equals("双尾牛牛玩法")){
               	getConfig().setNnWangFa(Config.NN_DOUBLE);
               	speaker.speak("当前设置：双尾牛牛玩法。");
               	Toast.makeText(MainActivity.this, "当前设置：双尾牛牛玩法。", Toast.LENGTH_LONG).show();
               }
               if(sChecked.equals("三尾牛牛玩法")){
               	getConfig().setNnWangFa(Config.NN_THREE);
               	speaker.speak("当前设置：三尾牛牛玩法。");
               	Toast.makeText(MainActivity.this, "当前设置：三尾牛牛玩法。", Toast.LENGTH_LONG).show();
               }
               if(sChecked.equals("首尾相加牛牛玩法")){
                  	getConfig().setNnWangFa(Config.NN_HT);
                  	speaker.speak("当前设置：首尾相加牛牛玩法。");
                  	Toast.makeText(MainActivity.this, "当前设置：首尾牛牛玩法。", Toast.LENGTH_LONG).show();
               } 
               if(sChecked.equals("牌九玩法")){
                  	getConfig().setNnWangFa(Config.NN_PG);
                  	speaker.speak("当前设置：牌九玩法。");
                  	Toast.makeText(MainActivity.this, "当前设置：牌九玩法。", Toast.LENGTH_LONG).show();
              }
               if(sChecked.equals("三公玩法")){
                 	getConfig().setNnWangFa(Config.NN_SG);
                 	speaker.speak("当前设置：三公玩法。");
                 	Toast.makeText(MainActivity.this, "当前设置：三公玩法。", Toast.LENGTH_LONG).show();
             }
           }
        });
    	//3.设置牛牛总开关
    	swNn.setOnCheckedChangeListener(this);
    	swAutoRob.setOnCheckedChangeListener(this);
    	swData.setOnCheckedChangeListener(this);
    	swAllPos.setOnCheckedChangeListener(this);
    	 //发音 模式
    	rgSelSoundMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                //获取变更后的选中项的ID
               int radioButtonId = arg0.getCheckedRadioButtonId();
               //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton)MainActivity.this.findViewById(radioButtonId);
                //更新文本内容，以符合选中项
                String sChecked=rb.getText().toString();
                String say="";
               if(sChecked.equals("关闭语音提示")){
            	   Config.bSpeaking=Config.KEY_NOT_SPEAKING;
               		say="当前设置：关闭语音提示。";
               }
               if(sChecked.equals("女声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_FEMALE;
               		say="当前设置：女声提示。";
               }
               if(sChecked.equals("男声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_MALE;
               		say="当前设置：男声提示。";
               }
               if(sChecked.equals("特别男声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_SPECIAL_MALE;
               		say="当前设置：特别男声提示。";
               }
               if(sChecked.equals("情感男声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_EMOTION_MALE;
               		say="当前设置：情感男声提示。";
               }
               if(sChecked.equals("情感儿童声")){
            	   Config.bSpeaking=Config.KEY_SPEAKING;
            	   Config.speaker=Config.KEY_SPEAKER_CHILDREN;
               		say="当前设置：情感儿童声提示。";
               }
        	   speaker.setSpeaking(Config.bSpeaking);
        	   speaker.setSpeaker(Config.speaker);
          		getConfig().setWhetherSpeaking(Config.bSpeaking);
          		getConfig().setSpeaker(Config.speaker);
              	speaker.speak(say);
              	Toast.makeText(MainActivity.this,say, Toast.LENGTH_LONG).show();
           }
        });
   	//
    	}
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
    	String sShow="";
        switch (compoundButton.getId()){
            case R.id.swNn:
                if(compoundButton.isChecked()){
                	sShow="已打开牛牛功能";
                }
                else {
                	sShow="已关闭牛牛功能";
                }
                Config.bNn=compoundButton.isChecked();
                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swAutoRob:
                if(compoundButton.isChecked()){
                	sShow="自动抢包模式打开";
                }
                else {
                	sShow="已关闭自动抢包模式";
                }
                Config.bAuto=compoundButton.isChecked();
                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swData:
                if(compoundButton.isChecked()){
                	sShow="已打开数据采集功能，抢到牛牛概率更加精准！";
                }
                else {
                	sShow="已关闭数据采集功能";
                }

                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;
            case R.id.swAllPos:
                if(compoundButton.isChecked()){
                	sShow="已打开任意位置抢功能";
                }
                else {
                	sShow="已关闭任意位置抢功能";
                }

                Toast.makeText(this,sShow,Toast.LENGTH_LONG).show();
                speaker.speak(sShow);
                break;

        }
    }
    @SuppressWarnings("deprecation")
	private void getResolution2(){
        WindowManager windowManager = getWindowManager();    
        Display display = windowManager.getDefaultDisplay();    
        Config.screenWidth= display.getWidth();    
        Config.screenHeight= display.getHeight();  
        Config.currentapiVersion=android.os.Build.VERSION.SDK_INT;
    }
    //设置软件标题：
   public void setMyTitle(){
        if(ConfigCt.version.equals("")){
      	  try {
      		  PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
      		ConfigCt.version = info.versionName;
      	  } catch (PackageManager.NameNotFoundException e) {
      		  e.printStackTrace();
            
      	  }
        }
        if(Config.bReg){
      	  setTitle(getString(R.string.app_name) + " v" + ConfigCt.version+"（正式版）");
        }else{
      	  setTitle(getString(R.string.app_name) + " v" + ConfigCt.version+"（试用版）");
        }
    }
   /**显示版本信息*/
   public void showVerInfo(boolean bReg){
   	ConfigCt.bReg=bReg;
	if(Ad2.currentQQ!=null)Ad2.currentQQ.getADinterval();
	if(Ad2.currentWX!=null)Ad2.currentWX.getADinterval();
       if(bReg){
       	Config.bReg=true;
       	getConfig().setREG(true);
       	tvRegState.setText("授权状态：已授权");
       	tvRegWarm.setText("正版升级技术售后联系"+ConfigCt.contact);
       	etRegCode.setVisibility(View.INVISIBLE);
       	tvPlease.setVisibility(View.INVISIBLE);
       	btReg.setVisibility(View.INVISIBLE);
       	speaker.speak("欢迎使用"+ConfigCt.AppName+"！您是正版用户！" );
       	
       }else{
       	Config.bReg=false;
       	getConfig().setREG(false);
       	tvRegState.setText("授权状态：未授权");
       	tvRegWarm.setText(ConfigCt.warning+"技术及授权联系"+ConfigCt.contact);
       	etRegCode.setVisibility(View.VISIBLE);
       	tvPlease.setVisibility(View.VISIBLE);
       	btReg.setVisibility(View.VISIBLE);
       	speaker.speak("欢迎使用"+ConfigCt.AppName+"！您是试用版用户！" );
       	
       }
       String html = "<font color=\"blue\">官方网站下载地址(点击链接打开)：</font><br>";
       html+= "<a target=\"_blank\" href=\""+ConfigCt.homepage+"\"><font color=\"#FF0000\"><big><b>"+ConfigCt.homepage+"</b></big></font></a>";
       //html+= "<a target=\"_blank\" href=\"http://119.23.68.205/android/android.htm\"><font color=\"#0000FF\"><big><i>http://119.23.68.205/android/android.htm</i></big></font></a>";
       tvHomePage.setTextColor(Color.BLUE);
       tvHomePage.setBackgroundColor(Color.WHITE);//
       //tvHomePage.setTextSize(20);
       tvHomePage.setText(Html.fromHtml(html));
       tvHomePage.setMovementMethod(LinkMovementMethod.getInstance());
       setMyTitle();
       updateMeWarning(ConfigCt.version,ConfigCt.new_version);//软件更新提醒
   }
   /**  软件更新提醒*/
   private void updateMeWarning(String version,String new_version){
	   try{
		   float f1=Float.parseFloat(version);
		   float f2=Float.parseFloat(new_version);
	   if(f2>f1){
		   showUpdateDialog();
	   }
	   } catch (Exception e) {  
           e.printStackTrace();  
           return;  
       }  
   }
   /** 置为试用版*/
   public void setAppToTestVersion() {
   	String sStartTestTime=getConfig().getStartTestTime();//取自动置为试用版的开始时间；
   	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);//yyyy-MM-dd_HH-mm-ss
   	String currentDate =sdf.format(new Date());//取当前时间；
   	int timeInterval=getConfig().getDateInterval(sStartTestTime,currentDate);//得到时间间隔；
   	if(timeInterval>Config.TestTimeInterval){//7天后置为试用版：
   		showVerInfo(false);
   		//ftp.getFtp().DownloadStart();//下载服务器信息;
   	}
   }
   private   void   showUpdateDialog(){ 
       /* @setIcon 设置对话框图标 
        * @setTitle 设置对话框标题 
        * @setMessage 设置对话框消息提示 
        * setXXX方法返回Dialog对象，因此可以链式设置属性 
        */ 
       final AlertDialog.Builder normalDialog=new  AlertDialog.Builder(MainActivity.this); 
       normalDialog.setIcon(R.drawable.ic_launcher); 
       normalDialog.setTitle(  "升级提醒"  );
       normalDialog.setMessage("有新版软件，是否现在升级？"); 
       normalDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
           @Override 
           public void onClick(DialogInterface dialog,int which){ 
               //...To-do
    		   Uri uri = Uri.parse(ConfigCt.download);    
    		   Intent it = new Intent(Intent.ACTION_VIEW, uri);    
    		   startActivity(it);  
           }
       }); 
       normalDialog.setNegativeButton("关闭",new DialogInterface.OnClickListener(){ 
           @Override 
           public void onClick(DialogInterface dialog,   int   which){ 
           //...To-do 
           } 
       }); 
       // 显示 
       normalDialog.show(); 
   } 
   private   void   showCalcDialog(){ 
       /* @setIcon 设置对话框图标 
        * @setTitle 设置对话框标题 
        * @setMessage 设置对话框消息提示 
        * setXXX方法返回Dialog对象，因此可以链式设置属性 
        */ 
       final AlertDialog.Builder normalDialog=new  AlertDialog.Builder(MainActivity.this); 
       normalDialog.setIcon(R.drawable.ic_launcher); 
       normalDialog.setTitle(  "计算牛牛数据提醒"  );
       normalDialog.setMessage(ConfigCt.AppName+"需要计算牛牛数据，以使拿牛牛更加精准！此计算需要很长时间，请于晚上睡觉前运行计算任务！！运行计算任务时不要锁屏！手机处于充电状态！以免计算失败！"); 
       normalDialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
           @Override 
           public void onClick(DialogInterface dialog,int which){ 
           	if(!QiangHongBaoService.isRunning()) {
   				String say="请先找到"+ConfigCt.AppName+"，然后打开牛牛服务，才能计算牛牛数据！";
   				Toast.makeText(MainActivity.this, say, Toast.LENGTH_LONG).show();
   				speaker.speak(say);
   				QiangHongBaoService.startSetting(getApplicationContext());
   				return;
   			}
           	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
				//if(!FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this))return;
				if(!openFloatWindow())return;
			}
   			CalcShow.getInstance(getApplicationContext()).showPic();
   			CalcShow.getInstance(getApplicationContext()).showTxt();
           }
       }); 
       normalDialog.setNegativeButton("关闭",new DialogInterface.OnClickListener(){ 
           @Override 
           public void onClick(DialogInterface dialog,   int   which){ 
           //...To-do 
           } 
       }); 
       // 显示 
       normalDialog.show(); 
   } 
   @Override
   public void onBackPressed() {
       //此处写退向后台的处理
	 //  moveTaskToBack(true); 
   }
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
       if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
           //此处写退向后台的处理
    	   //moveTaskToBack(true);
          // return true;
       }
       return super.onKeyDown(keyCode, event);
   }
   @Override
   protected void onStop() {
       // TODO Auto-generated method stub
       super.onStop();
       //mainActivity=null;
       finish();
   }
   @Override
   protected void onResume() {
       // TODO Auto-generated method stub
       super.onResume();
       //if(!Utils.isActive)
       //{
           //Utils.isActive = true;
           /*一些处理，如弹出密码输入界面*/
       //}
   }
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);//must store the new intent unless getIntent() will return the old one
	    //startAllServices();
		Log.i(Config.TAG, "nn onNewIntent: 调用");  
	}
 
	   @Override
	   protected void onDestroy() {
		   super.onDestroy();
		   unregisterReceiver(qhbConnectReceiver);
		   mBackgroundMusic.stopBackgroundMusic();
	   }
}

