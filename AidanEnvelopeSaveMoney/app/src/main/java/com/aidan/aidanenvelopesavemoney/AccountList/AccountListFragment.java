package com.aidan.aidanenvelopesavemoney.AccountList;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.aidan.aidanenvelopesavemoney.Model.Account;
import com.aidan.aidanenvelopesavemoney.Model.Envelope;
import com.aidan.aidanenvelopesavemoney.R;

import java.util.List;

/**
 * Created by Aidan on 2016/10/4.
 */

public class AccountListFragment extends DialogFragment implements AccountListContract.view {
    ViewGroup rootView;
    AccountListContract.presenter presenter;
    ListView accountListView;
    String title = "";
    public static AccountListFragment newInstance(List<Account> accountList){
        AccountListFragment fragment = new AccountListFragment();
        fragment.presenter = new AccountListPresenter(fragment,accountList);
        return fragment;
    }
    public void setTitle(String title){
        this.title = title;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_account_list, container, false);
        if(presenter == null)dismiss();
        else presenter.start();

        return rootView;
    }


    @Override
    public void findView() {
        accountListView = (ListView)rootView.findViewById(R.id.accountListView);
        if(title.length() > 0)getDialog().setTitle(title);
    }

    @Override
    public void setAccountListView() {
        AccountListAdapter adapter = new AccountListAdapter(getActivity());
        presenter.setAdapter(adapter);
        presenter.adapterLoadData();
        accountListView.setAdapter(adapter);
    }


}
