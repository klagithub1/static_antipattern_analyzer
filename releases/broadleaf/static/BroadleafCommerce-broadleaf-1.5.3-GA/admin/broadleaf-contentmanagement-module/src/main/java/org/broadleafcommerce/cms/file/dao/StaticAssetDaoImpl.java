/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.file.dao;

import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxImpl;
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by bpolster.
 */
@Repository("blStaticAssetDao")
public class StaticAssetDaoImpl implements StaticAssetDao {

    private static SandBox DUMMY_SANDBOX = new SandBoxImpl();
    {
        DUMMY_SANDBOX.setId(-1l);
    }

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public StaticAsset readStaticAssetById(Long id) {
        return (StaticAsset) em.find(entityConfiguration.lookupEntityClass(StaticAsset.class.getName()), id);
    }

    @Override
    public StaticAsset readStaticAssetByFullUrl(String fullUrl, SandBox targetSandBox) {
        TypedQuery<StaticAsset> query;
        if (targetSandBox == null) {
            query = em.createNamedQuery("BC_READ_STATIC_ASSET_BY_FULL_URL_AND_TARGET_SANDBOX_NULL", StaticAsset.class);
            query.setParameter("fullUrl", fullUrl);
        } else {
            query = em.createNamedQuery("BC_READ_STATIC_ASSET_BY_FULL_URL", StaticAsset.class);
            query.setParameter("targetSandbox", targetSandBox);
            query.setParameter("fullUrl", fullUrl);
        }

        List<StaticAsset> results = query.getResultList();
        if (CollectionUtils.isEmpty(results)) {
            return null;
        } else {
            return results.iterator().next();
        }
    }

    @Override
    public StaticAsset addOrUpdateStaticAsset(StaticAsset asset, boolean clearLevel1Cache) {
        if (clearLevel1Cache) {
            em.detach(asset);
        }
        return em.merge(asset);
    }

    @Override
    public void delete(StaticAsset asset) {
        if (!em.contains(asset)) {
            asset = (StaticAsset) readStaticAssetById(asset.getId());
        }
        em.remove(asset);
    }

}
