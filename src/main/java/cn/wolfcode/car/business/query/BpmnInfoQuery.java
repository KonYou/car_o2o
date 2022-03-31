package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

@Getter
public class BpmnInfoQuery extends QueryObject {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    public void setEndTime(Date endTime){
        if (endTime == null){
            return;
        }

        Long time = 1L;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        calendar.add(Calendar.DATE,1);
        this.endTime = new Date(calendar.getTime().getTime() - time);
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }
}
