package org.occ.back.common.exception;
import org.springframework.dao.DataAccessException;

/**
 * 
 * Description: 自定义Service层异常信息
 * All rights Reserved, Designed ByBeLLE
 * Copyright:   Copyright(C) 2014-2015
 * Company:     Wonhigh.
 * author:      wugy
 * Createdate:  2015-3-3下午2:23:15
 */
public class ServiceException extends DataAccessException{

	    private static final long serialVersionUID = 1L;

	    public ServiceException(String msg) {
	        super(msg);
	    }

	    public ServiceException(String msg, Throwable cause) {
	        super(msg, cause);
	    }
	}