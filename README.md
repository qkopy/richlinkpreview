# RICHLINK PREVIEW
## Android RichLink Preview Library


#### Implementation

In XML

```
<com.qkopy.richlink.RichLinkViewQkopy
            android:id="@+id/richLink"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_constraintBottom_toTopOf="@id/edittext"
            android:layout_marginBottom="10dp"
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
