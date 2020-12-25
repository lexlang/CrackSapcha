package com.lexlang.CrackSapcha.dun;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lexlang.CrackSapcha.Util.Traces;
import com.lexlang.CrackSapcha.Util.Util;
import com.lexlang.Requests.header.HeaderConfig;
import com.lexlang.Requests.javascript.JavaScript;
import com.lexlang.Requests.requests.HtmlUnitRequests;
import com.lexlang.Requests.responses.Response;

public class DunSlice {
	private static final Random RAND=new Random();
	private static JSONObject traces=Traces.getTraces();
	
	public void slice(String id,String referer) throws Exception{
		HtmlUnitRequests requests =new HtmlUnitRequests();
		requests.setJavaScriptEnabled(false);
		
		Map<String, String> hd = HeaderConfig.getBuilder()
									        .setAccept("*/*")
									        .setReferer(referer)
									        .setHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2")
									        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:61.0) Gecko/20100101 Firefox/61.0")
									        .setHeader("Accept-Encoding", "gzip, deflate")
									        .setHeader("Connection", "keep-alive")
									        .build();
		
		Response bResponse = requests.getUseHeader("https://c.dun.163.com/api/v2/getconf?"
				+ "id="+id
				+ "&ipv6=false&runEnv=10"
				+ "&referer="+URLEncoder.encode(referer, "utf-8")
				+ "&type=2&loadVersion=2.2.0&callback=__JSONP_"+getStringRandom(6)+"_0", hd);
		
		JSONObject bResults = filterObject(bResponse.getContent().replace(";", ""));
		
		JavaScript encrypyed= new JavaScript(FileUtils.readFileToString(new File("file/dun/DunEncrypyed.js")));
		JavaScript DunFinger= new JavaScript(FileUtils.readFileToString(new File("file/dun/DunFinger.js")));
		
		String fp=DunFinger.invokeFunction("getFP", "ssfw.gdcourts.gov.cn");
		System.out.println(fp);
		
		String cb=encrypyed.invokeFunction("B", UUID.randomUUID().toString().replace("-", ""));
		System.out.println(cb);
		
		Response aResponse = requests.getUseHeader("https://c.dun.163.com/api/v2/get?"
				+ "id="+id
				+ "&fp="+URLEncoder.encode(fp, "utf-8")
				+ "&https=true"
				+ "&type=2&version=2.14.1&dpr=1&dev=1"
				+ "&cb="+URLEncoder.encode(cb, "utf-8")
				+ "&ipv6=false"
				+ "&runEnv=10&group=&scene=&width=320&token="
				+ "&referer="+URLEncoder.encode(referer, "utf-8")
				+ "&callback=__JSONP_"+getStringRandom(6)+"_1", hd);
		
		JSONObject aResults = filterObject(aResponse.getContent().replace(";", ""));
		System.out.println(aResults.toJSONString());
		
		//下载图片
		String imgUrl=aResults.getJSONObject("data").getJSONArray("bg").getString(0);
		
		Response dResponse = requests.getUseHeader(imgUrl, hd);
		dResponse.writeToLocal("img.png");
		String imgBarUrl=aResults.getJSONObject("data").getJSONArray("front").getString(0);
		Response fResponse = requests.getUseHeader(imgBarUrl, hd);
		
		int distance=Util.filterImage(dResponse.getImage(),fResponse.getImage())-5;
		System.out.println("distance:"+distance);
		
		String cbb=encrypyed.invokeFunction("B", UUID.randomUUID().toString().replace("-", ""));
		
		hd.remove("Content-type");
		hd.put("Host", "c.dun.163.com");
		hd.put("Accept", "*/*");
		Thread.sleep(3000);
		
		String checkUrl="https://c.dun.163.com/api/v2/check?"
				+ "id="+id
				+ "&token="+aResults.getJSONObject("data").getString("token")
				+ "&acToken="
				+ "&data="+URLEncoder.encode(getData(encrypyed,aResults.getJSONObject("data").getString("token"),distance), "utf-8")
				+"&width=320&type=2&version=2.14.2"
				+ "&cb="+URLEncoder.encode(cbb, "utf-8")
				+ "&extraData=&runEnv=10"
				+ "&referer="+URLEncoder.encode(referer, "utf-8")
				+ "&callback=__JSONP_"+getStringRandom(6)+"_2";
		
