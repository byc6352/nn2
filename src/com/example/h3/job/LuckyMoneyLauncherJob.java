/**
 * 
 */
package com.example.h3.job;

import java.util.List;

import com.example.h3.Config;
import accessibility.AccessibilityHelper;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
/**
 * @author byc
 *
 */
public class LuckyMoneyLauncherJob {
	private static LuckyMoneyLauncherJob current;
	private Context context;
	private static String TAG = "byc001";
	private static final String DIGITAL="0123456789";//
	public AccessibilityNodeInfo LuckyMoneyNode;//红包来了；

	
	private LuckyMoneyLauncherJob(Context context) {
		this.context = context;
		TAG=Config.TAG;
	}
    public static synchronized LuckyMoneyLauncherJob getLuckyMoneyLauncherJob(Context context) {
        if(current == null) {
            current = new LuckyMoneyLauncherJob(context);
        }
        return current;
        
    }
    //获取最新的红包
    public AccessibilityNodeInfo getLastLuckyMoneyNode(AccessibilityNodeInfo nodeInfo){
        //AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null)return null;
        return AccessibilityHelper.findNodeInfosByText(nodeInfo,"领取红包",-1);
    }
    //获取已领取红包信息
    public AccessibilityNodeInfo getLastReceivedLuckyMoneyInfoNode(AccessibilityNodeInfo nodeInfo){
    	//(nodeInfo,"com.tencent.mm:id/ho",-1)
        if (nodeInfo == null)return null;
        return AccessibilityHelper.findNodeInfosByText(nodeInfo, "你领取了",-1);        
    }
    //判断是否已领取过红包：
    public boolean isReceivedLuckyMoney(AccessibilityNodeInfo LuckyMoneyNode,AccessibilityNodeInfo ReceivedLuckyMoneyInfoNode){
    	//无红包
    	if(LuckyMoneyNode==null)return true;
    	//没有领取信息
    	if(ReceivedLuckyMoneyInfoNode==null)return false;
    	//判断：
    	Rect outBounds1=new Rect();
    	Rect outBounds2=new Rect();
    	LuckyMoneyNode.getBoundsInScreen(outBounds1);
    	ReceivedLuckyMoneyInfoNode.getBoundsInScreen(outBounds2);
    	if(outBounds2.top>outBounds1.top)
    		return true;
    	else
    		return false;
    }
    //判断是否是已领完的红包：
    public boolean isReceivedOverLuckyMoney(AccessibilityNodeInfo LuckyMoneyNode,AccessibilityNodeInfo preLuckyMoneyNode){
    	if(LuckyMoneyNode==null)return false;
    	if(preLuckyMoneyNode==null)return false;
    	Rect outBounds1=new Rect();
    	Rect outBounds2=new Rect();
    	LuckyMoneyNode.getBoundsInScreen(outBounds1);
    	preLuckyMoneyNode.getBoundsInScreen(outBounds2);
    	if(outBounds1.top==outBounds2.top)return true;
    	return false;
    }
    //点击红包：
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean ClickLuckyMoney(AccessibilityNodeInfo LuckyMoneyNode){
    	AccessibilityNodeInfo parent=LuckyMoneyNode.getParent();
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
    		if (parent != null) {
    			parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    			return true;
    		}
    	 }
    	return false;
    }
    //获取红包上的文字信息
	public String getLuckyMoneyTxt(AccessibilityNodeInfo LuckyMoneyNode){
        //AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (LuckyMoneyNode == null)return null;
        AccessibilityNodeInfo parent=LuckyMoneyNode.getParent();
        if(parent!=null){
        	AccessibilityNodeInfo LuckyMoneySayNode =parent.getChild(0);
        	return LuckyMoneySayNode.getText().toString();
        }
        return null;
    }
    //判断是否是雷包
    public boolean isLuckyMoneyLei(AccessibilityNodeInfo LuckyMoneyNode){
    	//无红包
    	if(LuckyMoneyNode==null)return false;
    	//获取红包语
    	String sLuckyMoneySay=getLuckyMoneyTxt(LuckyMoneyNode);
    	if(sLuckyMoneySay==null)return false;
    	//判断：
    	if(checkLuckyMoneySayType(sLuckyMoneySay)==Config.TYPE_LUCKYMONEY_THUNDER)
    		return true;
    	else
    		return false;
    }
    public int checkLuckyMoneySayType(String LuckyMoneySay){

    		String LuckyMoneySayTail=LuckyMoneySay.substring(LuckyMoneySay.length()-1,LuckyMoneySay.length());
    		if(DIGITAL.indexOf(LuckyMoneySayTail)==-1)
    			return Config.TYPE_LUCKYMONEY_WELL;
    		else
    			return Config.TYPE_LUCKYMONEY_THUNDER;
    }
    /** 是否为群聊天*/
    public boolean isMemberChatUi(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return false;
        }
        //String id = "com.tencent.mm:id/ces";
        //980 com.tencent.mm:id/gd  6.5.3
        String id = "com.tencent.mm:id/ff";
        int wv = Config.getConfig(context).getWechatVersion();
        Log.i(TAG, "getWechatVersion()-----"+wv);
        if(wv <= 680) {
            id = "com.tencent.mm:id/ew";
        } else if(wv <= 700) {
            id = "com.tencent.mm:id/cbo";
        }else if (wv == 920) {
        	id = "com.tencent.mm:id/g1";
        }else if (wv == 940) {
        	id = "com.tencent.mm:id/g1";
        }else if (wv == 960) {
        	id = "com.tencent.mm:id/gc";
        }else if (wv == 980) {
        	id = "com.tencent.mm:id/gd";
        }else if (wv == 1000) {
        	id = "com.tencent.mm:id/gh";
        }else if (wv == 1020) {
        	id = "com.tencent.mm:id/gh";
        }else if (wv == 1041) {
        	id = "com.tencent.mm:id/gh";
        }else if (wv == 1060) {
        	id = "com.tencent.mm:id/gp";
        }else if (wv == 1080) {
        	id = "com.tencent.mm:id/gs";
        }
        String title = null;
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosById(nodeInfo, id,0);
        if(target != null) {
            title = String.valueOf(target.getText());
            if(title != null && title.endsWith(")")) {
                return true;
            }
        }
        //1。查找返回按钮：
        String desc="";
        boolean bFind=false;
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("返回");

        if(list != null && !list.isEmpty()) {
            for(AccessibilityNodeInfo node : list) {
                if(!"android.widget.ImageView".equals(node.getClassName())) {
                    continue;
                }else{
                	desc = String.valueOf(node.getContentDescription());
                	if(!"返回".equals(desc)) {
                		continue;
                	}else{bFind=true;break;}
                }//if(!"android.widget.ImageView".equals(node.getClassName())) {
            }// for(AccessibilityNodeInfo node : list) {
        }else {return false;}//if(list != null && !list.isEmpty()) {
        if(!bFind)return false;
        
        //查找聊天信息按钮：
        bFind=false;
        list = nodeInfo.findAccessibilityNodeInfosByText("聊天信息");

        if(list != null && !list.isEmpty()) {
            for(AccessibilityNodeInfo node : list) {
                if(!"android.widget.TextView".equals(node.getClassName())) {
                    continue;
                }else{
                	desc = String.valueOf(node.getContentDescription());
                	if(!"聊天信息".equals(desc)) {
                		continue;
                	}else{bFind=true;break;}
                }//if(!"android.widget.ImageView".equals(node.getClassName())) {
            }// for(AccessibilityNodeInfo node : list) {
            return bFind;
        }else {return false;}//if(list != null && !list.isEmpty()) {
    }
    public String getSendLuckyMoneyManName(AccessibilityNodeInfo LuckyMoneyNode){
    	
    	AccessibilityNodeInfo parent=LuckyMoneyNode.getParent();
    	int i=1;
    	while(parent!=null){
    		
    		parent=parent.getParent();
    		i=i+1;
    		if(i>=6)break;
    	}
    	if(i!=6)return null;
    	if(parent==null)return null;
    	parent=parent.getChild(0);
    	if(parent==null)return null;
    	parent=parent.getChild(2);
    	if(parent==null)return null;
    	if(!"android.widget.ImageView".equals(parent.getClassName())) return null;
    	String desc = String.valueOf(parent.getContentDescription());
    	return desc;
    }
  
    /*输入文本*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  boolean nodeInput(AccessibilityNodeInfo edtNode,String txt){
    	if(Config.currentapiVersion>=Build.VERSION_CODES.LOLLIPOP){//android 5.0
    		Bundle arguments = new Bundle();
        	arguments.putCharSequence(AccessibilityNodeInfo .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,txt);
        	edtNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        	return true;
    	}
    	if(Config.currentapiVersion>=Build.VERSION_CODES.JELLY_BEAN_MR2){//android 4.3
    		ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);  
    		ClipData clip = ClipData.newPlainText("text",txt);  
    		clipboard.setPrimaryClip(clip);  

    		edtNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);  
    		////粘贴进入内容  
    		edtNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);  
    		return true;
    	}

    	return false;
    }  
}

