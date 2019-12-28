/**
 * 
 */
package com.example.h3.job;

import com.example.h3.Config;
import com.example.h3.MainActivity;

import accessibility.AccessibilityHelper;
import util.SpeechUtil;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

/**
 * @author byc
 *
 */
public class LuckyMoneyDetailJob {
	private static LuckyMoneyDetailJob current;
	private Context context;
	private String[] mReceiveInfo={"","",""};//拆包信息；
	private int mIntInfo=0;//信息数；
	private boolean bReg=false;//是否注册；
	private boolean bRecycled=false;//是否退出循环
	private SpeechUtil speaker ;
	
    public static synchronized LuckyMoneyDetailJob getLuckyMoneyDetailJob(Context context) {
    	
        if(current == null) {
            current = new LuckyMoneyDetailJob(context);
        }
        return current;
    }
    private LuckyMoneyDetailJob(Context context){
    	this.context = context;
    	bReg=Config.getConfig(context).getREG();
    	speaker=SpeechUtil.getSpeechUtil(context);
    }
    
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void recycle(AccessibilityNodeInfo info) {
    	if(bRecycled)return;
		if (info.getChildCount() == 0) {
			//取信息
			mReceiveInfo[mIntInfo]=info.getText().toString();

			//Log.i(TAG, "child widget----------------------------" + info.getClassName());
			//Log.i(TAG, "Text：" + info.getText());
			//Log.i(TAG, "windowId:" + info.getWindowId());
			if(mIntInfo==2){bRecycled=true;return;}
			mIntInfo=mIntInfo+1;
		} else {
			for (int i = 0; i < info.getChildCount(); i++) {
				if(info.getChild(i)!=null){
					recycle(info.getChild(i));
				}
			}
		}
	}
    private int getNN(int nn){
    	String s=String.valueOf(nn);
    	s=s.substring(s.length()-1,s.length());
    	int n=Integer.parseInt(s);
    	return n;
    }
    private void getValues(AccessibilityNodeInfo rootNode){
    	AccessibilityNodeInfo nodeInfo=AccessibilityHelper.findNodeInfosById(rootNode, "com.tencent.mm:id/cqr", 0);
    	if(nodeInfo!=null&&nodeInfo.getText()!=null)
    		mReceiveInfo[0]=nodeInfo.getText().toString();
    	nodeInfo=AccessibilityHelper.findNodeInfosById(rootNode, "com.tencent.mm:id/cqt", 0);
    	if(nodeInfo!=null&&nodeInfo.getText()!=null)
    		mReceiveInfo[1]=nodeInfo.getText().toString();
    	nodeInfo=AccessibilityHelper.findNodeInfosById(rootNode, "com.tencent.mm:id/cqv", 0);
    	if(nodeInfo!=null&&nodeInfo.getText()!=null)
    		mReceiveInfo[2]=nodeInfo.getText().toString();
    }
    public void LuckyMoneyDetailShow(AccessibilityNodeInfo rootNode){
    	mIntInfo=0;
    	//bRecycled=false;
    	//recycle(rootNode);
    	getValues(rootNode);
    	String sMoneyValue=mReceiveInfo[2];//红包金额：
    	String sMoneyFen=sMoneyValue.substring(sMoneyValue.length()-1,sMoneyValue.length());//分位
    	String sMoneyJao=sMoneyValue.substring(sMoneyValue.length()-2,sMoneyValue.length()-1);//角位
    	String sMoneyYuan=sMoneyValue.substring(sMoneyValue.length()-4,sMoneyValue.length()-3);//元位
    	int iMoneyFen=Integer.parseInt(sMoneyFen);
    	int iMoneyJao=Integer.parseInt(sMoneyJao);
    	int iMoneyYuan=Integer.parseInt(sMoneyYuan);
    	String sShow="";
    	int nn=1;
    	//获取牛牛玩法参数,判断抢到牛几：
    	int iNnWangFa=Config.getConfig(context).getNnWangFa();
    	//是否是试用版
    	boolean bReg=Config.getConfig(context).getREG();
    	switch(iNnWangFa){
    	case Config.NN_SINGLE:
    		nn=iMoneyFen;
    		break;
    	case Config.NN_DOUBLE:
    		nn=iMoneyFen+iMoneyJao;
    		break;
    	case Config.NN_THREE:
    		nn=iMoneyFen+iMoneyJao+iMoneyYuan;
    		break;
    	case Config.NN_HT:
    		nn=iMoneyFen+iMoneyYuan;
    		break;
    	case Config.NN_PG:
    		nn=iMoneyFen+iMoneyJao;
    		break;
    	case Config.NN_SG:
    		nn=iMoneyFen+iMoneyJao;
    		break;
    	}
    	nn=getNN(nn);
		if(nn>6&&bReg==false){
			sShow="恭喜！抢到牛"+nn+",牛牛透视成功！";
		}
		if(nn==0&&(iNnWangFa!=Config.NN_PG)){
			sShow="恭喜！抢到牛牛,牛牛透视成功！";
		}
		if(nn>0&&nn<7){
			if(bReg)
				sShow="";
			else
				sShow="抢到牛"+nn+",试用版不享受本次透视服务！请购买正版！";
		}
		if(!sShow.equals("")){
    		Toast.makeText(context,sShow, Toast.LENGTH_LONG).show();
    		speaker.speak(sShow);
		}
    }
}
