package com.xibin.wms.constants;

public enum WmsCodeMaster {
	/****************************************
	 * AUDIT审核状态 WM_AUDIT_STATUS *
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
	 * 入库单状态 WM_INBOUND_STATUS *
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
	 * 入库单类型 WM_INBOUND_TYPE *
	 ****************************************
	 */
	/**
	 * 赊购入库
	 */
	INB_CI("CI"),
	/**
	 * 现购入库
	 */
	INB_XG("XG"),
	/**
	 * 退货入库
	 */
	INB_RI("RI"),
	/**
	 * 盘盈入库
	 */
	INB_PI("PI"),
	/****************************************
	 * 出库单类型 WM_OUTBOUND_TYPE *
	 ****************************************
	 */
	/**
	 * 赊销出库
	 */
	OUB_PO("PO"),
	/**
	 * 现销出库
	 */
	OUB_XX("XX"),
	/**
	 * 退货出库
	 */
	OUB_RO("RO"),
	/**
	 * 盘亏出库
	 */
	OUB_CO("CO"),
	/****************************************
	 * SO出库单状态 SYS_WM_SO_STATUS *
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
	 * SO超量拣货
	 */
	SO_OVER_PICKING("65"),
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
	 * 库存操作类型 WM_ACT_TRAN_TYPE *
	 ****************************************
	 */
	/**
	 * 创建
	 */
	ASS_NEW("00"),
	/**
	 * 已生成子件明细
	 */
	ASS_CREATE_S("10"),
	/**
	 * 部分分配
	 */
	ASS_PART_ALLOC("20"),
	/**
	 * 完全分配
	 */
	ASS_FULL_ALLOC("30"),
	/**
	 * 取消拣货
	 */
	ASS_PART_PICK("40"),
	/**
	 * 完全拣货
	 */
	ASS_FULL_PICK("50"),
	/**
	 * 部分组装
	 */
	ASS_PART("60"),
	/**
	 * 完全组装
	 */
	ASS_FULL("70"),
	/****************************************
	 * 库存操作类型 WM_ACT_TRAN_TYPE *
	 ****************************************
	 */
	/**
	 * 收货
	 */
	ACT_REC("REC"),
	/**
	 * 取消收货
	 */
	ACT_CANCEL_REC("CANCEL_REC"),
	/**
	 * 销售退货单收货
	 */
	ACT_RE_REC("RE_REC"),
	/**
	 * 销售退货单取消收货
	 */
	ACT_RE_CANCEL_REC("RE_CANCEL_REC"),
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
	 * 预组装分配拣货
	 */
	ACT_PRE_ASSEMBLE_PICK("PRE_ASSEMBLE_PICK"),

	/**
	 * 超量拣货
	 */
	ACT_OVER_PICK("OVER_PICK"),
	/**
	 * 预组装超量拣货
	 */
	ACT_OVER_PRE_ASSEMBLE_PICK("OVER_PRE_ASSEMBLE_PICK"),
	/**
	 * 取消拣货
	 */
	ACT_CANCEL_PICK("CANCEL_PICK"),
	/**
	 * 预组装取消超量拣货
	 */
	ACT_CANCEL_OVER_PRE_ASSEMBLE_PICK("CANCEL_OVER_PRE_ASSEMBLE_PICK"),
	/**
	 * 取消预组装拣货
	 */
	ACT_CANCEL_PRE_ASSEMBLE_PICK("CANCEL_PRE_ASSEMBLE_PICK"),
	/**
	 * 取消超量拣货
	 */
	ACT_CANCEL_OVER_PICK("CANCEL_OVER_PICK"),
	/**
	 * 分配
	 */
	ACT_ALLOC("ALLOC"),
	/**
	 * 预加工分配
	 */
	ACT_PRE_ASSEMBLE_ALLOC("PRE_ASSEMBLE_ALLOC"),
	/**
	 * 取消分配
	 */
	ACT_CANCEL_ALLOC("CANCEL_ALLOC"),
	/**
	 * 取消预加工分配
	 */
	ACT_CANCEL_PRE_ASSEMBLE_ALLOC("CANCEL_PRE_ASSEMBLE_ALLOC"),
	/**
	 * 组装消耗子件
	 */
	ACT_ASSEMBLE_S("ASSEMBLE_S"),
	/**
	 * 组装生成父件
	 */
	ACT_ASSEMBLE_F("ASSEMBLE_F"),
	/**
	 * 库存移动
	 */
	ACT_MOVE("MOVE"),
	/**
	 * 库存转变
	 */
	ACT_TRANSFER("TRANSFER"),
	/****************************************
	 * 功能权限类型 SYS_FUNCTION_TYPE *
	 ****************************************
	 */
	/**
	 * 菜单
	 */
	FUNCTION_M("M"),
	/**
	 * 按钮
	 */
	FUNCTION_B("B"),
	/****************************************
	 * 库存操作类型 WM_ORDER_TYPE *
	 ****************************************
	 */
	/**
	 * 组装
	 */
	ORDER_ASS("ASS"),
	/**
	 * 收货
	 */
	ORDER_INB("INB"),
	/**
	 * 发货
	 */
	ORDER_OUB("OUB"),
	/****************************************
	 * 分配类型 WM_ALLOC_TYPE *
	 ****************************************
	 */
	/**
	 * 自动分配
	 */
	ALLOC_AUTO("AUTO"),
	/**
	 * 预加工分配
	 */
	ALLOC_ASS("ASS"),
	/****************************************
	 * 固定库位 WM_LOC_CODE *
	 ****************************************
	 */
	/**
	 * 工作台
	 */
	LOC_WORKBENCH("WORKBENCH"),
	/**
	 * 发货区
	 */
	LOC_SORTATION("SORTATION");
	private String code;

	public String getCode() {
		return this.code;
	}

	private WmsCodeMaster(String code) {
		this.code = code;
	}

}
