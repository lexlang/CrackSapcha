package com.lexlang.CrackSapcha.WuBa;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lexlang.CrackSapcha.Util.Traces;
import com.lexlang.CrackSapcha.Util.Util;
import com.lexlang.ImageUtil.CommonUtil;
import com.lexlang.ImageUtil.cut.ReEdge;
import com.lexlang.Requests.header.HeaderConfig;
import com.lexlang.Requests.javascript.JavaScript;
import com.lexlang.Requests.requests.HtmlUnitRequests;
import com.lexlang.Requests.responses.Response;

public class WuBaVSlice {
	
	private static final Random RAND=new Random();
	
	private static JSONObject traces=Traces.getTraces();
	
	public void slice(String detailUrl) throws Exception{
		HtmlUnitRequests requests=new HtmlUnitRequests();
		requests.setJavaScriptEnabled(false);
		
		Response aResp = requests.get(detailUrl);
		System.out.println(requests.getCurrentUrl());
		Map<String,String> stores=getStore(requests.getCurrentUrl());
		
		Map<String, String> hd = HeaderConfig.postBuilder()
				                             .setHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
				                             .setXRequestWith()
				                             .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0")
				                             .setReferer(requests.getCurrentUrl())
				                             .build();
		
		//finger
		Response eResp = requests.getUseHeader("https://cdata.58.com/fpToken?"
				+ "callback=dpjsonp_08524849"+System.currentTimeMillis(), hd);
		JSONObject eRes= filterObject(eResp.getContent());
		System.out.println(eRes.toJSONString());
		String token=eRes.getString("token");
		
		//
		Response bResp = requests.postUseHeader("https://callback.58.com/antibot/codev2/getsession.do?"+System.currentTimeMillis(),
												   "serialId="+stores.get("serialId")
												+ "&code="+stores.get("code")
												+ "&sign="+stores.get("sign")
												+ "&url="+URLEncoder.encode(detailUrl, "utf-8")
												+ "&namespace=infodetailweb", hd);
		JSONObject bRes = bResp.getJsonObject();
		String sessionId=bRes.getJSONObject("data").getString("sessionId");
		
		Response cResp = requests.getUseHeader("https://verifycode.58.com/captcha/getV3?"
				+ "callback=jQuery1101010176564743880778_"+System.currentTimeMillis()
				+ "&showType=win"
				+ "&sessionId="+sessionId
				+ "&_="+System.currentTimeMillis(), hd);
		JSONObject cRes= filterObject(cResp.getContent());
		System.out.println(cRes.toJSONString());
		
		hd.put("Accept", "image/webp,*/*");
		
		if(cRes.toJSONString().contains("请点击并将滑块拖动到指定位置")){
			System.out.println("滑块验证码");
			String imgUrl="https://verifycode.58.com"+cRes.getJSONObject("data").getString("bgImgUrl");
			Response dResponse = requests.getUseHeader(imgUrl, hd);
			String imgBarUrl="https://verifycode.58.com"+cRes.getJSONObject("data").getString("puzzleImgUrl");
			Response fResponse = requests.getUseHeader(imgBarUrl, hd);
			//dResponse.writeToLocal(System.currentTimeMillis()+".png");
			int distance=Util.filterImage(dResponse.getImage(),fResponse.getImage());
			System.out.println(distance);
			
			//xxzlfingertoken
			String traces=getData(distance);
			System.out.println(traces);
			JSONObject dataRes=makeResSlice(token,traces);
			System.out.println(dataRes.toJSONString());
			System.out.println(cRes.getJSONObject("data").getString("responseId"));
			String data = new JavaScript(FileUtils.readFileToString(new File("CryptoJS.js"))).invokeFunction("DecryptInner", dataRes.toJSONString()
					,cRes.getJSONObject("data").getString("responseId"));
			
			System.out.println(data);
			//校验
			
			System.out.println("等待三秒");
			Thread.sleep(4000);
			
			Response gResp = requests.getUseHeader("https://verifycode.58.com/captcha/checkV3?"
					+ "callback=jQuery1101022707700422443566_"+System.currentTimeMillis()
					+ "&responseId="+cRes.getJSONObject("data").getString("responseId")
					+ "&sessionId="+sessionId
					+ "&data="+data
					+ "&_="+System.currentTimeMillis(), hd);
			JSONObject gRes= filterObject(gResp.getContent());
			System.out.println(gRes.toJSONString());
			
		}else{
			String imgUrl="https://verifycode.58.com"+cRes.getJSONObject("data").getString("bgImgUrl");
			Response dResp = requests.getUseHeader(imgUrl, hd);
			//dResp.writeToLocal("crawl/z_"+System.currentTimeMillis()+".png");
			BufferedImage img = dResp.getImage();
			
			
			//"23,73,0|23,73,53|26,73,83|39,73,99|64,74,117|91,73,133|117,71,150|148,70,166|207,68,182|239,66,199|262,66,216|276,66,234|281,67,251|282,67,266|282,67,446|"
			//轨迹
			// s.AESEncryption('{"x":"' + (e || 0) + '","g":"' + r + '","p":"' + o.pj + '","finger":"' + (t.xxzlfingertoken ? t.xxzlfingertoken : "") + '"}');
	        // this.AESEncryption = function(e) { var t = CryptoJS.enc.Utf8.parse(o.responseId.substr(0, 16));return CryptoJS.AES.encrypt(e, t, {iv: t,mode: CryptoJS.mode.CBC,padding: CryptoJS.pad.Pkcs7}).ciphertext.toString().toUpperCase()}
			
			//String traces="27,51,0|27,51,14|29,52,168|30,52,182|35,55,198|38,56,214|40,58,230|42,57,247|43,58,264|45,59,283|48,60,297|49,60,314|50,60,331|51,60,347|53,62,363|55,63,379|59,64,396|61,64,413|63,65,430|67,67,446|69,68,463|75,68,480|78,69,497|80,71,513|81,71,530|82,71,566|83,71,581|84,72,597|85,72,614|86,72,629|86,72,682|87,73,697|87,73,713|88,74,731|89,75,746|91,76,763|93,77,779|94,77,796|96,79,813|98,80,830|99,80,847|100,81,863|103,81,880|105,81,897|107,81,914|111,81,930|114,80,947|116,81,962|118,80,980|120,79,996|124,78,1013|128,75,1029|131,72,1046|133,71,1063|134,70,1080|134,69,1118|135,69,1129|135,68,1146|137,67,1164|138,66,1233|138,65,1246|139,65,1316|139,65,1332|141,65,1347|143,65,1362|145,64,1379|148,65,1396|149,64,1414|150,64,1453|151,64,1482|151,64,1502|152,65,1514|153,64,1530|155,64,1547|157,64,1563|158,65,1580|159,65,1616|159,66,1630|160,67,1645|161,66,1663|162,67,1679|163,68,1742|165,68,1780|165,69,1803|166,70,1813|167,71,1829|168,72,1846|169,72,1863|169,72,1888|170,73,1898|171,74,1913|173,76,1930|174,77,1946|175,77,1963|175,78,1980|175,78,1996|177,80,2013|179,80,2030|181,82,2047|182,83,2062|183,84,2080|183,85,2097|184,87,2113|185,87,2130|187,88,2146|187,89,2163|189,91,2179|190,92,2196|191,94,2214|191,94,2229|192,95,2247|193,96,2263|193,98,2280|195,100,2296|196,103,2313|196,104,2331|198,107,2346|199,108,2362|199,109,2379|201,112,2395|201,112,2413|202,112,2455|203,113,2468|203,113,2481|204,114,2498|205,115,2514|205,116,2531|206,116,2579|207,116,2595|207,116,2613|208,116,2629|210,117,2692|211,117,2712|212,117,2728|214,117,2746|214,118,2789|215,118,2807|216,118,2815|216,119,2830|217,119,2861|219,120,2880|220,120,2896|222,120,2914|223,120,2982|223,120,3164|224,120,3482|224,121,3497|227,122,3513|227,122,3863|";
			String traces=Util.traceOcr(img,5,"127.0.0.1", 80);
			System.out.println(traces);
			//ImageIO.write(, "PNG", new File("crawl/z_"+System.currentTimeMillis()+".png"));
			JSONObject dataRes=makeResTraces(token,traces);
			System.out.println(dataRes.toJSONString());
			System.out.println(cRes.getJSONObject("data").getString("responseId"));
			String data = new JavaScript(FileUtils.readFileToString(new File("CryptoJS.js"))).invokeFunction("DecryptInner", dataRes.toJSONString()
					,cRes.getJSONObject("data").getString("responseId"));
			
			System.out.println(data);
			//校验
			System.out.println("等待三秒");
			//Thread.sleep(4000);
			
			Response gResp = requests.getUseHeader("https://verifycode.58.com/captcha/checkV3?"
					+ "callback=jQuery1101022707700422443566_"+System.currentTimeMillis()
					+ "&responseId="+cRes.getJSONObject("data").getString("responseId")
					+ "&sessionId="+sessionId
					+ "&data="+data
					+ "&_="+System.currentTimeMillis(), hd);
			JSONObject gRes= filterObject(gResp.getContent());
			System.out.println(gRes.toJSONString());
			throw new RuntimeException("");
		}
		
	}
	
