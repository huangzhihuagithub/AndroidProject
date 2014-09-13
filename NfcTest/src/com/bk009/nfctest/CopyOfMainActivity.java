package com.bk009.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class CopyOfMainActivity extends Activity {
	NfcAdapter nfcAdapter;
	TextView promt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		promt = (TextView) findViewById(R.id.promt);
		// ��ȡĬ�ϵ�NFC������
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			promt.setText("�豸��֧��NFC��");
			finish();
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			promt.setText("����ϵͳ������������NFC���ܣ�");
			// startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));

			finish();
			return;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// �õ��Ƿ��⵽ACTION_TECH_DISCOVERED����
		/*
		 * if
		 * (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
		 * // �����intent processIntent(getIntent()); }
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				processIntent(getIntent());
			}
		}).start();
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

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	private void processIntent(Intent intent) {
		// ȡ����װ��intent�е�TAG
		boolean auth = false;
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tagFromIntent != null) {
			for (String tech : tagFromIntent.getTechList()) {
				System.out.println(tech);
				auth = true;
			}
		}

		// ��ȡTAG
		/*
		 * MifareClassic mfc = MifareClassic.get(tagFromIntent); try { String
		 * metaInfo = ""; //Enable I/O operations to the tag from this
		 * TagTechnology object. mfc.connect(); int type =
		 * mfc.getType();//��ȡTAG������ int sectorCount =
		 * mfc.getSectorCount();//��ȡTAG�а����������� String typeS = ""; switch (type)
		 * { case MifareClassic.TYPE_CLASSIC: typeS = "TYPE_CLASSIC"; break;
		 * case MifareClassic.TYPE_PLUS: typeS = "TYPE_PLUS"; break; case
		 * MifareClassic.TYPE_PRO: typeS = "TYPE_PRO"; break; case
		 * MifareClassic.TYPE_UNKNOWN: typeS = "TYPE_UNKNOWN"; break; } metaInfo
		 * += "��Ƭ���ͣ�" + typeS + "\n��" + sectorCount + "������\n��" +
		 * mfc.getBlockCount() + "����\n�洢�ռ�: " + mfc.getSize() + "B\n"; for (int
		 * j = 0; j < sectorCount; j++) { //Authenticate a sector with key A.
		 * auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
		 * int bCount; int bIndex; if (auth) { metaInfo += "Sector " + j +
		 * ":��֤�ɹ�\n"; // ��ȡ�����еĿ� bCount = mfc.getBlockCountInSector(j); bIndex =
		 * mfc.sectorToBlock(j); for (int i = 0; i < bCount; i++) { byte[] data
		 * = mfc.readBlock(bIndex); metaInfo += "Block " + bIndex + " : " +
		 * bytesToHexString(data) + "\n"; bIndex++; } } else { metaInfo +=
		 * "Sector " + j + ":��֤ʧ��\n"; } } promt.setText(metaInfo);
		 */
		try {
			if (auth) {
				NfcA nfcaTag = NfcA.get((Tag) intent
						.getParcelableExtra(NfcAdapter.EXTRA_TAG));//
				if (nfcaTag != null) {
					nfcaTag.connect();// ���ӿ�
					String atqa = "";
					String str = "";
					for (byte tmpByte : nfcaTag.getAtqa()) {
						atqa += tmpByte;
					}
					str += "tag Atqa:" + bytesToHexString(nfcaTag.getAtqa())
							+ "\n";// ��ȡ����atqa
					str += "tag SAK:" + nfcaTag.getSak() + "\n";// ��ȡ����sak
					str += "max len:" + nfcaTag.getMaxTransceiveLength() + "\n";// ��ȡ��Ƭ�ܽ��յ����ָ���
					byte[] cmd = null;
					// cmd=new
					// byte[]{0x41,0x54,0x4D,0x0A,0x52,0x01,0x00,0x01,(byte)
					// 0xff,(byte)0xff,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x0D};
					cmd = new byte[] { 0x0A, 0x00, 0x01 };// ������
					str += "Card Number:" + nfcaTag.transceive(cmd);// ���������Ƭ(�Ҳ������ǳ��Ƶ�������ȡָ��,��֪����ͬѧ����һ�����ǵĿ���ISO/IEC
																	// 14443
																	// typeA��׼��)
					promt.setText(str);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}