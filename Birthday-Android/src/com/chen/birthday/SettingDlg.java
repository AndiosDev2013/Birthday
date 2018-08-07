package com.chen.birthday;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.chen.birthday.util.DateUtil;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * The Class ConfirmExitDlg.
 */
@SuppressLint("NewApi")
public class SettingDlg extends Activity implements OnClickListener {
	// for UI
	private TextView txt_today;
	private EditText edt_fontsize;
	private EditText edt_refreshtime;
	private Button btn_ok;
	private Button btn_add_pupil;
	private Button btn_export;
	private Button btn_import;
	private Button btn_close;
	private Button btn_cancel;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set layout
		if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
			setContentView(R.layout.dlg_setting);
		} else {
			setContentView(R.layout.dlg_setting_rtl);
		}

		// edit text
		txt_today = (TextView) findViewById(R.id.txt_today);
		edt_fontsize = (EditText)findViewById(R.id.edt_fontsize);
		edt_refreshtime = (EditText)findViewById(R.id.edt_refreshtime);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		btn_add_pupil = (Button)findViewById(R.id.btn_add_pupil);
		btn_export = (Button)findViewById(R.id.btn_export);
		btn_import = (Button)findViewById(R.id.btn_import);
		btn_close = (Button)findViewById(R.id.btn_close);
		btn_cancel = (Button)findViewById(R.id.btn_cancel);

		txt_today.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		btn_add_pupil.setOnClickListener(this);
		btn_export.setOnClickListener(this);
		btn_import.setOnClickListener(this);
		btn_close.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);

		// get initial data
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			String strToday = bundle.getString(Constants.EXTRA_TODAY);
			int font_size = bundle.getInt(Constants.EXTRA_FONT_SIZE);
			int refresh_time = bundle.getInt(Constants.EXTRA_REFRESH_TIME);
			txt_today.setText(strToday);
			edt_fontsize.setText(""+font_size);
			edt_refreshtime.setText(""+refresh_time);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		// cancel
		doCancel();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.txt_today:
			showDatePickerDialog();
			break;
		case R.id.btn_ok:
			doOk();
			break;
		case R.id.btn_add_pupil:
			addNewPupil();
			break;
		case R.id.btn_export:
			doExport();
			break;
		case R.id.btn_import:
			doImport();
			break;
		case R.id.btn_close:
			doClose();
			break;
		case R.id.btn_cancel:
			doCancel();
			break;

		default:
			break;
		}
	}

	public void doOk() {
		if (!isValidate())
			return;

		Bundle bundle = new Bundle();
		String str_fontsize = edt_fontsize.getText().toString().trim();
		String str_refreshtime = edt_refreshtime.getText().toString().trim();
		try {
			String strToday = txt_today.getText().toString();
			int font_size = Integer.parseInt(str_fontsize);
			int refresh_time = Integer.parseInt(str_refreshtime);
			bundle.putString(Constants.EXTRA_TODAY, strToday);
			bundle.putInt(Constants.EXTRA_FONT_SIZE, font_size);
			bundle.putInt(Constants.EXTRA_REFRESH_TIME, refresh_time);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		Intent intent = SettingDlg.this.getIntent();
		intent.putExtras(bundle);

		setResult(RESULT_OK, intent);

		doCancel();
	}

	public void addNewPupil() {
		Intent intent = new Intent(SettingDlg.this, AddPupilInfoDlg.class);
		startActivity(intent);

		doCancel();
	}

	public void doExport() {
		try {
			MainActivity.instance.doExportToCSV();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doCancel();
	}

	public void doImport() {
		try {
			MainActivity.instance.doImportFromCSV();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doCancel();
	}
	
	public void doClose() {
		new AlertDialog.Builder(this)
		.setTitle("Do you really want to EXIT?")
		.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SettingDlg.this.finish();
				MainActivity.instance.finish();
			}
		})
		.setNegativeButton(R.string.btn_cancel, null)
		.show();
	}

	public void doCancel() {
		// finish activity
		SettingDlg.this.finish();

		// set animation
		overridePendingTransition(R.anim.in_down, R.anim.out_down);
	}

	public boolean isValidate() {
		String str = edt_fontsize.getText().toString().trim();
		if (str.length() <= 0) {
			Toast.makeText(this, R.string.error_fill_data, Toast.LENGTH_SHORT).show();
			edt_fontsize.requestFocus();
			return false;
		}
		str = edt_refreshtime.getText().toString().trim();
		if (str.length() <= 0) {
			Toast.makeText(this, R.string.error_fill_data, Toast.LENGTH_SHORT).show();
			edt_refreshtime.requestFocus();
			return false;
		}
		return true;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			txt_today.setText(new StringBuilder()
			.append(dayOfMonth).append("/")
			.append(monthOfYear + 1).append("/") // Month is 0 based so add 1
			.append(year).append(""));
		}
	};

	public void showDatePickerDialog() {
		Date today = DateUtil.stringToDate(txt_today.getText().toString().trim(), "dd/MM/yyyy");
		if (today == null)
			return;

		final Calendar c = Calendar.getInstance();
		c.setTime(today);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		new DatePickerDialog(this, mDateSetListener, year, month, day).show();
	}
}
