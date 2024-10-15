package com.hss01248.motion_photos;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.XmpImagingParameters;
import org.apache.commons.imaging.common.bytesource.ByteSourceInputStream;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageParser;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoBytes;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExifUtils {


    public static String readXmp(String imagePath){
        try {
            String imageParser  = new JpegImageParser()
                        .getXmpXml(
                                new ByteSourceInputStream(new FileInputStream(imagePath), "img.jpg"),
                                new XmpImagingParameters());
            System.out.println("xmp:\n"+imageParser);
            return imageParser;

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return "";
        }
    }

    public static void writeXmp(String path,String xmpXml) throws Exception{
        JpegXmpRewriter rewriter = new JpegXmpRewriter();
        File file = new File(path);
        File tmpFile = new File(file.getParentFile(),"xmp-"+file.getName());
        if(tmpFile.exists()){
            tmpFile.delete();
        }
        readXmp(path);
        tmpFile.createNewFile();
        FileInputStream inputStream = new FileInputStream(file);
        FileOutputStream outputStream = new FileOutputStream(tmpFile);

        rewriter.updateXmpXml(inputStream,outputStream, xmpXml);
        inputStream.close();
        outputStream.close();

        file.delete();
        boolean b = tmpFile.renameTo(file);
        System.out.println("写入xmp成功: "+b);
        readXmp(path);
    }

    /**
     * 鸟用没有。
     *
     * @param inputFile  输入图像文件路径
     * @param outputFile 输出图像文件路径
     * @param tag 标签  TiffTagConstants.TIFF_TAG_ARTIST
     * @throws IOException 如果处理过程发生错误
     */
    @Deprecated
    public static void updateExifTag(String inputFile, String outputFile, TagInfoBytes tag, String value)  {
        File input = new File(inputFile);
        File output = new File(outputFile);

        try {
            // 从输入文件中读取EXIF信息
            JpegImageMetadata exifMetadata = (JpegImageMetadata) Imaging.getMetadata(input);


            TiffOutputSet outputSet = null;

            if (exifMetadata != null) {
                outputSet = exifMetadata.getExif().getOutputSet();
            }

            // 如果原始图像没有EXIF数据，创建一个新的
            if (outputSet == null) {
                outputSet = new TiffOutputSet();
            }

            // 获取或创建Exif目录
            TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

            // 移除现有的艺术家标签并添加新的
            //TiffTagConstants.TIFF_TAG_ARTIST
           // TiffTagConstants.TIFF_TAG_XMP

            System.out.println("EXIF数据--> "+exifMetadata.getExif().getFieldValue(tag));
            ;
            exifDirectory.removeField(tag);
            if(value !=null && !value.isEmpty()){
                exifDirectory.add(tag, value.getBytes());
            }


            // 使用ExifRewriter将更新的EXIF信息写入输出文件
            try (FileOutputStream fos = new FileOutputStream(output)) {
                new ExifRewriter().updateExifMetadataLossless(input, fos, outputSet);
                //System.out.println("EXIF数据已成功更新！--> "+outputFile);
                Object fieldValue = ((JpegImageMetadata) Imaging.getMetadata(output)).getExif().getFieldValue(tag);
                System.out.println("EXIF数据已成功更新！--> "+outputFile+" ,"+fieldValue);
            }


        } catch (Exception e) {
            System.out.println("EXIF数据更新失败！--> "+outputFile);
            e.printStackTrace();
        }
    }


}

