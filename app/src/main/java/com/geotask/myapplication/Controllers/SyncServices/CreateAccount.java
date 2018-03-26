package com.geotask.myapplication.Controllers.SyncServices;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import static android.content.Context.ACCOUNT_SERVICE;

public class CreateAccount {

    public static Account CreateAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        Account[] accountList = accountManager.getAccountsByType("com.geotask");
        for(Account account : accountList) {
            accountManager.removeAccountExplicitly(account);
        }

        Account newAccount = new Account("com.geotask", "com.geotask");
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            Log.i("AccountCreation", "success");
        } else {
            Log.i("AccountCreation", "failed");
        }
        return newAccount;
    }
}