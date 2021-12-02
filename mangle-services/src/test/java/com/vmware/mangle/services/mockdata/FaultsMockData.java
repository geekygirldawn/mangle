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

package com.vmware.mangle.services.mockdata;

import static com.vmware.mangle.services.constants.CommonConstants.IO_SIZE_ARG;
import static com.vmware.mangle.services.constants.CommonConstants.LOAD_ARG;
import static com.vmware.mangle.services.constants.CommonConstants.TARGET_DIRECTORY_ARG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.vmware.mangle.cassandra.model.endpoint.AWSCredentials;
import com.vmware.mangle.cassandra.model.endpoint.AzureCredentials;
import com.vmware.mangle.cassandra.model.endpoint.EndpointSpec;
import com.vmware.mangle.cassandra.model.endpoint.K8SCredentials;
import com.vmware.mangle.cassandra.model.endpoint.RemoteMachineCredentials;
import com.vmware.mangle.cassandra.model.faults.specs.ClockSkewSpec;
import com.vmware.mangle.cassandra.model.faults.specs.CommandExecutionFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.CpuFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.DbFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.DbTransactionErrorFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.DbTransactionLatencyFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.DiskIOFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.DiskSpaceSpec;
import com.vmware.mangle.cassandra.model.faults.specs.DockerFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.FilehandlerLeakFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.JVMAgentFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.JVMCodeLevelFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.JVMProperties;
import com.vmware.mangle.cassandra.model.faults.specs.K8SDeleteResourceFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.K8SDrainNodeFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.K8SFaultTriggerSpec;
import com.vmware.mangle.cassandra.model.faults.specs.K8SResourceNotReadyFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.K8SServiceUnavailableFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.KernelPanicSpec;
import com.vmware.mangle.cassandra.model.faults.specs.KillProcessFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.MemoryFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.NetworkPartitionFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.ThreadLeakFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.VMDiskFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.VMNicFaultSpec;
import com.vmware.mangle.cassandra.model.faults.specs.VMStateFaultSpec;
import com.vmware.mangle.cassandra.model.redis.faults.specs.RedisDelayFaultSpec;
import com.vmware.mangle.cassandra.model.redis.faults.specs.RedisDropConnectionFaultSpec;
import com.vmware.mangle.cassandra.model.redis.faults.specs.RedisFaultSpec;
import com.vmware.mangle.cassandra.model.redis.faults.specs.RedisReturnErrorFaultSpec;
import com.vmware.mangle.cassandra.model.scheduler.SchedulerInfo;
import com.vmware.mangle.cassandra.model.tasks.DockerSpecificArguments;
import com.vmware.mangle.cassandra.model.tasks.K8SSpecificArguments;
import com.vmware.mangle.cassandra.model.tasks.Task;
import com.vmware.mangle.model.aws.AwsEC2NetworkFaults;
import com.vmware.mangle.model.aws.AwsEC2StateFaults;
import com.vmware.mangle.model.aws.AwsEC2StorageFaults;
import com.vmware.mangle.model.aws.AwsRDSFaults;
import com.vmware.mangle.model.aws.AwsRDSInstance;
import com.vmware.mangle.model.aws.faults.spec.AwsEC2InstanceStateFaultSpec;
import com.vmware.mangle.model.aws.faults.spec.AwsEC2NetworkFaultSpec;
import com.vmware.mangle.model.aws.faults.spec.AwsEC2StorageFaultSpec;
import com.vmware.mangle.model.aws.faults.spec.AwsRDSFaultSpec;
import com.vmware.mangle.model.azure.AzureNetworkFaults;
import com.vmware.mangle.model.azure.AzureStorageFaults;
import com.vmware.mangle.model.azure.AzureVMStateFaults;
import com.vmware.mangle.model.azure.faults.spec.AzureVMNetworkFaultSpec;
import com.vmware.mangle.model.azure.faults.spec.AzureVMStateFaultSpec;
import com.vmware.mangle.model.azure.faults.spec.AzureVMStorageFaultSpec;
import com.vmware.mangle.model.enums.ClockSkewOperation;
import com.vmware.mangle.model.enums.EndpointGroupType;
import com.vmware.mangle.model.enums.EndpointType;
import com.vmware.mangle.services.enums.AgentFaultName;
import com.vmware.mangle.services.enums.BytemanFaultType;
import com.vmware.mangle.services.enums.DockerFaultName;
import com.vmware.mangle.services.enums.K8SFaultName;
import com.vmware.mangle.services.enums.K8SResource;
import com.vmware.mangle.services.enums.VCenterDiskFaults;
import com.vmware.mangle.services.enums.VCenterNicFaults;
import com.vmware.mangle.services.enums.VCenterStateFaults;
import com.vmware.mangle.services.tasks.helper.MockTaskHelper;
import com.vmware.mangle.utils.ReadProperty;
import com.vmware.mangle.utils.constants.Constants;
import com.vmware.mangle.utils.constants.FaultConstants;

