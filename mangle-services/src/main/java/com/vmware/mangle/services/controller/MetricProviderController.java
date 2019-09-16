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

package com.vmware.mangle.services.controller;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.mangle.cassandra.model.metricprovider.MetricProviderSpec;
import com.vmware.mangle.model.enums.MetricProviderByStatus;
import com.vmware.mangle.model.response.MetricProviderResponse;
import com.vmware.mangle.services.MetricProviderService;
import com.vmware.mangle.services.cassandra.model.events.basic.EntityCreatedEvent;
import com.vmware.mangle.services.cassandra.model.events.basic.EntityUpdatedEvent;
import com.vmware.mangle.services.constants.CommonConstants;
import com.vmware.mangle.services.events.web.CustomEventPublisher;
import com.vmware.mangle.utils.constants.ErrorConstants;
import com.vmware.mangle.utils.constants.MetricProviderConstants;
import com.vmware.mangle.utils.exceptions.MangleException;
import com.vmware.mangle.utils.exceptions.handler.ErrorCode;

/**
 * Controller Class For Metric Provider Services.
 *
 * @author ashrimali
 */

@RestController
@RequestMapping("/rest/api/v1/metric-providers")
@Api("/rest/api/v1/metric-providers")
@Log4j2
public class MetricProviderController {

    @Autowired
    private MetricProviderService metricProviderService;

    @Autowired
    private CustomEventPublisher publisher;

    /**
     * API to get metric providers
     *
     * @param metricProviderByStatus
     * @return ResponseEntity<List<MetricProviderSpec>>
     * @throws MangleException
     */
    @ApiOperation(value = "API to get Metric Providers.", nickname = "getMetricProviders")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MetricProviderSpec>> getMetricProviders(
            @RequestParam(name = "metricProviderByStatus", required = false) MetricProviderByStatus metricProviderByStatus)
            throws MangleException {
        List<MetricProviderSpec> metricProviders = new ArrayList<>();
        if (null != metricProviderByStatus) {
            if (metricProviderByStatus.equals(MetricProviderByStatus.ACTIVE)) {
                log.info("API to get Active Metric Provider");
                MetricProviderSpec metricProvider = this.metricProviderService.getActiveMetricProvider();
                if (null != metricProvider) {
                    metricProviders.add(metricProvider);
                }
            } else if (metricProviderByStatus.equals(MetricProviderByStatus.ALL)) {
                log.info("API to get all Metric Providers.");
                metricProviders = this.metricProviderService.getAllMetricProviders();
            }
        } else {
            log.info("API to get all Metric Providers.");
            metricProviders = this.metricProviderService.getAllMetricProviders();
        }
        HttpHeaders headers = new HttpHeaders();
        if (metricProviders.isEmpty()) {
            headers.add(CommonConstants.MESSAGE_HEADER, MetricProviderConstants.METRIC_PROVIDERS_EMPTY);
        } else {
            headers.add(CommonConstants.MESSAGE_HEADER, MetricProviderConstants.METRIC_PROVIDERS_FOUND);
        }
        return new ResponseEntity<>(metricProviders, headers, HttpStatus.OK);
    }


    /**
     * API to add a metric provider
     *
     * @param metricProviderSpec
     * @return ResponseEntity<List<MetricProviderSpec>>
     * @throws MangleException
     */
    @ApiOperation(value = "API to add a Metric Provider", nickname = "addMetricProvider")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetricProviderSpec> addMetricProvider(
            @Validated @RequestBody MetricProviderSpec metricProviderSpec) throws MangleException {
        log.info("API to add a Metric Provider");
        if (this.metricProviderService.getMetricProviderByType(metricProviderSpec.getMetricProviderType()).isEmpty()) {
            this.metricProviderService.testConnectionMetricProvider(metricProviderSpec);
            MetricProviderSpec metricSpec = this.metricProviderService.addMetricProvider(metricProviderSpec);
            HttpHeaders headers = new HttpHeaders();
            headers.add(CommonConstants.MESSAGE_HEADER, MetricProviderConstants.METRIC_PROVIDER_CREATED);
            publisher.publishAnEvent(
                    new EntityCreatedEvent(metricSpec.getPrimaryKey(), metricSpec.getClass().getName()));
            return new ResponseEntity<>(metricSpec, headers, HttpStatus.OK);

        } else {
            log.debug("Can't add more than one metric provider of same type");
            throw new MangleException(ErrorConstants.SAME_RECORD, ErrorCode.SAME_RECORD, metricProviderSpec.getName());
        }

    }

