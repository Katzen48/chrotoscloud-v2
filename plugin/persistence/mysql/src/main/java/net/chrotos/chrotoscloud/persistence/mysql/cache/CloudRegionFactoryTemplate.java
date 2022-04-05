package net.chrotos.chrotoscloud.persistence.mysql.cache;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import java.util.Map;

public class CloudRegionFactoryTemplate extends RegionFactoryTemplate {
    @Override
    protected StorageAccess createQueryResultsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        return new CloudStorageAccess(regionName);
    }

    @Override
    protected StorageAccess createTimestampsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        return new CloudStorageAccess(regionName);
    }

    @Override
    protected void prepareForUse(SessionFactoryOptions settings, Map configValues) {}

    @Override
    protected void releaseFromUse() {}
}
