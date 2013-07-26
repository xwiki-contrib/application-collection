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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.collection.CollectionFilter;
import org.xwiki.collection.CollectionStore;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;

/**
 * Knows how static collections are stored.
 * 
 * @version $Id$
 */
@Component
@Singleton
@Named("static")
public class StaticCollectionStore implements CollectionStore
{
    @Inject
    private DocumentAccessBridge documentAccessBridge;

    @Inject
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @Override
    public void load(DocumentReference collectionReference, CollectionFilter filter)
    {
        DocumentReference collectionClassReference =
            new DocumentReference(collectionReference.getWikiReference().getName(), "Collections",
                "StaticCollectionClass");
        @SuppressWarnings("unchecked")
        List<String> pages =
            (List<String>) documentAccessBridge.getProperty(collectionReference, collectionClassReference, "pages");
        if (pages != null) {
            for (String page : pages) {
                filter.filter(documentReferenceResolver.resolve(page, collectionReference));
            }
        }
    }

    @Override
    public DocumentReference getTemplateReference()
    {
        return new DocumentReference(documentAccessBridge.getCurrentDocumentReference().getWikiReference().getName(),
            "Collections", "StaticCollectionTemplate");
    }
}
