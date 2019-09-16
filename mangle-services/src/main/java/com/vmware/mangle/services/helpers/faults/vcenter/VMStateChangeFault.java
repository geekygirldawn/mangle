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

package com.vmware.mangle.services.helpers.faults.vcenter;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vmware.mangle.cassandra.model.faults.specs.VMFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.VMStateFaultSpec;
import com.vmware.mangle.cassandra.model.tasks.TaskType;
import com.vmware.mangle.model.enums.EndpointType;
import com.vmware.mangle.services.helpers.faults.AbstractFault;
import com.vmware.mangle.task.framework.helpers.faults.SupportedEndpoints;
import com.vmware.mangle.utils.exceptions.MangleException;

/**
 * @author chetanc
 *
 *         VCenter Fault specific to the operations involving the modifications of the power state
 *         of vcenter vms
 */

@SupportedEndpoints(endPoints = { EndpointType.VCENTER })
public class VMStateChangeFault extends AbstractFault {

    public VMStateChangeFault(VMStateFaultSpec faultSpec) throws MangleException {
        super(faultSpec, TaskType.INJECTION);
        faultSpec.setFaultName(faultSpec.getFault().name());
    }

    @Override
    protected Map<String, String> getFaultSpecificArgs() {
        VMFaultSpec localFaultSpec = (VMFaultSpec) faultSpec;
        Map<String, String> specificArgs = new LinkedHashMap<>();
        specificArgs.put("--vmname", localFaultSpec.getVmName());
        return specificArgs;
    }
}
