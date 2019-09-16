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

package com.vmware.mangle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author chetanc
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VMDisk implements VCenterVMObject {
    private String type;
    private Backing backing;

    /**
     * @author chetanc
     *
     */
    @Data
    public class Backing {
        private String type;
        private String vmdk_file;
    }
}
