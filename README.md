##常见特殊验证码识别
##58同城滑块及手势验证码
* 先抓包调试定位到js加密位置,进行分析
```javascript
s.AESEncryption('{"x":"' + (e || 0) + '","g":"' + r + '","p":"' + o.pj + '","finger":"' + (t.xxzlfingertoken ? t.xxzlfingertoken : "") + '"}');
```
*滑块位置使用滑块跟周围色差求得
*手势验证码使用[image-segmentation-keras](https://github.com/divamgupta/image-segmentation-keras)项目训练得到
*如果pom少包,请自行下载我其他项目