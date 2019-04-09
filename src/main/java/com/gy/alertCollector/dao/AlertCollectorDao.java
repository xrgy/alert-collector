package com.gy.alertCollector.dao;



import com.gy.alertCollector.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Created by gy on 2018/3/31.
 */
public interface AlertCollectorDao {
    public TestEntity getJPAInfo();


    /**
     * 查询已恢复告警
     * @param hashcode
     * @return
     */
    public CompletionStage<Optional<AlertEntity>> getAlertRecordByHashCode(Integer hashcode);

    /**
     * 查询未恢复告警
     * @param hashcode
     * @return
     */
    public CompletionStage<Optional<AlertEntity>> getAlertRecordByTagHashCode(Integer hashcode);

    /**
     * 插入告警到数据库
     * @param alertEntity
     * @return
     */
    public boolean insertIntoAlert(AlertEntity alertEntity);

    List<AlertEntity> getAlertDetail(AlertView view);

    List<AlertEntity> getAlertDetailByStatus(AlertView view);

    List<AlertEntity> getAlertDetailByStatusAndLightType(AlertView view,List<String> lightTypeList);

    List<AlertEntity> getAlertDetailByLightType(AlertView view,List<String> lightTypeList);
}
