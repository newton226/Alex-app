package com.muslimu.alexcyber;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.net.wifi.WifiManager;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import java.util.List;

public class AlexAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Hapa ndipo Alex anaposoma screen ya WhatsApp au app yoyote background
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            // Mfumo wa kutafuta kitufe cha 'Send' na kukibonyeza kiotomatiki
            tafutaNaBonyezaSend(rootNode);
        }
    }

    // Amri kutoka kwenye Javascript (HTML) zitakuja hapa kuwasha mifumo
    public void tekelezaAmriYaSauti(String amri) {
        String amriSafi = amri.toLowerCase();
        
        if (amriSafi.contains("washa wifi") || amriSafi.contains("wifi on")) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) wifi.setWifiEnabled(true);
        } 
        else if (amriSafi.contains("zima wifi") || amriSafi.contains("wifi off")) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) wifi.setWifiEnabled(false);
        }
        else if (amriSafi.contains("washa taa") || amriSafi.contains("torch on")) {
            controlTorch(true);
        }
        else if (amriSafi.contains("zima taa") || amriSafi.contains("torch off")) {
            controlTorch(false);
        }
    }

    private void controlTorch(boolean status) {
        try {
            CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = camManager.getCameraIdList()[0];
            camManager.setTorchMode(cameraId, status);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void tafutaNaBonyezaSend(AccessibilityNodeInfo node) {
        // Algorithim ya kijasusi ya kutafuta kitufe cha kutuma cha WhatsApp bila ku-click mwenyewe
        if (node == null) return;
        if ("android.widget.ImageView".equals(node.getClassName()) && node.getContentDescription() != null) {
            if (node.getContentDescription().toString().toLowerCase().contains("send")) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            tafutaNaBonyezaSend(node.getChild(i));
        }
    }

    @Override
    public void onInterrupt() {}
}