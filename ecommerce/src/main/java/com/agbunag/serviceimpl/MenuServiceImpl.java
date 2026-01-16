package com.agbunag.serviceimpl;

import com.agbunag.entity.MenuData;
import com.agbunag.model.Menu;

import com.agbunag.repository.MenuDataRepository;
import com.agbunag.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    MenuDataRepository menuDataRepository;
    
    @Override
    public List<Menu> getMenus() {
        List<MenuData> menusData = new ArrayList<>();
        List<Menu> menus = new ArrayList<>();
        menuDataRepository.findAll().forEach(menusData::add);
        Iterator<MenuData> it = menusData.iterator();
        while(it.hasNext()) {
            MenuData menuData = it.next();
            Menu menu = new Menu();
            menu.setId(menuData.getId());
            menu.setName(menuData.getName());
            menu.setDescription(menuData.getDescription());
            menu.setRouterPath(menuData.getRouterPath());
            menu.setCategoryName(menuData.getCategoryName());
            menus.add(menu);
        }
        return menus;
    }

    @Override
    public Menu create(Menu menu) {
        log.info(" add:Input " + menu.toString());
        MenuData menuData = new MenuData();
        menuData.setName(menu.getName());
        menuData.setCategoryName(menu.getCategoryName());
        menuData.setDescription(menu.getDescription());
        menuData.setRouterPath(menu.getRouterPath());
        menuData.setIcon(menu.getIcon());
        menuData = menuDataRepository.save(menuData);
        log.info(" add:Input " + menuData.toString());
        Menu newMenu = new Menu();
        newMenu.setId(menuData.getId());
        newMenu.setName(menuData.getName());
        newMenu.setDescription(menuData.getDescription());
        newMenu.setCategoryName(menuData.getCategoryName());
        newMenu.setRouterPath(menuData.getRouterPath());
        newMenu.setIcon(menuData.getIcon());
        return newMenu;
    }
}
