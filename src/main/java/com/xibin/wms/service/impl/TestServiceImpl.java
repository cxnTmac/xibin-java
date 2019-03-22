package com.xibin.wms.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.xibin.core.daosupport.BaseManagerImpl;
import com.xibin.core.daosupport.BaseMapper;
import com.xibin.core.exception.BusinessException;
import com.xibin.wms.service.TestService;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@Service
public class TestServiceImpl extends BaseManagerImpl implements TestService {

	@Override
	public void Test() throws BusinessException {
		// TODO Auto-generated method stub
		aaa();

	}

	public void aaa() throws BusinessException {
		if (true) {
			throw new BusinessException("测试抛错！");
		}
	}

	@Override
	protected BaseMapper getMapper() {
		// TODO Auto-generated method stub
		return null;
	}

}
