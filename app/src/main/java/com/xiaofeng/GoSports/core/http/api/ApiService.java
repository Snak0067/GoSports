

package com.xiaofeng.GoSports.core.http.api;

import com.xiaofeng.GoSports.core.http.entity.TipInfo;
import com.xuexiang.xhttp2.model.ApiResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * @author xiaofeng
 * @since 2022/4/9 7:01 PM
 */
public class ApiService {

    /**
     * 使用的是retrofit的接口定义
     */
    public interface IGetService {

        /**
         * 获得小贴士
         */
        @GET("/app/src/main/assets/tips.json")
        Observable<ApiResult<List<TipInfo>>> getTips();
    }

}
