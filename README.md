# RICHLINK PREVIEW
## Android RichLink Preview Library

#### Example

![](richlink.gif)



#### Adding Library to Project

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
   repositories {
      ...
      maven { url 'https://jitpack.io' }
   }
}
```
Step 2. Add the dependency

VERSION: [![](https://jitpack.io/v/qkopy/richlinkpreview.svg)](https://jitpack.io/#qkopy/richlinkpreview)

```
dependencies {
    implementation "com.github.qkopy:richlinkpreview:[version]"
}
```


#### Implementation

In XML

```
<com.qkopy.richlink.RichLinkViewQkopy
            android:id="@+id/richLink"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
          />
```

In Your Activity

```
richLink.setLink("your-url",this@MainActivity, object : ViewListener {
          override fun onSuccess(status: Boolean) {

          }

          override fun onError(e: Exception) {

          }
})
```

Setting a Custom Click listener

```
richLink.setDefaultClickListener(false)

 richLink.setClickListener(object : RichLinkListener {
    override fun onClicked(view: View, meta: MetaData?) {
       TODO("Not yet implemented")
    }
})
```

#### Optional

Set Link Cache size with 

```
richLink.setDBCacheLimit(NO_OF_LINKS_TO_CACHE_INT)
```


