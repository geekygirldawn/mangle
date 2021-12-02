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

package com.vmware.mangle;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import com.vmware.mangle.utils.constants.Constants;

/**
 * class is used to boot up the fiassco service.
 *
 * @author kumargautam
 */
@SpringBootApplication(exclude = { CassandraAutoConfiguration.class, CassandraDataAutoConfiguration.class })
@Log4j2
public class MangleApplication {
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        log.info("Application initialization inprogress...");
        context = SpringApplication.run(MangleApplication.class, args);
        log.info("Application initialization completed...");
        if (Constants.isSchemaMigrated()) {
            log.info("Restarting the application after schema upgrade");
            restart();
        }
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);
        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(MangleApplication.class, args.getSourceArgs());
        });
        thread.setDaemon(false);
        thread.start();
    }
}
