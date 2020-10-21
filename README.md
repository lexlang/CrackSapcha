# 常见特殊验证码识别
* 请勿将本项目用于商业用途,不提供技术,仅供学习参考。如有侵权或其他问题,请发[邮件](fenghongbingcha@gmail.com)
## 58同城滑块及手势验证码
* 具体分析请参考[58/anjuke 手势验证码破解思路](https://www.cnblogs.com/triangle959/p/spider-captcha.html)
* 滑块位置使用滑块跟周围色差求得
* 手势验证码使用[image-segmentation-keras](https://github.com/divamgupta/image-segmentation-keras)项目训练得到
* 如果pom少包,请自行下载我其他项目