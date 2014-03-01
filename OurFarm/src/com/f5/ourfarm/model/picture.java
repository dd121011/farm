/**
 * 
 */
package com.f5.ourfarm.model;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * @author tianhao
 *
 */
public class picture implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String url;
    
    private Bitmap bitmap;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    
    

}
