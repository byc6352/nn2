package com.example.h3.job;


import com.example.h3.Config;
import accessibility.QiangHongBaoService;

import ad.VersionParam;
import accessibility.AccessibilityHelper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;


/*
 * 红包类；标识红包尾性，红包处理方法。
 */
public class LuckyMoney {
	
	private static LuckyMoney current;
	private Context context;
	private static String TAG = "byc001";
	private static final String DIGITAL="0123456789";//
	public static final int TYPE_LUCKYMONEY_ME_ROBBED=1;//自己领走的红包；
	public static final int TYPE_LUCKYMONEY_YOU_ROBBED=2;//别人领走的红包；
	public static final int TYPE_LUCKYMONEY_NO_ROBBED=0;//未领过的红包；
	public static final int TYPE_LUCKYMONEY_COMMON=1;//普通的红包；
	public static final int TYPE_LUCKYMONEY_PERSONALITY=2;//个性的红包；
	public static final int TYPE_LUCKYMONEY_THUNDER=11;//雷的红包；
	public static final int TYPE_LUCKYMONEY_WELFARE=12;//福利的红包；
	public static final int TYPE_LUCKYMONEY_PAY_FOR=13;//赔付的红包；
	/*
	 * 属性1：领取的红包情况：默认未领取，自己领取的，别人领取的；谁抢到了红包；
	 */
	public int WhoRobbed=TYPE_LUCKYMONEY_NO_ROBBED;//未领取
	/*
	 * 属性2：个性红包还是普通红包：默认普通红包；
	 */
	public int LuckyMoneyType=TYPE_LUCKYMONEY_COMMON;
	/*
	 * 属性3：赔付包，福利包还是雷包；默认雷包;
	 */
	public int LuckyMoneyDefined=TYPE_LUCKYMONEY_THUNDER;
	/*
	 * 标识要处理的红包：
	 */
	public AccessibilityNodeInfo LuckyMoneyNode;
	/*
	 * 已获取红包信息；
	 */
	public AccessibilityNodeInfo RobbedLuckyMoneyInfoNode;
	/*
	 * 红包语；
	 */
	public String mLuckyMoneySay;
	/*
	 * 构造方法
	 */
	private LuckyMoney(Context context) {
		this.context = context;
		TAG=Config.TAG;
	}
    public static synchronized LuckyMoney getLuckyMoney(Context context) {
        if(current == null) {
            current = new LuckyMoney(context);
        }
        return current;
    }
    /*
     * 获取最后一个未获取的普通红包
     */
    public AccessibilityNodeInfo getLastLuckyMoneyNode(AccessibilityNodeInfo rootNode){
    	if(Config.wv>=1380)
    		return AccessibilityHelper.findNodeInfosByText(rootNode, "微信红包", -1);
    	else
    		return AccessibilityHelper.findNodeInfosByText(rootNode, "领取红包", -1);
    }
/*
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
    */
    //点击红包：
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean ClickLuckyMoney(AccessibilityNodeInfo LuckyMoneyNode){
    	if(Config.wv>=1380)
    		return AccessibilityHelper.performClick(LuckyMoneyNode);
    	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
    		return AccessibilityHelper.performClick(LuckyMoneyNode);
    	Rect outBounds=new Rect();
    	LuckyMoneyNode.getBoundsInScreen(outBounds);
		Point position=new Point();
		position.x=outBounds.left+(outBounds.right-outBounds.left)/2;
		position.y=outBounds.top+(outBounds.bottom-outBounds.top)/2;
		return QiangHongBaoService.getQiangHongBaoService().pressLocation(position);
    }
    //获取已领取红包信息
    public AccessibilityNodeInfo getLastReceivedLuckyMoneyInfoNode(AccessibilityNodeInfo nodeInfo){
    	//(nodeInfo,"com.tencent.mm:id/ho",-1)
        if (nodeInfo == null)return null;
        //return AccessibilityHelper.findNodeInfosByText(nodeInfo, "{\"icon\":\"qqwallet_custom_tips_icon.png\",\"alt\":\"\"}你领取了",-1);  
        return AccessibilityHelper.findNodeInfosByText(nodeInfo, "你领取了",-1);
    }
    //判断是否新红包：
    public boolean isNewLuckyMoney(AccessibilityNodeInfo LuckyMoneyNode,AccessibilityNodeInfo ReceivedLuckyMoneyInfoNode){
    	//无红包
    	if(LuckyMoneyNode==null)return false;
    	//没有领取信息
    	if(ReceivedLuckyMoneyInfoNode==null)return true;
    	//判断：
    	boolean bNewLuckyMoney=false;
    	Rect outBounds1=new Rect();
    	Rect outBounds2=new Rect();
    	LuckyMoneyNode.getBoundsInScreen(outBounds1);
    	ReceivedLuckyMoneyInfoNode.getBoundsInScreen(outBounds2);
    	if(outBounds2.top>outBounds1.top)bNewLuckyMoney=false;else bNewLuckyMoney=true;
    	return bNewLuckyMoney;
    }
    //判断是否是雷包
    public boolean isLuckyMoneyLei(AccessibilityNodeInfo LuckyMoneyNode){
    	//无红包
    	if(LuckyMoneyNode==null)return false;
    	//获取红包语
    	mLuckyMoneySay=getLuckyMoneyTxt(LuckyMoneyNode);
    	if(mLuckyMoneySay==null)return false;
    	//判断：
    	if(checkLuckyMoneySayType(mLuckyMoneySay)==TYPE_LUCKYMONEY_THUNDER)
    		return true;
    	else
    		return false;
    }
    public int checkLuckyMoneySayType(String LuckyMoneySay){

    		String LuckyMoneySayTail=LuckyMoneySay.substring(LuckyMoneySay.length()-1,LuckyMoneySay.length());
    		if(DIGITAL.indexOf(LuckyMoneySayTail)==-1)
    			return TYPE_LUCKYMONEY_PAY_FOR;
    		else
    			return TYPE_LUCKYMONEY_THUNDER;
    }
    //获取红包上的文字信息
   	public String getLuckyMoneyTxt(AccessibilityNodeInfo LuckyMoneyNode){
           //980 com.tencent.mm:id/a55 6.5.3
           if (LuckyMoneyNode == null)return null;
           if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){;//系统版本小于4.3不拆包；
           		AccessibilityNodeInfo parent=LuckyMoneyNode.getParent();
           		if(parent!=null){
           			AccessibilityNodeInfo LuckyMoneySayNode =parent.getChild(0);
           			return LuckyMoneySayNode.getText().toString();
           		}//780
           		return null;
           }
           //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){//系统版本小于4.3不拆包；
           String resId=VersionParam.WIDGET_ID_LUCKYMONEY_SAY;//980 最新版本微信;6.5.3
           AccessibilityNodeInfo nodeInfo = AccessibilityHelper.getRootNode(LuckyMoneyNode);
           
           AccessibilityNodeInfo LuckyMoneySayNode=AccessibilityHelper.findNodeInfosById(nodeInfo, resId, -1);
           if (LuckyMoneySayNode == null){//没有及时更新处理：
        	   AccessibilityNodeInfo parent=LuckyMoneyNode.getParent();
          		if(parent!=null){
          			LuckyMoneySayNode =parent.getChild(0);
          			return LuckyMoneySayNode.getText().toString();
          		}//780
          		return null;
           }else{
        	   return LuckyMoneySayNode.getText().toString();
           }
       }

    /*
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
    */
   
}
