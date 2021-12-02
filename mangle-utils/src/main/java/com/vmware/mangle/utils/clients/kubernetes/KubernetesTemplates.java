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

package com.vmware.mangle.utils.clients.kubernetes;

/**
 * @author bkaranam
 */
public class KubernetesTemplates {

    private KubernetesTemplates() {
    }

    //kubectl get command json templates
    public static final String TEMPLATE_ERROR_MESSAGE = "Error executing template";
    public static final String GET_METADATA_NAME = "\"{.items[*].metadata.name}\"";
    public static final String TEMPLATE_OUTPUT = "-o template  --template=";
    public static final String GET = " get ";
    public static final String DELETE = " delete ";
    public static final String DRAIN = "drain ";
    public static final String PATCH = " patch ";
    public static final String CORDON = " cordon ";
    public static final String UNCORDON = " uncordon ";
    public static final String PODS = "pods ";
    public static final String POD = "pod ";
    public static final String NODE = "node ";
    public static final String SERVICE = "service ";
    public static final String NODES = "nodes ";
    public static final String NAMESPACE = "namespace ";
    public static final String SERVICES = "services ";
    public static final String ENDPOINTS = "endpoints ";
    public static final String RESOURCE = "%s ";
    public static final String SELECTORS_PREFIX = "selectors#";
    public static final String SERVICE_KEY_PREFIX = "key#";
    public static final String OUTPUT_JSONPATH = "-o=jsonpath=";
    private static final String K8S_LABEL_EXPRESSION = " -l \"%s\" ";
    public static final String GET_NODES_JSONPATH = GET + NODES + OUTPUT_JSONPATH;
    public static final String GET_NODE_PODCIDR = GET + NODE + "%s " + OUTPUT_JSONPATH + "\"{.spec.podCIDR}\"";
    public static final String GET_PODS_JSONPATH = GET + PODS + OUTPUT_JSONPATH;
    public static final String GET_POD = GET + POD + " \"%s\"";
    public static final String METADATA_NAME_TEMPLATE =
            TEMPLATE_OUTPUT + "\"{{range.items}}{{.metadata.name}} {{end}}\"";
    public static final String GET_RESOURCES_WITH_LABELS = GET + "%s -l %s " + METADATA_NAME_TEMPLATE;
    public static final String GET_SERVICES_JSONPATH = GET + SERVICES + OUTPUT_JSONPATH;
    public static final String GET_ALLPODS_IN_NODE = GET + PODS + TEMPLATE_OUTPUT
            + "\"{{range.items}}{{if eq .spec.nodeName \\\"%s\\\"}}{{.metadata.name}} {{end}}{{end}}\"";
    public static final String GET_ALLPOD_USING_LABELS = GET + PODS + K8S_LABEL_EXPRESSION + TEMPLATE_OUTPUT
            + "\"{{range.items}}{{\\$name:=.metadata.name}}{{range .status.conditions}}{{if (and (eq .type \\\"Ready\\\") (eq .status \\\"True\\\"))}}{{\\$name}} {{end}}{{end}}{{end}}\"";
    public static final String GET_PODNODE_MAP = GET + PODS + TEMPLATE_OUTPUT
            + "\"{{range.items}}{{if eq .status.phase \\\"Running\\\"}}{{.metadata.name}}:{{.spec.nodeName}} {{end}}{{end}}\"";
    public static final String GET_ALLPOD_IPS_USING_LABELS =
            GET + PODS + K8S_LABEL_EXPRESSION + OUTPUT_JSONPATH + "\"{.items[*].status.podIP}\"";
    public static final String GET_ALLPOD_NODES_USING_LABELS =
            GET + PODS + K8S_LABEL_EXPRESSION + TEMPLATE_OUTPUT + "\"{{range.items}}{{.spec.nodeName}} {{end}}\"";
    public static final String GET_NODE_EXTERNAL_IP_MAP = GET + NODES + TEMPLATE_OUTPUT
            + "\"{{range.items}}{{.metadata.name}},{{range.status.addresses}}{{if eq .type \\\"ExternalIP\\\"}}{{.address}} {{end}}{{end}}{{end}}\"";
    public static final String CHECK_AVAILABILITY_NODE = GET + NODES + TEMPLATE_OUTPUT
            + "\"{{range.items}}{{\\$nodeName := .}}{{range.status.conditions}}{{if (and (eq .type \\\"Ready\\\") (eq .status \\\"True\\\"))}}{{\\$nodeName.metadata.name}}{{end}}{{end}} {{end}}\"";
    public static final String GET_SERVICE_APP_NAMES =
            GET + SERVICES + TEMPLATE_OUTPUT + "\"{{range.items}}{{.metadata.labels.app}} {{end}}\"";

