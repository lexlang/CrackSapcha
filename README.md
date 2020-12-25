# 常见特殊验证码识别
* 请勿将本项目用于商业用途,不提供技术,仅供学习参考。如有侵权或其他问题,请发[邮件](fenghongbingcha@gmail.com)
* 如果pom少包,请自行下载我其他项目
## 58同城滑块及手势验证码
* 具体分析请参考[58/anjuke 手势验证码破解思路](https://www.cnblogs.com/triangle959/p/spider-captcha.html)
* 滑块位置使用滑块跟周围色差求得
* 手势验证码识别过程请参考[vaptcha等手势验证码图像识别与轨迹提取](https://wenanzhe.com/2020/08/11/vaptcha%e7%ad%89%e6%89%8b%e5%8a%bf%e9%aa%8c%e8%af%81%e7%a0%81%e5%9b%be%e5%83%8f%e8%af%86%e5%88%ab%e4%b8%8e%e8%bd%a8%e8%bf%b9%e6%8f%90%e5%8f%96/),我使用[image-segmentation-keras](https://github.com/divamgupta/image-segmentation-keras)项目训练得到[vCatpcha](https://github.com/lexlang/vCatpcha)

## 易盾滑块验证码
* 具体分析请参考[网易滑块验证码加密参数分析及滑块轨迹算法data是怎么生成的](https://www.it610.com/article/1290311707540922368.htm)
* 滑块位置同上

### java采集技术交流群 ：538441450