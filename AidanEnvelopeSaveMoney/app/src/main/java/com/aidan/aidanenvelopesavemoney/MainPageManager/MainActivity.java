package com.aidan.aidanenvelopesavemoney.MainPageManager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aidan.aidanenvelopesavemoney.AccountList.AccountListFragment;
import com.aidan.aidanenvelopesavemoney.DataBase.AccountDAO;
import com.aidan.aidanenvelopesavemoney.DataBase.EnvelopeDAO;
import com.aidan.aidanenvelopesavemoney.DataBase.LoadDataSingleton;
import com.aidan.aidanenvelopesavemoney.EnvelopeList.EnvelopeListFragment;
import com.aidan.aidanenvelopesavemoney.Information.InformationFragment;
import com.aidan.aidanenvelopesavemoney.Model.Constants;
import com.aidan.aidanenvelopesavemoney.Model.Envelope;
import com.aidan.aidanenvelopesavemoney.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TabBar.TabBarListener {
    private RelativeLayout fragmentContainerRelativeLayout;
    private TabBar tabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EnvelopeDAO.init(getApplicationContext());
        AccountDAO.init(getApplicationContext());
        LoadDataSingleton.getInstance().loadFromDB();
        setTabBar();
    }

    @Override
    public void onDestroy() {
        LoadDataSingleton.getInstance().saveToDB();
        try {
            EnvelopeDAO.getInstance().close();
            AccountDAO.getInstance().close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        findView();
        loadEnvelopeListFragment();
    }

    public void setTabBar() {
        tabBar = (TabBar) findViewById(R.id.tabBar);
        tabBar.addTabs(
                new String[]{
                        getResources().getString(R.string.envelop),
                        getResources().getString(R.string.account),
                        getResources().getString(R.string.information)},
                new int[]{
                        R.mipmap.envelop3,
                        R.mipmap.envelop,
                        R.mipmap.envelop2});
        tabBar.setTabBarListener(this);
    }

    public void findView() {
        fragmentContainerRelativeLayout = (RelativeLayout) findViewById(R.id.fragmentContainerRelativeLayout);

    }

    public void loadEnvelopeListFragment() {
        Fragment fragment = new EnvelopeListFragment();
        loadFragment(fragment);
    }

    public void loadAccountListFragment() {
        Fragment fragment = AccountListFragment.newInstance(LoadDataSingleton.getInstance().getAccountList());
        loadFragment(fragment);
    }

    public void loadInformationFragment() {
        Fragment fragment = InformationFragment.newInstance();
        loadFragment(fragment);
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        int backStackCount = getFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
            fragmentManager.popBackStack();
        }
        transaction.replace(R.id.fragmentContainerRelativeLayout, fragment, fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void didClickOnTab(int clickedTabIndex) {
        switch (clickedTabIndex) {
            case 0:
                loadEnvelopeListFragment();
                break;
            case 1:
                loadAccountListFragment();
                break;
            case 2:
                loadInformationFragment();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bill) {
            showBillingDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void showBillingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_bill_month, null, false);
        TextView okTextView = (TextView)dialogView.findViewById(R.id.okTextView);
        TextView cancelTextView = (TextView)dialogView.findViewById(R.id.cancelTextView);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billMonth();
                dialog.dismiss();
            }
        });
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void billMonth(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Envelope> envelopes = new ArrayList<>();
                envelopes.addAll(LoadDataSingleton.getInstance().getEnvelopeList());
                for(Envelope envelope :envelopes){
                    envelope.tobeNewEnvelope();
                }
                sendBroadcast(new Intent(Constants.envelopeRefresh));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),R.string.complete,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

    }
}