/**
 * Faults Mock Data.
 *
 * @author hkilari
 */
public class FaultsMockData {

    private Properties properties;

    private EndpointMockData endpointMockData = new EndpointMockData();
    private CredentialsSpecMockData credentialsSpecMockData = new CredentialsSpecMockData();

    private Integer faultLoad;

    private String javahomePath;

    private String jvmProcess;

    private Integer jvmProcessPort;

    private String jvmProcessUser;

    private String testContainername;

    private String testPodLabels;

    private Integer faultExecutionTimeout;

    private String faultTaskId;

    private String httpMethodsString;

    private String servicesString;

    private String vm;

    private String vmNicId;

    private String vmDiskId;

    private String directoryPath;

    private String cronExpression;

    public FaultsMockData() {
        this.properties = ReadProperty.readProperty(Constants.MOCKDATA_FILE);
        faultLoad = Integer.parseInt(properties.getProperty("faultLoad"));
        javahomePath = properties.getProperty("javahomePath");
        jvmProcess = properties.getProperty("jvmProcess");
        jvmProcessPort = Integer.parseInt(properties.getProperty("jvmProcessPort"));
        jvmProcessUser = properties.getProperty("jvmProcessUser");
        testContainername = properties.getProperty("testContainername");
        testPodLabels = properties.getProperty("testPodLabels");
        faultExecutionTimeout = Integer.parseInt(properties.getProperty("faultExecutionTimeout"));
        faultTaskId = properties.getProperty("faultTaskId");
        httpMethodsString = properties.getProperty("httpMethodsString");
        servicesString = properties.getProperty("servicesString");
        vm = properties.getProperty("vcenter.vm.name");
        vmNicId = properties.getProperty("vcenter.vm.nicid");
        vmDiskId = properties.getProperty("vcenter.vm.diskid");
        directoryPath = properties.getProperty("directoryPath");
        cronExpression = properties.getProperty("scheduler.cronExpression");
    }

    public K8SFaultTriggerSpec getK8SCPUFaultTriggerSpec() {
        K8SFaultTriggerSpec k8SFaultSpec = new K8SFaultTriggerSpec();

        CpuFaultSpec cpuFaultSpec = getK8SCPUFaultSpec();

        k8SFaultSpec.setFaultSpec(cpuFaultSpec);
        k8SFaultSpec.setSchedule(cpuFaultSpec.getSchedule());
        return k8SFaultSpec;
    }

    public CpuFaultSpec getK8SCPUFaultSpec() {
        K8SCredentials credentialsSpec = credentialsSpecMockData.getk8SCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.k8sEndpointMockData();
        CpuFaultSpec cpuFaultSpec = new CpuFaultSpec();
        cpuFaultSpec.setCpuLoad(faultLoad);
        Map<String, String> args = new HashMap<>();
        args.put("__load", String.valueOf(cpuFaultSpec.getCpuLoad()));
        cpuFaultSpec.setArgs(args);
        cpuFaultSpec.setEndpointName(endpointSpec.getName());
        cpuFaultSpec.setFaultName(AgentFaultName.INJECT_CPU_FAULT.getValue());

        cpuFaultSpec.setJvmProperties(getJVMProperties());

        cpuFaultSpec.setK8sArguments(getK8SSpecificArguments());
        cpuFaultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);

