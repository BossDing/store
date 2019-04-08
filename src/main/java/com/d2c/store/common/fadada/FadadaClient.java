package com.d2c.store.common.fadada;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.d2c.store.common.api.Asserts;
import com.d2c.store.common.fadada.sdk.client.FddClientBase;
import com.d2c.store.common.fadada.sdk.client.FddClientExtra;
import com.d2c.store.common.fadada.sdk.client.authForplatform.ApplyClientNumCert;
import com.d2c.store.common.fadada.sdk.client.authForplatform.CompanyDeposit;
import com.d2c.store.common.fadada.sdk.client.authForplatform.PersonDeposit;
import com.d2c.store.common.fadada.sdk.client.authForplatform.model.CompanyDepositReq;
import com.d2c.store.common.fadada.sdk.client.authForplatform.model.CompanyPrincipalVerifiedMsg;
import com.d2c.store.common.fadada.sdk.client.authForplatform.model.PersonDepositReq;
import com.d2c.store.common.fadada.sdk.client.authForplatform.model.PublicSecurityEssentialFactor;
import com.d2c.store.common.fadada.sdk.client.request.ExtsignReq;
import com.d2c.store.modules.core.model.P2PDO;
import com.d2c.store.modules.member.model.AccountDO;
import com.d2c.store.modules.member.model.MemberDO;
import com.d2c.store.modules.order.model.OrderDO;
import com.d2c.store.modules.order.model.OrderItemDO;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 法大大业务
 */
public class FadadaClient {

    //测试用的模板ID
    public static final String template_id = "template01";
    //法大大服务器地址
    private static String HOST;
    //APPID
    private static String APP_ID;
    //APPSECRET
    private static String APP_SECRET;
    //版本号
    private static String V = "2.0";
    //图片域名
    private static String PIC_BASE="http://s.fune.store";

    public static void main(String[] args) throws MalformedURLException, URISyntaxException {
        /**
         * 测试数据
         *
         *  15CBE6F987621E865C6A194A16C6770E 客户编号
         *  20190403151238498570518 客户证书编号
         *  sign_id 4342920
         *  template01
         *  contract_01
         *  signImg "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"
         */
        MemberDO memberDO = new MemberDO();
        memberDO.setCustomerId("15CBE6F987621E865C6A194A16C6770E");
        memberDO.setName("路人甲");
        memberDO.setIdentity("330327199001012222");
        memberDO.setMobile("13197677777");
        List<OrderItemDO> list = new ArrayList<>();
        OrderItemDO oi1 = new OrderItemDO();
        oi1.setProductName("商品1");
        oi1.setProductPrice(new BigDecimal(1));
        oi1.setQuantity(1);
        OrderItemDO oi2 = new OrderItemDO();
        oi2.setProductName("商品2");
        oi2.setProductPrice(new BigDecimal(2));
        oi2.setQuantity(2);
        list.add(oi1);
        list.add(oi2);
        OrderDO orderDO = new OrderDO();
        orderDO.setPayAmount(new BigDecimal(5));
        orderDO.setProvince("浙江");
        orderDO.setCity("杭州");
        orderDO.setDistrict("上城区");
        orderDO.setAddress("莲花峰路12号");
        orderDO.setMobile("13197677777");
        AccountDO accountDO = new AccountDO();
        accountDO.setOauthAmount(new BigDecimal(6));
        accountDO.setDeadline(new Date());
        P2PDO p2PDO = new P2PDO();
        p2PDO.setName("杭州迪尔西");
        p2PDO.setCreditCode("测试111111111111111111111");
        p2PDO.setAddress("莲花峰路");
        p2PDO.setMobile("123456789");
        p2PDO.setCustomerId("DB8C8351459F2DEE51A1DEEDB221BBDC2");
        //generateContract("template01","contract_01","测试合同",list,orderDO,accountDO,memberDO,p2PDO);
        extSignAuto("15CBE6F987621E865C6A194A16C6770E", "transation111", "contract_01", "测试合同");
        p2PDO.setCreditCodeFile("https://img1.360buyimg.com/imgb/s250x250_jfs/t27610/66/981425374/258674/92bbf014/5bbf1420N3362755f.jpg");
        p2PDO.setPowerAttorneyFile("https://img1.360buyimg.com/imgb/s250x250_jfs/t27610/66/981425374/258674/92bbf014/5bbf1420N3362755f.jpg");
        p2PDO.setLegalName("lin");
        p2PDO.setIdentity("330327198905051111");
        companyDeposit(p2PDO, "12", "12");
    }

