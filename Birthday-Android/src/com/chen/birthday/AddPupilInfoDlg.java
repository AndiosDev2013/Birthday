package com.chen.birthday;

import java.util.Calendar;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/*
 * The Class AddPupilInfoDlg.
 */
@SuppressLint("NewApi")
public class AddPupilInfoDlg extends Activity implements OnClickListener {
	private EditText edt_name;
	private EditText edt_family;
	private EditText edt_address;
	private TextView txt_birthday;
	private TextView txt_next_birthday;

	private TextView clicked_textView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set layout
		if (Constants.APP_DIRECTION == Constants.DIRECTION.LEFT_TO_RIGHT) {
			setContentView(R.layout.dlg_add_pupil_info);
		} else {
			setContentView(R.layout.dlg_add_pupil_info_rtl);
		}

		// edit text
		edt_name = (EditText)findViewById(R.id.edt_name);
		edt_family = (EditText)findViewById(R.id.edt_family);
		edt_address = (EditText)findViewById(R.id.edt_address);
		txt_birthday = (TextView)findViewById(R.id.txt_birthday);
		txt_next_birthday = (TextView)findViewById(R.id.txt_next_birthday);
		txt_birthday.setOnClickListener(this);
		txt_next_birthday.setOnClickListener(this);

		// button
		Button btn_ok = (Button) findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		// cancel
		doCancel();
	}

	public void doOk() {
		if (!isValidate())
			return;

		Bundle bundle = new Bundle();
		bundle.putString(Constants.EXTRA_NAME, edt_name.getText().toString().trim());
		bundle.putString(Constants.EXTRA_FAMILIY, edt_family.getText().toString().trim());
		bundle.putString(Constants.EXTRA_ADDRESS, edt_address.getText().toString().trim());
		bundle.putString(Constants.EXTRA_BIRTHDAY, txt_birthday.getText().toString().trim());
		bundle.putString(Constants.EXTRA_NEXT_BIRTHDAY, txt_next_birthday.getText().toString().trim());

		MainActivity.instance.AddNewPupilInfo(bundle);

		doCancel();
	}

	public boolean isValidate() {
		String str = edt_name.getText().toString().trim();
		if (str.length() <= 0) {
			Toast.makeText(this, R.string.error_fill_data, Toast.LENGTH_SHORT).show();
			edt_name.requestFocus();
			return false;
		}
		str = edt_family.getText().toString().trim();
		if (str.length() <= 0) {
			Toast.makeText(this, R.string.error_fill_data, Toast.LENGTH_SHORT).show();
			edt_family.requestFocus();
			return false;
		}
		str = edt_address.getText().toString().trim();
		if (str.length() <= 0) {
			Toast.makeText(this, R.string.error_fill_data, Toast.LENGTH_SHORT).show();
			edt_address.requestFocus();
			return false;
		}
		str = txt_birthday.getText().toString().trim();
		if (str.length() <= 0) {
			Toast.makeText(this, R.string.error_fill_data, Toast.LENGTH_SHORT).show();
			txt_birthday.requestFocus();
			return false;
		}
		str = txt_next_birthday.getText().toString().trim();
		if (str.length() <= 0) {
			Toast.makeText(this, R.string.error_fill_data, Toast.LENGTH_SHORT).show();
			txt_next_birthday.requestFocus();
			return false;
		}
		if (str.equals("MM/DD/YYYY")) {
			Toast.makeText(this, R.string.error_fill_birthday, Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	public void doCancel() {
		// finish activity
		AddPupilInfoDlg.this.finish();

		// set animation
		overridePendingTransition(R.anim.in_down, R.anim.out_down);
	}


	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			if (clicked_textView == txt_birthday) {
				txt_birthday.setText(new StringBuilder()
				.append(dayOfMonth).append("/")
				.append(monthOfYear + 1).append("/") // Month is 0 based so add 1
				.append(year).append(""));
				txt_next_birthday.setText(new StringBuilder()
				.append(dayOfMonth).append("/")
				.append(monthOfYear + 1).append("/") // Month is 0 based so add 1
				.append(year+1).append(""));

			} else if (clicked_textView == txt_next_birthday) {
				txt_birthday.setText(new StringBuilder()
				.append(dayOfMonth).append("/")
				.append(monthOfYear + 1).append("/") // Month is 0 based so add 1
				.append(year-1).append(""));
				txt_next_birthday.setText(new StringBuilder()
				.append(dayOfMonth).append("/")
				.append(monthOfYear + 1).append("/") // Month is 0 based so add 1
				.append(year).append(""));
			}
		}
	};

	public void showDatePickerDialog() {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		new DatePickerDialog(this, mDateSetListener, year, month, day).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ok:
			doOk();
			break;
		case R.id.btn_cancel:
			doCancel();
			break;
		case R.id.txt_birthday:
			clicked_textView = txt_birthday;
			showDatePickerDialog();
			break;
		case R.id.txt_next_birthday:
			clicked_textView = txt_next_birthday;
			showDatePickerDialog();
			break;
		}
	}
}
