package com.xibin.wms.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/report", produces = { "application/json;charset=UTF-8" })
public class ReportController {
	@Autowired
	@Qualifier("sqlSessionFactory")
	private SqlSessionFactory factory;

	private static String URL = "url";

	private static String reportPrePath = "/WEB-INF/jasper/";

	private static String reportSuffix = ".jasper";

	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public String report(HttpServletRequest request, Model model) {
		// 遍历request以获取到餐厨
		Enumeration<String> parameterNames = request.getParameterNames();
		if (null != parameterNames) {
			while (parameterNames.hasMoreElements()) {
				String parameterName = parameterNames.nextElement();
				String parameterValue = request.getParameter(parameterName);
				if (ReportController.URL.equals(parameterName)) {
					String fullUrl = reportPrePath + parameterValue + reportSuffix;
					model.addAttribute("url", fullUrl);
				} else {
					model.addAttribute(parameterName, parameterValue);
				}
			}
		}
		// model.addAttribute("url", "/WEB-INF/jasper/TestReport.jasper");
		model.addAttribute("format", "pdf"); // 报表格式
		return "iReportView"; // 对应jasper-defs.xml中的bean id
	}
}
