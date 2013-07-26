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
package org.xwiki.collection.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.collection.CollectionFilter;
import org.xwiki.collection.CollectionStore;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

/**
 * Knows how hierarchy (parent-child) collections are stored.
 * 
 * @version $Id$
 */
@Component
@Singleton
@Named("hierarchy")
public class HierarchyCollectionStore implements CollectionStore
{
    @Inject
    private DocumentAccessBridge documentAccessBridge;

    @Override
    public void load(DocumentReference collectionReference, CollectionFilter filter)
    {
        DocumentReference collectionClassReference =
            new DocumentReference(collectionReference.getWikiReference().getName(), "Collections",
                "HierarchyCollectionClass");
        String root = (String) documentAccessBridge.getProperty(collectionReference, collectionClassReference, "root");
        // TODO: Collect all descendants of the root document using the parent-child relationship.
    }

    @Override
    public DocumentReference getTemplateReference()
    {
        return new DocumentReference(documentAccessBridge.getCurrentDocumentReference().getWikiReference().getName(),
            "Collections", "HierarchyCollectionTemplate");
    }
}
