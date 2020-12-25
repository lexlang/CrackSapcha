package com.lexlang.CrackSapcha.Util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lexlang.ImageUtil.CommonUtil;
import com.lexlang.ImageUtil.cut.ReEdge;
import com.lexlang.ImageUtil.util.Base64Util;
import com.lexlang.Requests.header.HeaderConfig;
import com.lexlang.Requests.requests.HttpClientRequests;
import com.lexlang.Requests.responses.Response;

public class Util {
	private static final Random RAND=new Random();
	/**
	 * 手势验证码识别
	 * @param image
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String traceOcr(BufferedImage image,int offy,String host,int port) throws IOException, URISyntaxException{
		JSONArray recog=JSONArray.parseArray(localOcr(image,host,port));
		BufferedImage img=traceImg(image,recog);
		int w = img.getWidth();
		int h = img.getHeight();
		JSONArray res=new JSONArray();
		int t=0;
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				if(new Color(img.getRGB(x, y)).getRed()<50){
					JSONArray item = new JSONArray();
					item.add(x);item.add(y+offy);item.add(t);
					res.add(item);
					t=t+RAND.nextInt(20)+10;
					break;
				}
			}
		}
		return res.toJSONString().replace("],[", "|").replace("[[", "").replace("]]", "|");
	}
	
	public static BufferedImage traceImg(BufferedImage orgImg,JSONArray recog){
		BufferedImage bi = new BufferedImage(256, 128, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();     
        //白色背景
        g2.setColor(Color.WHITE);     
        g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        for(int i=0;i<recog.size();i++){//y
        	JSONArray item = recog.getJSONArray(i);
        	for(int j=0;j<item.size();j++){
        		if(item.getIntValue(j)>0){
        			bi.setRGB(j, i, Color.BLACK.getRGB());
        		}
        	}
        }
        g2.dispose();
		return CommonUtil.scaleImage(bi, 280, 158);
	}
	
	public static int filterImage(BufferedImage img,BufferedImage imgBar) throws Exception{
		int widthBar=imgBar.getWidth();
		int heightBar=imgBar.getHeight();
		int minHight=300;int maxHight=0;
		for(int j=0;j<heightBar;j++){
			for(int i=0;i<widthBar;i++){
				int dip=imgBar.getRGB(i, j) >> 24;
				if(dip!=0){
					if(minHight>j){minHight=j;}
					if(maxHight<j){maxHight=j;}
					break;
				}
			}
		}
		
		BufferedImage imgCut = img.getSubimage(0, minHight, img.getWidth(),  maxHight-minHight);
		BufferedImage finalImg = new ReEdge().reImage(imgCut,150,20);
		//ImageIO.write(finalImg, "png", new File("123.png"));
		
		if(finalImg.getWidth()>400){
			//58模块
			for(int j=10;j<=400;j++){//长度
				
				int diffC=0;
				for(int i=8;i<=72 && i<=finalImg.getHeight()-3;i++){//垂直方向的点数
					if(diffColor(finalImg.getRGB(j-1, i),finalImg.getRGB(j, i),finalImg.getRGB(j+1, i))){
						diffC++;
					}
				}

				if(diffC>=32){
					int aDiff=0;
					for(int i=0;i<=72;i++){
						if(diffColor(finalImg.getRGB(j+i, 7),finalImg.getRGB(j+i, 8),finalImg.getRGB(j+i, 9))){
							aDiff++;
						}
					}
					if(aDiff>36){
						return j;
					}
					int bDiff=0;
					for(int i=0;i<=68;i++){
						if(diffColor(finalImg.getRGB(j+i,10),finalImg.getRGB(j+i, 11),finalImg.getRGB(j+i, 12))){
							bDiff++;
						}
					}
					if(bDiff>=34){
						return j;
					}
				}
			}
		}else{
			//dun模块
			//检测
			for(int j=10;j<=270;j++){//长度
				int diffC=0;
				for(int i=14;i<=26;i++){//宽度
					if(diffColor(finalImg.getRGB(j-1, i),finalImg.getRGB(j, i),finalImg.getRGB(j+1, i))){
						diffC++;
					}
					if(diffColor(finalImg.getRGB(j-1+1, i),finalImg.getRGB(j+1, i),finalImg.getRGB(j+1+1, i))){
						diffC++;
					}
				}
				if(diffC>=8){
					int aDiff=0;
					for(int i=0;i<=12;i++){
						if(diffColor(finalImg.getRGB(j+i, 14),finalImg.getRGB(j+i, 15),finalImg.getRGB(j+i, 16))){
							aDiff++;
						}
					}
					if(aDiff>=8){
						return j+6;
					}
					int bDiff=0;
					for(int i=0;i<=12;i++){
						if(diffColor(finalImg.getRGB(j+i, 13),finalImg.getRGB(j+i, 14),finalImg.getRGB(j+i, 15))){
							bDiff++;
						}
					}
					if(bDiff>=8){
						return j+6;
					}
				}
			}
		}
		
		return 150;
	}
	
	public static boolean diffColor(int aRgb,int bRgb,int cRgb){
		if(aRgb==cRgb && aRgb!=bRgb){
			if(bRgb!=-1){
				return true;
			}
		}
		return false;
	}
	
	public static int colorDiff(int aRgb,int bRgb){
		 int r = Math.abs(((aRgb & 0xff0000) >> 16)-(bRgb & 0xff0000) >> 16);
         int g = Math.abs(((aRgb & 0xff00) >> 8)-((bRgb & 0xff00) >> 8));
         int b = Math.abs(((aRgb & 0xff))-(bRgb & 0xff)) ;
         return r+g+b;
	}
	
	public static String localOcr(BufferedImage img,String ip,int port) throws IOException, URISyntaxException{
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
		ImageIO.write(img, "jpg", os);  
		String imgString=Base64Util.encode(os.toByteArray());
		HttpClientRequests  requests = new HttpClientRequests();
		Map<String, String> headerMap = new HeaderConfig().postBuilder().setContentType(HeaderConfig.POSTFORM).build();
		Response response =requests.postUseHeader("http://"+ip+":"+port+"/localOcr", "image="+imgString, headerMap);
		return response.getContent().replace("_", "");
	}
	
	/**
	 * 
	 * @param image     给的图片
	 * @param itemRecog 给的文字
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String[] recogLocationStr(BufferedImage image,String itemRecog) throws IOException, URISyntaxException{
		String recog=localOcr(image,"localhost",8080); //114.116.246.246
		JSONArray orderItem =new JSONArray();
		for(int index=0;index<itemRecog.length();index++){
			String t=itemRecog.charAt(index)+"";
			JSONArray item =new JSONArray();
			item.add(t);
			orderItem.add(item);
		}
		//System.out.println(orderItem.toJSONString());
		//System.out.println(recog);
		JSONObject sortKeys=JSONObject.parseObject(recog);
		String[] locations=new String[orderItem.size()];
		//第一个字相同的
		HashSet<String> keysRemove=new HashSet<String>();
		for(int index=0;index<orderItem.size();index++){
			String check=orderItem.getJSONArray(index).getString(0);
			for(String key:sortKeys.keySet()){
				JSONObject item = sortKeys.getJSONObject(key);
				if(! keysRemove.contains(key) && (item.getJSONArray("ocr").getString(0).equals(check)
						|| item.getJSONArray("ocr").getString(1).equals(check)
						|| item.getJSONArray("ocr").getString(2).equals(check))){
					JSONArray local = item.getJSONArray("local");
					locations[index]=((local.getInteger(2)+local.getInteger(0))/2+",")+((local.getInteger(3)+local.getInteger(1))/2+"");
					//sortKeys.remove(key);
					keysRemove.add(key);
					break;
				}
			}
		}
		
		if(! checkNull(locations)){
			return locations;
		}
		
		//随机一个值
		for(int index=0;index<orderItem.size();index++){
			if(locations[index]!=null){
				continue;
			}
			for(String key:sortKeys.keySet()){
				if(! keysRemove.contains(key) ){
					JSONObject item = sortKeys.getJSONObject(key);
					JSONArray local = item.getJSONArray("local");
					locations[index]=((local.getInteger(2)+local.getInteger(0))/2+",")+((local.getInteger(3)+local.getInteger(1))/2+"");
					keysRemove.add(key);
					break;
				}
			}
		}
		
		return locations;
	}
	
	
	private static boolean checkNull(String[] locations){
		for(int index=0;index<locations.length;index++){
			if(locations[index]==null){
				return true;
			}
		}
		return false;
	}
	
	
}
