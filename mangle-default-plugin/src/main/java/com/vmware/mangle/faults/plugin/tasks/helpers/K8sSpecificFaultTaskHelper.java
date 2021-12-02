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

package com.vmware.mangle.faults.plugin.tasks.helpers;

import java.util.List;
import java.util.Map;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.vmware.mangle.cassandra.model.faults.specs.K8SFaultSpec;
import com.vmware.mangle.cassandra.model.tasks.FaultTask;
import com.vmware.mangle.cassandra.model.tasks.SupportScriptInfo;
import com.vmware.mangle.cassandra.model.tasks.Task;
import com.vmware.mangle.cassandra.model.tasks.TaskType;
import com.vmware.mangle.cassandra.model.tasks.commands.CommandInfo;
import com.vmware.mangle.faults.plugin.helpers.k8s.K8sFaultHelper;
import com.vmware.mangle.task.framework.helpers.AbstractCommandExecutionTaskHelper;
import com.vmware.mangle.task.framework.utils.TaskDescriptionUtils;
import com.vmware.mangle.utils.ICommandExecutor;
import com.vmware.mangle.utils.exceptions.MangleException;
import com.vmware.mangle.utils.exceptions.handler.ErrorCode;

/**
 * Implementation of AbstractRemoteCommandExecutionTaskHelper to Support Injection of K8S specific
 * faults
 *
 * @author bkaranam
 */
@Extension(ordinal = 1)
public class K8sSpecificFaultTaskHelper<T extends K8SFaultSpec> extends AbstractCommandExecutionTaskHelper<T> {

    private K8sFaultHelper k8sFaultHelper;

    @Override
    public Task<T> init(T k8sFaultSpec, String injectionTaskId) {
        Task<T> task = new FaultTask<>();
        return init(task, k8sFaultSpec, injectionTaskId);
    }

    @Override
    public Task<T> init(T k8sFaultSpec) {
        return init(k8sFaultSpec, null);
    }

    @Autowired
    public void setK8sFaultHelper(K8sFaultHelper k8sFaultHelper) {
        this.k8sFaultHelper = k8sFaultHelper;
    }

    @Override
    public void executeTask(Task<T> task) throws MangleException {
        Map<String, String> disabledResourceLabels =
                task.getTaskData().getEndpoint().getK8sConnectionProperties().getDisabledResourceLabels();
        if (!CollectionUtils.isEmpty(task.getTaskData().getResourceLabels())
                && disabledResourceLabels.entrySet().containsAll(task.getTaskData().getResourceLabels().entrySet())) {
            throw new MangleException(ErrorCode.K8S_RESOURCE_LABELS_DISABLED, task.getTaskData().getResourceLabels(),
                    task.getTaskData().getEndpointName());
        }
        if (task.getTaskType().equals(TaskType.INJECTION)) {
            K8SFaultSpec k8SFaultSpec = task.getTaskData();
            k8SFaultSpec.setResourcesList(k8sFaultHelper.getResouceList(getExecutor(task), k8SFaultSpec));
            task.getTaskData().setInjectionCommandInfoList(
                    k8sFaultHelper.getInjectionCommandInfoList(getExecutor(task), k8SFaultSpec));
            List<CommandInfo> remediationCommandInfoList =
                    k8sFaultHelper.getRemediationCommandInfoList(getExecutor(task), k8SFaultSpec);
            if (CollectionUtils.isEmpty(remediationCommandInfoList) || remediationCommandInfoList.get(0) != null) {
                task.getTaskData().setRemediationCommandInfoList(remediationCommandInfoList);
            }
        }
        super.executeTask(task);
    }

    /**
     * Overridden implementation of getExecutor(task) to return the executor based on User Input
     *
     * @throws MangleException
     */
    @Override
    public ICommandExecutor getExecutor(Task<T> task) throws MangleException {
        return k8sFaultHelper.getExecutor(task.getTaskData());
    }

    @Override
    public String getDescription(Task<T> task) {
        return TaskDescriptionUtils.getDescription(task);
    }

    @Override
    public void checkInjectionPreRequisites(Task<T> task) throws MangleException {
        //No injection prerequisites identified for this task as of now
    }

    @Override
    public void checkRemediationPreRequisites(Task<T> task) throws MangleException {
        //No remediation prerequisites identified for this task as of now
    }

    @Override
    protected void prepareEndpoint(Task<T> task, List<SupportScriptInfo> listFaultInjectionScripts)
            throws MangleException {
        //When needed we will implement this
    }

    @Override
    protected void checkTaskSpecificPrerequisites(Task<T> task) throws MangleException {
        //When needed we will implement this
    }
}
