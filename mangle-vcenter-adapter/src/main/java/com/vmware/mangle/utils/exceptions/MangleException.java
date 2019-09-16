/*
 * Copyright (c) 2016-2019 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright notices
 * and license terms. Your use of these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package com.vmware.mangle.utils.exceptions;

import lombok.Getter;

/**
 * @author bkaranam
 *
 *         Defines Mangle Exception, that is subclassed to generate specific
 *         types of exceptions
 */
@Getter
public class MangleException extends Exception {
    private static final long serialVersionUID = 1L;
    private String errorCode;
    private String errorMsg;

    public MangleException() {
        super();
    }

    public MangleException(String msg) {
        super(msg);
    }

    public MangleException(String msg, Throwable e) {
        super(msg, e);
    }

    public MangleException(Exception e) {
        super(e);
    }

    public MangleException(ErrorCodes code) {
        super("Mangle error code: " + code.getId() + " with message: " + code.getMsg());
        this.errorMsg = code.getMsg();
        this.errorCode = code.getId();
    }
}