package com.ymjrhk.rbac.exception;

/**
 * 写入历史失败
 */
public class HistoryInsertFailedException extends BaseException{

    public HistoryInsertFailedException() {
    }

    public HistoryInsertFailedException(String msg){
        super(msg);
    }
}
