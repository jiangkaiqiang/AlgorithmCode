package com.smartcold.manage.cold.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.smartcold.manage.cold.dao.UserMapper;
import com.smartcold.manage.cold.entity.Company;
import com.smartcold.manage.cold.entity.CompanyRdc;
import com.smartcold.manage.cold.entity.CompanyUser;
import com.smartcold.manage.cold.entity.Role;
import com.smartcold.manage.cold.entity.RoleUser;
import com.smartcold.manage.cold.entity.UserEntity;
import com.smartcold.manage.cold.service.CompanyRdcService;
import com.smartcold.manage.cold.service.CompanyService;
import com.smartcold.manage.cold.service.CompanyUserService;
import com.smartcold.manage.cold.service.RoleService;
import com.smartcold.manage.cold.service.RoleUserService;
import com.smartcold.manage.cold.service.UserService;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {

	@Autowired
	private UserMapper userDao;
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CompanyUserService companyUserService;
	@Autowired
	private CompanyRdcService companyRdcService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private RoleUserService roleUserService;
	
	/**
	 * @Title: login UserController
	 * @Description: 用户登录
	 * @param @param userName
	 * @param @param password
	 * @param @param request
	 * @param @param response
	 * @param @return ModelAndView
	 * @param @throws Exception
	 * @return true/false
	 * @throws
	 */
	/*
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public Object login(HttpServletRequest request, String userName, String password) {
		UserEntity user = userDao.findByPassword(userName, password);
		if (user != null) {
			user.setPassword("******");
			request.getSession().setAttribute("user", user);
			request.getSession().setAttribute("cookie", user);
			return true;
		}
		return false;
	}*/
	@SuppressWarnings({ "finally", "rawtypes", "unchecked" })
	@RequestMapping(value = "/login",method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView login(HttpServletRequest request,HttpServletResponse response, 
			String userName, String password)throws Exception {
		ModelAndView mav = new ModelAndView();
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		Map map = new HashMap();
		try{
			UserEntity user = userService.getUserByNAndP(userName, password);
			if (user!=null) {
		    RoleUser roleUser = roleUserService.getRoleIdByUserId(user.getId());
		    Role role = roleService.getRoleByRoleId(roleUser.getRoleid());
		    String roleName = role.getName();
		    CompanyUser compUser = companyUserService.getComUserByUserId(user.getId());
		    Company company = companyService.getCompByCompId(compUser.getCompanyid());
		    String companyName = company.getName();
		    ArrayList<Integer> rdcIdList = new ArrayList<Integer>();
			if(compUser.getCompanyid()!=null)
			{
            	List<CompanyRdc> compRdcList = companyRdcService.getCompRdcsByCompId(compUser.getCompanyid());
            	for(int i=0;i<compRdcList.size(); i++){
            	         rdcIdList.add(compRdcList.get(i).getRdcid());
            	}
			}
		    //取得角色对应的权限
		    /*List<Privilege> privList = new ArrayList<Privilege>();
		    List<PrivilegeRole> privRoleList = privilegeRoleService
					                          .getPrivRoleByRoleId(roleUser.getRoleid());
		    for (int i = 0; i < privRoleList.size(); i++) {
			     Privilege priv = privilegeService.getPrivByPrivId(privRoleList.get(i).getId());
				 privList.add(priv);
							}*/
			user.setPassword("******");
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			session.setAttribute("cookie", user);			
			map.put("companyName", companyName);
			map.put("roleName", roleName);
			map.put("rdcIdList", rdcIdList);
			map.put("result", Boolean.TRUE);
		}else {
			map.put("result", Boolean.FALSE);
		}
		}catch(Exception e){
			map.put("result", Boolean.FALSE);
			e.printStackTrace();
		}finally{
			view.setAttributesMap(map);
			mav.setView(view);
			return mav;
		}
  }

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public Object logout(HttpServletRequest request) {
		request.getSession().setAttribute("user", null);
		return true;
	}

	@RequestMapping(value = "/findUser", method = RequestMethod.GET)
	@ResponseBody
	public Object findUser(HttpServletRequest request) {
		UserEntity user = (UserEntity) request.getSession().getAttribute("user");
		if (user == null) {
			user = new UserEntity();
		}
		return user;
	}
}