package yh.org.yh29pulltoresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private PullToRefreshListView pullListView;
    private List<String> datas;
    private ArrayAdapter<String> adapter;
    private String TAG="yh";

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pullListView = (PullToRefreshListView) findViewById(R.id.listview);
        pullListView.setMode(PullToRefreshBase.Mode.BOTH);//设置刷新模式   上拉加载和下拉刷新都有
        //给 listview 特效监听下拉刷新效果
        pullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //开始刷新
                pullListView.setRefreshing(true);//开始刷新
                //下拉刷新的回调
                //起线程加载回调 实现耗时的刷新操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        datas.add(0,"最新数据"+System.currentTimeMillis());
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // 方法一 handler pust 刷新数据
                        // 方法二 runonuiThread
                        //更新完数据需要在主线程通知  不能在主线程中操作 调用这个方法会自动回到主线程进行操作
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();//刷新数据
                               // pullListView.setRefreshing(false);//刷新结束
                                pullListView.onRefreshComplete();//刷新成功
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载的回调
                pullListView.setRefreshing(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        datas.add("Old数据"+System.currentTimeMillis());//listView 加载更过数据
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                //pullListView.setRefreshing(false);
                                pullListView.onRefreshComplete();//刷新成功
                            }
                        });
                    }
                }).start();
            }
        });

        initdatas();
        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,datas);

   //new imageView 用上下文
        ImageView imageview = new ImageView(getApplicationContext());
        //老版本的添加视图是在加载数据之前
        imageview.setImageResource(R.mipmap.thor);
       // pullListView.addView(imageview,0);
        pullListView.getRefreshableView().addHeaderView(imageview);
        pullListView.setAdapter(adapter);
        //条目点击事件
        pullListView.setOnItemClickListener(this);

    }

    private void initdatas() {
        datas = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            datas.add("pull to ferresh"+i);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick: "+position);
        if(position>=2){
            Toast.makeText(this, ""+datas.get(position-2), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "您点击了头部试图", Toast.LENGTH_SHORT).show();
        }
    }
}
