package com.liferay.mobile.screens.viewsets.defaultviews.imagegallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.liferay.mobile.screens.R;
import com.liferay.mobile.screens.imagegallery.BaseDetailUploadView;

/**
 * @author Víctor Galán Grande
 */
public class DefaultUploadDialog {

	public AlertDialog createDialog(final BaseDetailUploadView view, Context context) {

		final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.default_theme_dialog);
		alert.setView(view);
		alert.setCancelable(false);

		alert.setNegativeButton(R.string.upload_detail_cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		alert.setPositiveButton(R.string.upload_detail_upload_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String title = view.getTitle();
				String description = view.getDescription();

				view.finishActivityAndStartUpload(title, description, "");
			}
		});

		AlertDialog alertDialog = alert.create();
		alertDialog.show();
		return alertDialog;
	}
}
