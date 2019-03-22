package com.xibin.core.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.xibin.core.exception.BusinessException;
import com.xibin.core.pojo.Message;
import com.xibin.wms.entity.OutboundDetailQuickImportEntity;

public class ReadExcelTools {
	// private static final Log logger =
	// LogFactory.getLog(ActualAppController.class);
	// private static final Gson gson = new
	// GsonBuilder().serializeNulls().create();
	private final static String xls = "xls";
	private final static String xlsx = "xlsx";

	public static Message readExcelForGetCustomerSkuCode(MultipartFile file, String customerSkuCodeColumnName,
			String skuCodeColumnName) throws IOException, BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		// 检查文件
		checkFile(file);
		// 获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);
		List<String> list = new ArrayList<String>();
		if (workbook != null) {
			// 默认读取第一个工作簿
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				throw new BusinessException("第一个工作簿不存在！");
			}
			// 获得当前sheet的开始行,默认开始行为表头
			int firstRowNum = sheet.getFirstRowNum();
			// 获得当前sheet的结束行
			int lastRowNum = sheet.getLastRowNum();
			// 获取列头行
			Row headerRow = sheet.getRow(firstRowNum);
			if (headerRow == null) {
				throw new BusinessException("列头行为空，检查表格的格式");
			}
			// 获得当前行的开始列
			int firstCellNum = headerRow.getFirstCellNum();
			// 获得当前行的列数
			int lastCellNum = headerRow.getLastCellNum();// 为空列获取
			int customerSkuCodeColumnNameIndex = -1;
			int skuCodeColumnNameIndex = -1;
			String header = "";
			for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
				Cell cell = headerRow.getCell(cellNum);
				header = getCellValue(cell);
				// 去空格比较，防止复制粘贴造成的问题
				if (header.trim().equals(skuCodeColumnName.trim())) {
					skuCodeColumnNameIndex = cellNum;
				}
				if (header.trim().equals(customerSkuCodeColumnName.trim())) {
					customerSkuCodeColumnNameIndex = cellNum;
				}
			}
			if (customerSkuCodeColumnNameIndex == -1) {
				throw new BusinessException("客户产品编码的列头：[" + customerSkuCodeColumnName + "]不存在");
			}
			if (skuCodeColumnNameIndex == -1) {
				throw new BusinessException("产品编码的列头：[" + skuCodeColumnName + "]不存在");
			} // 循环除了第一行的所有行
			for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
				// 为了过滤到第一行因为我的第一行是数据库的列
				// 获得当前行
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					errors.add("第" + rowNum + "行数据缺失，无法导入");
					list.add("NULL");
					continue;
				}
				// 获取产品编码
				String skuCodeStr = getCellValue(row.getCell(skuCodeColumnNameIndex));
				// 获取客户产品编码
				String customerSkuCodeStr = getCellValue(row.getCell(customerSkuCodeColumnNameIndex));
				if ("".equals(skuCodeStr) || null == skuCodeStr) {
					errors.add("第" + rowNum + "行数据产品编码缺失，无法导入");
					list.add("NULL");
					continue;
				}
				if ("".equals(customerSkuCodeStr) || null == customerSkuCodeStr) {
					errors.add("第" + rowNum + "行数据客户产品编码缺失，无法导入");
					list.add("NULL");
					continue;
				}
				list.add(skuCodeStr + "|" + customerSkuCodeStr);
			}
			message.setData(list);
			message.setMsgs(errors);
			message.converMsgsToMsg("\n");
			return message;
		} else {
			throw new BusinessException("表格为空，检查表格文件");
		}
	}

	public static Message readExcelForGetFittingSkuCode(MultipartFile file, String skuCodeColumnName)
			throws IOException, BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		// 检查文件
		checkFile(file);
		// 获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);
		List<String> list = new ArrayList<String>();
		if (workbook != null) {
			// 默认读取第一个工作簿
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				throw new BusinessException("第一个工作簿不存在！");
			}
			// 获得当前sheet的开始行,默认开始行为表头
			int firstRowNum = sheet.getFirstRowNum();
			// 获得当前sheet的结束行
			int lastRowNum = sheet.getLastRowNum();
			// 获取列头行
			Row headerRow = sheet.getRow(firstRowNum);
			if (headerRow == null) {
				throw new BusinessException("列头行为空，检查表格的格式");
			}
			// 获得当前行的开始列
			int firstCellNum = headerRow.getFirstCellNum();
			// 获得当前行的列数
			int lastCellNum = headerRow.getLastCellNum();// 为空列获取
			int skuCodeColumnNameIndex = -1;
			String header = "";
			for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
				Cell cell = headerRow.getCell(cellNum);
				header = getCellValue(cell);
				// 去空格比较，防止复制粘贴造成的问题
				if (header.trim().equals(skuCodeColumnName.trim())) {
					skuCodeColumnNameIndex = cellNum;
					break;
				}
			}
			if (skuCodeColumnNameIndex == -1) {
				throw new BusinessException("产品编码的列头：[" + skuCodeColumnName + "]不存在");
			} // 循环除了第一行的所有行
			for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
				// 为了过滤到第一行因为我的第一行是数据库的列
				// 获得当前行
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					errors.add("第" + rowNum + "行数据缺失，无法导入");
					list.add("NULL");
					continue;
				}
				// 获取产品编码
				String skuCodeStr = getCellValue(row.getCell(skuCodeColumnNameIndex));

				if ("".equals(skuCodeStr) || null == skuCodeStr) {
					errors.add("第" + rowNum + "行数据商品编码缺失，无法导入");
					list.add("NULL");
					continue;
				}
				list.add(skuCodeStr);
			}
			message.setData(list);
			message.setMsgs(errors);
			message.converMsgsToMsg("</br>");
			return message;
		} else {
			throw new BusinessException("表格为空，检查表格文件");
		}
	}

	public static Message readExcelForOutboundDetailImport(MultipartFile file, String skuCodeColumnName,
			String priceColumnName, String numColumnName) throws IOException, BusinessException {
		Message message = new Message();
		List<String> errors = new ArrayList<String>();
		// 检查文件
		checkFile(file);
		// 获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);
		// 创建返回对象，所有行作为一个集合返回
		List<OutboundDetailQuickImportEntity> list = new ArrayList<OutboundDetailQuickImportEntity>();
		if (workbook != null) {
			// 默认读取第一个工作簿
			Sheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				throw new BusinessException("第一个工作簿不存在！");
			}
			// 获得当前sheet的开始行,默认开始行为表头
			int firstRowNum = sheet.getFirstRowNum();
			// 获得当前sheet的结束行
			int lastRowNum = sheet.getLastRowNum();
			// 获取列头行
			Row headerRow = sheet.getRow(firstRowNum);
			if (headerRow == null) {
				throw new BusinessException("列头行为空，检查表格的格式");
			}
			// 获得当前行的开始列
			int firstCellNum = headerRow.getFirstCellNum();
			// 获得当前行的列数
			int lastCellNum = headerRow.getLastCellNum();// 为空列获取
			int skuCodeColumnNameIndex = -1;
			int priceColumnNameIndex = -1;
			int numColumnNameIndex = -1;
			String header = "";
			for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
				Cell cell = headerRow.getCell(cellNum);
				header = getCellValue(cell);
				if (header.trim().equals(skuCodeColumnName.trim())) {
					skuCodeColumnNameIndex = cellNum;
				} else if (header.trim().equals(priceColumnName.trim())) {
					priceColumnNameIndex = cellNum;
				} else if (header.trim().equals(numColumnName.trim())) {
					numColumnNameIndex = cellNum;
				}
			}
			if (skuCodeColumnNameIndex == -1) {
				throw new BusinessException("产品编码的列头：[" + skuCodeColumnName + "]不存在");
			}
			if (priceColumnNameIndex == -1) {
				throw new BusinessException("价格的列头：[" + priceColumnName + "]不存在");
			}
			if (numColumnNameIndex == -1) {
				throw new BusinessException("订货数的列头：[" + numColumnName + "]不存在");
			}
			// 默认列头的下一行开始就是数据，只读取需要的列
			// 循环除了第一行的所有行
			for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
				// 为了过滤到第一行因为我的第一行是数据库的列
				// 获得当前行
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					errors.add("第" + rowNum + "行数据缺失，无法导入");
					continue;
				}

				// 获取产品编码
				String skuCodeStr = getCellValue(row.getCell(skuCodeColumnNameIndex));
				// 获取产品编码
				String priceStr = getCellValue(row.getCell(priceColumnNameIndex));
				// 获取产品编码
				String numStr = getCellValue(row.getCell(numColumnNameIndex));
				if ("".equals(skuCodeStr) || null == skuCodeStr) {
					errors.add("第" + rowNum + "行数据商品编码缺失，无法导入");
					continue;
				} else if ("".equals(priceStr) || null == priceStr) {
					errors.add("第" + rowNum + "行数据价格缺失，无法导入");
					continue;
				} else if ("".equals(numStr) || null == numStr) {
					errors.add("第" + rowNum + "行数据数量缺失，无法导入");
					continue;
				}
				double price = Double.parseDouble(priceStr);
				double num = Double.parseDouble(numStr);
				OutboundDetailQuickImportEntity entity = new OutboundDetailQuickImportEntity();
				entity.setFittingSkuCode(skuCodeStr);
				entity.setOutboundPrice(price);
				entity.setOutboundNum(num);
				list.add(entity);
			}
			message.setData(list);
			message.setMsgs(errors);
			message.converMsgsToMsg("</br>");
			return message;
		} else {
			throw new BusinessException("表格为空，检查表格文件");
		}
	}

	public static List<String[]> readExcel(MultipartFile file) throws IOException {
		// 检查文件
		checkFile(file);
		// 获得Workbook工作薄对象
		Workbook workbook = getWorkBook(file);
		// 创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
		List<String[]> list = new ArrayList<String[]>();
		if (workbook != null) {
			for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
				// 获得当前sheet工作表
				Sheet sheet = workbook.getSheetAt(sheetNum);
				if (sheet == null) {
					continue;
				}
				// 获得当前sheet的开始行
				int firstRowNum = sheet.getFirstRowNum();
				// 获得当前sheet的结束行
				int lastRowNum = sheet.getLastRowNum();
				// 循环除了第一行的所有行
				for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) { // 为了过滤到第一行因为我的第一行是数据库的列
					// 获得当前行
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					// 获得当前行的开始列
					int firstCellNum = row.getFirstCellNum();
					// 获得当前行的列数
					int lastCellNum = row.getLastCellNum();// 为空列获取
					// int lastCellNum = row.getPhysicalNumberOfCells();//为空列不获取
					// String[] cells = new
					// String[row.getPhysicalNumberOfCells()];
					String[] cells = new String[row.getLastCellNum()];
					// 循环当前行
					for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
						Cell cell = row.getCell(cellNum);
						cells[cellNum] = getCellValue(cell);
					}
					list.add(cells);
				}
			}
		}
		return list;
	}

	public static void checkFile(MultipartFile file) throws IOException {
		// 判断文件是否存在
		if (null == file) {
			throw new FileNotFoundException("文件不存在！");
		}
		// 获得文件名
		String fileName = file.getOriginalFilename();
		// 判断文件是否是excel文件
		if (!fileName.endsWith(xls) && !fileName.endsWith(xlsx)) {
			throw new IOException(fileName + "不是excel文件");
		}
	}

	public static Workbook getWorkBook(MultipartFile file) {
		// 获得文件名
		String fileName = file.getOriginalFilename();
		// 创建Workbook工作薄对象，表示整个excel
		Workbook workbook = null;
		try {
			// 获取excel文件的io流
			InputStream is = file.getInputStream();
			// 根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
			if (fileName.endsWith(xls)) {
				// 2003
				workbook = new HSSFWorkbook(is);
			} else if (fileName.endsWith(xlsx)) {
				// 2007
				workbook = new XSSFWorkbook(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return workbook;
	}

	public static String getCellValue(Cell cell) {
		String cellValue = "";
		if (cell == null) {
			return cellValue;
		}
		// 把数字当成String来读，避免出现1读成1.0的情况
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
		}
		// 判断数据的类型
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: // 数字
			cellValue = String.valueOf(cell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_STRING: // 字符串
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BOOLEAN: // Boolean
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA: // 公式
			// cellValue = String.valueOf(cell.getCellFormula());
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BLANK: // 空值
			cellValue = "";
			break;
		case Cell.CELL_TYPE_ERROR: // 故障
			cellValue = "非法字符";
			break;
		default:
			cellValue = "未知类型";
			break;
		}
		return cellValue;
	}

}
