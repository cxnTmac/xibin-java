/**
 * 
 */
package com.xibin.core.daosupport;

import java.util.Date;

public abstract interface Recordable {

	public abstract Integer getId();
	
	public abstract void setId(Integer id);

	public abstract Integer getCreator();

	public abstract void setCreator(Integer paramString);

	public abstract Date getCreateTime();

	public abstract void setCreateTime(Date paramDate);

	public abstract Integer getModifier();

	public abstract void setModifier(Integer paramString);

	public abstract Date getModifyTime();

	public abstract void setModifyTime(Date paramDate);

	public abstract Integer getRecVer();

	public abstract void setRecVer(Integer recordVersion);

}