	public JSONObject makeResSlice(String token,String traces){
		JSONObject res=new JSONObject();
		String[] ts = traces.split("\\|");
		res.put("x", Integer.parseInt(ts[ts.length-1].split(",")[0]));
		res.put("p", "0,0");
		res.put("track", traces);
		res.put("finger", token);
		return res;
	}
	
	public JSONObject makeResTraces(String token,String traces){
		JSONObject res=new JSONObject();
		String[] ts = traces.split("\\|");
		res.put("x", Integer.parseInt(ts[ts.length-1].split(",")[1]));
		res.put("p", "0,0");
		res.put("g", traces);
		res.put("finger", token);
		return res;
	}
	
	
	public BufferedImage scaleImg(BufferedImage img){
		return CommonUtil.scaleImage(img, img.getWidth()/4, img.getHeight()/4);
	}
	
	public BufferedImage filterImg(BufferedImage img) throws Exception{
		return new ReEdge().reImage(img, 150, 39);
	}
	
	public JSONObject filterObject(String html){
		return JSONObject.parseObject(html.replaceAll(".+?\\(", "").replace("})", "}"));
	}
	
	public Map<String,String> getStore(String src){
		Map<String,String> store=new HashMap<String,String>();
		String[] arr = src.split("\\?")[1].split("&");
		for(String vals:arr){
			String[] vs = vals.split("=");
			if(vs.length==2){
				store.put(vs[0], vs[1]);
			}
		}
		return store;
	}
	
	public String getData(int distance){
		long last=Math.round(distance*0.626-9.145);
		System.out.println("last:"+last);
		String radio=Math.round(last*1.0/240*190)+"";
		System.out.println("radio:"+radio);
    	JSONArray ts = traces.getJSONArray(radio);
    	JSONArray css = JSONArray.parseArray(ts.getString(RAND.nextInt(ts.size())).replace("};{", "],[")
    			.replace("};", "]]").replace("{", "[["));
    	StringBuilder sb=new StringBuilder();
    	for(int ind=2;ind<css.size();ind++){
    		JSONArray cs = css.getJSONArray(ind);
    		sb.append(Math.round(cs.getInteger(0)*1.0/190*240)+","+(cs.getIntValue(1)+20)+","+cs.getIntValue(2)+"|");
    	}
		return sb.toString();
	}
	
	
	public static void main(String[] args){
		for(int ind=0;ind<10;ind++){
			try{
				new WuBaVSlice().slice("https://bazhou.58.com/jiazhengbaojiexin/41767766942726x.shtml");
			}catch(Exception ex){}
		}
		
	}
	
}
