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

package com.vmware.mangle.unittest.faults.plugin.helpers.k8s;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j2;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.vmware.mangle.cassandra.model.faults.specs.K8SFaultSpec;
import com.vmware.mangle.cassandra.model.tasks.commands.CommandExecutionResult;
import com.vmware.mangle.cassandra.model.tasks.commands.CommandInfo;
import com.vmware.mangle.faults.plugin.helpers.k8s.K8sFaultHelper;
import com.vmware.mangle.faults.plugin.mockdata.FaultsMockData;
import com.vmware.mangle.task.framework.endpoint.EndpointClientFactory;
import com.vmware.mangle.unittest.faults.plugin.helpers.CommandResultUtils;
import com.vmware.mangle.utils.ICommandExecutor;
import com.vmware.mangle.utils.clients.kubernetes.KubernetesCommandLineClient;
import com.vmware.mangle.utils.clients.kubernetes.KubernetesTemplates;
import com.vmware.mangle.utils.clients.restclient.RestTemplateWrapper;
import com.vmware.mangle.utils.exceptions.MangleException;
import com.vmware.mangle.utils.exceptions.handler.ErrorCode;


/**
 * Test Class for K8sFaultHelper
 *
 * @author hkilari
 * @author bkaranam
 *
 */
@Log4j2
public class K8sFaultHelperTest {
    @Mock
    EndpointClientFactory endpointClientFactory;
    @Mock
    KubernetesCommandLineClient kubernetesCommandLineClient;
    @Mock
    ICommandExecutor executor;
    K8sFaultHelper k8sFaultHelper;
    private FaultsMockData faultsMockData = new FaultsMockData();


    public static String getPodsListString() {
        return "app-inventory-service-243-0 app-inventory-service-243-1 app-inventory-service-243-2";
    }


    public static List<String> getPodsAsList() {
        List<String> list = new ArrayList<>();
        list.add("app-inventory-service-243-0");
        list.add("app-inventory-service-243-1");
        list.add("app-inventory-service-243-2");
        return list;
    }

    public static List<String> getServicesAsList() {
        List<String> list = new ArrayList<>();
        list.add("app-inventory-service-0");
        list.add("app-inventory-service-1");
        return list;
    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public void setUpBeforeClass() throws Exception {
        MockitoAnnotations.initMocks(this);
        faultsMockData = new FaultsMockData();
        k8sFaultHelper = new K8sFaultHelper(endpointClientFactory);
    }

    @Test(priority = 1)
    public void testGetExecutor() {
        ICommandExecutor executor = null;
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
        } catch (MangleException e) {
            log.error("testGetExecutor failed with Exception: ", e);
            Assert.assertTrue(false);
        }
        Assert.assertEquals(executor, kubernetesCommandLineClient);
    }

