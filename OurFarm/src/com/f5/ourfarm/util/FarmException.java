
package com.f5.ourfarm.util;


/**
 * 异常包装类
 * 
 * @author lify
 *
 */
public class FarmException extends Exception {

	private static final long serialVersionUID = 4853273124187194466L;
	private int statusCode = -1;
	
    public FarmException(String msg) {
        super(msg);
    }

    public FarmException(Exception cause) {
        super(cause);
    }

    public FarmException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public FarmException(String msg, Exception cause) {
        super(msg, cause);
    }

    public FarmException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
    
	public FarmException() {
		super(); 
	}

	public FarmException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public FarmException(Throwable throwable) {
		super(throwable);
	}

	public FarmException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	
	/*
	 * 1000：应用系统内部异常.
	 * 2000：网络异常
	 * 3000：
	 * 4000：
	*/
}
