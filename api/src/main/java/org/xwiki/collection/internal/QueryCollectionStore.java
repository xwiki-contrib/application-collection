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

import org.slf4j.Logger;
import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.collection.CollectionFilter;
import org.xwiki.collection.CollectionStore;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.query.Query;
import org.xwiki.query.QueryManager;

/**
 * Knows how query collections are stored.
 * 
 * @version $Id$
 */
@Component
@Singleton
@Named("query")
public class QueryCollectionStore implements CollectionStore
{
    @Inject
    private Logger logger;

    @Inject
    private DocumentAccessBridge documentAccessBridge;

    @Inject
    @Named("secure")
    private QueryManager queryManager;

    @Inject
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @Override
    public void load(DocumentReference collectionReference, CollectionFilter filter)
    {
        DocumentReference collectionClassReference =
            new DocumentReference(collectionReference.getWikiReference().getName(), "Collections",
                "QueryCollectionClass");
        try {
            String statement = documentAccessBridge.getDocumentContentForDefaultLanguage(collectionReference);
            String type =
                (String) documentAccessBridge.getProperty(collectionReference, collectionClassReference, "type");
            Query query = queryManager.createQuery(statement, type);
            query.setWiki(collectionReference.getWikiReference().getName());
            for (Object documentName : query.execute()) {
                filter.filter(documentReferenceResolver.resolve((String) documentName, collectionReference));
            }
        } catch (Exception e) {
            logger.error("Failed to load query collection.", e);
        }
    }

    @Override
    public DocumentReference getTemplateReference()
    {
        return new DocumentReference(documentAccessBridge.getCurrentDocumentReference().getWikiReference().getName(),
            "Collections", "QueryCollectionTemplate");
    }
}
