package com.example.lenovo.murphysl.event;

import com.baidu.location.BDLocation;

/**
 * LocationEvent
 *
 * @author: lenovo
 * @time: 2016/8/19 16:02
 */

public class LocationEvent {

    private BDLocation locData;

    public LocationEvent(BDLocation locData) {
        this.locData = locData;
    }

    public BDLocation getLocData() {
        return locData;
    }
}
