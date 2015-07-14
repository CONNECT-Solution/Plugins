/*
 * Copyright (c) 2013, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.connectopensource.interopgui.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * Template class for performing JPA operations wrapped in a single transaction using the entity manager. Manages the
 * EntityManagerFactory. 
 * @param <T> type of object instances returned in list.
 */
public abstract class AbstractJpaTemplate<T> {

    private static final EntityManagerFactory emFactory = Persistence
            .createEntityManagerFactory("org.connectopensource.interopgui.jpa");

    /**
     * @return entity manager
     */
    protected EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    /**
     * Execute a persistence operation using entity manager.
     * @return results of query
     */
    public List<T> execute() {        
        EntityManager entityManager = getEntityManager();
        try {    
            return handleTransaction(entityManager);            
        } finally {
            entityManager.close();
        }
    }

    /**
     * handle a single transaction considering resource closing and rollback.
     * @param entityManager used to perform persistence operation
     * @return results of query
     */
    private List<T> handleTransaction(EntityManager entityManager) {

        List<T> results;
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();        
            results = execute(entityManager);        
            transaction.commit();
        } finally {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();                    
            }
        }
        return results;
    }
    
    /**
     * Execute persistence operation using the provided entity manager and possibly return some results.
     * @param entityManager used to perform persistence operation
     * @return results of query
     */
    protected abstract List<T> execute(EntityManager entityManager);
    
}
