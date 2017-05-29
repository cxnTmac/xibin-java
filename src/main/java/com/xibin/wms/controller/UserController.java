package com.xibin.wms.controller;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xibin.core.costants.Constants;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.wms.pojo.SysUser;
import com.xibin.wms.service.UserService;
 
@Controller
@RequestMapping(value = "/user", produces = {"application/json;charset=UTF-8"})
public class UserController {
  @Resource
  private UserService userService;
  @Autowired  
  private HttpSession session;
  @RequestMapping("/showUser")
  @ResponseBody
  public SysUser toIndex(HttpServletRequest request,Model model){
    int userId = Integer.parseInt(request.getParameter("id"));
    return this.userService.getUserById(userId);
  }
  
  @RequestMapping("/showAllUser")
  @ResponseBody
  public PageEntity<SysUser> showAllUser(HttpServletRequest request,Model model){ 
    // 开始分页  
	PageEntity<SysUser> pageEntity = new PageEntity<SysUser>();
	UserDetails userDetails = (UserDetails)session.getAttribute(Constants.SESSION_USER_KEY);
	
	Page page = new Page();
	page.setPageSize(Integer.parseInt(request.getParameter("size")));
	page.setPageNo(Integer.parseInt(request.getParameter("page")));
	Map map = JSONObject.parseObject(request.getParameter("conditions"));
	map.put("page",page);
	map.put("companyId", userDetails.getCompanyId());
	List<SysUser> userList = userService.getAllUserByPage(map);
	pageEntity.setList(userList);
	//pageEntity.setSize(page.getTotalRecord());
    return  pageEntity;
  }
  
  @RequestMapping("/removeUser")
  @ResponseBody
  public int removeUser(HttpServletRequest request,Model model){
	int userId = Integer.parseInt(request.getParameter("id"));
    return this.userService.removeUser(userId);
  }

  
  @RequestMapping("/saveUser")
  @ResponseBody
  public int saveUser(HttpServletRequest request,Model model){
	String userStr = request.getParameter("user");
	SysUser user = JSON.parseObject(userStr, SysUser.class);
	return this.userService.saveUser(user);
  }
  
  @RequestMapping("/updateUser")
  @ResponseBody
  public int updateUser(HttpServletRequest request,Model model){
	String userStr = request.getParameter("user");
	SysUser user = JSON.parseObject(userStr, SysUser.class);
	return this.userService.updateByPrimaryKey(user);
  }
  
  @RequestMapping("/login")
  @ResponseBody
  public Message login(@RequestParam Map<String, String> params){
	Message message = new Message();
	SysUser user = JSON.parseObject(params.get("data"), SysUser.class);
	List<SysUser> list  = this.userService.selectByUserNameAndPassword(user.getUserName(),user.getPassword());
	if(list.size()>0){
		if(list.get(0).getIsEnable().equals("Y")){
			UserDetails userDetails = new UserDetails();
			userDetails.setUserName(list.get(0).getUserName());
			userDetails.setCompanyId(list.get(0).getCompanyId());
			userDetails.setUserId(list.get(0).getId());
			session.setAttribute(Constants.SESSION_USER_KEY, userDetails);
			message.setCode(200);
			message.setData(list.get(0));
			message.setMsg("登陆成功！");
		}else{
			message.setCode(0);
			message.setMsg("用户未启用！");
		}
	}else{
		message.setCode(0);
		message.setMsg("用户名或密码错误！");
	}
	return message;
  }
  @RequestMapping("/logout")
  @ResponseBody
  public Message logout(@RequestParam Map<String, String> params){
	  Message message = new Message();
	  session.invalidate();
	  message.setCode(200);
	  return message;
  }
}