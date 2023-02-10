package com.zf.common.product.controller;

import com.zf.common.product.entity.vo.UploadRequest;
import com.zf.common.product.utils.ImageUtil;
import com.zf.common.product.utils.MinioUtilS;
import com.zf.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/thirdparty/oss")
public class MinioController {

    @Autowired
    private MinioUtilS minioUtilS;

    @GetMapping("/policy")
    public R uploadToOss(@RequestBody UploadRequest req){
        try {
            MultipartFile[] file = new MultipartFile[]{ImageUtil.getMultipartFile(req.getImageUrl())};

            List<String> res = minioUtilS.upload(file);
            return R.ok().put("data", res);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
