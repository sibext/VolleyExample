Volley
======

The mirror from https://android.googlesource.com/platform/frameworks/volley
Added configuration for the maven.

How use it?
===========

* cd volley
* mvn install
* Add the section dependence to your maven config:

```xml
<dependency>
  <groupId>com.google</groupId>
  <artifactId>volley</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Example
=======
```java
ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(this), new BitmapLruCache());
imageLoader.get("http://sibext.com/i/l.png", new ImageLoader.ImageListener() {
  @Override
  public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
    // TODO: image was loaded
  }

  @Override
  public void onErrorResponse(VolleyError volleyError) {
    // TODO: Please inform about failed
  }
});
```
