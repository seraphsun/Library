安卓多渠道打包工具。
实现思路讲解： [Android批量打包提速 - GavinCT](http://www.cnblogs.com/ct2011/p/4152323.html)  

使用本工具，Android程序员仅需将ChannelUtil.java放入到工程里使用，以后打包的事情就不用自己动手了。  
安装个Python环境，运行一下MultiChannelBuildTool.py，谁都可以打包了！
# 具体使用步骤
将想要批量打包的apk文件拷贝到PythonTool目录下（与py同级），运行py脚本即可打包完成。（生成的渠道apk包在output_** 目录下）
# 目录介绍及使用注意
## PythonTool
Python2 与 Python3 都能正常使用 

- info目录下的channel用来存放渠道，多个渠道之间用换行隔开。  
  注意：  
  fork后通过Github clone，这个channel文件在Windows端是正常的，以换行隔开（`\r\n`)。  
  直接点击右侧的download下载zip，可能你在windows端看到的就不是以换行隔开的（`\n`)。  
  这是Github造成的。但不会影响程序最后的运行效果。   
  你可以粘贴下面的渠道到channel.txt中保持它在windows端的可读性。

  ```
  samsungapps
  hiapk
  anzhi
  360cn
  xiaomi
  myapp
  91com
  gfan
  appchina
  nduoa
  3gcn
  mumayi
  10086com
  wostore
  189store
  lenovomm
  hicloud
  meizu
  baidu
  googleplay
  wandou
  ```
  也可以自己来写入自己需要的市场，并以换行隔开

- ChannelBuild.py是多渠道打包的脚本。

## JavaUtil
ChannelUtil.java 用来解析渠道，直接拷贝到Android工程中使用即可。  
ChannelUtil中的getChannel方法可以方便的获取渠道。


