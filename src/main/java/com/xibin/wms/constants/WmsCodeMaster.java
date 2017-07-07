package com.xibin.wms.constants;

public enum WmsCodeMaster {
	/****************************************
	 * AUDIT审核状态 WM_AUDIT_STATUS	    *
	 ****************************************
	 */
	/**
	 * AUDIT未审核
	 */
	AUDIT_NEW("00"),
	/**
	 * AUDIT已审核
	 */
	AUDIT_CLOSE("10"),
	/**
	 * AUDIT不审核
	 */
	AUDIT_NOT("99"),
	
	/****************************************
	 * 	入库单状态	WM_INBOUND_STATUS	    *
	 ****************************************
	 */
	/**
	 * INB创建
	 */
	INB_NEW("00"),
	/**
	 * INB部分收货
	 */
	INB_PART_RECEIVING("10"),
	/**
	 * INB完全收货
	 */
	INB_FULL_RECEIVING("20"),
	/**
	 * INB取消
	 */
	INB_CANCEL("90"),
	/**
	 * INB关闭
	 */
	INB_CLOSE("99"),
	/****************************************
	 *  SO出库单状态	SYS_WM_SO_STATUS        *
	 ****************************************
	 */
	/**
	 * SO创建
	 */
	SO_NEW("00"),
	/**
	 * SO部分分配
	 */
	SO_PART_ALLOC("30"),
	/**
	 * SO完全分配
	 */
	SO_FULL_ALLOC("40"),
	/**
	 * SO部分拣货
	 */
	SO_PART_PICKING("50"),
	/**
	 * SO完全拣货
	 */
	SO_FULL_PICKING("60"),
	/**
	 * SO部分发货
	 */
	SO_PART_SHIPPING("70"),
	/**
	 * SO完全发货
	 */
	SO_FULL_SHIPPING("80"),
	/**
	 * SO取消
	 */
	SO_CANCEL("90"),
	/**
	 * SO关闭
	 */
	SO_CLOSE("99"),
	/****************************************
	 * 	库存操作类型	WM_ACT_TRAN_TYPE	    *
	 ****************************************
	 */
	/**
	 * 收货
	 */
	ACT_REC("REC"),
	/**
	 * 收货
	 */
	ACT_CANCEL_REC("CANCEL_REC"),
	/**
	 * 发货
	 */
	ACT_SHIP("SHIP"),
	/**
	 * 取消发货
	 */
	ACT_CANCEL_SHIP("CANCEL_SHIP"),
	/**
	 * 拣货
	 */
	ACT_PICK("PICK"),
	/**
	 * 取消拣货
	 */
	ACT_CANCEL_PICK("CANCEL_PICK"),
	/**
	 * 分配
	 */
	ACT_ALLOC("ALLOC"),
	/**
	 * 取消分配
	 */
	ACT_CANCEL_ALLOC("CANCEL_ALLOC"),
	/****************************************
	 * 	库存操作类型	WM_ORDER_TYPE	    *
	 ****************************************
	 */
	/**
	 * 收货
	 */
	ORDER_INB("INB"),
	/**
	 * 发货
	 */
	ORDER_OUB("OUB");
	private String code;

	public String getCode() {
		return this.code;
	}
	
	private WmsCodeMaster(String code) {
		this.code = code;
	}

}
