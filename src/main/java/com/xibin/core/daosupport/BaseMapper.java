/**
 * 
 */
package com.xibin.core.daosupport;

import java.util.Collection;

/**
 * @Description:
 * @author:Vincent.Zhang
 * 
 * 						<pre>
 * 版本号					修改人					修改说明
 * 2015年10月1日 .1			Vincent.Zhang    	新增
 *                       </pre>
 */
public interface BaseMapper {

	/**
	 * 查询一个用户详情方法
	 * 
	 * @param id
	 * @return
	 */
	public Collection getById(String[] ids);

	/**
	 * 增加方法
	 * 
	 * @param model
	 * @return
	 */
	public int insert(Collection baseModels);

	/**
	 * 修改方法
	 * 
	 * @param model
	 * @return
	 */
	public int update(Collection baseModels);

	/**
	 * 删除方法
	 * 
	 * @param id
	 * @return
	 */
	public int delete(int[] ids);

}
