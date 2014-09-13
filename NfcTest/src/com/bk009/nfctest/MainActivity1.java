package com.bk009.nfctest;

import java.io.IOException;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity1 extends Activity {
	private NfcAdapter mNfcAdapter;
	private TextView cardNoTxt;
	private Boolean firstRun = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		cardNoTxt = (TextView) this.findViewById(R.id.promt);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
					.show();
			// finish();
			return;
		}
		// mNfcAdapter.setNdefPushMessageCallback(this, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNewIntent(Intent intent) {// ��Ӧintent
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		System.out.println("tagFromIntent..." + tagFromIntent.toString());
		processTag(intent);
		// do something with tagFromIntent
	}

	@Override
	public void onResume() {// ��Ӧintent
		super.onResume();
		Intent intent = getIntent();
		// new Intent(this,
		// getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
		if (firstRun) {// ����һ������ʱ,��Ӧ��ע���nfc tagdiscover intent�Ĵ������
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					intent, 0);
			IntentFilter ndef = new IntentFilter(
					NfcAdapter.ACTION_NDEF_DISCOVERED);
			IntentFilter techIntent = new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED);
			IntentFilter tagIntent = new IntentFilter(
					NfcAdapter.ACTION_TAG_DISCOVERED);
			String[][] techListsArray = new String[][] { new String[] {
					NfcF.class.getName(),
					NfcA.class.getName()// ����ɴ����tech����
					, MifareClassic.class.getName(),
					MifareUltralight.class.getName() } };
			try {
				ndef.addDataType("*/*"); /*
										 * Handles all MIME based dispatches.
										 * You should specify only the ones that
										 * you need.
										 */
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("fail", e);
			}
			IntentFilter[] intentFiltersArray = new IntentFilter[] { ndef,
					techIntent, tagIntent };
			mNfcAdapter.enableForegroundDispatch(this, pendingIntent,
					intentFiltersArray, techListsArray);
			firstRun = false;
		}
		Log.i("NFCOnResume", intent.getAction());
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			processTag(intent);
		} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			processTag(intent);
		} else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			processNdefTag(intent);
		}
	}

	public void processNdefTag(Intent intent) {
		Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		NdefMessage[] messages = null;
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null) {
			messages = new NdefMessage[rawMsgs.length];
			for (int i = 0; i < rawMsgs.length; i++) {
				messages[i] = (NdefMessage) rawMsgs[i];
			}
		} else {
			byte[] empty = new byte[]{};
			NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,empty,empty,empty);
			NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
			messages = new NdefMessage[]{msg};
		}
	}

	public void processTag(Intent intent) {// ����tag
		String str = "";
		Boolean isNdef = false;
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		str += "Tech List:" + tagFromIntent.getTechList()[0] + "\n";// ��ӡ���ļ����б�
		byte[] aa = tagFromIntent.getId();
		str += "Card UID:" + bytesToHexString(aa) + "\n";// ��ȡ����UID
		for (String tech : tagFromIntent.getTechList()) {
			if (tech.equals("android.nfc.tech.NfcA")) {
				isNdef = true;
			}
		}
		if (isNdef) {
			NfcA nfcaTag = NfcA.get((Tag) intent
					.getParcelableExtra(NfcAdapter.EXTRA_TAG));// ���ǵĳ�����NfcA����,��������һ��NfcaTag

			try {
				// ndefTag.connect();
				nfcaTag.connect();// ���ӿ�
				String atqa = "";
				for (byte tmpByte : nfcaTag.getAtqa()) {
					atqa += tmpByte;
				}
				str += "tag Atqa:" + bytesToHexString(nfcaTag.getAtqa()) + "\n";// ��ȡ����atqa
				str += "tag SAK:" + nfcaTag.getSak() + "\n";// ��ȡ����sak
				str += "max len:" + nfcaTag.getMaxTransceiveLength() + "\n";// ��ȡ��Ƭ�ܽ��յ����ָ���
				byte[] cmd = null;
				// cmd=new byte[]{0x41,0x54,0x4D,0x0A,0x52,0x01,0x00,0x01,(byte)
				// 0xff,(byte)0xff,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x0D};
				cmd = new byte[] { 0x0A, 0x00, 0x01 };// ������
				str += "Card Number:" + nfcaTag.transceive(cmd);// ���������Ƭ(�Ҳ������ǳ��Ƶ�������ȡָ��,��֪����ͬѧ����һ�����ǵĿ���ISO/IEC
																// 14443
																// typeA��׼��)
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					nfcaTag.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		cardNoTxt.setText(str);
	}

	// �ַ�����ת��Ϊ16�����ַ���
	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("0x");
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			System.out.println(buffer);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}
}