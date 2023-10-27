package org.apache.bigtop.manager.server.config;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class EclipseLinkConfig extends JpaBaseConfiguration {

    @Resource
    private JpaProperties properties;

    protected EclipseLinkConfig(DataSource ds, JpaProperties props, ObjectProvider<JtaTransactionManager> txm) {
        super(ds, props, txm);
    }

    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipseLinkJpaVendorAdapter();
    }

    protected Map<String, Object> getVendorProperties() {
        return new HashMap<>(properties.getProperties());
    }
}