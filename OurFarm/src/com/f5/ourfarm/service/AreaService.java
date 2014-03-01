package com.f5.ourfarm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.f5.ourfarm.model.Region;

/**
 * 区域service
 * 
 * @author lify
 *
 */
public class AreaService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * 获取区域
     * 
     * @return
     */
    public List<Map<String, Object>> getAllRegionForListView() {
    	return Region.getAllRegionForListView(Region.getRegions());
    }
    
}