    /**
     * API to update an existing metric provider
     *
     * @param metricProviderSpec
     * @return ResponseEntity<List<MetricProviderSpec>>
     * @throws MangleException
     */
    @ApiOperation(value = "API to update a Metric Provider", nickname = "updateMetricProvider")
    @PutMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetricProviderSpec> updateMetricProvider(
            @Validated @RequestBody MetricProviderSpec metricProviderSpec) throws MangleException {
        log.info("API to update a  Metric Provider");
        this.metricProviderService.testConnectionMetricProvider(metricProviderSpec);
        MetricProviderSpec resultSpec =
                this.metricProviderService.updateMetricProviderByName(metricProviderSpec.getName(), metricProviderSpec);
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.MESSAGE_HEADER, MetricProviderConstants.METRIC_PROVIDER_UPDATED);
        publisher.publishAnEvent(new EntityUpdatedEvent(resultSpec.getPrimaryKey(), resultSpec.getClass().getName()));
        return new ResponseEntity<>(resultSpec, headers, HttpStatus.OK);
    }

    /**
     * API to delete metric provider
     *
     * @param metricProviderName
     * @return ResponseEntity<MetricProviderResponse>
     * @throws MangleException
     */
    @ApiOperation(value = "API to delete Metric Provider by name", nickname = "deleteMetricProvider")
    @DeleteMapping(value = "")
    public ResponseEntity deleteMetricProvider(@RequestParam("metricProviderName") String metricProviderName)
            throws MangleException {
        log.info("API to delete Metric Provider");
        if (StringUtils.isEmpty(metricProviderName)) {
            this.metricProviderService.deleteAllMetricProviders();
        } else {
            this.metricProviderService.deleteMetricProvider(metricProviderName);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * API to test connection of a metric provider
     *
     * @param metricProviderSpec
     * @return ResponseEntity<MetricProviderResponse>
     * @throws MangleException
     */
    @ApiOperation(value = "API to test connection of  Metric Provider", nickname = "testConnection")
    @PostMapping(value = "/test-connection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetricProviderResponse> testConnection(
            @Validated @RequestBody MetricProviderSpec metricProviderSpec) throws MangleException {
        log.info("API to test connection of  Metric Provider");
        boolean status = this.metricProviderService.testConnectionMetricProvider(metricProviderSpec);
        if (!status) {
            throw new MangleException(ErrorConstants.TEST_CONNECTION_FAILED_METRICPROVIDER,
                    ErrorCode.TEST_CONNECTION_FAILED, metricProviderSpec.getName());
        }
        MetricProviderResponse mangleResponse = new MetricProviderResponse();
        mangleResponse.setResultStatus(status);
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.MESSAGE_HEADER, CommonConstants.TEST_CONNECTION_SUCCESS);
        return new ResponseEntity<>(mangleResponse, headers, HttpStatus.OK);
    }

    /**
     * API to Change Metric Provider Status
     *
     * @param metricProviderName
     * @return ResponseEntity<MetricProviderResponse>
     * @throws MangleException
     */
    @ApiOperation(value = "API to Change Metric Provider Status", nickname = "changeMetricProviderStatus")
    @PostMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetricProviderResponse> changeMetricProviderStatus(
            @RequestParam("metricProviderName") String metricProviderName) throws MangleException {
        log.info("API to Change  Metric Provider status");
        this.metricProviderService.enableMetricProviderByName(metricProviderName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.MESSAGE_HEADER, MetricProviderConstants.METRIC_PROVIDER_UPDATED);
        MetricProviderResponse mangleResponse = new MetricProviderResponse();
        mangleResponse.setResultStatus(true);
        return new ResponseEntity<>(mangleResponse, headers, HttpStatus.OK);
    }

    /**
     * API to Change status of Mangle Metrics Collection status
     *
     * @param enableMangleMetrics
     * @return ResponseEntity<MetricProviderResponse>
     * @throws MangleException
     */
    @ApiOperation(value = "API to Change status of Mangle Metrics Collection status", nickname = "changeMangleMetricCollectionStatus")
    @PostMapping(value = "/mangle-metrics-collection-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetricProviderResponse> changeMangleMetricCollectionStatus(
            @RequestParam("enableMangleMetrics") Boolean enableMangleMetrics) throws MangleException {
        log.info("API to Change status of Mangle Metrics Collection status");
        if (enableMangleMetrics) {
            boolean status = false;
            status = this.metricProviderService.sendMetrics();
            this.metricProviderService.triggerMultiNodeResync("");
            MetricProviderResponse mangleResponse = new MetricProviderResponse();
            mangleResponse.setResultStatus(status);
            HttpHeaders headers = new HttpHeaders();
            headers.add(CommonConstants.MESSAGE_HEADER, MetricProviderConstants.METRIC_PROVIDER_ACTIVATED);
            return new ResponseEntity<>(mangleResponse, headers, HttpStatus.OK);
        } else {
            boolean status = this.metricProviderService.closeAllMetricCollection();
            this.metricProviderService.triggerMultiNodeResync("");
            MetricProviderResponse mangleResponse = new MetricProviderResponse();
            mangleResponse.setResultStatus(status);
            HttpHeaders headers = new HttpHeaders();
            headers.add(CommonConstants.MESSAGE_HEADER, MetricProviderConstants.METRIC_PROVIDER_DEACTIVATED);
            publisher.publishAnEvent(new EntityUpdatedEvent("Deactivated all Metrics"));
            return new ResponseEntity<>(mangleResponse, headers, HttpStatus.OK);
        }
    }


    /**
     * API to get status of Mangle Metrics Collection
     *
     * @return ResponseEntity<MetricProviderResponse>
     */
    @ApiOperation(value = "API to get status of Mangle Metrics Collection", nickname = "getMetricProviders")
    @GetMapping(value = "/mangle-metrics-collection-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MetricProviderResponse> getMetricCollectionStatus() {
        log.info("API to get status of Mangle Metrics Collection");
        boolean status = this.metricProviderService.isMangleMetricsEnabled();
        MetricProviderResponse mangleResponse = new MetricProviderResponse();
        mangleResponse.setResultStatus(status);
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.MESSAGE_HEADER, MetricProviderConstants.SENDING_MANGLE_METRICS_STATUS);
        return new ResponseEntity<>(mangleResponse, headers, HttpStatus.OK);
    }


}
