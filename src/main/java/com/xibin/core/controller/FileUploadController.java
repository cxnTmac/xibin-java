package com.xibin.core.controller;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.xibin.core.costants.Constants;
import com.xibin.core.pojo.Message;
import com.xibin.core.security.pojo.UserDetails;
import com.xibin.core.utils.ImageUtils;


@Controller
@RequestMapping(value = "/file", produces = {"application/json;charset=UTF-8"})
public class FileUploadController {
	@Autowired
	SqlSessionFactory factory;
	@Autowired
	HttpSession session;
	
	public static final String WEBPICUPLOADURL = "C:\\Users\\cxn\\WebstormProjects\\xibin-web\\static";
	@RequestMapping("/uploadFittingSkuPics")  
	@ResponseBody
    public Message uploadFittingSkuPics(@RequestParam MultipartFile[] pics, HttpServletRequest request) throws IOException{
		Message message = new Message();
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		//UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload/FittingSkuPic/"+fittingSkuCode);
		for(MultipartFile pic:pics){
			FileUtils.copyInputStreamToFile(pic.getInputStream(), new File(realPath, pic.getOriginalFilename())); 
		}
		return message;
		
	}
	
	@RequestMapping(value="/uploadFittingSkuPic",consumes="multipart/form-data",method = RequestMethod.POST)  
	@ResponseBody
    public Message uploadFittingSkuPic(HttpServletRequest request,@RequestParam("file") MultipartFile pic){
		Message message = new Message();
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		UserDetails userDetails = (UserDetails) session.getAttribute(Constants.SESSION_USER_KEY);
		//String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload/FittingSkuPic/"+fittingSkuCode+"-"+userDetails.getCompanyId());
		String realPath = WEBPICUPLOADURL+"\\fittingSku\\"+fittingSkuCode+"-"+userDetails.getCompanyId().toString();
		try {
			File originFile = new File(realPath, pic.getOriginalFilename());
			String fileName = pic.getOriginalFilename().substring(0,pic.getOriginalFilename().lastIndexOf("."));
			String destFileName = realPath+"\\"+fileName+"-zip"+".jpg";
			FileUtils.copyInputStreamToFile(pic.getInputStream(), originFile);
			ImageUtils.resizeAndSave(250, 250, originFile, destFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			message.setCode(0);
			message.setMsg("文件["+pic.getOriginalFilename()+"]上传失败!");
			return message;
		} 
		message.setCode(200);
		return message;
	}
	
	@RequestMapping(value="/uploadFittingSkuPicx",consumes="multipart/form-data",method = RequestMethod.POST)  
	@ResponseBody
    public Message uploadFittingSkuPicx(HttpServletRequest request){
		Message message = new Message();
		MultipartHttpServletRequest muti = (MultipartHttpServletRequest) request;
		MultiValueMap<String, MultipartFile> map = muti.getMultiFileMap();
		String fittingSkuCode = request.getParameter("fittingSkuCode");
		String realPath = request.getSession().getServletContext().getRealPath("/WEB-INF/upload/FittingSkuPic/"+fittingSkuCode);
		for (Map.Entry<String, List<MultipartFile>> entry : map.entrySet()) {
			 
            List<MultipartFile> list = entry.getValue();
            for (MultipartFile multipartFile : list) {
            	try{
            		FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(realPath, multipartFile.getOriginalFilename()));
            	}
            	catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			message.setCode(0);
        			message.setMsg("文件["+multipartFile.getOriginalFilename()+"]上传失败!");
        			return message;
        		} 
            }
        }
		message.setCode(200);
		message.setMsg("上传成功!");
		return message;
		
	}
	
}
