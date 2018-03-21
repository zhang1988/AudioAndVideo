package com.zhangchao.audioandvideo.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zhangchao.audioandvideo.R;


public class WebViewActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "url";
    private CommonWebViewClient commonWebViewClient;
    private WebView wvWebView;


    public static void ViewUrl(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        loadUrl();
    }

    private void initView() {
        wvWebView = (WebView) findViewById(R.id.wvWebView);
        WebSettings webSettings = wvWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
//        if (NetStatusUtil.isConnected(getApplicationContext())) {
//            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
//        } else {
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//没网，则从本地获取，即离线加载
//        }
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        commonWebViewClient = new CommonWebViewClient();
        wvWebView.setWebViewClient(commonWebViewClient);
        wvWebView.setWebChromeClient(new CommonWebChromeClient());
    }

    private void loadUrl(){
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(EXTRA_URL);
            wvWebView.loadUrl(url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvWebView.canGoBack()) {
            wvWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class CommonWebViewClient extends WebViewClient {

        //复写shouldOverrideUrlLoading()方法，使得打开网页时不调用系统浏览器， 而是在本WebView中显示
        //url重定向会执行此方法以及点击页面某些链接也会执行此方法
        //return true:表示当前url已经加载完成，即使url还会重定向都不会再进行加载 false 表示此url默认由系统处理，该重定向还是重定向，直到加载完成
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (view == null || StringUtils.isEmpty(url)) {
//                return false;
//            }
            //view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("sssssss",url);
            wvWebView.loadUrl("javascript:window.local_obj.showSource(''+document.getElementsByTagName('html')[0].innerHTML);");
            super.onPageFinished(view,url);
        }

        //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
    }

    private class CommonWebChromeClient extends WebChromeClient{
        public CommonWebChromeClient() {
            super();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > 25) {

            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            getSupportActionBar().setTitle(title);
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            getSupportActionBar().setIcon(new BitmapDrawable(icon));
            super.onReceivedIcon(view, icon);
        }
    }

    final class InJavaScriptLocalObj {
        // @JavascriptInterface 是必须加的，否则日志会提示showSource不是函数
        @JavascriptInterface
        public void showSource(String html) {
            //Log.e("HTML", html);
            //refreshHtmlContent(html);
        }
    }

    //https://www.jianshu.com/p/a620a2664f58
//    private void refreshHtmlContent(final String html){
//        Log.i("网页内容",html);
//        wvWebView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //解析html字符串为对象
//                Document document = Jsoup.parse(html);
//                Elements elementsTag = document.getElementsByTag("head");
//                Log.e("ssssssssssssssssssssss",elementsTag.get(0).data());
//
//                //通过类名获取到一组Elements，获取一组中第一个element并设置其html
//                Elements elements = document.getElementsByClass("loadDesc");
//                elements.get(0).html("<p>加载完成</p>");
//
//                //通过ID获取到element并设置其src属性
//                Element element = document.getElementById("imageView");
//                element.attr("src","file:///android_asset/dragon.jpg");
//
//                //通过类名获取到一组Elements，获取一组中第一个element并设置其文本
//                elements = document.select("p.hint");
//                elements.get(0).text("您好，我是龙猫军团！");
//
//                //获取进行处理之后的字符串并重新加载
//                String body = document.toString();
//                wvWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
//            }
//        },5000);
//    }

    private void injectHideJs(WebView view) {
        String url = view.getUrl();

        String fun="javascript:function getClass(parent,sClass) { "+
                "var aEle=parent.getElementsByTagName('div'); " +
                "var aResult=[]; " +
                "var i=0; " +
                "for(i<0;i<aEle.length;i++) { " +
                "if(aEle[i].className==sClass) { " +
                "aResult.push(aEle[i]); " +
                "} " +
                "}; " +
                "return aResult; " +
                "} ";

        String fun1="javascript:function getClass(parent,sClass) { "+
                "var aEle=parent.getElementsByTagName('div'); " +
                "var aResult=[]; " +
                "var i=0; " +
                "for(i<0;i<aEle.length;i++) { " +
                "if(aEle[i].className==sClass) { " +
                "aResult.push(aEle[i]); " +
                "} " +
                "}; " +
                "return aResult; " +
                "} ";
        view.loadUrl(fun);

        String fun2="javascript:function hideOther() {" +
                "getClass(document,'nav-sides')[0].style.display='none'; " +
                "getClass(document,'side-bar')[0].style.display='none'; " +
                "getClass(document,'area-main')[0].style.display='none'; " +
                "getClass(document,'home-foot')[0].style.display='none'; " +
                "getClass(document,'enter')[0].style.display='none'; " +
                "getClass(document,'crumb')[0].style.display='none';" +
                "getClass(document,'date-tab clearfix')[0].style.display='none'; " +
                "document.getElementById('id_sidebar').style.display='none'; " +
                "document.getElementById('top_nav').style.display='none'; " +
                "document.getElementById('fix-personal').style.display='none'; " +
                "document.getElementById('waterlogo').style.display='none';" +
                "getClass(document,'wrap')[0].style.minWidth=0;" +
                "getClass(document,'game')[0].style.paddingTop=0;}";

        view.loadUrl(fun2);

        //view.loadUrl("javascript:hideOther();

    }
}
