package com.qf.sso.core.validator;

import com.qf.sso.core.common.CheckWithResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiufeng
 * @date 2020/2/13 14:41
 */
@Data
public class MutipleValidator {
    List<IBaseValidator> list = new ArrayList<IBaseValidator>();
    public CheckWithResult<?> validate(){
        for(IBaseValidator validator:list){
            CheckWithResult result = validator.validate("");
            if(!result.isSuccess()){
                return result;
            }
        }
        return new CheckWithResult<>();
    }
}
