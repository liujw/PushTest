package com.wpn.iphone.send;

import java.io.File;  
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;

//package com.json;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PushConfigMgr 
{
	boolean m_isProduction;
	boolean m_isPushUpdateMsg;
	String m_strKeystore;
	String m_strPassword;
	String m_strPushMsg;
	List<String> m_deviceTokens; 

	public boolean loadConfig(String strConfigFile)
	{
		StringBuffer buffer = new StringBuffer();
		
		try{
			File configFile = new File(strConfigFile);
			FileInputStream inFile = new FileInputStream(configFile); 	
			readToBuffer(buffer,inFile);
		} 
		catch (Exception e) {	
			e.printStackTrace();
			return false;
		}
			
		JSONObject jo = JSONObject.fromObject(buffer.toString());
		m_isProduction = jo.getString("IsProductionPush").equals("TRUE");		
		m_isPushUpdateMsg = jo.getString("IsPushUpdateMsg").equals("TRUE");		
		m_strKeystore = m_isProduction ? jo.getString("ProductionKeystore") : jo.getString("DevelopmentKeystore");
		m_strPassword = jo.getString("Password");
		m_strPushMsg = m_isPushUpdateMsg ? jo.getString("PushMsgUpdate") : jo.getString("PushMsgInfo");
		
		JSONArray arrayTokens=jo.getJSONArray("DeviceTokens"); 
		m_deviceTokens = new ArrayList<String>();

		for(int i=0;i< arrayTokens.size();i++){
			JSONObject joToken  = (JSONObject)JSONObject.toBean((JSONArray.fromObject(arrayTokens.toString()).getJSONObject(i)),JSONObject.class);
			String token = joToken.getString("Token");
			m_deviceTokens.add(token); 
		}
				
		return true;
	}
	
	public void readToBuffer(StringBuffer buffer, InputStream is)
        throws IOException {
        String line;        // ��������ÿ�ж�ȡ������
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();       // ��ȡ��һ��
        while (line != null) {          // ��� line Ϊ��˵��������
            buffer.append(line);        // ��������������ӵ� buffer ��
            buffer.append("\n");        // ��ӻ��з�
            line = reader.readLine();   // ��ȡ��һ��
        }
    }
}