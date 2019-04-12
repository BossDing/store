 
 ## P2P平台授权接口规范
 
 #### 对接准备：管理员分配 appId appSecret
 #### 接口地址：https://yliao.com/api/oauth
 #### 接口方式：POST（application/x-www-form-urlencoded）

| 参数 | 类型 |描述 |
| ---- | ---- | ---- |
| appId | String | 平台ID（平台ID） |
| mobile | String |  授权账号（手机号码） |
| name | String |  真实姓名（务必真实）  |
| identity | String |  身份证号（务必真实）  |
| amount | BigDecimal |  授权金额（格式:1.23 / 单位:元） |
| sign | String |  请求签名（签名方式:MD5） |

 #### 签名方式：以上字段除去sign，按照参数名的字典顺序排列，以如下形式拼接成字符串，然后加上appSecret字符串，将整个字符串MD5得到sign。
 ```
 [appId=1111166059554922498]
 [appSecret=1234567890]
 
 1.字典排列
 amount=30000&appId=1111166059554922498&identity=330823XXXXXXXX2115&mobile=137XXXX9882&name=王XX
 
 2.加上appSecret
 amount=30000&appId=1111166059554922498&identity=330823XXXXXXXX2115&mobile=137XXXX9882&name=王XX1234567890
 
 3.最后MD5
 sign=9aa9c188d2e10c73a3638dc1df495002
 ```
 #### JAVA签名示例代码：
 ```
    public String getSignContent(Map<String, String> params) {
        if (params == null) {
            return null;
        } else {
            params.remove("sign");
            StringBuffer content = new StringBuffer();
            List<String> keys = new ArrayList(params.keySet());
            Collections.sort(keys);
            for (int i = 0; i < keys.size(); ++i) {
                String key = String.valueOf(keys.get(i));
                String value = String.valueOf(params.get(key));
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }
            return content.toString();
        }
    }
    
    String sign = DigestUtil.md5Hex(this.getSignContent(PARAMS_MAP) + APP_SECRET);
 ```