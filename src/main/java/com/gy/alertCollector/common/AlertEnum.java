package com.gy.alertCollector.common;

/**
 * Created by gy on 2018/5/6.
 */
public interface AlertEnum {

   public enum AlertResolvedType{

       UNRESOLVED("firing"),
       RESOLVED("resolved");
       String name;

       AlertResolvedType(String name) {
           this.name = name;
       }
       public String value() {
           return this.name;
       }
   }
    public enum AlertType{

        RULENAME_PERF("perf"),
        RULENAME_AVL("avl");
        String name;

        AlertType(String name) {
            this.name = name;
        }
        public String value() {
            return this.name;
        }
    }
    public enum AlertI18n{
        AVL_NOTREACH("alert.rule.status.notreach"),
        PERF_VALUE_OVERTHRESHOLD("alert.rule.value.overThreshold"),
        PERF_VALUE_BELOWTHRESHOLD("alert.rule.value.belowThreshold");
        String name;

        AlertI18n(String name) {
            this.name = name;
        }
        public String value() {
            return this.name;
        }
    }
}