    /**
     * 注册账号
     *
     * @param open_id      open_id      用户在接入方的唯一标识（必填）
     * @param account_type 账号类型（必填1个人，2企业）
     * @return 返回法大大客户编号 customerId
     */
    public static String registerAccount(String open_id, String account_type) {
        FddClientBase clientBase = new FddClientBase(APP_ID, APP_SECRET, V, HOST);
        String result = clientBase.invokeregisterAccount(open_id, account_type);
        System.out.println(result);
        JSONObject response = JSON.parseObject(result);
        Asserts.eq(response.getString("code"), "1", response.getString("msg"));
        return response.getString("data");
    }

    /**
     * 个人实名认证
     *
     * @param memberDO 会员信息
     */
    public static String personDeposit(MemberDO memberDO, String applyNum) {
        PersonDeposit personDeposit = new PersonDeposit(APP_ID, APP_SECRET, V, HOST);
        PersonDepositReq req = new PersonDepositReq();
        req.setCustomer_id(memberDO.getCustomerId()); //客户编号
        /**=======存证相关===========*/
        req.setPreservation_name("债券合同"); //存证名称
        req.setPreservation_data_provider(memberDO.getName());//存证数据提供方
        req.setName(memberDO.getName());//姓名
        /**=======证件相关===========*/
        req.setDocument_type("1");//证件类型默认是1：身份证
        req.setIdcard(memberDO.getIdentity());//证件号
        req.setMobile(memberDO.getMobile());//手机号
        req.setVerified_time(DateUtil.format(new Date(), "yyyyMMddHHmmss"));//实名时间
        req.setVerified_type("1");//实名存证类型
        PublicSecurityEssentialFactor public_security_essential_factor = new
                PublicSecurityEssentialFactor();
        //1:公安部二要素(姓名+身份证);
        public_security_essential_factor.setApply_num(applyNum);//申请编号
        req.setPublic_security_essential_factor(public_security_essential_factor);//verified_type =1 公安部二要素
        String result = personDeposit.invokePersonDeposit(req);
        System.out.println(result);
        JSONObject response = JSON.parseObject(result);
        Asserts.eq(response.getString("code"), "1", response.getString("msg"));
        return response.getString("data");
    }

