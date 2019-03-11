# ZXing
[ ![Download](https://api.bintray.com/packages/zhaoyingtao/maven/zxing_library/images/download.svg) ](https://bintray.com/zhaoyingtao/maven/zxing_library/_latestVersion)   

先看功能图：   
![三种主要功能](https://github.com/zhaoyingtao/ZXing/blob/master/image/aaaa.png)  

![扫描功能](https://github.com/zhaoyingtao/ZXing/blob/master/image/bbbbb.jpg)  

一、在项目中直接引入

将下面的 x.y.z 更改为上面显示的版本号   
`
compile 'com.bintray.library:zxing_library:x.y.z'
`    

二、调用方法   
```
1、生成带logo的二维码  
  Bitmap bitmapa = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);//logo图
  //如果生成的logo模糊那就将宽高值放大
  Bitmap bitmap = QRCodeUtil.init().createQRCodeBitmap("nihao", 400, 400, bitmapa);
  
2、生成带普通的二维码（还可以传其他参数，设置二维码，自己去看源码，有注释）
 Bitmap bitmap2 = QRCodeUtil.init().createQRCodeBitmap("nihao", 400, 400);
 
3、跳转默认扫描页面进行扫描获取扫描结果
//跳转
startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class)
                    , CaptureActivity.REQ_CODE);
//扫描结果回调
 @Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CaptureActivity.REQ_CODE://扫描结果
                    String resultStr = data.getStringExtra(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
                    break;
                default:
                    break;
            }
        }
    }
            
```


最后要是需要自定义扫描页面，可以参考CaptureActivity类进行自定义
