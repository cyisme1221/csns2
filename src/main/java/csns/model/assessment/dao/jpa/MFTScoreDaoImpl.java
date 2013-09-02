/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2013, Chengyu Sun (csun@calstatela.edu).
 * 
 * CSNS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * CSNS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with CSNS. If not, see http://www.gnu.org/licenses/agpl.html.
 */
package csns.model.assessment.dao.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import csns.model.academics.Department;
import csns.model.assessment.dao.MFTScoreDao;

@Repository
public class MFTScoreDaoImpl implements MFTScoreDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Date> getDates( Department department )
    {
        String query = "select distinct date from MFTScore "
            + " where department = :department order by date desc";

        return entityManager.createQuery( query, Date.class )
            .setParameter( "department", department )
            .getResultList();
    }

}