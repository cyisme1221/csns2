/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2012, Chengyu Sun (csun@calstatela.edu).
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
package csns.web.controller;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import csns.model.academics.Assignment;
import csns.model.academics.Section;
import csns.model.academics.dao.AssignmentDao;
import csns.model.academics.dao.SectionDao;
import csns.web.editor.CalendarPropertyEditor;
import csns.web.validator.AssignmentValidator;

@Controller
@SessionAttributes("assignment")
public class AssignmentController {

    @Autowired
    SectionDao sectionDao;

    @Autowired
    AssignmentDao assignmentDao;

    @Autowired
    AssignmentValidator assignmentValidator;

    @InitBinder
    public void initBinder( WebDataBinder binder )
    {
        binder.registerCustomEditor( Calendar.class,
            new CalendarPropertyEditor() );
    }

    @RequestMapping(value = "/assignment/add", method = RequestMethod.GET)
    public String add( @RequestParam Long sectionId, ModelMap models )
    {
        Assignment assignment = new Assignment();
        Section section = sectionDao.getSection( sectionId );
        assignment.setSection( section );

        models.addAttribute( "assignment", assignment );
        return "assignment/add";
    }

    @RequestMapping(value = "/assignment/add", method = RequestMethod.POST)
    public String add( @ModelAttribute Assignment assignment,
        BindingResult result, SessionStatus sessionStatus )
    {
        assignmentValidator.validate( assignment, result );
        if( result.hasErrors() ) return "assignment/add";

        if( !StringUtils.hasText( assignment.getAlias() ) )
            assignment.setAlias( assignment.getName() );
        assignment = assignmentDao.saveAssignment( assignment );

        sessionStatus.setComplete();
        return "redirect:/section/taught#section-"
            + assignment.getSection().getId();
    }

    @RequestMapping(value = "/assignment/edit", method = RequestMethod.GET)
    public String edit( @RequestParam Long id, ModelMap models )
    {
        models.addAttribute( "assignment", assignmentDao.getAssignment( id ) );
        return "assignment/edit";
    }

    @RequestMapping(value = "/assignment/edit", method = RequestMethod.POST)
    public String edit( @ModelAttribute Assignment assignment,
        BindingResult result, SessionStatus sessionStatus )
    {
        assignmentValidator.validate( assignment, result );
        if( result.hasErrors() ) return "assignment/edit";

        if( !StringUtils.hasText( assignment.getAlias() ) )
            assignment.setAlias( assignment.getName() );
        assignment = assignmentDao.saveAssignment( assignment );

        sessionStatus.setComplete();
        return "redirect:/section/taught#section-"
            + assignment.getSection().getId();
    }

    @RequestMapping(value = "/assignment/delete")
    public String delete( @RequestParam Long id )
    {
        Assignment assignment = assignmentDao.getAssignment( id );
        Section section = assignment.getSection();
        assignment.setSection( null );
        assignmentDao.saveAssignment( assignment );

        return "redirect:/section/taught#section-" + section.getId();
    }

}