package com.learning.interrogation.domain.constant;

/**
 * @ClassName: AgreementContant
 * @Description: 协议类型常量
 * @Author: tangYP
 * @Date: 2019/10/25 10:07
 * @Version V1.0
 **/
public interface RedisConstant {

    /**
     * 互联网 redis key 统一前缀
     */
    String COMMON_PREFIX_KEY = "HLWYL:";

    /**
     * 互联网 redis key 统一前缀
     */
    String WILDCARD_CHARACTER = "*";

    /**
     * redis 存储 AUTH_CODE key 前缀
     */
    String REDIS_AUTH_CODE_KEY_PREFIX = "HLWYL:AUTH_CODE:";

    /**
     * 退费同步  REFUND key 前缀
     */
    String REDIS_REFUND_KEY_PREFIX = "HLWYL:TF:";

    /**
     * redis锁标识
     */
    String REDIS_LOCK = "REDIS_LOCK:";

    /**
     * 预约挂号
     */
    String APPOINMENT_REGISTER_INFO_PUSH_JOB_ID = "APPOINTMENT_REGISTER_INFO_PUSH_JOB_ID:";

    /**
     * 预约挂号规则
     */
    String APPOINTMENT_REGISTER_RULE = "HLWYL:APPOINTMENT_REGISTER_RULE:";

    /**
     * 上次人员同步时间缓存前缀
     */
    String SYNCHRONIZATION_TIME_PREFIX = "SYNCHRONIZATION_TIME";

    /**
     * 自主问诊支付状态缓存
     */
    String HOSPITAL_PREPARATION_SELF_CONSULTATION_PAYMENT_STATUS = "HOSPITAL_PREPARATION_SELF_CONSULTATION_PAYMENT_STATUS:";

    /**
     * 机构菜单列表 key
     */
    String Org_Menu="menu:";

    /**
     * 院内制剂状态获取 key
     */
    String HOSPITAL_PREPARATION_STATUS ="HOSPITAL_PREPARATION_STATUS:";

    /**
     * 挂号费用 key
     */
    String REDIS_REGISTERED_AMOUNT_KEY = "REGISTERED:AMOUNT:";

    /**
     * 院内制剂推荐列表获取 key
     */
    String HOSPITAL_PREPARATION_RECOMMENDED_LIST ="HOSPITAL_PREPARATION_RECOMMENDED_LIST:";

    /**
     * 代诊人信息 key
     */
    String REFERRAL_INFORMATION = "REGISTERED:AMOUNT:";

    /**
     * his 机构信息 info
     */
    String HIS_DEPT_INFO = "HIS_DEPT_INFO";

    /**
     * 延时任务 Zset 集合 key
     */
    String DELAYED_TASK_LIST ="DELAYED_TASK_LIST";

    /**
     * 5min支付倒计时
     */
    String PAYMENT_COUNTDOWN = "PAYMENT_COUNTDOWN";

    /**
     * 病情描述填写
     * */
    String FILL_DISEASE_DESC = "FILL_DISEASE_DESC";

    /**
     * 支付平台任务 key
     */
    String PAYMENT_REPLENISHMENT = "PAYMENT_REPLENISHMENT:";

    String MALICE_USER_AUTH_TIMES = "maliceAuthUserAuthTimes:";

    String CACHE_DIAGNOSIS_PAYMENT = "cacheDiagnosisPayment:";
    String HOSPITAL_ORDER = "HOSPITAL_ORDER:";
    String PUSH_IMMSG = "HLWYL:PUSH_IMMSG:";
    String ACCESSTOKEN = "zfbAccessToken:";
    String BUSINESS_ORDER = "BUSINESS_ORDER:";

    //系统公告
    String SYSTEM_NOTICE = "SYSTEM_NOTICE:";

    /** 处方购药 运费订单 */
    String PRESCRIPTION_ORDER_LOGISTICS = "HLWYL:CFGY:WLDDSX:";


    /**
     * 正在创建患者信息缓存
     */
    String PATIENT_CREATING = "PATIENT_CREATING:";

    /** 住院记录规则 */
    String BE_HOSPITALIZED_RULE = "HLWYL:ZYGL:GZ:";

    /**
     * 出院预结算信息
     */
    String PRE_SETTLEMENT_INFO = "PRE_SETTLEMENT_INFO:";

    /**
     * 出院结算记录 key
     */
    String DISCHARGE_SETTLEMENT_RECORD = "DISCHARGE_SETTLEMENT_RECORD:";

    /**
     * 出院结算缴费记录 key
     */
    String DISCHARGE_SETTLEMENT_PAY_RECORD = "DISCHARGE_SETTLEMENT_PAY_RECORD";

    /**
     * 出院结算状态标签
     */
    String DISCHARGE_SETTLEMENT_STATUS = "DISCHARGE_SETTLEMENT_STATUS:";

    /**
     * 防止处方审核并发 key
     */
    String PRESCRIPTION_REVIEW_COMPLICATION = "PRESCRIPTION_REVIEW_COMPLICATION:";

    /**
     * 启用的院内制剂分类列表
     */
    String ENABLE_HOSPITAL_PREPARATIONS_CLASSIFICATION = "ENABLE_HOSPITAL_PREPARATIONS_CLASSIFICATION:";

    /**
     * 出院结算锁 key
     */
    String DISCHARGE_SETTLEMENT_LOCK = "DISCHARGE_SETTLEMENT_LOCK:";

    /**
     * 微信公众号 accessToken
     */
    String PUBLIC_ACCOUNT_ACCESS_TOKEN = "PUBLIC_ACCOUNT_ACCESS_TOKEN";

    /**
     * 拉取微信公众号 accessToken 锁 key
     */
    String PUBLIC_ACCOUNT_ACCESS_TOKEN_LOCK = "PUBLIC_ACCOUNT_ACCESS_TOKEN_LOCK:";

    /**
     * 拉取微信公众号 accessToken 锁 key
     */
    String PUBLIC_ACCOUNT_UPGRADE = "PUBLIC_ACCOUNT_UPGRADE:";

    /**
     * 拉取微信公众号 accessToken 锁 key
     */
    String PUBLIC_ACCOUNT_PATIENT_INFO = "PUBLIC_ACCOUNT_PATIENT_INFO";

    /**
     * 支付状态
     */
    String PAY_STATUS = "PAY_STATUS:";

    /**
     * 协同排版系统redis常量
     */
    String YSPB_PERFIX = "multi:yspb";
    String GHYS_PERFIX = "multi:ghys";
    String GHYSS_PERFIX = "multi:ghyss";
    String RCP_JGBM_PERFIX = "multi:jgbm";
    String RCP_KSHZ_PERFIX = "multi:kshz";


    /**
     * 微信医保授权用户信息
     *
     * */
    String WECHAT_AUTH_ACCOUNT= "HLWYL:YB:WECHAT_AUTH:";
}
