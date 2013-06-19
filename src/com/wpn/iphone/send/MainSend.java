/**
 * MainSend.java
 * ��Ȩ����(C) 2012 
 * ����:cuiran 2012-07-24 11:31:35
 */
package com.wpn.iphone.send;

import java.util.ArrayList;
import java.util.List;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 * @author cuiran
 * @version TODO
 */
public class MainSend {
	private static Log log = LogFactory.getLog(MainSend.class.getName());
	
	 /************************************************
	 �������ͷ�������ַ��gateway.sandbox.push.apple.com /2195 
	 ��Ʒ���ͷ�������ַ��gateway.push.apple.com / 2195 

	��ҪjavaPNS_2.2.jar��

	 ***************************************************/
	/**

     *����һ���Ƚϼ򵥵����ͷ�����

     * apple�����ͷ���

     * @param tokens   iphone�ֻ���ȡ��token

     * @param path ������һ��.p12��ʽ���ļ�·������Ҫȥapple��������һ�� 

     * @param password  p12������ �˴�ע�⵼����֤�����벻��Ϊ����Ϊ������ᱨ��

     * @param message ������Ϣ������

     * @param count Ӧ��ͼ����С��Ȧ�ϵ���ֵ

     * @param sendCount ��������Ⱥ��  true������ false��Ⱥ��

     */

	public void sendpush(List<String> tokens,String path, String password, String message,Integer count,boolean sendCount,boolean isProductionPush) {

	try {
		//message��һ��json���ַ���{��aps��:{��alert��:��iphone���Ͳ��ԡ�}}

			PushNotificationPayload payLoad =  PushNotificationPayload.fromJSON(message);
			
			//payLoad.addAlert("�ƽ𵺶��������Ͳ���"); // ��Ϣ����			
			payLoad.addBadge(count); // iphoneӦ��ͼ����С��Ȧ�ϵ���ֵ			
			payLoad.addSound("default"); // ���� Ĭ��					

			PushNotificationManager pushManager = new PushNotificationManager();
			
			//true����ʾ���ǲ�Ʒ�������ͷ��� false����ʾ���ǲ�Ʒ�������ͷ���			
			pushManager.initializeConnection(new AppleNotificationServerBasicImpl(path, password, isProductionPush));			
			List<PushedNotification> notifications = new ArrayList<PushedNotification>(); 
			
			// ����push��Ϣ			
			if (sendCount) {			
				log.debug("--------------------------apple ���� ��-------");			
				Device device = new BasicDevice();			
				device.setToken(tokens.get(0));			
				PushedNotification notification = pushManager.sendNotification(device, payLoad, true);			
				notifications.add(notification);			
			} 
			else {			
				log.debug("--------------------------apple ���� Ⱥ-------");			
				List<Device> device = new ArrayList<Device>();			
				for (String token : tokens) {			
				device.add(new BasicDevice(token));			
			}
			
			notifications = pushManager.sendNotifications(payLoad, device);			
			}
			
			List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);			
			List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
			
			int failed = failedNotifications.size();			
			int successful = successfulNotifications.size();			 
			
			if (successful > 0 && failed == 0) {			
				log.debug("-----All notifications pushed �ɹ� (" + successfulNotifications.size() + "):");			
			} 
			else if (successful == 0 && failed > 0) {			
				log.debug("-----All notifications ʧ�� (" + failedNotifications.size() + "):");			
			} 
			else if (successful == 0 && failed == 0) {			
				System.out.println("No notifications could be sent, probably because of a critical error");			
			} 
			else {			
				log.debug("------Some notifications ʧ�� (" + failedNotifications.size() + "):");			
				log.debug("------Others �ɹ� (" + successfulNotifications.size() + "):");			
			}
		// pushManager.stopConnection();
		} 
		catch (Exception e) {	
			e.printStackTrace();	
		}
	}	
	

	/**
	 * TODO
	 * @param args
	 */
	public static void main(String[] args) 
	{		
		PushConfigMgr mgr = new PushConfigMgr();		
		String path = "resource/config.json";
		if(!mgr.loadConfig(path))
			return;
		
		MainSend send=new MainSend();
		send.sendpush(mgr.m_deviceTokens, mgr.m_strKeystore, mgr.m_strPassword, mgr.m_strPushMsg, 1, false,mgr.m_isProduction);		
	}
}
