package com.xibin.core.utils;
import java.io.*;
import java.awt.*;  
import java.awt.image.*;  
import javax.imageio.ImageIO;
public class ImageUtils {
     
    /** 
     * 按照宽度还是高度进行压缩 
     * @param w int 最大宽度 
     * @param h int 最大高度 
     */  
	static private BufferedImage resizeFix(int dw, int dh,int ow,int oh,Image img) throws IOException {  
        if (ow / oh > dw / dh) {  
        	return resizeByWidth(dw,ow,oh,img);  
        } else {  
        	return resizeByHeight(dh,ow,oh,img);  
        }  
    }  
    /** 
     * 以宽度为基准，等比例放缩图片 
     * @param w int 新宽度 
     */  
    static private BufferedImage resizeByWidth(int dw,int ow,int oh,Image img) throws IOException {  
        int h = (int) (oh * dw / ow);  
        return resize(dw, h,img);
    }  
    /** 
     * 以高度为基准，等比例缩放图片 
     * @param h int 新高度 
     */  
    static private BufferedImage resizeByHeight(int dh,int ow,int oh,Image img) throws IOException {  
        int w = (int) (ow * dh / oh);  
        return resize(w, dh,img);  
    }  
    /** 
     * 强制压缩/放大图片到固定的大小 
     * @param w int 新宽度 
     * @param h int 新高度 
     */  
    static private  BufferedImage resize(int w, int h,Image img) throws IOException {  
        // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢  
        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB );
        image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图 
        return image;

    }
    
    public static void resizeAndSave(int dw,int dh,String originFileName,String destFileName) throws IOException{
    	File file = new File(originFileName);// 读入文件  
    	Image img = ImageIO.read(file);      // 构造Image对象  
        int width = img.getWidth(null);    // 得到源图宽  
        int height = img.getHeight(null);  // 得到源图长  
        BufferedImage image = resizeFix(dw,dh,width,height,img);
        File destFile = new File(destFileName);
        FileOutputStream out = new FileOutputStream(destFile);
        ImageIO.write(image, "jpg", out);  
//      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//      encoder.encode(image);
        out.close();
    }
    public static File resizeAndSave(int dw,int dh,File originFile,String destFileName) throws IOException{  
    	Image img = ImageIO.read(originFile);      // 构造Image对象  
        int width = img.getWidth(null);    // 得到源图宽  
        int height = img.getHeight(null);  // 得到源图长  
        BufferedImage image = resizeFix(dw,dh,width,height,img);
        File destFile = new File(destFileName);
        FileOutputStream out = new FileOutputStream(destFile);
        ImageIO.write(image, "jpg", out);
//      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//      encoder.encode(image);
        out.close();
        return destFile;
    }
    
}
