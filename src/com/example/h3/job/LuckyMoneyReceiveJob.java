/**
 * 
 */
package com.example.h3.job;

import com.example.h3.Config;

import accessibility.AccessibilityHelper;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * @author byc
 *
 */
public class LuckyMoneyReceiveJob {
	private static LuckyMoneyReceiveJob current;
	private Context context;
	public static final String WECHAT_LUCKY_END="�����ˣ����������";//
	
	private static final String WECHAT_LUCKY_SEND="����һ�������������";//
	private static final String DIGITAL="0123456789";//
	private String[] mReceiveInfo={"","","","",""};//�����Ϣ��
	private int mIntInfo=0;//��Ϣ����
	private boolean bClicked=false;//�� ��������
	private boolean bNeedClick=false;//�Ƿ������
	private boolean bRecycled=false;//�Ƿ��˳�ѭ��
	public int mLuckyMoneyType=0;//�������Ѳ���������������װ���
	
	private LuckyMoneyReceiveJob(Context context) {
		this.context = context;
	}
    public static synchronized LuckyMoneyReceiveJob getLuckyMoneyReceiveJob(Context context) {
        if(current == null) {
            current = new LuckyMoneyReceiveJob(context);
        }
        return current;
        
    }
    /*����*/
    public boolean UnpackLuckyMoney(AccessibilityNodeInfo rootNode) {
    	if(Config.wv<1380)return true;
    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosByText(rootNode, WECHAT_LUCKY_END, 0);
    	if(nodeInfo==null){
    		AccessibilityNodeInfo btnNode=AccessibilityHelper.findNodeInfosByClassName(rootNode, "android.widget.Button", -1, true);
    		if(btnNode!=null){
    			AccessibilityHelper.performClick(btnNode);
    			return true;
    		}
    	}else{
    		//speeker.speak(WECHAT_LUCKY_END);
    		//AccessibilityHelper.performBack(service);
    		return false;
    	}
    	return true;

    }
    
    
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void recycle(AccessibilityNodeInfo info) {
    	if(bRecycled)return;
		if (info.getChildCount() == 0) {
			//ȡ��Ϣ
			if(!bNeedClick){
				//�����ˣ�
				if(mIntInfo==1){
					mReceiveInfo[mIntInfo]=info.getText().toString();
					if(!mReceiveInfo[mIntInfo].equals(WECHAT_LUCKY_SEND)){
						mLuckyMoneyType=Config.TYPE_LUCKYMONEY_NONE;//�����ˣ�
						bRecycled=true;
						return;
					}else{
						mLuckyMoneyType=Config.TYPE_LUCKYMONEY_THUNDER;//�Ǻ����
						bRecycled=true;
						return;
					}
				}
				/*
				mReceiveInfo[mIntInfo]=info.getText().toString();
				if(mIntInfo==2){
					mLuckyMoneyType=CheckLuckyMoneyType(mReceiveInfo[1],mReceiveInfo[2]);
					bRecycled=true;
					return;
				}
				*/
				mIntInfo=mIntInfo+1;
			}
			//Log.i(TAG, "child widget----------------------------" + info.getClassName());
			//Log.i(TAG, "showDialog:" + info.canOpenPopup());
			//Log.i(TAG, "Text��" + info.getText());
			//Log.i(TAG, "windowId:" + info.getWindowId());
			//Log.i(TAG, "ResouceId:" + info.getViewIdResourceName());
			//Log.i(TAG, "isClickable:" + info.isClickable());
			if(info.isClickable()){
				
				if(!bClicked)info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				bClicked=true;
			}
			
		} else {
			for (int i = 0; i < info.getChildCount(); i++) {
				if(info.getChild(i)!=null){
					recycle(info.getChild(i));
				}
			}
		}
	}
    public int CheckLuckyMoneyType(String LuckyMoneySend,String LuckyMoneySay){
    	if(LuckyMoneySend.equals(WECHAT_LUCKY_SEND)){
    		String LuckyMoneySayTail=LuckyMoneySay.substring(LuckyMoneySay.length()-1,LuckyMoneySay.length());
    		if(DIGITAL.indexOf(LuckyMoneySayTail)==-1)
    			return Config.TYPE_LUCKYMONEY_WELL;
    		else
    			return Config.TYPE_LUCKYMONEY_THUNDER;
    	}else{
    		return Config.TYPE_LUCKYMONEY_NONE;
    	}
    }
    public void OpenLuckyMoney(AccessibilityNodeInfo info){
    	bNeedClick=true;
    	bClicked=false;
    	bRecycled=false;
    	mIntInfo=0;
    	recycle(info);
    	return ;
    }
    public int IsLuckyMoneyReceive(AccessibilityNodeInfo info){
    	bNeedClick=false;
    	mIntInfo=0;
    	bRecycled=false;
    	recycle(info);
    	return mLuckyMoneyType;
    }
}
