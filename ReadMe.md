# ClockfaceView #
一个具有渐变、动画效果的时钟控件  

![ClockfaceView](/ClockfaceView.gif)  

###[apk下载](https://github.com/EthanCo/ClockfaceView/raw/master/ClockfaceView.apk)

## 添加依赖 ##

###Step 1. Add the JitPack repository to your build file  

Add it in your root build.gradle at the end of repositories:  

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}  

###Step 2. Add the dependency  

	dependencies {
		compile 'com.github.User:Repo:Tag'
	}  

## 相关方法 ##

 		<!--外环渐变 开始颜色-->
        <attr name="ringStartColor" format="color" />
        <!--外环渐变 结束颜色-->
        <attr name="ringEndColor" format="color" />
        <!--外环宽度-->
        <attr name="ringWidth" format="dimension" />

        <!--小时:分钟 Text渐变 开始颜色-->
        <attr name="hourStartColor" format="color" />
        <!--小时:分钟 Text渐变 结束颜色-->
        <attr name="hourEndColor" format="color" />
        <!--小时:分钟 字体大小-->
        <attr name="hourTextSize" format="dimension" />

        <!--表盘 颜色-->
        <attr name="plateColor" format="color" />

        <!--顶部字体颜色-->
        <attr name="mTopTextColor" format="color" />
        <!--顶部字体大小-->
        <attr name="mTopTextSize" format="dimension" />

        <!--底部字体颜色-->
        <attr name="mBottomTextColor" format="color" />
        <!--底部字体大小-->
        <attr name="mBottomTextSize" format="dimension" />

        <!--底部(次标题)字体颜色-->
        <attr name="mBottomSecondTextColor" format="color" />
        <!--底部(次标题)字体大小-->
        <attr name="mBottomSecondTextSize" format="dimension" />  

## 示例 ##

### 最简单的使用 ###
	<com.ethanco.clockface.ClockfaceView
        android:layout_width="400dp"
        android:layout_height="400dp"/>  

### 具体的使用 ###

	<com.ethanco.clockface.ClockfaceView
        android:layout_width="400dp"
        android:layout_height="400dp"
        android:layout_centerInParent="true"
        app:hourEndColor="#01ce9b"
        app:hourStartColor="#aee847"
        app:hourTextSize="42sp"
        app:mBottomSecondTextColor="#FFFFFF"
        app:mBottomSecondTextSize="16sp"
        app:mBottomTextColor="#FFFFFF"
        app:mBottomTextSize="22sp"
        app:mTopTextColor="#abc5b3"
        app:mTopTextSize="24sp"
        app:plateColor="#3b3a3f"
        app:ringEndColor="#aee847"
        app:ringStartColor="#01ce9b"
        app:ringWidth="20dp" />  

###也可以使用主题  

	 <style name="DefaultClockfaceStyle">
	    <item name="ringStartColor">#01ce9b</item>
	    <item name="ringEndColor">#aee847</item>
	    <item name="hourStartColor">#aee847</item>
	    <item name="hourEndColor">#01ce9b</item>
	    <item name="plateColor">#3b3a3f</item>
	
	    <item name="ringWidth">20dp</item>
	    <item name="hourTextSize">42sp</item>
	
	    <item name="mTopTextColor">#abc5b3</item>
	    <item name="mTopTextSize">24sp</item>
	
	    <item name="mBottomTextColor">#FFFFFF</item>
	    <item name="mBottomTextSize">22sp</item>
	
	    <item name="mBottomSecondTextColor">#FFFFFF</item>
	    <item name="mBottomSecondTextSize">16sp</item>
    </style>  