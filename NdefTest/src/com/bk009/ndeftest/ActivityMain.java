package com.bk009.ndeftest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityMain extends Activity {

	private NfcAdapter nfcAdapter = null;
	private TextView nfcTView;

	private String readResult = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Button btn = (Button) findViewById(R.id.write);
		final EditText text = (EditText) findViewById(R.id.content);
		nfcTView = (TextView) findViewById(R.id.promt);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent()
						.getAction())) {
					String content = text.getText().toString();
					if (content == "") {
						nfcTView.setText("��û����������!");
					} else {
						if (writeData(content)) {
							nfcTView.setText("д��ɹ���");
						} else {
							nfcTView.setText("д��ʧ�ܣ�");
						}
					}
				} else {
					nfcTView.setText("û�м��NDEF����");
				}
			}
		});
		setContentView(R.layout.activity_main);
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			nfcTView.setText("�豸��֧��NFC��");
			finish();
			return;
		}
		if (nfcAdapter != null && !nfcAdapter.isEnabled()) {
			nfcTView.setText("����ϵͳ������������NFC���ܣ�");
			finish();
			return;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// �����NDEF���������INTENT����ö�NDEF����
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			readFromTag(getIntent());
		}
	}

	// ������
	private boolean readFromTag(Intent intent) {
		Parcelable[] rawArray = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
		NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
		try {
			if (mNdefRecord != null) {
				readResult = new String(mNdefRecord.getPayload(), "UTF-8");
				nfcTView.setText(readResult);
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;
	}

	// ��UTF-8��ʽд������data
	private boolean writeData(String data) {
		Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Ndef ndef = Ndef.get(tag);
		try {
			ndef.connect();
			NdefRecord ndefRecord = createTextRecord(data, Locale.US, true);
			NdefRecord[] records = { ndefRecord };
			NdefMessage ndefMessage = new NdefMessage(records);
			ndef.writeNdefMessage(ndefMessage);
			return true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} catch (FormatException e) {
			e.printStackTrace();
			return false;
		}
	}

	// createTextRecordԴ��
	public NdefRecord createTextRecord(String payload, Locale locale,
			boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("US-ASCII"));
		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
				.forName("UTF-16");
		byte[] textBytes = payload.getBytes(utfEncoding);
		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);
		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);
		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], data);
		return record;
	}
}