    /**
     * 企业信息实名存证
     *
     * @param p2PDO          p2p企业
     * @param transaction_id 业务号
     * @param apply_num      申请号
     */
    public static String companyDeposit(P2PDO p2PDO, String transaction_id, String apply_num) {
        CompanyDeposit companyDeposit = new CompanyDeposit(APP_ID, APP_SECRET, V, HOST);
        CompanyDepositReq req = new CompanyDepositReq();
        req.setCustomer_id(p2PDO.getCustomerId());
        /**=======存证相关===========*/
        req.setPreservation_name("债券合同");//存证名称
        req.setPreservation_data_provider(p2PDO.getName());//存证数据提供方
        req.setCompany_name(p2PDO.getName());//企业名称
        /**=======证件相关===========*/
        req.setDocument_type("1"); //证件类型1:三证合一2：旧版营业执照
        req.setCredit_code(p2PDO.getCreditCode()); //统一社会信用代码document_type =1 时必填
        String codefile = getLocalFilePath(p2PDO.getCreditCodeFile());
        req.setCredit_code_file(new File(codefile)); //统一社会信用代码电子版
        req.setVerified_time(DateUtil.format(new Date(), "yyyyMMddHHmmss"));//实名时间
        /**=======认证方式相关===========*/
        req.setVerified_mode("1");//实名认证方式1:授权委托书2:银行对公打款
        //verifiedMode =1 必填
        String powerAttorneyfile = getLocalFilePath(p2PDO.getCreditCodeFile());
        req.setPower_attorney_file(new File(powerAttorneyfile));//调资源维护接口返回verifiedMode =1 必填
        req.setCompany_principal_type("1");//企业负责人身份:1.法人，2 代理人
        /**=======法人信息===========*/
        req.setLegal_name(p2PDO.getLegalName());//法人姓名
        req.setLegal_idcard(p2PDO.getIdentity());//法人身份证号
        req.setTransaction_id(transaction_id);//交易号
        /**=======企业负责人实名存证信息===========*/
        CompanyPrincipalVerifiedMsg msg = new CompanyPrincipalVerifiedMsg();
        msg.setCustomer_id(p2PDO.getCustomerId());//企业负责人客户编号
        //存证描述相关
        msg.setPreservation_name("债权企业存证_" + p2PDO.getName());//存证名称
        msg.setPreservation_data_provider(p2PDO.getName());//存证数据提供方
        //负责人信息相关
        msg.setDocument_type("1");//证件类型默认是1：身份证
        msg.setName(p2PDO.getLegalName());//姓名
        msg.setIdcard(p2PDO.getIdentity());//证件号
        msg.setMobile(p2PDO.getMobile());//手机号
        PublicSecurityEssentialFactor public_security_essential_factor = new
                PublicSecurityEssentialFactor();
        //1:公安部二要素(姓名+身份证);
        public_security_essential_factor.setApply_num(apply_num);//申请编号
        //存证类型相关
        msg.setVerified_time(DateUtil.format(new Date(), "yyyyMMddHHmmss"));//实名时间
        msg.setVerified_type("1");
        msg.setPublic_security_essential_factor(public_security_essential_factor);//verified_type =1公安部二要素
        req.setCompany_principal_verified_msg(msg);//企业负责人身份:1.法人，2 代理人
        String result = companyDeposit.invokeCompanyDeposit(req);
        System.out.println(result);
        JSONObject response = JSON.parseObject(result);
        Asserts.eq(response.getString("code"), "1", response.getString("msg"));
        return response.getString("data");
    }

    /**
     * 编号证书申请
     *
     * @param customer_id 客户编号
     * @param evidence_no 存证编号
     */
    public static void applyClinetNumcert(String customer_id, String evidence_no) {
        ApplyClientNumCert applyClientNumCert =
                new ApplyClientNumCert(APP_ID, APP_SECRET, V, HOST);
        String result
                = applyClientNumCert.invokeapplyClinetNumcert(customer_id, evidence_no);
        System.out.println(result);
    }

    /**
     * 自定义印章
     *
     * @param customer_id 客户编号
     * @param content     印章展示的内容
     */
    public static String customSignature(String customer_id, String content) {
        FddClientBase base = new FddClientBase(APP_ID, APP_SECRET, V, HOST);
        String result = base.invokecustomSignature(customer_id, content);
        System.out.println(result);
        JSONObject response = JSON.parseObject(result);
        Asserts.eq(response.getString("code"), "1", response.getString("msg"));
        return response.getJSONObject("data").getString("signature_img_base64");
    }

    /**
     * 印章上传
     *
     * @param customer_id          客户编号
     * @param signature_img_base64 印章图片base64(由自定义印章获取)
     */
    public static String addSignature(String customer_id, String signature_img_base64) {
        FddClientBase base = new FddClientBase(APP_ID, APP_SECRET, V, HOST);
        String result = base.invokeaddSignature(customer_id, signature_img_base64);
        System.out.println(result);
        JSONObject response = JSON.parseObject(result);
        Asserts.eq(response.getString("code"), "1", response.getString("msg"));
        return response.getJSONObject("data").getString("signature_id");
    }

    /**
     * 模板上传
     *
     * @param template_id 模板ID
     * @param file        模板文件
     */
    public static void uploadtemplate(String template_id, File file) {
        FddClientBase base = new FddClientBase(APP_ID, APP_SECRET, V, HOST);
        String doc_url = null;//模板公网下载地址
        String result = base.invokeUploadTemplate(template_id, file, doc_url);
        System.out.println(result);
    }

