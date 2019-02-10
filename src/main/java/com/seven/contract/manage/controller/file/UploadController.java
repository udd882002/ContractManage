package com.seven.contract.manage.controller.file;

import com.seven.contract.manage.common.ApiResult;
import com.seven.contract.manage.common.AppRuntimeException;
import com.seven.contract.manage.common.BaseController;
import com.seven.contract.manage.model.Member;
import com.seven.contract.manage.uploader.UploaderProperties;
import com.seven.contract.manage.uploader.path.FileSavePath;
import com.seven.contract.manage.utils.Base64ToImageUtils;
import com.seven.contract.manage.utils.FileUtils;
import com.seven.contract.manage.utils.Pdf2ImgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UploadController extends BaseController {
    @Autowired
    private UploaderProperties properties;

    @Autowired
    private FileSavePath fileSavePath;

    @RequestMapping("/upload/{fileType}")
    public ApiResult upload(HttpServletRequest request, MultipartFile[] file, @PathVariable Integer fileType) {

        printHttpHeader(request);

        String phone = "";

        if(fileType != 2) {
            Member member;
            //登陆检测
            try {
                member = this.checkLogin(request);
            } catch (AppRuntimeException e) {
                return ApiResult.fail(request, e.getReqCode(), e.getMsg());
            }

            phone = member.getPhone();
        }

        try {
            checkParams(file, fileType);
            List<String> urls = new ArrayList<>();
            String fullPath = fileSavePath.fullPath(fileType, phone);
            String relativePath = fileSavePath.relativePath(fileType, phone);
            logger.debug("fullPath = {}, relativePath = {}", fullPath, relativePath);
            checkPath(fullPath);
            for (MultipartFile mf : file) {
                String filename = saveFile(fullPath, mf);
                if (StringUtils.isEmpty(filename)) {
                    continue;
                }
                if (fileType == 3) {
                    //第一个增加上传的pdf文件地址
                    urls.add(relativePath + "/" + filename);
                    String imgName = filename.substring(0, filename.lastIndexOf("."));
                    List<String> addresses = Pdf2ImgUtil.pdf2Img(fullPath + "/" + filename, fullPath, imgName);
                    for (String imgadd :addresses) {
                        String[] img = imgadd.split("/");
                        urls.add(relativePath + "/" + img[img.length-1]);
                    }
                } else if (fileType == 4) {
                    String imgName = UUID.randomUUID().toString() + ".jpg";
                    String base64Str = FileUtils.readFile(fullPath + "/" + filename);
                    //去掉头data:image/jpeg;base64,
                    String str = base64Str.substring(base64Str.indexOf(",")+1);
                    boolean isSuccess = Base64ToImageUtils.GenerateImage(str, fullPath + "/" + imgName);

                    if (!isSuccess) {
                        return ApiResult.fail(request, "上传失败");
                    }

                    urls.add(relativePath + "/" + imgName);
                } else {
                    urls.add(relativePath + "/" + filename);
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("urls", urls.toArray(new String[0]));
            return ApiResult.success(request, result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.fail(request, e.getMessage());
        }

    }

    private void checkParams(MultipartFile[] files, Integer fileType) throws AppRuntimeException {
        if (files == null || files.length == 0) throw new AppRuntimeException("未选择上传文件");
        if (fileType <= 0 || fileType > 4) throw new AppRuntimeException("文件类型参数错误");

        String[] imageType = {".png", ".jpg", ".jpeg"};
        String pdfType = ".pdf";
        String textType = ".txt";

        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            if (StringUtils.isEmpty(filename)) {
                continue;
            }
            String suffix = filename.substring(filename.lastIndexOf("."));

            switch (fileType)
            {
                case 1:
                    if(!Arrays.asList(imageType).contains(suffix)) {
                        throw new AppRuntimeException("请传入正确的图片格式文件");
                    }
                    break;
                case 2:
                    if(!Arrays.asList(imageType).contains(suffix)) {
                        throw new AppRuntimeException("请传入正确的图片格式文件");
                    }
                    break;
                case 3:
                    if(!pdfType.equals(suffix)) {
                        throw new AppRuntimeException("请传入正确的PDF格式文件");
                    }
                    break;
                case 4:
                    if(!textType.equals(suffix)) {
                        throw new AppRuntimeException("请传入正确的TXT格式文件");
                    }
                    break;
                default:
                    throw new RuntimeException("文件类型参数错误");
            }
        }

    }

    private String saveFile(String fullPath, MultipartFile mf) throws IOException {
        String filename = mf.getOriginalFilename();
        if (StringUtils.isEmpty(filename)) {
            return null;
        }
        String name = filename.substring(0, filename.lastIndexOf("."));
        String suffix = filename.substring(filename.lastIndexOf("."));
//        String newName = UUID.randomUUID() + suffix;
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String newName = name + sdf.format(new Date()) + suffix;
        File dest = new File(fullPath + "/" + newName);
        logger.debug("path = {}", fullPath + "/" + newName);
        mf.transferTo(dest);
        return newName;
    }

    private void checkPath(String pathStr) {
        File path = new File(pathStr);
        if (!path.exists()) {
            path.mkdirs();
        }
    }

}
