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

package com.vmware.mangle.cassandra.model.faults.specs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.vmware.mangle.services.enums.BytemanFaultType;

/**
 * @author bkaranam
 *
 *
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class KillJavaThreadFaultSpec extends JVMCodeLevelFaultSpec {
    private static final long serialVersionUID = 1L;

    public KillJavaThreadFaultSpec() {
        setFaultType(BytemanFaultType.KILL_THREAD.toString());
        setFaultName(BytemanFaultType.KILL_THREAD.toString());
        setSpecType(this.getClass().getName());
    }
}