    public static final String BUSYBOX_POD_NAME_TEMPLATE = "busybox-%s";
    public static final String KUBECTL_RUN_BUSYBOX_CURL_TEMPLATE =
            " run %s --image=radial/busyboxplus:curl -i --restart=Never --rm --command -- ";
    public static final String API_GATEWAY_POD_LABELS = "app=heimdall";
    public static final String API_GATEWAY_HTTP_SERVICE_NAME = "heimdall-service-http";
    public static final String SYMPHONY_POD_LABELS = "app=symphony";
    public static final String VRBC_ADAPTER_POD_LABELS = "app=vrbc-adapter";
    public static final String VRBC_COST_ANALYSIS_POD_LABELS = "app=vrbc-cost-analysis";
    public static final String VRBC_ONBOARDING_POD_LABELS = "app=vrbc-onboarding";
    public static final String VRBC_UI_POD_LABELS = "app=vrbc-ui";
    public static final String GET_SERVICE_LB_HOSTNAME =
            GET + SERVICE + " %s " + OUTPUT_JSONPATH + "\"{.status.loadBalancer.ingress[0].hostname}\"";
    public static final String RESTART_PODS_WITH_LABELS = DELETE + PODS + K8S_LABEL_EXPRESSION + " ";
    public static final String RESTART_POD_WITH_NAME = DELETE + POD + " \"%s\"";
    public static final String CSP_SERVICE_NAME = "csp-service";
    public static final String NGINX_CONTAINER_IMAGE = "nginx";
    public static final String PATCH_CONTAINER_IMAGE_OF_POD =
            PATCH + POD + " %s -p '{\"spec\":{\"containers\":[{\"name\":\"%s\",\"image\":\"%s\"}]}}' ";
    public static final String PATCH_SERVICE_WITH_SELECTORS =
            PATCH + SERVICE + " %s -p '{\"spec\":{\"selector\":%s}}' ";
    public static final String GET_CONTAINER_IMAGE = GET + POD + "%s " + TEMPLATE_OUTPUT
            + "\"{{range .spec.containers}}{{if eq .name \\\"%s\\\"}}{{.image}}{{end}}{{end}}\"";
    public static final String IS_CONTAINER_HAS_READINESS_PROBE = GET + POD + "%s " + TEMPLATE_OUTPUT
            + "\"{{range .spec.containers}}{{if eq .name \\\"%s\\\"}}{{if .readinessProbe}}ReadinessProbe Configured{{else}}ReadinessProbe not Configured for container %s {{end}}{{end}}{{end}}\"";
    public static final String IS_CONTAINER_READY = GET + POD + "%s " + TEMPLATE_OUTPUT
            + "\"{{range .status.containerStatuses}}{{if eq .name \\\"%s\\\"}}{{.ready}}{{end}}{{end}}\"";
    public static final String GET_SERVICE_ENDPOINTS = GET + ENDPOINTS + "%s " + TEMPLATE_OUTPUT + "\"{{.subsets}}\"";
    public static final String MANGLE_FAULT_LABEL = "mangle-fault";
    public static final String DEFAULT_MANGLE_SERVICE_SELECTORS = "{\"%s\":\"" + MANGLE_FAULT_LABEL + "\"}";
    public static final String GET_SERVICE_SELECTORS = GET + SERVICE + "%s " + TEMPLATE_OUTPUT + "\"" + SELECTORS_PREFIX
            + "{{\\\"{\\\"}}{{\\$i:=0}}{{range \\$k,\\$v := .spec.selector}}{{if eq \\$i 1}},{{end}}\\\"{{\\$k}}\\\":\\\"{{\\$v}}\\\"{{\\$i = 1}}{{end}}{{\\\"}\\\"}}\"";
    public static final String GET_SERVICE_SELECTORS_KEY = GET + SERVICE + "%s " + TEMPLATE_OUTPUT
            + "\"{{\\$key:=0}}{{range \\$k,\\$v := .spec.selector}}{{\\$key = \\$k}}{{end}}" + SERVICE_KEY_PREFIX
            + "{{\\$key}}\"";
    public static final String GET_RESOURCES_JSONPATH = GET + RESOURCE + OUTPUT_JSONPATH;
    public static final String IGNORE_TEMPLATE = "--ignore-daemonsets --delete-emptydir-data";
}
