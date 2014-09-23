/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2014, Chengyu Sun (csun@calstatela.edu).
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import csns.model.academics.Course;
import csns.model.academics.Quarter;
import csns.model.academics.Section;
import csns.model.academics.dao.CourseDao;
import csns.model.academics.dao.SectionDao;
import csns.model.core.Resource;
import csns.model.core.ResourceType;
import csns.model.core.User;
import csns.model.site.Block;
import csns.model.site.Item;
import csns.model.site.Site;
import csns.model.site.dao.BlockDao;
import csns.model.site.dao.SiteDao;
import csns.security.SecurityUtils;
import csns.util.FileIO;

@Controller
@SessionAttributes({ "block", "item" })
public class SiteBlockControllerS {

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private BlockDao blockDao;

    @Autowired
    private FileIO fileIO;

    private static final Logger logger = LoggerFactory.getLogger( SiteBlockControllerS.class );

    private Section getSection( String qtr, String cc, int sn )
    {
        Quarter quarter = new Quarter();
        quarter.setShortString( qtr );
        Course course = courseDao.getCourse( cc );
        return sectionDao.getSection( quarter, course, sn );
    }

    @RequestMapping(value = "/site/{qtr}/{cc}-{sn}/block/add",
        method = RequestMethod.GET)
    public String add( @PathVariable String qtr, @PathVariable String cc,
        @PathVariable int sn, ModelMap models )
    {
        models.put( "section", getSection( qtr, cc, sn ) );
        models.put( "block", new Block() );
        models.put( "blockTypes", Block.Type.values() );
        return "site/block/add";
    }

    @RequestMapping(value = "/site/{qtr}/{cc}-{sn}/block/add",
        method = RequestMethod.POST)
    public String add( @PathVariable String qtr, @PathVariable String cc,
        @PathVariable int sn, @ModelAttribute Block block,
        BindingResult result, SessionStatus sessionStatus )
    {
        User user = SecurityUtils.getUser();
        Site site = getSection( qtr, cc, sn ).getSite();
        site.getBlocks().add( block );
        site = siteDao.saveSite( site );
        sessionStatus.setComplete();

        logger.info( user.getUsername() + " added a block to site "
            + site.getId() );

        return "redirect:list";
    }

    @RequestMapping(value = "/site/{qtr}/{cc}-{sn}/block/edit",
        method = RequestMethod.GET)
    public String edit( @PathVariable String qtr, @PathVariable String cc,
        @PathVariable int sn, @RequestParam Long id, ModelMap models )
    {
        models.put( "section", getSection( qtr, cc, sn ) );
        models.put( "block", blockDao.getBlock( id ) );
        models.put( "blockTypes", Block.Type.values() );
        return "site/block/edit";
    }

    @RequestMapping(value = "/site/{qtr}/{cc}-{sn}/block/edit",
        method = RequestMethod.POST)
    public String edit( @PathVariable String qtr, @PathVariable String cc,
        @PathVariable int sn, @ModelAttribute Block block,
        BindingResult result, SessionStatus sessionStatus )
    {
        User user = SecurityUtils.getUser();
        block = blockDao.saveBlock( block );
        sessionStatus.setComplete();

        logger.info( user.getUsername() + " edited block " + block.getId() );

        return "redirect:list";
    }

    @RequestMapping(value = "/site/{qtr}/{cc}-{sn}/block/addItem",
        method = RequestMethod.GET)
    public String addItem( @PathVariable String qtr, @PathVariable String cc,
        @PathVariable int sn, @RequestParam Long blockId, ModelMap models )
    {
        models.put( "section", getSection( qtr, cc, sn ) );
        models.put( "block", blockDao.getBlock( blockId ) );
        models.put( "item", new Item() );
        models.put( "resourceTypes", ResourceType.values() );
        return "site/block/addItem";
    }

    @RequestMapping(value = "/site/{qtr}/{cc}-{sn}/block/addItem",
        method = RequestMethod.POST)
    public String addItem( @PathVariable String qtr, @PathVariable String cc,
        @PathVariable int sn, @ModelAttribute Item item,
        @RequestParam Long blockId, @RequestParam(value = "uploadedFile",
            required = false) MultipartFile uploadedFile, BindingResult result,
        SessionStatus sessionStatus )
    {
        User user = SecurityUtils.getUser();
        Block block = blockDao.getBlock( blockId );
        Resource resource = item.getResource();
        if( resource.getType() != ResourceType.NONE )
        {
            if( resource.getType() == ResourceType.FILE )
                resource.setFile( fileIO.save( uploadedFile, user, true ) );
            block.getItems().add( item );
            block = blockDao.saveBlock( block );
            sessionStatus.setComplete();

            logger.info( user.getUsername() + " added an item to block "
                + block.getId() );
        }
        return "redirect:list";
    }

    @RequestMapping(value = "/site/{qtr}/{cc}-{sn}/block/editItem",
        method = RequestMethod.GET)
    public String editItem( @PathVariable String qtr, @PathVariable String cc,
        @PathVariable int sn, @RequestParam Long blockId,
        @RequestParam Long itemId, ModelMap models )
    {
        Block block = blockDao.getBlock( blockId );
        models.put( "section", getSection( qtr, cc, sn ) );
        models.put( "block", block );
        models.put( "item", block.getItem( itemId ) );
        models.put( "resourceTypes", ResourceType.values() );
        return "site/block/editItem";
    }

    @RequestMapping(value = "/site/{qtr}/{cc}-{sn}/block/editItem",
        method = RequestMethod.POST)
    public String editItem( @PathVariable String qtr, @PathVariable String cc,
        @PathVariable int sn, @ModelAttribute Block block,
        @ModelAttribute Item item, @RequestParam(value = "uploadedFile",
            required = false) MultipartFile uploadedFile, BindingResult result,
        SessionStatus sessionStatus )
    {
        User user = SecurityUtils.getUser();
        Resource resource = item.getResource();
        if( resource.getType() == ResourceType.FILE )
            resource.setFile( fileIO.save( uploadedFile, user, true ) );
        block = blockDao.saveBlock( block );
        sessionStatus.setComplete();

        logger.info( user.getUsername() + " edited item " + item.getId()
            + " in block " + block.getId() );

        return "redirect:list";
    }

}