package me.xiaocao.news.ui.news;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.Bind;
import me.xiaocao.news.R;
import me.xiaocao.news.app.Constants;
import me.xiaocao.news.db.CollectionHelper;
import me.xiaocao.news.model.NewsList;
import me.xiaocao.news.model.db.CollectionVo;
import me.xiaocao.news.ui.pic.PhotoActivity;
import me.xiaocao.news.util.IntentUtil;
import me.xiaocao.news.util.webview.JavaScriptFunction;
import me.xiaocao.news.util.webview.MWebView;
import me.xiaocao.news.util.webview.NestedWebView;
import me.xiaocao.news.util.webview.WebUtils;
import x.lib.jsoup.RxJsoupNetWork;
import x.lib.jsoup.RxJsoupNetWorkListener;
import x.lib.ui.BaseActivity;
import x.lib.ui.TitleView;
import x.lib.utils.LogUtil;
import x.lib.utils.SPUtils;


/**
 * description: NewsDetailActivity
 * author: xiaocao
 * date: 17/7/5 下午6:00
 */
public class NewsDetailActivity extends BaseActivity {


    @Bind(R.id.webView)
    MWebView webView;
    private TitleView title;
    private NewsList news;

    @Override
    protected int setContentViewResId() {
        return R.layout.activity_news_sina_detail;
    }

    @Override
    protected void initTitle() {
//        BarUtils.setStatusBarAlpha(activity, 0);
        title = new TitleView(activity, findViewById(R.id.toolbar));
        title.setBack(activity);
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    protected void initInstance() {
        webView.setDrawingCacheEnabled(true);
        news = (NewsList) getIntent().getExtras().getSerializable(Constants.KEY_NEWS_DETAIL);
        if (null != news) {
            title.setTitleText(news.getTitle());
            WebSettings mWebSettings = webView.getSettings();
            mWebSettings.setJavaScriptEnabled(true);
            mWebSettings.setSupportZoom(true);
            mWebSettings.setBuiltInZoomControls(false);
            mWebSettings.setSavePassword(false);

            if (Build.VERSION.SDK_INT >= 21) {
                mWebSettings.setMixedContentMode(0);
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else if (Build.VERSION.SDK_INT >= 19) {
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else if (Build.VERSION.SDK_INT < 19) {
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            int index = SPUtils.getInstance().getInt(BaseActivity.isTextSize, 0);
            int size;
            if (index == 0) {
                size = 14;
            } else if (index == 1) {
                size = 16;
            } else {
                size = 18;
            }
            mWebSettings.setTextZoom(size);
            mWebSettings.setDatabaseEnabled(true);
            mWebSettings.setAppCacheEnabled(true);
            mWebSettings.setLoadsImagesAutomatically(true);
            mWebSettings.setSupportMultipleWindows(false);
            mWebSettings.setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
            mWebSettings.setAllowFileAccess(true); //允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭
            mWebSettings.setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            mWebSettings.setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
            mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            if (Build.VERSION.SDK_INT >= 19)
                mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            else
                mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            mWebSettings.setLoadWithOverviewMode(true);
            mWebSettings.setUseWideViewPort(true);
            mWebSettings.setDomStorageEnabled(true);
            mWebSettings.setNeedInitialFocus(true);
            mWebSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
            mWebSettings.setDefaultFontSize(22);
            mWebSettings.setMinimumFontSize(size);//设置 WebView 支持的最小字体大小，默认为 8
            mWebSettings.setGeolocationEnabled(true);
            //适配5.0不允许http和https混合使用情况
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            //缓存文件最大值
            mWebSettings.setAppCacheMaxSize(Long.MAX_VALUE);
            webView.addJavascriptInterface(new JavaScriptFunction() {
                @Override
                @JavascriptInterface
                public void getUrl(String imageUrl) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PHOTO_URL, imageUrl);
                    GoActivity(PhotoActivity.class, bundle);
                }
            }, "JavaScriptFunction");
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
//            String htmlData = WebUtils.buildHtmlForIt(news.getContent(), false);
//            webView.loadDataWithBaseURL(null, htmlData, WebUtils.MIME_TYPE, WebUtils.ENCODING, null);

            //http://www.jiemian.com/article/1860495.html
            //http://www.jiemian.com/article/1860495.html
            RxJsoupNetWork.getInstance().getApi("http://www.jiemian.com/article/1860495.html", new RxJsoupNetWorkListener<String>() {
                @Override
                public void onNetWorkStart() {

                }

                @Override
                public void onNetWorkError(Throwable e) {

                }

                @Override
                public void onNetWorkComplete() {

                }

                @Override
                public void onNetWorkSuccess(String s) {
                    String htmlData = WebUtils.buildHtmlForIt(s, false);
                    LogUtil.d(htmlData);
                    webView.loadDataWithBaseURL("http://www.jiemian.com", htmlData, WebUtils.MIME_TYPE, WebUtils.ENCODING, null);

                }

                @Override
                public String getT(Document document) {
                    LogUtil.d(document.select("div.article-content").toString());
                    return document.select("div.article-content").toString();
                }
            });

//            RxJsoupNetWork.getInstance().getApi(news.getWeburl(), new RxJsoupNetWorkListener<String>() {
//                @Override
//                public void onNetWorkStart() {
//
//                }
//
//                @Override
//                public void onNetWorkError(Throwable e) {
//
//                }
//
//                @Override
//                public void onNetWorkComplete() {
//
//                }
//
//                @Override
//                public void onNetWorkSuccess(String s) {
//                    String htmlData = WebUtils.buildHtmlForIt(s, false);
//                    webView.loadDataWithBaseURL(null, htmlData, WebUtils.MIME_TYPE, WebUtils.ENCODING, null);
//                }
//
//                @Override
//                public String getT(Document document) {//artical-player-wrap
//                    Elements els = new Elements();
//                    els.add(document.getElementById("artibody"));
//                    for (Element el : els) {
//                        el.select("artical-player-wrap").remove();
//                    }
//                    return els.toString();
//                }
//            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail, menu);
        CollectionVo dbVo = CollectionHelper.queryWebUrl(news.getWeburl());
        if (null != dbVo && dbVo.getUrl().equals(news.getWeburl())) {
            menu.getItem(0).setTitle("取消收藏");
        } else {
            menu.getItem(0).setTitle("添加收藏");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (null != news) {
            int id = item.getItemId();
            if (id == R.id.menuShare) {//分享
                IntentUtil.shareText(activity, "分享至", news.getWeburl());
            } else if (id == R.id.menuCollection) {//收藏
                CollectionVo dbVo = CollectionHelper.queryWebUrl(news.getWeburl());
                if (null != dbVo && dbVo.getUrl().equals(news.getWeburl())) {
                    CollectionHelper.deleteChannel(dbVo.getId());
                    showSnackbar(webView, "已取消收藏");
                } else {
                    CollectionVo vo = new CollectionVo();
                    vo.setUrl(news.getWeburl());
                    vo.setImgUrl(news.getPic());
                    vo.setTitle(news.getTitle());
                    vo.setSrc(news.getSrc());
                    vo.setId(CollectionHelper.queryAll().size() + 1);
                    vo.setType(CollectionVo.sina);
                    CollectionHelper.insert(vo);
                    showSnackbar(webView, "添加收藏成功");
                }
                supportInvalidateOptionsMenu();
            } else {
//                Bundle bundle=new Bundle();
//                bundle.putSerializable(Constants.KEY_NEWS_URL, news.getWeburl());
//                GoActivity(NewsWebActivity.class, bundle);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(news.getWeburl());
                intent.setData(content_url);
                startActivity(intent);
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
        }
    }
}
