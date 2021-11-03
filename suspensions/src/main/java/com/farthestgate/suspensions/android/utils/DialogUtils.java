package com.farthestgate.suspensions.android.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.farthestgate.suspensions.android.R;

/**
 * Created by Suraj on 15/09/2016.
 */
public class DialogUtils {

    public static String TAG = "DialogUtils";


    public static void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static ProgressDialog getProgressDialog(Context mContext, String message) {
        if (mContext != null) {
            ProgressDialog mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(false);
            return mProgressDialog;
        }

        return null;
    }


    public static void showNoNetworkToast(Context context) {
        //Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        String msg = context.getResources().getString(R.string.no_network_msg);
        showToast(context, msg);
    }

    public static Snackbar showRetrySnackBar(@NonNull View parentView, String msg, final View.OnClickListener retryListener) {
        //Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        try {
            final Snackbar snackBar = Snackbar.make(parentView, msg, Snackbar.LENGTH_LONG);//Snackbar.LENGTH_INDEFINITE
            snackBar.setActionTextColor(Color.WHITE);
            View view = snackBar.getView();
            TextView tv = (TextView)
                    view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackBar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackBar.dismiss();
                    retryListener.onClick(v);
                }
            });
            snackBar.show();
            return snackBar;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Snackbar showNoNetworkSnackBar(@NonNull View parentView, final View.OnClickListener listener) {
        String msg = parentView.getContext().getResources().getString(R.string.no_network_msg);
        return showRetrySnackBar(parentView, msg, listener);
    }

    public static Snackbar showNoNetworkSnackBar(@NonNull View parentView) {
        return showSnackBar(parentView, R.string.no_network_msg);
    }

    public static Snackbar showSnackBar(View parentLayout, String msg) {
        //Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        final Snackbar snackBar = Snackbar.make(parentLayout, msg, Snackbar.LENGTH_LONG);
        snackBar.setActionTextColor(Color.WHITE);
        View view = snackBar.getView();
        TextView tv = (TextView)
                view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackBar.setAction(R.string.dismiss, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
        return snackBar;
    }

    public static Snackbar showSnackBar(View parentLayout, String msg, final View.OnClickListener listener) {
        final Snackbar snackBar = Snackbar.make(parentLayout, msg, Snackbar.LENGTH_LONG);
        snackBar.setDuration(Snackbar.LENGTH_INDEFINITE);
        snackBar.setActionTextColor(Color.WHITE);
        View view = snackBar.getView();
        view.setPadding(0,0,0,0);
        view.setBackgroundColor(parentLayout.getResources().getColor(R.color.red));
        TextView tv = (TextView)
                view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        tv.setMaxLines(3);
        snackBar.setAction(R.string.dismiss, listener);
        snackBar.show();
        return snackBar;
    }

    public static Snackbar showSnackBar(View parentView, int msg) {
        //Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        Resources res = parentView.getContext().getResources();
        return showSnackBar(parentView, res.getString(msg));
    }

    public static void showExitDialog(final Context context, String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                System.exit(0);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialog(final Context context, String title, String message, String positiveButtonText,
                                  DialogInterface.OnClickListener onClickListenerPositive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveButtonText, onClickListenerPositive);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showDialog(Context context, String title, String message,
                                  String positiveButtonText,
                                  String negativeButtonText,
                                  DialogInterface.OnClickListener onClickListenerPositive,
                                  DialogInterface.OnClickListener onClickListenerNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveButtonText, onClickListenerPositive);
        builder.setNegativeButton(negativeButtonText, onClickListenerNegative);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}