    /**
     * 模板填充
     *
     * @param template_id 模板ID
     * @param contract_id 合同编号
     * @param doc_title   合同标题
     * @param list        订单明细列表
     * @param orderDO     订单
     * @param accountDO   钱包账号
     * @param memberDO    会员账号
     * @param p2PDO       P2P平台
     */
    public static JSONObject generateContract(String template_id, String contract_id, String doc_title, List<OrderItemDO> list
            , OrderDO orderDO, AccountDO accountDO, MemberDO memberDO, P2PDO p2PDO) {
        FddClientBase base = new FddClientBase(APP_ID, APP_SECRET, V, HOST);
        String font_size = "10";//字体大小
        String font_type = "0";//字体类型
        String paramter = getparamter(orderDO, accountDO, memberDO, p2PDO);//填充内容
        String dynamic_tables = getdynamic_tables(list);//动态表格
        String result = base.invokeGenerateContract(template_id, contract_id, doc_title,
                font_size, font_type, paramter, dynamic_tables);
        System.out.println(result);
        JSONObject response = JSON.parseObject(result);
        return response;
    }

    private static String getdynamic_tables(List<OrderItemDO> list) {
        JSONArray dynamic_tables = new JSONArray();
        JSONObject dynamic2 = new JSONObject();
        dynamic2.put("insertWay", 1);
        dynamic2.put("keyword", "具体实物信息见下表");
        dynamic2.put("pageBegin", "2");
        dynamic2.put("cellHeight", "16.0");//行高
        dynamic2.put("colWidthPercent", new int[]{4, 4, 4});//各列宽度比利
        //dynamic2.put("theFirstHeader", "附二");//表头标题
        dynamic2.put("cellHorizontalAlignment", "1");//水平对齐方式 (0：居左；1：居中；2：居右)
        dynamic2.put("cellVerticalAlignment", "5"); //垂直对齐方式 (4：居上；5：居中；6：居下)
        dynamic2.put("headers", new String[]{"货品名称", "货品单价", "货品数量"});
        String[][] dates = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            OrderItemDO oi = list.get(i);
            String[] row = new String[]{oi.getProductName(), oi.getProductPrice().toString(), oi.getQuantity().toString()};
            dates[i] = row;
        }
        dynamic2.put("datas", dates);
        dynamic2.put("headersAlignment", "1");
        dynamic2.put("tableWidthPercentage", 80);
        dynamic_tables.add(dynamic2);
        System.out.println(dynamic_tables.toString());
        return dynamic_tables.toString();
    }

    private static String getparamter(OrderDO orderDO, AccountDO accountDO, MemberDO memberDO, P2PDO p2PDO) {
        JSONObject paramter = new JSONObject();
        paramter.put("sign_time", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        paramter.put("down_time", DateUtil.format(accountDO.getDeadline(), "yyyyMMddHHmmss"));//合同结束日期
        //甲方
        paramter.put("part_a", memberDO.getName());
        paramter.put("id_card_a", memberDO.getIdentity());
        paramter.put("address_a", orderDO.getProvince() + orderDO.getCity() + orderDO.getDistrict() + orderDO.getAddress());
        paramter.put("tel_a", orderDO.getMobile());
        //乙方
        paramter.put("part_b", p2PDO.getName());
        paramter.put("credit_code_b", p2PDO.getCreditCode());
        paramter.put("address_b", p2PDO.getAddress());
        paramter.put("tel_b", p2PDO.getMobile());
        //合同金额
        paramter.put("xf_money", accountDO.getOauthAmount());//剩余出借本金小写
        paramter.put("xf_max_money", Convert.digitToChinese(accountDO.getOauthAmount()));//剩余出借本金大写
        paramter.put("price", orderDO.getPayAmount());//出借本金小写
        paramter.put("max_price", Convert.digitToChinese(orderDO.getPayAmount()));//出借本金大写
        return paramter.toString();
    }
    //===================================合同业务==================================

    /**
     * 自动签署
     *
     * @param customer_id    客户编号
     * @param transaction_id 交易号
     * @param contract_id    合同编号
     * @param doc_title      文档标题
     */
    public static JSONObject extSignAuto(String customer_id, String transaction_id, String contract_id, String doc_title) {
        FddClientBase base = new FddClientBase(APP_ID, APP_SECRET, V, HOST);
        ExtsignReq req = new ExtsignReq();
        req.setCustomer_id(customer_id);//客户编号
        req.setTransaction_id(transaction_id);//交易号
        req.setContract_id(contract_id);//合同编号
        req.setClient_role("1");//客户角色
        req.setSign_keyword("章1");//定位关键字
        req.setDoc_title(doc_title);//文档标题
        req.setNotify_url("");//签署结果回调地址
        String result = base.invokeExtSignAuto(req);
        return JSON.parseObject(result);
    }

    /**
     * 手动签署
     *
     * @param customer_id       客户编号
     * @param transaction_id    交易号
     * @param contract_id       合同编号
     * @param doc_title         文档标题
     * @param customer_mobile   客户手机号
     * @param customer_name     客户名称
     * @param customer_ident_no 客户身份证
     */
    public static String extsign(String customer_id, String transaction_id, String contract_id, String doc_title, String customer_mobile, String customer_name, String customer_ident_no) {
        // JAVA----短信校验
        FddClientBase base = new FddClientBase(APP_ID, APP_SECRET, V, HOST);
        ExtsignReq req = new ExtsignReq();
        req.setCustomer_id(customer_id);//客户编号
        req.setTransaction_id(transaction_id);//交易号
        req.setContract_id(contract_id);//合同编号
        req.setDoc_title(doc_title);//文档标题
        req.setReturn_url("");//页面跳转URL（签署结果同步通知）
        //短信校验该参数必填
        String sign_url = base.invokeExtSign(req, customer_mobile, customer_name, customer_ident_no);
        return sign_url;
        // sign_url 是组装好的地址，请重定向到这个地址呈现签署页面给用户
        // 例如：HttpServletResponse().sendRedirect(sign_url);
        // 输出签署页面
    }

    /**
     * 合同查看
     *
     * @param contract_id 合同编号
     */
    public static String viewContract(String contract_id) {
        FddClientExtra extra = new FddClientExtra(APP_ID, APP_SECRET, V, HOST);
        String view_url = extra.invokeViewPdfURL(contract_id);
        // 此时view_url 为查看链接，请开发者自行跳转
        return view_url;
    }

    /**
     * 合同下载
     *
     * @param contract_id 合同编号
     */
    public static void downloadPdf(String contract_id) {
        FddClientExtra extra = new FddClientExtra(APP_ID, APP_SECRET, V, HOST);
        String download_url = extra.invokeDownloadPdf(contract_id);
        // 此时download_url 为组装好的下载链接，请开发者自行跳转
    }

    /**
     * 合同归档
     *
     * @param contract_id 合同编号
     */
    public static void contractFilling(String contract_id) {
        FddClientBase base = new FddClientBase(APP_ID, APP_SECRET, V, HOST);
        String result = base.invokeContractFilling(contract_id);
    }

    /**
     * 网络图片存为文件（法大大业务存储企业凭证，并上传给发大大）
     *
     * @param urlString
     * @return
     */
    public static String getLocalFilePath(String urlString) {
        String filename = "/mnt/fadada/" + urlString.substring(urlString.lastIndexOf("/"), urlString.length());
        try {
            File file = new File(filename);
            if (file.exists()) {
                return filename;
            } else {
                if (!FileUtil.exist("/mnt/fadada/")) {
                    FileUtil.mkdir("/mnt/fadada/");
                }
                file.createNewFile();
            }
            URL url = new URL(PIC_BASE+urlString);// 构造URL
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();// 输入流
            //String code = con.getHeaderField("Content-Encoding");
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            OutputStream os = new FileOutputStream(filename);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;
    }

    @Value("${fadada.HOST}")
    public void setHOST(String HOST) {
        FadadaClient.HOST = HOST;
    }

    @Value("${fadada.APP_ID}")
    public void setAPP_ID(String APP_ID) {
        FadadaClient.APP_ID = APP_ID;
    }

    @Value("${fadada.APP_SECRET}")
    public void setAPP_SECRET(String APP_SECRET) {
        FadadaClient.APP_SECRET = APP_SECRET;
    }

}