		System.out.println(checkUrl);
	
		System.out.println(requests.getCookie());
		
		Response cResponse = requests.getUseHeader(checkUrl, hd);
		
		System.out.println(cResponse.getContent());
		
	}
	
	public String getData(JavaScript encrypyed,String token,int distance) throws IOException, NoSuchMethodException, ScriptException, InterruptedException{
		JSONObject data=new JSONObject(new LinkedHashMap());
		data.put("d", encrypyed.invokeFunction("B",simulationTraces(encrypyed,token,distance)));//最后五十条轨迹
		data.put("m","");
		String radio=distance*1.0/320*100+"";
		System.out.println(radio);
		data.put("p", encrypyed.invokeFunction("B",encrypyed.invokeFunction("n",token,radio)));//长度
		data.put("ext",encrypyed.invokeFunction("B",encrypyed.invokeFunction("n", token,1+","+(50+RAND.nextInt(20)))));
		System.out.println(data.toJSONString());
		return data.toJSONString();
	}
	
	public JSONArray choiceTrace(int last){
		String radio=Math.round(last*1.0/280*190)+"";
    	JSONArray ts = traces.getJSONArray(radio);
    	JSONArray css = JSONArray.parseArray(ts.getString(RAND.nextInt(ts.size())).replace("};{", "],[")
    			.replace("};", "]]").replace("{", "[["));
    	return css;
	}
	
	public String simulationTraces(JavaScript encrypyed,String token,int distance) throws NoSuchMethodException, ScriptException, IOException, InterruptedException{
		JSONArray slice =choiceTrace(50+RAND.nextInt(100));
		slice.remove(0);slice.remove(0);
		System.out.println(slice.toJSONString());
		double dis=(distance+10)*1.0 / slice.getJSONArray(slice.size()-1).getIntValue(0);
		
		int t=50;
		if(slice.size()>t){
			int reject=slice.size()/(slice.size()-50);
			int n=slice.size();
			
			JSONArray res = new JSONArray();
			for(int r=0,o=0;o<n;o++){
				if(o>= r*(n-1)/(t-1)){
					JSONArray item = slice.getJSONArray(o);
					item.set(0, (int) Math.floor(item.getIntValue(0)*dis));
					res.add(item);
					r++;
				}
			}
			slice=res;
		}else{
			JSONArray res = new JSONArray();
			for(int i=0;i<slice.size();i++){
				JSONArray item = slice.getJSONArray(i);
				item.set(0, (int) Math.floor(item.getIntValue(0)*dis));
				res.add(item);
			}
			slice=res;
		}
		t=slice.getJSONArray(slice.size()-1).getIntValue(2)-40;
		System.out.println(slice.toJSONString());
		//int startTime=300+RAND.nextInt(200);
		StringBuilder sb=new StringBuilder();
		for(int index=0;index<slice.size();index++){
			JSONArray item = slice.getJSONArray(index);
			if(sb.toString().length()==0){
				sb.append(clickTrace(encrypyed,token,((int)(item.getInteger(0)*dis))+","+item.getInteger(1),item.getInteger(2)));
			}else{
				sb.append(":"+clickTrace(encrypyed,token,((int)(item.getInteger(0)*dis))+","+item.getInteger(1),item.getInteger(2)));
			}
		}
		//Thread.sleep(1500);
		System.out.println(sb.toString());
		//return sb.toString();
		return sb.toString();
	}
	
	public String clickTrace(JavaScript encrypyed,String token,String local,int t) throws NoSuchMethodException, ScriptException, IOException{
		return encrypyed.invokeFunction("n", token,local+","+t);
	}
	

	
	
	public JSONObject filterObject(String html){
		return JSONObject.parseObject(html.replaceAll(".+?\\(", "").replace("})", "}"));
	}
	
    public String getStringRandom(int length) {
        String val = "";
        Random random = new Random();
        
        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {
            
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val.toLowerCase();
    }
	
	public static void main(String[] args) throws Exception {
		new DunSlice().slice("64bc091582fd4bd2917cd1d86f8d7eed", "https://ssfw.gdcourts.gov.cn/web/loginA");
	}

}
