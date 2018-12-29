package com.seven.contract.manage.utils;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF转化成图片工具类
 */
public class Pdf2ImgUtil {

    public static List<String> pdf2Img(String pdfPath, String imgPath, String imgName) {

        Document document=new Document();

        List<String> imgAddresses = new ArrayList<>();

        try{
            document.setFile(pdfPath);
            float scale=1.0f; //缩放比例
            float rotation=0f; //旋转角度
            Double convertFraction = 0.0;
            for(int i=0;i<document.getNumberOfPages();i++){
                BufferedImage image=(BufferedImage)document.getPageImage(i,
                        GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX,
                        rotation,scale);
                convertFraction = ((double)(i+1))/document.getNumberOfPages();
                System.out.println("已转化:"+new java.text.DecimalFormat("#.00").format(convertFraction*100)+"%");
                RenderedImage rendImage=image;
                String imgAddress = imgPath + "/" + imgName + "_" + (i+1) + ".jpg";
                File file=new File(imgAddress);
                // 这里png作用是：格式是jpg但有png清晰度
                ImageIO.write(rendImage,"png",file);
                image.flush();

                imgAddresses.add(imgAddress);
            }
            document.dispose();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("======================完成============================");

        return imgAddresses;

    }

    public static void main(String[] args) {
        String pdfPath = "/Users/apple/Downloads/最新合同文本.pdf";
        String imgPath = "/Users/apple/Downloads";
        String imgName = "最新合同文本";
        List<String> addresses = Pdf2ImgUtil.pdf2Img(pdfPath, imgPath, imgName);
        for(String address : addresses) {
            System.out.println(address);
        }
    }

}