        cpuFaultSpec.setEndpoint(endpointSpec);
        cpuFaultSpec.setCredentials(credentialsSpec);
        return cpuFaultSpec;
    }

    public CpuFaultSpec getRMGroupCPUFaultSpec() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        endpointSpec.setEndPointType(EndpointType.ENDPOINT_GROUP);
        endpointSpec.setEndpointGroupType(EndpointGroupType.MACHINE);
        CpuFaultSpec cpuFaultSpec = new CpuFaultSpec();
        cpuFaultSpec.setCpuLoad(faultLoad);
        Map<String, String> args = new HashMap<>();
        args.put("__load", String.valueOf(cpuFaultSpec.getCpuLoad()));
        cpuFaultSpec.setArgs(args);
        cpuFaultSpec.setEndpointName(endpointSpec.getName());
        cpuFaultSpec.setFaultName(AgentFaultName.INJECT_CPU_FAULT.getValue());

        cpuFaultSpec.setJvmProperties(getJVMProperties());
        cpuFaultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);

        cpuFaultSpec.setEndpoint(endpointSpec);
        cpuFaultSpec.setCredentials(credentialsSpec);
        return cpuFaultSpec;
    }

    public JVMAgentFaultSpec getK8sCpuJvmAgentFaultSpec() {
        K8SCredentials credentialsSpec = credentialsSpecMockData.getk8SCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.k8sEndpointMockData();
        JVMAgentFaultSpec cpuFaultSpec = new JVMAgentFaultSpec();
        cpuFaultSpec.setFaultName(AgentFaultName.INJECT_CPU_FAULT.getValue());
        Map<String, String> args = new HashMap<>();
        args.put("__load", String.valueOf(faultLoad));
        cpuFaultSpec.setArgs(args);

        cpuFaultSpec.setJvmProperties(getJVMProperties());
        cpuFaultSpec.setK8sArguments(getK8SSpecificArguments());
        cpuFaultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);

        cpuFaultSpec.setEndpointName(endpointSpec.getName());
        cpuFaultSpec.setEndpoint(endpointSpec);
        cpuFaultSpec.setCredentials(credentialsSpec);
        return cpuFaultSpec;
    }

    public JVMCodeLevelFaultSpec getK8sSpringExceptionJVMCodeLevelFaultSpec() {
        K8SCredentials credentialsSpec = credentialsSpecMockData.getk8SCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.k8sEndpointMockData();
        JVMCodeLevelFaultSpec springServiceExceptionFaultSpec = new JVMCodeLevelFaultSpec();
        springServiceExceptionFaultSpec.setFaultType(BytemanFaultType.SPRING_SERVICE_EXCEPTION.toString());
        springServiceExceptionFaultSpec.setFaultName(BytemanFaultType.SPRING_SERVICE_EXCEPTION.toString());
        Map<String, String> args = new HashMap<>();
        args.put("servicesString", servicesString);
        args.put("httpMethodsString", httpMethodsString);
        args.put("taskId", faultTaskId);
        springServiceExceptionFaultSpec.setArgs(args);
        springServiceExceptionFaultSpec.setEndpointName(endpointSpec.getName());

        springServiceExceptionFaultSpec.setJvmProperties(getJVMProperties());
        springServiceExceptionFaultSpec.setK8sArguments(getK8SSpecificArguments());
        springServiceExceptionFaultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);

        springServiceExceptionFaultSpec.setEndpoint(endpointSpec);
        springServiceExceptionFaultSpec.setCredentials(credentialsSpec);
        return springServiceExceptionFaultSpec;
    }

    public DockerFaultSpec getDockerPauseFaultSpec() {
        DockerFaultSpec faultSpec = new DockerFaultSpec();
        EndpointSpec endpointSpec = endpointMockData.dockerEndpointMockData();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setDockerFaultName(DockerFaultName.DOCKER_PAUSE);
        faultSpec.setFaultName(DockerFaultName.DOCKER_PAUSE.name());
        DockerSpecificArguments dockerArgs = new DockerSpecificArguments();
        dockerArgs.setContainerName(testContainername);
        faultSpec.setDockerArguments(dockerArgs);

        return faultSpec;
    }

    public DockerFaultSpec getDockerStopFaultSpec() {
        DockerFaultSpec faultSpec = new DockerFaultSpec();
        EndpointSpec endpointSpec = endpointMockData.dockerEndpointMockData();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setDockerFaultName(DockerFaultName.DOCKER_STOP);
        faultSpec.setFaultName(DockerFaultName.DOCKER_STOP.name());
        DockerSpecificArguments dockerArgs = new DockerSpecificArguments();
        dockerArgs.setContainerName(testContainername);
        faultSpec.setDockerArguments(dockerArgs);
        return faultSpec;
    }

    public JVMProperties getJVMProperties() {
        JVMProperties jvmProperties = new JVMProperties();
        jvmProperties.setJavaHomePath(javahomePath);
        jvmProperties.setJvmprocess(jvmProcess);
        jvmProperties.setPort(jvmProcessPort);
        jvmProperties.setUser(jvmProcessUser);
        return jvmProperties;
    }

    public K8SSpecificArguments getK8SSpecificArguments() {
        K8SSpecificArguments k8sArguments = new K8SSpecificArguments();
        k8sArguments.setContainerName(testContainername);
        k8sArguments.setEnableRandomInjection(true);
        k8sArguments.setPodLabels(testPodLabels);
        return k8sArguments;
    }

    public K8SResourceNotReadyFaultSpec getK8SResourceNotReadyFaultSpec() {
        K8SResourceNotReadyFaultSpec k8SResourceNotReadyFaultSpec = new K8SResourceNotReadyFaultSpec();
        k8SResourceNotReadyFaultSpec.setAppContainerName(testContainername);
        k8SResourceNotReadyFaultSpec.setEndpoint(endpointMockData.k8sEndpointMockData());
        k8SResourceNotReadyFaultSpec.setEndpointName(endpointMockData.k8sEndpointMockData().getName());
        k8SResourceNotReadyFaultSpec.setCredentials(credentialsSpecMockData.getk8SCredentialsData());
        k8SResourceNotReadyFaultSpec.setResourceType(K8SResource.POD);
        return k8SResourceNotReadyFaultSpec;
    }

    public K8SServiceUnavailableFaultSpec getK8SServiceUnavailableFaultSpec() {
        K8SServiceUnavailableFaultSpec k8SServiceUnavailableFaultSpec = new K8SServiceUnavailableFaultSpec();
        k8SServiceUnavailableFaultSpec.setEndpoint(endpointMockData.k8sEndpointMockData());
        k8SServiceUnavailableFaultSpec.setEndpointName(endpointMockData.k8sEndpointMockData().getName());
        k8SServiceUnavailableFaultSpec.setCredentials(credentialsSpecMockData.getk8SCredentialsData());
        Map<String, String> resourceLabels = new HashMap<>();
        resourceLabels.put("app", "app-inventory-service");
        k8SServiceUnavailableFaultSpec.setResourceLabels(resourceLabels);
        return k8SServiceUnavailableFaultSpec;
    }

    public K8SDrainNodeFaultSpec getDrainK8SNodeFaultSpec() {
        K8SDrainNodeFaultSpec k8SFaultSpec = new K8SDrainNodeFaultSpec();
        k8SFaultSpec.setEndpoint(endpointMockData.k8sEndpointMockData());
        k8SFaultSpec.setEndpointName(endpointMockData.k8sEndpointMockData().getName());
        k8SFaultSpec.setCredentials(credentialsSpecMockData.getk8SCredentialsData());
        k8SFaultSpec.setFaultName(K8SFaultName.DRAIN_NODE.name());
        return k8SFaultSpec;
    }

    public K8SDeleteResourceFaultSpec getDeleteK8SResourceFaultSpec() {
        K8SDeleteResourceFaultSpec k8SFaultSpec = new K8SDeleteResourceFaultSpec();
        k8SFaultSpec.setEndpoint(endpointMockData.k8sEndpointMockData());
        k8SFaultSpec.setEndpointName(endpointMockData.k8sEndpointMockData().getName());
        k8SFaultSpec.setCredentials(credentialsSpecMockData.getk8SCredentialsData());
        k8SFaultSpec.setFaultName(K8SFaultName.DELETE_RESOURCE.name());
        return k8SFaultSpec;
    }

    public Task<CommandExecutionFaultSpec> getMockTask() {
        MockTaskHelper<CommandExecutionFaultSpec> mockTask = new MockTaskHelper<>();
        Task<CommandExecutionFaultSpec> task = mockTask.init(new CommandExecutionFaultSpec());
        return task;
    }

    public VMNicFaultSpec getVMNicFaultSpec() {
        VMNicFaultSpec faultSpec = new VMNicFaultSpec();
        EndpointSpec endpointSpec = endpointMockData.getVCenterEndpointSpecMock();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setFaultName(VCenterNicFaults.DISCONNECT_NIC.name());
        faultSpec.setFault(VCenterNicFaults.DISCONNECT_NIC);
        faultSpec.setVmNicId(vmNicId);
        faultSpec.setVmName(vm);

        return faultSpec;
    }

    public VMDiskFaultSpec getVMDiskFaultSpec() {
        VMDiskFaultSpec faultSpec = new VMDiskFaultSpec();
        EndpointSpec endpointSpec = endpointMockData.getVCenterEndpointSpecMock();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setFaultName(VCenterDiskFaults.DISCONNECT_DISK.name());
        faultSpec.setFault(VCenterDiskFaults.DISCONNECT_DISK);
        faultSpec.setVmDiskId(vmDiskId);
        faultSpec.setVmName(vm);

        return faultSpec;
    }

    public VMStateFaultSpec getVMStateFaultSpec() {
        VMStateFaultSpec faultSpec = new VMStateFaultSpec();
        EndpointSpec endpointSpec = endpointMockData.getVCenterEndpointSpecMock();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setFaultName(VCenterStateFaults.POWEROFF_VM.name());
        faultSpec.setFault(VCenterStateFaults.POWEROFF_VM);
        faultSpec.setVmName(vm);

        return faultSpec;
    }

    public KillProcessFaultSpec getKillProcessSpec() {
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        KillProcessFaultSpec faultSpec = new KillProcessFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setProcessIdentifier("kill-test.jar");
        faultSpec.setProcessId("");
        faultSpec.setKillAll(false);
        faultSpec.setRemediationCommand("java -jar /tmp/kill-test/kill-test.jar");
        return faultSpec;
    }

    public DiskSpaceSpec getDiskSpaceSpec() {
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        DiskSpaceSpec faultSpec = new DiskSpaceSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setDirectoryPath("/diskspacefault");
        return faultSpec;
    }

    public KernelPanicSpec getKernelPanicSpec() {
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        KernelPanicSpec faultSpec = new KernelPanicSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        return faultSpec;
    }

    public MemoryFaultSpec getMemoryFaultSpecOfRemoteMachine() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        MemoryFaultSpec memoryFaultSpec = new MemoryFaultSpec();
        memoryFaultSpec.setFaultName(AgentFaultName.INJECT_MEMORY_FAULT.getValue());
        Map<String, String> args = new HashMap<>();
        args.put(LOAD_ARG, String.valueOf(faultLoad));
        memoryFaultSpec.setArgs(args);
        memoryFaultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        memoryFaultSpec.setEndpointName(endpointSpec.getName());
        memoryFaultSpec.setEndpoint(endpointSpec);
        memoryFaultSpec.setCredentials(credentialsSpec);
        return memoryFaultSpec;
    }

    public DiskIOFaultSpec getDiskIOFaultSpecOfRemoteMachine() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        DiskIOFaultSpec diskIOFaultSpec = new DiskIOFaultSpec();
        diskIOFaultSpec.setFaultName(AgentFaultName.INJECT_DISK_IO_FAULT.getValue());
        Map<String, String> args = new HashMap<>();
        args.put(IO_SIZE_ARG, directoryPath);
        args.put(TARGET_DIRECTORY_ARG, String.valueOf(faultLoad));
        diskIOFaultSpec.setArgs(args);
        diskIOFaultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        diskIOFaultSpec.setEndpointName(endpointSpec.getName());
        diskIOFaultSpec.setEndpoint(endpointSpec);
        diskIOFaultSpec.setCredentials(credentialsSpec);
        diskIOFaultSpec.setIoSize(0);
        return diskIOFaultSpec;
    }

    public FilehandlerLeakFaultSpec getFileHandlerLeakFaultSpecOfDockerEndpoint() {
        EndpointSpec endpointSpec = endpointMockData.dockerEndpointMockData();
        FilehandlerLeakFaultSpec fileHandlerFaultSpec = new FilehandlerLeakFaultSpec();
        fileHandlerFaultSpec.setFaultName(AgentFaultName.INJECT_FILE_HANDLER_FAULT.getValue());
        fileHandlerFaultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        fileHandlerFaultSpec.setEndpointName(endpointSpec.getName());
        fileHandlerFaultSpec.setEndpoint(endpointSpec);
        return fileHandlerFaultSpec;
    }

    public ThreadLeakFaultSpec getThreadLeakFaultSpecOfDockerEndpoint() {
        EndpointSpec endpointSpec = endpointMockData.dockerEndpointMockData();
        ThreadLeakFaultSpec threadFaultSpec = new ThreadLeakFaultSpec();
        threadFaultSpec.setFaultName(AgentFaultName.INJECT_THREAD_LEAK_FAULT.getValue());
        threadFaultSpec.setEnableOOM(false);
        threadFaultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        threadFaultSpec.setEndpointName(endpointSpec.getName());
        threadFaultSpec.setEndpoint(endpointSpec);
        return threadFaultSpec;
    }

    public AwsEC2InstanceStateFaultSpec getAwsEC2InstanceStateFaultSpec() {
        AwsEC2InstanceStateFaultSpec faultSpec = new AwsEC2InstanceStateFaultSpec();
        AWSCredentials creds = credentialsSpecMockData.getAwsCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.awsEndpointSpecMock();
        faultSpec.setCredentials(creds);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setFault(AwsEC2StateFaults.STOP_INSTANCES);
        faultSpec.setInstanceIds(new ArrayList<>());
        faultSpec.setFaultName(faultSpec.getFault().name());

        return faultSpec;
    }

    public AwsEC2NetworkFaultSpec getAwsEC2BlockAllNetworkFaultSpec() {
        AwsEC2NetworkFaultSpec faultSpec = new AwsEC2NetworkFaultSpec();
        AWSCredentials creds = credentialsSpecMockData.getAwsCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.awsEndpointSpecMock();
        faultSpec.setCredentials(creds);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setFault(AwsEC2NetworkFaults.BLOCK_ALL_NETWORK_TRAFFIC);
        faultSpec.setInstanceIds(new ArrayList<>());
        faultSpec.setFaultName(faultSpec.getFault().name());

        return faultSpec;
    }

    public AwsEC2StorageFaultSpec getAwsEC2DetachVolumesFaultSpec() {
        AwsEC2StorageFaultSpec faultSpec = new AwsEC2StorageFaultSpec();
        AWSCredentials creds = credentialsSpecMockData.getAwsCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.awsEndpointSpecMock();
        faultSpec.setCredentials(creds);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setFault(AwsEC2StorageFaults.DETACH_VOLUMES);
        faultSpec.setInstanceIds(new ArrayList<>());
        faultSpec.setFaultName(faultSpec.getFault().name());

        return faultSpec;
    }

    public AwsRDSFaultSpec getAwsRDSFaultSpec() {
        AwsRDSFaultSpec faultSpec = new AwsRDSFaultSpec();
        List<String> dbIdentifiers = new ArrayList<>();
        dbIdentifiers.add("id1");
        dbIdentifiers.add("id2");
        List<AwsRDSInstance> selectedRdsInstances = new ArrayList<>();
        AwsRDSInstance rdsInstance = new AwsRDSInstance();
        rdsInstance.setDbEngine("postgres");
        rdsInstance.setDbRole("Instance");
        rdsInstance.setInstanceIdentifier(dbIdentifiers.get(0));
        selectedRdsInstances.add(rdsInstance);
        AWSCredentials creds = credentialsSpecMockData.getAwsCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.awsEndpointSpecMock();
        faultSpec.setCredentials(creds);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setFault(AwsRDSFaults.STOP_INSTANCES);
        faultSpec.setDbIdentifiers(dbIdentifiers);
        faultSpec.setSelectedRDSInstances(selectedRdsInstances);
        faultSpec.setFaultName(faultSpec.getFault().name());
        return faultSpec;
    }

    public AzureVMStateFaultSpec getAzureVMStateFaultSpec() {
        AzureVMStateFaultSpec faultSpec = new AzureVMStateFaultSpec();
        AzureCredentials creds = credentialsSpecMockData.getAzureCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.awsEndpointSpecMock();
        faultSpec.setCredentials(creds);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setFault(AzureVMStateFaults.STOP_VMS);
        faultSpec.setResourceIds(new ArrayList<>());
        faultSpec.setFaultName(faultSpec.getFault().name());

        return faultSpec;
    }

    public AzureVMNetworkFaultSpec getAzureVMNetworkBlockFaultSpec() {
        AzureVMNetworkFaultSpec faultSpec = new AzureVMNetworkFaultSpec();
        AzureCredentials creds = credentialsSpecMockData.getAzureCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.awsEndpointSpecMock();
        faultSpec.setCredentials(creds);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setFault(AzureNetworkFaults.BLOCK_ALL_VM_NETWORK_TRAFFIC);
        faultSpec.setResourceIds(new ArrayList<>());
        faultSpec.setFaultName(faultSpec.getFault().name());
        return faultSpec;
    }

    public AzureVMStorageFaultSpec getAzureVMDetachDisksFaultSpec() {
        AzureVMStorageFaultSpec faultSpec = new AzureVMStorageFaultSpec();
        AzureCredentials creds = credentialsSpecMockData.getAzureCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.awsEndpointSpecMock();
        faultSpec.setCredentials(creds);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setFault(AzureStorageFaults.DETACH_DISKS);
        faultSpec.setResourceIds(new ArrayList<>());
        faultSpec.setRandomInjection(true);
        faultSpec.setFaultName(faultSpec.getFault().name());
        return faultSpec;
    }

    public CpuFaultSpec getScheduleFaultSpec() {
        CpuFaultSpec cpuFaultSpec = getK8SCPUFaultSpec();
        SchedulerInfo schedulerInfo = new SchedulerInfo();
        schedulerInfo.setCronExpression(cronExpression);
        cpuFaultSpec.setSchedule(schedulerInfo);
        return cpuFaultSpec;
    }

    public CpuFaultSpec getScheduleFaultSpecWithInvalidCron() {
        CpuFaultSpec cpuFaultSpec = getScheduleFaultSpec();
        cpuFaultSpec.getSchedule().setCronExpression("dummycron");
        return cpuFaultSpec;
    }

    public DbFaultSpec getDbConnectionLeakFaultSpecOfRemoteMachine() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        DbFaultSpec faultSpec = new DbFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        faultSpec.setCredentials(credentialsSpec);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setChildEndpoint(endpointMockData.getDatabaseEndpointSpec());
        faultSpec.setChildCredentials(credentialsSpecMockData.getDatabaseCredentials());
        return faultSpec;
    }

    public KillProcessFaultSpec getKillProcessFaultOnRemoteMachine() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        KillProcessFaultSpec faultSpec = new KillProcessFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        faultSpec.setCredentials(credentialsSpec);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setKillAll(false);
        faultSpec.setProcessIdentifier("xxx");
        faultSpec.setRemediationCommand("  ");
        return faultSpec;
    }

    public DbTransactionErrorFaultSpec getDbTransactionErrorFaultSpecOfRemoteMachine() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        DbTransactionErrorFaultSpec faultSpec = new DbTransactionErrorFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setTableName("emp");
        faultSpec.setErrorCode("invalid_transaction_state");
        faultSpec.setPercentage(60);
        faultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        faultSpec.setCredentials(credentialsSpec);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setChildEndpoint(endpointMockData.getDatabaseEndpointSpec());
        faultSpec.setChildCredentials(credentialsSpecMockData.getDatabaseCredentials());
        return faultSpec;
    }

    public ClockSkewSpec getClockSkewSpec() {
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        ClockSkewSpec clockSkewSpec = new ClockSkewSpec();
        clockSkewSpec.setEndpoint(endpointSpec);
        clockSkewSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        clockSkewSpec.setSeconds((short) 1);
        clockSkewSpec.setMinutes((short) 1);
        clockSkewSpec.setHours((short) 1);
        clockSkewSpec.setDays((short) 1);
        clockSkewSpec.setClockSkewOperation(ClockSkewOperation.FUTURE);
        return clockSkewSpec;
    }

    public DbTransactionLatencyFaultSpec getDbTransactionLatencyFaultSpecOfRemoteMachine() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        DbTransactionLatencyFaultSpec faultSpec = new DbTransactionLatencyFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setTableName("emp1");
        faultSpec.setLatency(1000);
        faultSpec.setPercentage(60);
        faultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        faultSpec.setCredentials(credentialsSpec);
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setChildEndpoint(endpointMockData.getDatabaseEndpointSpec());
        faultSpec.setChildCredentials(credentialsSpecMockData.getDatabaseCredentials());
        return faultSpec;
    }

    public RedisDelayFaultSpec getRedisDelayFaultSpec() {
        EndpointSpec endpointSpec = endpointMockData.getRedisProxyEndpointMockData();
        RedisDelayFaultSpec delayFaultSpec = new RedisDelayFaultSpec();
        delayFaultSpec.setEndpointName(endpointSpec.getName());
        delayFaultSpec.setEndpoint(endpointSpec);
        delayFaultSpec.setDelay(1000);
        delayFaultSpec.setPercentage(50);
        Map<String, String> specificArgs = new HashMap<>();
        specificArgs.put(FaultConstants.OPERATION, delayFaultSpec.getFaultName());
        delayFaultSpec.setArgs(specificArgs);
        return delayFaultSpec;
    }

    public RedisReturnErrorFaultSpec getRedisReturnErrorFaultSpec() {
        EndpointSpec endpointSpec = endpointMockData.getRedisProxyEndpointMockData();
        RedisReturnErrorFaultSpec faultSpec = new RedisReturnErrorFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setErrorType("CON");
        faultSpec.setPercentage(50);
        Map<String, String> specificArgs = new HashMap<>();
        specificArgs.put(FaultConstants.OPERATION, faultSpec.getFaultName());
        faultSpec.setArgs(specificArgs);
        return faultSpec;
    }

    public RedisFaultSpec getRedisFaultSpec() {
        EndpointSpec endpointSpec = endpointMockData.getRedisProxyEndpointMockData();
        RedisFaultSpec faultSpec = new RedisFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setPercentage(25);
        Map<String, String> specificArgs = new HashMap<>();
        specificArgs.put(FaultConstants.OPERATION, faultSpec.getFaultName());
        faultSpec.setArgs(specificArgs);
        return faultSpec;
    }

    public RedisDropConnectionFaultSpec getRedisDropConnectionFaultSpec() {
        EndpointSpec endpointSpec = endpointMockData.getRedisProxyEndpointMockData();
        RedisDropConnectionFaultSpec faultSpec = new RedisDropConnectionFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setEndpoint(endpointSpec);
        faultSpec.setPercentage(25);
        Map<String, String> specificArgs = new HashMap<>();
        specificArgs.put(FaultConstants.OPERATION, faultSpec.getFaultName());
        faultSpec.setArgs(specificArgs);
        return faultSpec;
    }


    public NetworkPartitionFaultSpec getNetworkPartitionFaultSpecOfRemoteMachine() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        NetworkPartitionFaultSpec faultSpec = new NetworkPartitionFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec.setHosts(Arrays.asList("10.2.3.4", "10,2,3,5"));
        faultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        faultSpec.setCredentials(credentialsSpec);
        faultSpec.setEndpoint(endpointSpec);
        return faultSpec;
    }

    public NetworkPartitionFaultSpec getNetworkPartitionFaultSpecOfRemoteMachine_WithSameHost() {
        RemoteMachineCredentials credentialsSpec = credentialsSpecMockData.getRMCredentialsData();
        EndpointSpec endpointSpec = endpointMockData.rmEndpointMockData();
        NetworkPartitionFaultSpec faultSpec = new NetworkPartitionFaultSpec();
        faultSpec.setEndpointName(endpointSpec.getName());
        faultSpec
                .setHosts(Arrays.asList("10.2.3.4", endpointMockData.getRemoteMachineConnectionProperties().getHost()));
        faultSpec.setTimeoutInMilliseconds(faultExecutionTimeout);
        faultSpec.setCredentials(credentialsSpec);
        faultSpec.setEndpoint(endpointSpec);
        return faultSpec;
    }
}
