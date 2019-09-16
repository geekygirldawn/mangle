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

package com.vmware.mangle.cassandra.model.tasks;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.Table;

import com.vmware.mangle.cassandra.model.faults.specs.TaskSpec;

/**
 * @author hkilari
 */
@Table(value = "task")
@Data
@EqualsAndHashCode(callSuper = true)
public class FaultTriggeringTask<T extends TaskSpec, S extends TaskSpec> extends RemediableTask<T>
        implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonIgnore
    @Transient
    private Map<String, Task<S>> taskObjmap;
}
