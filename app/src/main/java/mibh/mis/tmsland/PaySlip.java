package mibh.mis.tmsland;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by ponlakiss on 08/10/2015.
 */
public class PaySlip extends Fragment {

    View rootView;
    WebView webView;
    ProgressBar progressBar;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.pay_slip, container, false);
        webView = (WebView) rootView.findViewById(R.id.webView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar);

        sp = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);

        webView.getSettings().setJavaScriptEnabled(true);

        progressBar.setVisibility(View.VISIBLE);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                view.bringToFront();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.setVisibility(View.GONE);
            }
        });
        webView.loadUrl("http://www.mibholding.com/TMS_Mobile/TMS_ViewPayRoll.aspx?EMP=" + sp.getString("empid", "") + "&TRUCKID=" + sp.getString("truckid", ""));
        //webView.loadUrl("http://10.29.20.32:5000/TMS_ViewPayRoll.aspx?EMP=" + sp.getString("empid", "") + "&TRUCKID=" + sp.getString("truckid", ""));
        //webView.loadData(summary, "text/html", "UTF-8");

        return rootView;
    }

}