    @Test(priority = 2)
    public void testGetResouceList() throws MangleException {
        K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
        Mockito.when(endpointClientFactory.getEndPointClient(Mockito.any(), Mockito.any()))
                .thenReturn(kubernetesCommandLineClient);
        ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
        try {
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult(getPodsListString()));
            List<String> resources = k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
            Assert.assertEquals(resources, getPodsAsList());
        } catch (MangleException e) {
            log.error("getK8sDeleteResourceFault failed with Exception: ", e);
            Assert.assertTrue(false);
        }
        try {
            CommandExecutionResult commandOutput = new CommandExecutionResult();
            commandOutput.setCommandOutput("");
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any())).thenReturn(commandOutput);
            List<String> resources = k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
            Assert.assertFalse(true);
        } catch (MangleException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.INVALID_RESOURCE_LABELS);
        }
        try {
            CommandExecutionResult commandOutput = new CommandExecutionResult();
            commandOutput.setCommandOutput("");
            k8sFaultSpec.setResourceLabels(Collections.emptyMap());
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any())).thenReturn(commandOutput);
            k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
            Assert.assertFalse(true);
        } catch (MangleException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.INVALID_RESOURCE_LABELS);
        }

        try {
            CommandExecutionResult commandOutput = new CommandExecutionResult();
            commandOutput.setCommandOutput("");
            k8sFaultSpec.setResourceName("DummyResource");
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any())).thenReturn(commandOutput);
            k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
            Assert.assertFalse(true);
        } catch (MangleException e) {
            Assert.assertEquals(e.getErrorCode(), ErrorCode.INVALID_K8S_RESOURCE_NAME);
        }
    }

    @Test(priority = 3)
    public void testGetResouceListWithRandomInjectionTrue() {
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            k8sFaultSpec.setRandomInjection(true);
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);

            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult(getPodsListString()));
            List<String> resources = k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
            Assert.assertEquals(resources.size(), 1);
            Assert.assertTrue(getPodsAsList().toString().contains(resources.get(0)));
        } catch (MangleException e) {
            log.error("testGetResouceListWithRandomInjectionTrue failed with Exception: ", e);
            Assert.assertTrue(false);
        }
    }

    @Test(priority = 4)
    public void testGetResouceListWithInValidResourceLabels() {
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            k8sFaultSpec.setRandomInjection(true);
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);

            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult(getPodsListString()));
            List<String> resources = k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
        } catch (MangleException e) {
            log.error("testGetResouceListWithInValidResourceLabels failed with Exception: ", e);
            Assert.assertEquals(e.getErrorCode(), ErrorCode.INVALID_RESOURCE_LABELS);
        }
    }

    @Test(enabled = false, priority = 5)
    public void testGetResouceListWithEmptyResponseFromCommandExecutor() {
        Mockito.reset(kubernetesCommandLineClient);
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            k8sFaultSpec.setRandomInjection(true);
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult(""));
            List<String> resources = k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
        } catch (MangleException e) {
            log.error("testGetResouceListWithEmptyResponseFromCommandExecutor failed with Exception: ", e);
            Assert.assertEquals(e.getErrorCode(), ErrorCode.INVALID_RESOURCE_LABELS);
            Mockito.verify(kubernetesCommandLineClient, Mockito.times(6)).executeCommand(Mockito.any());
        }
    }

    @Test(enabled = false, priority = 6)
    public void testGetResouceListWithErrorResponseFromCommandExecutor() {
        Mockito.reset(kubernetesCommandLineClient);
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            k8sFaultSpec.setRandomInjection(true);
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult("error"));
            List<String> resources = k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
        } catch (MangleException e) {
            log.error("testGetResouceListWithEmptyResponseFromCommandExecutor failed with Exception: ", e);
            Assert.assertEquals(e.getErrorCode(), ErrorCode.INVALID_RESOURCE_LABELS);
            Mockito.verify(kubernetesCommandLineClient, Mockito.times(6)).executeCommand(Mockito.any());
        }
    }

    @Test(enabled = false, priority = 7)
    public void testGetResouceListWithNonZeroExitCodeResponseFromCommandExecutor() {
        Mockito.reset(kubernetesCommandLineClient);
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            k8sFaultSpec.setRandomInjection(true);
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
            CommandExecutionResult result = CommandResultUtils.getCommandResult("error");
            result.setExitCode(126);
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any())).thenReturn(result);
            List<String> resources = k8sFaultHelper.getResouceList(executor, k8sFaultSpec);
        } catch (MangleException e) {
            log.error("testGetResouceListWithEmptyResponseFromCommandExecutor failed with Exception: ", e);
            Assert.assertEquals(e.getErrorCode(), ErrorCode.INVALID_RESOURCE_LABELS);
            Mockito.verify(kubernetesCommandLineClient, Mockito.times(6)).executeCommand(Mockito.any());
        }
    }

    @Test(priority = 8)
    public void testGetInjectionCommandInfoListForDeleteResourceFault() {
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);

            k8sFaultSpec.setResourcesList(getPodsAsList());
            List<CommandInfo> injectionCommands = k8sFaultHelper.getInjectionCommandInfoList(executor, k8sFaultSpec);

            log.info(RestTemplateWrapper.objectToJson(injectionCommands));
            Assert.assertEquals(injectionCommands.size(), 1);
            Assert.assertEquals(injectionCommands.get(0),
                    getInjectionCommand(" delete  pod -l app=app-inventory-service"));
            List<CommandInfo> remediationCommands =
                    k8sFaultHelper.getRemediationCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(remediationCommands));
            Assert.assertEquals(remediationCommands.size(), 0);
        } catch (MangleException e) {
            log.error("testGetInjectionCommandInfoListForDeleteResourceFault failed with Exception: ", e);
            Assert.assertTrue(false);
        }
    }

    @Test(priority = 9, expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "No enum constant com.vmware.mangle.services.enums.K8SFaultName.testFaultName")
    public void testGetInjectionCommandInfoListForUnSupportedFault() {
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            Map<String, String> specificArgs = new HashMap<>();
            specificArgs.put("operation", "testFaultName");
            k8sFaultSpec.setArgs(specificArgs);
            k8sFaultSpec.setRandomInjection(true);
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
            Map<String, String> resourceLabels = new HashMap<>();
            k8sFaultSpec.setResourcesList(getPodsAsList());
            List<CommandInfo> injectionCommands = k8sFaultHelper.getInjectionCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(injectionCommands));
            Assert.assertEquals(injectionCommands.size(), 1);
            Assert.assertEquals(injectionCommands.get(0),
                    getInjectionCommand(" delete  pod -l app=app-inventory-service"));
            List<CommandInfo> remediationCommands =
                    k8sFaultHelper.getRemediationCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(remediationCommands));
            Assert.assertEquals(remediationCommands.size(), 0);
        } catch (MangleException e) {
            log.error("testGetInjectionCommandInfoListForUnSupportedFault failed with Exception: ", e);
            Assert.assertTrue(false);
        }
    }

    @Test(priority = 10)
    public void testGetInjectionCommandInfoListForDeleteResourceFaultWithRandomInjection() {
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getDeleteK8SResourceFaultSpec();
            k8sFaultSpec.setRandomInjection(true);
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);

            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult(getPodsListString()));
            k8sFaultSpec.setResourcesList(k8sFaultHelper.getResouceList(executor, k8sFaultSpec));
            List<CommandInfo> injectionCommands = k8sFaultHelper.getInjectionCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(injectionCommands));
            Assert.assertEquals(injectionCommands.size(), 1);
            Assert.assertTrue(verifyInjectionCommandInfoListForDeleteResource(injectionCommands.get(0),
                    getInjectionCommand(" delete  pod -l app=app-inventory-service")));
            List<CommandInfo> remediationCommands =
                    k8sFaultHelper.getRemediationCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(remediationCommands));
            Assert.assertEquals(remediationCommands.size(), 0);
        } catch (MangleException e) {
            log.error(
                    "testGetInjectionCommandInfoListForDeleteResourceFaultWithRandomInjection failed with Exception: ",
                    e);
            Assert.assertTrue(false);
        }
    }

    @Test(priority = 11)
    public void testgetInjectResourceNotReadyFault() {
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getK8SResourceNotReadyFaultSpec();
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);

            k8sFaultSpec.setResourcesList(getPodsAsList());
            log.info(k8sFaultSpec.getResourcesList());
            List<CommandInfo> injectionCommands = k8sFaultHelper.getInjectionCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(injectionCommands));
            List<CommandInfo> expectedCommands =
                    getExpectedInjectionCommandsForNonRandomResourceNotReadyFaultInjection();
            Assert.assertEquals(injectionCommands, expectedCommands);
            List<CommandInfo> remediationCommands =
                    k8sFaultHelper.getRemediationCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(remediationCommands));
            Assert.assertEquals(remediationCommands,
                    getExpectedRemediationCommandsForNonRandomResourceNotReadyFaultRemediation());
        } catch (MangleException e) {
            log.error("testgetInjectResourceNotReadyFault failed with Exception: ", e);
            Assert.assertTrue(false);
        }
    }

    @Test(priority = 12)
    public void testgetInjectServiceUnavailableFault() {
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getK8SServiceUnavailableFaultSpec();
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
            k8sFaultSpec.setResourcesList(getServicesAsList());
            log.info(k8sFaultSpec.getResourcesList());
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult(KubernetesTemplates.SERVICE_KEY_PREFIX + "app"));
            List<CommandInfo> injectionCommands = k8sFaultHelper.getInjectionCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(injectionCommands));
            List<CommandInfo> expectedCommands =
                    getExpectedInjectionCommandsForNonRandomServiceUnavailableFaultInjection();
            Assert.assertEquals(injectionCommands, expectedCommands);

            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult("error"));
            injectionCommands = k8sFaultHelper.getInjectionCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(injectionCommands));
            Assert.assertEquals(injectionCommands, Collections.EMPTY_LIST);

            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult(KubernetesTemplates.SERVICE_KEY_PREFIX));
            injectionCommands = k8sFaultHelper.getInjectionCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(injectionCommands));
            Assert.assertEquals(injectionCommands, Collections.EMPTY_LIST);
        } catch (MangleException e) {
            log.error("testgetInjectServiceUnavailableFault failed with Exception: ", e);
            Assert.assertTrue(false);
        }
    }

    @Test(priority = 13)
    public void testgetRemediateServiceUnavailableFault() {
        try {
            K8SFaultSpec k8sFaultSpec = faultsMockData.getK8SServiceUnavailableFaultSpec();
            Mockito.when(
                    endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                    .thenReturn(kubernetesCommandLineClient);
            ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
            k8sFaultSpec.setResourcesList(getServicesAsList());
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any())).thenReturn(CommandResultUtils
                    .getCommandResult(KubernetesTemplates.SELECTORS_PREFIX + "\"app\":\"app-inventory-service\""));
            List<CommandInfo> remediationCommands =
                    k8sFaultHelper.getRemediationCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(remediationCommands));
            Assert.assertEquals(remediationCommands,
                    getExpectedRemediationCommandsForServiceUnavailableFaultInjection());
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult("error"));
            remediationCommands = k8sFaultHelper.getRemediationCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(remediationCommands));
            Assert.assertEquals(remediationCommands, Collections.EMPTY_LIST);
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any()))
                    .thenReturn(CommandResultUtils.getCommandResult(KubernetesTemplates.SELECTORS_PREFIX));
            remediationCommands = k8sFaultHelper.getRemediationCommandInfoList(executor, k8sFaultSpec);
            log.info(RestTemplateWrapper.objectToJson(remediationCommands));
            Assert.assertEquals(remediationCommands, Collections.EMPTY_LIST);
        } catch (MangleException e) {
            log.error("testgetRemediateServiceUnavailableFault failed with Exception: ", e);
            Assert.assertTrue(false);
        }
    }

    @Test(priority = 14)
    public void testValidateResourceName() throws MangleException {
        K8SFaultSpec k8sFaultSpec = faultsMockData.getK8SServiceUnavailableFaultSpec();
        Mockito.when(endpointClientFactory.getEndPointClient(k8sFaultSpec.getCredentials(), k8sFaultSpec.getEndpoint()))
                .thenReturn(kubernetesCommandLineClient);
        ICommandExecutor executor = k8sFaultHelper.getExecutor(k8sFaultSpec);
        k8sFaultSpec.setResourcesList(getServicesAsList());
        CommandExecutionResult commandOutput = new CommandExecutionResult();
        try {
            commandOutput.setCommandOutput("success");
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any())).thenReturn(commandOutput);
            k8sFaultHelper.validateResourceName(executor, k8sFaultSpec.getResourceName(),
                    k8sFaultSpec.getResourceType());
        } catch (MangleException e) {
            log.error("testgetRemediateServiceUnavailableFault failed with Exception: ", e);
            Assert.assertTrue(false);
        }
        try {
            commandOutput.setExitCode(1);
            commandOutput.setCommandOutput("NotFound");
            Mockito.when(kubernetesCommandLineClient.executeCommand(Mockito.any())).thenReturn(commandOutput);
            k8sFaultHelper.validateResourceName(executor, k8sFaultSpec.getResourceName(),
                    k8sFaultSpec.getResourceType());
        } catch (MangleException e) {
            Assert.assertTrue(true);
        }
    }

    private boolean verifyInjectionCommandInfoListForDeleteResource(CommandInfo commandInfo,
            CommandInfo injectionCommand) {
        if (commandInfo.equals(getInjectionCommand(" delete  pod app-inventory-service-243-0"))) {
            return true;
        }
        if (commandInfo.equals(getInjectionCommand(" delete  pod app-inventory-service-243-1"))) {
            return true;
        }
        if (commandInfo.equals(getInjectionCommand(" delete  pod app-inventory-service-243-2"))) {
            return true;
        }
        return false;
    }

    public static List<CommandInfo> getExpectedInjectionCommandsForNonRandomResourceNotReadyFaultInjection() {
        List<CommandInfo> list = new ArrayList<>();
        CommandInfo pod1VerifyReadinessProbeCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-0 -o template  --template=\"{{range .spec.containers}}{{if eq .name \\\"testContainer\\\"}}{{if .readinessProbe}}ReadinessProbe Configured{{else}}ReadinessProbe not Configured for container testContainer {{end}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0)
                .expectedCommandOutputList(Arrays.asList("ReadinessProbe Configured")).build();

        CommandInfo pod1PatchCommand = CommandInfo.builder(
                " patch pod  app-inventory-service-243-0 -p '{\"spec\":{\"containers\":[{\"name\":\"testContainer\",\"image\":\"nginx\"}]}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo pod1VerifyReadyStateCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-0 -o template  --template=\"{{range .status.containerStatuses}}{{if eq .name \\\"testContainer\\\"}}{{.ready}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                .expectedCommandOutputList(Arrays.asList("false")).build();

        CommandInfo pod2VerifyReadinessProbeCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-1 -o template  --template=\"{{range .spec.containers}}{{if eq .name \\\"testContainer\\\"}}{{if .readinessProbe}}ReadinessProbe Configured{{else}}ReadinessProbe not Configured for container testContainer {{end}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0)
                .expectedCommandOutputList(Arrays.asList("ReadinessProbe Configured")).build();

        CommandInfo pod2PatchCommand = CommandInfo.builder(
                " patch pod  app-inventory-service-243-1 -p '{\"spec\":{\"containers\":[{\"name\":\"testContainer\",\"image\":\"nginx\"}]}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo pod2VerifyReadyStateCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-1 -o template  --template=\"{{range .status.containerStatuses}}{{if eq .name \\\"testContainer\\\"}}{{.ready}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                .expectedCommandOutputList(Arrays.asList("false")).build();

        CommandInfo pod3VerifyReadinessProbeCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-2 -o template  --template=\"{{range .spec.containers}}{{if eq .name \\\"testContainer\\\"}}{{if .readinessProbe}}ReadinessProbe Configured{{else}}ReadinessProbe not Configured for container testContainer {{end}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0)
                .expectedCommandOutputList(Arrays.asList("ReadinessProbe Configured")).build();

        CommandInfo pod3PatchCommand = CommandInfo.builder(
                " patch pod  app-inventory-service-243-2 -p '{\"spec\":{\"containers\":[{\"name\":\"testContainer\",\"image\":\"nginx\"}]}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo pod3VerifyReadyStateCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-2 -o template  --template=\"{{range .status.containerStatuses}}{{if eq .name \\\"testContainer\\\"}}{{.ready}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                .expectedCommandOutputList(Arrays.asList("false")).build();

        list.add(pod1VerifyReadinessProbeCommand);
        list.add(pod1PatchCommand);
        list.add(pod1VerifyReadyStateCommand);
        list.add(pod2VerifyReadinessProbeCommand);
        list.add(pod2PatchCommand);
        list.add(pod2VerifyReadyStateCommand);
        list.add(pod3VerifyReadinessProbeCommand);
        list.add(pod3PatchCommand);
        list.add(pod3VerifyReadyStateCommand);
        return list;
    }

    public static List<CommandInfo> getExpectedInjectionCommandsForNonRandomServiceUnavailableFaultInjection() {
        List<CommandInfo> list = new ArrayList<>();

        CommandInfo service1PatchCommand = CommandInfo
                .builder(" patch service  app-inventory-service-0 -p '{\"spec\":{\"selector\":{\"app\":\"mangle-fault\"}}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo service1VerifyEndpointsCommand =
                CommandInfo.builder(" get endpoints app-inventory-service-0 -o template  --template=\"{{.subsets}}\"")
                        .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                        .expectedCommandOutputList(Arrays.asList("<no value>")).build();

        CommandInfo service2PatchCommand = CommandInfo
                .builder(" patch service  app-inventory-service-1 -p '{\"spec\":{\"selector\":{\"app\":\"mangle-fault\"}}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo service2VerifyEndpointsCommand =
                CommandInfo.builder(" get endpoints app-inventory-service-1 -o template  --template=\"{{.subsets}}\"")
                        .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                        .expectedCommandOutputList(Arrays.asList("<no value>")).build();

        list.add(service1PatchCommand);
        list.add(service1VerifyEndpointsCommand);
        list.add(service2PatchCommand);
        list.add(service2VerifyEndpointsCommand);
        return list;
    }

    public static List<CommandInfo> getExpectedRemediationCommandsForServiceUnavailableFaultInjection() {
        List<CommandInfo> list = new ArrayList<>();

        CommandInfo service1PatchCommand = CommandInfo.builder(
                " patch service  app-inventory-service-0 -p '{\"spec\":{\"selector\":\"app\":\"app-inventory-service\"}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo service1VerifyEndpointsCommand =
                CommandInfo.builder(" get endpoints app-inventory-service-0 -o template  --template=\"{{.subsets}}\"")
                        .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                        .expectedCommandOutputList(Arrays.asList("addresses")).build();

        CommandInfo service2PatchCommand = CommandInfo.builder(
                " patch service  app-inventory-service-1 -p '{\"spec\":{\"selector\":\"app\":\"app-inventory-service\"}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo service2VerifyEndpointsCommand =
                CommandInfo.builder(" get endpoints app-inventory-service-1 -o template  --template=\"{{.subsets}}\"")
                        .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                        .expectedCommandOutputList(Arrays.asList("addresses")).build();

        list.add(service1PatchCommand);
        list.add(service1VerifyEndpointsCommand);
        list.add(service2PatchCommand);
        list.add(service2VerifyEndpointsCommand);
        return list;
    }

    public static List<CommandInfo> getExpectedRemediationCommandsForNonRandomResourceNotReadyFaultRemediation() {
        List<CommandInfo> list = new ArrayList<>();
        CommandInfo pod1PatchCommand = CommandInfo.builder(
                " patch pod  app-inventory-service-243-0 -p '{\"spec\":{\"containers\":[{\"name\":\"testContainer\",\"image\":\"app-inventory-service-243-0 app-inventory-service-243-1 app-inventory-service-243-2\"}]}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo pod1VerifyReadyStateCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-0 -o template  --template=\"{{range .status.containerStatuses}}{{if eq .name \\\"testContainer\\\"}}{{.ready}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                .expectedCommandOutputList(Arrays.asList("true")).build();

        CommandInfo pod2PatchCommand = CommandInfo.builder(
                " patch pod  app-inventory-service-243-1 -p '{\"spec\":{\"containers\":[{\"name\":\"testContainer\",\"image\":\"app-inventory-service-243-0 app-inventory-service-243-1 app-inventory-service-243-2\"}]}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo pod2VerifyReadyStateCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-1 -o template  --template=\"{{range .status.containerStatuses}}{{if eq .name \\\"testContainer\\\"}}{{.ready}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                .expectedCommandOutputList(Arrays.asList("true")).build();

        CommandInfo pod3PatchCommand = CommandInfo.builder(
                " patch pod  app-inventory-service-243-2 -p '{\"spec\":{\"containers\":[{\"name\":\"testContainer\",\"image\":\"app-inventory-service-243-0 app-inventory-service-243-1 app-inventory-service-243-2\"}]}}' ")
                .ignoreExitValueCheck(false).noOfRetries(0).retryInterval(0).timeout(0).build();

        CommandInfo pod3VerifyReadyStateCommand = CommandInfo.builder(
                " get pod app-inventory-service-243-2 -o template  --template=\"{{range .status.containerStatuses}}{{if eq .name \\\"testContainer\\\"}}{{.ready}}{{end}}{{end}}\"")
                .ignoreExitValueCheck(false).noOfRetries(30).retryInterval(10).timeout(0)
                .expectedCommandOutputList(Arrays.asList("true")).build();

        list.add(pod1PatchCommand);
        list.add(pod1VerifyReadyStateCommand);
        list.add(pod2PatchCommand);
        list.add(pod2VerifyReadyStateCommand);
        list.add(pod3PatchCommand);
        list.add(pod3VerifyReadyStateCommand);
        return list;
    }

    private CommandInfo getInjectionCommand(String command) {
        CommandInfo commandInfo = CommandInfo.builder(command).ignoreExitValueCheck(false).noOfRetries(0)
                .retryInterval(0).timeout(0).build();
        return commandInfo;
    }

}
