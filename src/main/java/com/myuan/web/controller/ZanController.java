package com.myuan.web.controller;

import com.myuan.web.entity.MyResult;
import com.myuan.web.service.ZanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @author liuwei
 * @date 2018/3/1 9:13
 * 点赞
 */
@RestController
@Api("点赞接口层")
@RequestMapping("api")
public class ZanController extends BaseController{

    @Autowired
    private ZanService zanService;

    @PostMapping("/zan/{userid}/{answerid}/{num}")
    @ApiOperation(value = "点赞", notes = "点赞")
    public MyResult zan(@PathVariable("answerid") Long answerId,
                        @PathVariable("userid") Long userId,
                        @PathVariable("num") String flag) {
        if("true".equals(flag)) {
            zanService.addZan(userId, answerId);
        } else {
            zanService.removeZan(userId, answerId);
        }
        return MyResult.ok("");
    }
}
