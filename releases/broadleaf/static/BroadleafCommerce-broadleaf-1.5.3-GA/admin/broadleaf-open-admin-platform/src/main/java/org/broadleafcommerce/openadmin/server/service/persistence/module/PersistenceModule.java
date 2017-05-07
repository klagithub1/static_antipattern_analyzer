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

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public interface PersistenceModule {

	public boolean isCompatible(OperationType operationType);
	
	public Entity add(PersistencePackage persistencePackage) throws ServiceException;
	
	public void updateMergedProperties(PersistencePackage persistencePackage, Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties) throws ServiceException;
	
	public void extractProperties(Map<MergedPropertyType, Map<String, FieldMetadata>> mergedProperties, List<Property> properties) throws NumberFormatException;
	
	public Entity update(PersistencePackage persistencePackage) throws ServiceException;
	
	public void remove(PersistencePackage persistencePackage) throws ServiceException;
	
	public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;
	
	public void setPersistenceManager(PersistenceManager persistenceManager);
	
}
