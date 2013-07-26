/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.collection;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.descriptor.ComponentDescriptor;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.script.service.ScriptService;

/**
 * Exposes the various types of collections to scripts.
 * 
 * @version $Id$
 */
@Component
@Singleton
@Named("collection")
public class CollectionScriptService implements ScriptService
{
    @Inject
    private Logger logger;

    @Inject
    private DocumentAccessBridge documentAccessBridge;

    @Inject
    @Named("context")
    private Provider<ComponentManager> contextComponentManager;

    /**
     * @return the list of available collection types
     */
    public List<String> getTypes()
    {
        List<String> types = new ArrayList<String>();
        for (ComponentDescriptor< ? > descriptor : contextComponentManager.get().getComponentDescriptorList(
            (Type) CollectionStore.class)) {
            types.add(descriptor.getRoleHint());
        }
        return types;
    }

    /**
     * Load the collection described by the specified document.
     * 
     * @param collectionReference a reference to the document that describes the collection
     * @return the collection
     */
    public Collection<DocumentReference> get(DocumentReference collectionReference)
    {
        if (documentAccessBridge.isDocumentViewable(collectionReference)) {
            DocumentReference collectionClassReference =
                new DocumentReference(collectionReference.getWikiReference().getName(), "Collections",
                    "CollectionClass");
            String type =
                (String) documentAccessBridge.getProperty(collectionReference, collectionClassReference, "type");
            if (type != null) {
                try {
                    CollectionStore store = contextComponentManager.get().getInstance(CollectionStore.class, type);
                    final Collection<DocumentReference> collection = new ArrayList<DocumentReference>();
                    store.load(collectionReference, new CollectionFilter()
                    {
                        @Override
                        public boolean filter(DocumentReference documentReference)
                        {
                            if (documentAccessBridge.isDocumentViewable(documentReference)) {
                                collection.add(documentReference);
                                return true;
                            }
                            return false;
                        }
                    });
                    return collection;
                } catch (ComponentLookupException e) {
                    logger.error("Failed to load [{}] collection.", type, e);
                }
            }
        }
        return null;
    }

    /**
     * Access the store for the specified collection type.
     * 
     * @param collectionType the collection type
     * @return the store for the specified collection type
     */
    public CollectionStore get(String collectionType)
    {
        try {
            return contextComponentManager.get().getInstance(CollectionStore.class, collectionType);
        } catch (ComponentLookupException e) {
            logger.error("Unknown collection type: [{}]", collectionType, e);
        }
        return null;
    }
}
