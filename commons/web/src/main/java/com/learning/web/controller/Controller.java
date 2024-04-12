package com.learning.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制器基类
 */
public abstract class Controller {
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    public Controller() {
    }
}
