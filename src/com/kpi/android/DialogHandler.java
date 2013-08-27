package com.kpi.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Handle msg from loadinfFeeds Threads
 * @author Bosicc
 *
 */
public class DialogHandler {
	
	private static final String TAG = "DialogHandler";

	private Context context;
	
	private View view;
	
	private TextView textView;

	public DialogHandler(Context ctx, View v){
		this.context = ctx;
		this.view = v;
		this.textView = (TextView)v.findViewById(R.id.statusMessageLabelBottom);
	}
	
	/**
	 * Msg types for handlers
	 */
	public enum MsgType {
		UNKNOWN,						
		HIDE_WAIT_DIALOG,				
		SHOW_WAIT_DIALOG;
		
		public static MsgType findByOrdinal(int ordinal) {
			for (MsgType item : values()) {
				if (item.ordinal() == ordinal) {
					return item;
				}
			}
			return UNKNOWN;
		}
		
	}
	
	/**
	 * Show waiting dialog
	 */
	public void showWaitDialog(String text) {
		Message msg = new Message();
        msg.obj = text;
        msg.what = MsgType.SHOW_WAIT_DIALOG.ordinal();
		dialogHandler.sendMessage(msg);

	}
	
	/**
	 * Hide waiting dialog
	 */
	public void hideWaitDialog() {
		dialogHandler.sendEmptyMessage(MsgType.HIDE_WAIT_DIALOG.ordinal());
	}
	
	/**
	 * Handler for Login dialogs
	 */
	public  Handler dialogHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			MsgType type = MsgType.findByOrdinal(msg.what); 
			String title = "";
			String text = (String)msg.obj;
			switch(type){
				case UNKNOWN:
					break;
				case HIDE_WAIT_DIALOG:
					hideWaitingDialog();
					return; 
				case SHOW_WAIT_DIALOG:
					showWaitingDialog(text);
					return; 
				default:
					break;
			}
			
			if (!title.equals("")){
				showErrorDialog(title, text);
			}else{
				Log.e(TAG, "Unknown type="+msg.what);
			}
			
		};
		
		/**
		 * Make visible dialog
		 */
		public void showWaitingDialog(String text){
			view.setVisibility(View.VISIBLE);
			textView.setText(text);
			
		}
		/**
		 * Hide and dissmiss waiting dialog
		 */
		public void hideWaitingDialog(){
			view.setVisibility(View.GONE);
		}
		
		/**
		 * Show Dialog with Error 
		 * @param message
		 */
		private void showErrorDialog(String title, String message){
			new AlertDialog.Builder(context)
					.setTitle(title)
					.setMessage(message)
					.setIcon(R.drawable.symbol_error)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
						}
					})
					.create()
					.show();
		}

	};
	
}
