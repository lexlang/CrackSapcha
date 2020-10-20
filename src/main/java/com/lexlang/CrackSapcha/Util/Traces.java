package com.lexlang.CrackSapcha.Util;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Traces {

	public static JSONObject getTraces(){
		JSONObject traces=null;
		try {
			traces = JSONObject.parseObject(FileUtils.readFileToString(new File("slice.json")));
		} catch (IOException e) {}
		return traces;
	}

}
