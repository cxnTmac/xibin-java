package com.xibin.core.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.xibin.core.costants.Constants;
import com.xibin.core.page.pojo.Page;
import com.xibin.core.page.pojo.PageEntity;
import com.xibin.core.security.pojo.UserDetails;

@Controller
@RequestMapping(value = "/popWin", produces = { "application/json;charset=UTF-8" })
public class PopWinController {
	@Autowired
	@Qualifier("sqlSessionFactory")
	SqlSessionFactory factory;
	@Autowired
	HttpSession session;

	/**
	 * 带分页的放大镜弹出框公用查询方法
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/query")
	@ResponseBody
	public PageEntity<?> query(HttpServletRequest request, Model model) {
		PageEntity pageEntity = new PageEntity();
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		Page<?> page = new Page();
		// 配置分页参数
		page.setPageNo(Integer.parseInt(request.getParameter("page")));
		page.setPageSize(Integer.parseInt(request.getParameter("size")));
		Map map = JSON.parseObject(request.getParameter("queryConditions"));
		map.put("page", page);
		map.put("companyId", userDetails.getCompanyId());
		if (request.getParameter("sys").equals("fin")) {
			map.put("bookId", userDetails.getBookId());
		}
		SqlSession sqlsession = factory.openSession();
		List<?> list = sqlsession
				.selectList(getClassPath(request.getParameter("sys")) + request.getParameter("queryType"), map);
		sqlsession.close();
		pageEntity.setList(list);
		pageEntity.setSize(page.getTotalRecord());
		return pageEntity;
	}

	private String getClassPath(String sys) {
		return "com.xibin." + sys + ".dao.";
	}
}
